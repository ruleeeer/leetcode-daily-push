package cn.ruleeeer.dailycode.advice;

import cn.ruleeeer.dailycode.bean.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ruleeeer
 * @date 2021/10/4 16:26
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public Result constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return Result.<String>builder().success(false).msg(collect.toString()).build();
    }


}
