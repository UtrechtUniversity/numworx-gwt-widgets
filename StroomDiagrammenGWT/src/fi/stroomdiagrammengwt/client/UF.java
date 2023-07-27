package fi.stroomdiagrammengwt.client;

/**
 * universal formatter class, containing only static methods
 */
class UF
{   
	/**
	 * format a double with decs decimals
	 * uses the decimal separator decSep() from StrooomDiagrammenGWT.rb
	 * @param val double to be formatted
	 * @param decs number of decimals required
	 * @return double formatted as required in a String
	 */
    public static String format(double val, int decs)
    {   String result = "";
        // no decimals required
        if (decs <= 0)
            result = String.valueOf(Math.round(val));
        else //
        {   // factor for decimal part
            double factor = Math.pow(10, decs);
            // integer part: hard cast to int
            int integerPart = (int) val;
            // fractional part
            int fractionalPart = (int) Math.round(Math.abs(val - integerPart) * factor);
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
            result = integerString +  
                     StroomDiagrammenGWT.rb.decSep() + 
                     fractionalString;
        }
        return result;
    }

    /**
     * as method format but also displaying the +-sign
     * @param val double to be formatted 
     * @param decs number of decimals required
     * @return double formatted as required in a String
     */
    public static String sformat(double val, int decs)
    {   String result = format(val, decs);
        if (val >= 0)
            result = "+" + result;
        return result;
    }

    /**
     * as method format but with the formatted String aligned (0=center, 1=left, 2=right)
     * within a String of width characters (if possible) 
     * @param val double to be formatted
     * @param decs number of decimals required
     * @param width width of enclosing String
     * @param al alignment
     * @return double formatted as required in a String
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
     * return a String of c zerps
     * @param c number of zeros
     * @return String of c zeros
     */
    private static String zeros(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += "0";
        return result;
    }

    /**
     * return a String of c spaces
     * @param c number of spaces
     * @return String of c spaces
     */
    private static String spaces(int c)
    {   String result = "";
        for (int i = 1; i <=c; i++)
            result += " ";
        return result;
    }
} 
