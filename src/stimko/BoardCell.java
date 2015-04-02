package strimko;

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