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

import java.util.List;

public class FormElement {
	private String name;
	private FormElementType type;
	private List<String> options;

	public FormElement(String name, FormElementType type) {
		this(name, type, null);
	}

	public FormElement(String name, FormElementType type, List<String> options) {
		this.name = name;
		this.type = type;
		this.options = options;
	}

	public FormElement(FormElement copy, FormElementType type) {
		this.name = copy.name;
		this.options = copy.options;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public FormElementType getType() {
		return type;
	}

	public boolean isCheckbox() {
		return type == FormElementType.CHECKBOX;
	}

	public boolean isSelect() {
		return type == FormElementType.SELECT;
	}

	public boolean isDate() {
		return type == FormElementType.DATE;
	}

	public boolean isText() {
		return type == FormElementType.TEXT;
	}

	public boolean isHidden() {
		return type == FormElementType.HIDDEN;
	}

	public boolean isPassword() {
		return type == FormElementType.PASSWORD;
	}

	public boolean isTextArea() {
		return type == FormElementType.TEXTAREA;
	}

	public List<String> getOptions() {
		return options;
	}
}
