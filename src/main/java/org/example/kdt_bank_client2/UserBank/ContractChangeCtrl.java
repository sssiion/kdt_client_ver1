package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;

public class ContractChangeCtrl {

    @FXML private Label lblName, lblPhone, lblResident, lblEmail, lblAddress;
    @FXML private VBox contractListVBox;

    @FXML private DatePicker newMaturityDatePicker;
    @FXML private CheckBox interestAdjustableCheckBox;
    @FXML private TextField newInterestRateField;
    @FXML private ComboBox<String> repaymentMethodComboBox;
    @FXML private TextField newLimitMoneyField;
    @FXML private Button saveChangeBtn;

    private LoanContract selectedContract;

    @FXML
    public void initialize() {

            // 고객 정보 확인
            CustomerInfo cust = CustomerSession.getCurrentCustomer();
            if (cust == null) {
                showAlert("경고", "먼저 고객을 검색하세요.");
                // UI 비활성화
                contractListVBox.setDisable(true);
                newMaturityDatePicker.setDisable(true);
                interestAdjustableCheckBox.setDisable(true);
                newInterestRateField.setDisable(true);
                repaymentMethodComboBox.setDisable(true);
                newLimitMoneyField.setDisable(true);
                saveChangeBtn.setDisable(true);
                return;
            }

            // 1) 고객 정보 표시
            lblName.setText(cust.getName());
            lblPhone.setText(cust.getPhone());
            lblResident.setText(cust.getResidentNumber());
            lblEmail.setText(cust.getEmail());
            lblAddress.setText(cust.getAddress());

            // 2) 변경 UI 초기화
            newMaturityDatePicker.setDisable(true);
            interestAdjustableCheckBox.setDisable(true);
            newInterestRateField.setDisable(true);
            repaymentMethodComboBox.setDisable(true);
            newLimitMoneyField.setDisable(true);
            saveChangeBtn.setDisable(true);
            repaymentMethodComboBox.getItems().addAll("만기일시상환","원리금균등상환","원금균등상환");

            // 3) DB에서 이 고객의 대출 계약 조회
            loadAndShowContracts(cust.getId());
        }

        private void loadAndShowContracts(int customerId) {
            // 고객 정보 확인
            if (CustomerSession.getCurrentCustomer() == null) {
                showAlert("경고", "고객 정보를 찾을 수 없습니다.");
                return;
            }

            contractListVBox.getChildren().clear();
        String sql =
                "SELECT c.contract_id, l.product_name, c.maturity_date, c.interest_rate, "
                        + "c.interest_adjustable, c.limit_money, c.repayment_method "
                        + "FROM loan_account c "
                        + " JOIN product l ON c.product_name = l.product_name "
                        + "WHERE c.customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanContract lc = new LoanContract(
                            rs.getInt("contract_id"),
                            rs.getString("product_name"),
                            rs.getDate("maturity_date").toLocalDate(),
                            rs.getDouble("interest_rate"),
                            rs.getBoolean("interest_adjustable"),
                            rs.getInt("limit_money"),
                            rs.getString("repayment_method")
                    );
                    Button btn = new Button(
                            lc.productName + " | 만기:" + lc.maturityDate +
                                    " | 금리:" + lc.interestRate + "%" +
                                    " | 한도:" + lc.limitMoney
                    );
                    btn.setMaxWidth(Double.MAX_VALUE);
                    btn.setOnAction(e -> onSelectContract(lc));
                    contractListVBox.getChildren().add(btn);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB 오류", "대출 계약 조회 중 오류 발생");
        }
    }

    private void onSelectContract(LoanContract lc) {
        selectedContract = lc;

        newMaturityDatePicker.setValue(lc.maturityDate);
        interestAdjustableCheckBox.setSelected(lc.interestAdjustable);
        newInterestRateField.setText(String.valueOf(lc.interestRate));
        repaymentMethodComboBox.setValue(lc.repaymentMethod);
        newLimitMoneyField.setText(String.valueOf(lc.limitMoney));

        newMaturityDatePicker.setDisable(false);
        interestAdjustableCheckBox.setDisable(false);
        newInterestRateField.setDisable(!lc.interestAdjustable);
        repaymentMethodComboBox.setDisable(false);
        newLimitMoneyField.setDisable(false);
        saveChangeBtn.setDisable(false);
    }

    @FXML
    private void onInterestAdjustableChanged() {
        newInterestRateField.setDisable(!interestAdjustableCheckBox.isSelected());
    }

    @FXML
    private void onSaveChange() {
        if (selectedContract == null) {
            showAlert("오류", "변경할 계약을 선택하세요.");
            return;
        }
        LocalDate newDate = newMaturityDatePicker.getValue();
        if (newDate == null) {
            showAlert("오류", "새 만기일을 입력하세요.");
            return;
        }
        double newRate = selectedContract.interestAdjustable
                ? Double.parseDouble(newInterestRateField.getText())
                : selectedContract.interestRate;
        int newLimit = Integer.parseInt(newLimitMoneyField.getText());
        String newRepay = repaymentMethodComboBox.getValue();

        String updateSql =
                "UPDATE loan_account SET maturity_date=?, interest_rate=?, interest_adjustable=?, "
                        + "limit_money=?, repayment_method=? WHERE contract_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setDate(1, Date.valueOf(newDate));
            ps.setDouble(2, newRate);
            ps.setBoolean(3, interestAdjustableCheckBox.isSelected());
            ps.setInt(4, newLimit);
            ps.setString(5, newRepay);
            ps.setInt(6, selectedContract.contractId);
            ps.executeUpdate();

            showAlert("성공", "대출 계약이 변경되었습니다.");
            // 리스트 갱신
            loadAndShowContracts(CustomerSession.getCurrentCustomer().getId());
            saveChangeBtn.setDisable(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("DB 오류", "계약 변경 중 오류 발생");
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static class LoanContract {
        int contractId;
        String productName;
        LocalDate maturityDate;
        double interestRate;
        boolean interestAdjustable;
        int limitMoney;
        String repaymentMethod;

        LoanContract(int id, String name, LocalDate date,
                     double rate, boolean adjustable,
                     int limit, String repay) {
            this.contractId = id;
            this.productName = name;
            this.maturityDate = date;
            this.interestRate = rate;
            this.interestAdjustable = adjustable;
            this.limitMoney = limit;
            this.repaymentMethod = repay;
        }
    }
}