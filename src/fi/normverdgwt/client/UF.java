package fi.normverdgwt.client;

// universal formatter
class UF
{   // basic formatting with decs decimals
    // no error handling (decs < 0)
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
//            else if (fractionalString.length() > decs)                       
//                fractionalString = fractionalString.substring(0, decs + 1);
            result = integerString + "," + fractionalString;
        }
        return result;
    }
    
    // as above with zeros at the end removed
    public static String format0(double val, int decs)
    {	   
    	String result = format(val, decs);
    	result = trimTrailingZeros(result, ',');
    	return result;
    }
    
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
    
    // as above but with sign always displayed
    public static String sformat(double val, int decs)
    {   String result = format(val, decs);
        if (val >= 0)
            result = "+" + result;
        return result;
    }
    // as above, but aligned (0=center, 1=left, 2=right) in a field of
    // width characters (if possible)
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
    
    // as above, but using exponential notation instead
    // forcing to width characters (if possible)
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
//                else if (len > 0)
//                    result = sign + format(temp, 0) + result;                
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
//                else if (len > 0)
//                    result = sign + format(temp, 0) + result;                
                else
                    result = stars(width); 
            }
        } // Math.abs(val) < 1
        
        return result;
    }

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



    // returns a string of c zeros
    private static String zeros(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += "0";
        return result;
    }
    // returns a string of c spaces
    private static String spaces(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += " ";
        return result;
    }
    
    // returns a string of c stars
    private static String stars(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += "*";
        return result;
    }

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
