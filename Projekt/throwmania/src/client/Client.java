package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {
		new Client(args[0],Integer.parseInt(args[1]));
	}
	
	public Client(String server,int port){
		
		
		Mailbox m = new Mailbox();
		//SharedData data = null;
		try {
			Socket s = new Socket(server,port);
			
			byte id = (byte)s.getInputStream().read();
			SharedData data = new SharedData(id,m);
			InputThread it = new InputThread(s.getInputStream(),data);
			OutputThread ot = new OutputThread(s.getOutputStream(),m);
			it.start();
			//ot.start();
			Game game = new Game(data,m,s,it,ot);
			new GUI(game,data);
			game.run();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
