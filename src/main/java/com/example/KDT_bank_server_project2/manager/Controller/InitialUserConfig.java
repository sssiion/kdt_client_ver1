package com.example.KDT_bank_server_project2.manager.Controller;

import com.example.KDT_bank_server_project2.manager.Entity.User;
import com.example.KDT_bank_server_project2.manager.EntityUser.BankEmployee;
import com.example.KDT_bank_server_project2.manager.EntityUser.Customer;
import com.example.KDT_bank_server_project2.manager.EntityUser.Product;
import com.example.KDT_bank_server_project2.manager.Repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Configuration
@Transactional
public class InitialUserConfig {
    @Bean
    public ApplicationRunner addAdminUser(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUserId("root").isEmpty()) {
                User admin = new User();
                admin.setUserId("root");
                admin.setUser_authority(User.UserType.BM);
                admin.setUserName("관리자");
                admin.setUserPhone("000-0000-0000");
                admin.setPassword("1234"); // 필요시 암호화
                admin.setCreateAt(java.time.LocalDateTime.now());
                admin.setOffline(); // 또는 setOnline() 원할 경우

                User client = new User();
                client.setUserId("client");
                client.setUser_authority(User.UserType.BT);
                client.setUserName("client");
                client.setUserPhone("000-0001-0000");
                client.setPassword("1234");
                client.setCreateAt(java.time.LocalDateTime.now());
                client.setOffline();
                userRepository.save(client);

                userRepository.save(admin);
            }
        };
    }
    @Bean
    public ApplicationRunner addInitialData(
            CustomerRepository customerRepository,
            BankEmployeeRepository bankEmployeeRepository,
            ProductRepository productRepository,
            AccountRepository accountRepository) {
        return args -> {
            // 초기 직원 데이터
            if (bankEmployeeRepository.findByEmail("admin@nuribank.com").isEmpty()) {
                BankEmployee admin = new BankEmployee();
                admin.setName("김관리");
                admin.setEmail("admin@nuribank.com");
                admin.setPassword("password123");
                admin.setDepartment("관리부");
                admin.setRole(BankEmployee.EmployeeRole.ADMIN);
                admin.setPhone("010-1234-5678");
                admin.setStatus(BankEmployee.EmployeeStatus.ACTIVE);
                bankEmployeeRepository.save(admin);

                BankEmployee manager = new BankEmployee();
                manager.setName("이매니저");
                manager.setEmail("manager@nuribank.com");
                manager.setPassword("password123");
                manager.setDepartment("고객서비스부");
                manager.setRole(BankEmployee.EmployeeRole.MANAGER);
                manager.setPhone("010-2345-6789");
                manager.setStatus(BankEmployee.EmployeeStatus.ACTIVE);
                bankEmployeeRepository.save(manager);
            }

            // 초기 상품 데이터
            if (productRepository.findByProductName("입출금통장").isEmpty()) {
                Product savingsProduct = new Product();
                savingsProduct.setProductName("입출금통장");
                savingsProduct.setProductDetail("자유로운 입출금이 가능한 통장");
                savingsProduct.setCategory("deachul");
                savingsProduct.setProductCategory("입출금");
                savingsProduct.setMaxRate(new BigDecimal("0.3"));
                savingsProduct.setMinRate(new BigDecimal("0.1"));
                savingsProduct.setLimitMoney(new BigDecimal("10000000"));
                savingsProduct.setStatus(Product.ProductStatus.ACTIVE);
                productRepository.save(savingsProduct);

                Product loanProduct = new Product();
                loanProduct.setProductName("개인신용대출");
                loanProduct.setProductDetail("개인신용대출 상품");
                loanProduct.setCategory("deachul");
                loanProduct.setProductCategory("대출");
                loanProduct.setMaxRate(new BigDecimal("8.5"));
                loanProduct.setMinRate(new BigDecimal("6.2"));
                loanProduct.setLimitMoney(new BigDecimal("50000000"));
                loanProduct.setStatus(Product.ProductStatus.ACTIVE);
                productRepository.save(loanProduct);
            }

            // 초기 고객 데이터
            if (customerRepository.findByEmail("test@example.com").isEmpty()) {
                Customer customer = new Customer();
                customer.setName("홍길동");
                customer.setEmail("test@example.com");
                customer.setPassword("password123");
                customer.setPhone("010-1111-2222");
                customer.setResidentNumber("900101-1234567");
                customer.setAddress("서울시 강남구");
                customer.setStatus(Customer.CustomerStatus.ACTIVE);
                customerRepository.save(customer);
            }
        };
    }
}
