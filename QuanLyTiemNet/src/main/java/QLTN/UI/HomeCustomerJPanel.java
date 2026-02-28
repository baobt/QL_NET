/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package QLTN.UI;

import QLTN.Utils.JdbcHelperQLTN;
import QLTN.Utils.MessageBoxQLTN;
import QLTN.Utils.SessionInfo;
import QLTN.Utils.SessionManager;
import QLTN.Utils.TimeFormatter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author AD
 */
public class HomeCustomerJPanel extends JPanel implements SessionManager.SessionEndListener {

    private int computerId;
    private SessionManager sessionManager;
    private Timer updateTimer;
    private boolean fiveMinuteWarningShown = false;
    private boolean timeUpDialogShown = false;

    public HomeCustomerJPanel() {
        this(-1);
    }

    public HomeCustomerJPanel(int computerId) {
        this.computerId = computerId;
        this.sessionManager = SessionManager.getInstance();
        initComponents();
        initializeSession();
        sessionManager.addSessionEndListener(computerId, this);
    }

//    Implement method từ SessionEndListener
    @Override
    public void onSessionEnd(String reason) {
        SwingUtilities.invokeLater(() -> {
            if (updateTimer != null) {
                updateTimer.stop();
            }
            showSessionEndDialog(reason);
        });
    }

    private void showSessionEndDialog(String reason) {
        JDialog sessionEndDialog = new JDialog();
        sessionEndDialog.setTitle("Kết thúc phiên sử dụng");
        sessionEndDialog.setModal(true);
        sessionEndDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        sessionEndDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();

        // Label thông báo
        JLabel messageLabel = new JLabel("Kết thúc phiên sử dụng");
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        messageLabel.setForeground(new Color(16, 54, 103));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(messageLabel, gbc);

        // Label lý do
        JLabel reasonLabel = new JLabel("Lý do: " + reason);
        reasonLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        reasonLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(reasonLabel, gbc);

        // Nút OK
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        okButton.setBackground(new Color(24, 71, 133));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new java.awt.Dimension(100, 40));
        okButton.addActionListener(e -> {
            sessionEndDialog.dispose();
            closeCustomerPanel();
        });

        gbc.gridy = 2;
        mainPanel.add(okButton, gbc);

        sessionEndDialog.add(mainPanel, BorderLayout.CENTER);
        sessionEndDialog.pack();
        sessionEndDialog.setLocationRelativeTo(this);
        sessionEndDialog.setVisible(true);
    }

    private void initializeSession() {
        if (computerId > 0) {
            SessionInfo session = sessionManager.getSession(computerId);
            if (session != null) {
                updateDisplay();
            }
        }
    }

    public void startTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }

        updateTimer = new Timer(1000, new ActionListener() { // Cập nhật mỗi giây
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSession();
                updateDisplay();
                checkWarnings();
            }
        });
        updateTimer.start();
    }

    private void updateSession() {
        SessionInfo session = sessionManager.getSession(computerId);
        if (session != null) {
            // Tính thời gian đã sử dụng
            LocalDateTime startTime = session.getStartTime();
            LocalDateTime now = LocalDateTime.now();
            long usedSeconds = ChronoUnit.SECONDS.between(startTime, now);

            session.setUsedTimeSeconds((int) usedSeconds);

            // Tính thời gian còn lại
            int remainingSeconds = session.getTotalTimeSeconds() - (int) usedSeconds;
            remainingSeconds = Math.max(0, remainingSeconds); // Không để âm

            session.setRemainingTimeSeconds(remainingSeconds);
        }
    }

    private void updateDisplay() {
        SessionInfo session = sessionManager.getSession(computerId);
        if (session != null) {
            // Cập nhật các trường hiển thị
            txtTongTG.setText(TimeFormatter.formatTime(session.getTotalTimeSeconds()));
            txtTGSuDung.setText(TimeFormatter.formatTime(session.getUsedTimeSeconds()));
            txtTGConLai.setText(TimeFormatter.formatTime(session.getRemainingTimeSeconds()));
            txtTienNap.setText(TimeFormatter.formatMoneyVND(session.getPrepaidAmount()));
            txtSoDuTK.setText(TimeFormatter.formatMoneyVND(session.getCurrentBalance()));
        }
    }

    private void checkWarnings() {
        SessionInfo session = sessionManager.getSession(computerId);
        if (session != null) {
            int remainingSeconds = session.getRemainingTimeSeconds();

            // Thông báo trước 5 phút (300 giây)
            if (remainingSeconds <= 300 && remainingSeconds > 0 && !fiveMinuteWarningShown) {
                fiveMinuteWarningShown = true;
                showFiveMinuteWarning();
            }

            // Thông báo hết giờ
            if (remainingSeconds <= 0 && !timeUpDialogShown) {
                timeUpDialogShown = true;
                showTimeUpDialog();
            }
        }
    }

    private void showFiveMinuteWarning() {
        SwingUtilities.invokeLater(() -> {
            // Tạo thông báo ở góc phải dưới màn hình
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

            // Tự động đóng sau 5 giây
            Timer closeTimer = new Timer(5000, e -> warningDialog.dispose());
            closeTimer.setRepeats(false);
            closeTimer.start();
        });
    }

    private void showTimeUpDialog() {
        SwingUtilities.invokeLater(() -> {
            if (updateTimer != null) {
                updateTimer.stop();
            }

            JDialog timeUpDialog = new JDialog();
            timeUpDialog.setTitle("Hết giờ");
            timeUpDialog.setModal(true);
            timeUpDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            timeUpDialog.setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            GridBagConstraints gbc = new GridBagConstraints();

            // Label thông báo
            JLabel messageLabel = new JLabel("Hết giờ sử dụng");
            messageLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
            messageLabel.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 20, 0);
            mainPanel.add(messageLabel, gbc);

            // Nút tắt máy
            JButton shutdownButton = new JButton("Tắt máy");
            shutdownButton.setFont(new Font("Tahoma", Font.BOLD, 16));
            shutdownButton.setBackground(Color.RED);
            shutdownButton.setForeground(Color.WHITE);
            shutdownButton.setPreferredSize(new java.awt.Dimension(120, 40));
            shutdownButton.addActionListener(e -> {
                timeUpDialog.dispose();

                // Cập nhật trạng thái máy thành inactive khi hết giờ
                try {
                    // Cập nhật end_time cho phiên sử dụng
                    JdbcHelperQLTN.update("UPDATE ComputerUsage SET end_time = CURRENT_TIMESTAMP WHERE computer_id = ? AND end_time IS NULL", computerId);
                    // Cập nhật trạng thái máy thành inactive
                    JdbcHelperQLTN.update("UPDATE Computer SET status = 'inactive' WHERE computer_id = ?", computerId);
                } catch (Exception ex) {
                    System.err.println("Lỗi cập nhật trạng thái máy: " + ex.getMessage());
                }

                closeCustomerPanel();
            });

            gbc.gridy = 1;
            mainPanel.add(shutdownButton, gbc);

            timeUpDialog.add(mainPanel, BorderLayout.CENTER);
            timeUpDialog.pack();
            timeUpDialog.setLocationRelativeTo(this);
            timeUpDialog.setVisible(true);
        });
    }

    private void closeCustomerPanel() {
        sessionManager.removeSessionEndListener(computerId, this);
        sessionManager.removeSession(computerId);
        SwingUtilities.getWindowAncestor(this).dispose();
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlThongTin = new javax.swing.JPanel();
        lblthongtg = new javax.swing.JLabel();
        txtTongTG = new javax.swing.JTextField();
        lblthoigiansudung = new javax.swing.JLabel();
        txtTGSuDung = new javax.swing.JTextField();
        lbltgconlai = new javax.swing.JLabel();
        txtTGConLai = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        pnlSoTienTK = new javax.swing.JPanel();
        lblTiennap = new javax.swing.JLabel();
        txtTienNap = new javax.swing.JTextField();
        lblTiennap1 = new javax.swing.JLabel();
        txtSoDuTK = new javax.swing.JTextField();
        btnChat = new javax.swing.JButton();
        btnAnUong = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 255, 255));
        setAutoscrolls(true);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 102));
        jLabel1.setText("CypherCoffeeNet");

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
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtTGSuDung, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTGConLai)
                    .addComponent(txtTongTG, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlThongTinLayout.setVerticalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(19, 19, 19)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDelete))
        );

        pnlSoTienTK.setBackground(new java.awt.Color(255, 255, 255));
        pnlSoTienTK.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTiennap.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTiennap.setForeground(new Color (16,54,103)  );
        lblTiennap.setText("Tiền nạp");

        txtTienNap.setEditable(false);
        txtTienNap.setBackground(new java.awt.Color(255, 255, 255));
        txtTienNap.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTienNap.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtTienNap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTienNapActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSoTienTKLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTiennap)
                    .addComponent(lblTiennap1))
                .addGap(26, 26, 26)
                .addGroup(pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTienNap, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(txtSoDuTK))
                .addContainerGap())
        );
        pnlSoTienTKLayout.setVerticalGroup(
            pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSoTienTKLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTienNap, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTiennap))
                .addGap(18, 18, 18)
                .addGroup(pnlSoTienTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoDuTK, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTiennap1))
                .addContainerGap())
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

        btnAnUong.setBackground(new Color (24, 71, 133)  );
        btnAnUong.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnAnUong.setForeground(new java.awt.Color(255, 255, 255));
        btnAnUong.setText("Ăn, Uống");
        btnAnUong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnUongActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnlThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlSoTienTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnChat, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(btnAnUong, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel1)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSoTienTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChat, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnUong, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTongTGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTongTGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTongTGActionPerformed

    private void txtTGConLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTGConLaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTGConLaiActionPerformed

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

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAnUongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnUongActionPerformed
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            // For testing, use a dummy usage ID and computer name
            CustomerOrderJPanel form = new CustomerOrderJPanel(1, "Máy 01");
            form.setVisible(true);
        });
    }//GEN-LAST:event_btnAnUongActionPerformed

    private void txtTienNapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTienNapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTienNapActionPerformed

    private void txtSoDuTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDuTKActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDuTKActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnUong;
    private javax.swing.JButton btnChat;
    private javax.swing.JButton btnDelete;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTiennap;
    private javax.swing.JLabel lblTiennap1;
    private javax.swing.JLabel lbltgconlai;
    private javax.swing.JLabel lblthoigiansudung;
    private javax.swing.JLabel lblthongtg;
    private javax.swing.JPanel pnlSoTienTK;
    private javax.swing.JPanel pnlThongTin;
    private javax.swing.JTextField txtSoDuTK;
    private javax.swing.JTextField txtTGConLai;
    private javax.swing.JTextField txtTGSuDung;
    private javax.swing.JTextField txtTienNap;
    private javax.swing.JTextField txtTongTG;
    // End of variables declaration//GEN-END:variables

}
