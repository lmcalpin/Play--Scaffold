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
import java.util.List;

import play.modules.scaffold.strategy.JpaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.SienaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;
import play.modules.scaffold.utils.Classes;
import play.modules.scaffold.utils.Fields;

public enum ModelType {
	PLAY_JPA, PURE_JPA, SIENA;

	public ViewScaffoldingStrategy getViewScaffoldingStrategy() {
		switch (this) {
		case PLAY_JPA:
			return new JpaViewScaffoldingStrategy();
		case PURE_JPA:
			return new JpaViewScaffoldingStrategy();
		case SIENA:
			return new SienaViewScaffoldingStrategy();
		}
		return null;
	}
	
	public boolean isId(Field field) {
		List<String> annotations = Fields.annotations(field);
		switch (this) {
		case PLAY_JPA:
			return (annotations.contains("javax.persistence.Id"));
		case PURE_JPA:
			return (annotations.contains("javax.persistence.Id"));
		case SIENA:
			return (annotations.contains("siena.Id"));
		}
		return false;
		
	}

	// Determine whether this is a play.db.jpa.Model model or an alternative
	// model,
	// such as a siena.Model. Since we can't be sure that alternative model
	// support classes,
	// such as siena.Model, are available on the classpath, we determine all
	// superclasses
	// for our model, and compile a List of class names that our model is
	// descended from.
	// Then, we simply see if the class name for our database support class is
	// in that list.
	public static ModelType forClass(Class<?> clazz) {
		List<String> superclasses = Classes.superclasses(clazz);
		List<String> annotations = Classes.annotations(clazz);
		if (superclasses.contains("play.db.jpa.Model")) {
			return ModelType.PLAY_JPA;
		}
		if (annotations.contains("javax.persistence.Entity")) {
			return ModelType.PURE_JPA;
		}
		if (superclasses.contains("siena.Model")) {
			return ModelType.SIENA;
		}
		// unsupported model
		return null;
	}

	public boolean getUsesPlayModelSupport() {
		if (this == ModelType.PURE_JPA)
			return false;
		return true;
	}
}
