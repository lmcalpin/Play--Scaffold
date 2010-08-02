package play.modules.scaffold;

import org.junit.Test;
import static org.junit.Assert.*;

public class GenerateTest {
	@Test
	public void testMatchWildcard() {
		assertTrue(Generate.match("StartsWith", "Starts*"));
		assertTrue(Generate.match("EndsWith", "*With"));
		assertTrue(Generate.match("EndsWith", "*sW*"));
		assertTrue(Generate.match("EndsWith", "E*sW*h"));
		assertFalse(Generate.match("EndsWith", "E*sW*hh"));
		assertFalse(Generate.match("EndsWith", "EE*sW*h"));
		assertFalse(Generate.match("EndsWith", "E*ssW*h"));
	}

	@Test
	public void testMatchIsCaseInsensitive() {
		assertTrue(Generate.match("StartsWith", "startswith"));
		assertTrue(Generate.match("EndsWith", "enDsWitH"));
	}
}
