package com.sky.controller.admin;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.wechat.pay.contrib.apache.httpclient.util.RsaCryptoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.annotations.OpenAPI31;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name="员工相关")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @Resource
    private JwtProperties jwtProperties;

    /*
        登录
        @param employeeLoginDTO
        @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<EmployeeLoginVO> login( @RequestBody EmployeeLoginDTO employeeLoginDTO) throws  Exception {
        log.info("员工登录:{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);
        Map<String,Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.EMPLOYEE_ID, employee.getId());
        String token= JwtUtil.createJWT(
                jwtProperties.getEmployeeSecretKey(),
                jwtProperties.getEmployTtl(),
                claims
        );

        EmployeeLoginVO employeeLoginVO= EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        return Result.success(employeeLoginVO);
    }

    /*
        退出登录
        @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    @Operation(summary = "员工注册")
    @PostMapping("/register")
    public Result<String> EmployeeRegister(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /*
        @param EmployeePageQueryDTO
        @return
     */
    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    public Result<PageResult> EmployeePageResult(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult= employeeService.pageQuery(employeePageQueryDTO);
        //return null;
        return Result.success(pageResult);
    }

    @GetMapping("/setStatus/{status}")
    @Operation(summary = "员工启用禁用")
    public Result enableOrDisableEmployee(@PathVariable("status") Integer status,Long id) {

        employeeService.enableOrDisableEmployee(status,id);
        return Result.success();
    }


    @GetMapping("/{id}")
    @Operation(summary = "查询员工")
    public Result<Employee> getEmployeeById(@PathVariable("id") Long id) {
        Employee employee=employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    @PutMapping("")
    @Operation(summary = "更新员工数据")
    public Result updateEmployee(@RequestBody EmployeeDTO employee) {
        employeeService.updateEmployee(employee);
        return Result.success();
    }
}
