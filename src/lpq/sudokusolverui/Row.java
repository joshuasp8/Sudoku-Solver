/*
 * Sudoku Solver v 1.0
 * Written by Joshua Speight [Liquid Pro Quo]
 * August 2011
 *
 * Row.java - Row class represents a single column on the grid.
 * 9 of these per grid.
 */


package lpq.sudokusolverui;

import java.util.ArrayList;

public class Row
{
	int rowNum;
	int[] items;
	ArrayList <Integer> found;
	ArrayList <Integer> missing;
        ArrayList <Integer> missingIndices;
        Grid grid;
        Panel[] panelItems;
	
	Row(int[] array, int i)
	{
            rowNum = i;
            items  = array;
            found = SudokuSolver.getFound(array);
            missing = SudokuSolver.findMissingNums(array, SudokuSolver.checkList);
            missingIndices = SudokuSolver.getMissingIndices(array);
	}
	
	public void addToFound(int x)
	{
		found.add(x);
		missing.remove(missing.indexOf(x));
	}

        //keeping the garbage collector busy here, since this is kinda reckless
        //with memory, but nbd
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
                panelItems[i].row = this;
                panelItems[i].rowNum = rowNum;
                panelItems[i].rowPos = i;
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

};