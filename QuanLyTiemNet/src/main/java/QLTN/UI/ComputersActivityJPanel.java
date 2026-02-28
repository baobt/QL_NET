/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package QLTN.UI;

import QLTN.Dao.ComputerDao;
import QLTN.Dao.ComputerGroupDao;
import QLTN.Dao.MessageDAO;
import QLTN.Dao.TransactionDao;
import QLTN.Dao.*;
import QLTN.Entity.ChatSession;
import QLTN.Entity.Computer;
import QLTN.Entity.ComputerGroup;
import QLTN.Utils.JdbcHelperQLTN;
import QLTN.Utils.Masterfrom;
import QLTN.Utils.MessageBoxQLTN;
import QLTN.Utils.SessionManager;
import QLTN.Utils.CustomerSessionManager;
import QLTN.Utils.TimeFormatter;
import QLTN.Utils.WrapLayout;
import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author AD
 */
public class ComputersActivityJPanel extends Masterfrom {

    private ComputerDao computerDao;
    private ComputerGroupDao computerGroupDao;
    private TransactionDao transactionDao;
    private List<Computer> computers;
    private Map<Integer, ComputerGroup> computerGroups;
    private Map<Integer, JButton> computerButtons;
    private SessionManager sessionManager;
        private CustomerSessionManager customerSessionManager;

    public ComputersActivityJPanel() {
        initComponents();
        computerDao = new ComputerDao();
        computerGroupDao = new ComputerGroupDao();
        transactionDao = new TransactionDao();
        computerGroups = new HashMap<>();
        computerButtons = new HashMap<>();
        sessionManager = SessionManager.getInstance();
          customerSessionManager = CustomerSessionManager.getInstance();
        setupScrollPane();
        initializeRadioButtons();
        fetchComputers("Standard");
        startNotificationTimer();
        updateNotificationCount();
        startComputerStatusRefreshTimer();
    }
    
    private void startComputerStatusRefreshTimer() {
        Timer refreshTimer = new Timer(20000, e -> {
            SwingUtilities.invokeLater(() -> {
                String groupName = getSelectedGroup();
                fetchComputers(groupName);
                System.out.println("Làm mới danh sách máy tính cho nhóm: " + groupName);
            });
        });
        refreshTimer.start();
    }
    
    private void setupScrollPane() {
        pnlListComputer.setLayout(new WrapLayout(WrapLayout.LEFT, 7, 7));
        pnlListComputer.setPreferredSize(null);
        pnlListComputer.setMaximumSize(null);
        
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane1.getVerticalScrollBar().setBlockIncrement(64);
    }
    
    private void initializeRadioButtons() {
        try {
            List<ComputerGroup> groups = computerGroupDao.selectAll();
            if (groups == null || groups.isEmpty()) {
                throw new RuntimeException("Không có nhóm máy nào trong cơ sở dữ liệu.");
            }
            for (ComputerGroup group : groups) {
                computerGroups.put(group.getGroupId(), group);
                System.out.println("Group loaded: " + group.getName() + ", ID: " + group.getGroupId());
                if ("inactive".equalsIgnoreCase(group.getStatus())) {
                    switch (group.getName()) {
                        case "Standard":
                            rdoStandard.setEnabled(false);
                            break;
                        case "VIP":
                            rdoVIP.setEnabled(false);
                            break;
                        case "Gaming Pro":
                            rdoGaming.setEnabled(false);
                            break;
                    }
                }
            }
            rdoStandard.setSelected(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo nhóm máy: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
            e.printStackTrace();
        }
    }

    private void fetchComputers(String groupName) {
        pnlListComputer.removeAll();
        computerButtons.clear();

        pnlListComputer.setLayout(new WrapLayout(WrapLayout.LEFT, 7, 7));

        try {
            if (computerGroups.isEmpty()) {
                throw new IllegalStateException("Danh sách nhóm máy trống. Vui lòng kiểm tra dữ liệu ComputerGroup.");
            }

            System.out.println("Group name: " + groupName);
            System.out.println("Computer groups: " + computerGroups);

            int groupId = computerGroups.values().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .map(ComputerGroup::getGroupId)
                    .orElse(-1);
            if (groupId == -1) {
                throw new RuntimeException("Không tìm thấy nhóm: " + groupName);
            }

            System.out.println("Group ID: " + groupId);

            computers = computerDao.selectbySql("SELECT * FROM Computer WHERE group_id = ?", groupId);
            if (computers == null || computers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có máy tính nào trong nhóm: " + groupName);
                return;
            }

            System.out.println("Computers: " + computers);

            ImageIcon offlineImg = new ImageIcon(getClass().getResource("/icons/screen.png"));
            ImageIcon lockImg = new ImageIcon(getClass().getResource("/icons/screen_clock.png"));
            ImageIcon onlineImg = new ImageIcon(getClass().getResource("/icons/computer.png"));

            Image scaledOfflineImg = offlineImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            Image scaledLockImg = lockImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            Image scaledOnlineImg = onlineImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

            offlineImg = new ImageIcon(scaledOfflineImg);
            lockImg = new ImageIcon(scaledLockImg);
            onlineImg = new ImageIcon(scaledOnlineImg);

            for (Computer computer : computers) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem openItem = new JMenuItem("Mở máy");
                JMenuItem shutDownItem = new JMenuItem("Tắt máy");
                JMenuItem lockItem = new JMenuItem("Khóa máy");
                JMenuItem infoItem = new JMenuItem("Thông tin máy");
                JMenuItem messageItem = new JMenuItem("Tin nhắn");
                JMenuItem addMoneyItem = new JMenuItem("Nạp tiền");

                Font menuFont = new Font("Tahoma", Font.PLAIN, 15);
                openItem.setFont(menuFont);
                shutDownItem.setFont(menuFont);
                lockItem.setFont(menuFont);
                infoItem.setFont(menuFont);
                messageItem.setFont(menuFont);
                addMoneyItem.setFont(menuFont);

                popupMenu.add(openItem);
                popupMenu.add(shutDownItem);
                popupMenu.add(lockItem);
                popupMenu.addSeparator();
                popupMenu.add(infoItem);
                popupMenu.add(messageItem);
                popupMenu.add(addMoneyItem);

                JButton button = new JButton(computer.getName());
                button.setComponentPopupMenu(popupMenu);
                button.addActionListener(e -> popupMenu.show(button, button.getWidth() / 2, button.getHeight() / 2));
                button.setFont(new Font("Tahoma", Font.BOLD, 14));
                button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                button.setBackground(new Color(255, 255, 255));
                button.setPreferredSize(new java.awt.Dimension(175, 175));

                computerButtons.put(computer.getComputerId(), button);

                switch (computer.getStatus().toLowerCase()) {
                    case "active":
                        button.setIcon(onlineImg);
                        button.setForeground(new Color(0x68D391));
                        openItem.setEnabled(false);
                        addMoneyItem.setEnabled(true);
                        break;
                    case "inactive":
                        button.setIcon(offlineImg);
                        button.setForeground(null);
                        shutDownItem.setEnabled(false);
                        messageItem.setEnabled(false);
                        addMoneyItem.setEnabled(false);
                        break;
                    case "maintenance":
                        button.setIcon(lockImg);
                        button.setForeground(new Color(0xF56565));
                        openItem.setEnabled(false);
                        shutDownItem.setEnabled(false);
                        lockItem.setEnabled(false);
                        messageItem.setEnabled(false);
                        addMoneyItem.setEnabled(false);
                        break;
                    default:
                        throw new IllegalStateException("Trạng thái máy không hợp lệ: " + computer.getStatus());
                }

                openItem.addActionListener(e -> openComputer(computer));
                shutDownItem.addActionListener(e -> shutDownComputer(computer));
                lockItem.addActionListener(e -> lockComputer(computer));
                infoItem.addActionListener(e -> showComputerInfo(computer));
                addMoneyItem.addActionListener(e -> addMoneyToComputer(computer));

                pnlListComputer.add(button);
            }

            updatePanelSize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải máy tính: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
            e.printStackTrace();
        }
    }
    
    private void updatePanelSize() {
        SwingUtilities.invokeLater(() -> {
            Dimension viewportSize = jScrollPane1.getViewport().getSize();
            int viewportWidth = viewportSize.width;
            
            if (viewportWidth <= 0) {
                viewportWidth = jScrollPane1.getWidth() - jScrollPane1.getVerticalScrollBar().getPreferredSize().width;
            }
            
            int buttonWidth = 175 + 7;
            int columnsPerRow = Math.max(1, viewportWidth / buttonWidth);
            
            int totalButtons = computers != null ? computers.size() : 0;
            int totalRows = totalButtons > 0 ? (int) Math.ceil((double) totalButtons / columnsPerRow) : 1;
            
            int buttonHeight = 175 + 7;
            int preferredHeight = Math.max(buttonHeight, totalRows * buttonHeight + 14);
            
            pnlListComputer.setPreferredSize(new Dimension(viewportWidth, preferredHeight));
            
            pnlListComputer.revalidate();
            pnlListComputer.repaint();
        });
    }
    
    private void openComputer(Computer computer) {
        try {
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(computer.getGroupId()));
            if (group == null) {
                throw new Exception("Không tìm thấy nhóm máy với ID: " + computer.getGroupId());
            }
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Nạp tiền để mở máy " + computer.getName());
            dialog.setModal(true);
            dialog.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.insets = new java.awt.Insets(10, 10, 10, 10);
            
            JLabel priceLabel = new JLabel("Giá: " + TimeFormatter.formatMoney(group.getHourlyRate()) + " VND/giờ");
            priceLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
            priceLabel.setForeground(new Color(16, 54, 103));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            dialog.add(priceLabel, gbc);
            
            JLabel label = new JLabel("Nhập số tiền trả trước:");
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
            dialog.add(label, gbc);
            
            JTextField amountField = new JTextField(10);
            gbc.gridx = 1; gbc.gridy = 1;
            dialog.add(amountField, gbc);
            
            JLabel timeLabel = new JLabel("Thời gian: 0 giờ 0 phút");
            timeLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
            timeLabel.setForeground(Color.GRAY);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            dialog.add(timeLabel, gbc);
            
            amountField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    try {
                        double amount = Double.parseDouble(amountField.getText());
                        double hours = amount / group.getHourlyRate().doubleValue();
                        int totalMinutes = (int)(hours * 60);
                        int displayHours = totalMinutes / 60;
                        int displayMinutes = totalMinutes % 60;
                        timeLabel.setText("Thời gian: " + displayHours + " giờ " + displayMinutes + " phút");
                    } catch (NumberFormatException ex) {
                        timeLabel.setText("Thời gian: 0 giờ 0 phút");
                    }
                }
            });
            
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            
            okButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 1000) {
                        throw new NumberFormatException("Số tiền phải lớn hơn 1,000 VND");
                    }
                    createSession(computer.getComputerId(), amount);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Số tiền không hợp lệ. " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
            dialog.add(okButton, gbc);
            gbc.gridx = 1; gbc.gridy = 3;
            dialog.add(cancelButton, gbc);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMoneyToComputer(Computer computer) {
        try {
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(computer.getGroupId()));
            if (group == null) {
                throw new Exception("Không tìm thấy nhóm máy với ID: " + computer.getGroupId());
            }
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Nạp tiền cho máy " + computer.getName());
            dialog.setModal(true);
            dialog.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.insets = new java.awt.Insets(10, 10, 10, 10);
            
            JLabel priceLabel = new JLabel("Giá: " + TimeFormatter.formatMoneyVND(group.getHourlyRate()) + "/giờ");
            priceLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
            priceLabel.setForeground(new Color(16, 54, 103));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            dialog.add(priceLabel, gbc);
            
            JLabel label = new JLabel("Nhập số tiền nạp thêm:");
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
            dialog.add(label, gbc);
            
            JTextField amountField = new JTextField(10);
            gbc.gridx = 1; gbc.gridy = 1;
            dialog.add(amountField, gbc);
            
            JLabel timeLabel = new JLabel("Thời gian thêm: 0 giờ 0 phút");
            timeLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
            timeLabel.setForeground(Color.GRAY);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            dialog.add(timeLabel, gbc);
            
            amountField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    try {
                        double amount = Double.parseDouble(amountField.getText());
                        double hours = amount / group.getHourlyRate().doubleValue();
                        int totalMinutes = (int)(hours * 60);
                        int displayHours = totalMinutes / 60;
                        int displayMinutes = totalMinutes % 60;
                        timeLabel.setText("Thời gian thêm: " + displayHours + " giờ " + displayMinutes + " phút");
                    } catch (NumberFormatException ex) {
                        timeLabel.setText("Thời gian thêm: 0 giờ 0 phút");
                    }
                }
            });
            
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            okButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        throw new NumberFormatException("Số tiền phải lớn hơn 0");
                    }
                    addMoneyToActiveComputer(computer.getComputerId(), amount);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Nạp tiền thành công!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Số tiền không hợp lệ. " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi nạp tiền: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
            dialog.add(okButton, gbc);
            gbc.gridx = 1; gbc.gridy = 3;
            dialog.add(cancelButton, gbc);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMoneyToActiveComputer(int computerId, double amount) {
        try {
            // Kiểm tra xem máy có đang hoạt động không
            ResultSet rs = JdbcHelperQLTN.query("SELECT * FROM ComputerUsage WHERE computer_id = ? AND end_time IS NULL", computerId);
            if (!rs.next()) {
                throw new Exception("Máy không có phiên sử dụng đang hoạt động");
            }
            
            int usageId = rs.getInt("usage_id");
            int customerId = rs.getInt("customer_id");
            
            // Lấy thông tin nhóm máy để tính thời gian
            Computer computer = computerDao.GetComputer(String.valueOf(computerId));
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(computer.getGroupId()));
            
            // THÊM MỚI: Kiểm tra loại khách hàng và xử lý tương ứng
            if (customerId == 1) {
                // Khách vãng lai - sử dụng SessionManager cũ
                sessionManager.addMoney(computerId, amount, group);
            } else {
                // Khách có tài khoản - sử dụng CustomerSessionManager mới
                customerSessionManager.addMoneyToCustomerSession(computerId, amount);
            }
            
            // Lưu transaction
            transactionDao.createTopupTransaction(customerId, usageId, amount, "cash");
            
            // Cập nhật total_amount trong ComputerUsage
            JdbcHelperQLTN.update("UPDATE ComputerUsage SET total_amount = total_amount + ? WHERE usage_id = ?", amount, usageId);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi nạp tiền: " + e.getMessage(), e);
        }
    }
    
    private void shutDownComputer(Computer computer) {
        try {
            ResultSet rs = JdbcHelperQLTN.query("SELECT * FROM ComputerUsage WHERE computer_id = ? AND end_time IS NULL", computer.getComputerId());
            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                
                JdbcHelperQLTN.update("UPDATE ComputerUsage SET end_time = CURRENT_TIMESTAMP WHERE usage_id = ?", rs.getInt("usage_id"));
                JdbcHelperQLTN.update("UPDATE Computer SET status = 'inactive' WHERE computer_id = ?", computer.getComputerId());

                // CẬP NHẬT: Thông báo kết thúc phiên theo loại khách hàng
                if (customerId == 1) {
                    // Khách vãng lai
                    sessionManager.endSession(computer.getComputerId(), "Tắt máy");
                } else {
                    // Khách có tài khoản
                    customerSessionManager.endCustomerSession(computer.getComputerId(), "Tắt máy");
                }
                
                computer = computerDao.GetComputer(String.valueOf(computer.getComputerId()));
                updateComputerButton(computer);
            } else {
                JOptionPane.showMessageDialog(this, "Không có phiên sử dụng đang hoạt động cho máy này.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tắt máy: " + e.getMessage());
        }
    }

    private void lockComputer(Computer computer) {
        try {
            ResultSet rs = JdbcHelperQLTN.query("SELECT * FROM ComputerUsage WHERE computer_id = ? AND end_time IS NULL", computer.getComputerId());
            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                
                JdbcHelperQLTN.update("UPDATE ComputerUsage SET end_time = CURRENT_TIMESTAMP WHERE usage_id = ?", rs.getInt("usage_id"));
                
                // CẬP NHẬT: Thông báo kết thúc phiên theo loại khách hàng
                if (customerId == 1) {
                    sessionManager.endSession(computer.getComputerId(), "Khóa máy");
                } else {
                    customerSessionManager.endCustomerSession(computer.getComputerId(), "Khóa máy");
                }
            }

            JdbcHelperQLTN.update("UPDATE Computer SET status = 'maintenance' WHERE computer_id = ?", computer.getComputerId());

            computer = computerDao.GetComputer(String.valueOf(computer.getComputerId()));
            updateComputerButton(computer);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khóa máy: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateComputerButton(Computer computer) {
        JButton button = computerButtons.get(computer.getComputerId());
        if (button == null) {
            return;
        }

        ImageIcon offlineImg = new ImageIcon(getClass().getResource("/icons/screen.png"));
        ImageIcon lockImg = new ImageIcon(getClass().getResource("/icons/screen_clock.png"));
        ImageIcon onlineImg = new ImageIcon(getClass().getResource("/icons/computer.png"));

        Image scaledOfflineImg = offlineImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        Image scaledLockImg = lockImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        Image scaledOnlineImg = onlineImg.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

        offlineImg = new ImageIcon(scaledOfflineImg);
        lockImg = new ImageIcon(scaledLockImg);
        onlineImg = new ImageIcon(scaledOnlineImg);

        switch (computer.getStatus().toLowerCase()) {
            case "active":
                button.setIcon(onlineImg);
                button.setForeground(new Color(0x68D391));
                break;
            case "inactive":
                button.setIcon(offlineImg);
                button.setForeground(null);
                break;
            case "maintenance":
                button.setIcon(lockImg);
                button.setForeground(new Color(0xF56565));
                break;
            default:
                throw new IllegalStateException("Trạng thái máy không hợp lệ: " + computer.getStatus());
        }

        JPopupMenu popupMenu = button.getComponentPopupMenu();
        if (popupMenu != null) {
            JMenuItem openItem = (JMenuItem) popupMenu.getComponent(0);
            JMenuItem shutDownItem = (JMenuItem) popupMenu.getComponent(1);
            JMenuItem lockItem = (JMenuItem) popupMenu.getComponent(2);
            JMenuItem messageItem = (JMenuItem) popupMenu.getComponent(5);
            JMenuItem addMoneyItem = (JMenuItem) popupMenu.getComponent(6);

            switch (computer.getStatus().toLowerCase()) {
                case "active":
                    openItem.setEnabled(false);
                    shutDownItem.setEnabled(true);
                    lockItem.setEnabled(true);
                    messageItem.setEnabled(true);
                    addMoneyItem.setEnabled(true);
                    break;
                case "inactive":
                    openItem.setEnabled(true);
                    shutDownItem.setEnabled(false);
                    lockItem.setEnabled(false);
                    messageItem.setEnabled(false);
                    addMoneyItem.setEnabled(false);
                    break;
                case "maintenance":
                    openItem.setEnabled(false);
                    shutDownItem.setEnabled(false);
                    lockItem.setEnabled(false);
                    messageItem.setEnabled(false);
                    addMoneyItem.setEnabled(false);
                    break;
            }
        }

        pnlListComputer.revalidate();
        pnlListComputer.repaint();
    }

    private void showComputerInfo(Computer computer) {
        try {
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(computer.getGroupId()));
            String groupInfo = group != null ? group.getName() + " (" + TimeFormatter.formatMoneyVND(group.getHourlyRate()) + "/giờ)" : "Không xác định";
            
            JOptionPane.showMessageDialog(this,
                    String.format("ID: %d\nTên: %s\nTrạng thái: %s\nNhóm: %s\nThông số: %s\nIP: %s\nVị trí: %d",
                            computer.getComputerId(), computer.getName(), computer.getStatus(),
                            groupInfo, computer.getSpecifications(), computer.getIpAddress(), computer.getPosition()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi hiển thị thông tin: " + e.getMessage());
        }
    }
    
    private void createSession(int computerId, double prepaidAmount) {
        try {
            Computer computer = computerDao.GetComputer(String.valueOf(computerId));
            if (computer == null) {
                throw new Exception("Không tìm thấy máy tính với ID: " + computerId);
            }
            int groupId = computer.getGroupId();
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(groupId));
            if (group == null) {
                throw new Exception("Không tìm thấy nhóm máy với ID: " + groupId);
            }
            double hourlyRate = group.getHourlyRate().doubleValue();

            // Tạo phiên sử dụng trong database
            JdbcHelperQLTN.update(
                    "INSERT INTO ComputerUsage (computer_id, customer_id, start_time, hourly_rate, total_amount) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)",
                    computerId, 1, hourlyRate, prepaidAmount);
            JdbcHelperQLTN.update("UPDATE Computer SET status = 'active' WHERE computer_id = ?", computerId);

            // Tạo session trong SessionManager (will notify listeners)
            sessionManager.createSession(computerId, prepaidAmount, group);

            // Lưu transaction
            ResultSet rs = JdbcHelperQLTN.query("SELECT TOP 1 usage_id FROM ComputerUsage WHERE computer_id = ? ORDER BY usage_id DESC", computerId);
            if (rs.next()) {
                int usageId = rs.getInt("usage_id");
                transactionDao.createTopupTransaction(1, usageId, prepaidAmount, "cash");
            }

            // No need to call updateComputerButton here; listener will handle it
            // Hiển thị dialog và mở panel
            showStartDialogAndOpenCustomerPanel(computerId, prepaidAmount, group);

        } catch (Exception e) {
            MessageBoxQLTN.alert(this, "Lỗi truy vấn dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showStartDialogAndOpenCustomerPanel(int computerId, double prepaidAmount, ComputerGroup group) {
        StartSessionDialog startDialog = new StartSessionDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            () -> {
                openHomeCustomerPanel(computerId);
            }
        );
        startDialog.setVisible(true);
    }
    
    private void openHomeCustomerPanel(int computerId) {
        SwingUtilities.invokeLater(() -> {
            HomeCustomerJPanel customerPanel = new HomeCustomerJPanel(computerId);
            
            javax.swing.JFrame customerFrame = new javax.swing.JFrame("Máy " + computerId + " - Khách hàng");
            customerFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            customerFrame.add(customerPanel);
            customerFrame.setSize(400, 500);

            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            int margin = 38;
            int x = screenSize.width - customerFrame.getWidth() - margin;
            int y = margin;
            customerFrame.setLocation(x, y);

            customerFrame.setVisible(true);
            
            customerPanel.startTimer();
        });
    }

    private String getSelectedGroup() {
        if (rdoStandard.isSelected()) {
            return "Standard";
        }
        if (rdoVIP.isSelected()) {
            return "VIP";
        }
        if (rdoGaming.isSelected()) {
            return "Gaming Pro";
        }
        return "Standard";
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlRadioGroup = new javax.swing.JPanel();
        rdoGaming = new javax.swing.JRadioButton();
        rdoVIP = new javax.swing.JRadioButton();
        rdoStandard = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlListComputer = new javax.swing.JPanel();
        pnlThongbao = new javax.swing.JPanel();
        btnThongbao = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        pnlRadioGroup.setBackground(new java.awt.Color(255, 255, 255));

        rdoGaming.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rdoGaming);
        rdoGaming.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        rdoGaming.setText("Gaming Pro");
        rdoGaming.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoGamingActionPerformed(evt);
            }
        });

        rdoVIP.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rdoVIP);
        rdoVIP.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        rdoVIP.setText("VIP");
        rdoVIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoVIPActionPerformed(evt);
            }
        });

        rdoStandard.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rdoStandard);
        rdoStandard.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        rdoStandard.setSelected(true);
        rdoStandard.setText("Standard");
        rdoStandard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoStandardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRadioGroupLayout = new javax.swing.GroupLayout(pnlRadioGroup);
        pnlRadioGroup.setLayout(pnlRadioGroupLayout);
        pnlRadioGroupLayout.setHorizontalGroup(
            pnlRadioGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRadioGroupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rdoStandard)
                .addGap(35, 35, 35)
                .addComponent(rdoVIP)
                .addGap(36, 36, 36)
                .addComponent(rdoGaming)
                .addContainerGap())
        );
        pnlRadioGroupLayout.setVerticalGroup(
            pnlRadioGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioGroupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlRadioGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoStandard)
                    .addComponent(rdoVIP)
                    .addComponent(rdoGaming)))
        );

        pnlListComputer.setBackground(new java.awt.Color(255, 255, 255));
        pnlListComputer.setAlignmentX(0.0F);
        pnlListComputer.setAlignmentY(0.0F);
        pnlListComputer.setInheritsPopupMenu(true);
        pnlListComputer.setMaximumSize(new java.awt.Dimension(0, 0));
        pnlListComputer.setPreferredSize(new java.awt.Dimension(1295, 0));

        javax.swing.GroupLayout pnlListComputerLayout = new javax.swing.GroupLayout(pnlListComputer);
        pnlListComputer.setLayout(pnlListComputerLayout);
        pnlListComputerLayout.setHorizontalGroup(
            pnlListComputerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1295, Short.MAX_VALUE)
        );
        pnlListComputerLayout.setVerticalGroup(
            pnlListComputerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 704, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnlListComputer);

        btnThongbao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/email.png"))); // NOI18N
        btnThongbao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThongbaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlThongbaoLayout = new javax.swing.GroupLayout(pnlThongbao);
        pnlThongbao.setLayout(pnlThongbaoLayout);
        pnlThongbaoLayout.setHorizontalGroup(
            pnlThongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongbaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnThongbao)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        pnlThongbaoLayout.setVerticalGroup(
            pnlThongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnThongbao, javax.swing.GroupLayout.PREFERRED_SIZE, 51, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(pnlRadioGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlThongbao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRadioGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlThongbao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 706, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rdoVIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoVIPActionPerformed
        // TODO add your handling code here:
        fetchComputers("VIP");
    }//GEN-LAST:event_rdoVIPActionPerformed

    private void rdoGamingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoGamingActionPerformed
        // TODO add your handling code here:
        fetchComputers("Gaming Pro");
    }//GEN-LAST:event_rdoGamingActionPerformed

    private void rdoStandardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoStandardActionPerformed
        // TODO add your handling code here:
        fetchComputers("Standard");
    }//GEN-LAST:event_rdoStandardActionPerformed

    private void btnThongbaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongbaoActionPerformed
        // TODO add your handling code here:
                SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new EmployeeDashboardForm(1).setVisible(true);
        });
    }//GEN-LAST:event_btnThongbaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnThongbao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlListComputer;
    private javax.swing.JPanel pnlRadioGroup;
    private javax.swing.JPanel pnlThongbao;
    private javax.swing.JRadioButton rdoGaming;
    private javax.swing.JRadioButton rdoStandard;
    private javax.swing.JRadioButton rdoVIP;
    // End of variables declaration//GEN-END:variables

    @Override
    public void formRefresh() {

    }
    private void updateNotificationCount() {
        MessageDAO dao = new MessageDAO();
        int count = dao.countUnreadCustomerMessages();

        if (count > 0) {
            btnThongbao.setText(" (" + count + ")");
        } else {
            btnThongbao.setText("");
        }
    }
    private Timer notificationTimer;

    private void startNotificationTimer() {
        notificationTimer = new Timer(5000, e -> updateNotificationCount());
        notificationTimer.start();
    }
}
