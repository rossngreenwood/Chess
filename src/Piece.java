import java.awt.Point;
import java.util.ArrayList;
import java.awt.*;

public abstract class Piece 
{
	protected Side side;

	protected int row;
	protected int col;

	protected Space space;
	
	protected ArrayList<Point> possibleMoves;
	
	protected Image image;
	
	protected int numMoved;
	
	public Piece(){}
	
	public Piece(Side side)
	{
		this.side = side;
		
		possibleMoves = new ArrayList<Point>();
	}
	
	public void addMove()
	{
		numMoved++;
	}
	
	public Side getSide()
	{
		return side;
	}

	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public void addPossibleMove(Point p)
	{
		possibleMoves.add(p);
	}

	public void removePossibleMove(Point p)
	{
		possibleMoves.remove(p);
	}

	public void removePossibleMove(int p)
	{
		possibleMoves.remove(p);
	}

	public ArrayList<Point> getPossibleMoves()
	{
		return possibleMoves;
	}
	
	public void updatePossibleMoves(Space[][] spaces){}
	
	public Image getImage()
	{
		return image;
	}
	
	public Space getSpace()
	{
		return space;
	}

	public void setSpace(Space space)
	{
		this.space = space;

		if (this.space != null)
		{
			row = space.getRow();
			col = space.getCol();
		}
	}
	
	public boolean validSpace(int n)
	{
		if (n < 0 || n >= 8)
			return false;
		else
			return true;
	}
}
