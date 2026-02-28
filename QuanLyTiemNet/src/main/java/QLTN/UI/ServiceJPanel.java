package QLTN.UI;


import QLTN.Dao.ServiceDAO;
import QLTN.Dao.ServiceOrderDAO;
import QLTN.Dao.ServiceOrderDetailDAO;
import QLTN.Entity.Service;
import QLTN.Entity.ServiceOrder;
import QLTN.Entity.ServiceOrderDetail;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;

public class ServiceJPanel extends JFrame {
    // Main panels
    private JTabbedPane tabbedPane;
    private JPanel productPanel;
    private JPanel orderPanel;

    // Product tab components
    private JTable productTable;
    private DefaultTableModel productModel;
    private JTextField searchField;
    private JButton addProductBtn;

    // Order tab components
    private JTable orderTable;
    private DefaultTableModel orderModel;
    private JTable orderDetailTable;
    private DefaultTableModel orderDetailModel;
    private JTextField orderSearchField;
    private JComboBox<String> statusFilter;

    // Image preview panel
    private JPanel imagePreviewPanel;
    private JLabel imagePreviewLabel;

    // DAOs
    private ServiceDAO serviceDAO;
    private ServiceOrderDAO orderDAO;
    private ServiceOrderDetailDAO orderDetailDAO;

    // Formatters
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ServiceJPanel() {
        setTitle("Quản Lý Quán Internet - Dịch Vụ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        setLocationRelativeTo(null);

        // Initialize DAOs
        serviceDAO = new ServiceDAO();
        orderDAO = new ServiceOrderDAO();
        orderDetailDAO = new ServiceOrderDetailDAO();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Initialize tabs
        initProductPanel();
        initOrderPanel();

        // Add tabs to tabbed pane
        tabbedPane.addTab("Quản Lý Sản Phẩm", null, productPanel, "Quản lý danh sách sản phẩm và dịch vụ");
        tabbedPane.addTab("Đơn Hàng", null, orderPanel, "Xem và quản lý đơn hàng");

        // Load data from database
        loadProductData();
        loadOrderData();
    }

    private void initProductPanel() {
        productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout(10, 10));
        productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel to hold header and search
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Quản Lý Sản Phẩm");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        addProductBtn = new JButton("+ Tạo Sản Phẩm");
        addProductBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addProductBtn.setPreferredSize(new Dimension(150, 35));
        addProductBtn.addActionListener(e -> showAddProductDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addProductBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(headerPanel);

        // Search and filter panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel searchLabel = new JLabel("Danh Sách Sản Phẩm");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel searchControlPanel = new JPanel(new BorderLayout(5, 0));

        JButton allButton = new JButton("Tất Cả");
        allButton.setPreferredSize(new Dimension(100, 30));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.putClientProperty("JTextField.placeholderText", "Search Here...");

        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String text = searchField.getText();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(productModel);
                productTable.setRowSorter(sorter);

                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.add(searchField, BorderLayout.CENTER);

        searchControlPanel.add(allButton, BorderLayout.WEST);
        searchControlPanel.add(searchBarPanel, BorderLayout.EAST);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchControlPanel, BorderLayout.EAST);

        topPanel.add(searchPanel);

        productPanel.add(topPanel, BorderLayout.NORTH);

        // Create a split pane for table and image preview
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); // 70% for table, 30% for image preview

        // Table panel
        String[] columnNames = {"ID", "Tên Sản Phẩm", "Giá Bán", "Phân Loại", "Mô Tả", "Số Lượng", "Hình Ảnh", "Trạng Thái"};

        productModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) { // Image column
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };

        productTable = new JTable(productModel);
        productTable.setRowHeight(60); // Increase row height for images
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Set custom renderer for image column
        productTable.getColumnModel().getColumn(6).setCellRenderer(new ImageRenderer());

        // Add context menu for edit/delete
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Ẩn");
        JMenuItem restoreItem = new JMenuItem("Hiện");

        editItem.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row != -1) {
                // Convert to model row in case table is sorted
                row = productTable.convertRowIndexToModel(row);
                showEditProductDialog(row);
            }
        });

        deleteItem.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row != -1) {
                // Convert to model row in case table is sorted
                row = productTable.convertRowIndexToModel(row);
                deleteProduct(row);
            }
        });
        restoreItem.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row != -1) {
                row = productTable.convertRowIndexToModel(row);
                int serviceId = Integer.parseInt(productModel.getValueAt(row, 0).toString());
                String productName = (String) productModel.getValueAt(row, 1);
                String status = (String) productModel.getValueAt(row, 7); // Cột "status"

                if (!"hidden".equals(status)) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm này không bị ẩn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn hiện lại sản phẩm '" + productName + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        serviceDAO.updateServiceStatus(serviceId, "available");
                        JOptionPane.showMessageDialog(this, "Đã hiện lại sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        loadProductData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi hiện lại sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        popupMenu.add(restoreItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        productTable.setComponentPopupMenu(popupMenu);

        // Add selection listener to show image preview
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    // Convert to model row in case table is sorted
                    row = productTable.convertRowIndexToModel(row);
                    showImagePreview(row);
                }
            }
        });

        // Add double-click listener for edit
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = productTable.getSelectedRow();
                    if (row != -1) {
                        // Convert to model row in case table is sorted
                        row = productTable.convertRowIndexToModel(row);
                        showEditProductDialog(row);
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(productTable);

        // Image preview panel
        imagePreviewPanel = new JPanel(new BorderLayout());
        imagePreviewPanel.setBorder(BorderFactory.createTitledBorder("Xem trước hình ảnh"));

        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);

        JScrollPane imageScrollPane = new JScrollPane(imagePreviewLabel);
        imagePreviewPanel.add(imageScrollPane, BorderLayout.CENTER);

        // Add components to split pane
        splitPane.setLeftComponent(tableScrollPane);
        splitPane.setRightComponent(imagePreviewPanel);

        // Add components to product panel
        productPanel.add(topPanel, BorderLayout.NORTH);
        productPanel.add(splitPane, BorderLayout.CENTER);
    }
    private void initOrderPanel() {
        orderPanel = new JPanel();
        orderPanel.setLayout(new BorderLayout(10, 10));
        orderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Đơn Hàng");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Chưa thanh toán", "Đã thanh toán"});
        statusFilter.addActionListener(e -> filterOrders());

        orderSearchField = new JTextField(15);
        orderSearchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm đơn hàng...");
        orderSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterOrders();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterOrders();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterOrders();
            }
        });

        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("   "));
        filterPanel.add(orderSearchField);

        headerPanel.add(filterPanel, BorderLayout.EAST);

        orderPanel.add(headerPanel, BorderLayout.NORTH);

        // Split pane for orders and order details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Orders table
        String[] orderColumns = {"ID", "Máy", "Thời gian", "Tổng tiền", "Trạng thái"};
        orderModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Add selection listener to show order details
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = orderTable.getSelectedRow();
                if (row != -1) {
                    // Convert to model row in case table is sorted
                    row = orderTable.convertRowIndexToModel(row);
                    loadOrderDetails(Integer.parseInt(orderModel.getValueAt(row, 0).toString()));
                }
            }
        });

        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        JPanel orderTablePanel = new JPanel(new BorderLayout());
        orderTablePanel.add(new JLabel("Danh sách đơn hàng:"), BorderLayout.NORTH);
        orderTablePanel.add(orderScrollPane, BorderLayout.CENTER);

        // Order details table
        String[] detailColumns = {"ID", "Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        orderDetailModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderDetailTable = new JTable(orderDetailModel);
        orderDetailTable.setRowHeight(30);
        orderDetailTable.getTableHeader().setReorderingAllowed(false);
        orderDetailTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane detailScrollPane = new JScrollPane(orderDetailTable);
        JPanel detailTablePanel = new JPanel(new BorderLayout());
        detailTablePanel.add(new JLabel("Chi tiết đơn hàng:"), BorderLayout.NORTH);
        detailTablePanel.add(detailScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(orderTablePanel);
        splitPane.setBottomComponent(detailTablePanel);

        orderPanel.add(splitPane, BorderLayout.CENTER);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton markPaidBtn = new JButton("Đánh dấu đã thanh toán");
        markPaidBtn.addActionListener(e -> markOrderAsPaid());

        JButton printBtn = new JButton("In hóa đơn");
        printBtn.addActionListener(e -> printOrder());

        actionPanel.add(markPaidBtn);
        actionPanel.add(printBtn);

        orderPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadProductData() {
        // Clear existing data
        productModel.setRowCount(0);

        // Get services from database
        List<Service> services = serviceDAO.getAllServices();

        // Add to table
        for (Service service : services) {
            Vector<Object> row = new Vector<>();
            row.add(service.getServiceId());
            row.add(service.getName());
            row.add(service.getPrice());

            // Determine category based on description or other logic
            String category = "Khác";
            if (service.getDescription() != null) {
                String desc = service.getDescription().toLowerCase();
                if (desc.contains("ăn") || desc.contains("food")) {
                    category = "Đồ ăn";
                } else if (desc.contains("uống") || desc.contains("drink") || desc.contains("nước")) {
                    category = "Đồ uống";
                } else if (desc.contains("thẻ") || desc.contains("card")) {
                    category = "Thẻ";
                }
            }

            row.add(category);
            row.add(service.getDescription());
            row.add(service.getQuantity());

            // Load image
            ImageIcon imageIcon = loadImageIcon(service.getImage());
            row.add(imageIcon);
            row.add(service.getStatus());
            productModel.addRow(row);
        }
    }
    private ImageIcon loadImageIcon(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                return null;
            }

            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                return null;
            }

            // Resize image for table cell
            Image scaledImg = img.getScaledInstance(200, 50, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    private void showImagePreview(int row) {
        String imagePath = null;
        int serviceId = Integer.parseInt(productModel.getValueAt(row, 0).toString());
        Service service = serviceDAO.getServiceById(serviceId);

        if (service != null) {
            imagePath = service.getImage();
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        // Resize image for preview panel
                        int maxWidth = 300;
                        int maxHeight = 300;

                        int width = img.getWidth();
                        int height = img.getHeight();

                        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);

                        int scaledWidth = (int) (width * scale);
                        int scaledHeight = (int) (height * scale);

                        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(scaledImg);

                        imagePreviewLabel.setIcon(icon);
                        imagePreviewLabel.setText("");
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error showing image preview: " + e.getMessage());
            }
        }

        // If no image or error
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Không có hình ảnh");
    }

    private void loadOrderData() {
        // Clear existing data
        orderModel.setRowCount(0);

        // Get orders from database
        List<ServiceOrder> orders = orderDAO.getAllOrders();

        // Add to table
        for (ServiceOrder order : orders) {
            Vector<Object> row = new Vector<>();
            row.add(order.getOrderId());
            // Assuming you have a way to get computer name from usage_id
            row.add(order.getUsageId()); // Replace with actual computer name
            row.add(dateFormat.format(order.getOrderTime()));
            row.add(currencyFormat.format(order.getTotalAmount()));
            String status = order.getPaymentStatus();
            row.add(status.equalsIgnoreCase("paid") ? "Đã thanh toán" : "Chưa thanh toán");

            orderModel.addRow(row);
        }
    }

    private void loadOrderDetails(int orderId) {
        // Clear existing data
        orderDetailModel.setRowCount(0);

        // Get order details from database
        List<ServiceOrderDetail> details = orderDetailDAO.getOrderDetailsByOrderId(orderId);

        // Add to table
        for (ServiceOrderDetail detail : details) {
            Vector<Object> row = new Vector<>();
            row.add(detail.getDetailId());
//            row.add(detail.getService().getName());
            row.add(detail.getQuantity());
            row.add(currencyFormat.format(detail.getUnitPrice()));
            row.add(currencyFormat.format(detail.getAmount()));

            orderDetailModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Thêm Sản Phẩm Mới", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên sản phẩm:");
        JTextField nameField = new JTextField();

        JLabel priceLabel = new JLabel("Giá bán:");
        JTextField priceField = new JTextField();

        JLabel categoryLabel = new JLabel("Phân loại:");
        String[] categories = {"Đồ ăn", "Đồ uống", "Thẻ", "Khác"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        JLabel descLabel = new JLabel("Mô tả:");
        JTextField descField = new JTextField();

        JLabel quantityLabel = new JLabel("Số lượng:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

        JLabel imageLabel = new JLabel("Hình ảnh:");
        JPanel imagePanel = new JPanel(new BorderLayout());
        JTextField imagePathField = new JTextField();
        JButton browseBtn = new JButton("Chọn");
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseBtn, BorderLayout.EAST);

        // Add image preview
        JLabel previewLabel = new JLabel("Xem trước:");
        JLabel imagePreview = new JLabel("Không có hình ảnh");
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreview.setPreferredSize(new Dimension(150, 150));

        // Browse button action
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());

                // Update preview
                try {
                    BufferedImage img = ImageIO.read(selectedFile);
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imagePreview.setIcon(new ImageIcon(scaledImg));
                        imagePreview.setText("");
                    }
                } catch (Exception ex) {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Không thể tải hình ảnh");
                }
            }
        });

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(quantityLabel);
        formPanel.add(quantitySpinner);
        formPanel.add(imageLabel);
        formPanel.add(imagePanel);
        formPanel.add(previewLabel);
        formPanel.add(imagePreview);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Hủy");
        JButton saveBtn = new JButton("Lưu");

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String category = (String) categoryCombo.getSelectedItem();
                String description = descField.getText().trim();
                int quantity = (int) quantitySpinner.getValue();
                String imagePath = imagePathField.getText().trim();

                // Create new service object
                Service newService = new Service();
                newService.setName(name);
                newService.setPrice(price);
                newService.setDescription(description);
                newService.setQuantity(quantity);
                newService.setImage(imagePath);
                newService.setStatus("available"); // Default status

                // Save to database
                serviceDAO.addService(newService);

                JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); // Reload data to update table
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditProductDialog(int row) {
        int serviceId = Integer.parseInt(productModel.getValueAt(row, 0).toString());

        // Get service from database
        Service service = serviceDAO.getServiceById(serviceId);

        if (service == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa Sản Phẩm", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên sản phẩm:");
        JTextField nameField = new JTextField(service.getName());

        JLabel priceLabel = new JLabel("Giá bán:");
        JTextField priceField = new JTextField(String.valueOf(service.getPrice()));

        JLabel categoryLabel = new JLabel("Phân loại:");
        String[] categories = {"Đồ ăn", "Đồ uống", "Thẻ", "Khác"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        // Determine category from table
        String category = (String) productModel.getValueAt(row, 3);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }

        JLabel descLabel = new JLabel("Mô tả:");
        JTextField descField = new JTextField(service.getDescription());

        JLabel quantityLabel = new JLabel("Số lượng:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(service.getQuantity(), 0, 999, 1));

        JLabel imageLabel = new JLabel("Hình ảnh:");
        JPanel imagePanel = new JPanel(new BorderLayout());
        JTextField imagePathField = new JTextField(service.getImage());
        JButton browseBtn = new JButton("Chọn");
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseBtn, BorderLayout.EAST);

        // Add image preview
        JLabel previewLabel = new JLabel("Xem trước:");
        JLabel imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreview.setPreferredSize(new Dimension(150, 150));

        // Load current image
        if (service.getImage() != null && !service.getImage().isEmpty()) {
            try {
                File file = new File(service.getImage());
                if (file.exists()) {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imagePreview.setIcon(new ImageIcon(scaledImg));
                    } else {
                        imagePreview.setText("Không thể tải hình ảnh");
                    }
                } else {
                    imagePreview.setText("Không tìm thấy file");
                }
            } catch (Exception ex) {
                imagePreview.setText("Lỗi tải hình ảnh");
            }
        } else {
            imagePreview.setText("Không có hình ảnh");
        }

        // Browse button action
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());

                // Update preview
                try {
                    BufferedImage img = ImageIO.read(selectedFile);
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imagePreview.setIcon(new ImageIcon(scaledImg));
                        imagePreview.setText("");
                    }
                } catch (Exception ex) {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Không thể tải hình ảnh");
                }
            }
        });

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(quantityLabel);
        formPanel.add(quantitySpinner);
        formPanel.add(imageLabel);
        formPanel.add(imagePanel);
        formPanel.add(previewLabel);
        formPanel.add(imagePreview);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Hủy");
        JButton saveBtn = new JButton("Lưu");

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String newCategory = (String) categoryCombo.getSelectedItem();
                String description = descField.getText().trim();
                int quantity = (int) quantitySpinner.getValue();
                String imagePath = imagePathField.getText().trim();

                // Update service object
                service.setName(name);
                service.setPrice(price);
                service.setDescription(description);
                service.setQuantity(quantity);
                service.setImage(imagePath);

                // Update to database
                serviceDAO.updateService(service);

                JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); // Reload data to update table
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteProduct(int row) {
        int serviceId = Integer.parseInt(productModel.getValueAt(row, 0).toString());
        String productName = (String) productModel.getValueAt(row, 1);

        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn ẩn sản phẩm '" + productName + "'?", "Xác nhận ẩn", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                serviceDAO.updateServiceStatus(serviceId, "hidden");
                JOptionPane.showMessageDialog(this, "Đã ẩn sản phẩm thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); // Reload data
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi ẩn sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    } private void filterOrders() {
        String status = (String) statusFilter.getSelectedItem();
        String searchText = orderSearchField.getText().trim().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(orderModel);
        orderTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> statusFilterRule = null;
        if (!status.equals("Tất cả")) {
            String mappedStatus = status.equals("Đã thanh toán") ? "Đã thanh toán" : "Chưa thanh toán";
            statusFilterRule = RowFilter.regexFilter("^" + mappedStatus + "$", 4);
        }

        RowFilter<DefaultTableModel, Object> searchFilterRule = null;
        if (!searchText.isEmpty()) {
            searchFilterRule = RowFilter.regexFilter("(?i)" + searchText); // Search in all columns
        }

        if (statusFilterRule != null && searchFilterRule != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(statusFilterRule, searchFilterRule)));
        } else if (statusFilterRule != null) {
            sorter.setRowFilter(statusFilterRule);
        } else if (searchFilterRule != null) {
            sorter.setRowFilter(searchFilterRule);
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void markOrderAsPaid() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để đánh dấu đã thanh toán", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        int orderId = Integer.parseInt(orderModel.getValueAt(modelRow, 0).toString());
        String displayedStatus = (String) orderModel.getValueAt(modelRow, 4);

        // Chỉ thực hiện nếu chưa thanh toán
        if (displayedStatus.equalsIgnoreCase("Đã thanh toán")) {
            JOptionPane.showMessageDialog(this, "Đơn hàng này đã được thanh toán rồi", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đánh dấu đơn hàng ID " + orderId + " là đã thanh toán?", "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                orderDAO.updateOrderPaymentStatus(orderId, "paid"); // cập nhật trạng thái thật
                JOptionPane.showMessageDialog(this, "Đã đánh dấu đơn hàng là đã thanh toán", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadOrderData(); // Reload bảng đơn hàng
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void printOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để in", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        int orderId = Integer.parseInt(orderModel.getValueAt(modelRow, 0).toString());

        ServiceOrder order = orderDAO.getOrderById(orderId);
        List<ServiceOrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(orderId);

        if (order == null || orderDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đơn hàng hoặc chi tiết đơn hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder bill = new StringBuilder("------------------- HÓA ĐƠN DỊCH VỤ -------------------\n");
        bill.append("Mã đơn hàng: ").append(order.getOrderId()).append("\n");
        // Assuming you can retrieve computer name based on usageId
        bill.append("Máy: ").append(order.getUsageId()).append("\n");
        bill.append("Thời gian: ").append(dateFormat.format(order.getOrderTime())).append("\n");
        bill.append("---------------------------------------------------\n");
        bill.append(String.format("%-5s %-20s %-10s %-10s %-10s\n", "ID", "Sản phẩm", "SL", "Đơn giá", "Thành tiền"));
        bill.append("---------------------------------------------------\n");

        double total = 0;
        for (ServiceOrderDetail detail : orderDetails) {
            bill.append(String.format("%-5d %-20s %-10d %-10s %-10s\n",
                    detail.getDetailId(),
                    detail.getServiceName(),
                    detail.getQuantity(),
                    currencyFormat.format(detail.getUnitPrice()),
                    currencyFormat.format(detail.getAmount())));
            total += detail.getAmount();
        }

        bill.append("---------------------------------------------------\n");
        bill.append(String.format("%-45s %-10s\n", "TỔNG TIỀN:", currencyFormat.format(total)));
        bill.append("---------------------------------------------------\n");
        bill.append("                 CẢM ƠN QUÝ KHÁCH!                 \n");

        JTextArea billTextArea = new JTextArea(bill.toString());
        billTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(billTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Hóa đơn đơn hàng ID " + orderId, JOptionPane.INFORMATION_MESSAGE);

        // Optionally, you can add code here to actually print the JTextArea content
    }

    // Custom renderer for the image column in product table
    private static class ImageRenderer extends JLabel implements TableCellRenderer {
        public ImageRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
                setText("");
            } else {
                setIcon(null);
                setText("Không có ảnh");
            }
            return this;
        }
    }

//    public static void main(String[] args) {
//        EventQueue.invokeLater(() -> {
//            try {
//                ServiceJPanel frame = new ServiceJPanel();
//                frame.setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
}