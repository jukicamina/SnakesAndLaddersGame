package snakesandladders;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class SnakesAndLadders {
	public static void main(String[] args) {
		CustomFrame gameFrame;
		try {
			gameFrame = new CustomFrame();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}

class CustomFrame extends JFrame {
	
	public GamePanel mainPanel;
	
	public CustomFrame() throws IOException {
		
		setTitle("----------  Let's play Snakes and ladders ----------");
		mainPanel = new GamePanel();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int screenWidth = tk.getScreenSize().width;
		int screenHeight = tk.getScreenSize().height;
		setLocation(screenWidth/4, screenHeight/4 - 200);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
			int retValue = JOptionPane.showConfirmDialog(null, "Are you sure you want to finish the game?", "Exit", JOptionPane.YES_OPTION);
			if(retValue == JOptionPane.YES_OPTION) {
			
			dispose();
			System.exit(0);
			}

			}
		});
		
		add(mainPanel, BorderLayout.CENTER);
		
		setVisible(true);
		pack();
	}
	}


class GamePanel extends JPanel {
	Board gameBoard;
	Player player, computer;
	Status status;
	Dice dice ;
	
	final static int finalFiled = 100;
	
	public GamePanel() throws IOException {
		
		setLayout(new BorderLayout());
		status = new Status();
		dice = new Dice();
		player = new Player(status.score.scorePlayer1);
		computer = new Player(status.score.scoreComputer);
		gameBoard = new Board();
		
		dice.addActionListener(dice);
		gameBoard.setLayout(new GridLayout(6, 6));
		status.add(dice);
		add(gameBoard, BorderLayout.CENTER);
		add(status, BorderLayout.EAST);
	}
	
	static Map <Integer, Integer> snakes = new HashMap<Integer, Integer>();
	{
		snakes.put(6,3);
		snakes.put(13,51);
		snakes.put(42,19);
		snakes.put(45,36);
		snakes.put(67, 54);
		snakes.put(66,96);
		snakes.put(83, 62);
		snakes.put(87,90);
	}
	
	static Map <Integer, Integer> ladders = new HashMap<Integer, Integer>();
	{
		ladders.put(5,9);
		ladders.put(15,25);
		ladders.put(18,80);
		ladders.put(44,86);
		ladders.put(47,68);
		ladders.put(63,78);
		ladders.put(81,98);
		ladders.put(71,94);
		
	}
	
	
	private void PlayerValue(Player player, int diceValue) {
		
		int playerValue = player.getCurrentField() + diceValue;
		
		String message;
		if(playerValue > finalFiled) {
			playerValue = playerValue - diceValue;
			message = " (Too high)";
		}
		else if(snakes.get(playerValue) != null) {
			message = "           (Snake:" + playerValue + " -> ";
			playerValue = snakes.get(playerValue);
			message = message + playerValue + ")"; 
		}
		else if(ladders.get(playerValue) != null) {
			message = "              (Ladder:" + playerValue + " -> ";
			playerValue = ladders.get(playerValue);
			message = message + playerValue + ")"; 
		}
		else {
			message = "                 (Rolled:" + diceValue + ")";
		}
		
		player.scorePlayer.setText(playerValue + message);
		player.setCurrentField(playerValue);
		
	}
	
	private boolean finished(int value) {
		return value == finalFiled;
	}
	
	private boolean checkWin(Player player) {
		if(finished(player.getCurrentField())) {
			String message;
			if(player.equals(player)) {
				message = "Player wins, computer loses!";
			}
			else {
				message = "Computer wins, player loses!";
			}
		    JOptionPane.showMessageDialog(this, message);
		    reset();
		    return true;
		}
		return false;
	}
	
	private void reset() {
		player.setCurrentField(1);
		computer.setCurrentField(1);
		status.score.scoreComputer.setText(String.valueOf(computer.currentField)); 
		status.score.scorePlayer1.setText(String.valueOf(computer.currentField) ); 
		repaint();
	}
			
	class Dice extends JButton implements ActionListener {
		private int diceValue = 1;
		private int compDice = 1;
		Random r = new Random();

		@Override
		public void actionPerformed(ActionEvent a) {
			 diceValue = r.nextInt(6) + 1;
			
			player.setCurrentRoll(diceValue);
			PlayerValue(player, diceValue);
			gameBoard.repaint();
			if(checkWin(player)) {
				return;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			compDice = r.nextInt(6) + 1;
			PlayerValue(computer, compDice);
			if(checkWin(computer)) {
				return;
			}
			gameBoard.repaint();
		}
		
		@Override
		protected
		void paintComponent(Graphics g) {
			super.paintComponent(g);
			Image currentDice;
			try {
				currentDice = ImageIO.read(new File("src/snakesandladders/Image/d" + diceValue + ".png"));
				//g.drawImage(currentDice, 630, 630, null);
				g.drawImage(currentDice, 60, 10, 100, 100, null);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
	}
	
	class Status extends JPanel {
		public Score score = new Score();
		private TitleImage title = new TitleImage();
		
		public Status() {
			setLayout(new GridLayout(3, 1, 35, 30));
			setBorder(new EmptyBorder(50, 50, 50, 30));
			setPreferredSize(new Dimension(300, 900));
			add(title);
			add(score);

		}
		
		private class Score extends JPanel {
			private JLabel scoreLabel = new JLabel("   S  C  O  R  E        B  O  A  R  D");
			public JLabel player1 = new JLabel("                     Player:");
			public JLabel computer = new JLabel("                  Computer:");
			public JLabel scorePlayer1= new JLabel("                          1");
			public JLabel scoreComputer = new JLabel("                          1");
			
			public Score() {
				setLayout(new GridLayout(6, 1));
				add(this.scoreLabel);
				add(this.player1);
				add(this.scorePlayer1);
				add(this.computer);
				add(this.scoreComputer);
			}
			
		}
		
		private class TitleImage extends JPanel {
			private Image titleImage;
			
				public TitleImage() {
				try {
					this.titleImage = ImageIO.read(new File("src/snakesandladders/Image/Title.png"));
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Image could not be loaded");
				}
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(this.titleImage, -10, 0, this);
			}
		}
	}
	
	class Board extends JPanel {
		Image boardImage;
		final static int IMAGESTART = 55;
		
		public Board() throws IOException {
			setBorder(new EmptyBorder(40, 40, 40, 40));
			setPreferredSize(new Dimension(650, 650));
			boardImage = ImageIO.read(new File("src/snakesandladders/Image/myBoard.png"));
			player.setPlayerImage(ImageIO.read(new File("src/snakesandladders/Image/p.png")));
			computer.setPlayerImage(ImageIO.read(new File("src/snakesandladders/Image/c.png")));

		}
		
		@Override
		protected
		void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(boardImage, IMAGESTART, IMAGESTART, boardImage.getWidth(this), boardImage.getHeight(this), null);
			if(player.getCurrentField() == 1) {
				g.drawImage(player.getPlayerImage(), IMAGESTART + player.getWidth(), 55+player.getHeight(), player.getPlayerImage().getWidth(this), player.getPlayerImage().getHeight(this), null);
			}
			else {
				g.drawImage(player.getPlayerImage(), IMAGESTART + player.getWidth(), player.getHeight(), player.getPlayerImage().getWidth(this), player.getPlayerImage().getHeight(this), null);

			}
			g.drawImage(computer.getPlayerImage(), IMAGESTART + computer.getWidth(), computer.getHeight(), computer.getPlayerImage().getWidth(this), computer.getPlayerImage().getHeight(this), null);

		}
	}
	
	class Player {
		private Image playerImage;
		private int currentRoll = 0;
		int currentField = 1;
		JLabel scorePlayer;
		
		Player(JLabel l) {
			scorePlayer = l;
		}
		
		public int getHeight() {
			int decimal = checkRow(currentField);
			return 600 - decimal*60;
			
		}
		public int getWidth() {
			int decimal = checkRow(currentField);
			int n = currentField%10;
			if(n == 0) {
				n = 10;
			}
			
			if(decimal % 2 == 1) {
				
				return 600 - n*60;
			}else {
				return (n-1)*60;
			}
		}
		public Image getPlayerImage() {
			return playerImage;
		}
		public void setPlayerImage(Image playerImage) {
			this.playerImage = playerImage;
		}
		public int getCurrentField() {
			return currentField;
		}
		public void setCurrentField(int currentField) {
			this.currentField = currentField;
		}
		
		
		
		private int checkRow(int currentField) {
			if(currentField <= 10 && currentField >=1) {
				return 0;
			}
			else if(currentField >=11 && currentField<=20) {
				return 1;
			}
			else if(currentField >=21 && currentField<=30) {
				return 2;
			}
			else if(currentField >=31 && currentField<=40) {
				return 3;
			}
			else if(currentField >=41 && currentField<=50) {
				return 4;
			}
			else if(currentField >=51 && currentField<=60) {
				return 5;
			}
			else if(currentField >=61 && currentField<=70) {
				return 6;
			}
			else if(currentField >=71 && currentField<=80) {
				return 7;
			}
			else if(currentField >=81 && currentField<=90) {
				return 8;
			}
			else if(currentField >=91 && currentField<=100) {
				return 9;
			}
			
			return 0;
		}
		public int getCurrentRoll() {
			return currentRoll;
		}
		public void setCurrentRoll(int currentRoll) {
			this.currentRoll = currentRoll;
		}
		
	}
	
	
	
}
