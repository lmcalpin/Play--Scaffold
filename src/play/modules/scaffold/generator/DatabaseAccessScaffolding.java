package play.modules.scaffold.generator;

import play.modules.scaffold.entity.Entity;

public class DatabaseAccessScaffolding {
	public static String allFor(Class<?> clazz) {
		Entity entity = new Entity(clazz);
		String query = entity.getPackageName() + "." + entity.getName() + ".all().fetch()";
		switch (entity.getModelType()) {
		case PURE_JPA:
			query = "play.db.jpa.JPA.getEntityManager().createQuery(\"from \"" + entity.getName() + ").getResultList()";
			break;
		case SIENA:
			query = "play.db.jpa.Model.all(" + entity.getName() + ".class).fetch()";
			break;
		}
		return query;
	}
	
	public static Entity entityFor(Class<?> clazz) {
		Entity entity = new Entity(clazz);
		return entity;
	}
}
