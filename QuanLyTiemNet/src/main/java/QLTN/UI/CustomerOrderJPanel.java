package QLTN.UI;






import QLTN.Dao.ServiceDAO;
import QLTN.Dao.ServiceOrderDAO;
import QLTN.Dao.ServiceOrderDetailDAO;
import QLTN.Entity.Service;
import QLTN.Entity.ServiceOrder;
import QLTN.Entity.ServiceOrderDetail;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;

public class CustomerOrderJPanel extends JFrame {
    // Main panels
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel productsPanel;
    private JPanel footerPanel;

    // Header components
    private JComboBox<String> categoryCombo;
    private JComboBox<String> sortCombo;
    private JTextField searchField;
    private JButton clearBtn;
    private JButton filterBtn;
    private JButton viewCartBtn;

    // Products display
    private JPanel productsGridPanel;
    private JScrollPane scrollPane;

    // Cart components
    private List<CartItem> cartItems = new ArrayList<>();

    // Computer info
    private String computerName;
    private int usageId;

    // DAOs
    private ServiceDAO serviceDAO;
    private ServiceOrderDAO orderDAO;
    private ServiceOrderDetailDAO orderDetailDAO;

    // Formatters
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    // Inner class to represent cart items
    private class CartItem {
        private Service service;
        private int quantity;

        public CartItem(Service service, int quantity) {
            this.service = service;
            this.quantity = quantity;
        }

        public Service getService() {
            return service;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return service.getPrice() * quantity;
        }
    }

    public CustomerOrderJPanel(int usageId, String computerName) {
        this.usageId = usageId;
        this.computerName = computerName;

        setTitle("Dịch vụ - " + computerName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setLocationRelativeTo(null);

        // Initialize DAOs
        serviceDAO = new ServiceDAO();
        orderDAO = new ServiceOrderDAO();
        orderDetailDAO = new ServiceOrderDetailDAO();

        // Initialize UI
        initUI();

        // Load products
        loadProducts();
    }

    private void initUI() {
                FlatLightLaf.setup();
        mainPanel = new JPanel(new BorderLayout(0, 0));
        setContentPane(mainPanel);

        // Initialize header panel
        initHeaderPanel();

        // Initialize products panel
        initProductsPanel();

        // Initialize footer panel
        initFooterPanel();

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(productsPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void initHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Dịch vụ");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Category and sort panel
        JPanel filterSortPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // Category filter
        JPanel categoryPanel = new JPanel(new BorderLayout(10, 0));
        JLabel categoryLabel = new JLabel("Loại sản phẩm");
        categoryCombo = new JComboBox<>(new String[]{"Tất cả", "Đồ ăn", "Đồ uống", "Thẻ", "Khác"});
        categoryCombo.setPreferredSize(new Dimension(200, 30));
        categoryPanel.add(categoryLabel, BorderLayout.WEST);
        categoryPanel.add(categoryCombo, BorderLayout.EAST);

        // Sort options
        JPanel sortPanel = new JPanel(new BorderLayout(10, 0));
        JLabel sortLabel = new JLabel("Sắp xếp theo:");
        sortCombo = new JComboBox<>(new String[]{"Tên A-Z", "Tên Z-A", "Giá thấp đến cao", "Giá cao đến thấp"});
        sortCombo.setPreferredSize(new Dimension(200, 30));
        sortPanel.add(sortLabel, BorderLayout.WEST);
        sortPanel.add(sortCombo, BorderLayout.EAST);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        JLabel searchLabel = new JLabel("Lọc theo tên");
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.EAST);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearBtn = new JButton("Clear");
        filterBtn = new JButton("Lọc");
        viewCartBtn = new JButton("Xem giỏ hàng");

        clearBtn.setPreferredSize(new Dimension(100, 30));
        clearBtn.setBackground(new Color(66, 139, 202));
        filterBtn.setPreferredSize(new Dimension(100, 30));
        viewCartBtn.setPreferredSize(new Dimension(120, 30));


        filterBtn.setBackground(new Color(66, 139, 202));
        filterBtn.setForeground(Color.BLACK);
        viewCartBtn.setBackground(new Color(66, 139, 202));
        viewCartBtn.setForeground(Color.BLACK);

        buttonsPanel.add(clearBtn);
        buttonsPanel.add(filterBtn);
        buttonsPanel.add(viewCartBtn);

        // Add action listeners
        categoryCombo.addActionListener(e -> filterProducts());
        sortCombo.addActionListener(e -> filterProducts());
        searchField.addActionListener(e -> filterProducts());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            sortCombo.setSelectedIndex(0);
            filterProducts();
        });
        filterBtn.addActionListener(e -> filterProducts());
        viewCartBtn.addActionListener(e -> openCartWindow());

        // Add components to filter sort panel
        filterSortPanel.add(categoryPanel);
        filterSortPanel.add(searchPanel);
        filterSortPanel.add(sortPanel);
        filterSortPanel.add(buttonsPanel);

        // Add components to header panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel);
        headerPanel.add(filterSortPanel);

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(getWidth(), 1));
        headerPanel.add(separator);
    }

    private void initProductsPanel() {
        productsPanel = new JPanel(new BorderLayout());

        // Sử dụng FlowLayout thay vì GridLayout để hiển thị sản phẩm linh hoạt hơn
        productsGridPanel = new JPanel();
        productsGridPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));

        scrollPane = new JScrollPane(productsGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        productsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void initFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Status label showing computer name
        JLabel statusLabel = new JLabel("Máy: " + computerName);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        footerPanel.add(statusLabel);
    }

    private void loadProducts() {
        // Clear existing products
        productsGridPanel.removeAll();

        // Get products from database
        List<Service> products = serviceDAO.getAvailableServices();

        // Display products
        displayProducts(products);
    }

    private void displayProducts(List<Service> products) {
        productsGridPanel.removeAll();

        for (Service service : products) {
            // Only display available products
            if (service.getQuantity() > 0 && "available".equals(service.getStatus())) {
                ProductPanel productPanel = new ProductPanel(service);
                productsGridPanel.add(productPanel);
            }
        }

        productsGridPanel.revalidate();
        productsGridPanel.repaint();
    }

    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();
        String category = (String) categoryCombo.getSelectedItem();
        String sortOption = (String) sortCombo.getSelectedItem();

        // Get all products
        List<Service> allProducts = serviceDAO.getAvailableServices();

        // Filter by category and search text
        List<Service> filteredProducts = new ArrayList<>();
        for (Service service : allProducts) {
            boolean matchesSearch = searchText.isEmpty() ||
                    service.getName().toLowerCase().contains(searchText) ||
                    (service.getDescription() != null &&
                            service.getDescription().toLowerCase().contains(searchText));

            boolean matchesCategory = category.equals("Tất cả") ||
                    (category.equals("Đồ ăn") && isFood(service)) ||
                    (category.equals("Đồ uống") && isDrink(service)) ||
                    (category.equals("Thẻ") && isCard(service)) ||
                    (category.equals("Khác") && isOther(service));

            if (matchesSearch && matchesCategory && service.getQuantity() > 0 && "available".equals(service.getStatus())) {
                filteredProducts.add(service);
            }
        }

        // Sort products
        filteredProducts.sort((s1, s2) -> {
            switch (sortOption) {
                case "Tên A-Z":
                    return s1.getName().compareTo(s2.getName());
                case "Tên Z-A":
                    return s2.getName().compareTo(s1.getName());
                case "Giá thấp đến cao":
                    return Double.compare(s1.getPrice(), s2.getPrice());
                case "Giá cao đến thấp":
                    return Double.compare(s2.getPrice(), s1.getPrice());
                default:
                    return 0;
            }
        });

        // Display filtered and sorted products
        displayProducts(filteredProducts);
    }

    private boolean isFood(Service service) {
        if (service.getDescription() == null) return false;
        String desc = service.getDescription().toLowerCase();
        return desc.contains("ăn") || desc.contains("food") || desc.contains("cơm") || desc.contains("mì");
    }

    private boolean isDrink(Service service) {
        if (service.getDescription() == null) return false;
        String desc = service.getDescription().toLowerCase();
        return desc.contains("uống") || desc.contains("drink") || desc.contains("nước") ||
                desc.contains("coca") || desc.contains("pepsi") || desc.contains("7up");
    }

    private boolean isCard(Service service) {
        if (service.getDescription() == null) return false;
        String desc = service.getDescription().toLowerCase();
        return desc.contains("thẻ") || desc.contains("card");
    }

    private boolean isOther(Service service) {
        return !isFood(service) && !isDrink(service) && !isCard(service);
    }

    private void addToCart(Service service, int quantity) {
        // Check if service is already in cart
        for (CartItem item : cartItems) {
            if (item.getService().getServiceId() == service.getServiceId()) {
                // Update quantity
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity <= service.getQuantity()) {
                    item.setQuantity(newQuantity);
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật số lượng " + service.getName() + " trong giỏ hàng.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không đủ số lượng. Chỉ còn " + service.getQuantity() + " sản phẩm.",
                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
        }

        // Add new item to cart
        cartItems.add(new CartItem(service, quantity));
        JOptionPane.showMessageDialog(this,
                "Đã thêm " + service.getName() + " vào giỏ hàng.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openCartWindow() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Giỏ hàng trống. Vui lòng thêm sản phẩm vào giỏ hàng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a new JFrame for the cart
        JFrame cartFrame = new JFrame("Giỏ hàng - " + computerName);
        cartFrame.setSize(800, 600);
        cartFrame.setLocationRelativeTo(this);
        cartFrame.setLayout(new BorderLayout(10, 10));

        // Create cart table
        String[] columnNames = {"Sản phẩm", "Đơn giá", "Số lượng", "Thành tiền", ""};
        DefaultTableModel cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 4; // Only quantity and remove button are editable
            }
        };

        JTable cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Tạo label tổng tiền
        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Set custom renderer and editor for remove button column
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Xóa"));
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), cartItems, cartModel, totalLabel));

        // Add quantity change listener
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Quantity column
                int row = e.getFirstRow();
                if (row >= 0 && row < cartItems.size()) {
                    try {
                        int newQuantity = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
                        if (newQuantity > 0 && newQuantity <= cartItems.get(row).getService().getQuantity()) {
                            cartItems.get(row).setQuantity(newQuantity);
                            updateCartTable(cartModel);

                            // Cập nhật tổng tiền
                            double total = 0;
                            for (CartItem item : cartItems) {
                                total += item.getTotal();
                            }
                            totalLabel.setText("Tổng cộng: " + currencyFormat.format(total) + "đ");
                        } else {
                            JOptionPane.showMessageDialog(cartFrame,
                                    "Số lượng không hợp lệ. Vui lòng nhập số từ 1 đến " +
                                            cartItems.get(row).getService().getQuantity(),
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                            updateCartTable(cartModel); // Reset to valid value
                        }
                    } catch (NumberFormatException ex) {
                        updateCartTable(cartModel); // Reset to valid value
                    }
                }
            }
        });

        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        // Total and action panel
        JPanel actionPanel = new JPanel(new BorderLayout(10, 0));
        actionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton clearBtn = new JButton("Xóa giỏ hàng");
        JButton checkoutBtn = new JButton("Đặt hàng");
        JButton closeBtn = new JButton("Đóng");

        // Style buttons
        checkoutBtn.setBackground(new Color(66, 139, 202));
        checkoutBtn.setForeground(Color.BLACK);

        clearBtn.addActionListener(e -> {
            if (!cartItems.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(cartFrame,
                        "Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    cartItems.clear();
                    updateCartTable(cartModel);
                    cartModel.fireTableDataChanged();
                    totalLabel.setText("Tổng cộng: 0đ");
                }
            }
        });

        checkoutBtn.addActionListener(e -> {
            checkout(cartFrame);
        });

        closeBtn.addActionListener(e -> cartFrame.dispose());

        buttonPanel.add(clearBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(closeBtn);

        actionPanel.add(totalLabel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        // Add components to cart frame
        JLabel headerLabel = new JLabel("Giỏ hàng - " + computerName);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        cartFrame.add(headerLabel, BorderLayout.NORTH);
        cartFrame.add(cartScrollPane, BorderLayout.CENTER);
        cartFrame.add(actionPanel, BorderLayout.SOUTH);

        // Update cart table
        updateCartTable(cartModel);

        // Update total
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotal();
        }
        totalLabel.setText("Tổng cộng: " + currencyFormat.format(total) + "đ");

        // Show cart frame
        cartFrame.setVisible(true);
    }

    private void updateCartTable(DefaultTableModel cartModel) {
        // Clear existing data
        cartModel.setRowCount(0);

        // Add cart items to table
        for (CartItem item : cartItems) {
            Object[] row = new Object[5];
            row[0] = item.getService().getName();
            row[1] = currencyFormat.format(item.getService().getPrice()) + "đ";
            row[2] = item.getQuantity();
            row[3] = currencyFormat.format(item.getTotal()) + "đ";
            row[4] = "Xóa";

            cartModel.addRow(row);
        }
    }

    private void checkout(JFrame cartFrame) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(cartFrame,
                    "Giỏ hàng trống. Vui lòng thêm sản phẩm vào giỏ hàng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(cartFrame,
                "Xác nhận đặt hàng?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Create order
            ServiceOrder order = new ServiceOrder();
            order.setUsageId(usageId);
            order.setOrderTime(new Date());
            order.setPaymentStatus("unpaid");
            order.setComputerName(computerName); // Lưu tên máy vào đơn hàng

            // Calculate total
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getTotal();
            }
            order.setTotalAmount(total);

            // Create order details
            List<ServiceOrderDetail> details = new ArrayList<>();
            for (CartItem item : cartItems) {
                ServiceOrderDetail detail = new ServiceOrderDetail();
                detail.setServiceId(item.getService().getServiceId());
                detail.setQuantity(item.getQuantity());
                detail.setUnitPrice(item.getService().getPrice());
                detail.setAmount(item.getTotal());
                details.add(detail);
            }

            order.setOrderDetails(details);

            // Save to database
            int orderId = orderDAO.addOrder(order);

            if (orderId > 0) {
                // Update product quantities
                for (CartItem item : cartItems) {
                    Service service = item.getService();
                    int newQuantity = service.getQuantity() - item.getQuantity();
                    serviceDAO.updateServiceQuantity(service.getServiceId(), newQuantity);
                }

                JOptionPane.showMessageDialog(cartFrame,
                        "Đặt hàng thành công! Mã đơn hàng: " + orderId,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Clear cart and reload products
                cartItems.clear();
                loadProducts();
                cartFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(cartFrame,
                        "Đặt hàng thất bại. Vui lòng thử lại sau.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Product panel for grid display
    private class ProductPanel extends JPanel {
        private Service service;

        public ProductPanel(Service service) {
            this.service = service;
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            setPreferredSize(new Dimension(220, 280));

            // Product image
            ImagePanel imagePanel = new ImagePanel(service.getImage());
            imagePanel.setPreferredSize(new Dimension(220, 150));

            // Product info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            // Product name
            JLabel nameLabel = new JLabel(service.getName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Product price
            JLabel priceLabel = new JLabel(currencyFormat.format(service.getPrice()) + "đ");
            priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Quantity panel
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            JButton minusBtn = new JButton("-");
            JTextField quantityField = new JTextField("0", 2);
            JButton plusBtn = new JButton("+");

            minusBtn.setMargin(new Insets(0, 5, 0, 5));
            plusBtn.setMargin(new Insets(0, 5, 0, 5));

            quantityField.setHorizontalAlignment(JTextField.CENTER);
            quantityField.setEditable(false);

            minusBtn.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    quantityField.setText(String.valueOf(quantity - 1));
                }
            });

            plusBtn.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity < service.getQuantity()) {
                    quantityField.setText(String.valueOf(quantity + 1));
                }
            });

            quantityPanel.add(minusBtn);
            quantityPanel.add(quantityField);
            quantityPanel.add(plusBtn);


            JButton addToCartBtn = new JButton("Thêm vào giỏ");
            addToCartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            addToCartBtn.setBackground(new Color(66, 139, 202));
            addToCartBtn.setForeground(Color.BLACK);


            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(addToCartBtn);

            addToCartBtn.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    addToCart(service, quantity);
                    quantityField.setText("0");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Vui lòng chọn số lượng lớn hơn 0",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // Add components to info panel
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            namePanel.add(nameLabel);

            JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            pricePanel.add(priceLabel);

            infoPanel.add(namePanel);
            infoPanel.add(pricePanel);
            infoPanel.add(quantityPanel);
            infoPanel.add(buttonPanel);

            // Add components to product panel
            add(imagePanel, BorderLayout.NORTH);
            add(infoPanel, BorderLayout.CENTER);
        }
    }


    private class ImagePanel extends JPanel {
        private BufferedImage image;

        public ImagePanel(String imagePath) {
            setBackground(Color.WHITE);

            try {
                if (imagePath != null && !imagePath.isEmpty()) {
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = ImageIO.read(file);
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi tải hình ảnh: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image != null) {

                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            } else {

                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.GRAY);
                g.drawString("Không có hình ảnh", 10, getHeight() / 2);
            }
        }
    }

    // Custom renderer for button column
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }

            return this;
        }
    }

    // Custom editor for button column
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        private List<CartItem> cartItems;
        private DefaultTableModel model;
        private JLabel totalLabel;

        public ButtonEditor(JCheckBox checkBox, List<CartItem> cartItems, DefaultTableModel model, JLabel totalLabel) {
            super(checkBox);
            this.cartItems = cartItems;
            this.model = model;
            this.totalLabel = totalLabel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            this.row = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Thay vì remove trực tiếp, gắn cờ để xử lý sau
                SwingUtilities.invokeLater(() -> {
                    if (row >= 0 && row < cartItems.size()) {
                        cartItems.remove(row);
                        updateCartTable(model); // <-- gọi lại phương thức này như đã xử lý trước
                        if (totalLabel != null) {
                            double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
                            totalLabel.setText("Tổng cộng: " + currencyFormat.format(total) + "đ");
                        }
                    }
                });
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            boolean result = super.stopCellEditing();
            model.fireTableDataChanged(); // Thêm dòng này để ép JTable cập nhật lại sau khi editor kết thúc
            return result;
        }
    }


    public class WrapLayout extends FlowLayout {
        private Dimension preferredLayoutSize;

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

                if (scrollPane != null && target.isValid()) {
                    dim.width -= (hgap + 1);
                }

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0) {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }

//    public static void main(String[] args) {
//        try {
//            // Set system look and feel
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        EventQueue.invokeLater(() -> {
//            // For testing, use a dummy usage ID and computer name
//            CustomerOrderJPanel form = new CustomerOrderJPanel ( 1, "Máy 01");
//            form.setVisible(true);
//        });
//    }
}