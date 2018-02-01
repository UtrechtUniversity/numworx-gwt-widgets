package fi.grafiek3dgwt.client;


/**
 * square incidence matrix used in Aad's painting algorithm 
 * (see class Object3D); initially the matrix contains 
 * the values +1, 0 and -1 
 * @author huub
 */

class IncidenceMatrix
{   
	/**
	 * size of the square incidence matrix
	 */
	int size;
	/**
	 * the integer incidence matrix
	 */
    int[][] matrix;
    /**
     * counter for numner of relevant elements in maxRows  
     */
    int maxCnt;
    /**
     * place holder for row indices of rows containing only
     * zeros and +1's 
     */
    int[] maxRows;
    /**
     * place holder for row sums (of rows still in the game)
     */
    int[] rowSums;
    /**
     * place holder for maximum row sum (of rows still in the game) 
     */
    int rowMax;
    /**
     * constructor
     * @param s size of the square matrix
     */
    public IncidenceMatrix(int s)
    {   size = s;
        matrix = new int[size][size];
    }    
    /**
     * set matrix[i][j] to val
     * @param i row position
     * @param j column position
     * @param val new value for matrix[i][j] 
     */
    public void setElement(int i, int j, int val)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            matrix[i][j] = val;
    }    
    
    /**
     * get matrix[i][j]
     * @param i row position
     * @param j column position
     * @return matrix[i][j]
     */
    public int getElement(int i, int j)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            return matrix[i][j];
        else
            return 0;
    }
    
    /**
     * put symmetric elements thus also the
     * diagonal) to zero
     */
    public void relax()
    {   for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)        
            {   if (matrix[i][j] == matrix[j][i])
                {   matrix[i][j] = 0;
                    matrix[j][i] = 0;
                }    
            }
    }
    /**
     * find the indices of rows from checkColumns that   
     * maxType == 0: only contain zeros and +1's, 
     * maxType == 1: sum of the -1's equals rowMax 
     * in both cases there are maxCnt such rows with indices in the '
     * first maxCnt elements of maxRows  
     * @param checkColumns indices of rows and columns still in the game
     * @param maxType type of search
     */
    public void findMaximalElements(int[] checkColumns, int maxType)
    {   int numRows = checkColumns.length;
        switch (maxType)
        {   case 0:
            {   maxCnt = 0;
                maxRows = new int[numRows];
                for (int fCnt = numRows - 1; fCnt >= 0; fCnt--)
                {   if (isTopRow(checkColumns[fCnt], checkColumns))
                    {   maxRows[maxCnt] = fCnt;
                        maxCnt++;
                    }    
                }

            }    
            break;
            case 1:
            {   maxCnt = 0;
                maxRows = new int[numRows];
                findRowSums2(checkColumns);
                for (int fCnt = numRows - 1; fCnt >= 0; fCnt--)
                {   if (rowSums[fCnt] == rowMax)
                    {   maxRows[maxCnt] = fCnt;
                        maxCnt++;
                    }    
                }                
            }    
            break;
            default: //none?
        }
    }
    /**
     * check if row i of the matrix contains only zeros and +1's
     * @param i an index from checkColumns 
     * @param checkColumns indices of rows and columns still in the game
     * @return true/false
     */
    public boolean isTopRow(int i, int[] checkColumns)
    {   boolean result = true;
        for (int cnt = 0; cnt < checkColumns.length; cnt++)
        {   result = result && (matrix[i][checkColumns[cnt]] >= 0);
        }
        return result;
    }    
    /**
     * find the row sums and the maximum row sum of the matrix
     * consisting of rows and columns with indices in checkColumns 
     * @param checkColumns indices of rows and columns still in the game
     */
    public void findRowSums(int[] checkColumns)
    {   int numRows = checkColumns.length;
        rowMax = -10;
        rowSums = new int[numRows];
        for (int rowCnt = 0; rowCnt < numRows; rowCnt++)
        {   int sum = 0;
            for (int colCnt = 0; colCnt < numRows; colCnt++)
                sum += matrix[checkColumns[rowCnt]][checkColumns[colCnt]];
            rowSums[rowCnt] = sum;
            if (sum > rowMax)
                rowMax = sum;
        }
    }
    
    /**
     * find the number of occurrences of -1 and the minimum number of
     * such occurrences (store these in rowSums and rowMax) of the matrix
     * consisting of rows and columns with indices in checkColumns
     * @param checkColumns indices of rows and columns still in the game
     */
    public void findRowSums2(int[] checkColumns)
    {   int numRows = checkColumns.length;
        rowMax = -10000;
        rowSums = new int[numRows];
        for (int rowCnt = 0; rowCnt < numRows; rowCnt++)
        {   int sum = 0;
            for (int colCnt = 0; colCnt < numRows; colCnt++)
            {   if (matrix[checkColumns[rowCnt]][checkColumns[colCnt]] == -1)
                    sum += matrix[checkColumns[rowCnt]][checkColumns[colCnt]];
            }    
            rowSums[rowCnt] = sum;
            if (sum > rowMax)
                rowMax = sum;
        }
    }
    
    /**
     * redefined
     */
    public String toString()
    {   String result = "";
        for (int i = 0; i < size; i++)
        {   result += "row " + i + " = ";
            for (int j = 0; j < size; j++)
                result += matrix[i][j] + " ";                
            result += "\n";
        }
        return result;
    }    
}    


