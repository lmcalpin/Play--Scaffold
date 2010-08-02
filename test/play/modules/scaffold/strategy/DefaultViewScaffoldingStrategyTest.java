package play.modules.scaffold.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

import play.modules.scaffold.entity.MyEntity;
import play.modules.scaffold.form.FormElement;

public class DefaultViewScaffoldingStrategyTest {
	ViewScaffoldingStrategy strategy = new DefaultViewScaffoldingStrategy();

	@Test
	public void testBooleanPrimitiveRenderedAsCheckbox()
			throws NoSuchFieldException {
		Field field = MyEntity.class.getField("aPrimitiveBoolean");
		FormElement property = strategy.render(field);
		assertTrue(property.isCheckbox());
	}

	@Test
	public void testBooleanObjectRenderedAsCheckbox()
			throws NoSuchFieldException {
		Field field = MyEntity.class
				.getField("aSlightlyMoreSophisticatedBoolean");
		FormElement property = strategy.render(field);
		assertTrue(property.isCheckbox());
	}

	@Test
	public void testEnumRenderedAsSelectWithValidValuesAsOptions()
			throws NoSuchFieldException {
		Field field = MyEntity.class.getField("anEnum");
		FormElement property = strategy.render(field);
		assertTrue(property.isSelect());
		assertEquals(3, property.getOptions().size());
		assertTrue(property.getOptions().contains("ONE"));
		assertTrue(property.getOptions().contains("TWO"));
		assertTrue(property.getOptions().contains("THREE"));
		assertFalse(property.getOptions().contains("FOUR"));
	}
}
