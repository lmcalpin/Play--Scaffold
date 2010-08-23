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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.modules.scaffold.Scaffolding;
import play.modules.scaffold.ScaffoldingException;
import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;
import play.modules.scaffold.utils.Classes;
import play.modules.scaffold.utils.Filterable;
import play.modules.scaffold.utils.Strings;

/**
 * Entity holds information related to a domain model entity that we intend to
 * process. For each entity, we generate a controller and basic CRUD view
 * templates.
 * 
 * @author Lawrence McAlpin
 */
public class Entity {
	private String packageName;
	private String name;
	private String controllerName;
	private Class<?> modelType;
	private PersistenceStrategy persistenceStrategy;

	// currently, we only support single field primary keys
	private String idField;
	private Class<?> idClass;

	// form elements that we render in the view
	private List<FormElement> formElements;
	private ViewScaffoldingStrategy scaffoldingStrategy;

	public Entity(Class<?> clazz) {
		this.modelType = clazz;
		this.name = clazz.getSimpleName();
		this.packageName = Classes.getPackageName(clazz);
		Scaffolding scaffolding = clazz.getAnnotation(Scaffolding.class);
		String controllerOverride = null;
		if (scaffolding != null) {
			controllerOverride = scaffolding.controller();
		}
		this.controllerName = controllerOverride != null ? controllerOverride
				: Strings.pluralize(name);
		this.persistenceStrategy = PersistenceStrategy.forModel(clazz);
		if (persistenceStrategy == null)
			persistenceStrategy = PersistenceStrategy.forEmbeddable(clazz);
		if (persistenceStrategy == null) {
			throw new ScaffoldingException("Could not determine persistence strategy for " + clazz.getName());
		}
		this.scaffoldingStrategy = persistenceStrategy.getViewScaffoldingStrategy();
		this.formElements = new ArrayList<FormElement>();

		List<Field> fields = publicFields(clazz);
		for (Field field : fields) {
			if (persistenceStrategy.isId(field)) {
				idField = field.getName();
				idClass = field.getType();
			}
			addFormElement(field);
		}
	}

	public static List<Field> publicFields(Class<?> clazz) {
		final List<Field> fields = Classes.publicFields(clazz);
		Filterable.remove(fields, new Filterable<Field>() {

			@Override
			public boolean filter(Field input) {
				int modifiers = input.getModifiers();
				if (Modifier.isTransient(modifiers)
						|| Modifier.isStatic(modifiers))
					return true;
				return false;
			}

		});
		return fields;
	}

	private void addFormElement(Field field) {
		FormElement formElement = scaffoldingStrategy.render(field);
		if (formElement != null) {
			formElements.add(formElement);
		}
	}

	public List<FormElement> getFormElements() {
		return formElements;
	}

	public String getIdField() {
		return idField;
	}

	public Class<?> getModelType() {
		return modelType;
	}

	public Class<?> getIdClass() {
		return idClass;
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public PersistenceStrategy getPersistenceStrategy() {
		return persistenceStrategy;
	}

	public ViewScaffoldingStrategy getScaffoldingStrategy() {
		return scaffoldingStrategy;
	}

	public String getPackage() {
		return packageName;
	}

	public boolean getUsesPlayModelSupport() {
		return persistenceStrategy.getUsesPlayModelSupport();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Entity [packageName=" + packageName + ", name=" + name + "]";
	}

	private static Map<String, Entity> ENTITY_CACHE = new HashMap<String, Entity>();

	public static Entity from(Class<?> clazz) {
		Entity entity = ENTITY_CACHE.get(clazz.getName());
		if (entity == null) {
			entity = new Entity(clazz);
			ENTITY_CACHE.put(clazz.getName(), entity);
		}
		return entity;
	}
}
