package play.modules.scaffold.strategy;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Classes;
import play.modules.scaffold.utils.Enums;

public class JpaViewScaffoldingStrategy extends DefaultViewScaffoldingStrategy
{

	@Override
	public FormElement render(Field field)
	{
		FormElement defaultValue = super.render(field);
		List<String> annotations = Classes.annotations(field);
		if (defaultValue.getType() == FormElementType.TEXT && annotations.contains("javax.persistence.Lob"))
		{
			return new FormElement(defaultValue, FormElementType.TEXTAREA);
		}
		return defaultValue;
	}
}
