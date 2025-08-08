package org.example.kdt_bank_client2.UserBank;

import javafx.beans.property.*;

public class Agreement {

    private final IntegerProperty agreementId;      // 약정 ID
    private final StringProperty customerName;      // 고객명
    private final StringProperty productName;       // 상품명
    private final StringProperty agreementDate;     // 약정 시작일
    private final StringProperty expirationDate;    // 약정 종료일
    private final StringProperty status;            // 상태 (ACTIVE, INACTIVE, EXPIRED)
    private final StringProperty note;              // 비고

    // 기본 생성자
    public Agreement(int agreementId, String customerName, String productName,
                     String agreementDate, String expirationDate,
                     String status, String note) {
        this.agreementId = new SimpleIntegerProperty(agreementId);
        this.customerName = new SimpleStringProperty(customerName);
        this.productName = new SimpleStringProperty(productName);
        this.agreementDate = new SimpleStringProperty(agreementDate);
        this.expirationDate = new SimpleStringProperty(expirationDate);
        this.status = new SimpleStringProperty(status);
        this.note = new SimpleStringProperty(note);
    }

    // Getter
    public int getAgreementId() { return agreementId.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getProductName() { return productName.get(); }
    public String getAgreementDate() { return agreementDate.get(); }
    public String getExpirationDate() { return expirationDate.get(); }
    public String getStatus() { return status.get(); }
    public String getNote() { return note.get(); }

    // Setter
    public void setAgreementId(int value) { agreementId.set(value); }
    public void setCustomerName(String value) { customerName.set(value); }
    public void setProductName(String value) { productName.set(value); }
    public void setAgreementDate(String value) { agreementDate.set(value); }
    public void setExpirationDate(String value) { expirationDate.set(value); }
    public void setStatus(String value) { status.set(value); }
    public void setNote(String value) { note.set(value); }

    // Property
    public IntegerProperty agreementIdProperty() { return agreementId; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty productNameProperty() { return productName; }
    public StringProperty agreementDateProperty() { return agreementDate; }
    public StringProperty expirationDateProperty() { return expirationDate; }
    public StringProperty statusProperty() { return status; }
    public StringProperty noteProperty() { return note; }
}