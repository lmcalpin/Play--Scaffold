package play.modules.scaffold.entity;

import java.lang.reflect.Field;

import org.junit.Test;
import static org.junit.Assert.*;

public class PropertyTest
{
	@Test
	public void testBooleanPrimitiveRenderedAsCheckbox() throws NoSuchFieldException
	{
		Field field = MyEntity.class.getField("aPrimitiveBoolean");
		Property property = new Property(field);
		assertTrue(property.isCheckbox());
	}
	
	@Test
	public void testBooleanObjectRenderedAsCheckbox() throws NoSuchFieldException
	{
		Field field = MyEntity.class.getField("aSlightlyMoreSophisticatedBoolean");
		Property property = new Property(field);
		assertTrue(property.isCheckbox());
	}
	
	@Test
	public void testEnumRenderedAsSelectWithValidValuesAsOptions() throws NoSuchFieldException
	{
		Field field = MyEntity.class.getField("anEnum");
		Property property = new Property(field);
		assertTrue(property.isSelect());
		assertEquals(3, property.getOptions().size());
		assertTrue(property.getOptions().contains("ONE"));
		assertTrue(property.getOptions().contains("TWO"));
		assertTrue(property.getOptions().contains("THREE"));
		assertFalse(property.getOptions().contains("FOUR"));
	}
}
