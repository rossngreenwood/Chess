import java.awt.Point;
import java.util.ArrayList;

public class Queen extends Piece 
{
	public Queen(Side side)
	{
		super(side);
		
		image = MediaLoader.loadImage(
			(side.equals(Side.WHITE) ? "White" : "Black") + "Queen.png");
		image = image.getScaledInstance(55, 55, 0);
	}
	
	public void updatePossibleMoves(Space[][] spaces)
	{
		possibleMoves = new ArrayList<Point>();

		for (int m = -1; m < 2; m++)
			for (int n = -1; n < 2; n++)
				for (int i = 1; i < 8; i++)
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
