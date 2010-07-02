package play.modules.scaffold.strategy;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Enums;

public class DefaultViewScaffoldingStrategy implements ViewScaffoldingStrategy
{

	public FormElement render(Field field)
	{
		String name = field.getName();
		FormElementType type;
		List<String> options = null;
		Class<?> classType = field.getType();
		if (classType.equals(Boolean.class) || classType.equals(boolean.class))
		{
			type = FormElementType.CHECKBOX;
		} else if (classType.equals(Date.class)) {
			type = FormElementType.DATE;
		} else if (classType.isEnum())
		{
			type = FormElementType.SELECT;
			Class<Enum> enumClass = (Class<Enum>)classType;
			options = Enums.asList(Enums.values(enumClass));
		} else
		{
			type = FormElementType.TEXT;
		}
		return new FormElement(name, type, options);
	}

}
