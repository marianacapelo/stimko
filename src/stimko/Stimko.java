package stimko;

import java.util.*;

public class Stimko 
{
	
	private static StimkoData puzzle ;
	private static SMTInteraction smt ;
	
	public static HashMap<Integer,String> commands;
	public static int CMD_EXIT = 0;
	public static int CMD_PLAY = 1;
	public static int CMD_RESTART = 2;
	public static int CMD_TIP = 3;
	public static int CMD_MMENU = 4;
	public static int CMD_HELP = 5;
	public static int CMD_CHECK = 6;
	public static int CMD_INVALID = 10000;
	
	/**
	 * Comment HELLO COMMIT from PlayInteraction branch!
	 */
	public static void main(String[] args)
	{
				
		// -- Initialize commands --
		commands = new HashMap<Integer,String>();
		commands.put(CMD_EXIT, "exit");
		commands.put(CMD_PLAY, "play");
		commands.put(CMD_RESTART, "restart");
		commands.put(CMD_TIP, "tip");
		commands.put(CMD_MMENU,"main menu");
		commands.put(CMD_HELP, "help");
		commands.put(CMD_CHECK, "check");
		
		try {
		System.out.println("YO");
		smt = new SMTInteraction();
		puzzle = new StimkoData("/Users/Mariana/Documents/workspace/stimko/src/stimko/4x4Test.txt");
		Output.printPuzzle(puzzle);
		
		// Print nao funciona ... 
		
		String complete_command = Input.lerString();
		int cmd = CMD_INVALID;
		for(Integer key : commands.keySet()) {
			
			String command = commands.get(key);
			if(complete_command.startsWith(command)) {
				cmd = key;
				complete_command.substring(command.length());
				break;
			}
			
		}
		
		if(cmd == CMD_INVALID) {
			Output.printInvalidInput();
		}
		
		switch(cmd) {
			
			case 1:
				//Expected to find 3 integers
				complete_command.replaceAll("[^0-9]+", " ");
				String[] inputs = complete_command.trim().split(" ");
				
				if(inputs.length != 3) { cmd = CMD_INVALID; break;}
				
				int row = Integer.parseInt(inputs[0]);
				int column = Integer.parseInt(inputs[1]);
				int value = Integer.parseInt(inputs[2]);
				
				smt.play(row,column,value);
				
				break;
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
			
		}
		
	}

}
