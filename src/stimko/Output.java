package stimko;

import java.util.ArrayList;
import java.util.Random;

import stimko.StimkoData.BoardCellValue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mariana
 */
public class Output {

    
    public static void clearScreen()
    {
        System.out.println("\n\n\n\n\n\n\n\n\n");
    }
    
    public static void printWelcome()
    {
    	System.out.println("\n\nWelcome to Stimko\n\n");
    }
    
    public static void printInvalidInput()
    {
    	System.out.println("\nInvalid input. Please re-enter command\n");
    }
    
    public static void printInvalidPlay()
    {
    	System.out.println("\nInvalid play. Please re-enter command\n");
    }
    
    public static void printMainMenu() 
    {
    	System.out.println("\nChoose an option");
    	System.out.println("\n1\tPlay initial puzzle");
    	System.out.println("\n2\tChoose puzzle");
        System.out.print("\n\n0\tExit ");

        System.out.print("\n > ");
    }
    
    public static void printPuzzle(StimkoData puzzle)
    {
    	int n = puzzle.getN();
    	ArrayList<ArrayList<Integer>> board = puzzle.getBoard();
    	int i;
    	int j;
    	int k;
    	StringBuilder sb = new StringBuilder() ;
        StringBuilder row_separator = new StringBuilder();
    	for(i=0; i<n ; i++) {
    		//Iterate over rows
    	
    		if(i==0) {
        		sb.append("  ");
    			for(k=0; k<n ; k++) 
	    			sb.append("_") ; 
	    		sb.append('\n');
	    		row_separator.append('\n');
    		}

    		sb.append(" ");
    		row_separator.append(" ");

    		ArrayList<Integer> row = board.get(i);

    		for(j=0 ; j<n ; j++) {
    			//Iterate over columns
    			if(j==0) { sb.append("|"); row_separator.append('|'); }
    			
    			int value = (int) row.get(j);


    			StimkoData.BoardCell neighbor = puzzle.findNeighbor(i, j);
    			if(neighbor!=null) {
    				int neighbor_row = neighbor.getRow();
    				int neighbor_col = neighbor.getColumn();
    				
    				//Check if in the same row
    				if(neighbor_row == i) {
    					//Check if to the left or to the right
    					if(neighbor_col == (j-1) ) {
    						sb.append('-');
    						sb.append(value);
    						sb.append(' ');
    					} else if(neighbor_col == (j+1)) {
    						sb.append(' ');
    						sb.append(value);
    						sb.append('-');
						}
						row_separator.append("   ");
    				} else {
    					//Neighbor is in the next row
    					int orientation = neighbor_col - j;
    					switch(orientation) {
    					case -1 : 
    						row_separator.append("/  ");
    						break;
    					case 0 : 
    						row_separator.append(" | ");
    						break;
    					case 1:
    						row_separator.append("  \\");
    					}
						sb.append(' '); sb.append(value); sb.append(' ');
    				}
    			}
    			
    		}
    		
    		sb.append("|"); row_separator.append('|');
//    		row_separator.append('\n');
        	
    		sb.append(row_separator.toString()+"\n");
    		row_separator = new StringBuilder();
    	}
    	
    	System.out.println(sb.toString());
    }
    
    public static void printCheck(boolean ok)
    {
    	if(ok) {
    		System.out.println("\tSo far so good");
    	} else {
    		System.out.println("\tSomething is wrong");
    	}
    }

	public static void printHint(BoardCellValue hint_cell_value, int hint_level, StimkoData puzzle) 
	{
		int target;
		int disclosure_level;
		int target_value;
		int n = puzzle.getN();
		if(hint_level < StimkoData.HINT_LEVEL_2) {
			int[] targets = new int[2];
			int[] target_values = new int[2];
			targets[0] = StimkoData.HINT_TARGET_COLUMN;
			target_values[0] = hint_cell_value.getColumn();
			targets[1] = StimkoData.HINT_TARGET_ROW;
			target_values[1] = hint_cell_value.getRow();
			
			
			Random r = new Random();
			int Low = 0;
			int High = 1;
			int R = r.nextInt(High-Low) + Low;
			target = targets[R];
			target_value = target_values[R];

		} else {
			target = StimkoData.HINT_TARGET_CELL;
			target_value = hint_cell_value.getValue();
		}
		
		if(hint_level % 2 == 0) {
			int[] disclosure_opt = new int[2];
			disclosure_opt[0] = StimkoData.HINT_FIRST_HEIGHT;
			disclosure_opt[1] = StimkoData.HINT_FIRST_PARITY;
			Random r = new Random();
			int Low = 0;
			int High = 1;
			int R = r.nextInt(High-Low) + Low;
			disclosure_level = disclosure_opt[R];
		} else {
			disclosure_level = StimkoData.HINT_SECOND_VALUE;
		}
		
		StringBuffer out = new StringBuffer();
		out.append("You are looking for a cell ");
		switch(target) {
			case StimkoData.HINT_TARGET_COLUMN: 
				out.append(" in a column ");
				break;
			case StimkoData.HINT_TARGET_ROW: 
				out.append(" in a row ");
				break;
			default: 
				break;
		}
		
		
		switch(disclosure_level) {
			case StimkoData.HINT_FIRST_HEIGHT:
				
				if(target_value>(n/2))
					out.append(" of high value.");
				else
					out.append(" of low value.");

				break;
			case StimkoData.HINT_FIRST_PARITY:
				if(target_value%2 == 0)
					out.append(" of a even value.");
				else
					out.append(" of a odd value.");
				
				break;
			case StimkoData.HINT_SECOND_VALUE:
				out.append(" with the value "+target_value);
				break;
		}
		
		System.out.println(out.toString());
	}
    
}
