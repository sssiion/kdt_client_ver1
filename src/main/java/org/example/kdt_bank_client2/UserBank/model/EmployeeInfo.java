// EmployeeInfo.java (세션에 저장할 모델 클래스)
package org.example.kdt_bank_client2.UserBank.model;

public class EmployeeInfo {
    private final int employeeId;
    private final String name;
    private final String department;
    private final String email;
    private final String role;

    public EmployeeInfo(int employeeId, String name, String department, String email, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.email = email;
        this.role = role;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName()       { return name; }
    public String getDepartment() { return department; }
    public String getEmail()      { return email; }
    public String getRole()       { return role; }
}
