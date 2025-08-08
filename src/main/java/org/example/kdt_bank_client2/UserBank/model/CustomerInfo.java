package org.example.kdt_bank_client2.UserBank.model;

public class CustomerInfo {
    private final int    id;
    private final String name;
    private final String residentNumber;
    private final String phone;
    private final String email;
    private final String address;

    public CustomerInfo(int id, String name, String residentNumber,
                    String phone, String email, String address) {
        this.id               = id;
        this.name             = name;
        this.residentNumber   = residentNumber;
        this.phone            = phone;
        this.email            = email;
        this.address          = address;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getResidentNumber() { return residentNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}
