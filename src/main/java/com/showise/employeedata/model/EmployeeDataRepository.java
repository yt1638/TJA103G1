
package com.showise.employeedata.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

public interface EmployeeDataRepository extends JpaRepository<EmployeeDataVO, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from employee_data where empId =?1", nativeQuery = true)
	void deleteByEmpno(int empno);

	@Query(value = "from EmployeeDataVO where empId=?1 and empName like?2 and empCreateTime=?3 order by empId")
	List<EmployeeDataVO> findByOthers(int empId , String empName , LocalDate empCreateTime);
}