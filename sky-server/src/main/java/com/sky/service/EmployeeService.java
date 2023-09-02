package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService extends IService<Employee> {
    Result<EmployeeLoginVO> login(EmployeeLoginDTO employeeLoginDTO);

    Result<String> logout();

    Result<String> addEmployee(Employee employee);

    Result<PageResult> getAllEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    Result<String> updateEmployee(Employee employee);

    Result updatePassword(PasswordEditDTO passwordEditDTO);
}
