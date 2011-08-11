/*
 * Sudoku Solver v 1.0
 * Written by Joshua Speight [Liquid Pro Quo]
 * August 2011
 *
 * Grid.java - Grid class represents the entire board and keeps track of all of
 * the important information occurring on the board at all times, and keeps
 * all row/column/box objects in sync. Various update methods available, where
 * use varies by situation.
 */


package lpq.sudokusolverui;

import java.util.ArrayList;


public class Grid 
{
	public final int[] checkList = {1,2,3,4,5,6,7,8,9};
	
	
	
	ArrayList <Row> rows;
	ArrayList <Column> columns;
	ArrayList <Box> boxes;
        int[] grid;
        Panel[] panelGrid;
	
	// the most unlikely instance
	public Grid(ArrayList<Row> rs, ArrayList<Column> cs, ArrayList<Box> bs)
	{
		rows = rs;
		columns = cs;
		boxes = bs;
	}
	
	//this constructor simply populates with 0's
	public Grid()
	{
		rows = new ArrayList<Row>();
		columns = new ArrayList<Column>();
		boxes = new ArrayList<Box>();
		
		for(int i = 0; i < 9; i++)
		{
			Row a = new Row(new int[]{0,0,0,0,0,0,0,0,0}, i);
			rows.add(a);
		}
		for(int i = 0; i < 9; i++)
		{
			Column a = new Column(new int[]{0,0,0,0,0,0,0,0,0}, i);
			columns.add(a);
		}
		for(int i = 0; i < 9; i++)
		{
			Box a = new Box(new int[]{0,0,0,0,0,0,0,0,0}, i);
			boxes.add(a);
		}
		
	}
	//Likely Input
	public Grid(ArrayList<Row> rs)
	{
		//we have all the rows now just put them into columns and boxes
		rows = rs;
		getColumnsFromRows();
		getBoxesFromRows();
	}
	
	//Constructor of choice
	public Grid(int [] gd)
	{
		//all 81 spots, in order left to right, top to bottom
		grid = gd;
                panelGrid = new Panel[81];
                populatePanelGrid();
                updatePanelGrid();
                
                
		initializeAllFromGrid();

                assignPanels();

	}

        // This method distributes all the panels of the grid to the respective
        //rows/columns/3x3 boxes of the board. This is important as it
        //now makes communication trivial btwn the different types
        public void assignPanels()
        {
            //First Rows
            //we'll get the rows from the main grid
            for(int i = 0; i < 9; i++)
            {
                //each 9 panels is a new row
                Panel[] temp = {panelGrid[i*9+0],panelGrid[i*9+1],panelGrid[i*9+2],panelGrid[i*9+3],
                panelGrid[i*9+4],panelGrid[i*9+5],panelGrid[i*9+6],panelGrid[i*9+7],panelGrid[i*9+8]};
                rows.get(i).panelItems = temp;
                rows.get(i).initializePanels();
            }

            //This time we'll get the columns from rows
            for(int i =0; i < 9; i++)
            {
                for(int j = 0; j < 9; j++)
                {
                    //simple inversion; works cuz its a square
                    columns.get(i).panelItems[j] = rows.get(j).panelItems[i];
                }
                columns.get(i).initializePanels();
            }

            //Finally The boxes, which will most likely be a headache
            // Note: just copy and pasting from the mthod boxes from rows below
            for(int i = 0; i < boxes.size(); i++)
		{
                    for(int j = 0; j < rows.size(); j++)
                    {
                    //1st column of boxes
                        if(i%3 == 0)
			{
                            if(j < 3)
                                boxes.get(i).panelItems[j] = rows.get(i).panelItems[j];
                            else if(j < 6)
                                boxes.get(i).panelItems[j] = rows.get(i+1).panelItems[j- 3]; //jump j back 3, since we moved frwd
                            else if(j < 9)
				boxes.get(i).panelItems[j] = rows.get(i+2).panelItems[j-6]; // now 6 since we moved 3 again
			}
			//2nd column of boxes
			if(i%3 == 1)
			{
                            if(j < 3)
                                boxes.get(i).panelItems[j] = rows.get(i-1).panelItems[j+3]; // start j off 3 ahead since
                            								// now we want columns 3,4 and 5
                            else if(j < 6)
                                boxes.get(i).panelItems[j] = rows.get(i).panelItems[j]; //push back 3 to remain in same spot
                            else if(j < 9)
				boxes.get(i).panelItems[j] = rows.get(i+1).panelItems[j-3]; // you get it

			}
			//Last column of boxes
			if(i % 3  == 2)
			{
                            if(j < 3)
                                boxes.get(i).panelItems[j] = rows.get(i-2).panelItems[j+6]; // same deal as above
                            else if(j < 6)
				boxes.get(i).panelItems[j] = rows.get(i-1).panelItems[j+3];
                            else if(j < 9)
				boxes.get(i).panelItems[j] = rows.get(i).panelItems[j];

			}

                    }
                    boxes.get(i).initializePanels();

		}


        }
	
	public void cleanInput()
	{
		
	}
	
	//does what it sounds like
	private void  getColumnsFromRows()
	{
		
		//ArrayList<int[]> toReturn = new ArrayList<int[]>();
		columns = new ArrayList<Column>();
		for(int i = 0; i < 9;i++)
		{
			columns.add(new Column(new int[9],i)); //hardcoding the 9, makes it less flexible, but w.e
		}
		
		for(int i = 0; i < columns.size();i++)
		{
			for(int j = 0; j < rows.size();j++)
			{
				//they contain the same info...just inverted, so
				columns.get(j).items[i] = rows.get(i).items[j];// basically a 90 turn
			}
		}
		
		//return toReturn;	
	}
	
	//also does what it sounds like it does
	private void getBoxesFromRows()
	{
		// what constitutes a box is a 3x3 square, but these boxes are only applied to
		//where they are divided on a board, every 3 spaces (horizontally and vertically)
		//you should know what im referencing if uve ever seen the board
		//anyway the box goes from 1-9, first 3 are the first row of the box, next 3
		//2nd, and final 3 3rd; straightforward
		
		boxes = new ArrayList<Box>();
		
		for(int i = 0; i < 9;i++)
		{
			boxes.add(new Box(new int[9], i));
		}
		
		//This is trickier and more annoying
		//and god, im sure there's a cleaner looking way to do this,
		//but i didnt feel like thinking too long, it was this or bruteforcing it
		for(int i = 0; i < boxes.size(); i++)
		{
			for(int j = 0; j < rows.size(); j++)
			{
				//1st column of boxes
				if(i == 0 || i == 3 || i == 6)
				{
					if(j < 3)
					{
						boxes.get(i).items[j] = rows.get(i).items[j];
					}
					else if(j < 6)
					{
						boxes.get(i).items[j] = rows.get(i+1).items[j- 3]; //jump j back 3, since we moved frwd
					}
					else if(j < 9)
					{
						boxes.get(i).items[j] = rows.get(i+2).items[j-6]; // now 6 since we moved 3 again
					}
				}
				//2nd column of boxes
				if(i == 1 || i == 4 || i == 7)
				{
					if(j < 3)
					{
						boxes.get(i).items[j] = rows.get(i-1).items[j+3]; // start j off 3 ahead since
					}								// now we want columns 3,4 and 5
					else if(j < 6)
					{
						boxes.get(i).items[j] = rows.get(i).items[j]; //push back 3 to remain in same spot
					}
					else if(j < 9)
					{
						boxes.get(i).items[j] = rows.get(i+1).items[j-3]; // you get it
					}
				}
				//Last column of boxes
				if(i == 2 || i == 5 || i == 8)
				{
					if(j < 3)
					{
						boxes.get(i).items[j] = rows.get(i-2).items[j+6]; // same deal as above
					}
					else if(j < 6)
					{
						boxes.get(i).items[j] = rows.get(i-1).items[j+3];
					}
					else if(j < 9)
					{
						boxes.get(i).items[j] = rows.get(i).items[j];
					}
				}
				
			}
			
		}
		
		//Sorry, that was ugly; hopefully interpretable though
		
		//ArrayList<int[]> toReturn = new ArrayList<int[]>();
		
		//return toReturn;
	}
	
	//takes the list of input and fills rows appropriately
	private void getRowsFromGrid(int[] gd)
	{
		rows = new ArrayList<Row>();
		for(int  i = 0; i < 9; i++)
		{
			int [] a = new int [9];
			for(int j = 0; j < 9; j++)
			{
				a[j] = gd[9*i + j];
			}
			rows.add(new Row(a,i));
		}
			
	}

        //update rows from grid
        public void updateRowsFromGrid()
        {
            for(int  i = 0; i < 9; i++)
		{

			for(int j = 0; j < 9; j++)
			{
				rows.get(i).items[j] = grid[9*i + j];
			}
                        rows.get(i).updateLists();

		}

        }

        public void updateColumnsFromRows()
        {
            //This time we'll update the columns from rows
            for(int i =0; i < 9; i++)
            {
                for(int j = 0; j < 9; j++)
                {
                    //simple inversion; works cuz its a square
                    columns.get(i).items[j] = rows.get(j).items[i];
                }
                columns.get(i).updateLists();
            }

        }

        public void updateBoxesFromRows()
        {
            for(int i = 0; i < boxes.size(); i++)
		{
                    for(int j = 0; j < rows.size(); j++)
                    {
                    //1st column of boxes
                        if(i%3 == 0)
			{
                            if(j < 3)
                                boxes.get(i).items[j] = rows.get(i).items[j];
                            else if(j < 6)
                                boxes.get(i).items[j] = rows.get(i+1).items[j- 3]; //jump j back 3, since we moved frwd
                            else if(j < 9)
				boxes.get(i).items[j] = rows.get(i+2).items[j-6]; // now 6 since we moved 3 again
			}
			//2nd column of boxes
			if(i%3 == 1)
			{
                            if(j < 3)
                                boxes.get(i).items[j] = rows.get(i-1).items[j+3]; // start j off 3 ahead since
                            								// now we want columns 3,4 and 5
                            else if(j < 6)
                                boxes.get(i).items[j] = rows.get(i).items[j]; //push back 3 to remain in same spot
                            else if(j < 9)
				boxes.get(i).items[j] = rows.get(i+1).items[j-3]; // you get it

			}
			//Last column of boxes
			if(i % 3  == 2)
			{
                            if(j < 3)
                                boxes.get(i).items[j] = rows.get(i-2).items[j+6]; // same deal as above
                            else if(j < 6)
				boxes.get(i).items[j] = rows.get(i-1).items[j+3];
                            else if(j < 9)
				boxes.get(i).items[j] = rows.get(i).items[j];

			}

                    }
                    boxes.get(i).updateLists();

		}

        }

        //Takes current changes made to the rows section and applies it to
        //entire grid
        public void updateGridFromRows()
        {
            int gc = 0; // gridcount
            for(int i = 0; i < rows.size();i++)
            {
                for(int j = 0; j < 9; j++)
                {
                    grid[gc] = rows.get(i).items[j];
                    gc++;
                }

            }

            updateAllFromGrid();
        }

        //Takes current changes made to the cols section and applies it to
        //entire grid
        public void updateGridFromColumns()
        {
            int gc = 0;//gridcount
            for(int i = 0; i < columns.size();i++)
            {
                for(int j = 0; j < 9 ; j++)
                {
                    grid[gc] = colConvert(gc);
                    gc++;
                }
            }
            updateAllFromGrid();
        }

        //Takes current changes made to the boxes section and applies it to
        //entire grid
        public void updateGridFromBoxes()
        {
            int gc = 0;//gridcount
            for(int i = 0; i < boxes.size();i++)
            {
                for(int j = 0; j < 9 ; j++)
                {
                    grid[gc] = boxConvert(gc);
                    gc++;
                }
            }
            updateAllFromGrid();
        }

        //looks at current state of the grid array and applies all changes
        //to the lists of rows, cols, and boxes
        public void initializeAllFromGrid()
        {
            getRowsFromGrid(grid);
            getColumnsFromRows();
            getBoxesFromRows();
            updatePanelGrid();

            // remember to update all the lists properly!
            for(int i = 0; i < 9; i++)
            {
                rows.get(i).updateLists();
                columns.get(i).updateLists();
                boxes.get(i).updateLists();
            }
        }

        //Properly update all components
        public void updateAllFromGrid()
        {
            updateRowsFromGrid();
            updateColumnsFromRows();
            updateBoxesFromRows();
            updatePanelGrid();
        }

        public void updatePanelGrid()
        {
            for(int i = 0; i < 81; i++)
            {
                panelGrid[i].value = grid[i];
            }
            
            // This is done afterwards, so as to take all changes into account
            for(int i = 0; i < 81; i++)
            {
                panelGrid[i].updatePotentials();
            }
        }

        public void updateGridFromPanels()
        {
            for(int i = 0; i < 81; i++)
            {
                grid[i] = panelGrid[i].value;
            }
            updateAllFromGrid();
        }

        public void populatePanelGrid()
        {
            for(int i = 0; i < 81; i++)
            {
                panelGrid[i] = new Panel(i);
            }
        }

        //made a converter because its 5am and I don't want to think out
        // a proper formula for these things, so here's a faux-dictionary
        //x is the current grid pos
        public int colConvert(int x)
        {
            int k = -1;
            switch(x)
            {
                case 0: k = columns.get(0).items[0];break;
                case 1: k = columns.get(1).items[0];break;
                case 2: k = columns.get(2).items[0];break;
                case 3: k = columns.get(3).items[0];break;
                case 4: k = columns.get(4).items[0];break;
                case 5: k = columns.get(5).items[0];break;
                case 6: k = columns.get(6).items[0];break;
                case 7: k = columns.get(7).items[0];break;
                case 8: k = columns.get(8).items[0];break;
                case 9: k = columns.get(0).items[1];break;
                case 10: k = columns.get(1).items[1];break;
                case 11: k = columns.get(2).items[1];break;
                case 12: k = columns.get(3).items[1];break;
                case 13: k = columns.get(4).items[1];break;
                case 14: k = columns.get(5).items[1];break;
                case 15: k = columns.get(6).items[1];break;
                case 16: k = columns.get(7).items[1];break;
                case 17: k = columns.get(8).items[1];break;
                case 18: k = columns.get(0).items[2];break;
                case 19: k = columns.get(1).items[2];break;
                case 20: k = columns.get(2).items[2];break;
                case 21: k = columns.get(3).items[2];break;
                case 22: k = columns.get(4).items[2];break;
                case 23: k = columns.get(5).items[2];break;
                case 24: k = columns.get(6).items[2];break;
                case 25: k = columns.get(7).items[2];break;
                case 26: k = columns.get(8).items[2];break;
                case 27: k = columns.get(0).items[3];break;
                case 28: k = columns.get(1).items[3];break;
                case 29: k = columns.get(2).items[3];break;
                case 30: k = columns.get(3).items[3];break;
                case 31: k = columns.get(4).items[3];break;
                case 32: k = columns.get(5).items[3];break;
                case 33: k = columns.get(6).items[3];break;
                case 34: k = columns.get(7).items[3];break;
                case 35: k = columns.get(8).items[3];break;
                case 36: k = columns.get(0).items[4];break;
                case 37: k = columns.get(1).items[4];break;
                case 38: k = columns.get(2).items[4];break;
                case 39: k = columns.get(3).items[4];break;
                case 40: k = columns.get(4).items[4];break;
                case 41: k = columns.get(5).items[4];break;
                case 42: k = columns.get(6).items[4];break;
                case 43: k = columns.get(7).items[4];break;
                case 44: k = columns.get(8).items[4];break;
                case 45: k = columns.get(0).items[5];break;
                case 46: k = columns.get(1).items[5];break;
                case 47: k = columns.get(2).items[5];break;
                case 48: k = columns.get(3).items[5];break;
                case 49: k = columns.get(4).items[5];break;
                case 50: k = columns.get(5).items[5];break;
                case 51: k = columns.get(6).items[5];break;
                case 52: k = columns.get(7).items[5];break;
                case 53: k = columns.get(8).items[5];break;
                case 54: k = columns.get(0).items[6];break;
                case 55: k = columns.get(1).items[6];break;
                case 56: k = columns.get(2).items[6];break;
                case 57: k = columns.get(3).items[6];break;
                case 58: k = columns.get(4).items[6];break;
                case 59: k = columns.get(5).items[6];break;
                case 60: k = columns.get(6).items[6];break;
                case 61: k = columns.get(7).items[6];break;
                case 62: k = columns.get(8).items[6];break;
                case 63: k = columns.get(0).items[7];break;
                case 64: k = columns.get(1).items[7];break;
                case 65: k = columns.get(2).items[7];break;
                case 66: k = columns.get(3).items[7];break;
                case 67: k = columns.get(4).items[7];break;
                case 68: k = columns.get(5).items[7];break;
                case 69: k = columns.get(6).items[7];break;
                case 70: k = columns.get(7).items[7];break;
                case 71: k = columns.get(8).items[7];break;
                case 72: k = columns.get(0).items[8];break;
                case 73: k = columns.get(1).items[8];break;
                case 74: k = columns.get(2).items[8];break;
                case 75: k = columns.get(3).items[8];break;
                case 76: k = columns.get(4).items[8];break;
                case 77: k = columns.get(5).items[8];break;
                case 78: k = columns.get(6).items[8];break;
                case 79: k = columns.get(7).items[8];break;
                case 80: k = columns.get(8).items[8];break;

                //by the time i realized this probably wasnt easier
                //I was already in too deep =[
            }


            return k;
        }

        //made a converter because its 5am and I don't want to think out
        // a proper formula for these things, so here's a faux-dictionary
        //x is the current grid pos
        public int boxConvert(int x)
        {
            int k = -1;
            switch(x)
            {
                case 0: k = boxes.get(0).items[0];break;
                case 1: k = boxes.get(0).items[1];break;
                case 2: k = boxes.get(0).items[2];break;
                case 3: k = boxes.get(1).items[0];break;
                case 4: k = boxes.get(1).items[1];break;
                case 5: k = boxes.get(1).items[2];break;
                case 6: k = boxes.get(2).items[0];break;
                case 7: k = boxes.get(2).items[1];break;
                case 8: k = boxes.get(2).items[2];break;
                case 9: k = boxes.get(0).items[3];break;
                case 10: k = boxes.get(0).items[4];break;
                case 11: k = boxes.get(0).items[5];break;
                case 12: k = boxes.get(1).items[3];break;
                case 13: k = boxes.get(1).items[4];break;
                case 14: k = boxes.get(1).items[5];break;
                case 15: k = boxes.get(2).items[3];break;
                case 16: k = boxes.get(2).items[4];break;
                case 17: k = boxes.get(2).items[5];break;
                case 18: k = boxes.get(0).items[6];break;
                case 19: k = boxes.get(0).items[7];break;
                case 20: k = boxes.get(0).items[8];break;
                case 21: k = boxes.get(1).items[6];break;
                case 22: k = boxes.get(1).items[7];break;
                case 23: k = boxes.get(1).items[8];break;
                case 24: k = boxes.get(2).items[6];break;
                case 25: k = boxes.get(2).items[7];break;
                case 26: k = boxes.get(2).items[8];break;
                case 27: k = boxes.get(3).items[0];break;
                case 28: k = boxes.get(3).items[1];break;
                case 29: k = boxes.get(3).items[2];break;
                case 30: k = boxes.get(4).items[0];break;
                case 31: k = boxes.get(4).items[1];break;
                case 32: k = boxes.get(4).items[2];break;
                case 33: k = boxes.get(5).items[0];break;
                case 34: k = boxes.get(5).items[1];break;
                case 35: k = boxes.get(5).items[2];break;
                case 36: k = boxes.get(3).items[3];break;
                case 37: k = boxes.get(3).items[4];break;
                case 38: k = boxes.get(3).items[5];break;
                case 39: k = boxes.get(4).items[3];break;
                case 40: k = boxes.get(4).items[4];break;
                case 41: k = boxes.get(4).items[5];break;
                case 42: k = boxes.get(5).items[3];break;
                case 43: k = boxes.get(5).items[4];break;
                case 44: k = boxes.get(5).items[5];break;
                case 45: k = boxes.get(3).items[6];break;
                case 46: k = boxes.get(3).items[7];break;
                case 47: k = boxes.get(3).items[8];break;
                case 48: k = boxes.get(4).items[6];break;
                case 49: k = boxes.get(4).items[7];break;
                case 50: k = boxes.get(4).items[8];break;
                case 51: k = boxes.get(5).items[6];break;
                case 52: k = boxes.get(5).items[7];break;
                case 53: k = boxes.get(5).items[8];break;
                case 54: k = boxes.get(6).items[0];break;
                case 55: k = boxes.get(6).items[1];break;
                case 56: k = boxes.get(6).items[2];break;
                case 57: k = boxes.get(7).items[0];break;
                case 58: k = boxes.get(7).items[1];break;
                case 59: k = boxes.get(7).items[2];break;
                case 60: k = boxes.get(8).items[0];break;
                case 61: k = boxes.get(8).items[1];break;
                case 62: k = boxes.get(8).items[2];break;
                case 63: k = boxes.get(6).items[3];break;
                case 64: k = boxes.get(6).items[4];break;
                case 65: k = boxes.get(6).items[5];break;
                case 66: k = boxes.get(7).items[3];break;
                case 67: k = boxes.get(7).items[4];break;
                case 68: k = boxes.get(7).items[5];break;
                case 69: k = boxes.get(8).items[3];break;
                case 70: k = boxes.get(8).items[4];break;
                case 71: k = boxes.get(8).items[5];break;
                case 72: k = boxes.get(6).items[6];break;
                case 73: k = boxes.get(6).items[7];break;
                case 74: k = boxes.get(6).items[8];break;
                case 75: k = boxes.get(7).items[6];break;
                case 76: k = boxes.get(7).items[7];break;
                case 77: k = boxes.get(7).items[8];break;
                case 78: k = boxes.get(8).items[6];break;
                case 79: k = boxes.get(8).items[7];break;
                case 80: k = boxes.get(8).items[8];break;

                //fml
            }

            return k;

    }

        public Column getColumnFromRowPos(int rn, int pos)
        {


            return null;
        }

        public void cleanPanels()
        {
            for(int i = 0; i < 81; i++)
            {
                panelGrid[i].resetPotentials();
            }

        }


        //Checks whether or not the solution to the puzzle is kosher
        public boolean sanitizeOutPut()
        {
            for(int i = 0; i < 9; i++)
            {
                if(!checkUnique(rows.get(i).items)) // not all unique?
                {
                    //System.out.println("ERROR: NonSensical OutPut!!");
                    return false;
                }
            }

            for(int i = 0; i < 9; i++)
            {
                if(!checkUnique(columns.get(i).items)) // not all unique?
                {
                    //System.out.println("ERROR: NonSensical OutPut!!");
                    return false;
                }
            }

            for(int i = 0; i < 9; i++)
            {
                if(!checkUnique(boxes.get(i).items)) // not all unique?
                {

                    return false;
                }
            }

            //System.out.println("Puzzle Solved With No Problems!");
            return true;
        }

        //checks for any repeats in a group
        // returns true if unique
        public boolean checkUnique(int [] items)
        {
            int [] test = new int[10]; // will hold a count for

            for(int i = 0; i < 9;i++)
                test[items[i]]++; //adds 1 to the respective ctr in the array for each encoutered digit; classy

            for(int i = 1; i < test.length;i++)
                if(test[i] > 1) // ignoring 0's, we can have at most 1 of any digit allowed
                    return false;

            return true;
        }

}
