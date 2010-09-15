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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;

import play.modules.scaffold.form.FormElement;
import play.modules.scaffold.form.FormElementType;
import play.modules.scaffold.utils.Fields;

public class JpaViewScaffoldingStrategy extends DefaultViewScaffoldingStrategy {

	@Override
	public FormElement render(Field field) {
		FormElement defaultValue = super.render(field);
		if (defaultValue == null)
			return null;
		// get the list of annotations
		List<Class<? extends Annotation>> annotations = Fields
				.annotations(field);
		// also get the list of the names of the annotations -- this is a little
		// redundant, but the reason we do this is because different versions of
		// Play! support different versions of JPA, and we can't know at compile
		// time which classes will be available... particularly, we currently
		// support Play! 1.0 which uses JPA 1 and therefore won't have the
		// ElementCollection annotation, while Play! 1.1, which uses JPA 2,
		// does.
		List<String> annotationNames = Fields.annotationNames(field);
		if (defaultValue.getType() == FormElementType.TEXT
				&& annotations.contains(javax.persistence.Lob.class)) {
			return new FormElement(defaultValue, FormElementType.TEXTAREA);
		}
		if (annotations.contains(javax.persistence.Id.class)) {
			return new FormElement(defaultValue, FormElementType.HIDDEN);
		} else if (annotations.contains(javax.persistence.Embedded.class)) {
			return new FormElement(defaultValue, FormElementType.EMBEDDED);
		} else if (annotations.contains(javax.persistence.OneToOne.class)) {
			OneToOne ann = field.getAnnotation(OneToOne.class);
			if (!StringUtils.isEmpty(ann.mappedBy())) {
				return null;
			}
			return new FormElement(defaultValue, FormElementType.RELATION);
		} else if (annotations.contains(javax.persistence.ManyToOne.class)) {
			return new FormElement(defaultValue, FormElementType.RELATION);
		}
		if (Collection.class.isAssignableFrom(field.getType())) {
			Class<?> parameterizedType = (Class<?>) ((ParameterizedType) field
					.getGenericType()).getActualTypeArguments()[0];
			if (annotationNames.contains("javax.persistence.ElementCollection")
					|| annotationNames
							.contains("org.hibernate.annotations.CollectionOfElements")) {
				return new FormElement(defaultValue, parameterizedType,
						FormElementType.LIST);
			}
			if (annotations.contains(javax.persistence.OneToMany.class)) {
				OneToMany ann = field.getAnnotation(OneToMany.class);
				if (!StringUtils.isEmpty(ann.mappedBy())) {
					return null;
				}
				return new FormElement(defaultValue, parameterizedType,
						FormElementType.RELATION).acceptMultiple();
			}
			if (annotations.contains(javax.persistence.ManyToMany.class)) {
				ManyToMany ann = field.getAnnotation(ManyToMany.class);
				if (!StringUtils.isEmpty(ann.mappedBy())) {
					return null;
				}
				return new FormElement(defaultValue, parameterizedType,
						FormElementType.RELATION).acceptMultiple();
			}
		}
		return defaultValue;
	}
}
