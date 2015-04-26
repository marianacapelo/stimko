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
    

    public static void printLevelChoice(){
    	System.out.printf("\nPlease choose the difficulty\n");
    	System.out.print("\n1\tEasy");
    	System.out.print("\n2\tMedium");
    	System.out.print("\n3\tHard\n");
    	System.out.print("\n > ");
    }
    
    public static void printLevelSelect(int levels){
    	System.out.println("\nPlease select a level from 1 to "+levels+"\n");
    	System.out.print("\n > ");
    }
    
    public static void printMainMenu() 
    {
    	printWelcome();
    	System.out.println("\nChoose an option");
    	System.out.println("\n1\tPlay existent puzzles");
    	System.out.println("\n2\tGenerate a new puzzle");
        System.out.print("\n\n0\tExit ");

        System.out.print("\n > ");
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
		
		if(hint_level == StimkoData.HINT_MAX_LEVEL) {
			System.out.println("You are looking for the cell in the row "+(hint_cell_value.getRow() + 1)+
					" and column "+(hint_cell_value.getColumn() +1)+ ", with the value "+hint_cell_value.getValue()+".");
			return;
		}
		
		if(hint_level < StimkoData.HINT_LEVEL_2) {
			int[] targets = new int[2];
			int[] target_values = new int[2];
			targets[0] = StimkoData.HINT_TARGET_COLUMN;

			target_values[0] = hint_cell_value.getColumn()+1;
			targets[1] = StimkoData.HINT_TARGET_ROW;
			target_values[1] = hint_cell_value.getRow()+1;
			
			
			Random r = new Random();
			int High = 1;
			int R = r.nextInt(High);
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
				out.append("in a column ");
				break;
			case StimkoData.HINT_TARGET_ROW: 
				out.append("in a row ");
				break;
			default: 
				break;
		}
		
		
		switch(disclosure_level) {
			case StimkoData.HINT_FIRST_HEIGHT:
				
				if(target_value>(n/2))
					out.append("of high value.");
				else
					out.append("of low value.");

				break;
			case StimkoData.HINT_FIRST_PARITY:
				if(target_value%2 == 0)
					out.append("of a even value.");
				else
					out.append("of a odd value.");
				
				break;
			case StimkoData.HINT_SECOND_VALUE:
				out.append("with the value "+target_value+".");
				break;
		}
		
		System.out.println(out.toString());
	}
	
	public static void printInvalidHint() {
		
		System.out.println("No valid hint available...");
		
	}
	public static void printPlayOptions(){
		System.out.println("Welcome to Stimko help\n");
		System.out.println("Play Options\n");
		System.out.println("play      - Make a new move. Usage: play \"row\" \"collumn\" \"value\". Cannot override original puzzle values\n");
		System.out.println("check     - Check if current puzzle state is valid, i.e., every filled cell value is correct\n");
		System.out.println("undo      - Cancels the last move\n");
		System.out.println("hint      - Gives a new hint based on the easiest values to find. Hints can vary from simply the cell's row or collumn to the value of the cell\n");
		System.out.println("help      - Displays the Play Menu help\n");
		System.out.println("restart   - Restarts the current game\n");
		System.out.println("main menu - Open main menu\n");
		System.out.println("exit      - Exit Strimko =(\n\n");
	}
	
	public static void drawPuzzle(StimkoData puzzle){
    	String res = "";
    	int row = 0, col = 0, n = puzzle.getN();
    	for(ArrayList<Integer> rowVal : puzzle.getBoard()){
    		col = 0;
    		for(Integer colVal : rowVal){
    			if(colVal!=0)
    				res = res + colVal;
    			else
    				res = res + "?";
    			if(col < n-1) res = res + puzzle.checkSideConn(row,col);
    			col++;
    		}
    		res = res + "\n";
    		if(row < n-1){
    			col = 0;
    			for(Integer colVal : rowVal){
    				res = res + puzzle.checkVertConn(row,col);
        			if(col < n-1) res = res + puzzle.check4WayConn(row,col);
        			col++;
        		}
    			res = res + "\n";
    		}
    		row++;
    	}
    	System.out.println(res);
    }
	
	public static void AskOutputOrFile(){
		System.out.println("Where to send the output?");
		System.out.println("1 - Console");
		System.out.println("2 - File");
		System.out.print("\n > ");
	}
	
	public static void InsertFileName(){
		System.out.print("Insert the output File name");
		System.out.print("\n > ");
	}
    
}
