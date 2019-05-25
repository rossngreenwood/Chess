import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;

public class King extends Piece
{
	public static final int CLEAR = 0;
	public static final int CHECK = 1;
	public static final int CHECKMATE = 2;
	public static final int DRAW = 3;

	private int check;

	public King(Side side)
	{
		super(side);

		image = MediaLoader.loadImage(
				(side.equals(Side.WHITE) ? "White" : "Black") + "King.png");
			image = image.getScaledInstance(55, 55, 0);
	}

	public void updatePossibleMoves(Space[][] spaces)
	{
		possibleMoves = new ArrayList<Point>();

		for (int m = -1; m < 2; m++)
			for (int n = -1; n < 2; n++)
			{
				int ix = row + m;
				int iy = col + n;

				if (validSpace(ix) && validSpace(iy))
				{
					Space current = spaces[ix][iy];

					if (current.isOccupied())
					{
						if (!current.getOccupant().getSide().equals(side))
							addPossibleMove(new Point(m, n));
					}
					else
						addPossibleMove(new Point(m, n));
				}
			}

		calculateCheck(spaces);

		if (numMoved == 0 && check == CLEAR)
		{
			if (side == Side.WHITE)
			{
				if (spaces[7][0].isOccupied())
					if (spaces[7][0].getOccupant().getClass() == Rook.class)
						if (spaces[7][0].getOccupant().numMoved == 0)
							if (spaces[7][1].isClear() &&
									spaces[7][2].isClear() &&
									spaces[7][3].isClear())
							{
								spaces[7][3].setOccupant(this);

								if (((King) spaces[7][3].getOccupant()).getCheck(spaces) == King.CLEAR)
									addPossibleMove(new Point(0, -2));

								spaces[7][4].setOccupant(spaces[7][3].getOccupant());

								spaces[7][3].restore();

								calculateCheck(spaces);
							}

				if (spaces[7][7].isOccupied())
					if (spaces[7][7].getOccupant().getClass() == Rook.class)
						if (spaces[7][7].getOccupant().numMoved == 0)
							if (spaces[7][5].isClear() &&
									spaces[7][6].isClear())
							{
								spaces[7][5].setOccupant(this);

								if (((King) spaces[7][5].getOccupant()).getCheck(spaces) == King.CLEAR)
									addPossibleMove(new Point(0, 2));

								spaces[7][4].setOccupant(spaces[7][5].getOccupant());

								spaces[7][5].restore();

								calculateCheck(spaces);
							}
			}
			else
			{
				if (spaces[0][0].isOccupied())
					if (spaces[0][0].getOccupant().getClass() == Rook.class)
						if (spaces[0][0].getOccupant().numMoved == 0)
							if (spaces[0][1].isClear() &&
									spaces[0][2].isClear() &&
									spaces[0][3].isClear())
							{
								spaces[0][3].setOccupant(this);

								if (((King) spaces[0][3].getOccupant()).getCheck(spaces) == King.CLEAR)
									addPossibleMove(new Point(0, -2));

								spaces[0][4].setOccupant(spaces[0][3].getOccupant());

								spaces[0][3].restore();

								calculateCheck(spaces);
							}

				if (spaces[0][7].isOccupied())
					if (spaces[0][7].getOccupant().getClass() == Rook.class)
						if (spaces[0][7].getOccupant().numMoved == 0)
							if (spaces[0][5].isClear() &&
									spaces[0][6].isClear())
							{
								spaces[0][5].setOccupant(this);

								if (((King) spaces[0][5].getOccupant()).getCheck(spaces) == King.CLEAR)
									addPossibleMove(new Point(0, 2));

								spaces[0][4].setOccupant(spaces[0][5].getOccupant());

								spaces[0][5].restore();

								calculateCheck(spaces);
							}
			}
		}

		calculateCheck(spaces);
	}

	boolean performingcheck = false;

	private int calculateCheck(Space[][] spaces)
	{
		check = CLEAR;

		for (int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++)
			{
				Piece occupant = spaces[j][k].getOccupant();

				if (occupant != null)
				{
					if (!occupant.getSide().equals(side))
					{
						space.clearOccupant();
						occupant.updatePossibleMoves(spaces);
						space.setOccupant(this);

						ArrayList<Point> moves = occupant.getPossibleMoves();

						if (occupant.getClass() != Pawn.class)
							for (int i = 0; i < moves.size(); i++)
							{
								if (check == CLEAR)
									if (occupant.getRow() + moves.get(i).x == row
											&& occupant.getCol() + moves.get(i).y == col)
										check = CHECK;

									removePossibleMove(new Point(
											(moves.get(i).x + occupant.getRow()) - row,
											(moves.get(i).y + occupant.getCol()) - col
											));
							}
						else
							if (occupant.getSide() == Side.BLACK)
							{
								if (row - occupant.getRow() == 1)
									if (col - occupant.getCol() == 1 || col - occupant.getCol() == -1)
										check = CHECK;
							}
							else
							{
								if (row - occupant.getRow() == -1)
									if (col - occupant.getCol() == 1 || col - occupant.getCol() == -1)
										check = CHECK;
							}

						if (occupant.getClass() == Pawn.class)
							if (occupant.getSide() == Side.BLACK)
							{
								removePossibleMove(new Point(
									(1 + occupant.getRow()) - row,
									(-1 + occupant.getCol()) - col
									));

								removePossibleMove(new Point(
									(1 + occupant.getRow()) - row,
									(1 + occupant.getCol()) - col
									));
							}
							else
							{
								removePossibleMove(new Point(
									(-1 + occupant.getRow()) - row,
									(-1 + occupant.getCol()) - col
									));

								removePossibleMove(new Point(
									(-1 + occupant.getRow()) - row,
									(1 + occupant.getCol()) - col
									));
							}
					}
				}
			}

		if (Board.turn != side && check == CHECK && possibleMoves.size() == 0)
		{
			boolean allyFound = false;

			for (int j = 0; j < 8; j++)
				for (int k = 0; k < 8; k++)
					if (spaces[j][k].isOccupied())
						if (spaces[j][k].getOccupant().getSide() == side &&
								!spaces[j][k].getOccupant().equals(this))
						{
							spaces[j][k].getOccupant().updatePossibleMoves(spaces);
							if (spaces[j][k].getOccupant().getPossibleMoves().size() > 0)
							{
								allyFound = true;
								break;
							}
						}

			if (!allyFound)
			{
				check = DRAW;

				return check;
			}

			//System.out.println(nummoves);
		}

		if (check == CHECK && possibleMoves.size() == 0 && !performingcheck)
		{
			performingcheck = true;
			boolean checkmate = true;

			for (int j = 0; j < 8 && checkmate; j++)
				for (int k = 0; k < 8 && checkmate; k++)
				{
					Piece occupant = spaces[j][k].getOccupant();

					if (occupant != null)
						if (occupant.getSide().equals(side) && occupant.getClass() != King.class)
						{
							occupant.updatePossibleMoves(spaces);

							ArrayList<Point> moves = occupant.getPossibleMoves();

							for (int i = 0; i < moves.size() && checkmate; i++)
							{
								Space agent = occupant.getSpace();
								Space destination = spaces[occupant.getRow() + moves.get(i).x][occupant.getCol() + moves.get(i).y];

								Piece agentOccupant = occupant;
								Piece destinationOccupant = destination.getOccupant();

								if (agentOccupant.getClass() != King.class)
								{
									if (destinationOccupant != null)
										if (destinationOccupant.getClass() != King.class)
											break;

									if (destination.isOccupied())
									{
										if (destination.getOccupant().getSide() != agent.getOccupant().getSide())
										{
											destination.setOccupant(agent.getOccupant());
											agent.clearOccupant();
										}
									}
									else
									{
										destination.setOccupant(agentOccupant);
										agent.clearOccupant();
									}

									if (getCheck(spaces) == King.CLEAR)
										checkmate = false;

									agent.setOccupant(destination.getOccupant());

									if (destinationOccupant != null)
										destination.setOccupant(destinationOccupant);
									else
										destination.clearOccupant();

									if (destinationOccupant != null)
										if (destinationOccupant.getClass() != King.class)
											continue;
								}
							}
						}
				}

			if (checkmate)
				check = CHECKMATE;
			else
				check = CHECK;

			performingcheck = false;
		}

		if (check != CLEAR)
			space.highlight(Color.red);
		else
			space.restore();

		return check;
	}

	public int getCheck()
	{
		return check;
	}

	public int getCheck(Space[][] spaces)
	{
		updatePossibleMoves(spaces);
		return check;
	}
}
