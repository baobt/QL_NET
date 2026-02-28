/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Utils;

import java.text.DecimalFormat;

/**
 * Utility class để format thời gian và tiền
 */
public class TimeFormatter {
    
    /**
     * Format giây thành HH:MM:SS
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    /**
     * Format tiền theo định dạng Việt Nam
     */
    public static String formatMoney(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }
    
    /**
     * Format tiền từ BigDecimal
     */
    public static String formatMoney(java.math.BigDecimal amount) {
        if (amount == null) return "0.00";
        return formatMoney(amount.doubleValue());
    }

    /**
     * Format tiền từ BigDecimal với định dạng VND
     */
    public static String formatMoneyVND(java.math.BigDecimal amount) {
        if (amount == null) return "0 VND";
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount.doubleValue()) + " VND";
    }

    /**
     * Format tiền với định dạng VND
     */
    public static String formatMoneyVND(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VND";
    }
}
