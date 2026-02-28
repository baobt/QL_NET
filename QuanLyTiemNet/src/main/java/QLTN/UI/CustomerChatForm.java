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

public class CustomerChatForm extends JFrame {

    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    private int customerId;
    private int computerId;
    private String computerName;
    private ChatSession chatSession;
    private Timer refreshTimer;
    private ChatSessionDAO chatSessionDAO;
    private CustomerMessageDAO customerMessageDAO;
    private EmployeeMessageDAO employeeMessageDAO;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private StyledDocument doc;
    private Style customerStyle;
    private Style employeeStyle;
    private Style systemStyle;

    public CustomerChatForm(int customerId, int computerId, String computerName) {
        this.customerId = customerId;
        this.computerId = computerId;
        this.computerName = computerName;

        // Initialize DAOs
        chatSessionDAO = new ChatSessionDAO();
        customerMessageDAO = new CustomerMessageDAO();
        employeeMessageDAO = new EmployeeMessageDAO();

        // Initialize UI
        initUI();

        // Get or create chat session
        initChatSession();

        // Start refresh timer
        startRefreshTimer();
    }

    private void initUI() {
        setTitle("Hỗ trợ khách hàng - " + computerName);
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

        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> refreshChat());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusLabel = new JLabel("Đang kết nối...");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(refreshButton, BorderLayout.EAST);

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

    private void initChatSession() {
        // Check if there's an active session for this computer
        chatSession = chatSessionDAO.getActiveChatSessionByComputer(computerId);

        if (chatSession == null) {
            // Create new session
            chatSession = new ChatSession(customerId, computerId, computerName);
            int sessionId = chatSessionDAO.createChatSession(chatSession);

            if (sessionId > 0) {
                chatSession.setSessionId(sessionId);
                appendSystemMessage("Phiên chat mới đã được tạo. Vui lòng đợi nhân viên phản hồi.");
                statusLabel.setText("Đang chờ nhân viên...");
            } else {
                appendSystemMessage("Không thể tạo phiên chat. Vui lòng thử lại sau.");
                statusLabel.setText("Lỗi kết nối");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
            }
        } else {
            // Use existing session
            appendSystemMessage("Đã kết nối với phiên chat hiện tại.");

            if ("waiting".equals(chatSession.getStatus())) {
                statusLabel.setText("Đang chờ nhân viên...");
            } else if ("active".equals(chatSession.getStatus())) {
                statusLabel.setText("Đang kết nối với nhân viên");
            } else {
                statusLabel.setText("Phiên chat đã kết thúc");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
            }

            // Load existing messages
            loadMessages();
        }
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

            // Mark employee messages as read
            chatSessionDAO.markAsRead(chatSession.getSessionId(), false);

            // Scroll to bottom
            chatArea.setCaretPosition(doc.getLength());

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();

        if (message.isEmpty() || chatSession == null) {
            return;
        }

        // Create and save customer message
        CustomerMessage customerMessage = new CustomerMessage(
                chatSession.getSessionId(),
                customerId,
                message);

        int messageId = customerMessageDAO.addMessage(customerMessage);

        if (messageId > 0) {
            try {
                // Display message in chat area
                appendCustomerMessage(message, null);

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
            if ("waiting".equals(chatSession.getStatus())) {
                statusLabel.setText("Đang chờ nhân viên...");
            } else if ("active".equals(chatSession.getStatus())) {
                statusLabel.setText("Đang kết nối với nhân viên");
            } else {
                statusLabel.setText("Phiên chat đã kết thúc");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                stopRefreshTimer();
            }

            // Load messages
            loadMessages();
        }
    }

    private void appendCustomerMessage(String message, Date sentAt) throws BadLocationException {
        String time = (sentAt != null) ? timeFormat.format(sentAt) : timeFormat.format(new java.util.Date());
        doc.insertString(doc.getLength(), "Bạn (" + time + "): ", customerStyle);
        doc.insertString(doc.getLength(), message + "\n", null);
    }

    private void appendEmployeeMessage(String message, Date sentAt) throws BadLocationException {
        String time = (sentAt != null) ? timeFormat.format(sentAt) : timeFormat.format(new java.util.Date());
        doc.insertString(doc.getLength(), "Nhân viên (" + time + "): ", employeeStyle);
        doc.insertString(doc.getLength(), message + "\n", null);
    }

    private void appendSystemMessage(String message) {
        try {
            doc.insertString(doc.getLength(), "Hệ thống: " + message + "\n", systemStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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

}
