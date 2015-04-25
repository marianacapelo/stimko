package stimko;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;


public class StimkoData 
{

//	/**
//	 * Class of each board position
//	 * @author Mariana
//	 *
//	 */
	public static class BoardCell 
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
			return (b == null)? false : b.getRow()==this.getRow() && b.getColumn()==this.getColumn();
		}
	}
	
	
	public static class BoardCellValue 
	{
		private int value;
		private BoardCell cell;
		
		public BoardCellValue(int a, int b, int c)
		{
			this.cell = new BoardCell(a,b);
			this.value = c;
		}

		public int getRow() {
			return cell.getRow();
		}

		public void setRow(int row) {
			this.cell.setRow(row); 
		}

		public int getColumn() {
			return this.cell.getColumn();
		}

		public void setColumn(int column) {
			this.cell.setColumn(column); 
		}
		
		public int getValue() {
			return this.value;
		}
		
		public void setValue(int v) {
			this.value = v;
		}
		public boolean equals(BoardCellValue b){
			return b.getRow()==this.getRow() && b.getColumn()==this.getColumn() && this.getValue() == b.getValue();
		}
	}
	
	
	public static final int HINT_LEVEL_1 = 1;
	public static final int HINT_LEVEL_2 = 2;
	public static final int HINT_LEVEL_3 = 3;
	public static final int HINT_LEVEL_4 = 4;
	public static final int HINT_MAX_LEVEL = 4;
	
	public static final int HINT_TARGET_ROW = 10;
	public static final int HINT_TARGET_COLUMN = 20;
	public static final int HINT_TARGET_CELL = 30;
	
	public static final int HINT_FIRST_PARITY = 100;
	public static final int HINT_FIRST_HEIGHT = 200;
	public static final int HINT_SECOND_VALUE = 300;
	
	
	
	
	
	
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
	
	private ArrayList<BoardCellValue> play_history;
	
	private ArrayList<ArrayList<Integer>> original_board;

	
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
			if(CurrentLine == null){System.out.println("Documento de input inv�lido"); return;}
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
	
			ArrayList<ArrayList<Integer>> tab_clone = new ArrayList<ArrayList<Integer>>(N);
			for(ArrayList<Integer> rows : tab) {
				ArrayList<Integer> rows_clone = new ArrayList<Integer>();
				for(Integer vals : rows)
					rows_clone.add(vals);
				tab_clone.add(rows_clone);	
			}
			this.n = N;
			this.board = tab;
			this.original_board = tab_clone;
			this.streams = stream;
			this.play_history = new ArrayList<BoardCellValue>();
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

	public StimkoData(int n2) {
		this.n = n2;
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
	
	public boolean play(int row, int column, int value) {
		
		boolean valid_play = false;
		row--;
		column--;
		
		//Check if play does not change original board values
		if(row < n  && column < n) {
			Integer original_position = this.original_board.get(row).get(column);
			if((int)original_position == 0) 
				valid_play = true;
		}
		
		if(valid_play) {
			Integer position = this.board.get(row).get(column);
			//Store previous value of the cell
			int old_value = (int) position;
			BoardCellValue history = new BoardCellValue(row,column,old_value);
			this.play_history.add(history);
			
			//Change value of cell of board
			this.board.get(row).set(column, value);
		}
		return valid_play;
	}
	
	public boolean undo() 
	{
		boolean valid_play = false;
		
		if(!this.play_history.isEmpty()) {
			//Fetch last cell played
			BoardCellValue last = this.play_history.get(this.play_history.size() - 1);
			int row = last.getRow();
			int column = last.getColumn();
			int value = last.getValue();
			System.out.println("undoing "+row+" "+column + " " + value);
			
			this.board.get(row).set(column, value);
			this.play_history.remove(this.play_history.size() - 1);
			
			valid_play = true;
			
		}
		
		return valid_play;
	}
	
	public void reset() 
	{
		this.board = this.original_board;
		this.play_history = new ArrayList<StimkoData.BoardCellValue>();
	}
	
	public BoardCell findEasiest(int x)
	{
		switch(x){
			//Encontra por colunas
			case 0: 
				ArrayList<Integer> zeros = new ArrayList<Integer>(n);
				int col = 0;
				for(ArrayList<Integer> rowN : this.board){
					col = 0;
					for(Integer colN : rowN){
						if(colN == 0){
							int aux = zeros.get(col); 
							aux++; 
							zeros.remove(col); 
							zeros.add(col, aux);
						}
						col++;
					}
				}
				int minzeros = this.n, counter=0;
				ArrayList<Integer> fres = new ArrayList<Integer>();
				for(int i=0; i<n; i++){
					int temp = zeros.get(i);
					if(temp==minzeros){
						fres.add(i);
						counter++;
					}
					else if(temp<minzeros && temp!=0){
						minzeros = temp;
						fres = new ArrayList<Integer>();
						fres.add(i);
						counter = 1;
					}
				}
				Random r = new Random();
				int ind = r.nextInt(counter-1);
				ArrayList<BoardCell> listP = new ArrayList<BoardCell>();
				for(int i=0; i<n; i++){
					int aux = getBoard().get(i).get(ind);
					if(aux==0){
						BoardCell temp = new BoardCell(i,ind);
						listP.add(temp);
					}
				}
				int resind = r.nextInt(listP.toArray().length-1);
				return listP.get(resind);
			//Encontra por linhas
			case 1:
				ArrayList<BoardCell> res = new ArrayList<BoardCell>();
				ArrayList<BoardCell> temp = new ArrayList<BoardCell>();
				int caux=0, raux=0, zcounter=0, counters=0;
				for(ArrayList<Integer> rowN : this.board){
					for(Integer colN : rowN){
						if(colN==0){
							BoardCell auxb = new BoardCell(raux,caux);
							temp.add(auxb);
							counters++;
						}
						if(caux==n-1){
							if(counters<zcounter && counters!=0){
								res = temp;
								zcounter = counters;
							}
							if(counters==zcounter && counters!=0){
								for(BoardCell bc : temp){
									res.add(bc);
								}
							}
						}
						caux++;
					}
					temp = new ArrayList<BoardCell>();
					caux=0;
					raux++;
					zcounter=0;
				}
				Random ra = new Random();
				int index = ra.nextInt(res.toArray().length-1);
				return res.get(index);
			//Encontra por streams
			case 2:
				ArrayList<BoardCell> zeross = new ArrayList<BoardCell>(); 
				ArrayList<BoardCell> tempV = new ArrayList<BoardCell>();
				int zcount=0, zmin=this.n, strpos=0;
				for(ArrayList<BoardCell> strN : this.streams){
					for(BoardCell bc : strN){
						int bcv = this.getBoard().get(bc.getRow()).get(bc.getColumn());
						if(bcv==0){
							tempV.add(bc);
							zcount++;
						}
						if(strpos==n-1){
							if(zcount<zmin && zcount!=0){
								zmin = zcount;
								zeross = new ArrayList<BoardCell>();
								for(BoardCell ins : tempV){
									zeross.add(ins);
								}
							}
							if(zcount==zmin){
								for(BoardCell ins : tempV){
									zeross.add(ins);
								}
							}
						}
						strpos++;
					}
					strpos=0;
					zcount=0;
				} 
				Random rn = new Random();
				int innd = rn.nextInt(zeross.toArray().length-1);
				return zeross.get(innd);
			default:
				System.out.println("Incorret parameter");
				return null;
		}
	}
	
	public BoardCell hint(BoardCell old_hint_cell) 
	{
		int row=0, col=0, value=-1;
		for(ArrayList<Integer> rowN : this.board){
			col=0;
			for(Integer colN : rowN){
				if(old_hint_cell.getRow() == row || old_hint_cell.getColumn() == col){
					value = colN;
				}
				col++;
			}
			row++;
		}
		if(value==-1){
			System.out.println("Position not found!!"); return null;
		}
		if( old_hint_cell == null || value!=0) {
			Random generator = new Random();
			int x = generator.nextInt(2);
			return findEasiest(x);
		} else {
		return old_hint_cell;		
		}
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
