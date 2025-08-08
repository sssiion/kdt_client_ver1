package org.example.kdt_bank_client2.UserBank;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Account {
    private final IntegerProperty accountNumber;
    private final StringProperty  productName;
    private final DoubleProperty  amount;
    private final ObjectProperty<LocalDate> openingDate;
    private final ObjectProperty<LocalDate> closingDate;
    private final StringProperty  status;
    private final StringProperty  productType;

    public Account(int accountNumber, String productName, double amount,
                   LocalDate openingDate, LocalDate closingDate,
                   String status, String productType) {
        this.accountNumber = new SimpleIntegerProperty(accountNumber);
        this.productName   = new SimpleStringProperty(productName);
        this.amount        = new SimpleDoubleProperty(amount);
        this.openingDate   = new SimpleObjectProperty<>(openingDate);
        this.closingDate   = new SimpleObjectProperty<>(closingDate);
        this.status        = new SimpleStringProperty(status);
        this.productType   = new SimpleStringProperty(productType);
    }

    public IntegerProperty   accountNumberProperty() { return accountNumber; }
    public StringProperty    productNameProperty()   { return productName;   }
    public DoubleProperty    amountProperty()        { return amount;        }
    public ObjectProperty<LocalDate> openingDateProperty() { return openingDate; }
    public ObjectProperty<LocalDate> closingDateProperty() { return closingDate; }
    public StringProperty    statusProperty()        { return status;        }
    public StringProperty    productTypeProperty()   { return productType;   }

    public int       getAccountNumber() { return accountNumber.get(); }
    public String    getProductName()   { return productName.get();   }
    public double    getAmount()        { return amount.get();        }
    public LocalDate getOpeningDate()   { return openingDate.get();   }
    public LocalDate getClosingDate()   { return closingDate.get();   }
    public String    getStatus()        { return status.get();        }
    public String    getProductType()   { return productType.get();   }
}
