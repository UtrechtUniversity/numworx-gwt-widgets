/*
 * Created on Mar 18, 2005
 *
 */
package fi.tekenveelvlakgwt.client;

//import java.util.Random;

/*

 StringUtils.java

 Created: 24 March 2000
 Release: $Name:  $
 Version: $Revision: 1.10 $
 Last Mod Date: $Date: 2001/08/15 02:34:02 $
 Module By: Jonathan Abbey, jonabbey@arlut.utexas.edu

 -----------------------------------------------------------------------
 
 Ganymede Directory Management System

 Copyright (C) 1996, 1997, 1998, 1999, 2000, 2001
 The University of Texas at Austin.

 Contact information

 Web site: http://www.arlut.utexas.edu/gash2
 Author Email: ganymede_author@arlut.utexas.edu
 Email mailing list: ganymede@arlut.utexas.edu

 US Mail:

 Computer Science Division
 Applied Research Laboratories
 The University of Texas at Austin
 PO Box 8029, Austin TX 78713-8029

 Telephone: (512) 835-3200

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA

 */

/**
 * <P>
 * This class contains a variety of utility String manipulating static methods.
 * </P>
 * @author Jonathan Abbey, jonabbey@arlut.utexas.edu - tweaked by M.J.B. Kupers
 * @see <a href="http://tools.arlut.utexas.edu/gash2/">Gash2</a>
 */
public class StringUtils {

    /**
     * <P>
     * This method strips out any characters from inputString that are not
     * present in legalChars.
     * </P>
     * 
     * <P>
     * This method will always return a non-null String.
     * </P>
     * @param inputString The string to strip characters from.
     * @param legalChars The characters that are allowed. The other characters will be removed.
     * @return A string with only the legalChars.
     */
    public static String strip(String inputString, String legalChars) {
        if (inputString == null || legalChars == null) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            if (legalChars.indexOf(c) != -1) {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }

    /**
     * <P>
     * This method tests to see if inputString consists of only characters
     * contained within the legalChars string. If inputString contains no
     * characters not contained within legalChars, containsOnly() will return
     * true, otherwise it will return false.
     * </P>
     * @param inputString The string to check 
     * @param legalChars The characters that are allowed
     * @return true/false
     * <P>
     * Note that containsOnly will always return true if inputString is null.
     * </P>
     */
    public static boolean containsOnly(String inputString, String legalChars) {
        if (inputString == null || inputString.length() == 0) {
            return true;
        }

        if (legalChars == null || legalChars.length() == 0) {
            return false;
        }

        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            if (legalChars.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * <P>
     * This method takes an inputString and counts the number of times that
     * patternString occurs within it.
     * </P>
     * @param inputString The string to check
     * @param patternString The substring whose occurrence should be counted
     * @return number of occurrences of patternString
     */
    public static int count(String inputString, String patternString) {
        int index = 0;
        int count = 0;

        /* -- */

        while (true) {
            index = inputString.indexOf(patternString, index);

            if (index == -1) {
                break;
            } else {
                index += patternString.length();
                count++;
            }
        }

        return count;
    }

    /**
     * <P>
     * This method takes a (possibly multiline) inputString containing
     * subsequences matching splitString and returns an array of Strings which
     * contain the contents of the inputString between instances of the
     * splitString. The splitString divider will not be returned in the split
     * strings.
     * </P>
     * @param inputString The string to split
     * @param splitString The divider substring 
     * @return array of pieces of inputString after deleting all occurrences of splitString   
     * <P>
     * In particular, this can be used to split a multiline String into an array
     * of Strings by using a splitString of "\n". The resulting strings will not
     * include their terminating newlines.
     * </P>
     */
    public static String[] split(String inputString, String splitString) {
        int index;
        int count = StringUtils.count(inputString, splitString);
        int upperBound = inputString.length();
        String results[] = new String[count + 1];

        /* -- */

        index = 0;
        count = 0;

        while (index < upperBound) {
            int nextIndex = inputString.indexOf(splitString, index);

            if (nextIndex == -1) {
                results[count++] = inputString.substring(index);
                return results;
            } else {
                results[count++] = inputString.substring(index, nextIndex);
            }

            index = nextIndex + splitString.length();
        }

        // we should never get here

        return results;
    }

    /**
     * <p>
     * This method behaves like String.replace(), but replaces substrings rather
     * than chars.
     * </p>
     * @param inputString The string in which to replace substring
     * @param splitString The substring to replace
     * @param joinString The substring to replace by 
     * @return inputPutString with all occurrences of splitString replaced by joinString   
     */

    public static String replaceStr(String inputString, String splitString,
            String joinString) {
        StringBuffer buffer = new StringBuffer();
        String[] elems = split(inputString, splitString);

        for (int i = 0; i < elems.length; i++) {
            if (i != 0) {
                buffer.append(joinString);
            }

            if (elems[i] != null) {
                buffer.append(elems[i]);
            }
        }

        return buffer.toString();
    }

    /*private static Random rn = new Random();

    private static int rand(int lo, int hi) {
        int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0)
            i = -i;
        return lo + i;
    }

    private static String randomstring(int lo, int hi) {
        int n = rand(lo, hi);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++)
            b[i] = (byte) rand('a', 'z');
        return new String(b, 0);
    }

    public static String randomstring() {
        return randomstring(10, 20);
    }*/
    
}