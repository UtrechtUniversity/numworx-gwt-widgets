package nl.numworx.sqlitegwt.client;

public class Util {

	private Util() {
	}

	static String decodeZ(String program) {
		int off = program.indexOf("$Z");
		if (off < 0) return program;
		StringBuffer sb = new StringBuffer(program);
		while (off >= 0) {
			int at = sb.indexOf("@", off+2);
			if (at > off) {
				String nnn = sb.substring(off+2, at);
				char[] decode = Character.toChars(Integer.parseInt(nnn));
				nnn = new String(decode);
				sb.replace(off, at+1, nnn);
				off = sb.indexOf("$Z", off + nnn.length());
			}
		}		
		return sb.toString();
	}

}
