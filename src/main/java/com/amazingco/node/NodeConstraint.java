package com.amazingco.node;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = NodeConstraintValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface NodeConstraint {

    String message() default "Height of the Node should be higher then the height of the Parent Node";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
