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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;
import play.modules.scaffold.utils.Classes;
import play.modules.scaffold.utils.Filterable;
import play.templates.JavaExtensions;

/**
 * Entity holds information related to a domain model entity that we intend to process.
 * For each entity, we generate a controller and basic CRUD view templates.
 * 
 * @author Lawrence McAlpin
 */
public class Entity
{
	String name;
	String controllerName;
	String controllerTemplatePath;
	String viewTemplatePath;
	ModelType modelType;
	
	// form elements that we render in the view
	private List<FormElement> formElements;
	private ViewScaffoldingStrategy scaffoldingStrategy;

	public Entity(Class<?> clazz)
	{
		this.name = clazz.getSimpleName();
		this.controllerName = name + JavaExtensions.pluralize(2);
		this.controllerTemplatePath = "app" + File.separator + "views" + File.separator + controllerName + File.separator;
		this.viewTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "views" + File.separator + "Entity" +  File.separator;
		this.modelType = ModelType.forClass(clazz);
		this.scaffoldingStrategy = modelType.getViewScaffoldingStrategy();
		List<Field> fields = fields(clazz);
		formElements = new ArrayList<FormElement>();
		for (Field field : fields)
		{
			addFormElement(field);
		}
	}
	
	public static List<Field> fields(Class<?> clazz)
	{
		final List<Field> fields = Classes.fields(clazz);
		Filterable.remove(fields, new Filterable<Field>() {

			@Override
			public boolean filter(Field input)
			{
				int modifiers = input.getModifiers();
				if (Modifier.isTransient(modifiers) ||
					Modifier.isStatic(modifiers))
					return true;
				return false;
			}
			
		});
		return fields;
	}
	
	private void addFormElement(Field field)
	{
		FormElement FormElement = scaffoldingStrategy.render(field);
		formElements.add(FormElement);
	}

	public List<FormElement> getFormElements()
	{
		return formElements;
	}

	public String getControllerName()
	{
		return controllerName;
	}

	public String getName()
	{
		return name;
	}
}
