package play.modules.scaffold.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModelTypeTest {
	@Test
	public void testDetectsIdFieldForJPA() throws SecurityException, NoSuchFieldException {
		ModelType modelType = ModelType.PURE_JPA;
		assertFalse(modelType.isId(MyEntity.class.getField("aString")));
		assertTrue(modelType.isId(MyEntity.class.getField("theId")));
	}
}
