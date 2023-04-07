package fi.doorziengwt.client;

/**
 * incidence matrix for Aad Goddijn's painting algorithm, see class IncidenceMatrix in Grafiek3DGWT
 * @author huub
 */

public class IncidenceMatrix
{   int size;
    int[][] matrix;
    int maxCnt;
    int[] maxRows;
    int[] rowSums;
    int rowMax;
    public IncidenceMatrix(int s)
    {   size = s;
        matrix = new int[size][size];
    }    
    public void setElement(int i, int j, int val)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            matrix[i][j] = val;
    }    
    public int getElement(int i, int j)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            return matrix[i][j];
        else
            return 0;
    }    
    public void relax()
    {   for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)        
            {   
                if (matrix[i][j] == matrix[j][i])
                {   matrix[i][j] = 0;
                    matrix[j][i] = 0;
                }    
            }
    }
    // checkColumns bevat de indices van de rijen die nog meedoen
    public void findMaximalElements(int[] checkColumns, int maxType)
    {   int numRows = checkColumns.length;
        switch (maxType)
        {   case 0:
            {   maxCnt = 0;
                // te groot
                maxRows = new int[numRows];
                // vindt indices in checkColums van rijen waarin 
                // alleen nullen of enen staan in de kolommen
                // in checkColumns
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
    // checkColumns bevat de indices van de rijen die nog meedoen
    // i is an index from checkColumns
    public boolean isTopRow(int i, int[] checkColumns)
    {   boolean result = true;
        for (int cnt = 0; cnt < checkColumns.length; cnt++)
        {   result = result && (matrix[i][checkColumns[cnt]] >= 0);
        }
        return result;
    }    
    // find the rowSums of the matrix consisting only of
    // checkColumns rows AND columns
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
    
    // find the rows of the matrix consisting only of
    // checkColumns rows AND columns, which
    // contain the minimum number of occurences of -1
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

