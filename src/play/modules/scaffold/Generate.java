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
package play.modules.scaffold;

import java.io.File;
import java.util.List;

import play.Logger;
import play.Play;
import play.modules.scaffold.entity.Entity;
import play.modules.scaffold.entity.ScaffoldGenerator;

/**
 * Processes the scaffold:gen command.  This command generates the scaffolding
 * for a project, creating a rudimentary Application.index template, layout, and
 * basic CRUD screens.
 * 
 * @author Lawrence McAlpin
 */
public class Generate
{
	public static void main(String[] args) throws Exception
	{
		// initialize Play!
		File root = new File(System.getProperty("application.path"));
		Play.init(root, System.getProperty("play.id", ""));
		Thread.currentThread().setContextClassLoader(Play.classloader);
		
		boolean forceOverwrite = false;
		for (String arg : args)
		{
			if (arg.startsWith("--"))
			{
				if (arg.equalsIgnoreCase("--overwrite"))
				{
					Logger.info("--overwrite: We will force overwrite target files");
					forceOverwrite = true;
				}
			}
		}
		
		// Locate domain model classes that we can process.
		// Currently, we only support classes that extend the 
		// play.db.jpa.Model or siena.Model classes. 
		List<Class> classes = Play.classloader.getAllClasses();
		ScaffoldGenerator generator = new ScaffoldGenerator();
		generator.setForceOverwrite(forceOverwrite);
		for (Class clazz : classes)
		{
			// If this model is of a supported type, queue it up
			// so the ScaffoldGenerator will create its controller
			// and views.
			if (Entity.type(clazz) != null)
			{
				Entity entity = new Entity(clazz);
				generator.addEntity(entity);
			}
		}
		generator.generate();
	}
}
