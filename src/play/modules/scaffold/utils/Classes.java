package play.modules.scaffold.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Classes
{
	public static List<Field> fields(Class<?> clazz)
	{
		final List<Field> output = new ArrayList<Field>();
		foreachSuperclass(clazz, false, new Executable<Class<?>>()
		{
			public void execute(Class<?> superclass)
			{
				Field[] fields = superclass.getDeclaredFields();
				output.addAll(Arrays.asList(fields));
			}
		});
		return output;
	}

	public static List<String> superclasses(Class<?> clazz)
	{
		final List<String> output = new ArrayList<String>();
		foreachSuperclass(clazz, true, new Executable<Class<?>>()
		{
			public void execute(Class<?> superclass)
			{
				output.add(superclass.getName());
			}
		});
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

	private static void foreachSuperclass(Class<?> clazz, boolean skipCurrent, Executable<Class<?>> block)
	{
		Class<?> superclass = clazz;
		if (skipCurrent)
		{
			superclass = superclass.getSuperclass();
		}
		do
		{
			if (superclass != null)
			{
				block.execute(superclass);
			}
			superclass = superclass.getSuperclass();
		} while (superclass != null);
	}

}
