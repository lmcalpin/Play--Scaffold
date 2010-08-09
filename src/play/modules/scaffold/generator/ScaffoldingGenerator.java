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
package play.modules.scaffold.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;
import play.libs.IO;
import play.modules.scaffold.entity.Entity;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * Creates boilerplate controllers and views for basic CRUD functions. This is
 * useful if you don't want to use the CRUD plugin, because you intend to
 * customize the output, but would still like to have basic CRUD functionality
 * built out automagically.
 * 
 * The scaffolding also creates a basic Application.index page which lists the
 * controllers, as well as a default stylesheet and layout.
 * 
 * @author Lawrence McAlpin
 */
public class ScaffoldingGenerator {
	private static final String CREATE_HTML = "create.html";
	private static final String SHOW_HTML = "show.html";
	private static final String EDIT_HTML = "edit.html";
	private static final String LIST_HTML = "index.html";

	private static final List<String> VIEW_HTMLS = Arrays.asList(CREATE_HTML,
			SHOW_HTML, EDIT_HTML, LIST_HTML);

	private List<Entity> entities = new ArrayList<Entity>();
	private boolean forceOverwrite;
	private boolean includeLayout;
	private boolean includeLogin;
	private String applicationName;

	public ScaffoldingGenerator() {
		applicationName = Play.configuration.getProperty("application.name",
				"Your New Application");
	}

	/**
	 * TemplateCompiler was a concrete class in Play 1.0. In Play 1.1, it became
	 * an abstract class to better support alternative template engines.
	 * GroovyTemplate is the class we use in Play 1.1.
	 * 
	 * Because we want to support BOTH Play 1.0 and Play 1.1, we use reflection
	 * to invoke the correct template compiler.
	 * 
	 * @param templateFile
	 * @param targetFile
	 * @param args
	 */
	public static void invokeTemplate(VirtualFile templateFile,
			File targetFile, Map<String, Object> args) {
		try {
			String version = Play.version;
			Object templateCompiler = null;
			Method compile = null;
			if (version.startsWith("1.0")) {
				Class<?> templateCompilerClass = Class
						.forName("play.templates.TemplateCompiler");
				if (templateCompilerClass != null) {
					compile = templateCompilerClass.getMethod("compile",
							VirtualFile.class);
				}
				templateCompiler = templateCompilerClass;
			} else {
				Class<?> templateCompilerClass = Class
						.forName("play.templates.GroovyTemplateCompiler");
				if (templateCompilerClass != null) {
					compile = templateCompilerClass.getMethod("compile",
							VirtualFile.class);
				}
				templateCompiler = templateCompilerClass.newInstance();
			}
			if (templateCompiler == null || templateCompiler == null) {
				Logger.error("Error looking up the template compiler method");
				System.exit(-1);
			}
			// Template template = TemplateCompiler.compile(templateFile);
			Template template = (Template) compile.invoke(templateCompiler,
					templateFile);
			String output = template.render(args);
			IO.writeContent(output, targetFile);
		} catch (IOException e) {
			Logger.warn(
					e,
					"! Failed to generate output successfully: "
							+ e.getMessage());
		} catch (Throwable t) {
			Logger.fatal(t, "! Unexpected error processing template", args);
			System.exit(-1);
		}
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void generate() {
		// copy over the main.html layout
		if (isIncludeLayout()) {
			generateLayout();

			// create Application.index
			generateHome();
		}

		if (includeLogin) {
			generateLogin();
		}

		for (Entity entity : entities) {
			generate(entity);
		}
	}

	public void generate(Entity entity) {
		// determine the name of the controller
		// by convention, it is the plural of the entity name
		generateController(entity);

		// determine the name of the controller
		// by convention, it is the plural of the entity name
		generateViewsForEntity(entity);
	}

	private void generate(String templatePath, String targetPath) {
		generate(templatePath, targetPath, new HashMap<String, Object>());
	}

	private void generate(String templatePath, String targetPath,
			Map<String, Object> templateArgs) {
		templateArgs.put("applicationName", applicationName);
		templateArgs.put("includeLogin", includeLogin);
		templateArgs.put("entities", entities);
		templateArgs.put("tagOpen", "#{");
		templateArgs.put("exprOpen", "${");
		templateArgs.put("actionOpen", "@{");

		// this represents the source file that we generate
		VirtualFile targetFile = Play.getVirtualFile(targetPath);
		// this represents the template we use to generate the source code
		VirtualFile templateFile = Play.getVirtualFile(templatePath);
		// if the source code already exists, skip it - we do not overwrite
		// normally
		if (targetFile == null || !targetFile.exists() || forceOverwrite) {
			File fileToCreate = Play.getFile(targetPath);
			if (templateFile == null || !templateFile.exists()) {
				Logger.error("!! ERROR: Can't find scaffold template -- "
						+ templatePath);
			}
			invokeTemplate(templateFile, fileToCreate, templateArgs);
			Logger.info("+ " + targetPath);
		} else {
			Logger.info("! Skipping " + targetPath);
		}
	}

	private void copyFile(TargetFileType type, String fileName) {
		String[] paths = getPaths(type, fileName);
		copyFile(paths[0], paths[1]);
	}

	private void generate(TargetFileType type, String fileName) {
		String[] paths = getPaths(type, fileName);
		generate(paths[0], paths[1]);
	}

	private String[] getPaths(TargetFileType type, String fileName) {
		String templatePath = "app" + File.separator + "views" + File.separator
				+ "scaffold" + File.separator + type.getPath() + File.separator
				+ fileName.toLowerCase() + ".html";
		String targetPath = "app" + File.separator + type.getPath()
				+ File.separator + fileName + ".java";
		return new String[] { templatePath, targetPath };
	}

	private void generateController(Entity entity) {
		Class<?> controller = Play.classloader.getClassIgnoreCase(entity
				.getControllerName());
		if (controller != null) {
			Logger.info("Skipping controller: " + entity.getControllerName());
			return;
		}

		Logger.info("Generating controller: " + entity.getControllerName());
		String controllerSourcePath = "app" + File.separator + "controllers"
				+ File.separator + entity.getControllerName() + ".java";
		String controllerTemplatePath = "app" + File.separator + "views"
				+ File.separator + "scaffold" + File.separator + "controllers"
				+ File.separator + "controller."
				+ entity.getModelType().name().toLowerCase() + ".html";

		generateForEntity(controllerTemplatePath, controllerSourcePath, entity);
	}

	private void generateForEntity(String templatePath, String targetPath,
			Entity entity) {
		Map<String, Object> templateArgs = new HashMap<String, Object>();
		templateArgs.put("entity", entity);
		generate(templatePath, targetPath, templateArgs);
	}

	private void generateHome() {
		Map<String, Object> templateArgs = new HashMap<String, Object>();
		String sourcePath = "app" + File.separator + "views" + File.separator
				+ "Application" + File.separator + "index.html";
		String templatePath = "app" + File.separator + "views" + File.separator
				+ "scaffold" + File.separator + "views" + File.separator
				+ "Application" + File.separator + "index.html";
		generate(templatePath, sourcePath, templateArgs);
	}

	// generate the layout file
	private void generateLayout() {
		Logger.info("Generating layout: main.html");
		String sourcePath = "app" + File.separator + "views" + File.separator
				+ "scaffold" + File.separator + "main.html";
		String targetPath = "app" + File.separator + "views" + File.separator
				+ "main.html";
		generate(sourcePath, targetPath);
	}

	private void copyFile(String sourcePath, String targetPath) {
		VirtualFile templateFile = Play.getVirtualFile(sourcePath);
		VirtualFile targetFile = Play.getVirtualFile(targetPath);
		String templateLayout = templateFile.contentAsString();
		if (targetFile == null || !targetFile.exists() || forceOverwrite) {
			try {
				File fileToCreate = Play.getFile(targetPath);
				IO.writeContent(templateLayout, fileToCreate);
			} catch (IOException e) {
				Logger.error(e, "IO Exception");
			}
		} else {
			Logger.info("! Skipping " + targetPath);
		}
	}

	private void generateLogin() {
		// generate model for login handling
		generate(TargetFileType.MODEL, "RoleType");
		copyFile(TargetFileType.MODEL, "User");
		// implement authentify
		copyFile(TargetFileType.CONTROLLER, "Security");
		// generate views and controller for User
		Class<?> clazz;
		try {
			clazz = Play.classloader.loadClass("models.User");
			if (clazz != null) {
				Entity entity = new Entity(clazz);
				if (!entities.contains(entity)) {
					generateViewsForEntity(entity);
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.error(e, "Can't load User class");
		}
		// copy Secure/login.html
		String sourcePath = "app" + File.separator + "views" + File.separator
				+ "scaffold" + File.separator + "views" + File.separator
				+ "Secure" + File.separator + "login.html";
		String securePath = "app" + File.separator + "views" + File.separator
				+ "Secure";
		ensureDirectoryExists(securePath);
		String targetPath = securePath + File.separator + "login.html";
		copyFile(sourcePath, targetPath);
	}

	private void generateViewsForEntity(Entity entity) {
		// create the view folder if necessary
		Logger.info("Generating views for " + entity.getControllerName());
		String targetViewPath = "app" + File.separator + "views"
				+ File.separator + entity.getControllerName() + File.separator;
		String baseViewTemplatePath = "app" + File.separator + "views"
				+ File.separator + "scaffold" + File.separator + "views"
				+ File.separator + "Entity" + File.separator;
		ensureDirectoryExists(targetViewPath);
		for (String view : VIEW_HTMLS) {
			String modelViewTemplatePath = baseViewTemplatePath + view;
			generateForEntity(modelViewTemplatePath,
					targetViewPath + view, entity);
		}
	}

	private void ensureDirectoryExists(String templatePath) {
		File viewPathDirectory = new File(templatePath);
		if (!viewPathDirectory.exists()) {
			viewPathDirectory.mkdir();
		}
	}

	public boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public boolean isIncludeLayout() {
		return includeLayout;
	}

	public boolean isIncludeLogin() {
		return includeLogin;
	}

	public void setForceOverwrite(boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}

	public void setIncludeLayout(boolean includeLayout) {
		this.includeLayout = includeLayout;
	}

	public void setIncludeLogin(boolean includeLogin) {
		this.includeLogin = includeLogin;
	}
}
