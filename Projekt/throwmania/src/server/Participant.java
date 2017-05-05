package server;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Participant extends Thread{
	private String name;
	private byte id;
	private Socket s;
	private Mailbox m;
	private ParticipantMonitor pm;
	private ServerData data;
	
	public Participant(byte id,String name,Socket s,Mailbox m,ParticipantMonitor pm,ServerData data){
		super();
		this.name = name;
		this.s = s;
		this.m = m;
		this.pm = pm;
		this.id = id;
		this.data = data;
	}

	@Override
	public void run() {
		super.run();

		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
			boolean done = false;
			while(!done){
				
				String message = is.readLine();
				if(message.charAt(0)=='Q'){
					pm.removeParticipant(this);	
					m.writeString("C: "+name+" has left the game.");			
					done = true;
					s.close();
				}
				
				if(message.charAt(0)=='X'){
					if(message.charAt(1)!='A'){
						String[] subs = message.split(",");
						data.updatePlayerPosition(id, Integer.parseInt(subs[0].substring(2, subs[0].length())), Integer.parseInt(subs[1].substring(2, subs[1].length())));
						m.writeString(id+message);
					}
				}
				if(message.charAt(0)=='B'){
					if(message.charAt(1)!='A'){
						String[] subs = message.split(",");
						if(data.updateBulletPosition(id,(byte)Integer.parseInt(subs[3]),Integer.parseInt(subs[0].substring(2, subs[0].length())), Integer.parseInt(subs[1])))
							m.writeString(message);
					} else {
						String[] subs = message.split(",");
						data.addBullet(id,(byte)Integer.parseInt(subs[3]),Integer.parseInt(subs[0].substring(3, subs[0].length())), Integer.parseInt(subs[1]));
						m.writeString(message);
					}
				}
				if(message.charAt(0)=='D'){
					data.killPlayer(Byte.parseByte(message.substring(2, message.length())));
					m.writeString(message);
				}
				if(message.charAt(0)=='C'){
					m.writeString("C:"+name+": "+message.substring(2,message.length()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String m){
		try{
			OutputStream os = s.getOutputStream();
			String s = m + "\n";
			os.write(s.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(m);
		}
	}

	public byte getParticipantId() {
		return id;
	}
}
