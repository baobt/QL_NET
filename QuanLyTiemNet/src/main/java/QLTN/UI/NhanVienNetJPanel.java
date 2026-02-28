package QLTN.UI;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
import QLTN.Dao.EmployeeDAO;
import QLTN.Entity.Employee;
import QLTN.Utils.Masterfrom;
import java.awt.Color;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class NhanVienNetJPanel extends Masterfrom {

    private EmployeeDAO dao = new EmployeeDAO();
    private DefaultTableModel tblModel;
    private int row = -1;
    private javax.swing.JComboBox<String> cboTrangThai;

    public NhanVienNetJPanel() {
        initComponents();
        init();
        tblNhanVien.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 14));
        tblNhanVien.getTableHeader().setBackground(new Color(24, 71, 133));
        tblNhanVien.getTableHeader().setForeground(new Color(255, 255, 255));
    }

    private void init() {
        ButtonGroup group = new ButtonGroup();
        group.add(rdoQuanLy);
        group.add(rdoNhanVien);

        tblModel = (DefaultTableModel) tblNhanVien.getModel();
        load();
    }

    private void load() {
        tblModel.setRowCount(0);
        tblModel.setColumnIdentifiers(new Object[]{"MaNV", "Tên Nhân Viên", "Số điện thoại", "Mật khẩu", "Chức vụ", "Username", "Trạng thái"});
        try {
            List<Employee> list = dao.getAllEmployees();
            for (Employee nv : list) {
                Object[] rowData = {
                    nv.getEmployeeId(),
                    nv.getFullName(),
                    nv.getPhone(),
                    "********",
                    nv.getRole(),
                    nv.getUsername(),
                    nv.getStatus()
                };
                tblModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu nhân viên!");
            e.printStackTrace();
        }
    }

    private void insert() {
        Employee nv = getForm();
        String password = new String(txtMatKhau.getText());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
            return;
        }
        try {
            dao.insert(nv, password);
            this.load();
            this.clearForm();
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm nhân viên!");
            e.printStackTrace();
        }
    }

    private void update() {
        Employee nv = getForm();
        try {
            dao.update(nv);
            this.load();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật nhân viên!");
            e.printStackTrace();
        }
    }

    private void update2() {
        Employee nv = getFormUD();
        String password = new String(txtMatKhau.getText());
        try {
            dao.update2(nv, password);
            this.load();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật nhân viên!");
            e.printStackTrace();
        }
    }

    private void delete() {
        if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtMaNV.getText());
                dao.delete(id);
                this.load();
                this.clearForm();
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa nhân viên!");
                e.printStackTrace();
            }
        }
    }

    private void edit() {
        try {
            int id = (int) tblNhanVien.getValueAt(this.row, 0);
            Employee nv = dao.getEmployeeById(id);
            this.setForm(nv);
            txtMatKhau.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi hiển thị thông tin nhân viên!");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        txtSDT.setText("");
        txtMatKhau.setText("");
        txtUsername.setText("");
        rdoNhanVien.setSelected(false);
        rdoQuanLy.setSelected(false);
        cboTrangThai.setSelectedIndex(0); // Reset về trạng thái mặc định (ví dụ: "active")
        this.row = -1;
    }

    private void setForm(Employee nv) {
        txtMaNV.setText(String.valueOf(nv.getEmployeeId()));
        txtTenNV.setText(nv.getFullName());
        txtSDT.setText(nv.getPhone());
        txtUsername.setText(nv.getUsername());
        if (nv.getRole().equalsIgnoreCase("admin")) {
            rdoQuanLy.setSelected(true);
        } else {
            rdoNhanVien.setSelected(true);
        }
        cboTrangThai.setSelectedItem(nv.getStatus());
    }

    private Employee getForm() {
        Employee nv = new Employee();
        if (!txtMaNV.getText().isEmpty()) {
            nv.setEmployeeId(Integer.parseInt(txtMaNV.getText()));
        }
        nv.setFullName(txtTenNV.getText());
        nv.setPhone(txtSDT.getText());
        nv.setUsername(txtUsername.getText());
        if (rdoQuanLy.isSelected()) {
            nv.setRole("admin");
        } else {
            nv.setRole("reception");
        }
        nv.setStatus((String) cboTrangThai.getSelectedItem());
        return nv;
    }

    private Employee getFormUD() {
        Employee nv = new Employee();
        if (!txtMaNV.getText().isEmpty()) {
            nv.setEmployeeId(Integer.parseInt(txtMaNV.getText()));
        }
        nv.setPasswordHash(txtMatKhau.getText());
        nv.setFullName(txtTenNV.getText());
        nv.setPhone(txtSDT.getText());
        nv.setUsername(txtUsername.getText());
        if (rdoQuanLy.isSelected()) {
            nv.setRole("admin");
        } else {
            nv.setRole("reception");
        }
        nv.setStatus((String) cboTrangThai.getSelectedItem());
        return nv;
    }

    private void first() {
        this.row = 0;
        this.edit();
        tblNhanVien.setRowSelectionInterval(this.row, this.row);
    }

    private void prev() {
        if (this.row > 0) {
            this.row--;
            this.edit();
            tblNhanVien.setRowSelectionInterval(this.row, this.row);
        }
    }

    private void next() {
        if (this.row < tblNhanVien.getRowCount() - 1) {
            this.row++;
            this.edit();
            tblNhanVien.setRowSelectionInterval(this.row, this.row);
        }
    }

    private void last() {
        this.row = tblNhanVien.getRowCount() - 1;
        this.edit();
        tblNhanVien.setRowSelectionInterval(this.row, this.row);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        pnlChitietNV = new javax.swing.JPanel();
        lblMaNV = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        lblTenNV = new javax.swing.JLabel();
        txtTenNV = new javax.swing.JTextField();
        lblSoDT = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        lblMatKhau = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        lblChucvu = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        txtUsername = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnDeleteNV = new javax.swing.JButton();
        lblTrangThai = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        pnlDanhSachNV = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();

        pnlChitietNV.setBackground(new java.awt.Color(255, 255, 255));
        pnlChitietNV.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMaNV.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblMaNV.setForeground(new Color(16, 54, 103));
        lblMaNV.setText("Mã nhân viên");

        txtMaNV.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMaNV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaNVActionPerformed(evt);
            }
        });

        lblTenNV.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTenNV.setForeground(new Color(16, 54, 103));
        lblTenNV.setText("Tên nhân viên");

        txtTenNV.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        lblSoDT.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblSoDT.setForeground(new Color(16, 54, 103));
        lblSoDT.setText("Số điện thoại");

        txtSDT.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSDT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSDTActionPerformed(evt);
            }
        });

        lblMatKhau.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblMatKhau.setForeground(new Color(16, 54, 103));
        lblMatKhau.setText("Mật khẩu");

        txtMatKhau.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        lblChucvu.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblChucvu.setForeground(new Color(16, 54, 103));
        lblChucvu.setText("Chức vụ");

        lblUsername.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblUsername.setForeground(new Color(16, 54, 103));
        lblUsername.setText("Tên đăng nhập");

        rdoQuanLy.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rdoQuanLy.setForeground(new java.awt.Color(102, 0, 51));
        rdoQuanLy.setText("Quản lý");
        rdoQuanLy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoQuanLyActionPerformed(evt);
            }
        });

        rdoNhanVien.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rdoNhanVien.setForeground(new java.awt.Color(102, 0, 51));
        rdoNhanVien.setText("Nhân viên");
        rdoNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNhanVienActionPerformed(evt);
            }
        });

        txtUsername.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        btnClear.setBackground(new Color(239, 239, 239));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnThem.setBackground(new Color(253, 251, 251));
        btnThem.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnThem.setForeground(new java.awt.Color(0, 0, 0));
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new Color(253, 253, 253));
        btnUpdate.setText("Sửa");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new Color(255, 255, 255));
        btnDelete.setForeground(new java.awt.Color(0, 0, 0));
        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnDeleteNV.setBackground(new Color(248, 248, 248));
        btnDeleteNV.setText("Xóa NV");
        btnDeleteNV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteNVActionPerformed(evt);
            }
        });

        lblTrangThai.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTrangThai.setForeground(new Color(16, 54, 103));
        lblTrangThai.setText("Trạng thái");

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"active", "inactive"}));

        javax.swing.GroupLayout pnlChitietNVLayout = new javax.swing.GroupLayout(pnlChitietNV);
        pnlChitietNV.setLayout(pnlChitietNVLayout);
        pnlChitietNVLayout.setHorizontalGroup(
                pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtMatKhau, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                                        .addComponent(txtSDT)
                                        .addComponent(txtTenNV)
                                        .addComponent(txtMaNV)
                                        .addComponent(txtUsername)
                                        .addComponent(lblTrangThai)
                                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                                .addGroup(pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblMatKhau)
                                                        .addComponent(lblMaNV)
                                                        .addComponent(lblTenNV)
                                                        .addComponent(lblSoDT)
                                                        .addComponent(lblUsername)
                                                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                                                .addGap(6, 6, 6)
                                                                .addGroup(pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                                                                .addComponent(rdoQuanLy, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(70, 70, 70)
                                                                                .addComponent(rdoNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(lblChucvu))))
                                                .addContainerGap())))
                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnDeleteNV, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                .addGap(487, 487, 487)
                                .addComponent(btnDelete))
        );
        pnlChitietNVLayout.setVerticalGroup(
                pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlChitietNVLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(lblMaNV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(lblTenNV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(lblSoDT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(lblMatKhau)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(lblUsername)
                                .addGap(18, 18, 18)
                                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblChucvu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(rdoQuanLy, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(rdoNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(lblTrangThai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(pnlChitietNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDeleteNV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(33, 33, 33)
                                .addComponent(btnDelete))
        );

        pnlDanhSachNV.setBackground(new Color(0, 0, 0, 0));
        pnlDanhSachNV.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DANH SÁCH", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), new Color(33, 21, 81))); // NOI18N

        tblNhanVien.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "MaNV", "Tên Nhân Viên", "Số điện thoại", "Mật khẩu", "Chức vụ", "Email"
                }
        ));
        tblNhanVien.setRowHeight(23);
        tblNhanVien.setSelectionBackground(new Color(24, 116, 180));
        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNhanVien);

        btnFirst.setBackground(new Color(24, 71, 133));
        btnFirst.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnFirst.setForeground(new java.awt.Color(255, 255, 255));
        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setBackground(new Color(24, 71, 133));
        btnPrev.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnPrev.setText("<<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setBackground(new Color(24, 71, 133));
        btnNext.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnNext.setForeground(new java.awt.Color(255, 255, 255));
        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setBackground(new Color(24, 71, 133));
        btnLast.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnLast.setForeground(new java.awt.Color(255, 255, 255));
        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDanhSachNVLayout = new javax.swing.GroupLayout(pnlDanhSachNV);
        pnlDanhSachNV.setLayout(pnlDanhSachNVLayout);
        pnlDanhSachNVLayout.setHorizontalGroup(
                pnlDanhSachNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDanhSachNVLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlDanhSachNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDanhSachNVLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        pnlDanhSachNVLayout.setVerticalGroup(
                pnlDanhSachNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDanhSachNVLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addGroup(pnlDanhSachNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(pnlChitietNV, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(pnlDanhSachNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pnlDanhSachNV, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pnlChitietNV, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28))
        );
    }// </editor-fold>

    private void txtMaNVActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void txtSDTActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void rdoQuanLyActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void rdoNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
        clearForm();
    }

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {
        insert();
    }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtMatKhau.getText().isEmpty()) {
            update();
        } else {
            update2();
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        //delete();
    }

    private void btnDeleteNVActionPerformed(java.awt.event.ActionEvent evt) {
        delete();
    }

    private void tblNhanVienMouseClicked(java.awt.event.MouseEvent evt) {
        this.row = tblNhanVien.rowAtPoint(evt.getPoint());
        edit();
    }

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {
        first();
    }

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {
        prev();
    }

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {
        next();
    }

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {
        last();
    }

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteNV;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChucvu;
    private javax.swing.JLabel lblMaNV;
    private javax.swing.JLabel lblMatKhau;
    private javax.swing.JLabel lblSoDT;
    private javax.swing.JLabel lblTenNV;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlChitietNV;
    private javax.swing.JPanel pnlDanhSachNV;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTenNV;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Quản Lý Nhân Viên");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1350, 800);
//            frame.setLocationRelativeTo(null);
//            NhanVienNetJPanel nhanVienPanel = new NhanVienNetJPanel();
//            frame.getContentPane().add(nhanVienPanel);
//            frame.setVisible(true);
//        });
//    }
    @Override
    public void formRefresh() {
    }
}
