package stimko;

public class Stimko 
{
	/**
	 * Comment HELLO COMMIT from PlayInteraction branch!
	 */
	public static void main(String[] args)
	{
				
		System.out.println("YO");
		SMTInteraction aux = new SMTInteraction();
		StimkoData b = new StimkoData(9);
		
		
		try {
			aux.solveStimko(b);
			System.out.println("------");
			
		} catch(Exception e){
			
		}
		
	}

}
