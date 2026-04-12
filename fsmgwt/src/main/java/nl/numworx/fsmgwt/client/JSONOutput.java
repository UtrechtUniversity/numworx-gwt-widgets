package nl.numworx.fsmgwt.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fi.euclides.persist.DataOutput;

public class JSONOutput implements DataOutput {
	
	List<Object> list;

	public JSONOutput() {
		list = new LinkedList<>();
	}

	@Override
	public void writeByte(int i) throws IOException {
		list.add(i);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		list.add(s);
	}

	@Override
	public void writeInt(int i) throws IOException {
		list.add(i);
	}

	@Override
	public void writeDouble(double d) throws IOException {
		list.add(d);
	}

	@Override
	public void writeLong(long a) throws IOException {
		list.add(Long.toString(a));
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		list.add(b);
	}

	List<Object> toList() {
		return list;
	}
	
}
