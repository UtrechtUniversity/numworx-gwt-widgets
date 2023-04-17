package fi.kladjegwt.client;

/**
 * klasse die aan affiene transformatie A in het vlak representeert,
 * dus een 2x2 matrix M en een vector b, zodat voor een punt x in het
 * vlak, A(x)=M(x)+b 
 * @author huub
 */

public class AffineTransform 
{
	/**
	 * de matrix M
	 */
	double m00 = 1, m01 = 0, m10 = 0, m11 = 1;
	/**
	 * de vector b
	 */
	double b0 = 0, b1 = 0;

	/**
	 * default constructor
	 */
	public AffineTransform()
	{
	}
	
	/**
	 * constructor, alle waarden gegeven
	 * @param m00 M linksboven
	 * @param m01 M rechtsboven
	 * @param m10 M linksonder
	 * @param m11 M rechtsonder
	 * @param b0 b x-coordinaat
	 * @param b1 b-y-coordinaat
	 */
	public AffineTransform(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.b0 = b0;
		this.b1 = b1;
	}
	
	/**
	 * vermenigvuldig deze affiene transformatie A=M+b van links met de affiene
	 * transformatie at, d.w.z. als at=M'+b', dan is (at)A=M'M+(M'b+b') 
	 * @param at de affiene transformatie at 
	 * @return (at)A
	 */
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
	
	/**
	 * vindt de inverse affiene transformatie inv(A) van de affiene
	 * transformatie at=M+b , d.w.z. vind inv(at)=inv(M)-(inv(M))(b)   
	 * @param at affiene transformatie waarvan inverse bepaald moet worden
	 * @return inv(at) of id als det(M)=0 
	 */
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
	
	/**
	 * output deze affiene transformatie als String
	 */
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
