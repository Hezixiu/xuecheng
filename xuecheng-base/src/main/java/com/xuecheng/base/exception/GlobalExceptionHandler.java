package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO  全局异常处理
 * @date 2023/1/15 11:51
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//    处理XueChengPlusException异常  此类异常是主动抛出的异常（可预知的异常）
    @ResponseBody  //返回json
    @ExceptionHandler(XueChengException.class)  //此方法捕获XueChengException异常(可预知的异常)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //返回响应状态码 500
    public RestErrorResponse handlerXueChengException (XueChengException e){
        log.error("捕获异常：{}",e.getErrMessage());
        e.printStackTrace();
        String errMessage = e.getErrMessage();
        return new RestErrorResponse(errMessage);
    }
//    处理未知异常
    @ResponseBody  //返回json
    @ExceptionHandler(Exception.class)  //此方法捕获XueChengException异常(可预知的异常)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //返回响应状态码 500
    public RestErrorResponse handlerUnKnowException (Exception e){
        log.error("捕获异常：{}",e.getMessage());
        e.printStackTrace();
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

//    处理JSR303校验的异常
    @ResponseBody  //返回json
    @ExceptionHandler(MethodArgumentNotValidException.class)  //此方法捕获XueChengException异常(可预知的异常)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //返回响应状态码 500
    public RestErrorResponse handlerMethodArgumentException (MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuffer errorMessage =new StringBuffer();
        fieldErrors.forEach(error->{
            errorMessage.append(error.getDefaultMessage()).append(",");
        });

        return new RestErrorResponse(errorMessage.toString());
    }

}
