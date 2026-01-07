package com.showise.employeedata.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service("emplyeeDataService")
public class EmployeeDataService {

	@Autowired
	EmployeeDataRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addEmp(EmployeeDataVO empVO) {
		repository.save(empVO);
	}

	public void updateEmp(EmployeeDataVO empVO) {
		repository.save(empVO);
	}

	public void deleteEmp(Integer empId) {
		if (repository.existsById(empId))
			repository.deleteByEmpno(empId);
	}

	public EmployeeDataVO getOneEmp(Integer empId) {
		Optional<EmployeeDataVO> optional = repository.findById(empId);
		return optional.orElse(null); 
	}

	public List<EmployeeDataVO> getAll() {
		return repository.findAll();
	}

	public List<EmployeeDataVO> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_employee_data.getAllC(map,sessionFactory.openSession());
	}
	
	 @Autowired
	    private EmployeeDataRepository repo;

	    public boolean existsEmpName(String empName) {
	        return empName != null && repo.existsByEmpName(empName.trim());
	    }

	    public boolean existsEmpAccount(String empAccount) {
	        return empAccount != null && repo.existsByEmpAccount(empAccount.trim());
	    }

	    public boolean existsEmpPassword(String empPassword) {
	        return empPassword != null && repo.existsByEmpPassword(empPassword.trim());
	    }

	    public boolean existsEmpEmail(String empEmail) {
	        return empEmail != null && repo.existsByEmpEmail(empEmail.trim());
	    }

	    public boolean existsEmpNameExcludeId(String empName, Integer empId) {
	        if (empName == null || empId == null) return false;
	        return repo.existsByEmpNameAndEmpIdNot(empName.trim(), empId);
	    }

	    public boolean existsEmpAccountExcludeId(String empAccount, Integer empId) {
	        if (empAccount == null || empId == null) return false;
	        return repo.existsByEmpAccountAndEmpIdNot(empAccount.trim(), empId);
	    }

	    public boolean existsEmpPasswordExcludeId(String empPassword, Integer empId) {
	        if (empPassword == null || empId == null) return false;
	        return repo.existsByEmpPasswordAndEmpIdNot(empPassword.trim(), empId);
	    }

	    public boolean existsEmpEmailExcludeId(String empEmail, Integer empId) {
	        if (empEmail == null || empId == null) return false;
	        return repo.existsByEmpEmailAndEmpIdNot(empEmail.trim(), empId);
	    }

	}

