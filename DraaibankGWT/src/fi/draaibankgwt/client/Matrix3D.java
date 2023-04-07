package fi.draaibankgwt.client;

import java.util.HashMap;
import java.util.Map;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

class Matrix3D
{	
	//-------------------------------------------------------------------------------------------
	//deze klasse onthoudt, en berekent steeds opnieuw de tekenrichting, en berekent voor het 
	//Tekenblad aan de hand van een dx,dy en dz het volgende eindpunt van de tekenlijn.
	//-------------------------------------------------------------------------------------------

	private Rotatie3D[] rotatieRij;
	private int aantalRotaties;
	double starthoekx,starthoeky,starthoekz;
	double startschaal;
	private double xx, xy, xz ;
	private double yx, yy, yz;
	private double zx, zy, zz;
 	private static double pi = Math.PI;
 	double laatsteSchaal;

	public Matrix3D()
	{	rotatieRij = new Rotatie3D[150];
		aantalRotaties = 0;
		starthoekx = 0;
		starthoeky = 0;
		starthoekz = 0;
		startschaal = 1;
		laatsteSchaal = 1;
		xx = 1.0;
		yy = 1.0;
		zz = 1.0;
	}
	void initialiseer()
	{	rotatieRij = new Rotatie3D[150];
		aantalRotaties = 0;
		xx=1;xy=0;xz=0;
		yx=0;yy=1;yz=0;
		zx=0;zy=0;zz=1;
		schaal(startschaal);
		ydraaiAbs(starthoeky);
		xdraaiAbs(starthoekx);
		zdraaiAbs(starthoekz);
	}
	
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("xx", new Double(xx));
		h.put("xy", new Double(xy));
		h.put("xz", new Double(xz));
		h.put("yx", new Double(yx));
		h.put("yy", new Double(yy));
		h.put("yz", new Double(yz));
		h.put("zx", new Double(zx));
		h.put("zy", new Double(zy));
		h.put("zz", new Double(zz));
		
		
		return h;
	}
	
	public void setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		if (h.containsKey("xx"))
			xx = h.getDouble("xx");
		if (h.containsKey("xy"))
			xy = h.getDouble("xy");
		if (h.containsKey("xz"))
			xz = h.getDouble("xz");
		if (h.containsKey("yx"))
			yx = h.getDouble("yx");
		if (h.containsKey("yy"))
			yy = h.getDouble("yy");
		if (h.containsKey("yz"))
			yz = h.getDouble("yz");
		if (h.containsKey("zx"))
			zx = h.getDouble("zx");
		if (h.containsKey("zy"))
			zy = h.getDouble("zy");
		if (h.containsKey("zz"))
			zz = h.getDouble("zz");
		
	}
	

	
/*
al weg in Draaibank 
  	
	void initialiseer(double hx, double hy, double hz, double schl)
	{	starthoekx = hx;
		starthoeky = hy;
		starthoekz = hz;
		startschaal = schl;
		initialiseer();
	}	
*/	
	
	void schaal(double f) 
	{
//System.out.println("schaal " + f);

		xx *= f;
		xy *= f;
		xz *= f;
		yx *= f;
		yy *= f;
		yz *= f;
		zx *= f;
		zy *= f;
		zz *= f;
		laatsteSchaal *= f;
   }

	void herschaal(double hf)
	{
System.out.println("herschaal " + hf);

		xx *= (hf/laatsteSchaal);
		xy *= (hf/laatsteSchaal);
		xz *= (hf/laatsteSchaal);
		yx *= (hf/laatsteSchaal);
		yy *= (hf/laatsteSchaal);
		yz *= (hf/laatsteSchaal);
		zx *= (hf/laatsteSchaal);
		zy *= (hf/laatsteSchaal);
		zz *= (hf/laatsteSchaal);
		laatsteSchaal = hf;
		
	}
	
	void xdraai(double theta) 
	{	voegRotatieToe(1,theta);
	}
	void ydraai(double theta) 
	{	voegRotatieToe(2,theta);
	}
	void zdraai(double theta) 
	{	voegRotatieToe(3,theta);
	}
	public void voegRotatieToe(int as, double rotatieHoek)
	{	Rotatie3D r = new Rotatie3D(as,rotatieHoek);
		for(int i = 0; i<aantalRotaties ; i++)
		{	Rotatie3D rt = rotatieRij[i];
			if(rt.as == 1)xdraaiAbs(-rt.rotatieHoek);
			else if(rt.as == 2)ydraaiAbs(-rt.rotatieHoek);
			else if(rt.as == 3)zdraaiAbs(-rt.rotatieHoek);
		}
		if(r.as == 1)xdraaiAbs(rotatieHoek);
		else if(r.as == 2)ydraaiAbs(rotatieHoek);
		else if(r.as == 3)zdraaiAbs(rotatieHoek);
		for(int i = aantalRotaties ; i>0 ; i--)
		{	Rotatie3D rt = rotatieRij[i-1];
			if(rt.as == 1)xdraaiAbs(rt.rotatieHoek);
			else if(rt.as == 2)ydraaiAbs(rt.rotatieHoek);
			else if(rt.as == 3)zdraaiAbs(rt.rotatieHoek);
		}
		
		if(aantalRotaties>0 && (rotatieRij[aantalRotaties-1].as == as))
		{	rotatieRij[aantalRotaties-1].rotatieHoek += rotatieHoek;
			if(rotatieRij[aantalRotaties-1].rotatieHoek%360 == 0) aantalRotaties--;
		}
		else 
		{	rotatieRij[aantalRotaties] = r;
			aantalRotaties++;
		}
	}

	void ydraaiAbs(double theta) 
	{
		theta *= (pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nxx =  (xx * ct + zx * st);
		double Nxy =  (xy * ct + zy * st);
		double Nxz =  (xz * ct + zz * st);

		double Nzx =  (zx * ct - xx * st);
		double Nzy =  (zy * ct - xy * st);
		double Nzz =  (zz * ct - xz * st);

		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
    }

    void xdraaiAbs(double theta) 
    {
		theta *= (pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nyx = (yx * ct + zx * st);
		double Nyy = (yy * ct + zy * st);
		double Nyz = (yz * ct + zz * st);

		double Nzx = (zx * ct - yx * st);
		double Nzy = (zy * ct - yy * st);
		double Nzz = (zz * ct - yz * st);

		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
	}

	void zdraaiAbs(double theta) 
	{
		theta *= -(pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nyx = (yx * ct + xx * st);
		double Nyy = (yy * ct + xy * st);
		double Nyz = (yz * ct + xz * st);

		double Nxx = (xx * ct - yx * st);
		double Nxy = (xy * ct - yy * st);
		double Nxz = (xz * ct - yz * st);

		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
	}
	
	void mult(Matrix3D rhs) 
	{	
		double lxx = xx * rhs.xx + yx * rhs.xy + zx * rhs.xz;
		double lxy = xy * rhs.xx + yy * rhs.xy + zy * rhs.xz;
		double lxz = xz * rhs.xx + yz * rhs.xy + zz * rhs.xz;

		double lyx = xx * rhs.yx + yx * rhs.yy + zx * rhs.yz;
		double lyy = xy * rhs.yx + yy * rhs.yy + zy * rhs.yz;
		double lyz = xz * rhs.yx + yz * rhs.yy + zz * rhs.yz;

		double lzx = xx * rhs.zx + yx * rhs.zy + zx * rhs.zz;
		double lzy = xy * rhs.zx + yy * rhs.zy + zy * rhs.zz;
		double lzz = xz * rhs.zx + yz * rhs.zy + zz * rhs.zz;

		xx = lxx;
		xy = lxy;
		xz = lxz;

		yx = lyx;
		yy = lyy;
		yz = lyz;

		zx = lzx;
		zy = lzy;
		zz = lzz;
    }

	Punt3D geefVolgendPunt(Punt3D bp, double dx, double dy, double dz)
	{
		Punt3D ep = new Punt3D(0,0,0);
		ep.x = bp.x + dx*xx + dy*xy + dz*xz;
		ep.y = bp.y + dx*yx + dy*yy + dz*yz;
		ep.z = bp.z + dx*zx + dy*zy + dz*zz;
		return ep;
	}
}
