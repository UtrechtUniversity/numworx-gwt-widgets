package fi.kladjegwt.client;



public class AffineTransform 
{
	double m00 = 1, m01 = 0, m10 = 0, m11 = 1, b0 = 0, b1 = 0;
	
	public AffineTransform()
	{
		
	}
	
	public AffineTransform(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.b0 = b0;
		this.b1 = b1;
	}
	
	public AffineTransform leftMultiplyBy(AffineTransform at)
	{
		AffineTransform result = new AffineTransform(
				at.m00*m00+at.m01*m10,
				at.m00*m01+at.m01*m11,
				at.m10*m00+at.m11*m10,
				at.m10*m01+at.m11*m11,
				at.m00*b0+at.m01*b1+at.b0,
				at.m10*b0+at.m11*b1+at.b1);
				
		return result;
	}
	
	public AffineTransform inverseTransform(AffineTransform at)
	{
		AffineTransform result = new AffineTransform(1,0,0,1,0,0);
		
		// check if M is invertible
		double detM = at.m00 * at.m11 - at.m01 * at.m10;
		if (Math.abs(detM) < 1e-5d)
		{
			return result;
		}
		else
		{
			// M^-1
			double atm00 = at.m11 / detM;
			double atm01 = - at.m10 / detM;
			double atm10 = - at.m00 / detM;
			double atm11 = at.m00 / detM;
			
			// -M^-1(b) 
			double atb0 = - atm00 * b0 - atm01 * b1;
			double atb1 = - atm10 * b0 - atm11 * b1;
			
			result = new AffineTransform(atm00,atm01,atm10,atm11,atb0,atb1);
			
		}
		
		
		return result;
	}
	
	public String toString()
	{
		String result = "";
		
		result += "m00=" + UF.format(m00, 2) + " "; 
		result += "m01=" + UF.format(m01, 2) + " ";
		result += "m10=" + UF.format(m10, 2) + " ";
		result += "m11=" + UF.format(m11, 2) + " ";
		result += "b0=" + UF.format(b0, 2) + " ";
		result += "b1=" + UF.format(b1, 2);
		
		
		return result;
	}
	
}
