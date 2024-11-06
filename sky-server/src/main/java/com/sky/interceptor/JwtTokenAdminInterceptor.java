package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Resource
    private JwtProperties jwtProperties;

    /*
        校验jwt
        @param request
        @param response
        @param handler
        @return
        @throws Exception
     */
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源

        if(!(handler instanceof HandlerMethod)){
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        String token=request.getHeader(jwtProperties.getEmployeeTokenName());

        try{
            log.info("jwt token:{}",token);
            Claims claims= JwtUtil.parseJWT(jwtProperties.getEmployeeSecretKey(),token);
            Long empId=Long.valueOf(claims.get(JwtClaimsConstant.EMPLOYEE_ID).toString());
            log.info("empId:{}",empId);

            BaseContext.setCurrentId(empId);

            return true;
        }catch(Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
