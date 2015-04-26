package stimko;

import java.util.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.StringBuilder;

import com.microsoft.z3.Z3Exception;

import stimko.StimkoData.BoardCell;


public class Stimko 
{
	
	private static StimkoData puzzle;
	
	public static int MAX_LEVELS = 4;
	public static String DIRECTORY_NAME = "/home/mjf/workspace/Stimko/src/stimko/";

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
	

	public static StimkoData MainMenu(){
		int esc = -1;
		StimkoData res = new StimkoData(-1);
		while(esc != 3){
			Output.printMainMenu();
			esc = Input.lerInt();
			switch(esc){
				case 1:
					int esc2 = -1;
					StringBuilder file = new StringBuilder(512);
					file.append(DIRECTORY_NAME);
					while(esc2<0 || esc2>3){
						Output.printLevelChoice();
						esc2 = Input.lerInt();
						switch(esc2){
							case 1:
								file.append("easy/");
								boolean valid_level=false;
								int aux=-1;
								while(!valid_level){
									Output.printLevelSelect(MAX_LEVELS);
									aux = Input.lerInt(); 
									if(aux>0 && aux<=MAX_LEVELS){valid_level=true;}
								}
								file.append(aux);
								file.append(".txt");
								res = new StimkoData(file.toString());
								return res;
							case 2:
								file.append("normal/");
								boolean valid_level_b=false;
								int aux_b=-1;
								while(!valid_level_b){
									Output.printLevelSelect(MAX_LEVELS);
									aux_b = Input.lerInt(); 
									if(aux_b>0 && aux_b<=MAX_LEVELS){valid_level_b=true;}
								}
								file.append(aux_b);
								file.append(".txt");
								res = new StimkoData(file.toString());
								return res;
							case 3:
								file.append("hard/");
								boolean valid_level_c=false;
								int aux_c=-1;
								while(!valid_level_c){
									Output.printLevelSelect(MAX_LEVELS);
									aux_c = Input.lerInt(); 
									if(aux_c>0 && aux_c<=MAX_LEVELS){valid_level_c=true;}
								}
								file.append(aux_c);
								file.append(".txt");
								res = new StimkoData(file.toString());
								return res;
							default:
								Output.printInvalidInput();
								break;
						}
					}
					break;
				case 2:
					//Generate new puzzle
					break;
				case 3: 
					return null;
				default: 
					Output.printInvalidInput();
					break;
			}
		}
			
		return null;
	}
	
	/**
	 * Comment HELLO COMMIT from PlayInteraction branch!
	 */
	public static void main(String[] args)
	{
				
		// -- Initialize commands --
		commands = new HashMap<Integer,String>();
		commands.put(CMD_EXIT, "exit"); //done
		commands.put(CMD_PLAY, "play");	//done
		commands.put(CMD_UNDO, "undo");	//done
		commands.put(CMD_RESTART, "restart");	//done
		commands.put(CMD_HINT, "hint");		
		commands.put(CMD_MMENU,"main menu"); //done
		commands.put(CMD_HELP, "help");
		commands.put(CMD_CHECK, "check");	//done
		//gerar


		
		try {			
		
		puzzle = MainMenu();
		if(puzzle == null){System.out.println("Exiting");return;}
		Output.drawPuzzle(puzzle);
//		smt.initStimko(puzzle);
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
			Output.drawPuzzle(puzzle);
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
					
					System.out.println(" playing "+row+" "+column + " "+ value);
					valid_play = puzzle.play(row, column, value);
					System.out.println(valid_play);
					if(!valid_play) {
						Output.printInvalidPlay();
					} else {
						smt.play(row,column,value);
						System.out.println(puzzle);
					}
					break;
					
				case 2:
					//No extra inputs expected
					
					valid_play = puzzle.undo();
					if(!valid_play) {
						Output.printInvalidPlay();
					} else {
						smt.undo();
						System.out.println(puzzle);
					}
					break;
					
				case 3 :
					
					puzzle.reset();
					smt.reset();
					
					System.out.println(puzzle);
					break;
					
				case 4 :
					
					StimkoData.BoardCell new_hint_cell = puzzle.hint(hint_cell);
					if(new_hint_cell.equals(hint_cell) ) {
						hint_level++;
						if(hint_level>StimkoData.HINT_MAX_LEVEL){
							hint_cell = null;
							new_hint_cell = puzzle.hint(hint_cell);
							try {
								hint_cell_value = smt.findValue(new_hint_cell.getRow(), new_hint_cell.getColumn(), puzzle);
								hint_cell = new_hint_cell;
								hint_level = 1;
							} catch(Z3Exception e) {
								System.out.println(e.getMessage());
							}
						}
					} else {
						try {
							hint_cell_value = smt.findValue(new_hint_cell.getRow(), new_hint_cell.getColumn(), puzzle);
							hint_cell = new_hint_cell;
							hint_level = 1;
						} catch(Z3Exception e) {
							System.out.println(e.getMessage());
//						}
					}
					if(hint_cell_value == null) {
						Output.printInvalidHint();
					} else {
						Output.printHint(hint_cell_value,hint_level,puzzle);
					}
					break;
				case 5:
					puzzle = MainMenu();
					if(puzzle == null){return;}
					Output.drawPuzzle(puzzle);
					smt.initStimko(puzzle);
					break;
				case 6:
					Output.printPlayOptions();
					break;
				case 7 : 
					Output.printCheck(smt.solvableStimko(puzzle));
					break;
					
			}
			
			
			for(Integer key : commands.keySet()) {
				
				String command = commands.get(key);
				if(complete_command.startsWith(command)) {
					cmd = key;
					complete_command = complete_command.substring(command.length());
					break;
				}
				
			}
		}
	
		} catch(Exception e){
			System.out.println("exception!");
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace().toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			sw.toString(); 
			System.out.println(sw);
			System.out.println(" --- exception! --- ");

		}
		
	}

}
