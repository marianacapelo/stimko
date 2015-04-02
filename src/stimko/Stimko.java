package stimko;

public class Stimko 
{
	
	private static StimkoData puzzle ;
	private static SMTInteraction smt ;
	
	/**
	 * Comment HELLO COMMIT from PlayInteraction branch!
	 */
	public static void main(String[] args)
	{
				
		System.out.println("YO");
		smt = new SMTInteraction();
		puzzle = new StimkoData(9);
		
		
		try {
			
			Output.printWelcome();
//			Output.printInitialMenu();
//			int opt = Input.readInt();
//			
//			while(opt!==0) {
//				
//				switch(opt) {
//			
//				case '1' : 
					smt.initStimko(puzzle);
					Output.printPuzzle(puzzle);
					
				
//				}
//			
//			}
			System.out.println("------");
			
		} catch(Exception e){
			
		}
		
	}

}
