package play.modules.scaffold.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class PersistenceStrategyTest {
	@Test
	public void testDetectsIdFieldForJPA() throws SecurityException, NoSuchFieldException {
		PersistenceStrategy modelType = PersistenceStrategy.PURE_JPA;
		assertFalse(modelType.isId(MyEntity.class.getField("aString")));
		assertTrue(modelType.isId(MyEntity.class.getField("theId")));
	}
}
