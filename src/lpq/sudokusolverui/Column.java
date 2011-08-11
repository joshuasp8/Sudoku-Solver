/*
 * Sudoku Solver v 1.0
 * Written by Joshua Speight [Liquid Pro Quo]
 * August 2011
 *
 *  * Column.java - Column class represents a single column on the grid.
 * 9 of these per grid.
 */


package lpq.sudokusolverui;

import java.util.ArrayList;


public class Column 
{
	int colNum;
	int[] items;
	ArrayList <Integer> found;
	ArrayList <Integer> missing;
        Panel[] panelItems;
        ArrayList <Integer> missingIndices;
	
	Column(int[] array, int i)
	{
            panelItems = new Panel[9];
            colNum = i;
            items= array;
            found = SudokuSolver.getFound(array);
            missing = SudokuSolver.findMissingNums(array, SudokuSolver.checkList);
            missingIndices = SudokuSolver.getMissingIndices(array);

	}
	
	public void addToFound(int x)
	{
		found.add(x);
		missing.remove(missing.indexOf(x));
	}


        public void updateLists()
        {
            found = SudokuSolver.getFound(items);
            missing = SudokuSolver.findMissingNums(items, SudokuSolver.checkList);
            missingIndices = SudokuSolver.getMissingIndices(items);
        }

        public void initializePanels()
       {
            for(int i = 0; i < 9; i++)
            {
                panelItems[i].col = this;
                panelItems[i].colNum = colNum;
                panelItems[i].colPos = i;
            }
       }

        //Returns whether or not this structure currently contains a given number
       public boolean contains(int x)
       {
           for(int i = 0; i < 9; i++)
           {
               if(items[i] == x)
                   return true;
           }
           return false;
       }
}
