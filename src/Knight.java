import java.awt.Point;
import java.util.ArrayList;

public class Knight extends Piece 
{
	public Knight(Side side)
	{
		super(side);
		
		image = MediaLoader.loadImage(
			(side.equals(Side.WHITE) ? "White" : "Black") + "Knight.png");
		image = image.getScaledInstance(55, 55, 0);
	}
	
	public void updatePossibleMoves(Space[][] spaces)
	{
		possibleMoves = new ArrayList<Point>();
		
		addValidMove(1, 2, spaces);
		addValidMove(1, -2, spaces);
		addValidMove(-1, 2, spaces);
		addValidMove(-1, -2, spaces);
		addValidMove(2, 1, spaces);
		addValidMove(2, -1, spaces);
		addValidMove(-2, 1, spaces);
		addValidMove(-2, -1, spaces);
	}
	
	private void addValidMove(int r, int c, Space[][] spaces)
	{
		if (validSpace(row + r) && validSpace(col + c))
			if (spaces[row + r][col + c].getOccupant() != null)
			{
				if (!spaces[row + r][col + c].getOccupant().getSide().equals(side))
				{
					addPossibleMove(new Point(r, c));
				}
			}
			else
				addPossibleMove(new Point(r, c));
		
	}
}
