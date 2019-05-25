import java.awt.Point;
import java.util.*;

public class Bishop extends Piece 
{
	public Bishop(Side side)
	{
		super(side);
		
		image = MediaLoader.loadImage(
			(side.equals(Side.WHITE) ? "White" : "Black") + "Bishop.png");
		image = image.getScaledInstance(55, 55, 0);
	}
	
	public void updatePossibleMoves(Space[][] spaces)
	{
		possibleMoves = new ArrayList<Point>();
		
		for (int m = -1; m < 2; m++)
			for (int n = -1; n < 2; n++)
				if (m != 0 && n != 0)
					for (int i = 1; i < 8; i++)
					{
						if (validSpace(row + (i * m)) && validSpace(col + (i * n)))
						{
							Space current = spaces[row + (i * m)][col + (i * n)];
							
							if (current.isOccupied())
							{
								if (!current.getOccupant().getSide().equals(side))
									addPossibleMove(new Point(i * m, i * n));
								
								break;
							}
							else
								addPossibleMove(new Point(i * m, i * n));
						}
						else
							break;
					}
	}
}
