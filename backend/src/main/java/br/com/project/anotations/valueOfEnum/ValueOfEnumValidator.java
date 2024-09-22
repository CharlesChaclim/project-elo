package br.com.project.anotations.valueOfEnum;

import br.com.project.util.MessageUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> allowedValues;

    @Override
    public void initialize(ValueOfEnum annotation) {
        List<String> acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());

        allowedValues = annotation.allowedValues().length == 0 ?
                acceptedValues : Arrays.asList(annotation.allowedValues());
    }


    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!allowedValues.contains(value.toString())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    MessageUtil.get("precisa.ser.um.dos.seguintes.valores") + " " + allowedValues
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}