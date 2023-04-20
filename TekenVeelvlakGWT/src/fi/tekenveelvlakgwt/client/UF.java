package fi.tekenveelvlakgwt.client;

/**
 * formatting klasse, zie klasse UF in KladjeGWT
 * @author huub
 */

class UF
{   // basic formatting with decs decimals
    // no error handling (decs < 0)
    public static String format(double val, int decs)
    {   String result = "";
        if (val == 0)
            return "0";
        // no decimals required
        if (decs == 0)
            result = String.valueOf(Math.round(val));
        else //
        {   // factor for decimal part
            double factor = Math.pow(10, decs);
            // integer part: hard cast to int
            int integerPart = (int) val;
            // fractional part
            int fractionalPart = (int) Math.round(
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
            result = integerString + "." +
            	//DoorzienDWO.rb.getString("decSep") + 
            	fractionalString;
        }
        return result;
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
} // class UF
