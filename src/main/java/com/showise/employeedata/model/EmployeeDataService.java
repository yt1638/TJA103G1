package com.showise.employeedata.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.hibernate.SessionFactory;

@Service("employeeDataService") // 你原本 emplyeeDataService 拼錯，建議一起修
public class EmployeeDataService {

    @Autowired
    private EmployeeDataRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    public EmployeeDataVO login(String account, String password) {
        if (account == null || password == null) return null;
        account = account.trim();
        password = password.trim();
        if (account.isEmpty() || password.isEmpty()) return null;

        return repository.findByEmpAccountAndEmpPassword(account, password);
    }



    public void addEmp(EmployeeDataVO empVO) {
        repository.save(empVO);
    }

    public void updateEmp(EmployeeDataVO empVO) {
        repository.save(empVO);
    }

    public void deleteEmp(Integer empId) {
        if (repository.existsById(empId)) {
            repository.deleteByEmpno(empId);
        }
    }

    public EmployeeDataVO getOneEmp(Integer empId) {
        Optional<EmployeeDataVO> optional = repository.findById(empId);
        return optional.orElse(null);
    }

    public List<EmployeeDataVO> getAll() {
        return repository.findAll();
    }

    public List<EmployeeDataVO> getAll(Map<String, String[]> map) {
        return HibernateUtil_CompositeQuery_employee_data.getAllC(map, sessionFactory.openSession());
    }

    // ✅ 下面 exists 系列全部改用同一個 repository
    public boolean existsEmpName(String empName) {
        return empName != null && repository.existsByEmpName(empName.trim());
    }

    public boolean existsEmpAccount(String empAccount) {
        return empAccount != null && repository.existsByEmpAccount(empAccount.trim());
    }

    public boolean existsEmpPassword(String empPassword) {
        return empPassword != null && repository.existsByEmpPassword(empPassword.trim());
    }

    public boolean existsEmpEmail(String empEmail) {
        return empEmail != null && repository.existsByEmpEmail(empEmail.trim());
    }

    public boolean existsEmpNameExcludeId(String empName, Integer empId) {
        if (empName == null || empId == null) return false;
        return repository.existsByEmpNameAndEmpIdNot(empName.trim(), empId);
    }

    public boolean existsEmpAccountExcludeId(String empAccount, Integer empId) {
        if (empAccount == null || empId == null) return false;
        return repository.existsByEmpAccountAndEmpIdNot(empAccount.trim(), empId);
    }

    public boolean existsEmpPasswordExcludeId(String empPassword, Integer empId) {
        if (empPassword == null || empId == null) return false;
        return repository.existsByEmpPasswordAndEmpIdNot(empPassword.trim(), empId);
    }

    public boolean existsEmpEmailExcludeId(String empEmail, Integer empId) {
        if (empEmail == null || empId == null) return false;
        return repository.existsByEmpEmailAndEmpIdNot(empEmail.trim(), empId);
        
    }
    
}
