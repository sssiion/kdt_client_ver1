package org.example.kdt_bank_client2.UserBank.ServiceUser;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.Controller.UserController;
import org.example.kdt_bank_client2.ControllerUser.AccountController;
import org.example.kdt_bank_client2.ControllerUser.CustomerController;
import org.example.kdt_bank_client2.ControllerUser.LoanAccountController;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.DtoUser.*;
import org.example.kdt_bank_client2.Session.UserSession;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerController customerController;
    private final AccountController accountController;
    private final CustomerSession customerSession;
    private final LoanAccountController loanAccountController;


    public CustomerResponseDto updateCustomer(String id, CustomerUpdateRequestDto requestDto){
        try{
            return customerController.updateCustomer(id,requestDto).getData();
        }catch (Exception e){
            e.printStackTrace();
        }return null;
    }

    public List<CustomerResponseDto> searchCustomers(String keyword){
        try{
            return customerController.searchCustomers(keyword).getData();
        }catch (Exception e){
            e.printStackTrace();
        }return null;
    }

    //고객 생성
    public CustomerResponseDto createCustomer(CustomerCreateRequestDto dto){
        try{
            CustomerResponseDto customer = customerController.createCustomer(dto).getData();
            return customer;
        }catch(Exception e){
            e.printStackTrace();
        }return null;
    }

    //고객별 대출 계좌 조회
    public void getLoanAccountsByCustomerId(String customerID) {
        try{
            List<LoanAccountResponseDto> dtos = loanAccountController.getLoanAccountsByCustomerId(customerID);
            customerSession.setLoanAccountResponseDtos(dtos);
        }catch (Exception e){
            System.out.println("대출 계좌 조회 오류: "+e.getMessage());
        }
    }
    // 고객 대출 변경
    public void getLoanAccountChange(LoanAccountCreateRequestDto loanAccount) throws Exception {
        LoanAccountResponseDto dto = loanAccountController.changeExpireDate(loanAccount);
        customerSession.setLoanAccountResponseDto(dto);
        //getLoanAccountsByCustomerId(customerSession.getCustomer().getId());
    }
    
    //계좌번호로 계좌 조회
    public void getotherAccount(String accountNumber){
        try{
            AccountResponseDto dto = accountController.getAccountByNumber(accountNumber);
            customerSession.setOtheracountResponseDto(dto);
        }catch(Exception e){
            System.err.println("❌ 계좌조회 오류: " + e.getMessage());
        }
    }
    //이름, 주민번호 고객 조회
    public void getCustomerByNameAndResidentNumber(String name, String number){
        try{
            CustomerResponseDto dto = customerController.getCustomerByNameAndResidentNumber(name, number);
            customerSession.setCustomerResponseDto(dto);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 고객별 계좌 조회
    //loadCustomerAccounts
    public void getAccountsByCustomerId(String customerId) {
        try{
            List<AccountResponseDto> dtos = accountController.getAccountById(customerId);
            customerSession.setAccountResponseDtos(dtos);
        }catch(Exception e){
            System.err.println("❌고객 계좌조회 오류: " + e.getMessage());

        }
    }
    //입금
    public CashTransactionResponseDto deposit(TransferRequestDto dto){
        try{
            CashTransactionResponseDto currentdto = accountController.deposit(dto);
            getAccountsByCustomerId(customerSession.getCustomer().getId());
            return currentdto;
        }catch(Exception e){
            System.err.println("❌입금 실패: " + e.getMessage());
        } return null;
    }
    //출금
    public CashTransactionResponseDto withdraw(TransferRequestDto dto){
        try{
            CashTransactionResponseDto currentdto = accountController.withdraw(dto);
            getAccountsByCustomerId(customerSession.getCustomer().getId());
            return currentdto;
        }catch(Exception e){
            System.err.println("❌출금 실패: " + e.getMessage());
        }return null;
    }

    //송금
    public CashTransactionResponseDto remittance(TransferRequestDto dto){
        try{
            CashTransactionResponseDto currentdto = accountController.remittance(dto);
            getAccountsByCustomerId(customerSession.getCustomer().getId());
            return currentdto;
        }catch(Exception e){
            System.err.println("❌송금 실패: " + e.getMessage());
        }return null;
    }



}
