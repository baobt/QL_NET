/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package QLTN.UI;

import QLTN.Utils.MessageBoxQLTN;
import QLTN.Utils.SessionManager;
import QLTN.Utils.JdbcHelperQLTN;
import QLTN.Utils.TimeFormatter;
import QLTN.Utils.CustomerSessionManager;
import QLTN.Utils.CustomerSessionInfo;
import QLTN.Utils.NetworkUtils;
import QLTN.Dao.ComputerDao;
import QLTN.Dao.ComputerGroupDao;
import QLTN.Dao.CustomerDao;
import QLTN.Entity.Computer;
import QLTN.Entity.ComputerGroup;
import QLTN.Entity.Customer;
import QLTN.Utils.Masterfrom;
import QLTN.Utils.SessionInfo;
import QLTN.Utils.UndoRedo;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 *
 * @author AD
 */
public class HomeCustomerFrame extends javax.swing.JFrame implements CustomerSessionManager.CustomerSessionEndListener {

    private Customer loggedInCustomer;
    private int computerId;
    private CustomerSessionManager sessionManager;
    private Timer updateTimer;
    private boolean fiveMinuteWarningShown = false;
    private boolean timeUpDialogShown = false;

    public HomeCustomerFrame() {
        initComponents();
        this.setVisible(false);
        sessionManager = CustomerSessionManager.getInstance();
        addWindowCloseHandler();
        setupLoginProcess();
        updateDisplay();
        updateSession();
        initializeCustomerSession();
    }

    private boolean validateComputerAndIP() {
        try {
            String currentIP = NetworkUtils.getCurrentMachineIP();
            System.out.println("Validating IP: " + currentIP);

            if (currentIP == null || currentIP.trim().isEmpty()) {
                MessageBoxQLTN.alert(this, "Không thể lấy địa chỉ IP của máy hiện tại!");
                return false;
            }

            int foundComputerId = NetworkUtils.findComputerByIP(currentIP);
            if (foundComputerId == -1) {
                MessageBoxQLTN.alert(this,
                        "Máy tính với IP " + currentIP + " không được tìm thấy trong hệ thống!\n"
                        + "Vui lòng liên hệ quản trị viên để cập nhật thông tin máy tính.");
                return false;
            }

            computerId = foundComputerId;
            return true;

        } catch (Exception e) {
            MessageBoxQLTN.alert(this, "Lỗi khi xác thực máy tính: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void setupLoginProcess() {
        LoginCustomerJDialog loginDialog = new LoginCustomerJDialog(this, true);
        loginDialog.setVisible(true);

        if (loginDialog.isLoginSuccessful()) {
            loggedInCustomer = loginDialog.getLoggedInCustomer();
            initializeCustomerSession();
        } else {
            this.dispose();
            return;
        }
    }

    private void initializeCustomerSession() {
        try {
            // Lấy IP máy hiện tại
            String currentIP = NetworkUtils.getCurrentMachineIP();
            System.out.println("Current machine IP: " + currentIP);

            computerId = NetworkUtils.findComputerByIP(currentIP);
            System.out.println("Found computer ID: " + computerId);

            if (computerId == -1) {
                MessageBoxQLTN.alert(this, "Không thể xác định máy hiện tại. IP: " + currentIP);
                this.dispose();
                return;
            }

            // Kiểm tra trạng thái hiện tại của máy
            ComputerDao computerDao = new ComputerDao();
            Computer computer = computerDao.SelectbyID(String.valueOf(computerId));

            if (computer == null) {
                MessageBoxQLTN.alert(this, "Không tìm thấy thông tin máy tính với ID: " + computerId);
                this.dispose();
                return;
            }

            System.out.println("Computer current status: " + computer.getStatus());

//            // Kiểm tra xem máy có đang được sử dụng không
//            if ("active".equalsIgnoreCase(computer.getStatus())) {
//                int choice = JOptionPane.showConfirmDialog(
//                        this,
//                        "Máy này đang được sử dụng. Bạn có muốn tiếp tục không?",
//                        "Cảnh báo",
//                        JOptionPane.YES_NO_OPTION,
//                        JOptionPane.WARNING_MESSAGE
//                );
//
//                if (choice != JOptionPane.YES_OPTION) {
//                    this.dispose();
//                    return;
//                }
//            }
            // Cập nhật trạng thái máy thành active
            updateComputerStatus(computerId, "active");
            System.out.println("Updated computer status to: active");

            // Tạo phiên sử dụng
            sessionManager.createCustomerSession(computerId, loggedInCustomer);
            sessionManager.addSessionEndListener(computerId, this);

            CustomerSessionInfo session = sessionManager.getCustomerSession(computerId);
            if (session == null) {
                MessageBoxQLTN.alert(this, "Không thể tạo phiên sử dụng.");
                // Khôi phục trạng thái máy về inactive nếu tạo phiên thất bại
                updateComputerStatus(computerId, "inactive");
                this.dispose();
                return;
            }

            System.out.println("Phiên được tạo: computerId=" + computerId
                    + ", initialBalance=" + session.getInitialBalance()
                    + ", hourlyRate=" + session.getComputerGroup().getHourlyRate());

            startTimer();

            updateDisplay();
            this.setVisible(true);
            this.setSize(400, 600);
            this.setLocationRelativeTo(null);

        } catch (Exception e) {
            MessageBoxQLTN.alert(this, "Lỗi khởi tạo phiên: " + e.getMessage());
            e.printStackTrace();
            // Khôi phục trạng thái máy về inactive nếu có lỗi
            if (computerId != -1) {
                updateComputerStatus(computerId, "inactive");
            }
            this.dispose();
        }
    }

    private void updateComputerStatus(int computerId, String status) {
        try {
            ComputerDao computerDao = new ComputerDao();
            Computer computer = computerDao.SelectbyID(String.valueOf(computerId));

            if (computer != null) {
                computer.setStatus(status);
                computerDao.update(computer);
                System.out.println("Computer " + computerId + " status updated to: " + status);
            } else {
                System.err.println("Cannot find computer with ID: " + computerId);
            }
        } catch (Exception e) {
            System.err.println("Error updating computer status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }

        updateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Timer fired at: " + LocalDateTime.now());
                updateSession();
                updateDisplay();
                checkWarnings();
            }
        });
        updateTimer.start();
        System.out.println("Timer started for computerId=" + computerId);
    }

    private void updateSession() {
        CustomerSessionInfo session = sessionManager.getCustomerSession(computerId);
        if (session == null) {
            System.err.println("Session is null for computerId=" + computerId);
            return;
        }

        // Tính thời gian đã sử dụng
        LocalDateTime startTime = session.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        long usedSeconds = ChronoUnit.SECONDS.between(startTime, now);
        session.setUsedTimeSeconds((int) usedSeconds);

        // Tính số tiền đã sử dụng
        double usedHours = usedSeconds / 3600.0;
        BigDecimal hourlyRate = session.getComputerGroup().getHourlyRate();
        BigDecimal usedAmount = hourlyRate.multiply(BigDecimal.valueOf(usedHours)).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Tính số dư hiện tại
        BigDecimal currentBalance = session.getInitialBalance().subtract(usedAmount);
        currentBalance = currentBalance.max(BigDecimal.ZERO);
        session.setCurrentBalance(currentBalance);

        // Tính thời gian còn lại
        double remainingHours = currentBalance.doubleValue() / hourlyRate.doubleValue();
        session.setRemainingTimeSeconds((int) (remainingHours * 3600));

        // Log thông tin
        System.out.println("Updated session: usedSeconds=" + usedSeconds
                + ", usedAmount=" + usedAmount
                + ", currentBalance=" + currentBalance
                + ", remainingSeconds=" + session.getRemainingTimeSeconds());

        checkBalanceIncrease(session);
    }

    private void checkBalanceIncrease(CustomerSessionInfo session) {
        try {
            // Lấy số dư từ database
            CustomerDao customerDao = new CustomerDao();
            Customer updatedCustomer = customerDao.SelectbyID(String.valueOf(loggedInCustomer.getCustomerId()));

            if (updatedCustomer != null) {
                BigDecimal dbBalance = updatedCustomer.getBalance();
                BigDecimal sessionBalance = session.getInitialBalance();

                // Chỉ cập nhật nếu số dư database lớn hơn số dư ban đầu của phiên
                if (dbBalance.compareTo(sessionBalance) > 0) {
                    BigDecimal addedAmount = dbBalance.subtract(sessionBalance);
                    System.out.println("Detected balance increase: addedAmount=" + addedAmount);

                    // Cập nhật initialBalance và currentBalance
                    session.setInitialBalance(dbBalance);
                    session.setCurrentBalance(dbBalance.subtract(
                            session.getInitialBalance().subtract(session.getCurrentBalance())
                    ));

                    // Cập nhật thời gian còn lại
                    double remainingHours = session.getCurrentBalance().doubleValue()
                            / session.getComputerGroup().getHourlyRate().doubleValue();
                    session.setTotalTimeSeconds((int) (remainingHours * 3600));
                    session.setRemainingTimeSeconds((int) (remainingHours * 3600));

                    // Reset cảnh báo
                    fiveMinuteWarningShown = false;
                    timeUpDialogShown = false;

                    System.out.println("Balance updated: newInitialBalance=" + session.getInitialBalance()
                            + ", newCurrentBalance=" + session.getCurrentBalance());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra số dư: " + e.getMessage());
        }
    }

    private void updateDisplay() {
        CustomerSessionInfo session = sessionManager.getCustomerSession(computerId);
        if (session == null) {
            System.err.println("Cannot update display: session is null");
            return;
        }

        txtTongTG.setText(TimeFormatter.formatTime(session.getTotalTimeSeconds()));
        txtTGSuDung.setText(TimeFormatter.formatTime(session.getUsedTimeSeconds()));
        txtTGConLai.setText(TimeFormatter.formatTime(session.getRemainingTimeSeconds()));
        txtSoDuTK.setText(TimeFormatter.formatMoneyVND(session.getCurrentBalance()));

        txtSoDuTK.repaint();
        jPanel1.revalidate();
        jPanel1.repaint();
        System.out.println("Updated display: balance=" + TimeFormatter.formatMoneyVND(session.getCurrentBalance()));
    }

// Thông báo trước 5 phút    
    private void checkWarnings() {
        CustomerSessionInfo session = sessionManager.getCustomerSession(computerId);
        if (session != null) {
            int remainingSeconds = session.getRemainingTimeSeconds();

            if (remainingSeconds <= 300 && remainingSeconds > 0 && !fiveMinuteWarningShown) {
                fiveMinuteWarningShown = true;
                showFiveMinuteWarning();
            }

            // Thông báo hết giờ
            if (remainingSeconds <= 0 && !timeUpDialogShown) {
                timeUpDialogShown = true;
            }
        }
    }

    private void showFiveMinuteWarning() {
        SwingUtilities.invokeLater(() -> {
            JDialog warningDialog = new JDialog();
            warningDialog.setTitle("Thông báo");
            warningDialog.setModal(false);
            warningDialog.setAlwaysOnTop(true);

            JLabel messageLabel = new JLabel("Còn 5 phút nữa hết giờ!");
            messageLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
            messageLabel.setForeground(Color.RED);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            warningDialog.add(messageLabel);
            warningDialog.pack();

            // Đặt vị trí góc phải dưới màn hình
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            int x = screenSize.width - warningDialog.getWidth() - 50;
            int y = screenSize.height - warningDialog.getHeight() - 100;
            warningDialog.setLocation(x, y);

            warningDialog.setVisible(true);

            // Tự động đóng sau 10 giây
            Timer closeTimer = new Timer(10000, e -> warningDialog.dispose());
            closeTimer.setRepeats(false);
            closeTimer.start();
        });
    }

    @Override
    public void onSessionEnd(String reason) {
        SwingUtilities.invokeLater(() -> {
            if (updateTimer != null) {
                updateTimer.stop();
            }

            // Cập nhật trạng thái máy về inactive khi phiên kết thúc
            updateComputerStatus(computerId, "inactive");

            showSessionEndDialog(reason);
        });
    }

    @Override
    public void dispose() {
        try {
            // Dừng timer
            if (updateTimer != null) {
                updateTimer.stop();
            }

            // Cập nhật trạng thái máy về inactive
            if (computerId != -1) {
                updateComputerStatus(computerId, "inactive");
            }

            // Kết thúc phiên
            if (sessionManager != null && computerId != -1) {
                sessionManager.endCustomerSession(computerId, "Đóng ứng dụng");
            }

            System.out.println("HomeCustomerFrame disposed, computer status reset to inactive");

        } catch (Exception e) {
            System.err.println("Error during dispose: " + e.getMessage());
        } finally {
            super.dispose();
        }
    }

    private void addWindowCloseHandler() {
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (MessageBoxQLTN.confirm(HomeCustomerFrame.this, "Bạn có muốn đóng ứng dụng không?")) {
                    // Cập nhật trạng thái máy về inactive
                    updateComputerStatus(computerId, "inactive");

                    // Kết thúc phiên
                    if (sessionManager != null && computerId != -1) {
                        sessionManager.endCustomerSession(computerId, "Đóng ứng dụng");
                    }

                    System.exit(0);
                }
            }
        });
    }

    private void showSessionEndDialog(String reason) {
        JDialog sessionEndDialog = new JDialog(this, "Kết thúc phiên sử dụng", true);
        sessionEndDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        sessionEndDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel messageLabel = new JLabel("Kết thúc phiên sử dụng");
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        messageLabel.setForeground(new Color(16, 54, 103));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(messageLabel, gbc);

        JLabel reasonLabel = new JLabel("Lý do: " + reason);
        reasonLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        reasonLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(reasonLabel, gbc);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        okButton.setBackground(new Color(24, 71, 133));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new java.awt.Dimension(100, 40));
        okButton.addActionListener(e -> {
            sessionEndDialog.dispose();
            this.dispose();
        });

        gbc.gridy = 2;
        mainPanel.add(okButton, gbc);

        sessionEndDialog.add(mainPanel, BorderLayout.CENTER);
        sessionEndDialog.pack();
        sessionEndDialog.setLocationRelativeTo(this);
        sessionEndDialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlThongTin = new javax.swing.JPanel();
        lblthongtg = new javax.swing.JLabel();
        txtTongTG = new javax.swing.JTextField();
        lblthoigiansudung = new javax.swing.JLabel();
        txtTGSuDung = new javax.swing.JTextField();
        lbltgconlai = new javax.swing.JLabel();
        txtTGConLai = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        btnChat = new javax.swing.JButton();
        btnDangXuat = new javax.swing.JButton();
        btnDoiMK = new javax.swing.JButton();
        btnAnUong = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pnlSoTienTK = new javax.swing.JPanel();
        lblTiennap1 = new javax.swing.JLabel();
        txtSoDuTK = new javax.swing.JTextField();
        btnXemLichsu = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        pnlThongTin.setBackground(new java.awt.Color(255, 255, 255));
        pnlThongTin.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblthongtg.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblthongtg.setForeground(new Color (16,54,103)  );
        lblthongtg.setText("Tổng thời gian");

        txtTongTG.setEditable(false);
        txtTongTG.setBackground(new java.awt.Color(255, 255, 255));
        txtTongTG.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTongTG.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtTongTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTongTGActionPerformed(evt);
            }
        });

        lblthoigiansudung.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblthoigiansudung.setForeground(new Color (16,54,103)  );
        lblthoigiansudung.setText("Thời gian sử dụng");

        txtTGSuDung.setEditable(false);
        txtTGSuDung.setBackground(new java.awt.Color(255, 255, 255));
        txtTGSuDung.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        lbltgconlai.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbltgconlai.setForeground(new Color (16,54,103)  );
        lbltgconlai.setText("Thời gian còn lại");

        txtTGConLai.setEditable(false);
        txtTGConLai.setBackground(new java.awt.Color(255, 255, 255));
        txtTGConLai.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTGConLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTGConLaiActionPerformed(evt);
            }
        });

        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlThongTinLayout = new javax.swing.GroupLayout(pnlThongTin);
        pnlThongTin.setLayout(pnlThongTinLayout);
        pnlThongTinLayout.setHorizontalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(487, 487, 487)
                .addComponent(btnDelete))
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblthoigiansudung)
                    .addComponent(lblthongtg)
                    .addComponent(lbltgconlai))
                .addGap(18, 18, 18)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTGSuDung)
                    .addComponent(txtTGConLai)
                    .addComponent(txtTongTG, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlThongTinLayout.setVerticalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblthongtg)
                    .addComponent(txtTongTG, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblthoigiansudung)
                    .addComponent(txtTGSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltgconlai)
                    .addComponent(txtTGConLai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnDelete))
        );

        btnChat.setBackground(new Color (24, 71, 133)  );
        btnChat.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnChat.setForeground(new java.awt.Color(255, 255, 255));
        btnChat.setText("Tin nhắn");
        btnChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatActionPerformed(evt);
            }
        });

        btnDangXuat.setBackground(new Color (24, 71, 133)  );
        btnDangXuat.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnDangXuat.setForeground(new java.awt.Color(255, 255, 255));
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });

        btnDoiMK.setBackground(new Color (24, 71, 133)  );
        btnDoiMK.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnDoiMK.setForeground(new java.awt.Color(255, 255, 255));
        btnDoiMK.setText("Mật Khẩu");
        btnDoiMK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoiMKActionPerformed(evt);
            }
        });

        btnAnUong.setBackground(new Color (24, 71, 133)  );
        btnAnUong.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnAnUong.setForeground(new java.awt.Color(255, 255, 255));
        btnAnUong.setText("Ăn, Uống");
        btnAnUong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnUongActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 102));
        jLabel1.setText("CypherCoffeeNet");

        pnlSoTienTK.setBackground(new java.awt.Color(255, 255, 255));
        pnlSoTienTK.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTiennap1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTiennap1.setForeground(new Color (16,54,103)  );
        lblTiennap1.setText("Số dư TK");

        txtSoDuTK.setEditable(false);
        txtSoDuTK.setBackground(new java.awt.Color(255, 255, 255));
        txtSoDuTK.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSoDuTK.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSoDuTK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDuTKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSoTienTKLayout = new javax.swing.GroupLayout(pnlSoTienTK);
        pnlSoTienTK.setLayout(pnlSoTienTKLayout);
        pnlSoTienTKLayout.setHorizontalGroup(
            pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoTienTKLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTiennap1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addComponent(txtSoDuTK, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        pnlSoTienTKLayout.setVerticalGroup(
            pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoTienTKLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTiennap1)
                    .addComponent(txtSoDuTK, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnXemLichsu.setBackground(new Color (24, 71, 133)  );
        btnXemLichsu.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnXemLichsu.setForeground(new java.awt.Color(255, 255, 255));
        btnXemLichsu.setText("Xem lịch sử");
        btnXemLichsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemLichsuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnDangXuat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(pnlSoTienTK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnChat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnDoiMK, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnXemLichsu, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                    .addComponent(btnAnUong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlSoTienTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChat, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnUong, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDoiMK, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXemLichsu, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        btnChat.getAccessibleContext().setAccessibleDescription("");
        btnChat.getAccessibleContext().setAccessibleParent(btnDelete);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTongTGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTongTGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTongTGActionPerformed

    private void txtTGConLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTGConLaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTGConLaiActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChatActionPerformed
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new CustomerChatForm(1, 1, "Máy 01").setVisible(true);
        });
    }//GEN-LAST:event_btnChatActionPerformed

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        if (MessageBoxQLTN.confirm(this, "Bạn có muốn đăng xuất không?")) {
            updateComputerStatus(computerId, "inactive");
            sessionManager.endCustomerSession(computerId, "Đăng xuất");
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                HomeCustomerFrame newFrame = new HomeCustomerFrame();
            });
        }
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void btnDoiMKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiMKActionPerformed
        showChangePasswordDialog();
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Đổi mật khẩu", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Mật khẩu mới
        JLabel lblNewPassword = new JLabel("Mật khẩu mới:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(lblNewPassword, gbc);

        JPasswordField txtNewPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(txtNewPassword, gbc);

        // Nhập lại mật khẩu
        JLabel lblConfirmPassword = new JLabel("Nhập lại mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(lblConfirmPassword, gbc);

        JPasswordField txtConfirmPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(txtConfirmPassword, gbc);

        // Buttons
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        btnOK.addActionListener(e -> {
            String newPassword = new String(txtNewPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());

            if (newPassword.length() < 3) {
                MessageBoxQLTN.alert(dialog, "Mật khẩu phải có ít nhất 3 ký tự!");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                MessageBoxQLTN.alert(dialog, "Mật khẩu nhập lại không khớp!");
                return;
            }

            try {
                // Cập nhật mật khẩu
                CustomerDao customerDao = new CustomerDao();
                loggedInCustomer.setPasswordHash(newPassword);
                customerDao.update(loggedInCustomer);

                MessageBoxQLTN.alert(dialog, "Đổi mật khẩu thành công!");
                dialog.dispose();
            } catch (Exception ex) {
                MessageBoxQLTN.alert(dialog, "Lỗi đổi mật khẩu: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(btnOK, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(btnCancel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnDoiMKActionPerformed

    private void btnAnUongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnUongActionPerformed
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            CustomerOrderJPanel form = new CustomerOrderJPanel(1, "Máy 01");
            form.setVisible(true);
        });
    }//GEN-LAST:event_btnAnUongActionPerformed

    private void txtSoDuTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDuTKActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDuTKActionPerformed

    private void btnXemLichsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemLichsuActionPerformed
        // TODO add your handling code here:
        showUsageHistoryDialog();
    }

    private void showUsageHistoryDialog() {
        JDialog dialog = new JDialog(this, "Lịch sử sử dụng", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        // Tạo bảng hiển thị lịch sử
        String[] columnNames = {"Ngày", "Máy", "Thời gian sử dụng", "Số tiền"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columnNames, 0);
        javax.swing.JTable table = new javax.swing.JTable(model);

        try {
            // Lấy lịch sử từ database
            ResultSet rs = QLTN.Utils.JdbcHelperQLTN.query(
                    "SELECT cu.start_time, cu.end_time, c.name, cu.total_amount "
                    + "FROM ComputerUsage cu "
                    + "JOIN Computer c ON cu.computer_id = c.computer_id "
                    + "WHERE cu.customer_id = ? AND cu.end_time IS NOT NULL "
                    + "ORDER BY cu.start_time DESC",
                    loggedInCustomer.getCustomerId()
            );

            while (rs.next()) {
                String date = rs.getTimestamp("start_time").toString().substring(0, 10);
                String computerName = rs.getString("name");

                // Tính thời gian sử dụng
                java.sql.Timestamp startTime = rs.getTimestamp("start_time");
                java.sql.Timestamp endTime = rs.getTimestamp("end_time");
                long diffInMillies = endTime.getTime() - startTime.getTime();
                long diffInMinutes = diffInMillies / (60 * 1000);
                String duration = diffInMinutes / 60 + " giờ " + diffInMinutes % 60 + " phút";

                String amount = TimeFormatter.formatMoneyVND(rs.getBigDecimal("total_amount"));

                model.addRow(new Object[]{date, computerName, duration, amount});
            }

            rs.getStatement().getConnection().close();

        } catch (Exception e) {
            MessageBoxQLTN.alert(dialog, "Lỗi tải lịch sử: " + e.getMessage());
        }

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }//GEN-LAST:event_btnXemLichsuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeCustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeCustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeCustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeCustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeCustomerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnUong;
    private javax.swing.JButton btnChat;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDoiMK;
    private javax.swing.JButton btnXemLichsu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTiennap1;
    private javax.swing.JLabel lbltgconlai;
    private javax.swing.JLabel lblthoigiansudung;
    private javax.swing.JLabel lblthongtg;
    private javax.swing.JPanel pnlSoTienTK;
    private javax.swing.JPanel pnlThongTin;
    private javax.swing.JTextField txtSoDuTK;
    private javax.swing.JTextField txtTGConLai;
    private javax.swing.JTextField txtTGSuDung;
    private javax.swing.JTextField txtTongTG;
    // End of variables declaration//GEN-END:variables

    private void openLoginDialog() {
        new LoginCustomerJDialog(this, true).setVisible(true);
    }
}
