package play.modules.scaffold.generator;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.modules.scaffold.entity.Entity;
import play.modules.scaffold.entity.PersistenceStrategy;

public class DatabaseAccessScaffolding {
    public static List<String> additionalImportsFor(PersistenceStrategy strategy) {
        List<String> imports = new ArrayList<String>();
        switch (strategy) {
        case PLAY_JPA:
            break;
        case PURE_JPA:
            imports.add("import play.db.jpa.JPA;");
            break;
        case SIENA:
            imports.add("import siena.Model;");
            break;
        }
        return imports;
    }

    public static String allFor(Class<?> clazz) {
        Entity entity = new Entity(clazz);
        return allFor(entity);
    }

    public static String allFor(Entity entity) {
        String query = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            query = entity.getPackageName() + "." + entity.getName() + ".all().fetch()";
            break;
        case PURE_JPA:
            query = "play.db.jpa.JPA.em().createQuery(\"from " + entity.getName() + "\").getResultList()";
            break;
        case SIENA:
            query = "siena.Model.all(" + entity.getName() + ".class).fetch()";
            break;
        }
        return query;
    }

    public static String findByIdFor(Entity entity) {
        return findByIdFor(entity, "id");
    }

    public static String findByIdFor(Entity entity, String fieldName) {
        String stmt = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            stmt = entity.getName() + ".findById(" + fieldName + ")";
            break;
        case PURE_JPA:
            stmt = "JPA.em().find(" + entity.getModelType().getName() + ".class, " + fieldName + ")";
            break;
        case SIENA:
            stmt = "Model.all(" + entity.getModelType().getName() + ".class).filter(\"" + entity.getIdField() + "\", "
                    + fieldName + ").get()";
            break;
        }
        return stmt;
    }

    public static String saveFor(Entity entity) {
        return saveFor(entity, StringUtils.uncapitalize(entity.getName()));
    }

    public static String saveFor(Entity entity, String fieldName) {
        String stmt = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            stmt = fieldName + ".save()";
            break;
        case PURE_JPA:
            stmt = "JPA.em().persist(" + fieldName + ")";
            break;
        case SIENA:
            stmt = fieldName + ".insert()";
            break;
        }
        return stmt;
    }

    public static String updateFor(Entity entity) {
        return updateFor(entity, StringUtils.uncapitalize(entity.getName()));
    }

    public static String updateFor(Entity entity, String fieldName) {
        String stmt = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            stmt = fieldName + ".save()";
            break;
        case PURE_JPA:
            stmt = "JPA.em().merge(" + fieldName + ")";
            break;
        case SIENA:
            stmt = fieldName + ".update()";
            break;
        }
        return stmt;
    }

    public static String deleteFor(Entity entity) {
        return deleteFor(entity, StringUtils.uncapitalize(entity.getName()));
    }

    public static String deleteFor(Entity entity, String fieldName) {
        String stmt = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            stmt = fieldName + ".delete()";
            break;
        case PURE_JPA:
            stmt = "JPA.em().remove(" + fieldName + ")";
            break;
        case SIENA:
            stmt = fieldName + ".delete()";
            break;
        }
        return stmt;
    }

    /**
     * Returns true if the persistence strategy employed for this entity
     * requires a transient object to be reattached, reassociated, or merged
     * with a new session before we can update or delete it.
     * 
     * @return true if entity requires reassociation with the current database
     *         session
     */
    public static boolean requiresReattachFor(Entity entity) {
        boolean requiresReattach = false;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            requiresReattach = true;
            break;
        }
        return requiresReattach;
    }

    public static String reattachFor(Entity entity) {
        return reattachFor(entity, StringUtils.uncapitalize(entity.getName()));
    }

    public static String reattachFor(Entity entity, String fieldName) {
        String stmt = null;
        switch (entity.getPersistenceStrategy()) {
        case PLAY_JPA:
            stmt = fieldName + ".merge()";
            break;
        }
        return stmt;
    }
}
