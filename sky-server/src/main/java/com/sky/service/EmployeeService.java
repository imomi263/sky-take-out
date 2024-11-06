package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;

public interface EmployeeService {
    /*
        员工登录
        @param employeeLoginId
        @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO) throws AccountNotFoundException, AccountLockedException;
    /*
        员工注册
     */
    void save(EmployeeDTO employeeDTO);
    /*
        一页显示多少员工
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void enableOrDisableEmployee(Integer status, Long id);

    Employee getEmployeeById(Long id);

    void updateEmployee(EmployeeDTO employee);
}
