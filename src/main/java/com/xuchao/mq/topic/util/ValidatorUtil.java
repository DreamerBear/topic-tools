package com.xuchao.mq.topic.util;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 参数校验器工具类
 *
 * @author : xuchao
 * @date : 2019/3/1 10:03
 */
public final class ValidatorUtil {
    private static final Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
    private static final String EMPTY_STRING = "";

    private ValidatorUtil() {
    }

    /**
     * 如果命中校验错误则返回错误信息
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String returnAnyMessageIfError(T object, Class... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);
        if (constraintViolationSet == null || constraintViolationSet.isEmpty()) {
            return EMPTY_STRING;
        }

        return String.join(",",
                constraintViolationSet.stream()
                        .map(constraintViolation -> String.format("%s(%s)", constraintViolation.getMessage(), constraintViolation.getPropertyPath()))
                        .collect(Collectors.toList())
        );

    }

    /**
     * 如果命中校验错误则抛出异常
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> void throwExceptionIfError(T object, Class... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);
        if (constraintViolationSet != null && !constraintViolationSet.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join(",",
                            constraintViolationSet.stream()
                                    .map(constraintViolation -> String.format("%s(%s)", constraintViolation.getMessage(), constraintViolation.getPropertyPath()))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }
}
