package com.showise.employeedata.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employee_data")
public class EmployeeDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer empId;

    @NotBlank(message = "請輸入員工姓名")
    @Column(name = "employee_name")
    private String empName;

    @NotBlank(message = "請輸入員工帳號")
    @Column(name = "employee_account")
    private String empAccount;

    @NotBlank(message = "請輸入員工密碼")
    @Column(name = "employee_password")
    private String empPassword;

    @NotBlank(message = "請輸入員工信箱")
    @Email(message = "信箱格式不正確")
    @Column(name = "employee_email")
    private String empEmail;

    @NotNull(message = "請輸入員工狀態")
    @Column(name = "employee_status")
    private Short empStatus;

    @NotNull(message = "請選擇到職日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "employee_create_time")
    private LocalDate empCreateTime;

    @NotNull(message = "請輸入員工權限")
    @Column(name = "employee_permissions")
    private Short empPermissions;

    public EmployeeDataVO() {}

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

    public LocalDate getEmpCreateTime() {
        return empCreateTime;
    }

    public void setEmpCreateTime(LocalDate empCreateTime) {
        this.empCreateTime = empCreateTime;
    }

    public Short getEmpPermissions() {
        return empPermissions;
    }

    public void setEmpPermissions(Short empPermissions) {
        this.empPermissions = empPermissions;
    }
}
