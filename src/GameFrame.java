import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener, WindowListener, MouseListener
{
	protected static final long serialVersionUID = 1L;

	protected BufferedImage buffer;
	protected Graphics gBuffer;
	
	protected static final Point margin = new Point(3, 39);
	
	protected Board board;
	
	protected JTextPane msgLog;
	protected TurnIndicator turnIndicator;
	protected JTextField inputField;
	protected JButton sendButton;

	protected AudioClip imsend;
	
	public static void main(String[] args)
	{
		GameFrame game = new GameFrame(new Board());
		game.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public GameFrame(Board gameBoard)
	{
		super("Chess");
		
		board = gameBoard;
		
		buffer = new BufferedImage(Board.length, Board.length, BufferedImage.TYPE_INT_RGB);
		gBuffer = buffer.getGraphics();
		
		addWindowListener(this);
		
		setJMenuBar(new ChessMenu(board));
		
		Container pane = getContentPane();
		
		pane.setLayout(new FlowLayout());
		
		pane.add(board);
		
		if (board.getClass() == Board.class)
		{
			setSize(Board.length + 15, Board.length + (int) getJMenuBar().getPreferredSize().getHeight() + 51);
		}
		else if (board.getClass() == NetworkBoard.class)
		{
			setSize(Board.length + 15, Board.length + (int) getJMenuBar().getPreferredSize().getHeight() + 147);
			
			Font font = new Font((new JRadioButton()).getFont().getFontName(), 
					Font.BOLD, 12);
			
			msgLog = new JTextPane();
			msgLog.setFont(font);
			msgLog.setEditable(false);
			msgLog.setBorder(BorderFactory.createLineBorder(Color.black));
			msgLog.setPreferredSize(new Dimension(getSize().width - 80, 65));
			
			turnIndicator = new TurnIndicator();
			turnIndicator.setBackground(Color.white);
			turnIndicator.setBorder(BorderFactory.createLineBorder(Color.black));
			turnIndicator.setPreferredSize(new Dimension(60, 60));
			turnIndicator.addMouseListener(this);
			
			inputField = new JTextField();
			inputField.setFont(font);
			inputField.addActionListener(this);
			inputField.setBorder(BorderFactory.createLineBorder(Color.black));
			inputField.setPreferredSize(new Dimension(getSize().width - 80, inputField.getPreferredSize().height));
			
			font = new Font((new JRadioButton()).getFont().getFontName(), 
					Font.BOLD, 10);
			
			sendButton = new JButton("Send");
			sendButton.setPreferredSize(new Dimension(60, (int) inputField.getPreferredSize().getHeight()));
			sendButton.setFont(font);
			sendButton.addActionListener(this);

			pane.add(inputField);
			pane.add(sendButton);
			pane.add(msgLog);
			pane.add(turnIndicator);
		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point location = new Point((toolkit.getScreenSize().width / 2) - ((getSize().width) / 2), 
				(toolkit.getScreenSize().height / 2) - ((getSize().height) / 2));
		
		setResizable(false);
		setLocation(location);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setIconImage(MediaLoader.loadImage("ChessIcon.png"));

		imsend = MediaLoader.loadSound("imsend.wav");
		
		setVisible(true);
	}
	
	public void appendMessage(String str)
	{
		msgLog.setText(str + msgLog.getText());
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	public void setTurn(Side turn)
	{
		if (turnIndicator != null)
			turnIndicator.setTurn(turn);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (inputField.getText().length() > 0 && board.getClass() == NetworkBoard.class)
		{
			appendMessage("127.0.0.1 : : " + inputField.getText() + "\n");
			
			if (board.getClass() == NetworkBoard.class && ChessServer.isSoundEnabled())
			{
				try
				{
					((NetworkBoard) board).out.writeObject(inputField.getText());
					
					imsend.play();
					
					toFront();
				}
				catch (Exception ex){}
			}
	
			inputField.setText("");
			
			inputField.requestFocus();
		}
	}
	
	public void windowClosing(WindowEvent e)
	{
		if (board.getClass() == NetworkBoard.class)
			((NetworkBoard) board).getServer().terminateConnection();
	}
	
	public void windowClosed(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public void mouseClicked(MouseEvent e)
	{
		if (e.getSource() == turnIndicator)
			board.blinkLastMove();
	}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public class TurnIndicator extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		Side turn;
		
		public TurnIndicator()
		{
			super();
			turn = Side.WHITE;
		}
		
		public void paint(Graphics g)
		{
			(new Space(0, 0, Board.length, turn)).draw(g, this);
		}
		
		public void setTurn(Side side)
		{
			turn = side;
			repaint();
		}
	}
}