// Session.java (직원 세션 저장 기능 추가)
package org.example.kdt_bank_client2.UserBank.session;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.model.EmployeeInfo;

public class Session {
    private static CustomerInfo currentCustomer;
    private static EmployeeInfo  currentEmployee;

    public static CustomerInfo getCurrentCustomer() { return currentCustomer; }
    public static void setCurrentCustomer(CustomerInfo c) { currentCustomer = c; }

    public static EmployeeInfo getCurrentEmployee() { return currentEmployee; }
    public static void setCurrentEmployee(EmployeeInfo e) { currentEmployee = e; }
}
