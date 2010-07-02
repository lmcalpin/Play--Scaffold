package play.modules.scaffold.form;

import java.util.List;

public class FormElement
{
	public String name;
	public FormElementType type;
	public List<String> options;

	public FormElement(String name, FormElementType type)
	{
		this(name, type, null);
	}

	public FormElement(String name, FormElementType type, List<String> options)
	{
		this.name = name;
		this.type = type;
		this.options = options;
	}

	public FormElement(FormElement copy, FormElementType type)
	{
		this.name = copy.name;
		this.options = copy.options;
		this.type = type;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public FormElementType getType()
	{
		return type;
	}

	public boolean isCheckbox()
	{
		return type == FormElementType.CHECKBOX;
	}
	
	public boolean isSelect()
	{
		return type == FormElementType.SELECT;
	}
	
	public boolean isDate()
	{
		return type == FormElementType.DATE;
	}
	
	public boolean isText()
	{
		return type == FormElementType.TEXT;
	}
	
	public boolean isTextArea()
	{
		return type == FormElementType.TEXTAREA;
	}
	
	public List<String> getOptions()
	{
		return options;
	}
}
