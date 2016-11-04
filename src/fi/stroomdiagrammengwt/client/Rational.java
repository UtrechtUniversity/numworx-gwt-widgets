package fi.stroomdiagrammengwt.client;

//import java.io.Serializable;

public class Rational //implements Serializable
{   // public(!) attributes
    // initialized as 0
    public int nom, denom;
    public double decVal;
    public static double unDefined = -1e9d;        
    // creating rationals
    // no error check on denom!
    public Rational(int p, int q)
    {   nom = p;
        denom = q;
        double d = ((double) nom) / denom;        
        decVal = d;//round(d);
        simplify();
    }
    // for parallel calculations
    public Rational(int p, int q, double d)
    {   nom = p;
        denom = q;
        decVal = d; //round(d);
        simplify();
    }
    
    public double round(double d)
    {   return ((double) Math.round(d * Math.pow(10, StroomDiagrammenGWT.capDecs))) /
                 Math.pow(10, StroomDiagrammenGWT.capDecs);
    }    
    
    public double round(double d, int decs)
    {   return ((double) Math.round(d * Math.pow(10, decs))) /
                 Math.pow(10, decs);
    }    
    
    // copying a rational
    public Rational(Rational r)
    {   nom = r.nom;
        denom = r.denom;
        decVal = r.decVal;
    }
    // converting an integer to a rational
    public Rational(int p)
    {   nom = p;
        denom = 1;
        decVal = p;
    }
    // creating a rational from a double d
    // d is assumed to be <= 1
    public Rational(double d)
    {   if (d > 1)
        {   nom = 1;
            denom = 1;
            decVal = 1;
        }    
        else
        {   decVal = round(d);
            nom = (int) Math.round(d * Math.pow(10, StroomDiagrammenGWT.capDecs));
            denom = (int) Math.pow(10, StroomDiagrammenGWT.capDecs);
            simplify();                          
        }
    }
    // creating a rational from a double d
    public Rational(double d, int decs)
    {   decVal = round(d, decs);
        nom = (int) Math.round(d * Math.pow(10, decs));
        denom = (int) Math.pow(10, decs);
        simplify();                          
    }

    public static Rational unDefined()
    {   Rational r = new Rational(1);
        r.decVal = unDefined;
        return r;
    }    

    public boolean equals(Rational r)
    {   return (nom == r.nom) &&
               (denom == r.denom) &&
               (decVal == r.decVal);
    }    

    public boolean isUndefined()
    {   return decVal == unDefined; 
        
    }    
    public boolean isSmaller(Rational r, int mode)
    {   if ((mode == StroomDiagrammenGWT.decMode) ||
            (mode == StroomDiagrammenGWT.percMode))
            return decVal < r.decVal;
        else // mode == DrawingPanel.fracMode    
            return nom * r.denom < r.nom * denom;
    }    
    public boolean isSmallerOrEqual(Rational r, int mode)
    {   if ((mode == StroomDiagrammenGWT.decMode) ||
            (mode == StroomDiagrammenGWT.percMode))
            return decVal <= r.decVal;
        else // mode == DrawingPanel.fracMode    
            return nom * r.denom <= r.nom * denom;
    }    
    public boolean isLarger(Rational r, int mode)
    {   if ((mode == StroomDiagrammenGWT.decMode) ||
            (mode == StroomDiagrammenGWT.percMode))
            return decVal > r.decVal;
        else // mode == DrawingPanel.fracMode    
            return nom * r.denom > r.nom * denom;
    }    
    public boolean isLargerOrEqual(Rational r, int mode)
    {   if ((mode == StroomDiagrammenGWT.decMode) ||
            (mode == StroomDiagrammenGWT.percMode))
            return decVal >= r.decVal;
        else // mode == DrawingPanel.fracMode    
            return nom * r.denom >= r.nom * denom;
    }    
    // only used for formatting rationals
    public boolean isInteger()
    {   simplify();
        return (denom == 1);
    }
    public Rational plus(Rational r)
    {   Rational s = new Rational(
            nom * r.denom + r.nom * denom, denom * r.denom,
            decVal + r.decVal);
        s.simplify();
        return s;
    }
    public Rational minus(Rational r)
    {   Rational s = new Rational(
            nom * r.denom - r.nom * denom, denom * r.denom,
            decVal - r.decVal);
        s.simplify();
        return s;
    }
    public Rational times(Rational r)
    {   Rational s = new Rational(
            nom * r.nom, denom * r.denom,
            decVal * r.decVal);
        s.simplify();
        return s;
    }
    // no error check!!
    public Rational divideBy(Rational r)
    {   Rational s = new Rational(
            nom * r.denom, denom * r.nom,
            decVal / r.decVal);
        s.simplify();
        return s;
    } // divideBy

    public void simplify()
    {   // positive denominator
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
