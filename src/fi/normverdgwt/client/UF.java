package fi.normverdgwt.client;

/**
 * universele formatteer klasse
 * @author huub
 */
class UF
{   
	/**
	 * formatteren met decs decimalen
	 * geen error handling als decs negatief
	 * @param val te formatteren double
	 * @param decs aantal decimalen
	 * @return String met val geformatteerd met dec decimalen
	 */
    public static String format(double val, int decs)
    {   String result = "";
        // no decimals required
        if (decs == 0)
            result = String.valueOf(Math.round(val));
        else //
        {   // factor for decimal part
            double factor = Math.pow(10, decs);
            // integer part: hard casr to int
            long integerPart = (long) val;
            // fractional part
            long fractionalPart = Math.round(
                                       Math.abs(val - integerPart) * factor);
            // correct for rounding up
            if (fractionalPart >= factor)
            {   fractionalPart = 0;
                if (val < 0)
                    integerPart -= 1;
                else    
                    integerPart += 1;
            }    
            String integerString = String.valueOf(integerPart);
            // -0 does not exist!!
            if ( (integerPart == 0) && (val < 0) )
                integerString = "-" + integerString;
            String fractionalString = String.valueOf(fractionalPart);
            // add left zeroes when necessary!!
            if (fractionalString.length() < decs)
                fractionalString = zeros(decs - fractionalString.length()) +
                                   fractionalString;
            result = integerString + "," + fractionalString;
        }
        return result;
    }
    
    /**
     * als methode format, maar met overbodige nullen
     * in het decimale deel verwijderd
     * @param val te fomatteren double
     * @param decs aantal decimalen
     * @return String met val geformatteerd met dec decimalen
     * en zonder overbodige nullen in het decimale deel
     */
    public static String format0(double val, int decs)
    {	   
    	String result = format(val, decs);
    	result = trimTrailingZeros(result, ',');
    	return result;
    }
    
    /**
     * verwijder overbodige nullen in het decimale deel
     * (d.w.z. de substring rechts can decSep) van String s
     * @param s te wijzigen String
     * @param decSep character gebruikt als scheiding gehele/decimale deel
     * @return String s zonder overbodige nullen in het decimale deel
     */
	public static String trimTrailingZeros(String s, char decSep)
	{	String txt = new String(s);
		if (txt.indexOf(decSep) < 0)
			return txt;
		char c = txt.charAt(txt.length() - 1);
		while (c == '0')
		{	txt = removeCharAt(txt, txt.length() - 1);
			c = txt.charAt(txt.length() - 1);
		}	
		c = txt.charAt(txt.length() - 1);
		if (c == decSep)
			txt = removeCharAt(txt, txt.length() - 1);
		return txt;		
	}				
    
	/**
	 * verwijder het character op plaats index uit String s
	 * @param s String waaruit character verwijderd moet worden
	 * @param index index te verwijderen karakter
	 * @return String s met character op plaats index verwijderd
	 */
	public static String removeCharAt(String s, int index)
	{	String txt = new String(s);
		// eerste
		if (index == 0)
			txt = txt.substring(1);
		// laatste	
		else if (index == (txt.length() - 1))
			txt = txt.substring(0, txt.length() - 1);
		// middenin	
		else
		{	String txt1 = txt.substring(0, index);
			String txt2 = txt.substring(index + 1);
			txt = txt1 + txt2;
		}
		return txt;
	}		
    
	/**
	 * als methode format maar met een + vooraan als val groter gelijk 0
	 * @param val te formateren double
	 * @param decs aantal decimalen
	 * @return String met val geformatteerd met dec decimalen en voorzien van teken
	 */
    public static String sformat(double val, int decs)
    {   String result = format(val, decs);
        if (val >= 0)
            result = "+" + result;
        return result;
    }

    /**
     * formatteer de double val met decs decimalen en stop het
     * resultaat in een String met lengte width en een gegeven
     * alignment; als het resultaat langer is dan width,
     * return dan het resultaat 
     * @param val te formatteren double
     * @param decs aantal decimalen
     * @param width maximum lengthe resultaat String
     * @param al alignment: 0 centre, 1: left, 2: right
     * @return String met geformatteerde waarde en gegeven alignment 
     */
    public static String fformat(double val, int decs, int width, int al)
    {   String result = format(val, decs);
        int len = result.length();
        if (len < width)
            switch (al)
            {   // centered, rather crude, for drawing strings
                // do this at pixel level
                case 0: // divide extra spaces left and right
                {   int left = (width - len) / 2;
                    int right = width - left;
                    result = spaces(left) + result + spaces(right);
                }
                break;
                // left aligned
                case 1: result = result + spaces(width - len);
                break;
                // right aligned
                case 2: result = spaces(width - len) + result;
                break;
                // left aligned
                default: result = result + spaces(width - len);
            }
        return result;
    }

    /**
     * formatteer de double val met decs decimalen zodat het
     * resultaat een String met width characters is;
     * gebruik als nodig exponentiele notatie   
     * @param val te formatteren double
     * @param decs aantal decimalen
     * @param width lengte formatteer String
     * @return String met geformatteerde double val 
     */
    public static String eformat(double val, int decs, int width)
    {   String result = "";
        if (Math.abs(val) >= 1)
        {   // format normally
            result = format(val, decs);
             // find width
            int len = result.length();
            // sign
            String sign;
            if (val < 0)
                sign = "-";
            else     
                sign = "";
            // width too small for normal notation
            if (len > width)
            {   // very big   
                if (Math.abs(val) >= 1e+100d)
                        return stars(width);
                // find exponential
                int oom = 0;
                double temp = Math.abs(val);
                while (temp >= 10)
                {   oom += 1;
                    temp /= 10;
                }   
                result = "E" + String.valueOf(oom);
                len = width - (result.length() + sign.length());
                if (len > 2)
                {   String t = format(temp, len - 2);
                    if (t.length() > len)
                    {   result = "E" + String.valueOf(oom + 1);
                        result = sign + format(temp / 10, len - 2) + result;
                    }
                    else
                        result = sign + format(temp, len - 2) + result;
                }
                else
                    result = stars(width);
            } // if (len > width)    
        } // if (Math.abs(val) >= 1)    
            
        else // Math.abs(val) smaller then 1      
        {   // very small or 0
            if (Math.abs(val) < 1e-50d)
               return String.valueOf(0);
            // find exponential
            int oom = 0;
            double temp = Math.abs(val);
            while ( (temp != 0) && (temp < 1))
            {   oom += 1;
                temp *= 10;
            }
            String sign;
            if (val < 0)
                sign = "-";
            else     
                sign = "";
            result = format(val, Math.max(oom + 1, decs));
            // find width
            int len = result.length();
            if ((len > width) || allZeros(result)) 
            {   result = "E-" + String.valueOf(oom);
                len = width - (result.length() + sign.length());
                if (len > 2)
                {   String t = format(temp, len - 2);
                    if (t.length() > len)
                    {   result = "E-" + String.valueOf(oom + 1);
                        result = sign + format(temp / 10, len - 2) + result;
                    }
                    else
                       result = sign + format(temp, len - 2) + result;
                }    
                else
                    result = stars(width); 
            }
        } // Math.abs(val) < 1
        
        return result;
    }

    /**
     * check of String s bestaat uit nullene en maximaal
     * een decimale punt 
     * @param s te checken String
     * @return true/false
     */
    private static boolean allZeros(String s)
    {   int index = 0;
        boolean found = false;
        for (int i = 0; i < s.length(); i++)
        {   if (!found)
            {   if ((s.charAt(i) == '0') ||
                    (s.charAt(i) == '.'))
                    index++;
                else
                    found = true;
            }        
        }    
        if (index == s.length())
            return true;
        else if (index == (s.length() - 1))
            return true;
        else
            return false;
    }    

    /**
     * creeer een String bestaande uit c nullen
     * @param c aantal nullen
     * @return String bestaande uit c nullen
     */
    private static String zeros(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += "0";
        return result;
    }
    /**
     * creeer een String bestaande uit c spaties
     * @param c aantal spaties
     * @return String bestaande uit c spaties
     */
    private static String spaces(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += " ";
        return result;
    }
    
    /**
     * creeer een String bestaande uit c sterretjes
     * @param c aantal sterretjes
     * @return String bestaande uit c sterretjes
     */
    private static String stars(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += "*";
        return result;
    }

    /**
     * bepaal de OOM (order of magnitude) van een double val,
     * d.w.z. OOM = 0 als abs(val) groter gelijk 1 en kleiner dan 10,
     * OOM = 1 als abs(val) groter gelijk 10 en kleiner dan 100, etc.
     * OOM = -1 als abs(val) groter gelijk 1/10 en kleiner dan 1,
     * OOM = -2 als abs(val) groter gelijk 1/100 en kleiner dan 1/10, etc.
     * @param val te onderzoeken double
     * @return OOM
     */
    public static int findOOM(double val)
    {   int oom = 0;
        double temp = Math.abs(val);
        while (temp >= 10)
        {   oom += 1;
            temp /= 10;
        }   
        while (temp < 1)
        {   oom -= 1;
            temp *= 10;
        }   
        return oom;
    }    
    
} // class UF
