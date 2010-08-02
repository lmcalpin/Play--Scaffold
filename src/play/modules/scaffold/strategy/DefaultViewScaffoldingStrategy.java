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
package play.modules.scaffold.strategy;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import play.modules.scaffold.ViewScaffoldingHint;
import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Enums;

public class DefaultViewScaffoldingStrategy implements ViewScaffoldingStrategy {

	public FormElement render(Field field) {
		String name = field.getName();
		FormElementType type;
		List<String> options = null;
		Class<?> classType = field.getType();
		if (classType.equals(Boolean.class) || classType.equals(boolean.class)) {
			type = FormElementType.CHECKBOX;
		} else if (classType.equals(Date.class)) {
			type = FormElementType.DATE;
		} else if (classType.isEnum()) {
			type = FormElementType.SELECT;
			Class<Enum> enumClass = (Class<Enum>) classType;
			options = Enums.list(Enums.values(enumClass));
		} else {
			type = FormElementType.TEXT;
		}
		ViewScaffoldingHint formHint = field.getAnnotation(ViewScaffoldingHint.class);
		if (formHint != null && !formHint.display())
			return null;
		return new FormElement(name, type, options);
	}

}
