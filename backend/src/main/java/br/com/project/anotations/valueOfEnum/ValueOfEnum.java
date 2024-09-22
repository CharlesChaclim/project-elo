package br.com.project.anotations.valueOfEnum;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();


    Class<?>[] groups() default {};

    String[] allowedValues() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "{precisa.ser.um.dos.seguintes.valores} {values}";
}

