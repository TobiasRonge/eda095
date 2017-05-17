package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import client.Game.Bullet;

public class SharedData {
	
	private static final String WON_STRING ="YOU HAVE WON!";
	private static final String LOST_STRING = "YOU HAVE LOST";
	private static final String NOTREADY_STRING = "Press ENTER to start new game";
	private static final String READY_STRING = "Waiting for other players...";
	
	private Font f = new Font("Verdana", Font.PLAIN, 10);
	private Font f_outcome = new Font("Courier",Font.BOLD,26);
	private Font f_outcomeSmall = new Font("Courier",Font.PLAIN,14);
	
	private ArrayList<Bullet> bullets;
	
	private HashMap<Byte,Float> other_x;
	private HashMap<Byte,Float> other_y;
	private float oldX,oldY;
	private HashMap<Integer,Float> bullet_x;
	private HashMap<Integer,Float> bullet_y;
	
	private BufferedImage img_ufo1, img_ufo2, img_ufo3, img_ufo4;
	
	private Stack<String> chatMessages;
	
	private byte ownId;
	private Mailbox m;
	private Game g;
	
	private boolean dead = false;
	
	private boolean won = false;
	private boolean lost = false;
	
	private boolean ready = false;
	
	private byte health = 100;
	
	public SharedData(byte id,Mailbox m){
		bullets = new ArrayList<Bullet>();
		other_x = new HashMap<Byte,Float>();
		other_y = new HashMap<Byte,Float>();
		bullet_x = new HashMap<Integer,Float>();
		bullet_y = new HashMap<Integer,Float>();
		other_x.put(id, (float)350);
		other_y.put(id, (float)350);
		ownId = id;
		chatMessages = new Stack<String>();
		chatMessages.push("Press ESCAPE to quit");
		this.m = m;
		try {
			img_ufo1 = ImageIO.read(getClass().getResource("ufo1.png"));
			img_ufo2 = ImageIO.read(getClass().getResource("ufo2.png"));
			img_ufo3 = ImageIO.read(getClass().getResource("ufo3.png"));
			img_ufo4 = ImageIO.read(getClass().getResource("ufo4.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float getX(){
		return other_x.get(ownId);
	}
	
	public float getY(){
		return other_y.get(ownId);
	}
	
	
	public synchronized void drawOtherPlayers(Graphics g,JPanel obs){
		Iterator<Byte> itr = other_x.keySet().iterator();
		while(itr.hasNext()){
			byte id = itr.next();
			float x = other_x.get(id);
			float y = other_y.get(id);
			switch (id){
			case 1:
				g.setColor(Color.BLUE);
				g.drawImage(img_ufo1,1024-700-50+(int)x-32,34+(int)y-10,64,20,obs);
				break;
			case 2:
				g.setColor(Color.RED);
				g.drawImage(img_ufo2,1024-700-50+(int)x-32,34+(int)y-10,64,20,obs);
				break;
			case 3:
				g.setColor(Color.GREEN);
				g.drawImage(img_ufo3,1024-700-50+(int)x-32,34+(int)y-10,64,20,obs);
				break;
			case 4:
				g.setColor(Color.YELLOW);
				g.drawImage(img_ufo4,1024-700-50+(int)x-32,34+(int)y-10,64,20,obs);
				break;
			}
			//g.drawRect((1024-700)-50+(int)x-5, 34+(int)y-5, 10, 10);
		}
	}
	
	public synchronized void drawBullets(Graphics g){
		Iterator<Integer> itr = bullet_x.keySet().iterator();
		while(itr.hasNext()){
			int b = itr.next();
			
			if(b/256>=3){
				g.setColor(Color.YELLOW);
			} else if(b/256>=2){
				g.setColor(Color.GREEN);
			} else if(b/256>=1) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLUE);
			}
			float x = bullet_x.get(b);
			float y = bullet_y.get(b);
			g.drawRect((1024-700)-50+(int)x-2, 34+(int)y-2, 4, 4);
		}
	}
	
	public synchronized void drawOutcome(Graphics g){
		g.setFont(f_outcome);
		if(won){
			g.setColor(new Color(0,255,0));
			g.drawChars(WON_STRING.toCharArray(), 0, WON_STRING.length(),512-g.getFontMetrics().stringWidth(WON_STRING)/2, g.getFontMetrics().getHeight()+768/2);
		} else if(lost){
			g.setColor(new Color(255,0,0));
			g.drawChars(LOST_STRING.toCharArray(), 0, LOST_STRING.length(),512-g.getFontMetrics().stringWidth(LOST_STRING)/2, g.getFontMetrics().getHeight()+768/2);
		}
		if(won||lost){
			g.setFont(f_outcomeSmall);
			g.setColor(Color.WHITE);
			if(!ready){
				g.drawChars(NOTREADY_STRING.toCharArray(), 0, NOTREADY_STRING.length(), 512-g.getFontMetrics().stringWidth(NOTREADY_STRING)/2, g.getFontMetrics().getHeight()+768/2+32);
			} else {
				g.drawChars(READY_STRING.toCharArray(), 0, READY_STRING.length(), 512-g.getFontMetrics().stringWidth(READY_STRING)/2, g.getFontMetrics().getHeight()+768/2+32);
			}
		}
	}
	
	public synchronized void takeDamage(byte damage){
		health -= damage;
		if(health <= 0){
			health = 0;
			m.putLine("D:"+ownId);
			dead = true;
			oldX = other_x.get(ownId);
			oldY = other_y.get(ownId);
		}
	}
	
	public synchronized void removeBullet(byte id,byte bId){
		bullet_x.remove((id-1)*256+bId);
		bullet_y.remove((id-1)*256+bId);
	}
	
	public synchronized void addBullet(Bullet b){
		if(!bullet_x.containsKey(b.id+256*(ownId-1))){
			bullets.add(b);
			bullet_x.put(b.id+(ownId-1)*256,b.x);
			bullet_y.put(b.id+(ownId-1)*256, b.y);
			m.putLine("BA:"+(int)(b.x+0.5)+","+(int)(b.y+0.5)+","+ownId+","+b.id);
		}
	}
	
	// Updates the clients own bullets and sends information about them to server
	public synchronized void updateBullets(){
		ArrayList<Bullet> removes = new ArrayList<Bullet>();
		
		for(Bullet b:bullets){
			if(!b.update()){
				removes.add(b);
			}
			m.putLine("B:"+(int)(b.x+0.5)+","+(int)(b.y+0.5)+","+ownId+","+b.id);
		}
		for(Bullet b:removes){
			bullets.remove(b);
			bullet_x.remove((ownId-1)*256+b.id);
			bullet_y.remove((ownId-1)*256+b.id);
			//m.putLine("BR:"+ownId+","+b.id);
		}
		
	}

	public synchronized void putChatMessage(String s) {
		chatMessages.push(s);
	}
	

	public synchronized void drawChatMessages(Graphics g){
		g.setFont(f);
		g.setColor(Color.RED);
		Iterator<String> itr = chatMessages.iterator();
		int i=0;
		while(itr.hasNext()){
			String s = itr.next();
			if(chatMessages.size()-i<20)
				g.drawChars(s.toCharArray(), 0, s.length(), 15,100+(chatMessages.size()-i)*20);
			i++;
		}
	}

	public synchronized void addPlayer(byte n) {
		if(!other_x.containsKey(n)){
			other_x.put(n, (float)350.0);
			other_y.put(n, (float)350.0);
		}
	}
	
	public synchronized void updatePlayerPositions(byte id,int x,int y){
		other_x.put(id, (float)x);
		other_y.put(id, (float)y);
	}

	public synchronized void updateBulletPosition(int x, int y, byte id, byte bId) {
		if(x<=0||x>=700||y<=0||y>=700){
			bullet_x.remove((id-1)*256+bId);
			bullet_y.remove((id-1)*256+bId);
		} else {
			bullet_x.put((id-1)*256+bId,(float)x);
			bullet_y.put((id-1)*256+bId,(float)y);
		}
	}

	public synchronized void addBulletFromServer(int x, int y, byte id, byte bId) {
		bullet_x.put((id-1)*256+bId,(float)x);
		bullet_y.put((id-1)*256+bId,(float)y);
		g.otherShoot();
	}

	public synchronized byte getHealth() {
		return health;
	}

	public synchronized boolean isDead() {
		return dead;
	}

	public synchronized void killPlayer(byte id) {
		other_x.remove(id);
		other_y.remove(id);
		putChatMessage("Player "+id+" died");
		if(other_x.size()==1&&!lost&&!won){
			putChatMessage("Player "+other_x.keySet().iterator().next()+" has won the game!");
			if(other_x.keySet().iterator().next()==ownId){
				won = true;
				g.win();
			} else {
				lost = true;
			}
		}
	}
	
	public synchronized void removePlayer(byte id){
			other_x.remove(id);
			other_y.remove(id);
			putChatMessage("Player "+id+" died");
			if(other_x.size()==1&&!lost&&!won){
				putChatMessage("Player "+other_x.keySet().iterator().next()+" has won the game!");
				if(other_x.keySet().iterator().next()==ownId){
					won = true;
					g.win();
				} else {
					lost = true;
				}
			}
		}

	public void registerGame(Game game) {
		g = game;
	}

	public void pressedEnter() {
		if((won||lost)&&!ready){
			ready = true;
			m.putLine("R");
		}
	}

	public void resetGame() {
		ready = false;
		won = false;
		lost = false;
		bullet_x.clear();
		bullet_y.clear();
		bullets.clear();
		health = 100;
		if(dead){
			other_x.put(ownId, oldX);
			other_y.put(ownId, oldY);
		}
		dead = false;
		g.reset();
	}
}
