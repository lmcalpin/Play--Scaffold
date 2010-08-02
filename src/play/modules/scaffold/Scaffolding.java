package play.modules.scaffold;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotations intended for placement on an entity to allow you to override some
 * defaults, such as the name of the controller generated for an entity.
 * 
 * @author Lawrence
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Scaffolding {
	public String controller();
}
