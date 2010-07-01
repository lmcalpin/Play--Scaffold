package play.modules.scaffold.utils;

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
}
