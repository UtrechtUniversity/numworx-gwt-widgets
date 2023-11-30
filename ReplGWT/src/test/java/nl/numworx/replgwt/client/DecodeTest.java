package nl.numworx.replgwt.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class DecodeTest {

	@Test
	public void test() {
		String program = "$Z65@space$Z33@";
		String result = Util.decodeZ(program);
		assertEquals("Aspace!", result);
	}
	@Test
	public void testsmiley() {
		String smile = "😀";
		int cp = smile.codePointAt(0);
		String program = "$Z" + cp + "@space$Z36@";
		String result = Util.decodeZ(program);
		assertEquals(smile + "space$", result);
	}
	@Test
	public void testdollar() {
		String program = "$Z36@Z33@";
		String result = Util.decodeZ(program);
		assertEquals("$Z33@", result);
	}
	@Test
	public void testnone() {
		String program = "program";
		String result = Util.decodeZ(program);
		assertTrue (result == program);
	}
}
