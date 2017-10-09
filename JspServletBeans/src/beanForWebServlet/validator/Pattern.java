package beanForWebServlet.validator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Pattern {
	String pattern();
	String msg();
	ValidateOrder order() default ValidateOrder.FIRST;

}
