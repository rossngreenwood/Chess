import java.util.ArrayList;

import java.awt.*;

public class Pawn extends Piece 
{
	private boolean enpassant = false;
	
	public Pawn(Side side)
	{
		super(side);
		
		image = MediaLoader.loadImage(
			(side.equals(Side.WHITE) ? "White" : "Black") + "Pawn.png");
		image = image.getScaledInstance(55, 55, 0);
	}
	
	public void updatePossibleMoves(Space[][] spaces)
	{
		possibleMoves = new ArrayList<Point>();
		
		int dy;
		
		if (side == Side.BLACK)
			dy = 1;
		else
			dy = -1;
		
		if (validSpace(row + dy))
			if (spaces[row + dy][col].getOccupant() == null)
			{
				addPossibleMove(new Point(dy, 0));
				
				if (row == (side == Side.BLACK ? 1 : 6) && 
						validSpace(row + (2 * dy)) && 
						spaces[row + 2 * dy][col].getOccupant() == null)
					addPossibleMove(new Point(2 * dy, 0));
			}

		if (validSpace(row + dy) && validSpace(col - 1))
			if (spaces[row + dy][col - 1].isOccupied())
				if (!spaces[row + dy][col - 1].getOccupant().getSide().equals(side))
					addPossibleMove(new Point(dy, -1));

		if (validSpace(row + dy) && validSpace(col + 1))
			if (spaces[row + dy][col + 1].isOccupied())
				if (!spaces[row + dy][col + 1].getOccupant().getSide().equals(side))
					addPossibleMove(new Point(dy, 1));

		if (validSpace(col + 1))
			if (spaces[row][col + 1].isOccupied())
				if (spaces[row][col + 1].getOccupant().getClass() == Pawn.class && 
						spaces[row][col + 1].getOccupant().getSide() != side)
					if (((Pawn) spaces[row][col + 1].getOccupant()).enpassant)
						addPossibleMove(new Point(dy, 1));
		
		if (validSpace(col - 1))
			if (spaces[row][col - 1].isOccupied())
				if (spaces[row][col - 1].getOccupant().getClass() == Pawn.class && 
						spaces[row][col - 1].getOccupant().getSide() != side)
					if (((Pawn) spaces[row][col - 1].getOccupant()).enpassant)
						addPossibleMove(new Point(dy, -1));
	}
	
	public void setEnPassant(boolean status)
	{
		enpassant = status;
	}
	
	public boolean isEnPassant()
	{
		return enpassant;
	}
}
