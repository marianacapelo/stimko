package stimko;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;



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
		
		public boolean equals(BoardCell b){
			return b.getRow()==this.getRow() && b.getColumn()==this.getColumn();
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
	
	public BoardCell findStartCell(HashMap<BoardCell,ArrayList<BoardCell>> viz){
		int ns = 99999999,counter;
		BoardCell ret = null;
		for(BoardCell key : viz.keySet()){
			counter = 0;
			ArrayList<BoardCell> auxV = viz.get(key);
			for(BoardCell auxB : auxV){
				counter++;
			}
			if(counter <= ns){
				ns = counter;
				ret = key;
			}
			counter = 0;
		}
		return ret;
	}
	
	public ArrayList<ArrayList<BoardCell>> organizeStream(int n, ArrayList<ArrayList<BoardCell>> st){
		ArrayList<ArrayList<BoardCell>> ret = new ArrayList<ArrayList<BoardCell>>(n);
		int Scounter;
		//Organiza por Streams
		for(ArrayList<BoardCell> aux : st){
			Scounter = 0;
			HashMap<BoardCell,ArrayList<BoardCell>> viz = new HashMap<BoardCell,ArrayList<BoardCell>>(n);
			ArrayList<BoardCell> finalS = new ArrayList<BoardCell>();
			for(BoardCell auxB : aux){
				ArrayList<BoardCell> auxv = new ArrayList<BoardCell>();
				for(BoardCell auxC : aux){
					if(auxB.getColumn() == auxC.getColumn() && auxB.getRow() == auxC.getRow()){continue;}
					if((Math.abs(auxB.getColumn()-auxC.getColumn())) <= 1 && (Math.abs(auxB.getRow()-auxC.getRow())) <= 1){
						auxv.add(auxC);
					}
				}
				viz.put(auxB, auxv);
			}
			BoardCell start = findStartCell(viz);
			Scounter++;
			while(Scounter < n){
				ArrayList<BoardCell> auxv = viz.get(start);
				BoardCell pair = auxv.get(0);
				finalS.add(start);
				Scounter++;
				for(ArrayList<BoardCell> auxB : viz.values()){
					auxB.remove(start);
				}
				start = pair;
			}
			finalS.add(start);
			ret.add(finalS);
		}
		
		return ret;
	}
	
	public StimkoData (String filename) 
	{
		int N = 0,Rcounter=0,Ccounter=0,auxval;
		BufferedReader reader = null;
		try{
			String CurrentLine;
			reader = new BufferedReader(new FileReader(filename));
			
			CurrentLine = reader.readLine();
			if(CurrentLine == null){System.out.println("Documento de input invalido"); return;}
			N = Integer.parseInt(CurrentLine);
			//System.out.println("Tamanho de tabuleiro: "+N+"\n");
			
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
			
			stream = organizeStream(N, stream);
	
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
	
	public String checkSideConn(int row, int col){
		int inds = -1, indb = -1;
		for(ArrayList<BoardCell> stream : this.streams){
			for(BoardCell bc : stream){
				if(bc.getRow()==row && bc.getColumn()==col){
					indb = stream.indexOf(bc);
					inds = streams.indexOf(stream);
				}
			}
		}
		ArrayList<BoardCell> auxs = streams.get(inds);
		if(indb != -1){
			if(indb!=0){
				BoardCell aux2 = auxs.get(indb-1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row) return "-";
			}
			if(indb!=n-1){
				BoardCell aux2 = auxs.get(indb+1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row) return "-";
			}	
		}
		return " ";
	}
	
	public String checkVertConn(int row, int col){
		int inds = -1, indb = -1;
		for(ArrayList<BoardCell> stream : this.streams){
			for(BoardCell bc : stream){
				if(bc.getRow()==row && bc.getColumn()==col){
					indb = stream.indexOf(bc);
					inds = streams.indexOf(stream);
				}
			}
		}
		ArrayList<BoardCell> auxs = streams.get(inds);
		if(indb != -1){
			if(indb!=0){
				BoardCell aux2 = auxs.get(indb-1);
				if(aux2.getColumn()==col && aux2.getRow()==row+1) return "|";
			}
			if(indb!=n-1){
				BoardCell aux2 = auxs.get(indb+1);
				if(aux2.getColumn()==col && aux2.getRow()==row+1) return "|";
			}	
		}
		return " ";
	}
	
	public String check4WayConn(int row, int col){
		boolean con1 = false, con2 = false;
		int inds = -1, indb = -1;
		for(ArrayList<BoardCell> stream : this.streams){
			for(BoardCell bc : stream){
				if(bc.getRow()==row && bc.getColumn()==col){
					indb = stream.indexOf(bc);
					inds = streams.indexOf(stream);
				}
			}
		}
		ArrayList<BoardCell> auxs = streams.get(inds);
		if(indb != -1){
			if(indb!=0){
				BoardCell aux2 = auxs.get(indb-1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row+1) con1 = true;
			}
			if(indb!=n-1){
				BoardCell aux2 = auxs.get(indb+1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row+1) con1 = true;
			}	
		}
		inds = -1; indb = -1;
		row++;
		for(ArrayList<BoardCell> stream : this.streams){
			for(BoardCell bc : stream){
				if(bc.getRow()==row && bc.getColumn()==col){
					indb = stream.indexOf(bc);
					inds = streams.indexOf(stream);
				}
			}
		}
		ArrayList<BoardCell> auxs2 = streams.get(inds);
		if(indb != -1){
			if(indb!=0){
				BoardCell aux2 = auxs2.get(indb-1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row-1) con2 = true;
			}
			if(indb!=n-1){
				BoardCell aux2 = auxs2.get(indb+1);
				if(aux2.getColumn()==col+1 && aux2.getRow()==row-1) con2 = true;
			}	
		}
		
		if(con1){
			if(con2){
				return "X";
			}
			else return "\\";
		}
		if(con2){
			return "/";
		}
		else return " ";
	}
}
