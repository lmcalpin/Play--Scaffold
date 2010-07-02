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

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;
import play.modules.scaffold.utils.Classes;
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
	
	// form elements that we render in the view
	public List<FormElement> formElements;
	public ModelType modelType;
	
	private ViewScaffoldingStrategy scaffoldingStrategy;

	public Entity(Class<?> clazz)
	{
		this.name = clazz.getSimpleName();
		this.controllerName = name + JavaExtensions.pluralize(2);
		this.controllerTemplatePath = "app" + File.separator + "views" + File.separator + controllerName + File.separator;
		this.viewTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "views" + File.separator + "Entity" +  File.separator;
		this.modelType = type(clazz);
		this.scaffoldingStrategy = modelType.getViewScaffoldingStrategy();
		Field[] fields = clazz.getDeclaredFields();
		formElements = new ArrayList<FormElement>();
		for (Field field : fields)
		{
			addFormElement(field);
		}
	}
	
	// Determine whether this is a play.db.jpa.Model model or an alternative model,
	// such as a siena.Model.  Since we can't be sure that alternative model support classes,
	// such as siena.Model, are available on the classpath, we determine all superclasses
	// for our model, and compile a List of class names that our model is descended from.
	// Then, we simply see if the class name for our database support class is in that list.
	public static ModelType type(Class<?> clazz)
	{
		if (Classes.superclasses(clazz).contains("play.db.jpa.Model"))
		{
			return ModelType.PLAY_JPA;
		}
		if (Classes.superclasses(clazz).contains("siena.Model"))
		{
			return ModelType.SIENA;
		}
		// unsupported model
		return null;
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
