package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputThread extends Thread{

	private BufferedReader is;
	private SharedData data;
	private boolean done = false;
	
	public InputThread(InputStream inputStream,SharedData data) {
		is = new BufferedReader(new InputStreamReader(inputStream));
		this.data = data;
	}

	public void close(){
		done = true;
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){		
		try {
			String s;
			while(!done){
				s = is.readLine();
				if(s!=null&&!s.equals("")){
					if(s.charAt(0)=='C'){
						data.putChatMessage(s.substring(2,s.length()));
					}
					if(s.charAt(0)=='J'){
						data.addPlayer((byte)Character.getNumericValue(s.charAt(s.length()-1)));
					}
					if(s.charAt(0)>='1'&&s.charAt(0)<='4'){
						int commaIndex = s.indexOf(',');
						int x = Integer.parseInt(s.substring(3,commaIndex));
						int y = Integer.parseInt(s.substring(commaIndex+3,s.length()));
						data.updatePlayerPositions((byte)Character.getNumericValue(s.charAt(0)),x,y);
					}
					
					// Reset game
					if(s.charAt(0)=='R'){
						data.resetGame();
					}
					if(s.charAt(0)=='B'){
						if(s.charAt(1)=='A'){
							String[] subs = s.split(",");
							int x = Integer.parseInt(subs[0].substring(3, subs[0].length()));
							int y = Integer.parseInt(subs[1]);
							byte id = (byte)Integer.parseInt(subs[2]);
							byte bId = (byte)Integer.parseInt(subs[3]);
							data.addBulletFromServer(x,y,id,bId);
						} else if(s.charAt(1)=='R'){
							String[] subs = s.split(",");
							byte id = (byte)Integer.parseInt(subs[0].substring(3,subs[0].length()));
							byte bId = (byte)Integer.parseInt(subs[1]);
							data.removeBullet(id,bId);
						} else {
							String[] subs = s.split(",");
							int x = Integer.parseInt(subs[0].substring(2, subs[0].length()));
							int y = Integer.parseInt(subs[1]);
							byte id = (byte)Integer.parseInt(subs[2]);
							byte bId = (byte)Integer.parseInt(subs[3]);
							data.updateBulletPosition(x,y,id,bId);
						}
					}
					if(s.charAt(0)=='H'){
						data.takeDamage((byte) 10);
					}
					if(s.charAt(0)=='D'){
						data.killPlayer((byte)Integer.parseInt(s.substring(2,s.length())));
					}
					// Other player quit game
					if(s.charAt(0)=='Z'){
						data.killPlayer((byte)Integer.parseInt(s.substring(2,s.length())));
					}
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			data.putChatMessage("Connection problems!");
			System.out.println("Connection problems!");
		}
		
	}
}
