

package org.example.kdt_bank_client2.UserBank;

import javafx.beans.property.*;

/**
 * 만기 상품 데이터 모델 클래스
 *  - TableView와 데이터 바인딩용 Property 제공
 */
public class ExpirationProduct {

    private final StringProperty customerName;
    private final IntegerProperty accountNumber;
    private final StringProperty productName;
    private final StringProperty openingDate;
    private final StringProperty expirationDate;
    private final DoubleProperty balance;
    private final StringProperty status;

    // 생성자
    public ExpirationProduct(String customerName, int accountNumber, String productName,
                             String openingDate, String expirationDate, double balance, String status) {
        this.customerName = new SimpleStringProperty(customerName);
        this.accountNumber = new SimpleIntegerProperty(accountNumber);
        this.productName = new SimpleStringProperty(productName);
        this.openingDate = new SimpleStringProperty(openingDate);
        this.expirationDate = new SimpleStringProperty(expirationDate);
        this.balance = new SimpleDoubleProperty(balance);
        this.status = new SimpleStringProperty(status);
    }

    // Getter & Setter

    public String getCustomerName() {
        return customerName.get();
    }
    public void setCustomerName(String value) {
        customerName.set(value);
    }
    public StringProperty customerNameProperty() {
        return customerName;
    }

    public int getAccountNumber() {
        return accountNumber.get();
    }
    public void setAccountNumber(int value) {
        accountNumber.set(value);
    }
    public IntegerProperty accountNumberProperty() {
        return accountNumber;
    }

    public String getProductName() {
        return productName.get();
    }
    public void setProductName(String value) {
        productName.set(value);
    }
    public StringProperty productNameProperty() {
        return productName;
    }

    public String getOpeningDate() {
        return openingDate.get();
    }
    public void setOpeningDate(String value) {
        openingDate.set(value);
    }
    public StringProperty openingDateProperty() {
        return openingDate;
    }

    public String getExpirationDate() {
        return expirationDate.get();
    }
    public void setExpirationDate(String value) {
        expirationDate.set(value);
    }
    public StringProperty expirationDateProperty() {
        return expirationDate;
    }

    public double getBalance() {
        return balance.get();
    }
    public void setBalance(double value) {
        balance.set(value);
    }
    public DoubleProperty balanceProperty() {
        return balance;
    }

    public String getStatus() {
        return status.get();
    }
    public void setStatus(String value) {
        status.set(value);
    }
    public StringProperty statusProperty() {
        return status;
    }
}
