import java.awt.event.*;
import java.io.*;

public class NetworkBoard extends Board implements MouseListener, MouseMotionListener//, Runnable
{
	private static final long serialVersionUID = 1L;
	
	public ChessServer server;
	
	public ObjectOutputStream out;
	
	private Side side;

	public NetworkBoard(ChessServer server)
	{
		super();
		
		this.server = server;
		
		out = server.getObjectOutputStream();

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void setSide(Side side)
	{
		this.side = side;
		
		if (getTopLevelAncestor().getClass() == GameFrame.class)
			if (side == Side.WHITE)
				((GameFrame) getTopLevelAncestor()).appendMessage("You are playing as white.\n");
			else
				((GameFrame) getTopLevelAncestor()).appendMessage("You are playing as black.\n");
	}
	
	public Side getSide()
	{
		return side;
	}
	
	public ChessServer getServer()
	{
		return server;
	}
	
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			if (turn == side)
			{
				handleMousePressed(e);
				
				try
				{
					out.writeObject(e);
					out.flush();
				}
				catch (Exception ex){}
			}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (turn == side)
		{
			handleMouseDragged(e);
			
			try
			{
				out.writeObject(e);
				out.flush();
			}
			catch (Exception ex){}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			if (turn == side)
			{
				handleMouseReleased(e);
				
				try
				{
					out.writeObject(e);
					out.flush();
				}
				catch (Exception ex){}
			}
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
}
