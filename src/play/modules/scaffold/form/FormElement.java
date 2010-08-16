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

public class FormElement {
	private String name;
	private Class<?> fieldType;
	private FormElementType formElementType;
	private List<String> options;
	private boolean required;

	public FormElement(Field field, FormElementType type) {
		this(field, type, null);
	}

	public FormElement(Field field, FormElementType formElementType, List<String> options) {
		this.name = field.getName();
		this.fieldType = field.getType();
		this.formElementType = formElementType;
		this.options = options;
	}

	public FormElement(FormElement copy, FormElementType formElementType) {
		this.name = copy.name;
		this.fieldType = copy.fieldType;
		this.options = copy.options;
		this.formElementType = formElementType;
	}

	public FormElement(FormElement copy, Class<?> fieldType, FormElementType formElementType) {
		this.name = copy.name;
		this.fieldType = fieldType;
		this.options = copy.options;
		this.formElementType = formElementType;
	}

	public String getName() {
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

	public boolean isHidden() {
		return formElementType == FormElementType.HIDDEN;
	}

	public boolean isPassword() {
		return formElementType == FormElementType.PASSWORD;
	}

	public boolean isTextArea() {
		return formElementType == FormElementType.TEXTAREA;
	}

	public boolean isManyToOneRelation() {
		return formElementType == FormElementType.RELATION_SINGLE;
	}

	public boolean isManyToManyRelation() {
		return formElementType == FormElementType.RELATION_MANY;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<String> getOptions() {
		return options;
	}
}
