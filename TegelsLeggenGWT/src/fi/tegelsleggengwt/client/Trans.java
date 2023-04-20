package fi.tegelsleggengwt.client;

public class Trans
{	static int drBreedte = 8;
	static int drHoogte = 7;
	static int factor = 2;
	
	public static int geefx(int i, int j)
	{	return factor * (drBreedte * i + drBreedte * j / 2);
	}
	public static int geefy(int i, int j)
	{	return factor * (drHoogte * j);
	}
	public static void zetFactor(int f)
	{	factor = f;
	}
}
