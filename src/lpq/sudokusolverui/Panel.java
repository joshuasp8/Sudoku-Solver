/*
 * Sudoku Solver v 1.0
 * Written by Joshua Speight [Liquid Pro Quo]
 * August 2011
 *
 * Panel.java - Class represents an individual square on the grid. Important
 * as this object can easily reference itself as a member of an individual
 * box, row or column.
 */


package lpq.sudokusolverui;

import java.util.ArrayList;

/**
 *
 * @author Josh
 */
// Should have done this from the start
public class Panel implements Comparable
{
    int value;
    
    int rowNum, rowPos;
    int colNum, colPos;
    int boxNum, boxPos;
    int gp;// grid position

    ArrayList <Integer> potentials;

    Row row;
    Column col;
    Box box;

    public Panel(int g)
    {
        value = 0;

        gp = g;

        potentials = new ArrayList<Integer>();
        resetPotentials();
    }

    public void resetPotentials()
    {
        potentials.clear();
        for(int i = 0; i < 9; i++)
        {
            potentials.add(i+1); // list all numbers from 1-9 as potentials
        }
    }

    // Here we'll update the list of potential number candidates by
    //eliminating certain numbers by cross check
    //This is actually the core of the AI work being done 
    public void updatePotentials()
    {
        if(value != 0 || row == null || box  == null || col == null) // then no need
            return;

        ArrayList <Integer> toBeRemoved = new ArrayList <Integer>();
        boolean escape = false; // will be true once a match is found, so that we move on

        for(int i = 0; i < potentials.size(); i++) // go thru list of curr potentials
        {

            escape = false;

            for(int j = 0; j < row.found.size();j++) // compare to nums curr in row
            {
                if(potentials.get(i) == row.found.get(j)) // match?
                {
                    toBeRemoved.add(potentials.get(i)); // the queue to remove
                    escape = true;
                    break; // next number
                }
            }
            //repeat for columns now
            for(int j = 0; j < col.found.size();j++) // compare to nums curr in row
            {
                if(escape) // then already found this, move on
                    break;

                if(potentials.get(i) == col.found.get(j)) // match?
                {
                    toBeRemoved.add(potentials.get(i)); // the queue to remove
                    escape = true;
                    break; // next number
                }
            }

             //repeat for boxes now
            for(int j = 0; j < box.found.size();j++) // compare to nums curr in row
            {
                if(escape) // then already found this, move on
                    break;

                if(potentials.get(i) == box.found.get(j)) // match?
                {
                    toBeRemoved.add(potentials.get(i)); // the queue to remove
                    escape = true;
                    break; // next number
                }
            }

        }

        // now remove all neccessary nums
        for(int i = 0; i < toBeRemoved.size();i++)
        {
            potentials.remove(potentials.indexOf(toBeRemoved.get(i))); //peace
        }
        
        uniquePotentialCheck();
    }
    
    //Next step for AI
    // Here we check for unique potential numbers being held by panels as these indicate
    // that they are the only viable panel to hold the number
    //this method is only called if it makes it past the above
    public void uniquePotentialCheck()
    {
        if(potentials.size() == 1) // then no need
            return;
        //Unlike other instances, every check here is independent
        //ie: results from the row check dont carry over to the column/box check
        //1 success in any is enough to declare a victory
        //and it requires 3 failures to fail

        if(upCheckRow())
        {
            return;
        }
        if(upCheckColumn())
            return;
        upCheckBox();

        //that was hard, yay!
        
    }

    //Unique potential check for rows
    //returns true on success
    public boolean upCheckRow()
    {
        ArrayList<Integer> temp = new ArrayList<Integer>(potentials);

        // first, row
        for(int i = 0; i < row.panelItems.length; i++)// go thru all panels in row
        {
            if(temp.isEmpty()) // then weve finished
                return false;

            Panel comp = row.panelItems[i];
            if(comp.value != 0 || i == rowPos) // another elim, to get to the point
                continue;

            for(int j = 0; j < comp.potentials.size();j++)
            {
                if(temp.isEmpty()) // then weve finished
                return false;

                if(temp.contains(comp.potentials.get(j)))
                {
                    temp.remove(temp.indexOf(comp.potentials.get(j))); // easy peasy
                }
            }


        }

        //if by some miracle exactly 1 remains, we declare victory
        if(temp.size() == 1)
        {
            potentials = temp;
            return true;
        }

        //otherwise
        return false;
    }

    //Unique potential check for rows
    //returns true on success
    public boolean upCheckColumn()
    {
        ArrayList<Integer> temp = new ArrayList<Integer>(potentials);


        for(int i = 0; i < col.panelItems.length; i++)// go thru all panels in col
        {
            if(temp.isEmpty()) // then weve finished
                return false;

            Panel comp = col.panelItems[i];
            if(comp.value != 0 || i == colPos) // another elim, to get to the point
                continue;

            for(int j = 0; j < comp.potentials.size();j++)
            {
                if(temp.isEmpty()) // then weve finished
                return false;

                if(temp.contains(comp.potentials.get(j)))
                {
                    temp.remove(temp.indexOf(comp.potentials.get(j))); // easy peasy
                }
            }


        }

        //if by some miracle exactly 1 remains, we declare victory
        if(temp.size() == 1)
        {
            potentials = temp;
            return true;
        }

        //otherwise
        return false;
    }

    //Unique potential check for box
    //returns true on success
    public boolean upCheckBox()
    {
        ArrayList<Integer> temp = new ArrayList<Integer>(potentials);


        for(int i = 0; i < box.panelItems.length; i++)// go thru all panels in row
        {
            if(temp.isEmpty()) // then weve finished
                return false;

            Panel comp = box.panelItems[i];
            if(comp.value != 0 || i == boxPos) // another elim, to get to the point
                continue;

            for(int j = 0; j < comp.potentials.size();j++)
            {
                if(temp.isEmpty()) // then weve finished
                return false;

                if(temp.contains(comp.potentials.get(j)))
                {
                    temp.remove(temp.indexOf(comp.potentials.get(j))); // easy peasy
                }
            }


        }

        //if by some miracle exactly 1 remains, we declare victory
        if(temp.size() == 1)
        {
            potentials = temp;
            return true;
        }

        //otherwise
        return false;
    }

    //sorts panels by number of potentials remaining;
    //useful later, when bruteforcing
    public int compareTo(Object o)
    {
        int k = 0;

        if(this.potentials.size() != ((Panel)o).potentials.size())
            k = this.potentials.size() > ((Panel)o).potentials.size() ? 1:-1;

        return k;
    }
    
}
