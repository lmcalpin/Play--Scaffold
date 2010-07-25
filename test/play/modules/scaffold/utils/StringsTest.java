package play.modules.scaffold.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import play.modules.scaffold.entity.MyEntity;
import play.modules.scaffold.entity.MyPolymorphicEntity;

public class StringsTest
{
	@Test
	public void testPluralizeEs()
	{
		assertEquals("Foxes", Strings.pluralize("Fox"));
		assertEquals("Churches", Strings.pluralize("Church"));
		assertEquals("Princesses", Strings.pluralize("Princess"));
	}

	@Test
	public void testPluralizeIes()
	{
		assertEquals("Entities", Strings.pluralize("Entity"));
	}

	@Test
	public void testPluralizeDefault()
	{
		assertEquals("Cats", Strings.pluralize("Cat"));
	}

	@Test
	public void testPluralizeIrregular()
	{
		assertEquals("Men", Strings.pluralize("Man"));
		assertEquals("Women", Strings.pluralize("Woman"));
		assertEquals("Axes", Strings.pluralize("Axis"));
	}
	
}
