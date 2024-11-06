package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLClientInfoException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
        捕获业务异常
        @param ex
        @return
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(BaseException e) {
        log.error("异常信息：{}",e.getMessage());
        return Result.error(e.getMessage());
    }

    /*
        处理sql异常
        @param
        @return
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        //log.error("sql"+e.getMessage());
        String message = e.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split=message.split(" ");
            String msg= split[2]+ MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKONWN_ERROR);
        }

    }
}
