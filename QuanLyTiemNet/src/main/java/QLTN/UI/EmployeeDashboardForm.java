package QLTN.UI;



import QLTN.Dao.ChatSessionDAO;
import QLTN.Entity.ChatSession;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class EmployeeDashboardForm extends JFrame {
    // Components
    private JList<ChatSession> waitingSessionsList;
    private JList<ChatSession> activeSessionsList;
    private DefaultListModel<ChatSession> waitingSessionsModel;
    private DefaultListModel<ChatSession> activeSessionsModel;
    private JButton refreshButton;
    private JLabel statusLabel;

    // Data
    private int employeeId;
    private Timer refreshTimer;

    // DAOs
    private ChatSessionDAO chatSessionDAO;

    public EmployeeDashboardForm(int employeeId) {
        this.employeeId = employeeId;

        // Initialize DAOs
        chatSessionDAO = new ChatSessionDAO();

        // Initialize UI
        initUI();

        // Load sessions
        loadSessions();

        // Start refresh timer
        startRefreshTimer();
    }

    private void initUI() {
        setTitle("Quản lý hỗ trợ khách hàng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Sessions panel
        JPanel sessionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Waiting sessions panel
        JPanel waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.setBorder(new TitledBorder("Phiên chat đang chờ"));

        waitingSessionsModel = new DefaultListModel<>();
        waitingSessionsList = new JList<>(waitingSessionsModel);
        waitingSessionsList.setCellRenderer(new ChatSessionListCellRenderer(true));
        waitingSessionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openChatSession(waitingSessionsList.getSelectedValue(), true);
                }
            }
        });

        JScrollPane waitingScrollPane = new JScrollPane(waitingSessionsList);
        waitingPanel.add(waitingScrollPane, BorderLayout.CENTER);

        // Active sessions panel
        JPanel activePanel = new JPanel(new BorderLayout());
        activePanel.setBorder(new TitledBorder("Phiên chat đang hoạt động"));

        activeSessionsModel = new DefaultListModel<>();
        activeSessionsList = new JList<>(activeSessionsModel);
        activeSessionsList.setCellRenderer(new ChatSessionListCellRenderer(false));
        activeSessionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openChatSession(activeSessionsList.getSelectedValue(), false);
                }
            }
        });

        JScrollPane activeScrollPane = new JScrollPane(activeSessionsList);
        activePanel.add(activeScrollPane, BorderLayout.CENTER);

        // Add panels to sessions panel
        sessionsPanel.add(waitingPanel);
        sessionsPanel.add(activePanel);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusLabel = new JLabel("Sẵn sàng");
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadSessions());

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(refreshButton, BorderLayout.EAST);

        // Add components to main panel
        mainPanel.add(sessionsPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

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

    private void loadSessions() {
        // Clear models
        waitingSessionsModel.clear();
        activeSessionsModel.clear();

        // Get all active sessions
        List<ChatSession> allSessions = chatSessionDAO.getAllActiveChatSessions();

        // Separate waiting and active sessions
        for (ChatSession session : allSessions) {
            if ("waiting".equals(session.getStatus())) {
                waitingSessionsModel.addElement(session);
            } else if ("active".equals(session.getStatus())) {
                // Only show active sessions assigned to this employee
                if (session.getEmployeeId() != null && session.getEmployeeId() == employeeId) {
                    activeSessionsModel.addElement(session);
                }
            }
        }

        // Update status label
        statusLabel.setText("Có " + waitingSessionsModel.size() + " phiên chat đang chờ và "
                + activeSessionsModel.size() + " phiên chat đang hoạt động");
    }

    private void openChatSession(ChatSession session, boolean isNew) {
        if (session == null) return;

        if (isNew) {
            // Update session status and assign to this employee
            session.setEmployeeId(employeeId);
            session.setStatus("active");
            chatSessionDAO.updateChatSession(session);
        }

        // Open chat form
        SwingUtilities.invokeLater(() -> {
            EmployeeChatForm chatForm = new EmployeeChatForm(session, employeeId);
            chatForm.setVisible(true);

            // Refresh list after chat form is closed
            chatForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadSessions();
                }
            });
        });
    }

    private void startRefreshTimer() {
        // Refresh sessions list every 10 seconds
        refreshTimer = new Timer(10000, e -> loadSessions());
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    // Custom cell renderer for chat sessions
    private class ChatSessionListCellRenderer extends DefaultListCellRenderer {
        private boolean isWaiting;

        public ChatSessionListCellRenderer(boolean isWaiting) {
            this.isWaiting = isWaiting;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ChatSession) {
                ChatSession session = (ChatSession) value;

                // Set text
                setText(session.getComputerName() + " - " +
                        new java.text.SimpleDateFormat("HH:mm:ss").format(session.getStartedAt()));

                // Set icon based on unread status
                if (session.isHasUnread()) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    if (!isSelected) {
                        setForeground(Color.RED);
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }

                // Set tooltip
                setToolTipText("Nhấp đúp để mở phiên chat");
            }

            return c;
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
//            new EmployeeDashboardForm(1).setVisible(true);
//        });
//    }
}

