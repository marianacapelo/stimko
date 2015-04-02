package stimko;

import java.util.ArrayList;

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
    
    public static void printMainMenu() 
    {
    	System.out.println("\nChoose an option");
    	System.out.println("\n1\tPlay initial puzzle");
    	System.out.println("\n2\tChoose puzzle");
        System.out.print("\n\n0\tExit ");

        System.out.print("\t\t\t\t > ");
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
    	
    		sb.append(" ");
    		if(i==0)
	    		for(k=0; k<n ; k++) 
	    			sb.append("_") ;                       
    		
    		ArrayList<Integer> row = board.get(i);
    		for(j=0 ; j<n ; j++) {
    			//Iterate over columns
    			if(j==0) sb.append("|");
    			
    			int value = (int) row.get(j);
    			StimkoData.BoardCell neighbor = puzzle.findNeighbor(i, j);
    			if(neighbor!=null) {
    				int neighbor_row = neighbor.getRow();
    				int neighbor_col = neighbor.getColumn();
    				
    				//Check if in the same row
    				if(neighbor_row == i) {
    					//Check if to the left or to the right
    					if(neighbor_col == (j-1) ) {
    						sb.append('-'+value+' ');
    						row_separator.append("   ");
    					} else if(neighbor_col == (j+1)) {
    						sb.append(' '+value+'-');
    						sb.append(' '+value+' ');
    					}
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
						sb.append(' '+value+' ');
    				}
    			}
    			
    		}
    		sb.append(row_separator.toString());
    		row_separator = new StringBuilder();
    	}
    	
    	System.out.println(sb.toString());
    }
    
}
