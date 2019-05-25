import javax.swing.*;

import java.awt.event.*;

public class ChessMenu extends JMenuBar implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	Board board;
	
	JMenu file, game, options;
	
	JMenuItem close;
	JMenuItem exit;
	
	JMenuItem draw;
	JMenuItem forfeit;
	
	JMenuItem toggleSound;
	
	public ChessMenu(Board board)
	{
		super();
		
		this.board = board;
		
		file = new JMenu("File");
		
		close = new JMenuItem("Close");
		close.addActionListener(this);
		file.add(close);
		file.addSeparator();
		
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		
		file.add(exit);
		
		add(file);
		
		if (board.getClass() == NetworkBoard.class)
		{
			game = new JMenu("Game");
			
			draw = new JMenuItem("Draw");
			draw.addActionListener(this);
			
			forfeit = new JMenuItem("Forfiet");
			forfeit.addActionListener(this);
			
			game.add(draw);
			game.add(forfeit);
			
			options = new JMenu("Options");
			
			toggleSound = new JCheckBoxMenuItem("Sound");
			toggleSound.setSelected(true);
			toggleSound.addActionListener(this);
			
			options.add(toggleSound);
			
			add(game);
			add(options);
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source.equals(close))
		{
			((JFrame) board.getTopLevelAncestor()).dispose();
			
			if (board.getClass() == NetworkBoard.class)
				((NetworkBoard) board).getServer().terminateConnection();
		}
		
		if (source.equals(exit))
		{
			System.exit(0);
		}

		if (source.equals(draw))
		{
			int ans = JOptionPane.showConfirmDialog(getTopLevelAncestor(), "Are you sure you would like to request a draw?", "Confirm Draw", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			if (ans == JOptionPane.YES_OPTION)
			{
				ChessServer server = ((NetworkBoard) ((GameFrame) getTopLevelAncestor()).getBoard()).getServer();
				
				try
				{
					server.getObjectOutputStream().writeObject(new Integer(0));
					server.getObjectOutputStream().flush();
					
					((GameFrame) getTopLevelAncestor()).appendMessage("Draw request sent.\n");
				}
				catch (Exception ex){}
			}
		}
		
		if (source.equals(forfeit))
		{
			int ans = JOptionPane.showConfirmDialog(getTopLevelAncestor(), "Are you sure you would like to forfeit the game?", "Confirm Forfeit", JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.YES_OPTION)
			{
				ChessServer server = ((NetworkBoard) ((GameFrame) getTopLevelAncestor()).getBoard()).getServer();
				
				try
				{
					server.getObjectOutputStream().writeObject(new Integer(3));
					server.getObjectOutputStream().flush();
					
					board.setForfeit();
					((GameFrame) getTopLevelAncestor()).appendMessage("You forfeit the game.\n");
				}
				catch (Exception ex){}
				
				draw.setEnabled(false);
				forfeit.setEnabled(false);
			}
		}
		
		if (source.equals(toggleSound))
		{
			if (ChessServer.isSoundEnabled())
				ChessServer.setSoundEnabled(false);
			else
				ChessServer.setSoundEnabled(true);
		}
	}
}
