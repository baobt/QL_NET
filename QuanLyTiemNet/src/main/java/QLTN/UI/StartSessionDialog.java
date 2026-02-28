package QLTN.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog "Bắt đầu" để khởi tạo phiên sử dụng
 */
public class StartSessionDialog extends JDialog {
    private boolean started = false;
    private Runnable onStartCallback;
    
    public StartSessionDialog(Frame parent, Runnable onStartCallback) {
        super(parent, "Bắt đầu sử dụng", true);
        this.onStartCallback = onStartCallback;
        initComponents();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel chính
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Label thông báo
        JLabel messageLabel = new JLabel("Bắt đầu sử dụng máy");
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        messageLabel.setForeground(new Color(16, 54, 103));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(messageLabel, gbc);
        
        // Nút OK
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        okButton.setBackground(new Color(24, 71, 133));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                started = true;
                dispose();
                if (onStartCallback != null) {
                    onStartCallback.run();
                }
            }
        });
        
        gbc.gridy = 1;
        mainPanel.add(okButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
    }
    
    public boolean isStarted() {
        return started;
    }
}
