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

import play.modules.scaffold.Scaffolding;
import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;
import play.modules.scaffold.utils.Classes;
import play.modules.scaffold.utils.Filterable;
import play.modules.scaffold.utils.Strings;

/**
 * Entity holds information related to a domain model entity that we intend to process.
 * For each entity, we generate a controller and basic CRUD view templates.
 * 
 * @author Lawrence McAlpin
 */
public class Entity
{
	String packageName;
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
		this.packageName = Classes.getPackageName(clazz);
		Scaffolding scaffolding = clazz.getAnnotation(Scaffolding.class);
		String controllerOverride = null;
		if (scaffolding != null)
		{
			controllerOverride = scaffolding.controller();
		}
		this.controllerName = controllerOverride != null ? controllerOverride : Strings.pluralize(name);
		this.controllerTemplatePath = "app" + File.separator + "views" + File.separator + controllerName + File.separator;
		this.viewTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "views" + File.separator + "Entity" +  File.separator;
		this.modelType = ModelType.forClass(clazz);
		this.scaffoldingStrategy = modelType.getViewScaffoldingStrategy();
		this.formElements = new ArrayList<FormElement>();
		
		List<Field> fields = publicFields(clazz);
		for (Field field : fields)
		{
			addFormElement(field);
		}
	}
	
	public static List<Field> publicFields(Class<?> clazz)
	{
		final List<Field> fields = Classes.publicFields(clazz);
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
	
	public String getPackage()
	{
		return packageName;
	}
	
	public boolean getUsesPlayModelSupport()
	{
		return modelType.getUsesPlayModelSupport();
	}
}
