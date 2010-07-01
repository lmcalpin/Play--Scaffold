package play.modules.scaffold.utils;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import play.modules.scaffold.entity.MyEntity;

public class ClassesTest
{
	@Test
	public void testSuperClasses()
	{
		List<String> classes = Classes.superclasses(MyEntity.class);
		assertTrue(classes.contains("java.lang.Object"));
		assertTrue(classes.contains("play.db.jpa.Model"));
	}
}
