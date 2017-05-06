package server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Server extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4052386212172340394L;
	byte i = 1;
	private ParticipantMonitor pm;
	private BroadcastThread bt;
	
	public Server(){
		try {
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(25000);
			System.out.println("Server running at port 25000");
			setSize(300,150);
			setVisible(true);
			ServerPanel panel = new ServerPanel(this);
			add(panel);
			panel.setSize(300,150);
			repaint();
			addKeyListener(panel);
			
			
			Mailbox m = new Mailbox();
			pm = new ParticipantMonitor();
			bt = new BroadcastThread(m,pm);
			bt.start();
			
			ServerData data = new ServerData(pm,m);
			GameLogic gl = new GameLogic(data);
			gl.start();
			
			while(i<=4){
				Socket s = ss.accept();
				String name = "Player " + i;
				s.getOutputStream().write((int)i);
				s.getOutputStream().flush();
				System.out.println(name+" has joined the game.");
				m.writeString("C: "+name+" has joined the game.");
				m.writeString("J: "+i);
				Participant p = new Participant(i,name,s,m,pm,data);
				data.sendParticipantInfo(p);
				pm.addParticipant(p);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Server();
	}
	
	public void close(){
		pm.close();
		bt.close();
	}
	
	class ServerPanel extends JPanel implements KeyListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2263681472474019767L;
		private JFrame parent;
		public ServerPanel(JFrame parent){
			repaint();
			this.parent = parent;
		}
		
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			String s = "Server running. Press ESCAPE to quit.";
			g.drawChars(s.toCharArray(), 0, s.length(), 10, 75);
			g.dispose();
			repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
				parent.dispose();
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
