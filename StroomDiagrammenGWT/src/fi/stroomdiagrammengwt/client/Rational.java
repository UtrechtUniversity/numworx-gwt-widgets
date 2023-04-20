package fi.stroomdiagrammengwt.client;

/**
 * class implementing fractions and their arithmetic 
 */

public class Rational 
{   
	/**
	 * the nominator
	 */
    public int nom;
    /**
     * the denominator
     */
    public int denom;
    /**
     * the decimal value
     */
    public double decVal;
    /**
     * a very large negative double
     */
    public static double unDefined = -1e9d;
    /** 
     * a very large negative integer
     */
    public static int notDefined = -10000;
    /**
     * a very small positive double
     */
    public static double NZero = 1e-9d;
    
    /**
     * create a rational, no error check on denominator
     * @param p nominator
     * @param q denominator
     */
    public Rational(int p, int q)
    {   nom = p;
        denom = q;
        double d = ((double) nom) / denom;        
        decVal = d; 
        simplify();
    }
    /**
     * create a Rational, decimal value given  
     * @param p nominator
     * @param q denominator
     * @param d decimal value
     */
    public Rational(int p, int q, double d)
    {   nom = p;
        denom = q;
        decVal = d; 
        simplify();
    }
    
    /**
     * round a double to StroomDiagrammenGWT.capDecs decimals
     * @param d double to be rounded
     * @return rounded double
     */
    public double round(double d)
    {   return ((double) Math.round(d * Math.pow(10, DrawingPanel.capDecs))) /
                 Math.pow(10, DrawingPanel.capDecs);
    }    

    /**'
     * round a double to decs decimals
     * @param d double to be rounded
     * @param decs number of decimals wanted
     * @return rounded double
     */
    public double round(double d, int decs)
    {   return ((double) Math.round(d * Math.pow(10, decs))) /
                 Math.pow(10, decs);
    }    
    
    /**
     * create a deep copy of the Rational r 
     * @param r Rational to be copied
     */
    public Rational(Rational r)
    {   nom = r.nom;
        denom = r.denom;
        decVal = r.decVal;
    }

    /**
     * convert an integer to a Rational
     * @param p the integer to be converted
     */
    public Rational(int p)
    {   nom = p;
        denom = 1;
        decVal = p;
    }
    /**
     * create a Rational from a double d, d is assumed to be smaller or equal to 1
     * round to StroomDiagrammenGWT.capDecs decimals
     * @param d double to be converted to Rational 
     */
    public Rational(double d)
    {   if (d > 1)
        {   nom = 1;
            denom = 1;
            decVal = 1;
        }    
        else
        {   decVal = round(d);
            nom = (int) Math.round(d * Math.pow(10, DrawingPanel.capDecs));
            denom = (int) Math.pow(10, DrawingPanel.capDecs);
            simplify();                          
        }
    }
    /**
     * create a Rational from a double d,
     * round to decs decimals
     * @param d double to be converted to Rational
     * @param decs decimals for rounding
     */
    public Rational(double d, int decs)
    {   decVal = round(d, decs);
        nom = (int) Math.round(d * Math.pow(10, decs));
        denom = (int) Math.pow(10, decs);
        simplify();                          
    }

    /**
     * @return a Rational with decimal value unDefined
     */
    public static Rational unDefined()
    {   Rational r = new Rational(1);
        r.decVal = unDefined;
        return r;
    }    

    /**
     * check if this Rational equals Rational r
     * @param r Rational to be compared to
     * @return true/false
     */
    public boolean equals(Rational r)
    {   return (nom == r.nom) &&
               (denom == r.denom) &&
               (Math.abs(decVal - r.decVal) < NZero);
    }    

    /**
     * is the Rational unDefined (that is, its decimal value)?
     * @return true/false
     */
    public boolean isUndefined()
    {   return decVal == unDefined; 
        
    }    
    
    /**
     * check if this Rational is smaller then Rational r using mode
     * @param r Rational to be compared to
     * @param mode compare decimal values (mode = 0,1) or fractions (mode = 2)
     * @return true/false
     */
    public boolean isSmaller(Rational r, int mode)
    {   if ((mode == DrawingPanel.decMode) ||
            (mode == DrawingPanel.percMode))
            return (decVal - r.decVal) < -NZero;
        else // mode == StroomDiagrammenGWT.fracMode    
            return nom * r.denom < r.nom * denom;
    }
    /**
     * check if this Rational is smaller then or equal to Rational r using mode
     * @param r Rational to be compared to
     * @param mode compare decimal values (mode = 0,1) or fractions (mode = 2)
     * @return true/false
     */
    public boolean isSmallerOrEqual(Rational r, int mode)
    {   if ((mode == DrawingPanel.decMode) ||
            (mode == DrawingPanel.percMode))
            return (decVal - r.decVal) < NZero;
        else // mode == StroomDiagrammenGWT.fracMode    
            return nom * r.denom <= r.nom * denom;
    }    
    /**
     * check if this Rational is larger then Rational r using mode
     * @param r Rational to be compared to
     * @param mode compare decimal values (mode = 0,1) or fractions (mode = 2)
     * @return true/false
     */
    public boolean isLarger(Rational r, int mode)
    {   if ((mode == DrawingPanel.decMode) ||
            (mode == DrawingPanel.percMode))
            return (decVal - r.decVal) > NZero;
        else // mode == StroomDiagrammenGWT.fracMode    
            return nom * r.denom > r.nom * denom;
    }
    /**
     * check if this Rational is larger then or equal to Rational r using mode
     * @param r Rational to be compared to
     * @param mode compare decimal values (mode = 0,1) or fractions (mode = 2)
     * @return true/false
     */
    public boolean isLargerOrEqual(Rational r, int mode)
    {   if ((mode == DrawingPanel.decMode) ||
            (mode == DrawingPanel.percMode))
            return (decVal - r.decVal) > -NZero;
        else // mode == StroomDiagrammenGWT.fracMode    
            return nom * r.denom >= r.nom * denom;
    }    
    
    /** 
     * check if this Rational is an integer, that is denominator
     * should equal 1 after simplifying
     * @return true/false
     */
    public boolean isInteger()
    {   simplify();
        return (denom == 1);
    }
    
    /**
     * add Rational r to this Rational
     * @param r Rational to be added
     * @return Rational this+r
     */
    public Rational plus(Rational r)
    {   Rational s = new Rational(
            nom * r.denom + r.nom * denom, denom * r.denom,
            decVal + r.decVal);
        s.simplify();
        return s;
    }
    /**
     * subtract Rational r from this Rational
     * @param r Rational to be substracted
     * @return Rational this-r
     */
    public Rational minus(Rational r)
    {   Rational s = new Rational(
            nom * r.denom - r.nom * denom, denom * r.denom,
            decVal - r.decVal);
        s.simplify();
        return s;
    }
    /**
     * multiply this Rational with Rational r 
     * @param r Rational to be multiplied with
     * @return this*r
     */
    public Rational times(Rational r)
    {   Rational s = new Rational(
            nom * r.nom, denom * r.denom,
            decVal * r.decVal);
        s.simplify();
        return s;
    }

    /**
     * divide this Rational by Rational r, no error check!
     * @param r Rational to be divided by
     * @return this/r
     */
    public Rational divideBy(Rational r)
    {   Rational s = new Rational(
            nom * r.denom, denom * r.nom,
            decVal / r.decVal);
        s.simplify();
        return s;
    } 

    /**
     * simplify this Rational, so that gcd(nom,denom) = 1 
     */
    public void simplify()
    {   // make sure denominator is positive
        if (denom < 0)
        {   nom = - nom;
            denom = - denom;
        }
        if (nom == 0)
            denom = 1;
        else
        {   int g = gcd(nom, denom);
            nom = nom / g;
            denom = denom / g;
        }
    }
    /**
     * find the greatest common divisor of two integers 
     * @param a first integer
     * @param b second integer
     * @return gcd(a,b)
     */
	public int gcd(int a, int b)
	{   int m = Math.abs(a);
	    int n = Math.abs(b);
	    int temp = 0;
	    while ( n != 0 )
	    {   temp = m % n;
	        m = n;
	        n = temp;
	    }
	    return m;
	}
}
