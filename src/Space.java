import java.awt.*;
import java.awt.image.ImageObserver;

public class Space {
	private int row;
	private int col;
	
	private int x;
	private int y;
	
	private int s;
	
	private Color defaultColor;
	private Color color;
	
	private Piece occupant;
	
	public Space(int row, int col, int s, Side side)
	{
		this.row = row;
		this.col = col;
		
		x = this.col * s / 8;
		y = this.row * s / 8;
		
		this.s = s / 8;
		
		if (side == Side.WHITE)
			defaultColor = Color.white;
		else
			defaultColor = new Color(20, 20, 20);
		
		color = defaultColor;

		color = defaultColor;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public Side getSide()
	{
		if (defaultColor == Color.white)
			return Side.WHITE;
		else
			return Side.BLACK;
	}
	
	public void highlight(Color c)
	{
		color = c;
	}
	
	public void restore()
	{
		color = defaultColor;

		if (occupant instanceof King)
			if (((King) occupant).getCheck() == King.CHECK)
				color = Color.red;
	}
	
	public Piece getOccupant()
	{
		return occupant;
	}

	public boolean isOccupied()
	{
		if (occupant != null)
			return true;
		else
			return false;
	}

	public boolean isClear()
	{
		if (occupant == null)
			return true;
		else
			return false;
	}
	
	public boolean setOccupant(Piece piece)
	{
		if (occupant != null)
			if (occupant.getClass() == King.class)
				return false;
		
		if (piece.getSpace() != null)
			piece.getSpace().clearOccupant();
		
		occupant = piece;
		occupant.setSpace(this);
		
		return true;
	}
	
	public void clearOccupant()
	{
		occupant = null;
	}
	
	public void draw(Graphics g, ImageObserver frame)
	{
		g.setColor(color);
		
		g.fillRect(x, y, s, s);
		
		if (defaultColor == Color.white || color != defaultColor)
		{
			Color c = color;

			for (int i = 3; i >= 0; i--)
			{
				g.setColor(c);
				g.drawLine(x+i, y+i, x+i, y+s+i);
				g.drawLine(x+i, y+i, x+s+i, y+i);
				c = c.darker();
			}
			
			c = color.darker();
			
			for (int i = 4; i >= 0; i--)
			{
				g.setColor(c);
				g.drawLine(x+s-i, y+i, x+s-i, y+s-i);
				g.drawLine(x+i, y+s-i, x+s-i, y+s-i);
				c = c.darker();
			}
			
		}
		else if (defaultColor == color)
		{
			Color c = color;

			for (int i = 3; i > 0; i--)
			{
				g.setColor(c);
				g.drawLine(x+i, y+i, x+i, y+s+i);
				g.drawLine(x+i, y+i, x+s+i, y+i);
				c = c.brighter().brighter();
			}
			
			c = color;
			
			for (int i = 3; i >= 0; i--)
			{
				g.setColor(c);
				g.drawLine(x+s-i, y+i, x+s-i, y+s-i);
				g.drawLine(x+i, y+s-i, x+s-i, y+s-i);
				c = c.darker();
			}
			
		}
		
		if (occupant != null)
		{
			try
			{
				Image pieceImage = occupant.getImage();
				
				g.drawImage(pieceImage, x + (s / 2) - (pieceImage.getWidth(frame) / 2), y + (s / 2) - (pieceImage.getHeight(frame) / 2), frame);
				
			}
			catch(Exception e){}
		}
	}
	
	public int getLength()
	{
		return s;
	}
}
