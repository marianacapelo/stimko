package stimko;

import java.util.*;

import stimko.StimkoData.BoardCell;

import com.microsoft.z3.*;

public class SMTInteraction 
{
	
	private Context ctx;
	
	private Solver current_state;
	
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
		Context ctx = this.ctx;
		// n x n matrix of integer variables
		int n  = puzzle.getN();
        IntExpr[][] X = new IntExpr[n][];
        for (int i = 0; i < n; i++)
        {
            X[i] = new IntExpr[n];
            for (int j = 0; j < n; j++)
                X[i][j] = (IntExpr) ctx.mkConst(
                        ctx.mkSymbol("x_" + (i + 1) + "_" + (j + 1)),
                        ctx.getIntSort());
        }

        // each cell contains a value in {1, ..., n}
        BoolExpr[][] cells_c = new BoolExpr[n][];
        for (int i = 0; i < n; i++)
        {
            cells_c[i] = new BoolExpr[n];
            for (int j = 0; j < n; j++)
                cells_c[i][j] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), X[i][j]),
                        ctx.mkLe(X[i][j], ctx.mkInt(n)));
        }
		
        // each row contains a digit at most once
        BoolExpr[] rows_c = new BoolExpr[n];
        for (int i = 0; i < n; i++)
            rows_c[i] = ctx.mkDistinct(X[i]);

        // each column contains a digit at most once
        BoolExpr[] cols_c = new BoolExpr[n];
        for (int j = 0; j < n; j++)
            cols_c[j] = ctx.mkDistinct(X[j]);

        // each stream contains a digit at most once
        BoolExpr[] streams_c = new BoolExpr[n];
        for (int i0 = 0; i0 < n; i0++)
        {
            ArrayList<BoardCell> current_stream = puzzle.getStream(i0);
            
            //Create expression for the n cells in the stream
            IntExpr[] cells_stream = new IntExpr[n];
            
        	//Fetch each cell of the stream
            for (int j0 = 0; j0 < n; j0++)
            {
            	BoardCell current_cell = current_stream.get(j0);
            	int row = current_cell.getRow();
            	int column = current_cell.getColumn();
            	
            	cells_stream[j0] = X[row][column];
            }
            
            streams_c[i0] = ctx.mkDistinct(cells_stream);
        }
        
        BoolExpr stimko_c = ctx.mkTrue();
        for (BoolExpr[] t : cells_c)
        	stimko_c = ctx.mkAnd(ctx.mkAnd(t), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(rows_c), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(cols_c), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(streams_c), stimko_c);

        //Read from puzzle TODO
        // stimko instance, we use '0' for empty cells
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
        s.add(stimko_c);
        s.add(instance_c);
        this.current_state = s;
        
//        System.out.println(stimko_c);
        
//        if (s.check() == Status.SATISFIABLE)
//        {
//            Model m = s.getModel();
//            Expr[][] R = new Expr[9][9];
//            for (int i = 0; i < 9; i++)
//                for (int j = 0; j < 9; j++)
//                    R[i][j] = m.evaluate(X[i][j], false);
//            System.out.println("stimko solution:");
//            for (int i = 0; i < 9; i++)
//            {
//                for (int j = 0; j < 9; j++)
//                    System.out.print(" " + R[i][j]);
//                System.out.println();
//            }
//        } else
//        {
//            System.out.println("Failed to solve stimko");
//            throw new Exception();
//        }
		
	}
	
	
	public void solveStimko(StimkoData puzzle) throws Exception 
	{
		Context ctx = this.ctx;
		// n x n matrix of integer variables
		int n  = puzzle.getN();
        IntExpr[][] X = new IntExpr[n][];
        for (int i = 0; i < n; i++)
        {
            X[i] = new IntExpr[n];
            for (int j = 0; j < n; j++)
                X[i][j] = (IntExpr) ctx.mkConst(
                        ctx.mkSymbol("x_" + (i + 1) + "_" + (j + 1)),
                        ctx.getIntSort());
        }

        // each cell contains a value in {1, ..., n}
        BoolExpr[][] cells_c = new BoolExpr[n][];
        for (int i = 0; i < n; i++)
        {
            cells_c[i] = new BoolExpr[n];
            for (int j = 0; j < n; j++)
                cells_c[i][j] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), X[i][j]),
                        ctx.mkLe(X[i][j], ctx.mkInt(n)));
        }
		
        // each row contains a digit at most once
        BoolExpr[] rows_c = new BoolExpr[n];
        for (int i = 0; i < n; i++)
            rows_c[i] = ctx.mkDistinct(X[i]);

        // each column contains a digit at most once
        BoolExpr[] cols_c = new BoolExpr[n];
        for (int j = 0; j < n; j++)
            cols_c[j] = ctx.mkDistinct(X[j]);

        // each stream contains a digit at most once
        BoolExpr[] streams_c = new BoolExpr[n];
        for (int i0 = 0; i0 < n; i0++)
        {
            ArrayList<BoardCell> current_stream = puzzle.getStream(i0);
            
            //Create expression for the n cells in the stream
            IntExpr[] cells_stream = new IntExpr[n];
            
        	//Fetch each cell of the stream
            for (int j0 = 0; j0 < n; j0++)
            {
            	BoardCell current_cell = current_stream.get(j0);
            	int row = current_cell.getRow();
            	int column = current_cell.getColumn();
            	
            	cells_stream[j0] = X[row][column];
            }
            
            streams_c[i0] = ctx.mkDistinct(cells_stream);
        }
        
        BoolExpr stimko_c = ctx.mkTrue();
        for (BoolExpr[] t : cells_c)
        	stimko_c = ctx.mkAnd(ctx.mkAnd(t), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(rows_c), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(cols_c), stimko_c);
        
        stimko_c = ctx.mkAnd(ctx.mkAnd(streams_c), stimko_c);

        //Read from puzzle TODO
        // stimko instance, we use '0' for empty cells
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
        s.add(stimko_c);
        s.add(instance_c);
        System.out.println(stimko_c);
        
        if (s.check() == Status.SATISFIABLE)
        {
            Model m = s.getModel();
            Expr[][] R = new Expr[9][9];
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    R[i][j] = m.evaluate(X[i][j], false);
            System.out.println("stimko solution:");
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                    System.out.print(" " + R[i][j]);
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
