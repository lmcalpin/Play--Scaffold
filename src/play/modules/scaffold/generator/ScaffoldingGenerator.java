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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
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
	private static final String FORM_HTML = "_form";
	private static final String CREATE_HTML = "create";
	private static final String SHOW_HTML = "show";
	private static final String EDIT_HTML = "edit";
	private static final String LIST_HTML = "index";

	private static final List<String> VIEW_HTMLS = Arrays.asList(FORM_HTML, CREATE_HTML,
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
		} catch (UnexpectedException e) {
			Logger.warn(
					e,
					"! Failed to generate output successfully: "
							+ e.getMessage());
		} catch (Throwable t) {
			Logger.fatal(t, "! Unexpected error processing template: " + templateFile.getName(), args);
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
		String[] paths = getPaths(type, fileName, fileName);
		copyFile(paths[0], paths[1]);
	}

	private void generate(TargetFileType type, String fileName) {
		String[] paths = getPaths(type, fileName, fileName);
		generate(paths[0], paths[1]);
	}

	private void generate(TargetFileType type, String sourceFileName,
			String targetName) {
		String[] paths = getPaths(type, null, sourceFileName, targetName);
		generate(paths[0], paths[1]);
	}

	private void generate(TargetFileType type, String sourceFolderName,
			String sourceFileName, String targetName) {
		String[] paths = getPaths(type, sourceFolderName, sourceFileName,
				targetName);
		generate(paths[0], paths[1]);
	}

	private void generateForEntity(Entity entity, TargetFileType type,
			String templateFolderPath, String templateFileName, String targetName) {
		String[] paths = getPaths(type, templateFolderPath, templateFileName, targetName);
		Map<String, Object> templateArgs = new HashMap<String,Object>();
		templateArgs.put("entity", entity);
		generate(paths[0], paths[1], templateArgs);
	}

	private String[] getPaths(TargetFileType type, String templateFileName,
			String targetFileName) {
		return getPaths(type, null, templateFileName, targetFileName);
	}

	private String[] getPaths(TargetFileType type, String templateFolderPath,
			String templateFileName, String targetFileName) {
		String sourceFileName = templateFolderPath != null ? templateFolderPath
				+ '/' + templateFileName.toLowerCase()
				: templateFileName.toLowerCase();
		StringBuilder baseTemplatePath = new StringBuilder("app"
				+ '/' + "views" + '/' + "scaffold"
				+ '/');
		if (type != TargetFileType.LAYOUT) {
			baseTemplatePath.append(type.getPath() + '/');
		}
		String templateFile = baseTemplatePath.toString() + sourceFileName
				+ type.getSourceSuffix();
		String targetPath = "app" + '/';
		String additionalPath = type.getPath();
		if (additionalPath.length() > 0)
				targetPath = targetPath + additionalPath + '/';
		ensureDirectoryExists(targetPath);
		if (targetFileName.contains("/")) {
			String[] fileSplit = targetFileName.split("/");
			if (fileSplit.length >= 2) {
				ensureDirectoryExists(targetPath + '/' + fileSplit[0]);
			}
		}
		String targetFile = targetPath + targetFileName + type.getTargetSuffix();
		return new String[] { templateFile, targetFile };
	}

	private void generateController(Entity entity) {
		Class<?> controller = Play.classloader.getClassIgnoreCase(entity
				.getControllerName());
		if (controller != null) {
			Logger.info("Skipping controller: " + entity.getControllerName());
			return;
		}

		Logger.info("Generating controller: " + entity.getControllerName());
		generateForEntity(entity, TargetFileType.CONTROLLER, null, "controller", entity.getControllerName());
	}

	private void generateHome() {
		generate(TargetFileType.VIEW, "Application", "index", "Application"
				+ '/' + "index");
	}

	// generate the layout file
	private void generateLayout() {
		Logger.info("Generating layout: main.html");
		generate(TargetFileType.LAYOUT, "main", "views" + '/' + "main");
	}

	private void copyFile(String sourcePath, String targetPath) {
		VirtualFile templateFile = Play.getVirtualFile(sourcePath);
		VirtualFile targetFile = Play.getVirtualFile(targetPath);
		String templateLayout = templateFile.contentAsString();
		if (targetFile == null || !targetFile.exists() || forceOverwrite) {
			try {
				File fileToCreate = Play.getFile(targetPath);
				IO.writeContent(templateLayout, fileToCreate);
			} catch (UnexpectedException e) {
				Logger.error(e, "IO Exception");
			} catch (Throwable t) {
				Logger.error(t, "Unhandled Exception");
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
		String sourcePath = "app" + '/' + "views" + '/'
				+ "scaffold" + '/' + "views" + '/'
				+ "Secure" + '/' + "login.html";
		String securePath = "app" + '/' + "views" + '/'
				+ "Secure";
		ensureDirectoryExists(securePath);
		String targetPath = securePath + '/' + "login.html";
		copyFile(sourcePath, targetPath);
	}

	private void generateViewsForEntity(Entity entity) {
		// create the view folder if necessary
		Logger.info("Generating views for " + entity.getControllerName());
		for (String view : VIEW_HTMLS) {
			generateForEntity(entity, TargetFileType.VIEW, "Entity",
					'/' + view, entity.getControllerName()
							+ '/' + view);
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
