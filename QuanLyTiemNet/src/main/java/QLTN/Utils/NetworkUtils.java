package QLTN.Utils;

import QLTN.Dao.ComputerDao;
import QLTN.Entity.Computer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class để xử lý network và IP address
 * @author AD
 */
public class NetworkUtils {
    
    /**
     * Lấy địa chỉ IP hiện tại của máy tính
     * Ưu tiên lấy IP LAN trước, sau đó mới đến IP khác
     */
    public static String getCurrentMachineIP() {
        try {
            String candidateIP = null;
            
            // Lấy tất cả network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                // Bỏ qua loopback và interface không hoạt động
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    
                    // Chỉ lấy IPv4 và không phải loopback
                    if (!inetAddress.isLoopbackAddress() && 
                        !inetAddress.isLinkLocalAddress() && 
                        inetAddress.getHostAddress().indexOf(':') == -1) {
                        
                        String ip = inetAddress.getHostAddress();
                        
                        // Ưu tiên IP trong dải LAN
                        if (isLANIP(ip)) {
                            System.out.println("Found LAN IP: " + ip + " on interface: " + networkInterface.getName());
                            return ip;
                        }
                        
                        // Lưu IP khác làm candidate
                        if (candidateIP == null) {
                            candidateIP = ip;
                        }
                    }
                }
            }
            
            // Nếu không tìm thấy LAN IP, trả về candidate IP
            if (candidateIP != null) {
                System.out.println("Using candidate IP: " + candidateIP);
                return candidateIP;
            }
            
            // Fallback: sử dụng phương pháp cũ
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
            System.out.println("Fallback to localhost IP: " + ip);
            return ip;
            
        } catch (Exception e) {
            System.err.println("Error getting current machine IP: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback cuối cùng
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex) {
                System.err.println("Fallback IP detection failed: " + ex.getMessage());
                return "127.0.0.1";
            }
        }
    }
    
    /**
     * Kiểm tra xem IP có phải là địa chỉ LAN không
     */
    private static boolean isLANIP(String ip) {
        return ip.startsWith("192.168.") || 
               ip.startsWith("10.") || 
               (ip.startsWith("172.") && 
                Integer.parseInt(ip.split("\\.")[1]) >= 16 && 
                Integer.parseInt(ip.split("\\.")[1]) <= 31);
    }
    
    /**
     * Tìm máy tính dựa trên địa chỉ IP
     * @param ipAddress địa chỉ IP cần tìm
     * @return computer ID nếu tìm thấy, -1 nếu không tìm thấy
     */
    public static int findComputerByIP(String ipAddress) {
        try {
            ComputerDao computerDao = new ComputerDao();
            List<Computer> computers = computerDao.selectAll();
            
            System.out.println("Searching for IP: " + ipAddress + " in " + computers.size() + " computers");
            
            for (Computer computer : computers) {
                String computerIP = computer.getIpAddress();
                System.out.println("Checking computer ID " + computer.getComputerId() + 
                                 " with IP: " + computerIP);
                
                if (ipAddress.equals(computerIP)) {
                    System.out.println("Found matching computer: ID=" + computer.getComputerId() + 
                                     ", Name=" + computer.getName());
                    return computer.getComputerId();
                }
            }
            
            System.out.println("No computer found with IP: " + ipAddress);
            return -1;
            
        } catch (Exception e) {
            System.err.println("Error finding computer by IP: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Lấy thông tin chi tiết về máy tính dựa trên IP
     */
    public static Computer getComputerByIP(String ipAddress) {
        try {
            ComputerDao computerDao = new ComputerDao();
            List<Computer> computers = computerDao.selectAll();
            
            for (Computer computer : computers) {
                if (ipAddress.equals(computer.getIpAddress())) {
                    return computer;
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error getting computer by IP: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kiểm tra xem IP có hợp lệ không
     */
    public static boolean isValidIP(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        try {
            String[] parts = ip.split("\\.");
            
            if (parts.length != 4) {
                return false;
            }
            
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Lấy tất cả IP addresses của máy hiện tại
     */
    public static void printAllIPAddresses() {
        try {
            System.out.println("=== All IP Addresses ===");
            
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                System.out.println("Interface: " + networkInterface.getName() + 
                                 " (" + networkInterface.getDisplayName() + ")");
                System.out.println("  Up: " + networkInterface.isUp());
                System.out.println("  Loopback: " + networkInterface.isLoopback());
                
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    System.out.println("  IP: " + inetAddress.getHostAddress());
                    System.out.println("    Loopback: " + inetAddress.isLoopbackAddress());
                    System.out.println("    Link Local: " + inetAddress.isLinkLocalAddress());
                    System.out.println("    Site Local: " + inetAddress.isSiteLocalAddress());
                }
                
                System.out.println();
            }
            
        } catch (SocketException e) {
            System.err.println("Error printing IP addresses: " + e.getMessage());
        }
    }
    
    /**
     * Test method để kiểm tra kết nối database và tìm máy tính
     */
    public static void testComputerLookup() {
        try {
            String currentIP = getCurrentMachineIP();
            System.out.println("Current IP: " + currentIP);
            
            ComputerDao computerDao = new ComputerDao();
            List<Computer> computers = computerDao.selectAll();
            
            System.out.println("Available computers in database:");
            for (Computer computer : computers) {
                System.out.println("ID: " + computer.getComputerId() + 
                                 ", Name: " + computer.getName() + 
                                 ", IP: " + computer.getIpAddress() + 
                                 ", Status: " + computer.getStatus());
            }
            
            int foundId = findComputerByIP(currentIP);
            if (foundId != -1) {
                System.out.println("SUCCESS: Found computer with ID: " + foundId);
            } else {
                System.out.println("WARNING: No computer found with current IP: " + currentIP);
            }
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}