package com.xuecheng.base.exception;

/**
 * @author Linzkr
 * @description: TODO  自定义异常处理
 * @date 2023/1/15 11:40
 */
public class XueChengException extends RuntimeException{
    private String errMessage;

    public XueChengException() {
        super();
    }

    public XueChengException(String errMessage) {
        super(errMessage);
        this.errMessage=errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage){
        throw new XueChengException(errMessage);
    }
    public static void cast(CommonError commonError){
        throw new XueChengException(commonError.getErrMessage());
    }
}
