package nl.numworx.fsmgwt.client;

import java.io.IOException;

import fi.euclides.persist.DataInput;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

public class JSONInput implements DataInput {

	private ObjectList list;
	private int cursor;
	
	
	private void next() throws IOException {
		if (cursor == list.size()) throw new IOException("EOF");
	}

	public JSONInput(ObjectList list) {
		this.list = list;
		this.cursor = 0;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		int b = readInt();
		return b & 0xFF;
	}

	@Override
	public int readInt() throws IOException {
		next();
		return list.getInt(cursor++);
	}

	@Override
	public String readUTF() throws IOException {
		next();
		return list.getString(cursor++);
	}

	@Override
	public long readLong() throws IOException { // long is a string!!!!
		return Long.parseLong(readUTF());
	}

	@Override
	public boolean readBoolean() throws IOException {
		next();
		return list.getBoolean(cursor++);
	}

	@Override
	public double readDouble() throws IOException {
		next();
		return list.getDouble(cursor++);
	}

}
