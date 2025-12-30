package com.showise.employeedata.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee_data")
public class EmployeeDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "employee_id")                            
    private Integer empId;

    @Column(name = "employee_name")
    private String empName;

    @Column(name = "employee_account")
    private String empAccount;

    @Column(name = "employee_password")
    private String empPassword;

    @Column(name = "employee_email")
    private String empEmail;

    @Column(name = "employee_status")
    private Short empStatus;

    @Column(name = "employee_create_time")
    private Timestamp empCreateTime;

    @Column(name = "employee_permissions")
    private Short empPermissions;

    public EmployeeDataVO() {
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpAccount() {
        return empAccount;
    }

    public void setEmpAccount(String empAccount) {
        this.empAccount = empAccount;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public void setEmpPassword(String empPassword) {
        this.empPassword = empPassword;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public Short getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(Short empStatus) {
        this.empStatus = empStatus;
    }

    public Timestamp getEmpCreateTime() {
        return empCreateTime;
    }

    public void setEmpCreateTime(Timestamp empCreateTime) {
        this.empCreateTime = empCreateTime;
    }

    public Short getEmpPermissions() {
        return empPermissions;
    }

    public void setEmpPermissions(Short empPermissions) {
        this.empPermissions = empPermissions;
    }
}
