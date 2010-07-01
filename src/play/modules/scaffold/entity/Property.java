/**
 *
 * Copyright 2010, Lawrence McAlpin.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package play.modules.scaffold.entity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import play.modules.scaffold.utils.Enums;

public class Property
{
	public static enum Type
	{
		TEXT, CHECKBOX, SELECT, DATE
	};

	public String name;
	public Type type;
	public List<String> options;

	public Property(Field field)
	{
		this.name = field.getName();
		Class<?> classType = field.getType();
		if (classType.equals(Boolean.class) || classType.equals(boolean.class))
		{
			this.type = Type.CHECKBOX;
		} else if (classType.equals(Date.class)) {
			this.type = Type.DATE;
		} else if (classType.isEnum())
		{
			this.type = Type.SELECT;
			Class<Enum> enumClass = (Class<Enum>)classType;
			this.options = Enums.asList(Enums.values(enumClass));
		} else
		{
			this.type = Type.TEXT;
		}
	}

	public boolean isCheckbox()
	{
		return type == Type.CHECKBOX;
	}
	
	public boolean isSelect()
	{
		return type == Type.SELECT;
	}
	
	public boolean isDate()
	{
		return type == Type.DATE;
	}
	
	public boolean isText()
	{
		return type == Type.TEXT;
	}
	
	public List<String> getOptions()
	{
		return options;
	}
}
