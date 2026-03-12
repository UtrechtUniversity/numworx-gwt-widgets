package fi.nabouwenaanzichtengwt.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class HoekTest {

	@Test
	public void test0() {
		Viewer3d v = new Viewer3d();
		v.beginx = 0;
		v.beginy = 0;
		v.xhoek = 0;
		v.yhoek = 0;
		Matrix3D m = v.hoekMatrix();
		double trace = m.trace();
		trace = Math.max(-1, trace); // fouten in berekening.
		trace = Math.min(3,  trace);
		double angle = Math.acos((trace - 1.0)*0.5);
		long hoek = v.deg(angle);
		assertEquals(0L, hoek);

	}

	@Test
	public void testx() {
		Viewer3d v = new Viewer3d();
		v.beginx = 0;
		v.beginy = 0;
		v.xhoek = 0;
		v.yhoek = 0;
		for (int i = 1; i < 30; i+=3) {
			v.xhoek = i;
			Matrix3D m = v.hoekMatrix();
			double trace = m.trace();
			trace = Math.max(-1, trace); // fouten in berekening.
			trace = Math.min(3,  trace);
			double angle = Math.acos((trace - 1.0)*0.5);
			long hoek = v.deg(angle);
			assertEquals(i, hoek);
		}
	}

	@Test
	public void testy() {
		Viewer3d v = new Viewer3d();
		v.beginx = 0;
		v.beginy = 0;
		v.xhoek = 0;
		v.yhoek = 0;
		for (int i = 1; i < 30; i+=3) {
			v.yhoek = i;
			Matrix3D m = v.hoekMatrix();
			double trace = m.trace();
			trace = Math.max(-1, trace); // fouten in berekening.
			trace = Math.min(3,  trace);
			double angle = Math.acos((trace - 1.0)*0.5);
			long hoek = v.deg(angle);
			assertEquals(i, hoek);
		}
	}
	@Test
	public void testxy() {
		Viewer3d v = new Viewer3d();
		v.beginx = 0;
		v.beginy = 0;
		v.xhoek = 0;
		v.yhoek = 0;
		System.out.println(Math.sqrt(2));
		Matrix3D old = v.hoekMatrix();
		for (int i = 0; i <= 180; i+=5) {
			v.yhoek = i;
			v.xhoek = i;
			Matrix3D m = v.hoekMatrix();
			old.transpose();
			old.mult(m);
			double div = trac(old);
			double angle = trac(m);
			long hoek = v.deg(angle);
			System.out.println(i + " -> " + hoek + " " + angle / i * 180 / Math.PI + " " + v.deg(div));
			old = m;
		}
	}

	private double trac(Matrix3D m) {
		double trace = m.trace();
		trace = Math.max(-1, trace); // fouten in berekening.
		trace = Math.min(3,  trace);
		double angle = Math.acos((trace - 1.0)*0.5);
		return angle;
	}

	
}
