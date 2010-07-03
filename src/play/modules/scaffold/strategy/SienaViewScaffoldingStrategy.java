package play.modules.scaffold.strategy;

import java.lang.reflect.Field;
import java.util.List;

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Classes;

public class SienaViewScaffoldingStrategy extends DefaultViewScaffoldingStrategy
{

	@Override
	public FormElement render(Field field)
	{
		FormElement defaultValue = super.render(field);
		List<String> annotations = Classes.annotations(field);
		if (defaultValue.getType() == FormElementType.TEXT && annotations.contains("siena.Text"))
		{
			return new FormElement(defaultValue, FormElementType.TEXTAREA);
		} if (annotations.contains("siena.Id"))
		{
			return new FormElement(defaultValue, FormElementType.HIDDEN);
		}
		return defaultValue;
	}

}
