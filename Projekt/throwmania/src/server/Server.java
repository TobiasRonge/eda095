package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	byte i = 1;
	
	
	public Server(){
		try {
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(25000);
			Mailbox m = new Mailbox();
			ParticipantMonitor pm = new ParticipantMonitor();
			BroadcastThread bt = new BroadcastThread(m,pm);
			bt.start();
			
			ServerData data = new ServerData(pm,m);
			GameLogic gl = new GameLogic(data);
			gl.start();
			
			while(i<=4){
				Socket s = ss.accept();
				String name = "Player " + i;
				s.getOutputStream().write((int)i);
				s.getOutputStream().flush();
				m.writeString("C: "+name+" has joined the game.");
				m.writeString("J: "+i);
				Participant p = new Participant((byte)(i-1),name,s,m,pm,data);
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
}
