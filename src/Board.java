import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.applet.AudioClip;

public class Board extends JPanel implements MouseListener, MouseMotionListener
{
	protected static final long serialVersionUID = 1L;

	protected BufferedImage buffer;
	protected Graphics gBuffer;

	protected final static int length = 480;
	
	protected Space[][] spaces;
	
	protected King[] kings;

	public static Side turn;

	protected boolean checkmate = false;
	protected boolean draw = false;
	protected boolean forfeit = false;
	
	protected Space agent;
	protected Space destination;
	
	protected boolean validMove = false;

	protected AudioClip moveClip;
	
	protected Space lastAgent;
	protected Space lastDestination;
	
	public Board()
	{
		super();
		
		turn = Side.WHITE;
		
		buffer = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
		
		gBuffer = buffer.getGraphics();
		
		generateSpaces();
		
		kings = new King[2];
		
		generatePieces();
		
		lastAgent = null;
		lastDestination = null;
		
		if (this.getClass() == Board.class)
		{
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		setSize(length, length);
		setPreferredSize(new Dimension(length, length));

		moveClip = MediaLoader.loadSound("newmail.wav");
	}
	
	public void paint(Graphics g)
	{
		drawSpaces(gBuffer);
		
		if (checkmate)
		{
			gBuffer.setColor(Color.red);
			
			Font font = new Font("Times New Roman", Font.BOLD, 70);
			FontMetrics metrics = g.getFontMetrics(font);
			
			String checkmatemsg = new String("CHECKMATE");
			
			int msgx = (buffer.getWidth() / 2) - (metrics.stringWidth(checkmatemsg) / 2);
			int msgy = (buffer.getHeight() / 2) + (metrics.getHeight() / 4);
			
			gBuffer.setFont(font);
			gBuffer.drawString(checkmatemsg, msgx, msgy);
		}

		if (draw)
		{
			gBuffer.setColor(Color.red);
			
			Font font = new Font("Times New Roman", Font.BOLD, 70);
			FontMetrics metrics = g.getFontMetrics(font);
			
			String checkmatemsg = new String("DRAW");
			
			int msgx = (buffer.getWidth() / 2) - (metrics.stringWidth(checkmatemsg) / 2);
			int msgy = (buffer.getHeight() / 2) + (metrics.getHeight() / 4);
			
			gBuffer.setFont(font);
			gBuffer.drawString(checkmatemsg, msgx, msgy);
		}

		if (forfeit)
		{
			gBuffer.setColor(Color.red);
			
			Font font = new Font("Times New Roman", Font.BOLD, 70);
			FontMetrics metrics = g.getFontMetrics(font);
			
			String checkmatemsg = new String("FORFEIT");
			
			int msgx = (buffer.getWidth() / 2) - (metrics.stringWidth(checkmatemsg) / 2);
			int msgy = (buffer.getHeight() / 2) + (metrics.getHeight() / 4);
			
			gBuffer.setFont(font);
			gBuffer.drawString(checkmatemsg, msgx, msgy);
		}
		
		super.paint(g);
		g.drawImage(buffer, 0, 0, this);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			handleMousePressed(e);
	}

	public synchronized void handleMousePressed(MouseEvent e)
	{
		Space space = spaces[(int) Math.floor((e.getPoint().y) * 8 / length)]
		                     [(int) Math.floor((e.getPoint().x) * 8 / length)];
		
		kings[Side.BLACK.ordinal()].updatePossibleMoves(spaces);
		kings[Side.WHITE.ordinal()].updatePossibleMoves(spaces);
		
		if (space.isOccupied())
			if (space.getOccupant().getSide() == turn)
			{
				agent = space;
				agent.getOccupant().updatePossibleMoves(spaces);

				if (this instanceof NetworkBoard ? ((NetworkBoard) this).getSide() == turn : true)
					if (agent != kings[Side.BLACK.ordinal()].getSpace() && agent != kings[Side.WHITE.ordinal()].getSpace())
						agent.highlight(Color.yellow);
				
				destination = space;
			}
	}

	public void mouseDragged(MouseEvent e)
	{
		handleMouseDragged(e);
	}

	public synchronized void handleMouseDragged(MouseEvent e)
	{
		int mx = e.getPoint().x;
		int my = e.getPoint().y;

		if (mx <= 0 || mx >= getSize().width || my < 0 || my >= getSize().height)
			return;
		
		if (agent != null && destination != null)
		{
			if (
					mx < destination.getX() || 
					my < destination.getY() || 
					mx > destination.getX() + getWidth() / 8 || 
					my > destination.getY() + getHeight() / 8)
			{
				if (destination.isOccupied())
				{
					if (destination.getOccupant().getClass() != King.class &&
							!agent.equals(destination))
					{
						destination.restore();
					}
				}
				else
				{
					if (!agent.equals(destination))
					{
						destination.restore();
					}
				}
				
				destination = spaces[(int) Math.floor((my) * 8 / length)]
				                     [(int) Math.floor((mx) * 8 / length)];
				
				if (!destination.equals(agent) && !agent.equals(destination))
				{
					validMove = false;
					
					if (agent.isOccupied())
						for (int i = 0; i < agent.getOccupant().getPossibleMoves().size(); i++)
							if ((new Point(destination.getRow() - agent.getRow(), destination.getCol() - agent.getCol())).equals( 
									agent.getOccupant().getPossibleMoves().get(i)))
								validMove = true;
					
					if (validMove)
					{
						Piece agentOccupant = agent.getOccupant();
						/*if (agentOccupant != null)
							if (agentOccupant.getClass() != King.class)
									agentOccupant.setSpace(null);*/
						
						Piece destinationOccupant = destination.getOccupant();
						/*if (destinationOccupant != null)
							if (destinationOccupant.getClass() != King.class)
								destinationOccupant.setSpace(null);*/
						
						destination.setOccupant(agent.getOccupant());
						
						if (kings[agentOccupant.getSide().ordinal()].getCheck(spaces) != King.CLEAR && 
								kings[agentOccupant.getSide().ordinal()].getCheck(spaces) != King.DRAW)
							validMove = false;
						
						agent.setOccupant(agentOccupant);
						
						if (destinationOccupant != null)
							destination.setOccupant(destinationOccupant);
						else
							destination.clearOccupant();
						
						kings[agent.getOccupant().getSide().ordinal()].getCheck(spaces);
					}
					
					if (this instanceof NetworkBoard ? ((NetworkBoard) this).getSide() == turn : true)
						if (!(destination.equals(kings[Side.BLACK.ordinal()].space) || destination.equals(kings[Side.WHITE.ordinal()].space)))
							if (validMove)
								destination.highlight(Color.green);
							else
								destination.highlight(Color.red);
				}

				repaint();
			}

			if (agent.equals(destination))
			{
				if (agent != kings[Side.BLACK.ordinal()].getSpace() && agent != kings[Side.WHITE.ordinal()].getSpace())
					agent.restore();
			}
			else
				if (this instanceof NetworkBoard ? ((NetworkBoard) this).getSide() == turn : true)
					if (agent != kings[Side.BLACK.ordinal()].getSpace() && agent != kings[Side.WHITE.ordinal()].getSpace())
						agent.highlight(Color.yellow);
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			handleMouseReleased(e);
	}
	
	public synchronized void handleMouseReleased(MouseEvent e)
	{
		int mx = e.getPoint().x;
		int my = e.getPoint().y;
		
		if (agent != null)
			if (agent != kings[Side.BLACK.ordinal()].getSpace() && agent != kings[Side.WHITE.ordinal()].getSpace())
				agent.restore();
		
		if (mx < 0 || mx >= getSize().width || my < 0 || my >= getSize().height)
		{
			agent = null;
		
			if (destination != null)
			{
				if (!destination.equals(kings[Side.WHITE.ordinal()].getSpace()) && !destination.equals(kings[Side.BLACK.ordinal()].getSpace()))
					destination.restore();

				destination = null;
			}
			
			repaint();
			
			return;
		}
		
		if (agent != null && agent.isOccupied() && 
				destination != null && !destination.equals(agent))
		{
			if (!destination.equals(kings[Side.WHITE.ordinal()]) && ! destination.equals(kings[Side.BLACK.ordinal()]))
				destination.restore();

			if (validMove)
			{
				if (destination.isOccupied())
				{
					if (destination.getOccupant().getSide() != agent.getOccupant().getSide())
						movePiece();
				}
				else
					if (!(agent.getOccupant().getClass() == King.class && 
							(Math.abs(destination.getRow() - agent.getRow()) == 2 || 
							Math.abs(destination.getCol() - agent.getCol()) == 2)))
					{
						movePiece();
					}
					else
					{
						agent.getOccupant().addMove();
						
						if (agent.getOccupant().getSide() == Side.WHITE)
						{
							if (destination.getCol() == 2)
							{
								spaces[7][3].setOccupant(agent.getOccupant());
								
								if (((King) spaces[7][3].getOccupant()).getCheck(spaces) == King.CLEAR)
								{
									spaces[7][2].setOccupant(spaces[7][3].getOccupant());
									
									spaces[7][3].setOccupant(spaces[7][0].getOccupant());
								}
								else
									agent.setOccupant(spaces[7][3].getOccupant());
							}
							else
							{
								spaces[7][5].setOccupant(agent.getOccupant());
								
								if (((King) spaces[7][5].getOccupant()).getCheck(spaces) == King.CLEAR)
								{
									spaces[7][6].setOccupant(spaces[7][5].getOccupant());
									
									spaces[7][5].setOccupant(spaces[7][7].getOccupant());								}
								else
									agent.setOccupant(spaces[7][5].getOccupant());
							}
						}
						else
						{
							if (destination.getCol() == 2)
							{
								spaces[0][3].setOccupant(agent.getOccupant());
								
								if (((King) spaces[0][3].getOccupant()).getCheck(spaces) == King.CLEAR)
								{
									spaces[0][2].setOccupant(spaces[0][3].getOccupant());

									spaces[0][3].setOccupant(spaces[0][0].getOccupant());
								}
								else
									agent.setOccupant(spaces[0][3].getOccupant());
							}
							else
							{
								spaces[0][5].setOccupant(agent.getOccupant());
								
								if (((King) spaces[0][5].getOccupant()).getCheck(spaces) == King.CLEAR)
								{
									spaces[0][6].setOccupant(spaces[0][5].getOccupant());

									spaces[0][5].setOccupant(spaces[0][7].getOccupant());
								}
								else
									agent.setOccupant(spaces[0][5].getOccupant());
							}
						}
						
						if (turn == Side.WHITE)
							turn = Side.BLACK;
						else
							turn = Side.WHITE;

						if (getTopLevelAncestor().getClass() == GameFrame.class)
							((GameFrame) this.getTopLevelAncestor()).setTurn(turn);
						
						for (int j = 0; j < 8; j++)
							for (int k = 0; k < 8; k++)
								if (spaces[j][k].isOccupied())
									if (spaces[j][k].getOccupant().getClass() == Pawn.class && spaces[j][k].getOccupant().getSide() == turn)
										((Pawn) spaces[j][k].getOccupant()).setEnPassant(false);
					}
			}
			
			kings[Side.WHITE.ordinal()].getCheck(spaces);
			kings[Side.BLACK.ordinal()].getCheck(spaces);

			repaint();
		}

		agent = null;
		
		destination = null;
	}
	
	public void movePiece()
	{
		if (destination.setOccupant(agent.getOccupant()))
		{
			if (destination.getOccupant().getClass() == Pawn.class)
				if (Math.abs(destination.getRow() - agent.getRow()) == 2)
					((Pawn) destination.getOccupant()).setEnPassant(true);
				else
					((Pawn) destination.getOccupant()).setEnPassant(false);
			
			if (destination.getOccupant().getClass() == Pawn.class)
				if (destination.getCol() != agent.getCol())
					if (spaces[agent.getRow()][destination.getCol()].isOccupied())
						if (spaces[agent.getRow()][destination.getCol()].getOccupant().getClass() == Pawn.class && 
								spaces[agent.getRow()][destination.getCol()].getOccupant().getSide() != destination.getOccupant().getSide())
							if (((Pawn) spaces[agent.getRow()][destination.getCol()].getOccupant()).isEnPassant())
								spaces[agent.getRow()][destination.getCol()].clearOccupant();
			
			destination.getOccupant().addMove();

			if (kings[Side.BLACK.ordinal()].getCheck(spaces) == King.CHECKMATE || 
					kings[Side.WHITE.ordinal()].getCheck(spaces) == King.CHECKMATE)
			{
				setCheckmate();
			}

			if (kings[Side.BLACK.ordinal()].getCheck(spaces) == King.DRAW || 
					kings[Side.WHITE.ordinal()].getCheck(spaces) == King.DRAW)
			{
				setDraw();
			}
			
			if (getClass() == NetworkBoard.class && ChessServer.isSoundEnabled())
			{
				if (turn != ((NetworkBoard) this).getSide())
				{
					moveClip.play();
					
					if (!getTopLevelAncestor().hasFocus())
						((Window) getTopLevelAncestor()).toFront();
				}
			}
			
			agent.restore();
			
			if (turn == Side.WHITE)
				turn = Side.BLACK;
			else
				turn = Side.WHITE;

			if (getTopLevelAncestor().getClass() == GameFrame.class)
				((GameFrame) this.getTopLevelAncestor()).setTurn(turn);
			
			for (int j = 0; j < 8; j++)
				for (int k = 0; k < 8; k++)
					if (spaces[j][k].isOccupied())
						if (spaces[j][k].getOccupant().getClass() == Pawn.class && spaces[j][k].getOccupant().getSide() == turn)
							((Pawn) spaces[j][k].getOccupant()).setEnPassant(false);
		}
		
		if (destination.getOccupant().getClass() == Pawn.class && 
				(destination.getRow() == 0 || destination.getRow() == 7))
			destination.setOccupant(new Queen(destination.getOccupant().getSide()));
		
		lastAgent = agent;
		lastDestination = destination;
		
		if (this instanceof NetworkBoard)
			if (((NetworkBoard) this).getTurn() == (((NetworkBoard) this).getSide()))
				blinkLastMove();
	}
	
	public boolean isCheckmate()
	{
		return checkmate;
	}

	public void generateSpaces()
	{
		spaces = new Space[8][8];
		
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if (i % 2 == j % 2)
					spaces[i][j] = new Space(i, j, length, Side.WHITE);
				else
					spaces[i][j] = new Space(i, j, length, Side.BLACK);
	}

	public void generatePieces()
	{
		for (int i = 0; i < 8; i++)
		{
			spaces[1][i].setOccupant(new Pawn(Side.BLACK));
			spaces[6][i].setOccupant(new Pawn(Side.WHITE));
		}
		
		spaces[0][0].setOccupant(new Rook(Side.BLACK));
		spaces[0][7].setOccupant(new Rook(Side.BLACK));
		spaces[7][0].setOccupant(new Rook(Side.WHITE));
		spaces[7][7].setOccupant(new Rook(Side.WHITE));

		spaces[0][1].setOccupant(new Knight(Side.BLACK));
		spaces[0][6].setOccupant(new Knight(Side.BLACK));
		spaces[7][1].setOccupant(new Knight(Side.WHITE));
		spaces[7][6].setOccupant(new Knight(Side.WHITE));

		spaces[0][2].setOccupant(new Bishop(Side.BLACK));
		spaces[0][5].setOccupant(new Bishop(Side.BLACK));
		spaces[7][2].setOccupant(new Bishop(Side.WHITE));
		spaces[7][5].setOccupant(new Bishop(Side.WHITE));

		spaces[0][3].setOccupant(new Queen(Side.BLACK));
		spaces[7][3].setOccupant(new Queen(Side.WHITE));
		
		spaces[0][4].setOccupant(new King(Side.BLACK));
		kings[Side.BLACK.ordinal()] = (King) spaces[0][4].getOccupant();
		
		spaces[7][4].setOccupant(new King(Side.WHITE));
		kings[Side.WHITE.ordinal()] = (King) spaces[7][4].getOccupant();
	}
	
	public void drawSpaces(Graphics g)
	{
		for (int j = 0; j < 8; j += 1)
			for (int k = 0; k < 8; k += 1)
				spaces[j][k].draw(g, this);
	}
	
	public void setDisable()
	{
		for (int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++)
					if (spaces[j][k].getSide() == Side.BLACK)
						spaces[j][k].highlight(new Color(98, 98, 98));
					else
						spaces[j][k].highlight(new Color(158, 158, 158));

		if (getListeners(MouseListener.class).length > 0)
			removeMouseListener(getListeners(MouseListener.class)[0]);

		if (getListeners(MouseListener.class).length > 0)
			removeMouseMotionListener(getListeners(MouseMotionListener.class)[0]);
		
		repaint();
	}
	
	public boolean checkmate()
	{
		return checkmate;
	}
	
	public Side getTurn()
	{
		return turn;
	}
	
	public void setCheckmate()
	{
		checkmate = true;
		removeMouseListener(getMouseListeners()[0]);
		removeMouseMotionListener(getMouseMotionListeners()[0]);
		repaint();
	}

	public void setDraw()
	{
		draw = true;
		removeMouseListener(getMouseListeners()[0]);
		removeMouseMotionListener(getMouseMotionListeners()[0]);
		repaint();
	}

	public void setForfeit()
	{
		forfeit = true;
		removeMouseListener(getMouseListeners()[0]);
		removeMouseMotionListener(getMouseMotionListeners()[0]);
		repaint();
	}
	
	public synchronized void blinkLastMove()
	{
		if (lastAgent != null && lastDestination != null)
		{
			new Thread()
			{
				public Space ag = lastAgent;
				public Space dest = lastDestination;
				
				public void run()
				{
					for (int i = 0; i < 3; i++)
					{
						try
						{
							dest.highlight(Color.orange);
							ag.restore();
							repaint();
							sleep(400);
							dest.restore();
							
							if (i < 2)
								ag.highlight(Color.orange);
							
							repaint();
							sleep(400);
						}
						catch (Exception e){}
					}
					
					ag.restore();
					repaint();
				}
			}.start();
		}
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}