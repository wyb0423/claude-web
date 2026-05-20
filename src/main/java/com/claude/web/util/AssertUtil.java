package com.claude.web.util;

import com.claude.web.base.CommonException;
import com.claude.web.constant.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @Description 断言异常输出处理
 * @Author LiuXin
 * @Date 2023-7-26
 */
@Slf4j
public abstract class AssertUtil {

    /**
     * 判空
     */
    public static void notNull(Object obj, ReturnCode returnCode) {
        notNull(obj, returnCode.getCode(), returnCode.getText());
    }

    /**
     * 判空
     */
    public static void notNull(Object obj, Integer code, String message) {
        if (obj == null) {
            log.error(message);
            throw new CommonException(code, message);
        }
    }

    /**
     * 集合判空
     */
    public static void notEmpty(@Nullable Collection<?> collection, Integer code, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            log.error(message);
            throw new CommonException(code, message);
        }
    }

    /**
     * 主键判空
     */
    public static void primaryNotNull(Object obj, Integer code, String message) {
        if (obj == null) {
            log.error(message);
            throw new CommonException(code, message);
        }
    }
}
