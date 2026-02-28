package QLTN.UI;




import QLTN.Dao.ChatSessionDAO;
import QLTN.Dao.CustomerMessageDAO;
import QLTN.Dao.EmployeeMessageDAO;
import QLTN.Entity.ChatSession;
import QLTN.Entity.CustomerMessage;
import QLTN.Entity.EmployeeMessage;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

public class EmployeeChatForm extends JFrame {
    // Components
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton refreshButton;
    private JButton endChatButton;
    private JLabel statusLabel;
    private JLabel computerLabel;

    // Data
    private ChatSession chatSession;
    private int employeeId;
    private Timer refreshTimer;

    // DAOs
    private ChatSessionDAO chatSessionDAO;
    private CustomerMessageDAO customerMessageDAO;
    private EmployeeMessageDAO employeeMessageDAO;

    // Formatting
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private StyledDocument doc;
    private Style customerStyle;
    private Style employeeStyle;
    private Style systemStyle;

    public EmployeeChatForm(ChatSession chatSession, int employeeId) {
        this.chatSession = chatSession;
        this.employeeId = employeeId;

        // Initialize DAOs
        chatSessionDAO = new ChatSessionDAO();
        customerMessageDAO = new CustomerMessageDAO();
        employeeMessageDAO = new EmployeeMessageDAO();

        // Initialize UI
        initUI();

        // Load messages
        loadMessages();

        // Start refresh timer
        startRefreshTimer();
    }

    private void initUI() {
        setTitle("Chat với " + chatSession.getComputerName());
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();

        // Create styles
        customerStyle = chatArea.addStyle("CustomerStyle", null);
        StyleConstants.setForeground(customerStyle, new Color(0, 102, 204));
        StyleConstants.setBold(customerStyle, true);

        employeeStyle = chatArea.addStyle("EmployeeStyle", null);
        StyleConstants.setForeground(employeeStyle, new Color(204, 0, 0));
        StyleConstants.setBold(employeeStyle, true);

        systemStyle = chatArea.addStyle("SystemStyle", null);
        StyleConstants.setForeground(systemStyle, Color.GRAY);
        StyleConstants.setItalic(systemStyle, true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());

        sendButton = new JButton("Gửi");
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));

        // Computer info
        computerLabel = new JLabel("Máy: " + chatSession.getComputerName() + " (ID: " + chatSession.getComputerId() + ")");
        computerLabel.setFont(computerLabel.getFont().deriveFont(Font.BOLD));

        // Status and buttons
        JPanel statusButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        statusLabel = new JLabel("Đang kết nối...");
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> refreshChat());

        endChatButton = new JButton("Kết thúc");
        endChatButton.addActionListener(e -> endChat());

        statusButtonsPanel.add(statusLabel);
        statusButtonsPanel.add(refreshButton);
        statusButtonsPanel.add(endChatButton);

        statusPanel.add(computerLabel, BorderLayout.WEST);
        statusPanel.add(statusButtonsPanel, BorderLayout.EAST);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // Set content pane
        setContentPane(mainPanel);

        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopRefreshTimer();
            }
        });
    }

    private void loadMessages() {
        if (chatSession == null) return;

        try {
            // Clear chat area
            doc.remove(0, doc.getLength());

            // Get all customer messages
            List<CustomerMessage> customerMessages = customerMessageDAO.getMessagesBySessionId(chatSession.getSessionId());

            // Get all employee messages
            List<EmployeeMessage> employeeMessages = employeeMessageDAO.getMessagesBySessionId(chatSession.getSessionId());

            // Combine and sort messages by time
            List<Object> allMessages = new ArrayList<>();
            allMessages.addAll(customerMessages);
            allMessages.addAll(employeeMessages);

            allMessages.sort((o1, o2) -> {
                Date date1 = (o1 instanceof CustomerMessage) ?
                        ((CustomerMessage) o1).getSentAt() : ((EmployeeMessage) o1).getSentAt();
                Date date2 = (o2 instanceof CustomerMessage) ?
                        ((CustomerMessage) o2).getSentAt() : ((EmployeeMessage) o2).getSentAt();
                return date1.compareTo(date2);
            });

            // Display messages
            for (Object msg : allMessages) {
                if (msg instanceof CustomerMessage) {
                    CustomerMessage customerMsg = (CustomerMessage) msg;
                    appendCustomerMessage(customerMsg.getContent(), customerMsg.getSentAt());
                } else if (msg instanceof EmployeeMessage) {
                    EmployeeMessage employeeMsg = (EmployeeMessage) msg;
                    appendEmployeeMessage(employeeMsg.getContent(), employeeMsg.getSentAt());
                }
            }

            // Mark customer messages as read
            chatSessionDAO.markAsRead(chatSession.getSessionId(), true);

            // Update has_unread flag
            chatSession.setHasUnread(false);
            chatSessionDAO.updateChatSession(chatSession);

            // Scroll to bottom
            chatArea.setCaretPosition(doc.getLength());

            // Update status
            statusLabel.setText("Đang kết nối");

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();

        if (message.isEmpty() || chatSession == null) {
            return;
        }

        // Create and save employee message
        EmployeeMessage employeeMessage = new EmployeeMessage(
                chatSession.getSessionId(),
                employeeId,
                message);

        int messageId = employeeMessageDAO.addMessage(employeeMessage);

        if (messageId > 0) {
            try {
                // Display message in chat area
                appendEmployeeMessage(message, null);

                // Clear message field
                messageField.setText("");

                // Scroll to bottom
                chatArea.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể gửi tin nhắn. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshChat() {
        // Reload chat session
        chatSession = chatSessionDAO.getChatSessionById(chatSession.getSessionId());

        if (chatSession != null) {
            // Update status
            if ("closed".equals(chatSession.getStatus())) {
                statusLabel.setText("Phiên chat đã kết thúc");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                endChatButton.setEnabled(false);
                stopRefreshTimer();
            } else {
                statusLabel.setText("Đang kết nối");
            }

            // Load messages
            loadMessages();
        }
    }

    private void endChat() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn kết thúc phiên chat này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = chatSessionDAO.endChatSession(chatSession.getSessionId());

            if (success) {
                try {
                    // Add system message
                    doc.insertString(doc.getLength(), "Hệ thống: Phiên chat đã kết thúc.\n", systemStyle);

                    // Update UI
                    statusLabel.setText("Phiên chat đã kết thúc");
                    messageField.setEnabled(false);
                    sendButton.setEnabled(false);
                    endChatButton.setEnabled(false);

                    // Stop refresh timer
                    stopRefreshTimer();

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể kết thúc phiên chat. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void appendCustomerMessage(String message, Date sentAt) throws BadLocationException {
        String time = (sentAt != null) ? timeFormat.format(sentAt) : timeFormat.format(new java.util.Date());
        doc.insertString(doc.getLength(), "Khách hàng (" + time + "): ", customerStyle);
        doc.insertString(doc.getLength(), message + "\n", null);
    }

    private void appendEmployeeMessage(String message, Date sentAt) throws BadLocationException {
        String time = (sentAt != null) ? timeFormat.format(sentAt) : timeFormat.format(new java.util.Date());
        doc.insertString(doc.getLength(), "Bạn (" + time + "): ", employeeStyle);
        doc.insertString(doc.getLength(), message + "\n", null);
    }

    private void startRefreshTimer() {
        // Refresh chat every 5 seconds
        refreshTimer = new Timer(5000, e -> refreshChat());
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

//    public static void main(String[] args) {
//        // For testing
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Create a dummy chat session
//            ChatSession session = new ChatSession();
//            session.setSessionId(1);
//            session.setCustomerId(1);
//            session.setComputerId(1);
//            session.setComputerName("Máy 01");
//            session.setStatus("active");
//
//            new EmployeeChatForm(session, 1).setVisible(true);
//        });
//    }
}
