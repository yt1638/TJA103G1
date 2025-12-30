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

}