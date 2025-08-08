package com.example.KDT_bank_server_project2.manager.ControllerUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.BankEmployee;
import com.example.KDT_bank_server_project2.manager.ServiceUser.BankEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class BankEmployeeController {

    @Autowired
    private BankEmployeeService bankEmployeeService;

    // 직원 생성
    @PostMapping
    public ResponseEntity<BankEmployee> createEmployee(@RequestBody BankEmployee employee) {
        try {
            BankEmployee createdEmployee = bankEmployeeService.createEmployee(employee);
            return ResponseEntity.ok(createdEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 모든 직원 조회
    @GetMapping
    public ResponseEntity<List<BankEmployee>> getAllEmployees() {
        List<BankEmployee> employees = bankEmployeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // ID로 직원 조회
    @GetMapping("/{id}")
    public ResponseEntity<BankEmployee> getEmployeeById(@PathVariable Long id) {
        Optional<BankEmployee> employee = bankEmployeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 이메일로 직원 조회
    @GetMapping("/email/{email}")
    public ResponseEntity<BankEmployee> getEmployeeByEmail(@PathVariable String email) {
        Optional<BankEmployee> employee = bankEmployeeService.getEmployeeByEmail(email);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 직원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<BankEmployee> updateEmployee(@PathVariable Long id, @RequestBody BankEmployee employee) {
        try {
            BankEmployee updatedEmployee = bankEmployeeService.updateEmployee(id, employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 직원 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<BankEmployee> updateEmployeeStatus(@PathVariable Long id,
                                                             @RequestParam BankEmployee.EmployeeStatus status) {
        try {
            BankEmployee updatedEmployee = bankEmployeeService.updateEmployeeStatus(id, status);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 부서별 직원 조회
    @GetMapping("/department/{department}")
    public ResponseEntity<List<BankEmployee>> getEmployeesByDepartment(@PathVariable String department) {
        List<BankEmployee> employees = bankEmployeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    // 역할별 직원 조회
    @GetMapping("/role/{role}")
    public ResponseEntity<List<BankEmployee>> getEmployeesByRole(@PathVariable BankEmployee.EmployeeRole role) {
        List<BankEmployee> employees = bankEmployeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(employees);
    }

    // 활성 직원 조회
    @GetMapping("/active")
    public ResponseEntity<List<BankEmployee>> getActiveEmployees() {
        List<BankEmployee> employees = bankEmployeeService.getActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    // 직원 검색
    @GetMapping("/search")
    public ResponseEntity<List<BankEmployee>> searchEmployees(@RequestParam String keyword) {
        List<BankEmployee> employees = bankEmployeeService.searchEmployees(keyword);
        return ResponseEntity.ok(employees);
    }

    // 비밀번호 변경
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            bankEmployeeService.changePassword(id, newPassword);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
