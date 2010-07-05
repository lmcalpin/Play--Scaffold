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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;
import play.libs.IO;
import play.templates.Template;
import play.templates.TemplateCompiler;
import play.vfs.VirtualFile;

/**
 * Creates boilerplate controllers and views for basic CRUD functions. This is useful if you don't
 * want to use the CRUD plugin, because you intend to customize the output, but would still like to
 * have basic CRUD functionality built out automagically.
 * 
 * The scaffolding also creates a basic Application.index page which lists the controllers, as well
 * as a default stylesheet and layout.
 * 
 * @author Lawrence McAlpin
 */
public class ScaffoldGenerator
{
	private static final String CREATE_HTML = "create.html";
	private static final String SHOW_HTML = "show.html";
	private static final String EDIT_HTML = "edit.html";
	private static final String LIST_HTML = "index.html";

	private static final List<String> VIEW_HTMLS = Arrays.asList(CREATE_HTML, SHOW_HTML, EDIT_HTML, LIST_HTML);

	private List<Entity> entities = new ArrayList<Entity>();
	private boolean forceOverwrite;

	public ScaffoldGenerator()
	{

	}

	public void addEntity(Entity entity)
	{
		this.entities.add(entity);
	}

	public boolean isForceOverwrite()
	{
		return forceOverwrite;
	}

	public void setForceOverwrite(boolean forceOverwrite)
	{
		this.forceOverwrite = forceOverwrite;
	}

	public void generate()
	{
		// copy over the main.html layout
		generateLayout();

		// create Application.index
		generateHome();

		for (Entity entity : entities)
		{
			generate(entity);
		}
	}

	public void generate(Entity entity)
	{
		// determine the name of the controller
		// by convention, it is the plural of the entity name
		generateController(entity);

		// determine the name of the controller
		// by convention, it is the plural of the entity name
		generateViews(entity);
	}

	private void generateController(Entity entity)
	{
		Class controller = Play.classloader.getClassIgnoreCase(entity.controllerName);
		if (controller != null)
		{
			Logger.info("Skipping controller: " + entity.controllerName);
			return;
		}

		Logger.info("Generating controller: " + entity.controllerName);
		String controllerSourcePath = "app" + File.separator + "controllers" + File.separator + entity.controllerName + ".java";
		String controllerTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "controllers" + File.separator + "controller." + entity.modelType.name().toLowerCase() + ".html";

		generateForEntity(controllerSourcePath, controllerTemplatePath, entity);
	}

	private void generateViews(Entity entity)
	{
		// create the view folder if necessary
		Logger.info("Generating views for " + entity.controllerName);
		File viewPathDirectory = new File(entity.controllerTemplatePath);
		if (!viewPathDirectory.exists())
		{
			viewPathDirectory.mkdir();
		}
		for (String view : VIEW_HTMLS)
		{
			generateForEntity(entity.controllerTemplatePath + view, entity.viewTemplatePath + view, entity);
		}
	}

	// simply copy over the layout file -- nothing to generate (yet)
	// TODO: it would be nice to auto generate the application name which we can easily get from the
	// properties file
	private void generateLayout()
	{
		Logger.info("Generating layout: main.html");
		String targetPath = "app" + File.separator + "views" + File.separator + "main.html";
		String layoutTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "main.html";
		VirtualFile templateFile = Play.getVirtualFile(layoutTemplatePath);
		VirtualFile targetFile = Play.getVirtualFile(targetPath);
		String templateLayout = templateFile.contentAsString();
		if (targetFile == null || !targetFile.exists() || forceOverwrite)
		{
			try {
				File fileToCreate = Play.getFile(targetPath);
				IO.writeContent(templateLayout, fileToCreate);
			} catch (IOException e)
			{
				Logger.error(e, "IO Exception");
			}
		} else
		{
			Logger.info("! Skipping " + targetPath);
		}
	}

	private void generateHome()
	{
		String homeSourcePath = "app" + File.separator + "views" + File.separator + "Application" + File.separator + "index.html";
		String homeTemplatePath = "app" + File.separator + "views" + File.separator + "scaffold" + File.separator + "views" + File.separator + "Application" + File.separator + "index.html";
		Map<String, Object> templateArgs = new HashMap<String, Object>();
		templateArgs.put("entities", entities);
		templateArgs.put("tagOpen", "#{"); // TODO: HACK, need to get rid of these
		generate(homeSourcePath, homeTemplatePath, templateArgs);
	}

	private void generateForEntity(String sourcePath, String templatePath, Entity entity)
	{
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("entity", entity);
		generate(sourcePath, templatePath, args);
	}

	private void generate(String targetPath, String templatePath, Map<String, Object> args)
	{
		// this represents the source file that we generate
		VirtualFile targetFile = Play.getVirtualFile(targetPath);
		// this represents the template we use to generate the source code
		VirtualFile templateFile = Play.getVirtualFile(templatePath);
		// if the source code already exists, skip it - we do not overwrite normally
		if (targetFile == null || !targetFile.exists() || forceOverwrite)
		{
			File fileToCreate = Play.getFile(targetPath);
			if (templateFile == null || !templateFile.exists())
			{
				Logger.error("!! ERROR: Can't find scaffold template -- " + templatePath);
			}
			invokeTemplate(templateFile, fileToCreate, args);
			Logger.info("+ " + targetPath);
		} else
		{
			Logger.info("! Skipping " + targetPath);
		}
	}

	public static void invokeTemplate(VirtualFile templateFile, File targetFile, Map<String, Object> args)
	{
		try
		{
			Template template = TemplateCompiler.compile(templateFile);
			String output = template.render(args);
			IO.writeContent(output, targetFile);
		} catch (IOException e)
		{
			Logger.warn(e, "! Failed to generate output successfully: " + e.getMessage());
		} catch (Throwable t) {
			Logger.fatal(t, "! Unexpected error processing template", args);
			System.exit(-1);
		}
	}
}
