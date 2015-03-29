package stimko;

import java.util.ArrayList;


public class StimkoData 
{

//	/**
//	 * Class of each board position
//	 * @author Mariana
//	 *
//	 */
	public class BoardCell 
	{
		private int row;
		private int column;
		
		public BoardCell(int a, int b)
		{
			this.setRow(a);
			this.setColumn(b);
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}
		
	}
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
	
	public StimkoData (int n) 
	{
		this.n = n;
		this.board = new ArrayList<ArrayList<Integer>>(n);
		
		for(ArrayList<Integer> aux : this.board) {
			aux = new ArrayList<Integer>(n);
		}
		
		this.streams = new ArrayList<ArrayList<BoardCell>>(n);
		
		int i;
		int j = 0;
		for(i = 0 ; i < n ; i++) {
			ArrayList<BoardCell> aux = new ArrayList<BoardCell>(n);
			for(j = 0; j< n; j++) {
				BoardCell aux2 = new BoardCell(i,j);
				aux.add(aux2);
			}
			this.streams.add(aux);
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
	
	
	
}
