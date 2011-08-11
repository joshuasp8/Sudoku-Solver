package lpq.sudokusolverui;

import java.util.ArrayList;


public class SudokuSolverMain 
{
	public static void main(String [] args)
	{
		int[] gd = new int[81];
		//gd[0] = 1;gd[1] = 2;gd[2] = 3;gd[3] = 4;gd[4] = 5;
                //gd[5] = 6;gd[6] = 7;gd[7] = 8;
		
		new SudokuSolver(gd);
	}

}
