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

import play.modules.scaffold.NoScaffolding;
import play.modules.scaffold.ViewScaffoldingOverride;
import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Enums;
import play.modules.scaffold.utils.Fields;

public class DefaultViewScaffoldingStrategy implements ViewScaffoldingStrategy {

    public FormElement render(Field field) {
        String name = field.getName();
        FormElementType type;
        List<String> options = null;
        Class<?> classType = field.getType();
        List<String> annotationNames = Fields.annotationNames(field);
        if (classType.equals(Boolean.class) || classType.equals(boolean.class)) {
            type = FormElementType.CHECKBOX;
        } else if (classType.equals(Date.class)) {
            type = FormElementType.DATE;
        } else if (classType.isEnum()) {
            type = FormElementType.SELECT;
            Class<Enum> enumClass = (Class<Enum>) classType;
            options = Enums.list(Enums.values(enumClass));
        } else if (annotationNames.contains("play.data.validation.Password")) {
            type = FormElementType.PASSWORD;
        } else {
            type = FormElementType.TEXT;
        }
        NoScaffolding formHint = field.getAnnotation(NoScaffolding.class);
        if (formHint != null)
            return null;
        ViewScaffoldingOverride formOverride = field.getAnnotation(ViewScaffoldingOverride.class);
        if (formOverride != null) {
            // user override for the FormElementType
            type = formOverride.type();
        }
        FormElement element = new FormElement(field, type, options);
        if (annotationNames.contains("play.data.validation.Required")) {
            element.setRequired(true);
        }
        return element;
    }

}
