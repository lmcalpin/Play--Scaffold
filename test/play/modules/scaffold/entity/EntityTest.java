package play.modules.scaffold.entity;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class EntityTest
{
	@Test
	public void testFields()
	{
		List<Field> fields = Entity.publicFields(MyPolymorphicEntity.class);
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields)
		{
			fieldNames.add(field.getName());
		}
		assertTrue(fieldNames.contains("aLongString")); // from MyPolymorphicEntity
		assertTrue(fieldNames.contains("anEnum")); // from MyEntity
		assertTrue(fieldNames.contains("id")); // from Model
		assertFalse(fieldNames.contains("willBeSaved")); // from JPASupport && transient
	}
	
	@Test
	public void testAnnotationOverridesController()
	{
		Entity entity = new Entity(MyAnnotatedEntity.class);
		assertEquals("Foo", entity.getControllerName());
	}
}
