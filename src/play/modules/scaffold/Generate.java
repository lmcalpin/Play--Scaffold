/**
 *
 * Copyright 2012, Lawrence McAlpin.
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

import javax.persistence.GeneratedValue;

import play.Logger;
import play.Play;
import play.i18n.MessagesPlugin;
import play.modules.scaffold.entity.Entity;
import play.modules.scaffold.entity.PersistenceStrategy;
import play.modules.scaffold.generator.DatabaseAccessScaffolding;
import play.modules.scaffold.generator.ScaffoldingGenerator;
import play.modules.scaffold.utils.Fields;

/**
 * Processes the scaffold:gen command. This command generates the scaffolding
 * for a project, creating a rudimentary Application.index template, layout, and
 * basic CRUD screens.
 * 
 * @author Lawrence McAlpin
 */
public class Generate {
    private static final String EXCLUDE = "--exclude";
    private static final String INCLUDE = "--include";
    private static final String OVERWRITE = "--overwrite";
    // TODO: --flatten used to be default; we intend to change this so that future
    // versions of scaffold use the subpackages the model is in when generating the controllers
    // and views.  This work is currently unfinished...
    private static final String FLATTEN_PATHS = "--flatten";
    private static final String WITH_LAYOUT = "--with-layout";
    private static final String WITH_LOGIN = "--with-login";
    private static final String ALL = "--all";

    private static final String INVALID_MODELPATTERN = "Invalid pattern, provide a text string with optional '*' wildcard";

    boolean forceOverwrite = false;
    String includeRegEx = null;
    String excludeRegEx = null;
    boolean includeLayout = false;
    boolean includeLogin = false;
    boolean flattenPaths = true;
    
    public Generate() {

    }

    public void parseArguments(String[] args) {
        // interpret command line arguments
        String gettingArgumentsForCommand = null;
        for (String arg : args) {
            if (gettingArgumentsForCommand != null) {
                if (gettingArgumentsForCommand.equalsIgnoreCase(INCLUDE)) {
                    includeRegEx = validateFilePatternArgs(INCLUDE, arg);
                } else if (gettingArgumentsForCommand.equalsIgnoreCase(EXCLUDE)) {
                    excludeRegEx = validateFilePatternArgs(EXCLUDE, arg);
                }
                gettingArgumentsForCommand = null;
                continue;
            }
            String lowerArg = arg.toLowerCase();
            if (arg.startsWith("--")) {
                if (lowerArg.equals(OVERWRITE)) {
                    forceOverwrite = true;
                    Logger.info("--overwrite: We will force overwrite target files");
                } else if (lowerArg.equalsIgnoreCase(INCLUDE)) {
                    gettingArgumentsForCommand = INCLUDE;
                } else if (lowerArg.equalsIgnoreCase(EXCLUDE)) {
                    gettingArgumentsForCommand = EXCLUDE;
                } else if (lowerArg.startsWith(INCLUDE + "=")) {
                    includeRegEx = validateFilePatternArgs(INCLUDE, arg.split("=")[1]);
                } else if (lowerArg.startsWith(EXCLUDE + "=")) {
                    excludeRegEx = validateFilePatternArgs(EXCLUDE, arg.split("=")[1]);
                } else if (lowerArg.equalsIgnoreCase(FLATTEN_PATHS)) {
                    flattenPaths = true;
                } else if (lowerArg.equalsIgnoreCase(WITH_LAYOUT)) {
                    includeLayout = true;
                } else if (lowerArg.equalsIgnoreCase(WITH_LOGIN)) {
                    includeLogin = true;
                } else if (lowerArg.equalsIgnoreCase(ALL)) {
                    forceOverwrite = true;
                    includeLayout = true;
                    includeLogin = true;
                } else {
                    Logger.warn("Invalid argument: %s", arg);
                    System.exit(-1);
                }
            }
        }
    }

    public void validate() {
        // validate flags
        if (includeLogin) {
            try {
                Class.forName("controllers.Secure", false, Play.classloader);
            } catch (ClassNotFoundException e) {
                Logger.warn(" ! controllers.Secure could not be found");
                Logger.warn(" ! check application.conf to ensure module.secure is imported");
                includeLogin = false;
            }
        }
    }

    public void generateScaffolding() {
        // Locate domain model classes that we can process.
        // Currently, we only support classes that extend the
        // play.db.jpa.Model or siena.Model classes.
        List<Class> classes = Play.classloader.getAllClasses();
        ScaffoldingGenerator generator = new ScaffoldingGenerator();
        generator.setForceOverwrite(forceOverwrite);
        generator.setIncludeLayout(includeLayout);
        generator.setIncludeLogin(includeLogin);
        generator.setFlattenPaths(flattenPaths);
        for (Class clazz : classes) {
            // If this model is of a supported type, queue it up
            // so the ScaffoldGenerator will create its controller
            // and views.
            PersistenceStrategy persistenceStrategy = PersistenceStrategy.forModel(clazz);
            if (persistenceStrategy != null) {
                String simpleName = clazz.getSimpleName();
                boolean includeEntity = false;
                // by default, include all entities if no --include= value is
                // specified
                if (includeRegEx == null) {
                    includeEntity = true;
                }
                // if an --include= value is specified, include only the models
                // that match
                if (includeRegEx != null && match(simpleName, includeRegEx)) {
                    includeEntity = true;
                }
                // always exclude models that match the --exclude= parameter
                if (excludeRegEx != null && match(simpleName, excludeRegEx)) {
                    includeEntity = false;
                }
                if (includeEntity) {
                    Entity entity = new Entity(clazz);

                    // validate known limitations
                    if (persistenceStrategy == PersistenceStrategy.PURE_JPA) {
                        String idField = entity.getIdField();
                        if (idField == null) {
                            Logger.warn("Can not process %s because it needs an @Id annotated column", simpleName);
                            continue;
                        }
                        if (!Fields.annotations(clazz, idField).contains(GeneratedValue.class)) {
                            Logger.warn("Can not process %s because key must be auto-generated (use @GeneratedValue)",
                                    simpleName);
                            continue;
                        }
                    }

                    // we appear to be good to go!
                    generator.addEntity(entity);
                } else {
                    Logger.info("Skipping %s", simpleName);
                }
            }
        }
        generator.generate();
    }

    public void run(String[] args) {
        parseArguments(args);
        validate();
        generateScaffolding();
    }

    public static void main(String[] args) throws Exception {
        // initialize Play!
        File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        Thread.currentThread().setContextClassLoader(Play.classloader);
        MessagesPlugin plugin = new MessagesPlugin();
        plugin.onApplicationStart();

        Generate generator = new Generate();
        generator.run(args);
    }

    private static String validateFilePatternArgs(String cmd, String arg) {
        if (arg.isEmpty()) {
            Logger.warn(cmd + ": " + INVALID_MODELPATTERN);
            System.exit(-1);
        }
        String regex = arg;
        Logger.info(cmd + ": files that match: %s", regex);
        return regex;
    }

    // Does simple matching: you can add an asterisk to match "any"
    // text. Matching is case insensitive.
    public static boolean match(String text, String pattern) {
        String normalizedText = text.toLowerCase();
        String normalizedPattern = pattern.toLowerCase();
        String[] subsections = normalizedPattern.split("[\\*~]");
        int section = 0;
        for (String subsection : subsections) {
            int idx = normalizedText.indexOf(subsection);
            if (idx == -1) {
                return false;
            }
            // if we don't start with a wildcard, the first matched section must
            // be at index 0
            if (section == 0 && (!(normalizedPattern.startsWith("*") || normalizedPattern.startsWith("~")))) {
                if (idx != 0) {
                    return false;
                }
            }
            // if we don't end with a wildcard, the first matched section must
            // be at index 0
            if (section == subsections.length - 1
                    && (!(normalizedPattern.endsWith("*") || normalizedPattern.endsWith("~")))) {
                if (!normalizedText.endsWith(subsection)) {
                    return false;
                }
            }
            normalizedText = normalizedText.substring(idx + subsection.length());
            section++;
        }
        return true;
    }
}
