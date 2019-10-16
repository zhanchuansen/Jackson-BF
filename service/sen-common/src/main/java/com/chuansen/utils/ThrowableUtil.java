package com.chuansen.utils;

import com.chuansen.exception.BadRequestException;
import org.hibernate.exception.ConstraintViolationException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具
 */
public class ThrowableUtil {

    /**
     * 获取堆栈信息
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    public static void throwForeignKeyException(Throwable e, String msg){
        Throwable t = e.getCause();
        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        if (t instanceof ConstraintViolationException) {
            throw new BadRequestException(msg);
        }
        throw new BadRequestException("删除失败：" + t.getMessage());
    }
}
