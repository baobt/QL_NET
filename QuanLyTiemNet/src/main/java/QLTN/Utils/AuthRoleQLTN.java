/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Utils;

import QLTN.Entity.Employee;

public class AuthRoleQLTN {
    public static Employee user = null;

    public static boolean isLogin() {
        return user != null;
    }

    public static boolean isManager() {
        return isLogin() && "admin".equalsIgnoreCase(user.getRole());
    }

    public static void logout() {
        user = null;
    }
}
