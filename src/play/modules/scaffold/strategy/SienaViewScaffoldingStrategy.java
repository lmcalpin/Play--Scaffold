package play.modules.scaffold.strategy;

import java.lang.reflect.Field;

import play.modules.scaffold.form.FormElement;

public class SienaViewScaffoldingStrategy extends DefaultViewScaffoldingStrategy
{

	@Override
	public FormElement render(Field field)
	{
		FormElement defaultValue = super.render(field);
		return defaultValue;
	}

}
