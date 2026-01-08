
package com.showise.employeedata.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeDataRepository extends JpaRepository<EmployeeDataVO, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from employee_data where empId =?1", nativeQuery = true)
	void deleteByEmpno(int empno);

	@Query(value = "from EmployeeDataVO where empId=?1 and empName like?2 and empCreateTime=?3 order by empId")
	List<EmployeeDataVO> findByOthers(int empId , String empName , LocalDate empCreateTime);
	EmployeeDataVO findByEmpAccountAndEmpPassword(String empAccount, String empPassword);

	    boolean existsByEmpName(String empName);
	    boolean existsByEmpAccount(String empAccount);
	    boolean existsByEmpPassword(String empPassword);
	    boolean existsByEmpEmail(String empEmail);

	    boolean existsByEmpNameAndEmpIdNot(String empName, Integer empId);
	    boolean existsByEmpAccountAndEmpIdNot(String empAccount, Integer empId);
	    boolean existsByEmpPasswordAndEmpIdNot(String empPassword, Integer empId);
	    boolean existsByEmpEmailAndEmpIdNot(String empEmail, Integer empId);

}