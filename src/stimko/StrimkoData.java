package strimko;

import java.util.ArrayList;


public class StrimkoData 
{

//	/**
//	 * Class of each board position
//	 * @author Mariana
//	 *
//	 */
	/**
	 * Size of board
	 */
	private int n;
	
	/**
	 * Board 1
	 */
	private ArrayList<ArrayList<Integer>> board;
	
	/**
	 * Ordered Streams
	 */
	private ArrayList<ArrayList<BoardCell>> streams;
	
	public StrimkoData (int n) 
	{
		this.n = n;
		this.board = new ArrayList<ArrayList<Integer>>(n);
		
		for(ArrayList<Integer> aux : this.board) {
			aux = new ArrayList<Integer>(n);
			board.add(aux);
		}
		
		this.streams = new ArrayList<ArrayList<BoardCell>>(n);
		
		int i;
		//int j = 0;
		for(i = 0 ; i < n ; i++) {
			ArrayList<BoardCell> aux = new ArrayList<BoardCell>(n);
			//for(j = 0; j< n; j++) {
			//	BoardCell aux2 = new BoardCell(i,j);
			//	aux.add(aux2);
			//}
			streams.add(aux);
		}
		
	}
	
	public StrimkoData (int n, ArrayList<ArrayList<Integer>> tab, ArrayList<ArrayList<BoardCell>> stre){
		this.n = n;
		this.board = new ArrayList<ArrayList<Integer>>(n);
		int i;
		for(i = 0; i < n; i++){
			ArrayList<Integer> aux = tab.get(i);
			this.board.add(aux);
		}
		this.streams = new ArrayList<ArrayList<BoardCell>>(n);
		for(i = 0 ; i < n ; i++) {
			ArrayList<BoardCell> aux = stre.get(i);
			streams.add(aux);
		}
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}
	
	public ArrayList<BoardCell> getStream(int n) {
		
		return this.streams.get(n);
	}
	
	
	public String toString(){
		String aux = "";
		aux = aux + "Tabuleiro: \n";
		aux = aux + "--------------------\n";
		for(ArrayList<Integer> auxR : this.board){
			for(int value : auxR){
				aux = aux + "|" + value;
			}
			aux = aux + "|\n";
		}
		aux = aux + "--------------------\n";
		aux = aux + "Streams:\n";
		aux = aux + "--------------------\n";
		for(ArrayList<BoardCell> auxB : this.streams){
			for(BoardCell auxC : auxB){
				aux = aux + "|" + auxC.getRow() + auxC.getColumn();
			}
			aux = aux + "|\n";
		}
		aux = aux + "--------------------\n";
		return aux;
	}
}