import java.net.*;
import java.io.*;
import java.applet.AudioClip;
import java.awt.event.*;
import java.awt.Color;

import javax.swing.JOptionPane;

public class ChessServer
{
	private ServerSocket server = null;
	private Socket socket = null;

	private StartFrame frame;

	private Object[] vars;

	private boolean network;
	private boolean host;
	private String hostname;

	private GameFrame game = null;

	private ObjectOutputStream out;
	private ObjectInputStream in;

	private static boolean soundEnabled = true;

	private AudioClip imrcv;

	public static void main(String[] args)
	{
		new ChessServer();
	}

	public ChessServer()
	{
		soundEnabled = true;

		imrcv = MediaLoader.loadSound("imrcv.wav");

		frame = new StartFrame(this);
		
		while (true)
		{
			frame.setMessageText("");

			initializeGameOptions();

			boolean retry = true;
			while (retry)
			{
				retry = false;

				if (!network)
				{
					game = new GameFrame(new Board());
					frame.setVisible(false);
					while (game.isVisible());
				}
				else
				{
					if (!host)
					{
						try
						{
							if (hostname != "")
								socket = new Socket(InetAddress.getByName(hostname), 9027);
							else
								retry = true;
						}
						catch (UnknownHostException e)
						{
							frame.setMessageText(" Host not found.", Color.red);

							retry = true;
						}
						catch (IOException e)
						{
							frame.setMessageText(" Connection failed.", Color.red);

							retry = true;
						}
					}
					else
					{
						try
						{
							server = new ServerSocket(9027);
							server.setSoTimeout(60000);

							frame.enterSearchMode();
							frame.setMessageText(" Waiting for players...", Color.green);

							socket = server.accept();
						}
						catch (SocketTimeoutException ex)
						{
							frame.setMessageText(" No players found.", Color.red);

							try
							{
								server.close();
								server = null;
							}
							catch (Exception exe){}

							retry = true;
						}
						catch (IOException ex)
						{
							frame.setMessageText(" Connection failed.", Color.red);

							try
							{
								server.close();
								server = null;
							}
							catch (Exception exe){}

							retry = true;
						}
					}
				}

				if (retry)
				{
					initializeGameOptions();
				}
				else
				{
					frame.setVisible(false);
				}
			}

			if (socket != null)
			{
				try
				{
					out = new ObjectOutputStream(socket.getOutputStream());
					out.flush();
					in = new ObjectInputStream(socket.getInputStream());

					game = new GameFrame(new NetworkBoard(this));

					if (server != null)
						((NetworkBoard) game.getBoard()).setSide(Side.WHITE);
					else
						((NetworkBoard) game.getBoard()).setSide(Side.BLACK);

					Object objIn;
					while ((objIn = in.readObject()) != null)
					{
						if (objIn.getClass() == MouseEvent.class)
						{
							MouseEvent e = (MouseEvent) objIn;
							String param = e.paramString().split(",")[0];

							if (param.equals("MOUSE_PRESSED"))
								((NetworkBoard) game.getBoard()).handleMousePressed(e);

							if (param.equals("MOUSE_DRAGGED"))
								((NetworkBoard) game.getBoard()).handleMouseDragged(e);

							if (param.equals("MOUSE_RELEASED"))
								((NetworkBoard) game.getBoard()).handleMouseReleased(e);
						}
						else if (objIn.getClass() == String.class)
						{
							String id = socket.getRemoteSocketAddress().toString().split(":")[0];

							id = id.split("/")[1];

							game.appendMessage(id + " : : "+ ((String) objIn) + "\n");

							if (ChessServer.isSoundEnabled())
								imrcv.play();

							if (!game.hasFocus())
								game.toFront();
						}
						else if (objIn.getClass() == Integer.class)
						{
							if (((Integer) objIn).equals(0))
							{
								int ans = JOptionPane.showConfirmDialog(game, "Opponent has requested a draw.\nWould you like to agree to a draw?", "Confirm Draw", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

								if (ans == JOptionPane.YES_OPTION)
								{
									out.writeObject(new Integer(1));
									out.flush();

									game.getBoard().setDraw();
									game.appendMessage("Game ended in a draw.\n");
								}
								else
								{
									out.writeObject(new Integer(2));
									out.flush();
								}
							}

							if (((Integer) objIn).equals(1))
							{
								game.getBoard().setDraw();
								game.appendMessage("Game ended in a draw.\n");

								((ChessMenu) game.getJMenuBar()).draw.setEnabled(false);
								((ChessMenu) game.getJMenuBar()).forfeit.setEnabled(false);

								if (!game.hasFocus())
									game.toFront();
							}

							if (((Integer) objIn).equals(2))
							{
								game.appendMessage("Draw request denied.\n");
							}

							if (((Integer) objIn).equals(3))
							{
								game.getBoard().setForfeit();
								game.appendMessage("Opponent has forfeited the game.\n");

								((ChessMenu) game.getJMenuBar()).draw.setEnabled(false);
								((ChessMenu) game.getJMenuBar()).forfeit.setEnabled(false);

								if (!game.hasFocus())
									game.toFront();
							}
						}
						else if (objIn.getClass() == Object.class)
						{
							if (!game.getBoard().checkmate())
								handleDisconnect();

							while (game.isVisible());

							break;
						}
					}
				}
				catch (Exception e)
				{
					if (!game.getBoard().checkmate())
						handleDisconnect();

					while (game.isVisible());
				}
			}
		}
	}

	private void initializeGameOptions()
	{
		vars = frame.getGameOptions();

		network = (Boolean) vars[0];
		host = (Boolean) vars[1];
		hostname = (String) vars[2];
	}

	public void handleDisconnect()
	{
		if (game != null)
			game.getBoard().setDisable();

		game.appendMessage("Opponent has disconnected.\n");

		((ChessMenu) game.getJMenuBar()).game.setEnabled(false);

		game.inputField.setEnabled(false);
		game.sendButton.setEnabled(false);

		if (socket != null)
		{
			try
			{
				socket.close();
				socket = null;
			} catch (Exception e){}
		}

		if (server != null)
		{
			try
			{
				server.close();
				server = null;
			} catch (Exception e){}
		}

		if (in != null)
		{
			try
			{
				in.close();
				in = null;
			} catch (Exception e){}
		}

		if (out != null)
		{
			try
			{
				out.close();
				out = null;
			} catch (Exception e){}
		}
	}

	public void terminateConnection()
	{
		if (network)
			try
			{
				out.writeObject(new Object());
			}
			catch (Exception e){}
	}

	public ObjectOutputStream getObjectOutputStream()
	{
		return out;
	}

	public static void setSoundEnabled(boolean se)
	{
		soundEnabled = se;
	}

	public static boolean isSoundEnabled()
	{
		return soundEnabled;
	}

}
