package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ServerData {
	
	private HashMap<Byte,Integer> player_x;
	private HashMap<Byte,Integer> player_y;
	private HashMap<Integer,Integer> bullet_x;
	private HashMap<Integer,Integer> bullet_y;
	private ArrayList<Integer> bullets;
	private HashMap<Byte,Boolean> ready;
	private ParticipantMonitor pm;
	private Mailbox m;
	
	public ServerData(ParticipantMonitor pm,Mailbox m){
		player_x = new HashMap<Byte,Integer>();
		player_y = new HashMap<Byte,Integer>();
		bullet_x = new HashMap<Integer,Integer>();
		bullet_y = new HashMap<Integer,Integer>();
		bullets = new ArrayList<Integer>();
		ready = new HashMap<Byte,Boolean>();
		this.pm = pm;
		this.m = m;
	}
	
	public synchronized void ready(byte id){
		ready.put(id, true);
		if(ready.size()==pm.getNumberOfPlayers()){
			bullet_x.clear();
			bullet_y.clear();
			bullets.clear();
			ready.clear();
			m.writeString("R");
			
		}
	}
	
	public synchronized void killPlayer(byte id){
		player_x.remove(id);
		player_y.remove(id);
	}
	
	public synchronized void sendParticipantInfo(Participant p){
			for(byte id:player_x.keySet()){
				p.sendMessage("J:"+id);
			}
	}
	
	public synchronized void updatePlayerPosition(byte id,int x,int y){
		player_x.put(id, x);
		player_y.put(id, y);
	}
	
	public synchronized void addBullet(byte id,byte bId,int x,int y){
		bullets.add((id-1)*256+bId);
		bullet_x.put((id-1)*256+bId, x);
		bullet_y.put((id-1)*256+bId, y);
	}
	
	public synchronized boolean updateBulletPosition(byte id,byte bId,int x,int y){
		if(bullets.contains((id-1)*256+bId)){
			bullet_x.put((id-1)*256+bId, x);
			bullet_y.put((id-1)*256+bId, y);
			return true;
		}	
		return false;
	}

	public synchronized void checkCollision() {
		Iterator<Byte> itr = player_x.keySet().iterator();
		while(itr.hasNext()){
			byte id = itr.next();
			int x = player_x.get(id);
			int y = player_y.get(id);
			
			Iterator<Integer> itr_b = bullet_x.keySet().iterator();
			while(itr_b.hasNext()){
				int i = itr_b.next();
				byte p_id = -1;
				if (i/256>=3)
					p_id = 4;
				else if (i/256 >= 2)
					p_id = 3;
				else if (i/256 >= 1)
					p_id = 2;
				else
					p_id = 1;
				if(p_id != id){
					if(collision(x,y,64,20,bullet_x.get(i),bullet_y.get(i),4,4)){
						System.out.println("Collision between bullet from "+p_id+" and "+id+" i="+i);
						pm.sendMessageTo(id, "H");
						bullets.remove((Integer)i);
						itr_b.remove();
						bullet_x.remove(i);
						bullet_y.remove(i);
						m.writeString("BR:"+p_id+","+i%256);						
					}
				}
			}
		}
	}
	
	private boolean collision(int x1,int y1,int w1,int h1,int x2,int y2,int w2,int h2){
		if((y1-h1/2>y2-h2/2&&y1-h1/2<y2+h2/2&&x1-w1/2>x2-w2/2&&x1-w1/2<x2+w2/2) || // Top left
				(y1+h1/2>y2-h2/2&&y1+h1/2<y2+h2/2&&x1-w1/2>x2-w2/2&&x1-w1/2<x2+w2/2) || // Bottom left
				(y1-h1/2>y2-h2/2&&y1-h1/2<y2+h2/2&&x1+w1/2>x2-w2/2&&x1+w1/2<x2+w2/2) || // Top right
				(y1+h1/2>y2-h2/2&&y1+h1/2<y2+h2/2&&x1+w1/2>x2-w2/2&&x1+w1/2<x2+w2/2) ||
				(y2-h2/2>y1-h1/2&&y2-h2/2<y1+h1/2&&x2-w2/2>x1-w1/2&&x2-w2/2<x1+w1/2) || // Top left
				(y2+h2/2>y1-h1/2&&y2+h2/2<y1+h1/2&&x2-w2/2>x1-w1/2&&x2-w2/2<x1+w1/2) || // Bottom left
				(y2-h2/2>y1-h1/2&&y2-h2/2<y1+h1/2&&x2+w2/2>x1-w1/2&&x2+w2/2<x1+w1/2) || // Top right
				(y2+h2/2>y1-h1/2&&y2+h2/2<y1+h1/2&&x2+w2/2>x1-w1/2&&x2+w2/2<x1+w1/2)
				) // Bottom right
			return true;
		return false;
	}
}
