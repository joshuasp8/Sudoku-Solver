/*
 * Sudoku Solver v 1.0
 * Written by Joshua Speight [Liquid Pro Quo]
 * August 2011
 *
 * Sudoku Solver.java - Heart of program. Has an instance of the grid,
 * communicates with gui and performs all of the AI neccessary for solving
 * puzzle.
 */


package lpq.sudokusolverui;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Random;
import java.util.Collections;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SudokuSolver 
{
	Grid grid;
        SudokuSolverUI gui;
	public static final int[] checkList = {1,2,3,4,5,6,7,8,9};
        public static int [] initVals;
        public boolean fileChosen;
        public String displayMsg;

        int correctPBIndex; // when used, will tell us which of the preBuilt grids to return
        public ArrayList <int[]> preBuilds;

        public Random ran = new Random();
	
	public SudokuSolver()
	{
		grid = new Grid();
		//printGrid();
		
		
	}
	
	public SudokuSolver(ArrayList<Row> rs, ArrayList<Column> cs, ArrayList<Box> bs)
	{
		grid = new Grid(rs,cs,bs);
	}
	
	
	public SudokuSolver(ArrayList<Row> rs)
	{
		grid = new Grid(rs);
		printGrid();
	}
	
	// Prob the constructor of choice
	public SudokuSolver(int [] gd)
	{
		
		grid = new Grid(gd); 
		//printGrid();
                gui = new SudokuSolverUI(this);
                gui.setVisible(true);
                gui.setInitVals(grid.grid);
                gui.setTitle("Sudoku Solver:Joshua Speight");
                fileChosen = false;
                displayMsg = "Solving...";

                correctPBIndex = -1;
                preBuilds = new ArrayList<int[]>();
                initPreBuilts();
               
	}

        //To avoid wasting time on harder sparse grids, heres some pre builds
        public void initPreBuilts()
        {
            int [] temp = {1,2,3,4,5,6,7,8,9,
                           4,8,9,3,2,7,5,1,6,
                           6,5,7,1,9,8,4,2,3,
                            5,7,4,2,1,3,6,9,8,
                            3,6,1,8,4,9,2,7,5,
                            2,9,8,6,7,5,3,4,1,
                            8,1,2,5,6,4,9,3,7,
                            9,4,5,7,3,1,8,6,2,
                            7,3,6,9,8,2,1,5,4};

            preBuilds.add(temp);
            // do more later, maybe, or perhaps store in a file somewhere
        }
	
	//Returns an arraylist of the missing numbers in order
	public static ArrayList findMissingNums(int[] array, int[] checkList)
	{
		ArrayList toReturn;
		toReturn = new ArrayList();
		for(int i = 0; i < checkList.length;i++)
		{
			for(int j = 0; j < array.length; j++)
			{
				if(checkList[i] == array[j]) //match?
				{
					break;//then skip this num
				}
				else
				{
					if(j == array.length-1) //went thru entire list?
					{
						//congrats, winner
						toReturn.add(checkList[i]); // add missing number
						
					}
				}
			}
		}
		
		return toReturn;
	}
	
	public static ArrayList getFound(int[] array)
	{
		ArrayList toReturn = new ArrayList();
		
		for(int i  = 0; i  < array.length;i++)
		{
			if(array[i] != 0)
				toReturn.add(array[i]);
		}
		
		return toReturn;
	}

        //Returns a list of the indices with missing values in order
        public static ArrayList getMissingIndices(int [] array)
        {
            ArrayList toReturn = new ArrayList();

            for(int i = 0; i < array.length;i++)
            {
                if(array[i] == 0)
                    toReturn.add(i);
            }

            return toReturn;
        }
	
	//Test to see if findMissingNums worked
	private void test1(int [] a)
	{
		ArrayList missing = findMissingNums(a,checkList);
		
		for(int i = 0; i < missing.size();i++)
		{
			System.out.println(missing.get(i));
		}
	}
	
	//Prints the Grid
	public void printGrid()
	{
		if(grid == null)
		{
			pln("No Grid Available!");
			return;
		}
		
		for(int i = 0; i < grid.rows.size();i++)
		{
			for(int j = 0; j < grid.rows.get(i).items.length;j++)
			{
				print(""+grid.rows.get(i).items[j]+" ");
			}
			pln("");
		}
		
	}
	
	// Print stuff cuz I hate typing system.etc
	public void print(String str)
	{
		System.out.print(str);
	}
	//Same as above, although I did sometimes use system.etc throughout anyway
	public void pln(String str)
	{
		System.out.println(str);
	}

        //solve

        //lets see if we dont even have to waste time
        public boolean checkPreBuilds(int [] vals)
        {
            //go thru input; if aside from the blanks, everything matches, we have
            //our answer already!

            boolean failed;
            correctPBIndex = -1; //reset for good measure

            for(int j = 0; j < preBuilds.size();j++) // j because i started with the middle first
            {
                failed = false;
                for(int i = 0; i < 81;i++)
                {
                    if(vals[i] == 0)
                        continue;

                    if(vals[i] != preBuilds.get(j)[i]) //only checking oneToNineH here, but in future will do more
                    {
                        if(j == preBuilds.size()-1) //last one?
                            return false;
                        else
                        {
                            failed  = true;
                            break; //otherwise check next prebuild
                        }
                    }
                }

                //made it thru all 81 with no prob? Match!
                if(!failed)
                {
                    this.correctPBIndex = j;
                    break;
                }
                //otherwise next
            }

            return true;
        }

        //Here we use human-like algorithmns (I literally just translated the process
        //I used while solving a sudoku puzzle into code) to solve the given puzzle
        public boolean solve(int turn)
        {
           
            for(int  i = 0; i < 10; i++) //on average takes about 5 iterations on the hardest puzzles
            {                           //10's here to be safe

                // ill let it try this 10 times and see how far it can get
                //unless we're all done already; (which we should be!)
                if(this.prelimCompleteCheck())
                {
                    System.out.println("Finished in " + (i+1) + " cycles !");
                    break;
                }
                checkPotentials();
                grid.updateAllFromGrid();
            }
            if(!prelimCompleteCheck()) // still no?
            {
                educatedGuessing(turn); // alright then fine, we'll solve like someone who's better
                                        //at sudoku than I am
            }
            //printGrid();
            displayMsg = "Sorry, Puzzle was too hard =[";
            if(grid.sanitizeOutPut())
            {
                if(prelimCompleteCheck())
                {
                    displayMsg = "Puzzled Solved!!";
                    gui.setResultVals(grid.grid);
                    return true;
                }
                else
                {
                    gui.setResultVals(new int [81]);
                    return false;
                }
            }
            else
            {
                displayMsg = "Sorry, Puzzle Resolved Nonsensically";
                gui.setResultVals(new int [81]);
                return false;
            }

            //return true;
        }


        //When Solving like a human doesn't give us the win, we instead
        //attempt to solve like a cyborg
        public void educatedGuessing(int turn)
        {
            ArrayList <Panel> rem = new ArrayList<Panel>(); // remaining panels

            //lets gather all the still blank tiles
            for(int i = 0; i < 81; i++)
                if(grid.panelGrid[i].value == 0)
                    rem.add(grid.panelGrid[i]);

            //to avoid the backtracking stuff, when we end up failing a search, instead of going back
            //we simply continue and if by the end we cant find a winning combo, we just re-sort
            //the array and try again, no biggie

            //now for efficiency purposes, lets sort them with the easiest to solve first
            if(turn == 0)
            {
                Collections.sort(rem);
            }
            if(turn  == 2)
            {
                Collections.reverse(rem); //c'mon, just work already
            }
            if(turn > 2)
                Collections.shuffle(rem); // No, YOU give up ='[

            this.bruteForceRemaining(rem);


            //we're just trying different permutations, until we get one that
            //doesn't ruin the puzzle solving; the re-sorting works on probability
            //since there are many tiles that when guessed correctly will lead us into
            //the right direction, so theres no need to find any 1 specific panel.
        }

        //Core AI of program
        public void checkPotentials()
        {
            //first lets check for panels with only 1 option
            checkSinglePotential(); // actually this is all we do lol
        }

        //Brute Force Quote-UnQuote; only does as much as neccessary, guesses a bit
        //then lets the human-like thinking resume, so whats implemented is more of a
        //faux-backtracking algorithmn: less actual backtracking and more re-sorting array
        //until a suitable permutation is found and passed in here where the program may
        //attempt to solve like a cyborg as opposed to a human; as a result, some sparse
        //grids may take muc much longer to solve than others

        public void bruteForceRemaining(ArrayList <Panel> rem)
        {
            int [] lkGrid = new int [81]; // last known good grid
            System.arraycopy(grid.grid, 0, lkGrid, 0, 81);
            int [] permGrid = new int [81]; // back up of last known
            System.arraycopy(grid.grid, 0, permGrid, 0, 81);

            //int trustRecord = -1;
            //boolean betrayed = false; // used if a panel misleads us into thinking its value is correct

            // now here i will allow a panel go cycle thru its potentials, and then reattempt to solve
            //until an inconsistency is hit or a solution is found (*crosses fingers*)...or something
            for(int i = 0; i < rem.size();i++)
            {
                Panel p = rem.get(i);
                ArrayList<Integer> backUpPot = new ArrayList<Integer>(p.potentials);

                for(int j = 0; j < backUpPot.size();j++)
                {
                   
                    boolean abandoned = false; // did we jump ship mid-way for some reason?
                    int currVal = backUpPot.get(j);
                    p.potentials.clear();
                    p.potentials.add(currVal);

                    for(int k = 0; k < 10; k++)
                    {
                        if(!grid.sanitizeOutPut()) // inconsistency?
                        {
                            pln("Panel: "+p.rowNum +","+p.colNum + " triggered inconsistancy.");
                            pln("value: "+ p.value);
                            if(j == backUpPot.size()-1)
                            {
                                System.out.println("What? Serious Problem!"); //lkGrid has betrayed us!
                                produceGrid(permGrid);//emergency backup, grid reset
                                abandoned = true; //instead of backtracking, lets just continue
                                //betrayed = true;
                                //j=-1; // since it honestly hasnt had its fair shot, due to lkGrid's lies
                                //^ whoops, apparently that causes the occassional inifnite loop, go figure
                                break;
                                //return;
                            }
                            produceGrid(lkGrid); // reset grid
                            pln("grid reset");
                            abandoned = true;
                            break; // lets try a diff value

                        }

                        if(prelimCompleteCheck() && grid.sanitizeOutPut())
                        {

                            return; // whoo! we did it!
                        }
                        checkPotentials();
                        grid.updateAllFromGrid();
                    }
                    //still no? =[
                    if(grid.sanitizeOutPut()) // no inconsistencies at least?
                    {
                        if(abandoned)
                        {
                            abandoned = false;
                            continue;
                        }
                        pln("No Entries Successfully Finish; CurrPanel: "+p.rowNum +","+p.colNum);
                        pln("value: "+ p.value);
                        pln("Potentials "+ p.potentials);
                        pln("Panel Value Tried: "+backUpPot.get(j));

                        //*whew, then lets at least update the backup grid*
                        //trustRecord = i; // denoting that this update is hopefully trustworthy
                        System.arraycopy(grid.grid, 0, lkGrid, 0, 81);
                        produceGrid(lkGrid);

                        //also I suppose theres no need to keep checking potentials
                        //for this panel, since this 1 worked? so
                        break;
                    }
                    //other continue to next value

                }

            }

        }

        // simple and sweet
        public void checkSinglePotential()
        {
            for(int i = 0; i < 81; i++)
            {
                if(grid.panelGrid[i].potentials.size() == 1)
                {
                    grid.grid[i] = grid.panelGrid[i].potentials.get(0);
                    grid.panelGrid[i].potentials.clear(); // so we dont constantly reset these
                }
            }
            grid.updateAllFromGrid();

        }

        //checks that at minimum all boxes are filled; ie: no 0's
        //returns true if this is the case, false otherwise
        public boolean prelimCompleteCheck()
        {
            for(int i = 0; i < 81; i++)
                if(grid.grid[i] == 0)
                    return false;

            return true;
        }

        public int loadGridFromFile() throws IOException
        {

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Sudoku Formatted Text Files", "txt");
            chooser.setFileFilter(filter);

            //lets get the application's current directory
            File tmp = new File(".");
            chooser.setCurrentDirectory(tmp.getAbsoluteFile());

            int returnVal = chooser.showOpenDialog(gui);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
                fileChosen = true;
            }
            if(chooser.getSelectedFile() == null)
                return JFileChooser.CANCEL_OPTION;
            
            int [] gd = new int[81];
            int gc = 0;//grid count
            File f = chooser.getSelectedFile();//new File("input1.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            //we assume the input type is a 9x9 int box
            for(int i = 0; i < 9; i++)
            {
                String row = br.readLine();
                for(int j = 0; j < 9; j++)
                {
                    if(row.charAt(j) != '.' && row.charAt(j) != '*')
                    {
                       String s = ""+row.charAt(j);
                       gd[gc] = Integer.parseInt(s);
                       gc++;
                    }
                    else
                    {
                        gd[gc] = 0;
                        gc++;
                    }

                }
            }

            produceGrid(gd);
            return returnVal;

        }

        public void produceGrid(int [] gd)
        {
            //grid.grid = gd;
            System.arraycopy(gd, 0, grid.grid, 0, 81); // try this instead
            //gui.setInitVals(gd);
            //gui.setFieldsFromInit();
            grid.cleanPanels();
            grid.updateAllFromGrid();
            //printGrid();
        }

}
