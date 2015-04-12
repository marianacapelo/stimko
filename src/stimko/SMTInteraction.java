package stimko;

import java.util.*;

import stimko.StimkoData.BoardCell;

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

        System.out.println("init!");
        Solver s = this.ctx.mkSolver();
        s.add(stimko_c);
        s.add(instance_c);
        s.push();
        this.current_solver = s;
        this.original_solver = s;
		
	}
	
	public void play(int row,int column,int value) throws Z3Exception
	{    
		IntExpr position = this.original_positions[row-1][column-1];
		BoolExpr play_c = this.ctx.mkTrue();
		play_c = this.ctx.mkAnd(this.ctx.mkEq(this.ctx.mkInt(value), position));
		this.current_solver.add(play_c);
		this.current_solver.push();
	}
	
	public void undo() throws Z3Exception
	{
		System.out.println("before pop");
		System.out.println(this.current_solver);

		this.current_solver.pop(1);;
		System.out.println("after pop");
		System.out.println(this.current_solver);

		this.current_solver.push();
		System.out.println("after push");
		System.out.println(this.current_solver);

		
	}
	
	
	//TODO solveStimko returning puzzle solved and 
	public void solveStimko(StimkoData puzzle) throws Exception 
	{   
		System.out.println(this.current_solver);
        if (this.current_solver.check() == Status.SATISFIABLE)
        {
            Model m = this.current_solver.getModel();
            int n = puzzle.getN();
            Expr[][] solution = new Expr[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    solution[i][j] = m.evaluate(this.original_positions[i][j], false);
            System.out.println("stimko solution:");
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                    System.out.print(" " + solution[i][j]);
                System.out.println();
            }
        } else
        {
            System.out.println("Failed to solve stimko");
            throw new Exception();
        }
		
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
	
	

}
