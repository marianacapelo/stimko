package stimko;

import java.util.*;

import stimko.StimkoData.BoardCell;
import stimko.StimkoData.BoardCellValue;

import com.microsoft.z3.*;

public class SMTInteraction 
{
	
	private Context ctx;
	
	private Solver current_solver;

	private Solver original_solver;
	
	private IntExpr[][] original_positions;
		
	public SMTInteraction ()
	{
        try {
			this.ctx = new Context();
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initStimko(StimkoData puzzle) throws Exception 
	{
		// n x n matrix of integer variables
		int n  = puzzle.getN();
        this.original_positions = new IntExpr[n][];
        for (int i = 0; i < n; i++)
        {
            this.original_positions[i] = new IntExpr[n];
            for (int j = 0; j < n; j++)
                this.original_positions[i][j] = (IntExpr) this.ctx.mkConst(
                        this.ctx.mkSymbol("x_" + (i + 1) + "_" + (j + 1)),
                        this.ctx.getIntSort());
        }

        // each cell contains a value in {1, ..., n}
        BoolExpr[][] cells_c = new BoolExpr[n][];
        for (int i = 0; i < n; i++)
        {
            cells_c[i] = new BoolExpr[n];
            for (int j = 0; j < n; j++)
                cells_c[i][j] = this.ctx.mkAnd(this.ctx.mkLe(this.ctx.mkInt(1), this.original_positions[i][j]),
                        this.ctx.mkLe(this.original_positions[i][j], this.ctx.mkInt(n)));
        }
		
        // each row contains a digit at most once
        BoolExpr[] rows_c = new BoolExpr[n];
        for (int i = 0; i < n; i++)
            rows_c[i] = this.ctx.mkDistinct(this.original_positions[i]);

        // each column contains a digit at most once
        BoolExpr[] cols_c = new BoolExpr[n];
        for(int col = 0; col<n ; col++) {
        	
        	IntExpr[] col_int_exp = new IntExpr[n];
	        for (int j = 0; j < n; j++) {
	        	col_int_exp[j] = this.original_positions[j][col];
	        }
            cols_c[col] = this.ctx.mkDistinct(col_int_exp);
        }
        
        // each stream contains a digit at most once
        BoolExpr[] streams_c = new BoolExpr[n];
        for (int i0 = 0; i0 < n; i0++)
        {
            ArrayList<BoardCell> current_solver_stream = puzzle.getStream(i0);
            
            //Create expression for the n cells in the stream
            IntExpr[] cells_stream = new IntExpr[n];
            
        	//Fetch each cell of the stream
            for (int j0 = 0; j0 < n; j0++)
            {
            	BoardCell current_solver_cell = current_solver_stream.get(j0);
            	int row = current_solver_cell.getRow();
            	int column = current_solver_cell.getColumn();
            	
            	cells_stream[j0] = this.original_positions[row][column];
            }
            
            streams_c[i0] = this.ctx.mkDistinct(cells_stream);
        }
        
        BoolExpr stimko_c = this.ctx.mkTrue();
        for (BoolExpr[] t : cells_c)
        	stimko_c = this.ctx.mkAnd(this.ctx.mkAnd(t), stimko_c);
        
        stimko_c = this.ctx.mkAnd(this.ctx.mkAnd(rows_c), stimko_c);
        
        stimko_c = this.ctx.mkAnd(this.ctx.mkAnd(cols_c), stimko_c);
        
        stimko_c = this.ctx.mkAnd(this.ctx.mkAnd(streams_c), stimko_c);

        //Read from puzzle TODO
        // stimko instance, we use '0' for empty cells
        int[][] instance = new int[n][];
        ArrayList<ArrayList<Integer>> board = puzzle.getBoard();
        for(ArrayList<Integer> row : board) {
        	int n_row = board.indexOf(row);
        	instance[n_row] = new int[n];
        	for(Integer v :row) {
        		int n_column = row.indexOf(v);
        		instance[n_row][n_column] = (int) v;
        	}
        }
        BoolExpr instance_c = this.ctx.mkTrue();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                instance_c = this.ctx.mkAnd(
                        instance_c,
                        (BoolExpr) this.ctx.mkITE(
                                this.ctx.mkEq(this.ctx.mkInt(instance[i][j]),
                                        this.ctx.mkInt(0)), this.ctx.mkTrue(),
                                this.ctx.mkEq(this.original_positions[i][j], this.ctx.mkInt(instance[i][j]))));

        Solver s = this.ctx.mkSolver();
        s.add(stimko_c);
        s.push();
        s.add(instance_c);
        this.original_solver = s;
        Solver ss = this.ctx.mkSolver();
        ss.add(stimko_c);
        ss.push();
        ss.add(instance_c);
        this.current_solver = ss;
	}
	
	public void assertPlays(ArrayList<ArrayList<Integer>> board) throws Z3Exception
	{    
			
		this.current_solver.pop();
		this.current_solver.push();
        BoolExpr instance_c = this.ctx.mkTrue();

		int n_row = 1;
		for(ArrayList<Integer> rows : board) {
			
			int n_column = 1;
			for(Integer val : rows) {
				
				instance_c = this.ctx.mkAnd(
						instance_c,
						(BoolExpr) this.ctx.mkITE(
								this.ctx.mkEq(this.ctx.mkInt(val),
								this.ctx.mkInt(0)), this.ctx.mkTrue(),
								this.ctx.mkEq(this.original_positions[n_row-1][n_column-1], this.ctx.mkInt(val))));
				n_column++;
				
			}
			n_row++;	
		}
		
		this.current_solver.add(instance_c);
		
	}
	
	public void reset() throws Z3Exception
	{
		System.out.println(this.original_solver);

		this.current_solver = this.original_solver;
		System.out.println("After reset...");
		System.out.println(this.current_solver);
	}
	
	public boolean solvableStimko(StimkoData puzzle) throws Z3Exception 
	{   
		System.out.println(this.current_solver);
        if (this.current_solver.check() == Status.SATISFIABLE)
        {
            return true;
        } 
        return false;
		
	}
	
	public BoardCellValue findValue(int row, int column, StimkoData puzzle) throws Z3Exception
	{
		BoardCellValue c = null;
		
		if(row > 0 && row < puzzle.getN() && column > 0 && column < puzzle.getN()
				&& this.current_solver.check() == Status.SATISFIABLE)
        {
			Model m = this.current_solver.getModel();
	        Expr solution = m.evaluate(this.original_positions[row-1][column-1], false);
	        System.out.println("Sudoku solution: "+solution);
	        if(solution.isNumeral()) {
	        	int value =  Integer.parseInt(solution.toString());
	        	c = new StimkoData.BoardCellValue(row, column, value);
	        }
        }
		return c;
	}
	
	public void my() throws Z3Exception, Exception
	{
		Context ctx = this.ctx;
		// 9x9 matrix of integer variables
        IntExpr[][] X = new IntExpr[9][];
        for (int i = 0; i < 9; i++)
        {
            X[i] = new IntExpr[9];
            for (int j = 0; j < 9; j++)
                X[i][j] = (IntExpr) ctx.mkConst(
                        ctx.mkSymbol("x_" + (i + 1) + "_" + (j + 1)),
                        ctx.getIntSort());
        }

        // each cell contains a value in {1, ..., 9}
        BoolExpr[][] cells_c = new BoolExpr[9][];
        for (int i = 0; i < 9; i++)
        {
            cells_c[i] = new BoolExpr[9];
            for (int j = 0; j < 9; j++)
                cells_c[i][j] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), X[i][j]),
                        ctx.mkLe(X[i][j], ctx.mkInt(9)));
        }

        // each row contains a digit at most once
        BoolExpr[] rows_c = new BoolExpr[9];
        for (int i = 0; i < 9; i++)
            rows_c[i] = ctx.mkDistinct(X[i]);

        // each column contains a digit at most once
        BoolExpr[] cols_c = new BoolExpr[9];
        for (int j = 0; j < 9; j++)
            cols_c[j] = ctx.mkDistinct(X[j]);

        // each 3x3 square contains a digit at most once
        BoolExpr[][] sq_c = new BoolExpr[3][];
        for (int i0 = 0; i0 < 3; i0++)
        {
            sq_c[i0] = new BoolExpr[3];
            for (int j0 = 0; j0 < 3; j0++)
            {
            	//Create exp. for 9 cells 
                IntExpr[] square = new IntExpr[9];
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        square[3 * i + j] = X[3 * i0 + i][3 * j0 + j];
                
                //Bool exp makes distinct values for the 9 cells created
                sq_c[i0][j0] = ctx.mkDistinct(square);
            }
        }

        BoolExpr sudoku_c = ctx.mkTrue();
        for (BoolExpr[] t : cells_c)
            sudoku_c = ctx.mkAnd(ctx.mkAnd(t), sudoku_c);
        sudoku_c = ctx.mkAnd(ctx.mkAnd(rows_c), sudoku_c);
        sudoku_c = ctx.mkAnd(ctx.mkAnd(cols_c), sudoku_c);
        for (BoolExpr[] t : sq_c)
            sudoku_c = ctx.mkAnd(ctx.mkAnd(t), sudoku_c);

        // sudoku instance, we use '0' for empty cells
        int[][] instance = { { 0, 0, 0, 0, 9, 4, 0, 3, 0 },
                { 0, 0, 0, 5, 1, 0, 0, 0, 7 }, { 0, 8, 9, 0, 0, 0, 0, 4, 0 },
                { 0, 0, 0, 0, 0, 0, 2, 0, 8 }, { 0, 6, 0, 2, 0, 1, 0, 5, 0 },
                { 1, 0, 2, 0, 0, 0, 0, 0, 0 }, { 0, 7, 0, 0, 0, 0, 5, 2, 0 },
                { 9, 0, 0, 0, 6, 5, 0, 0, 0 }, { 0, 4, 0, 9, 7, 0, 0, 0, 0 } };

        BoolExpr instance_c = ctx.mkTrue();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                instance_c = ctx.mkAnd(
                        instance_c,
                        (BoolExpr) ctx.mkITE(
                                ctx.mkEq(ctx.mkInt(instance[i][j]),
                                        ctx.mkInt(0)), ctx.mkTrue(),
                                ctx.mkEq(X[i][j], ctx.mkInt(instance[i][j]))));

        Solver s = ctx.mkSolver();
        s.add(sudoku_c);
        s.add(instance_c);

        
        System.out.println("CELSS");
        System.out.println(cells_c.toString());
        System.out.println("ROWS");
        System.out.println(rows_c.toString());
        System.out.println("COLS");
        System.out.println(cols_c.toString());

        System.out.println("SQUARE");
        System.out.println(sq_c.toString());
        
        
        System.out.println(ctx.toString());
        System.out.println("Sudoku_c");
        System.out.println(sudoku_c.toString());
        System.out.println("Instance_c");
        System.out.println(instance_c.toString());
        
        if (s.check() == Status.SATISFIABLE)
        {
            Model m = s.getModel();
            Expr[][] R = new Expr[9][9];
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    R[i][j] = m.evaluate(X[i][j], false);
            System.out.println("Sudoku solution:");
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                    System.out.print(" " + R[i][j]);
                System.out.println();
            }
        } else
        {
            System.out.println("Failed to solve sudoku");
            throw new Exception();
        }
	}
	
	
	public boolean unique_solution(Solver s, Context c, int n, ArrayList<ArrayList<Integer>> board_values, IntExpr[][] positions ) throws Z3Exception 
	{
			
		boolean single = true;
		
		Model first_model = s.getModel();
		
		s.push();
		
		Expr[][] R = new Expr[n][n];
		BoolExpr bool_exp = c.mkTrue();

		for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
            	
            	//Only deny assignments of values that do not belong to the original board values
            	if(board_values.get(i).get(j)!=0) {
	            	R[i][j] = first_model.evaluate(positions[i][j], false);
	            	int val = Integer.parseInt(R[i][j].toString());
	            	
	            	if( val != 0 ) {
	            		
	            		bool_exp = c.mkAnd(
	            					(BoolExpr) 
	    								c.mkNot(c.mkEq(positions[i][j], c.mkInt(val))),
										bool_exp);
	            	}
            	}
            }
		
		System.out.println(s.toString());
		s.add(bool_exp);
		System.out.println(s.toString());

		if(s.check() == Status.SATISFIABLE) {
			single = false;
		}
		
		s.pop();
		
		return single;
		
	}


	public StimkoData generate(StimkoData empty) throws Z3Exception
	{
			
		boolean success = false;
		Context new_ctx = null;
		Solver new_solver= null;
		IntExpr[][] new_original_positions= null;
		ArrayList<ArrayList<Integer>> new_board_values= null;

		while(!success) {
			//O que fazer...
			new_ctx = new Context();
			new_solver = new_ctx.mkSolver();
			
			// Num novo contexto, com um novo solver, preparar expressões para as posições novas.
			
			int n  = empty.getN();
			
			new_original_positions = new IntExpr[n][];
	        for (int i = 0; i < n; i++)
	        {
	            new_original_positions[i] = new IntExpr[n];
	            for (int j = 0; j < n; j++)
	                new_original_positions[i][j] = (IntExpr) new_ctx.mkConst(
	                        new_ctx.mkSymbol("x_" + (i + 1) + "_" + (j + 1)),
	                        new_ctx.getIntSort());
	        }
	
			//Passo 1 - criar regras de tabuleiro sobre colunas e linhas
	        // each cell contains a value in {1, ..., n}
	        BoolExpr[][] cells_c = new BoolExpr[n][];
	        for (int i = 0; i < n; i++)
	        {
	            cells_c[i] = new BoolExpr[n];
	            for (int j = 0; j < n; j++)
	                cells_c[i][j] = new_ctx.mkAnd(new_ctx.mkLe(new_ctx.mkInt(1), new_original_positions[i][j]),
	                        new_ctx.mkLe(new_original_positions[i][j], new_ctx.mkInt(n)));
	        }
			
	        // each row contains a digit at most once
	        BoolExpr[] rows_c = new BoolExpr[n];
	        for (int i = 0; i < n; i++)
	            rows_c[i] = new_ctx.mkDistinct(new_original_positions[i]);
	
	        // each column contains a digit at most once
	        BoolExpr[] cols_c = new BoolExpr[n];
	        for(int col = 0; col<n ; col++) {
	        	
	        	IntExpr[] col_int_exp = new IntExpr[n];
		        for (int j = 0; j < n; j++) {
		        	col_int_exp[j] = new_original_positions[j][col];
		        }
	            cols_c[col] = new_ctx.mkDistinct(col_int_exp);
	        }
	
	        
	        //Passo 2 - conjugar regras criadas até ao momento
	        
	        BoolExpr stimko_c = new_ctx.mkTrue();
	        for (BoolExpr[] t : cells_c)
	        	stimko_c = new_ctx.mkAnd(new_ctx.mkAnd(t), stimko_c);
	        
	        stimko_c = new_ctx.mkAnd(new_ctx.mkAnd(rows_c), stimko_c);
	        
	        stimko_c = new_ctx.mkAnd(new_ctx.mkAnd(cols_c), stimko_c);
	        
	        new_solver.add(stimko_c);
	        
	        
	        
	        //Passo 3 - gerar streams e adicionar regras de tabuleiro quanto streams
	        
			System.out.println("Generating puzzle structure");

	        // each stream contains a digit at most once
	        BoolExpr[] streams_c = new BoolExpr[1];
	        ArrayList<ArrayList<BoardCell>> streams;
	        boolean done = false;
	        int n_streams;
	        while (!done)
	        {
	        	
	        	boolean trying = true;
	        	streams = new ArrayList<ArrayList<BoardCell>>();
	        	empty.setStreams(streams);
	    		n_streams = 0;
	    		ArrayList<ArrayList<BoardCell>> bad_streams = new ArrayList<ArrayList<BoardCell>>();
	        	while(trying && n_streams < n) {
		        	
	        		new_solver.push();
		
		            ArrayList<BoardCell> current_solver_stream = generateNewStream(empty, n_streams, bad_streams);
		            
		            if(current_solver_stream == null) {
		            	trying = false; break;
		            }
		            
		            //Create expression for the n cells in the stream
		            IntExpr[] cells_stream = new IntExpr[n];
		            
		        	//Fetch each cell of the stream
		            for (int j0 = 0; j0 < n; j0++)
		            {
		            	BoardCell current_solver_cell = current_solver_stream.get(j0);
		            	int row = current_solver_cell.getRow();
		            	int column = current_solver_cell.getColumn();
		            	
		            	cells_stream[j0] = new_original_positions[row][column];
		            }
		            
		            streams_c[0] = new_ctx.mkDistinct(cells_stream);
		            
		            new_solver.add(streams_c);
		            
		            if(new_solver.check() == Status.SATISFIABLE) {
		            	System.out.println("Generated a stream");
		            	streams.add(current_solver_stream);
		            	empty.setStreams(streams);
		            	n_streams ++;
		            	if(n_streams == n) done = true;
		            	else bad_streams = new ArrayList<ArrayList<BoardCell>>();
		            	
		            } else {
		            	bad_streams.add(current_solver_stream);
		            	new_solver.pop();
		            	System.out.println("Undone a stream");
		            }
	        	}
	        	
	        	if(!trying) {
	        		//Try failed!
	        		//Pop all formulas added
	        		for(int p = 0; p<=n_streams; p++) {
	        			new_solver.pop();
	        		}        	
	        		System.out.println("Restarting streams");
	        	}
	        }
	        
			
			//Passo 4 - definir percentagem de preenchimento desejada.
	
	        double wanted_percentage = 0.5;
	        int n_wanted_cells = (int) (wanted_percentage * n * n);
	        int n_current_cells = 0;
	        
	        System.out.println("Generating puzzle values");

	        new_board_values = new ArrayList<ArrayList<Integer>>(n);
			for(int i = 0 ; i < n ; i++) {
				ArrayList<Integer> r = new ArrayList<Integer>(n);
				for(int j = 0 ; j< n ; j++) {
					r.add(0);
				}
				new_board_values.add(r);
			}
			
			
	        // Enquanto não se atingir a percentagem:
	        while(n_current_cells < n_wanted_cells) {
	        	
				//  - Push 
	        	new_solver.push();
	        	
	        	// - Encontrar random célula vazia 
	        	int column = 0;
	        	int row = 0;
	        	boolean found_random_empty_cell = false;
				int High = n;
				while(!found_random_empty_cell) {
	        		
	        		Random r_1 = new Random();
	    			row = r_1.nextInt(High);
	    			
	    			Random r_2 = new Random();
	    			column = r_2.nextInt(High);
	    			
	    			if(new_board_values.get(row).get(column) == 0) {
	    				found_random_empty_cell = true;
	    			}
	        	}
				
	        	// - Fetch valor válido de um dos modelos
				
				
	        	Model m = new_solver.getModel();
	        	IntExpr p = new_original_positions[row][column];
	        	Expr from_model = m.evaluate(p, false);
	        	
	        	int value = Integer.parseInt(from_model.toString());
	        	
	        	// - Assert do valor na célula
	        	IntExpr position = new_original_positions[row][column];
	        	BoolExpr play_c = new_ctx.mkTrue();
	        	
	        	IntExpr number = new_ctx
	        			.mkInt(value);
	        	play_c = new_ctx.mkEq(number, position);
	        	
	        	new_solver.add(play_c);
	        	
				//  - SATISFIABLE ? 
	    		if (new_solver.check() == Status.SATISFIABLE) {
	    			// Se sim, guardar valor e continuar. 
	    			(new_board_values.get(row)).set(column, value);
	    			n_current_cells ++;
	                System.out.println("Generated a value.");
	            } else {
	            	// Se não, pop.
	            	new_solver.pop();
	            	System.out.println("Undone a value");
	            }
		        	
	        }
	       
	        System.out.println("Making sure puzzle as an unique solution");
	        if(unique_solution(new_solver,new_ctx,n,new_board_values,new_original_positions)) {
				success = true;
	        } 
		}
		
		//Passo 6 - Assign de this.solvers e por aí com os utilizados.
	    this.ctx = new_ctx;
		this.current_solver = new_solver;
		this.original_solver = new_solver; //TODO clone
	    this.original_positions = new_original_positions;
	    
		//Passo 5 - Devolver o puzzle populado  
		empty.setBoardValues(new_board_values);
		return empty;
		
		
	}
	
	public ArrayList<BoardCell> generateNewStream(StimkoData puzzle, int n_stream, ArrayList<ArrayList<BoardCell>> bad_streams)
	{
		
		//Generate random streams
		
		int n = puzzle.getN();

		ArrayList<ArrayList<Integer>> board = puzzle.getBoard();
		

		boolean done = false;
		boolean alive = true;
		boolean reverting;	
		BoardCell target = null;
		ArrayList<BoardCell> stream = null;
		ArrayList<BoardCell> tried_targets = new ArrayList<BoardCell>();
		
		
		while(!done) {
		
			alive = true;
			reverting = false;
			stream = new ArrayList<BoardCell>();
			target = null;

			//Find starting cell for stream
			int n_r = 0;
			//Find cell that is not in any streams or was already tried as first target
			for(ArrayList<Integer> r : board) {
				int n_c = 0;
				for(Integer v : r) {
					
					boolean tried = false;
					for(BoardCell tried_target : tried_targets) {
						if(tried_target.getColumn() == n_c && tried_target.getRow()==n_r) {
							tried = true; break;
						}
					}
					
					if(!tried && puzzle.findStream(n_r,n_c)==null) {
						target = new BoardCell(n_r,n_c);
						break;
					}
					n_c++;
				}
				if(target!= null) {
					break;
				}
				n_r++;
			}
			
			if(target==null) {
				alive = false;
			} else {
				stream.add(target);
			}
		
				
			int size_stream = 1;
			boolean can_block;
			
			// Starting of loop to construct stream
			while( alive && (size_stream<n) ) {
				
				can_block = (n_stream == n-1);
				
				//Find one random neighbor that is not in any stream
				
				ArrayList<BoardCell> neighbors = new ArrayList<BoardCell>();
				
				ArrayList<Integer> possible_n_r = new ArrayList<Integer>();
				int col = target.getColumn();
				int row = target.getRow();
				if(row != 0) {
					possible_n_r.add(row-1);					
				}
				if(row != n-1) {
					possible_n_r.add(row+1);
				}
				possible_n_r.add(row);
				ArrayList<Integer> possible_n_c = new ArrayList<Integer>();
				if(col != 0) {
					possible_n_c.add(col-1);					
				}
				if(col != n-1) {
					possible_n_c.add(col+1);
				}
				possible_n_c.add(col);
				
				for(Integer possible_row : possible_n_r) {
					for(Integer possible_col : possible_n_c) {
						
						//Exclude own cell
						if(!(possible_row==row && possible_col == col)) {
							BoardCell neighbor = new BoardCell(possible_row,possible_col);
							//Check if neighbor in any stream
							boolean in_current_stream = false;
							for(int k = 0; k < size_stream ; k++) {
								if(stream.get(k).getColumn() == possible_col && stream.get(k).getRow() == possible_row)
									in_current_stream = true;
							}
							if(!in_current_stream && puzzle.findStream(possible_row, possible_col) == null && (can_block || !puzzle.hasDirectlyDependentNeighbour(possible_row,possible_col, stream) )) {
								neighbors.add(neighbor);
							}
						}
					}
				}
				
				int n_original_neighbors = neighbors.size();
				
				//If forming stream is nearly formed and matches a bad stream received, remove the appropriate neighbours in order to avoid loops
				
				if(reverting || size_stream == n-1) {
					int check_n;
	
					for(ArrayList<BoardCell> bad_stream : bad_streams) {
						
						boolean matches = true;
						boolean matching ;
						//Check if every cell in current stream is present in the bad stream
						int k;
						int matching_index = -1;
						for(k=0 ; k<size_stream && matches; k++) {
							
							matching = false;
							BoardCell current_stream_cell = stream.get(k);
							for(check_n = 0; check_n < n && !matching; check_n++) {
								BoardCell bad_stream_cell = bad_stream.get(check_n);
								if(bad_stream_cell.getRow() == current_stream_cell.getRow() &&
										bad_stream_cell.getColumn() == current_stream_cell.getColumn()) {
									matching = true;
									//Store index in bad_stream of the matching cell of current target 
									if(k==size_stream-1) {
										matching_index = check_n;
									}
								}
							}
							matches = matches && matching;
							
						}
						
						
						if(matches) {
							
							int bad_neighbor_index = 0;
							if(matching_index < n-1) {
								bad_neighbor_index = matching_index+1;
								BoardCell bad_neighbor = bad_stream.get(bad_neighbor_index);
								for (Iterator<BoardCell> iterator = neighbors.iterator(); iterator.hasNext(); ) {
								    BoardCell neighbor = iterator.next();
								    if(neighbor.getColumn() == bad_neighbor.getColumn() && 
											neighbor.getRow() == bad_neighbor.getRow()) {
								        iterator.remove();
								        break;
									}
								}
							}
							if(matching_index > 0) {
								bad_neighbor_index = matching_index-1;
								BoardCell bad_neighbor = bad_stream.get(bad_neighbor_index);
								for (Iterator<BoardCell> iterator = neighbors.iterator(); iterator.hasNext(); ) {
								    BoardCell neighbor = iterator.next();
								    if(neighbor.getColumn() == bad_neighbor.getColumn() && 
											neighbor.getRow() == bad_neighbor.getRow()) {
								        iterator.remove();
								        break;
									}
								}
							}
							
							
							
						}
					}
					
				}
	
				int n_final_neighbors = neighbors.size();
				
				if(neighbors.size() == 0 ){
					
					if(n_original_neighbors != n_final_neighbors) {
						reverting = true;
					}
					
					if(reverting && size_stream > 1) {
						//Remove last target and decrement size_stream;
						stream.remove(size_stream-1);
						target = stream.get(size_stream-2);
						size_stream--;
					} else {						
						alive = false;
					}
				} else {
				
					Random r = new Random();
					BoardCell random_neighbor ;
					if(neighbors.size() > 1) {
						int High = neighbors.size();
						int random_index = r.nextInt(High);
		
						random_neighbor = neighbors.get(random_index);
					} else {
						random_neighbor = neighbors.get(0);
					}
					stream.add(random_neighbor);
					target = random_neighbor;
					size_stream++;
				}
				
			}
				
			// End of loop to construct stream
			
			if(!alive) {
				if(reverting)
					tried_targets.add(stream.get(0));
				else
					done = true;
			} else {
				done = true;
			}	
		
		}
		
		if(!alive) return null;
		
		return stream;
			
	}
	
}
