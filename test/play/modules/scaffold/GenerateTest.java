package play.modules.scaffold;

import org.junit.Test;
import static org.junit.Assert.*;

public class GenerateTest {
	@Test
	public void testMatchWildcardStar() {
		assertTrue(Generate.match("StartsWith", "Starts*"));
		assertFalse(Generate.match("StartsWith", "Starts~t"));
		assertFalse(Generate.match("DoesNotStartsWith", "Starts*"));
		assertTrue(Generate.match("EndsWith", "*With"));
		assertTrue(Generate.match("EndsWith", "*sW*"));
		assertTrue(Generate.match("EndsWith", "E*sW*h"));
		assertFalse(Generate.match("EndsWith", "E*sW*hh"));
		assertFalse(Generate.match("EndsWith", "EE*sW*h"));
		assertFalse(Generate.match("EndsWith", "E*ssW*h"));
	}

	@Test
	public void testMatchWildcardTilde() {
		assertTrue(Generate.match("StartsWith", "Starts~"));
		assertFalse(Generate.match("StartsWith", "Starts~t"));
		assertFalse(Generate.match("DoesNotStartsWith", "Starts~"));
		assertTrue(Generate.match("EndsWith", "~With"));
		assertTrue(Generate.match("EndsWith", "~sW~"));
		assertTrue(Generate.match("EndsWith", "E~sW~h"));
		assertFalse(Generate.match("EndsWith", "E~sW~hh"));
		assertFalse(Generate.match("EndsWith", "EE~sW~h"));
		assertFalse(Generate.match("EndsWith", "E~ssW~h"));
	}

	@Test
	public void testMatchIsCaseInsensitive() {
		assertTrue(Generate.match("StartsWith", "startswith"));
		assertTrue(Generate.match("EndsWith", "enDsWitH"));
	}
}
