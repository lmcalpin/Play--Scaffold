package play.modules.scaffold.strategy;

import java.lang.reflect.Field;

import play.modules.scaffold.form.FormElement;

public interface ViewScaffoldingStrategy
{
	public FormElement render(Field field);
}
