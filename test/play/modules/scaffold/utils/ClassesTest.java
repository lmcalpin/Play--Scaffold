package play.modules.scaffold.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import play.modules.scaffold.entity.MyEntity;
import play.modules.scaffold.entity.MyPolymorphicEntity;

public class ClassesTest {
	@Test
	public void testSuperClasses() {
		List<String> classes = Classes.superclasses(MyEntity.class);
		assertTrue(classes.contains("java.lang.Object"));
		assertTrue(classes.contains("play.db.jpa.Model"));
	}

	// make sure we don't see private fields
	@Test
	public void testPublicFields() {
		List<Field> fields = Classes.publicFields(MyPolymorphicEntity.class);
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		assertFalse(fieldNames.contains("cantSeeMe")); // from
														// MyPolymorphicEntity
		assertTrue(fieldNames.contains("aLongString")); // from
														// MyPolymorphicEntity
		assertTrue(fieldNames.contains("anEnum")); // from MyEntity
		assertTrue(fieldNames.contains("id")); // from Model
		assertTrue(fieldNames.contains("willBeSaved")); // from JPASupport
	}

	@Test
	public void testPackageName() {
		String packageName = Classes.getPackageName(MyPolymorphicEntity.class);
		assertEquals("play.modules.scaffold.entity", packageName);
	}

}
