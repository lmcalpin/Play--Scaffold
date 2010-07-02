package play.modules.scaffold.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Classes
{
	public static List<String> superclasses(Class<?> clazz)
	{
		List<String> output = new ArrayList<String>();
		Class<?> superclass = clazz;
		do {
			superclass = superclass.getSuperclass();
			if (superclass != null)
				output.add(superclass.getName());
		} while (superclass != null);
		return output;
	}
	
	public static List<String> annotations(Field field)
	{
		List<String> output = new ArrayList<String>();
		Annotation[] annotations = field.getAnnotations();
		for (Annotation ann : annotations)
		{
			output.add(ann.annotationType().getName());
		}
		return output;
	}
}
