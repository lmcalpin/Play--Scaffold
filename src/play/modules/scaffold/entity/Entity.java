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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import play.templates.JavaExtensions;

/**
 * Entity holds information related to a domain model entity that we intend to process.
 * For each entity, we generate a controller and basic CRUD view templates.
 * 
 * @author Lawrence McAlpin
 */
public class Entity
{
	public String name;
	public String controllerName;
	public String controllerTemplatePath;
	public String viewTemplatePath;
	public List<Property> properties;

	public Entity()
	{
	}

	public Entity(Class<?> clazz)
	{
		this.name = clazz.getSimpleName();
		this.controllerName = name + JavaExtensions.pluralize(2);
		this.controllerTemplatePath = "app" + File.separator + "views" + File.separator + controllerName + File.separator;
		this.viewTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "views" + File.separator + "Entity" +  File.separator;
		Field[] fields = clazz.getDeclaredFields();
		properties = new ArrayList<Property>();
		for (Field field : fields)
		{
			addProperty(field);
		}
	}

	private void addProperty(Field idField)
	{
		Property property = new Property(idField);
		properties.add(property);
	}

	public List<Property> getProperties()
	{
		return properties;
	}

	public String getControllerName()
	{
		return controllerName;
	}

	public String getName()
	{
		return name;
	}
	
	/**
	 * TODO: REMOVE THIS HACK. We need to generate Groovy templates from our Groovy templates, 
	 * but the ${ tag is being interpreted even when it appears in a string.  ${"${".raw()} 
	 * doesn't appear to work, though we haven't had the same problem with other tags.  For now,
	 * we just get things to work by putting "${entity.expressionOpen}...}" in the templates 
	 * to generate the expressions that should appear in the target template. 
	 */
	public String getExpressionOpen()
	{
		return "${";
	}
}
