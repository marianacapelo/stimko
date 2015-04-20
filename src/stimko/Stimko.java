package stimko;

import java.util.*;

import com.microsoft.z3.Z3Exception;

import print.Printer;
import print.Printer.Types;
import print.color.ColoredPrinter;
import print.color.Ansi.*;
import print.exception.InvalidArgumentsException;
import stimko.StimkoData.BoardCell;


public class Stimko 
{
	
	private static StimkoData puzzle ;
	private static SMTInteraction smt ;
	
	public static HashMap<Integer,String> commands;
	public static int CMD_EXIT = 0;
	public static int CMD_PLAY = 1;
	public static int CMD_UNDO = 2;
	public static int CMD_RESTART = 3;
	public static int CMD_HINT = 4;
	public static int CMD_MMENU = 5;
	public static int CMD_HELP = 6;
	public static int CMD_CHECK = 7;
	public static int CMD_INVALID = 10000;
	
	/**
	 * Comment HELLO COMMIT from PlayInteraction branch!
	 */
	public static void main(String[] args)
	{
				
		// -- Initialize commands --
		commands = new HashMap<Integer,String>();
		commands.put(CMD_EXIT, "exit"); 
		commands.put(CMD_PLAY, "play");	//done
		commands.put(CMD_UNDO, "undo");	//done
		commands.put(CMD_RESTART, "restart");	//done
		commands.put(CMD_HINT, "hint");		
		commands.put(CMD_MMENU,"main menu");
		commands.put(CMD_HELP, "help");
		commands.put(CMD_CHECK, "check");	//done
		//gerar
		
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false)
						        .foreground(FColor.WHITE).background(BColor.BLUE)   
						        //setting format
						        .build();
		
		cp.println("HEELLLO",Attribute.BOLD, FColor.BLUE, BColor.BLUE);
		cp.clear();
		
		try {
		System.out.println("YO");
		smt = new SMTInteraction();
		puzzle = new StimkoData("/Users/Mariana/Documents/workspace/stimko/src/stimko/4x4Test.txt");
		
		
		//DEBUG:
		
		puzzle.generateStep1(4);
		puzzle = smt.populatePuzzle(puzzle);
		
		
		System.out.println(puzzle);
		
		smt.initStimko(puzzle);
		//Output.printPuzzle(puzzle);
		// Print nao funciona ... 

		System.out.println("Write command!");
		String complete_command = Input.lerString();
		int cmd = CMD_INVALID;
		for(Integer key : commands.keySet()) {
			
			String command = commands.get(key);
			if(complete_command.startsWith(command)) {
				cmd = key;
				complete_command = complete_command.substring(command.length());
				break;
			}
			
		}
		System.out.println("got "+cmd);
		

		boolean valid_play;
		StimkoData.BoardCell hint_cell = null;
		StimkoData.BoardCellValue hint_cell_value = null;
		int hint_level = 0;
		
		while (cmd != CMD_EXIT) {
			
			if(cmd == CMD_INVALID) {
				Output.printInvalidInput();
			}
			switch(cmd) {
				
				case 1:
					//Expected to find 3 integers
					complete_command.replaceAll("[^0-9]+", " ");
					String[] inputs = complete_command.trim().split(" ");
					
					for(int i = 0 ; i<inputs.length ; i++) System.out.println(inputs[i]);
//					System.out.println("splitted into "+inputs.length);
					if(inputs.length != 3) { cmd = CMD_INVALID; break;}
					
					int row = Integer.parseInt(inputs[0]);
					int column = Integer.parseInt(inputs[1]);
					int value = Integer.parseInt(inputs[2]);
					
//					System.out.println(" playing "+row+" "+column + " "+ value);
					valid_play = puzzle.play(row, column, value);
//					System.out.println(valid_play);
					if(!valid_play) {
						Output.printInvalidPlay();
					} else {
						smt.assertPlays(puzzle.getBoard());
						System.out.println(puzzle);
					}
					break;
					
				case 2:
					//No extra inputs expected
					
					valid_play = puzzle.undo();
					if(!valid_play) {
						Output.printInvalidPlay();
					} else {
						smt.assertPlays(puzzle.getBoard());
						System.out.println(puzzle);
					}
					break;
					
				case 3 :
					
					puzzle.reset();
					smt.reset();
					
					System.out.println(puzzle);
					break;
					
				case 4 :
					
					//TODO joao! ver se hint_level == max_hint_level! se sim, chutar nova célula!
					StimkoData.BoardCell new_hint_cell = puzzle.hint(hint_cell);
					if(new_hint_cell.equals(hint_cell) ) {
						hint_level++;
					} else {
						try {
							hint_cell_value = smt.findValue(new_hint_cell.getRow(), new_hint_cell.getColumn(), puzzle);
							hint_cell = new_hint_cell;
						} catch(Z3Exception e) {
							System.out.println(e.getMessage());
						}
					}
					if(hint_cell_value == null) {
						Output.printInvalidHint();
					} else {
						Output.printHint(hint_cell_value,hint_level,puzzle);
					}
					break;
				case 7 : 
					Output.printCheck(smt.solvableStimko(puzzle));
					break;
					
			}
			
			System.out.println("Write command!");
			complete_command = Input.lerString();
			cmd = CMD_INVALID;
			for(Integer key : commands.keySet()) {
				
				String command = commands.get(key);
				if(complete_command.startsWith(command)) {
					cmd = key;
					complete_command = complete_command.substring(command.length());
					break;
				}
				
			}
		
		}
		
		
		
//		try {
//			
//			Output.printWelcome();
////			Output.printInitialMenu();
////			int opt = Input.readInt();
////			
////			while(opt!==0) {
////				
////				switch(opt) {
////			
////				case '1' : 
//					smt.initStimko(puzzle);
//					Output.printPuzzle(puzzle);
//					
//				
////				}
//			
//			}
//			System.out.println("------");
//			
		} catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace().toString());
		}
		
	}

}
