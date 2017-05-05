package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 982728831872124279L;
	myPanel p;
	GamePanel gp;
	Game game;
	SharedData data;
	
	public GUI(Game g,SharedData data){
		this.setSize(1024,768);
		this.setVisible(true);
		p = new myPanel(this);
		this.addKeyListener(p);
		this.data = data;
		add(p);
		this.game=g;		
	}
	
	public void startGame(){
		this.remove(p);
		gp = new GamePanel(this);
		this.add(gp);
		this.addKeyListener(gp);
		this.addMouseListener(gp);
		repaint();
		game.startGame();
	}
	
	class GamePanel extends JPanel implements KeyListener, MouseListener{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2407442322401146721L;
		private GUI parent;
		private BufferedImage img_bg;
		
		public GamePanel(GUI parent){
			this.setSize(1024,768);
			this.setVisible(true);
			this.parent = parent;
			
			try {
				img_bg = ImageIO.read(new File("bin/client/bg.png"));
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLUE);
			g.drawRect((1024-700)-50, 34, 700, 700);
			g.drawImage(img_bg, 1024-700-50, 34, 700,700,this);
			
			data.drawOtherPlayers(g);
			data.drawBullets(g);
			
			g.setColor(Color.RED);
			g.drawRect(0, 0, 200, 20);
			g.fillRect(0, 0, data.getHealth()*2, 20);
			
			data.drawChatMessages(g);
			/*String s = data.getChatMessage();
			if(!s.equals("")){
				g.setFont(f);
				g.setColor(Color.RED);
				g.drawChars(s.toCharArray(), 0, s.length(), 0,100);
			}*/
			g.dispose();
			repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_UP){
				game.pressedUp();
			}
			if(e.getKeyCode()==KeyEvent.VK_DOWN){
				game.pressedDown();
			}
			if(e.getKeyCode()==KeyEvent.VK_LEFT){
				game.pressedLeft();
			}
			if(e.getKeyCode()==KeyEvent.VK_RIGHT){
				game.pressedRight();
			}
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
				game.quit();
				parent.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_UP){
				game.releasedUp();
			}
			if(e.getKeyCode()==KeyEvent.VK_DOWN){
				game.releasedDown();
			}
			if(e.getKeyCode()==KeyEvent.VK_LEFT){
				game.releasedLeft();
			}
			if(e.getKeyCode()==KeyEvent.VK_RIGHT){
				game.releasedRight();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			game.shoot(e.getX()-(1024-700)+50,e.getY()-34);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	class myPanel extends JPanel implements KeyListener{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4456281891640829217L;
		private boolean choice = false;
		private GUI parent;
		char[] s1 = new String("JOIN").toCharArray();
		char[] s2 = new String("QUIT").toCharArray();
		BufferedImage img;
		public myPanel(GUI parent){
			this.setSize(1024,768);
			this.setVisible(true);
			this.parent = parent;
			
			try {
				img = ImageIO.read(new File("bin/client/markZmanship.png"));
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.drawImage(img, 0, 0, 1024, 768, this);
			
			
			
			//Font f = new Font("Jokerman", Font.PLAIN, 35);
			Font f = new Font("HelveticaNeue LT 23 UltLtEx", Font.PLAIN, 35);
			
			g.setFont(f);
			g.setColor(Color.RED);
			g.drawChars(s1, 0, 4, 350-g.getFontMetrics().stringWidth("JOIN")/2, 650+35/2);
			g.drawChars(s2, 0, 4, 1024-200-300+150-g.getFontMetrics().stringWidth("QUIT")/2, 650+35/2);
			if(!choice){
				g.drawRect(200, 600, 300, 100);
			} else {
				g.drawRect(1024-200-300, 600, 300, 100);
			}
			repaint();
			g.dispose();
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				choice = true;
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				choice = false;	
			if(e.getKeyCode() == KeyEvent.VK_ENTER && choice)
				parent.dispose();
			if(e.getKeyCode() == KeyEvent.VK_ENTER && !choice){
				parent.startGame();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}
