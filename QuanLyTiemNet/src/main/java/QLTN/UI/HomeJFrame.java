/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package QLTN.UI;

import QLTN.Utils.AuthRoleQLTN;
import com.formdev.flatlaf.FlatLightLaf;

import QLTN.Utils.Masterfrom;
import QLTN.Utils.UndoRedo;
import QLTN.Utils.Thread_Menu;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import static java.awt.Font.BOLD;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import sun.net.www.content.text.plain;

/**
 *
 * @author AD
 */
public class HomeJFrame extends javax.swing.JFrame {

    private final UndoRedo<Masterfrom> forms = new UndoRedo<>();

    public void showForm(Masterfrom form) {
        forms.add(form);
        pnlView.removeAll();
        pnlView.add(form);
        pnlView.revalidate();
        pnlView.repaint();
    }

    public HomeJFrame() {
        initComponents();
        setLocationRelativeTo(null);
        init();
        openComputerActivity();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBackgroudAll = new javax.swing.JPanel();
        pnlContent = new javax.swing.JPanel();
        pnlView = new javax.swing.JPanel();
        pnlListMenuIcon = new javax.swing.JPanel();
        pnlListMenu = new javax.swing.JPanel();
        btnHoatdongMay = new javax.swing.JPanel();
        lblBanhang = new javax.swing.JLabel();
        ind2 = new javax.swing.JPanel();
        btnMenu = new javax.swing.JPanel();
        lblSanpham = new javax.swing.JLabel();
        ind3 = new javax.swing.JPanel();
        btnKhachHang = new javax.swing.JPanel();
        lblKhachhang = new javax.swing.JLabel();
        ind4 = new javax.swing.JPanel();
        btnQuanLyMay = new javax.swing.JPanel();
        lblKhuyenmai = new javax.swing.JLabel();
        ind5 = new javax.swing.JPanel();
        btnNhanVien = new javax.swing.JPanel();
        lblNhanvien = new javax.swing.JLabel();
        ind7 = new javax.swing.JPanel();
        btnDangxuat = new javax.swing.JPanel();
        lblTrangchu1 = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        lblTen = new javax.swing.JLabel();
        pnlHeader = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBackgroudAll.setBackground(new java.awt.Color(255, 255, 255));
        pnlBackgroudAll.setPreferredSize(new java.awt.Dimension(1535, 860));
        pnlBackgroudAll.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlContent.setBackground(new java.awt.Color(255, 255, 255));
        pnlContent.setPreferredSize(new java.awt.Dimension(1317, 760));

        pnlView.setBackground(new java.awt.Color(255, 255, 255));
        pnlView.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContentLayout.createSequentialGroup()
                .addComponent(pnlView, javax.swing.GroupLayout.DEFAULT_SIZE, 1300, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
        );

        pnlBackgroudAll.add(pnlContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(217, 30, 1310, 780));

        pnlListMenuIcon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlListMenu.setBackground(new java.awt.Color(20, 20, 82));
        pnlListMenu.setPreferredSize(new java.awt.Dimension(218, 800));
        pnlListMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnHoatdongMay.setBackground(new java.awt.Color(20, 37, 100));
        btnHoatdongMay.setPreferredSize(new java.awt.Dimension(190, 65));
        btnHoatdongMay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHoatdongMayMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHoatdongMayMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnHoatdongMayMousePressed(evt);
            }
        });

        lblBanhang.setBackground(new Color(0,0,0,0));
        lblBanhang.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblBanhang.setForeground(new java.awt.Color(255, 255, 255));
        lblBanhang.setText("Hoạt động Máy");
        lblBanhang.setIconTextGap(20);

        ind2.setPreferredSize(new java.awt.Dimension(5, 0));

        javax.swing.GroupLayout ind2Layout = new javax.swing.GroupLayout(ind2);
        ind2.setLayout(ind2Layout);
        ind2Layout.setHorizontalGroup(
            ind2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        ind2Layout.setVerticalGroup(
            ind2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnHoatdongMayLayout = new javax.swing.GroupLayout(btnHoatdongMay);
        btnHoatdongMay.setLayout(btnHoatdongMayLayout);
        btnHoatdongMayLayout.setHorizontalGroup(
            btnHoatdongMayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnHoatdongMayLayout.createSequentialGroup()
                .addComponent(ind2, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblBanhang, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        btnHoatdongMayLayout.setVerticalGroup(
            btnHoatdongMayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ind2, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
            .addComponent(lblBanhang, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnHoatdongMay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 220, -1));

        btnMenu.setBackground(new java.awt.Color(20, 37, 100));
        btnMenu.setPreferredSize(new java.awt.Dimension(190, 65));
        btnMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMenuMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnMenuMousePressed(evt);
            }
        });

        lblSanpham.setBackground(new Color(0,0,0,0));
        lblSanpham.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblSanpham.setForeground(new java.awt.Color(255, 255, 255));
        lblSanpham.setText("Quản lý Menu");
        lblSanpham.setIconTextGap(20);

        ind3.setPreferredSize(new java.awt.Dimension(5, 0));

        javax.swing.GroupLayout ind3Layout = new javax.swing.GroupLayout(ind3);
        ind3.setLayout(ind3Layout);
        ind3Layout.setHorizontalGroup(
            ind3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        ind3Layout.setVerticalGroup(
            ind3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnMenuLayout = new javax.swing.GroupLayout(btnMenu);
        btnMenu.setLayout(btnMenuLayout);
        btnMenuLayout.setHorizontalGroup(
            btnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnMenuLayout.createSequentialGroup()
                .addComponent(ind3, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblSanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        btnMenuLayout.setVerticalGroup(
            btnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ind3, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
            .addComponent(lblSanpham, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 218, -1));

        btnKhachHang.setBackground(new java.awt.Color(20, 37, 100));
        btnKhachHang.setPreferredSize(new java.awt.Dimension(190, 65));
        btnKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKhachHangMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKhachHangMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnKhachHangMousePressed(evt);
            }
        });

        lblKhachhang.setBackground(new Color(0,0,0,0));
        lblKhachhang.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblKhachhang.setForeground(new java.awt.Color(255, 255, 255));
        lblKhachhang.setText("Quản lý Khách hàng");
        lblKhachhang.setIconTextGap(20);

        ind4.setPreferredSize(new java.awt.Dimension(5, 0));

        javax.swing.GroupLayout ind4Layout = new javax.swing.GroupLayout(ind4);
        ind4.setLayout(ind4Layout);
        ind4Layout.setHorizontalGroup(
            ind4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        ind4Layout.setVerticalGroup(
            ind4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnKhachHangLayout = new javax.swing.GroupLayout(btnKhachHang);
        btnKhachHang.setLayout(btnKhachHangLayout);
        btnKhachHangLayout.setHorizontalGroup(
            btnKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnKhachHangLayout.createSequentialGroup()
                .addComponent(ind4, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblKhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        btnKhachHangLayout.setVerticalGroup(
            btnKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ind4, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
            .addComponent(lblKhachhang, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 218, -1));

        btnQuanLyMay.setBackground(new java.awt.Color(20, 37, 100));
        btnQuanLyMay.setPreferredSize(new java.awt.Dimension(190, 65));
        btnQuanLyMay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnQuanLyMayMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnQuanLyMayMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnQuanLyMayMousePressed(evt);
            }
        });

        lblKhuyenmai.setBackground(new Color(0,0,0,0));
        lblKhuyenmai.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblKhuyenmai.setForeground(new java.awt.Color(255, 255, 255));
        lblKhuyenmai.setText("Quản lý Máy");
        lblKhuyenmai.setIconTextGap(20);

        ind5.setPreferredSize(new java.awt.Dimension(5, 0));

        javax.swing.GroupLayout ind5Layout = new javax.swing.GroupLayout(ind5);
        ind5.setLayout(ind5Layout);
        ind5Layout.setHorizontalGroup(
            ind5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        ind5Layout.setVerticalGroup(
            ind5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnQuanLyMayLayout = new javax.swing.GroupLayout(btnQuanLyMay);
        btnQuanLyMay.setLayout(btnQuanLyMayLayout);
        btnQuanLyMayLayout.setHorizontalGroup(
            btnQuanLyMayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnQuanLyMayLayout.createSequentialGroup()
                .addComponent(ind5, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblKhuyenmai, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        btnQuanLyMayLayout.setVerticalGroup(
            btnQuanLyMayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ind5, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
            .addComponent(lblKhuyenmai, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnQuanLyMay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 218, -1));

        btnNhanVien.setBackground(new java.awt.Color(20, 37, 100));
        btnNhanVien.setPreferredSize(new java.awt.Dimension(190, 65));
        btnNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnNhanVienMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnNhanVienMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnNhanVienMousePressed(evt);
            }
        });

        lblNhanvien.setBackground(new Color(0,0,0,0));
        lblNhanvien.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblNhanvien.setForeground(new java.awt.Color(255, 255, 255));
        lblNhanvien.setText("Nhân viên");
        lblNhanvien.setIconTextGap(20);

        ind7.setPreferredSize(new java.awt.Dimension(5, 0));

        javax.swing.GroupLayout ind7Layout = new javax.swing.GroupLayout(ind7);
        ind7.setLayout(ind7Layout);
        ind7Layout.setHorizontalGroup(
            ind7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        ind7Layout.setVerticalGroup(
            ind7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout btnNhanVienLayout = new javax.swing.GroupLayout(btnNhanVien);
        btnNhanVien.setLayout(btnNhanVienLayout);
        btnNhanVienLayout.setHorizontalGroup(
            btnNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnNhanVienLayout.createSequentialGroup()
                .addComponent(ind7, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblNhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        btnNhanVienLayout.setVerticalGroup(
            btnNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ind7, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
            .addComponent(lblNhanvien, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 210, -1));

        btnDangxuat.setBackground(new java.awt.Color(20, 37, 100));
        btnDangxuat.setPreferredSize(new java.awt.Dimension(190, 65));
        btnDangxuat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDangxuatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDangxuatMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnDangxuatMousePressed(evt);
            }
        });

        lblTrangchu1.setBackground(new Color(0,0,0,0));
        lblTrangchu1.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        lblTrangchu1.setForeground(new java.awt.Color(255, 255, 255));
        lblTrangchu1.setText("Đăng xuất");
        lblTrangchu1.setIconTextGap(20);

        javax.swing.GroupLayout btnDangxuatLayout = new javax.swing.GroupLayout(btnDangxuat);
        btnDangxuat.setLayout(btnDangxuatLayout);
        btnDangxuatLayout.setHorizontalGroup(
            btnDangxuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnDangxuatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrangchu1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btnDangxuatLayout.setVerticalGroup(
            btnDangxuatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTrangchu1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        pnlListMenu.add(btnDangxuat, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 700, 110, 40));

        lblRole.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        pnlListMenu.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 150, 40));

        lblTen.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblTen.setForeground(new java.awt.Color(255, 255, 255));
        lblTen.setText("Ten");
        pnlListMenu.add(lblTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 170, 40));

        pnlListMenuIcon.add(pnlListMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 220, 810));

        pnlBackgroudAll.add(pnlListMenuIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 218, 860));

        pnlHeader.setBackground(new java.awt.Color(204, 204, 204));
        pnlHeader.setPreferredSize(new java.awt.Dimension(1317, 50));
        pnlHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlHeaderMouseDragged(evt);
            }
        });
        pnlHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlHeaderMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1330, Short.MAX_VALUE)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        pnlBackgroudAll.add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 1330, 20));

        getContentPane().add(pnlBackgroudAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 800));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHoatdongMayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHoatdongMayMousePressed
        openComputerActivity();
    }//GEN-LAST:event_btnHoatdongMayMousePressed

    private void btnMenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMousePressed

        setColor(btnMenu);
        ind3.setOpaque(true);
        resetColor(new JPanel[]{btnHoatdongMay, btnQuanLyMay, btnKhachHang, btnNhanVien},
                new JPanel[]{ind2, ind4, ind5, ind7});
        EventQueue.invokeLater(() -> {
            try {
                ServiceJPanel frame = new ServiceJPanel();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }//GEN-LAST:event_btnMenuMousePressed

    private void btnKhachHangMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMousePressed
        openKhachHang();
    }//GEN-LAST:event_btnKhachHangMousePressed

    private void btnQuanLyMayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMayMousePressed

        openMayTinh();
    }//GEN-LAST:event_btnQuanLyMayMousePressed

    private void btnNhanVienMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMousePressed
        if (!QLTN.Utils.AuthRoleQLTN.isManager()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập chức năng này.");
            return;
        }
        setColor(btnNhanVien);
        ind7.setOpaque(true);
        resetColor(new JPanel[]{btnHoatdongMay, btnMenu, btnKhachHang, btnQuanLyMay},
                new JPanel[]{ind2, ind4, ind3, ind5});
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Nhân Viên");
            frame.setSize(1300, 750);
            frame.setLocationRelativeTo(null);
            NhanVienNetJPanel nhanVienPanel = new NhanVienNetJPanel();
            frame.getContentPane().add(nhanVienPanel);
            frame.setVisible(true);
        });
    }//GEN-LAST:event_btnNhanVienMousePressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    int x = 218;
    int a = 0;
    int xx, xy;
    private void btnHoatdongMayMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHoatdongMayMouseEntered
        setColor(btnHoatdongMay);
    }//GEN-LAST:event_btnHoatdongMayMouseEntered

    private void btnHoatdongMayMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHoatdongMayMouseExited
        setColor2(btnHoatdongMay);
    }//GEN-LAST:event_btnHoatdongMayMouseExited

    private void btnMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseEntered
        setColor(btnMenu);
    }//GEN-LAST:event_btnMenuMouseEntered

    private void btnMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseExited
        setColor2(btnMenu);
    }//GEN-LAST:event_btnMenuMouseExited

    private void btnKhachHangMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMouseEntered
        setColor(btnKhachHang);
    }//GEN-LAST:event_btnKhachHangMouseEntered

    private void btnKhachHangMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKhachHangMouseExited
        setColor2(btnKhachHang);
    }//GEN-LAST:event_btnKhachHangMouseExited

    private void btnQuanLyMayMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMayMouseEntered
        setColor(btnQuanLyMay);
    }//GEN-LAST:event_btnQuanLyMayMouseEntered

    private void btnQuanLyMayMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuanLyMayMouseExited
        setColor2(btnQuanLyMay);
    }//GEN-LAST:event_btnQuanLyMayMouseExited

    private void btnNhanVienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMouseEntered
        setColor(btnNhanVien);
    }//GEN-LAST:event_btnNhanVienMouseEntered

    private void btnNhanVienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienMouseExited
        setColor2(btnNhanVien);
    }//GEN-LAST:event_btnNhanVienMouseExited

    private void btnDangxuatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDangxuatMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDangxuatMouseEntered

    private void btnDangxuatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDangxuatMouseExited
        // TODO add your handling code here:

    }//GEN-LAST:event_btnDangxuatMouseExited

    private void btnDangxuatMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDangxuatMousePressed
        // TODO add your handling code here:
        System.exit(0);
        openDangXuat();
    }//GEN-LAST:event_btnDangxuatMousePressed

    private void pnlHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - xy);
    }//GEN-LAST:event_pnlHeaderMouseDragged

    private void pnlHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMousePressed
        // TODO add your handling code here:
        xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_pnlHeaderMousePressed

    public static void main(String args[]) {

        FlatLightLaf.setup();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeJFrame().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnDangxuat;
    private javax.swing.JPanel btnHoatdongMay;
    private javax.swing.JPanel btnKhachHang;
    private javax.swing.JPanel btnMenu;
    private javax.swing.JPanel btnNhanVien;
    private javax.swing.JPanel btnQuanLyMay;
    private javax.swing.JPanel ind2;
    private javax.swing.JPanel ind3;
    private javax.swing.JPanel ind4;
    private javax.swing.JPanel ind5;
    private javax.swing.JPanel ind7;
    private javax.swing.JLabel lblBanhang;
    private javax.swing.JLabel lblKhachhang;
    private javax.swing.JLabel lblKhuyenmai;
    private javax.swing.JLabel lblNhanvien;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSanpham;
    private javax.swing.JLabel lblTen;
    private javax.swing.JLabel lblTrangchu1;
    private javax.swing.JPanel pnlBackgroudAll;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlListMenu;
    private javax.swing.JPanel pnlListMenuIcon;
    private javax.swing.JPanel pnlView;
    // End of variables declaration//GEN-END:variables

    private void init() {
        this.setLocationRelativeTo(null);
        openLoginDialog();

        if (!AuthRoleQLTN.isManager()) {
            btnNhanVien.setVisible(false);
        }

        if (AuthRoleQLTN.user != null) {
            String role = AuthRoleQLTN.user.getRole();
            String roleLabel = role.equalsIgnoreCase("admin") ? "Quản lý" : "Nhân viên";
            String welcome = roleLabel;
            String ten = AuthRoleQLTN.user.getFullName();

            lblRole.setText(welcome);
            lblRole.setToolTipText(welcome);
            lblTen.setText(ten);
        }
    }

    private void setColor(JPanel pane) {
        pane.setBackground(new Color(20, 37, 100));
    }

    private void setColor2(JPanel pane) {
        pane.setBackground(new Color(20, 20, 82));
    }

    private void resetColor(JPanel[] pane, JPanel[] indicators) {
        for (int i = 0; i < pane.length; i++) {
            pane[i].setBackground(new Color(20, 20, 82));
        }
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setOpaque(false);
        }

    }

    private BufferedImage img;

    int z = 219;
    int y = 845;
    int t = 100;
    int i = 700;

    public void paint(Graphics gp) {
        super.paint(gp);
        Graphics2D g2d = (Graphics2D) gp;

        g2d.setColor(new Color(16, 78, 139));
        g2d.setFont(new Font("ALLBITS", ALLBITS, 15));
        g2d.drawString("", z, y);
        try {
            Thread.sleep(80);
            z += 2;
            if (z > getWidth()) {
                z = 219;
            }
            repaint();
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    private void openDangXuat() {
//        AuthRoleQLTN.clear();
        openLoginDialog();
        this.dispose();
    }

    private void openLoginDialog() {
        new LoginJDialog(this, true).setVisible(true);
    }

    private void openComputerActivity() {
        setColor(btnHoatdongMay);
        ind2.setOpaque(true);
        resetColor(new JPanel[]{btnQuanLyMay, btnMenu, btnKhachHang, btnNhanVien},
                new JPanel[]{ind3, ind4, ind5, ind7});
        showForm(new ComputersActivityJPanel());
    }

    private void openMayTinh() {
        setColor(btnQuanLyMay);
        ind5.setOpaque(true);
        resetColor(new JPanel[]{btnHoatdongMay, btnMenu, btnKhachHang, btnNhanVien},
                new JPanel[]{ind2, ind4, ind3, ind7});
        showForm(new ComputersJPanel());
    }

    private void openKhachHang() {
        setColor(btnKhachHang);
        ind4.setOpaque(true);
        resetColor(new JPanel[]{btnQuanLyMay, btnMenu, btnHoatdongMay, btnNhanVien},
                new JPanel[]{ind2, ind3, ind5, ind7});
        showForm(new CustomerJPanel());
    }

}
