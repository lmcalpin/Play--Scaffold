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

import play.modules.scaffold.strategy.JpaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.SienaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;

public enum ModelType
{
	PLAY_JPA,
	SIENA;
	
	public ViewScaffoldingStrategy getViewScaffoldingStrategy() 
	{
		switch(this)
		{
		case PLAY_JPA: return new JpaViewScaffoldingStrategy();
		case SIENA: return new SienaViewScaffoldingStrategy();
		}
		return null;
	}
}
