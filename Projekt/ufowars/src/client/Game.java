package client;

import java.io.IOException;
import java.net.Socket;

public class Game extends Thread {
	
	public static byte BULLET_COUNTER = 0;
	
	private float x,y;
	private boolean keyUp = false;
	private boolean keyDown = false;
	private boolean keyLeft = false;
	private boolean keyRight = false;
	private boolean done = false;

	private SharedData data;
	private Mailbox m;
	private InputThread it;
	private OutputThread ot;
	private Socket s;
	
	private Audio at;
	
	public Game(SharedData data,Mailbox m,Socket s,InputThread it,OutputThread ot) {
		this.data = data;
		this.m = m;
		this.it = it;
		this.ot = ot;
		this.s = s;
	}
	
	public void startGame(){
		x = 700/2;
		y = 700/2;
		ot.start();
		
		at = new Audio();		
		at.startBackgroundMusic();
	}
	
	public void shoot(int bx,int by){
		if(!data.isDead()){
			double a = Math.atan((y-(float)by)/(x-(float)bx));
			if(bx<x)
				a += Math.PI;
			Bullet b = new Bullet(x,y,(float)a);
			data.addBullet(b);
			//at.playSound("bin/client/shoot.wav",false);
			//at.playShootSound();
		}
	}
	
	public void win(){
		at.startWinMusic();
	}
	
	public void otherShoot(){
		//at.playSound("bin/client/shoot.wav", false);
		at.playShootSound();
	}
	
	public void chat(String msg){
		m.putLine("C:"+msg);
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}

	public void pressedDown() {
		keyDown = true;
	}
	
	public void releasedDown(){
		keyDown = false;
	}
	
	public void pressedLeft() {
		keyLeft = true;
	}
	
	public void releasedLeft(){
		keyLeft = false;
	}
	
	public void pressedRight() {
		keyRight = true;
	}
	
	public void releasedRight(){
		keyRight = false;
	}
	
	public void pressedUp() {
		keyUp = true;
	}
	
	public void releasedUp(){
		keyUp = false;
	}
	
	public void run(){
		while(!done){
			try {
				Thread.sleep(1000/30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!data.isDead()){
			x = data.getX();
			y = data.getY();
			if(keyUp){
				y -= 5;
				if(y<10)
					y=10;
			}
			if(keyDown){
				y += 5;
				if(y>=700-10)
					y=699-10;
			}
			if(keyLeft){
				x -= 5;
				if(x<32)
					x=32;
			}
			if(keyRight){
				x += 5;
				if(x>=700-32)
					x=699-32;
			}
			m.putLine("X:"+(int)x+",Y:"+(int)y);
			}
			data.updateBullets();
		}
	}
	
	class Bullet{
		float x,y,a;
		byte id;
		
		public Bullet(float x,float y,float a){
			this.x = x;
			this.y = y;
			this.a = a;
			BULLET_COUNTER = (byte) ((BULLET_COUNTER + 1) % 256);
			id = BULLET_COUNTER;
		}
		
		public boolean update(){
			x += 5*Math.cos(a);
			y += 5*Math.sin(a);
			
			if(x<0||x>700||y<0||y>700){
				return false;
			}
			return true;
		}
		
		
	}

	public void quit() {
		done = true;
		it.close();
		m.putLine("Q");
		at.close();
		//try {
		//	s.close();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
	}
	
	public void quitFinal(){
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Shutting down socket");
	}

	public void pressedEnter() {
		data.pressedEnter();
	}
	
	public void reset(){
		at.startBackgroundMusic();
	}
}
