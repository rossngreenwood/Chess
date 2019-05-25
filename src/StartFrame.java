import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartFrame extends JFrame implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;

	Thread thread;

	private boolean searchmode;
	private boolean fieldaccessed;

	private Container pane;

	private LogoCanvas logo;

	private JPanel logopanel;

	private JRadioButton localButton;
	private JRadioButton hostButton;
	private JRadioButton joinButton;

	private JTextField joinField;

	private JButton start;
	private JButton exit;

	private JLabel msgLabel;

	private boolean network;
	private boolean host;
	private String hostname = null;

	public StartFrame(ChessServer server)
	{
		super("Chess");

		searchmode = false;
		fieldaccessed = false;

		logo = new LogoCanvas();

		logopanel = new JPanel();

		FlowLayout logolayout = new FlowLayout();
		logolayout.setAlignment(FlowLayout.CENTER);
		logopanel.setBackground(Color.black);

		logopanel.add(logo);

		Font radiofont = new Font((new JRadioButton()).getFont().getFontName(),
				Font.BOLD, 16);

		localButton = new JRadioButton("Play Local Game");
		localButton.addActionListener(this);
		localButton.setFont(radiofont);
		localButton.setBackground(Color.black);
		localButton.setForeground(Color.white);

		hostButton = new JRadioButton("Host Network Game");
		hostButton.addActionListener(this);
		hostButton.setFont(radiofont);
		hostButton.setBackground(Color.black);
		hostButton.setForeground(Color.white);

		joinButton = new JRadioButton("Join Network Game");
		joinButton.addActionListener(this);
		joinButton.setFont(radiofont);
		joinButton.setBackground(Color.black);
		joinButton.setForeground(Color.white);

		ButtonGroup group = new ButtonGroup();
		group.add(localButton);
		group.add(joinButton);
		group.add(hostButton);

		joinField = new JTextField();
		joinField.setEditable(false);
		joinField.setFont(radiofont);
		joinField.addActionListener(this);
		joinField.setPreferredSize(new Dimension(208, joinField.getPreferredSize().height));
		joinField.addKeyListener(this);

		Font buttonfont = new Font((new JRadioButton()).getFont().getFontName(),
				Font.BOLD, 24);

		start = new JButton("Start");
		start.addActionListener(this);
		start.setFont(buttonfont);
		start.setPreferredSize(new Dimension(102, joinField.getPreferredSize().height));
		start.setBackground(Color.black);
		start.setForeground(Color.green);
		start.setBorderPainted(false);
		start.setContentAreaFilled(false);
		start.setEnabled(false);

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setFont(buttonfont);
		exit.setPreferredSize(new Dimension(104, joinField.getPreferredSize().height));
		exit.setBackground(Color.black);
		exit.setForeground(Color.red);
		exit.setBorderPainted(false);
		exit.setContentAreaFilled(false);

		msgLabel = new JLabel("");
		msgLabel.setFont(radiofont);
		msgLabel.setBackground(Color.black);
		msgLabel.setForeground(Color.red);
		msgLabel.setPreferredSize(new Dimension(200, 20));

		pane = getContentPane();
		pane.setBackground(Color.black);

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		pane.setLayout(layout);

		pane.add(logopanel);
		pane.add(msgLabel);
		pane.add(localButton);
		pane.add(hostButton);
		pane.add(joinButton);
		pane.add(joinField);
		pane.add(start);
		pane.add(exit);

		setSize(224, 328);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point location = new Point((toolkit.getScreenSize().width / 2) - ((getSize().width) / 2),
				(toolkit.getScreenSize().height / 2) - ((getSize().height) / 2));

		setLocation(location);

		setIconImage(MediaLoader.loadImage("ChessIcon.png"));

		setVisible(true);
	}

	public Object[] getGameOptions()
	{
		setVisible(true);

		exitSearchMode();

		hostname = null;

		while (hostname == null)
            try {
                Thread.sleep(10);
            }
            catch (Exception e) {}

		Object[] args = new Object[4];

		args[0] = network;
		args[1] = host;
		args[2] = hostname;
		args[3] = this;

		setMessageText("");

		return args;
	}

	public void setMessageText(String text)
	{
		msgLabel.setText(text);
	}

	public void setMessageText(String text, Color color)
	{
		msgLabel.setText(text);
		msgLabel.setForeground(color);
	}

	public void enterSearchMode()
	{
		if (!searchmode)
		{
			searchmode = true;

			localButton.setEnabled(false);
			hostButton.setEnabled(false);
			joinButton.setEnabled(false);
			joinField.setEnabled(false);
			start.setEnabled(false);
		}
	}

	public void exitSearchMode()
	{
		if (searchmode)
		{
			searchmode = false;

			localButton.setEnabled(true);
			hostButton.setEnabled(true);
			joinButton.setEnabled(true);
			joinField.setEnabled(true);
			start.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource().equals(exit))
		{
			System.exit(0);
		}

		if (localButton.isSelected())
		{
			joinField.setEditable(false);
			start.setEnabled(true);

			if (event.getSource() == start || event.getSource() == joinField)
			{
				network = false;
				host = false;
				hostname = "";
			}
			else
			{
				if (fieldaccessed == false)
					joinField.setText("");
			}
		}

		if (hostButton.isSelected())
		{
			joinField.setEditable(false);
			start.setEnabled(true);

			if (event.getSource() == start || event.getSource() == joinField)
			{
				network = true;
				host = true;
				hostname = "";
			}
			else
			{
				if (fieldaccessed == false)
					joinField.setText("");
			}
		}

		if (joinButton.isSelected())
		{
			joinField.setEditable(true);
			start.setEnabled(true);

			if (event.getSource() == start || event.getSource() == joinField)
			{
				if (joinField.getText().length() > 0 && fieldaccessed)// &&
						//!joinField.getText().equals("localhost") &&
						//!joinField.getText().equals("127.0.0.1"))
				{
					network = true;
					host = false;
					hostname = joinField.getText();
				}
				/*
				if (joinField.getText().equals("localhost") ||
						joinField.getText().equals("127.0.0.1"))
				{
					setMessageText(" Invalid name / IP address.");
				}*/
			}
			else
			{
				joinField.requestFocusInWindow();

				if (fieldaccessed == false)
					joinField.setText("Enter name or IP address");

				joinField.selectAll();
			}
		}
	}

	public void keyPressed(KeyEvent e)
	{
		fieldaccessed = true;
	}

	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}

	public class LogoCanvas extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public LogoCanvas()
		{
			super();

			setPreferredSize(new Dimension(200, 80));
		}

		public void paint(Graphics g)
		{
			g.drawImage(MediaLoader.loadImage("ChessLogo.png"), 0, 0, this);
		}
	}
}
