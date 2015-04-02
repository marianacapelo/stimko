package stimko;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	
	public StimkoData (String filename) 
	{
		int N = 0,Rcounter=0,Ccounter=0,auxval;
		BufferedReader reader = null;
		try{
			String CurrentLine;
			reader = new BufferedReader(new FileReader(filename));
			
			CurrentLine = reader.readLine();
			if(CurrentLine == null){System.out.println("Documento de input inv�lido");}
			N = Integer.parseInt(CurrentLine);
			System.out.println("Tamanho de tabuleiro: "+N+"\n");
			
			ArrayList<ArrayList<Integer>> tab = new ArrayList<ArrayList<Integer>>(N);
			ArrayList<ArrayList<BoardCell>> stream = new ArrayList<ArrayList<BoardCell>>(N);
			
			
			while((CurrentLine = reader.readLine()) != null && Rcounter < N){
				ArrayList<Integer> aux = new ArrayList<Integer>(N);
				String[] values = CurrentLine.split(" ");
				for(String v : values){
					if(Ccounter >= N){break;}
					auxval = Integer.parseInt(v);
					aux.add(auxval);
					Ccounter++;
				}
				Ccounter=0;
				Rcounter++;
				tab.add(aux);
			}
			for(Ccounter=0; Ccounter < N; Ccounter++){
				ArrayList<BoardCell> aux = new ArrayList<BoardCell>(N);
				stream.add(aux);
			}
			Rcounter=0;
			Ccounter=0;
			while(Rcounter < N){
				String[] values = CurrentLine.split(" ");
				for(String v : values){
					if(Ccounter >= N){break;}
					auxval = Integer.parseInt(v);
					BoardCell auxB = new BoardCell(Rcounter,Ccounter);
					ArrayList<BoardCell> temp = stream.get(auxval-1);
					temp.add(auxB);
					Ccounter++;
				}
				Ccounter = 0;
				Rcounter++;
				if((CurrentLine = reader.readLine()) == null){break;}
			}
	
			this.n = N;
			this.board = tab;
			this.streams = stream;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(reader != null)reader.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}		
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}
	
	public ArrayList<ArrayList<Integer>> getBoard() {
		return board;
	}

	public void setBoard(ArrayList<ArrayList<Integer>> board) {
		this.board = board;
	}

	public ArrayList<BoardCell> getStream(int n) {
		
		return this.streams.get(n);
	}
	
	public ArrayList<BoardCell> findStream(int row, int col) {
		
		ArrayList<BoardCell> result = null;
		for( ArrayList<StimkoData.BoardCell> stream : this.streams) {
			
			for(BoardCell cell : stream) {
				if(cell.column == col && cell.row == row) {
					result = stream; break;
				}
			}
			if(result!= null) break;
		}
		return result;
		
	}

	public BoardCell findNeighbor(int row, int col) {
		
		BoardCell neighbor = null;
		boolean right_stream = false;
		for( ArrayList<StimkoData.BoardCell> stream : this.streams) {
			
			for(BoardCell cell : stream) {
				if(cell.column == col && cell.row == row) {
					right_stream = true; continue;
				}
				if(right_stream) {
					neighbor = cell; break;
				}
			}
			if(right_stream) break;
		}
		return neighbor;
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
