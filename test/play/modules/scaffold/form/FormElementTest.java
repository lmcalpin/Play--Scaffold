package play.modules.scaffold.form;

import javax.persistence.Entity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FormElementTest {
	@Entity public class EmbeddableExample {}
	FormElement fe = new FormElement("test", String.class, FormElementType.PASSWORD, null);
	FormElement parent = new FormElement("parent", EmbeddableExample.class, FormElementType.EMBEDDED, null);
	FormElement simple = new FormElement("someInt", int.class, FormElementType.TEXT, null);
	
	@Before
	public void setup() {
		fe.setParent(parent);
	}
	
	@Test
	public void testNameIsFieldsName() {
		Assert.assertEquals("someInt", simple.getName());
		Assert.assertEquals("test", fe.getName());
	}
	
	@Test
	public void testPathUsesDotNotationForChildrenOfEmbeddables() {
		Assert.assertEquals("parent?.test", fe.getPath());
	}
}
