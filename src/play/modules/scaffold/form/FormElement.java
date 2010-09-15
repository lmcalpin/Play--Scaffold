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
package play.modules.scaffold.form;

import java.lang.reflect.Field;
import java.util.List;

import play.modules.scaffold.entity.Entity;

public class FormElement {
	private String name;
	private Class<?> fieldType;
	private FormElementType formElementType;
	private List<String> options;
	private boolean required;
	private boolean multiple;
	private Entity fieldAsEntity;
	private FormElement parent;
	
	public FormElement(Field field, FormElementType type) {
		this(field, type, null);
	}

	public FormElement(Field field, FormElementType formElementType, List<String> options) {
		this(field.getName(), field.getType(), formElementType, options);
	}

	public FormElement(FormElement copy, FormElementType formElementType) {
		this(copy, copy.fieldType, formElementType);
	}

	public FormElement(FormElement copy, Class<?> fieldType, FormElementType formElementType) {
		this(copy.name, fieldType, formElementType, copy.options);
	}

	public FormElement(String fieldName, Class<?> fieldType, FormElementType formElementType, List<String> options) {
		this.name = fieldName;
		this.fieldType = fieldType;
		this.options = options;
		this.formElementType = formElementType;
		if (formElementType == FormElementType.EMBEDDED) {
			this.fieldAsEntity = Entity.from(fieldType);
			List<FormElement> childFormElements = this.fieldAsEntity.getFormElements();
			for (FormElement childFormElement : childFormElements) {
				childFormElement.setParent(this);
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path(".");
	}
	
	public String path(String separator) {
		if (parent != null) {
			// find top of parent chain
			FormElement topMostParent = parent;
			while (topMostParent.getParent() != null) {
				topMostParent = topMostParent.getParent();
			}
			// now go back down and prepend each element's name
			// to construct a dot notation reference to this field
			StringBuilder sb = new StringBuilder();
			while (topMostParent != null) {
				sb.append(topMostParent.getName());
				sb.append(separator);
				topMostParent = topMostParent.getParent();
			}
			sb.append(name);
			return sb.toString();
		}
		return name;
	}
	
	public Class<?> getFieldType() {
		return fieldType;
	}

	public FormElementType getType() {
		return formElementType;
	}

	public boolean isCheckbox() {
		return formElementType == FormElementType.CHECKBOX;
	}

	public boolean isSelect() {
		return formElementType == FormElementType.SELECT;
	}

	public boolean isDate() {
		return formElementType == FormElementType.DATE;
	}

	public boolean isText() {
		return formElementType == FormElementType.TEXT;
	}

	public boolean isList() {
		return formElementType == FormElementType.LIST;
	}

	public boolean isHidden() {
		return formElementType == FormElementType.HIDDEN;
	}

	public boolean isPassword() {
		return formElementType == FormElementType.PASSWORD;
	}

	public boolean isTextArea() {
		return formElementType == FormElementType.TEXTAREA;
	}

	public boolean isRelation() {
		return formElementType == FormElementType.RELATION;
	}
	
	public boolean isEmbedded() {
		return formElementType == FormElementType.EMBEDDED;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	
	public Entity getFieldAsEntity() {
		return fieldAsEntity;
	}

	public FormElement acceptMultiple() {
		this.multiple = true;
		return this;
	}

	public FormElement getParent() {
		return parent;
	}

	public void setParent(FormElement parent) {
		this.parent = parent;
	}

	public List<String> getOptions() {
		return options;
	}
	
	public String toString() {
		return name + " as " + fieldType.getName();
	}
}
