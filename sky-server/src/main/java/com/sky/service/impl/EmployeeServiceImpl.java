package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.EmployeeHolder;
import com.sky.vo.EmployeeLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Result<EmployeeLoginVO> login(EmployeeLoginDTO employeeLoginDTO) {
        Employee e = query().eq("username", employeeLoginDTO.getUsername()).one();
        if(e == null) {
            return Result.error(MessageConstant.ACCOUNT_NOT_FOUND);
        } else if (!DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes()).equals(e.getPassword())) {
            return Result.error(MessageConstant.PASSWORD_ERROR);
        }
        String token = UUID.randomUUID().toString();
        EmployeeLoginVO employeeLoginVO = new EmployeeLoginVO(e.getId(), e.getUsername(), e.getName(), token);
        String tokenKey = RedisConstant.EMPLOYEE_LOGIN_TOKEN+token;
        Map<String, Object> employeeMap = BeanUtil.beanToMap(employeeLoginVO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
        redisTemplate.opsForHash().putAll(tokenKey, employeeMap);
        redisTemplate.expire(tokenKey, RedisConstant.EMPLOYEE_LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        EmployeeHolder.saveUser(employeeLoginVO);
        return Result.success(employeeLoginVO);
    }

    /**
     * 登出
     * @return
     */
    @Override
    public Result<String> logout() {
        String token = EmployeeHolder.getUser().getToken();
        if(StrUtil.isBlank(token)){
            return Result.error("401");
        }
        String tokenKey = RedisConstant.EMPLOYEE_LOGIN_TOKEN+ token;
        redisTemplate.delete(tokenKey);
        return Result.success();
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @Override
    @AutoFill(OperationType.INSERT)
    public Result<String> addEmployee(Employee employee) {
        if(!authorityCheck(EmployeeHolder.getUser().getUserName())){
            return Result.error("你没有权限");
        }
        if(query().eq("username", employee.getUsername()).one() != null){
            return Result.error(MessageConstant.ACCOUNT_IS_EXIST);
        }
        save(employee);
        return Result.success("添加成功");
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public Result<PageResult> getAllEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        Page getPage = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        /*if(StrUtil.isNotBlank(employeePageQueryDTO.getName())){
            empList = query().like("name", employeePageQueryDTO.getName()).orderByAsc("create_time").page(getPage).getRecords();
        }else {
            empList = query().orderByAsc("create_time").page(getPage).getRecords();
        }*/
        List<Employee> empList = query().like(StrUtil.isNotBlank(employeePageQueryDTO.getName()), "name", employeePageQueryDTO.getName())
                .orderByAsc("create_time").page(getPage).getRecords();
        return Result.success(new PageResult(empList.size(), empList));
    }

    /**
     * 修改员工信息
     * @param employee
     * @return
     */
    @Override
    @AutoFill(OperationType.UPDATE)
    public Result<String> updateEmployee(Employee employee) {
        if(!authorityCheck(EmployeeHolder.getUser().getUserName())){
            return Result.error("你没有权限");
        }
        boolean update = updateById(employee);
        if(!update){
            return Result.error("操作失败");
        }
        return Result.success();
    }

    @Override
    public Result updatePassword(PasswordEditDTO passwordEditDTO) {
        Employee old = getById(EmployeeHolder.getUser().getId());
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        if(!oldPassword.equals(old.getPassword())){
            return Result.error("旧密码错误");
        }
        old.setPassword(newPassword);
        updateById(old);
        return Result.success();
    }

    /**
     * 权限校验
     * @param username
     * @return
     */
    private boolean authorityCheck(String username) {
        if(!username.equals("admin")) {
            return false;
        }
        return true;
    }
}
