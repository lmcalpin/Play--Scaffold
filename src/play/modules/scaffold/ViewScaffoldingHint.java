package play.modules.scaffold;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations intended for placement on an entity to allow you to override some
 * defaults, such as the name of the controller generated for an entity.
 * 
 * @author Lawrence
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ViewScaffoldingHint {
	public boolean display() default true;
}
