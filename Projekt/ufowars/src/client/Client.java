package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	
	private Game game;
	private Mailbox m;
	private String server;
	private GUI gui;
	
	public static void main(String[] args) {
		new Client(args[0]);
	}
	
	public void connect(){	
		try {
			Socket s = new Socket(server,25000);
			byte id = (byte)s.getInputStream().read();
			SharedData data = new SharedData(id,m);
			InputThread it = new InputThread(s.getInputStream(),data);
			OutputThread ot = new OutputThread(s.getOutputStream(),m);
			it.start();
			//ot.start();
			game = new Game(data,m,s,it,ot);
			ot.registerGame(game);
			gui.registerGame(game,data);
			data.registerGame(game);
			game.start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			gui.dispose();
		} catch (IOException e) {
			e.printStackTrace();
			gui.dispose();

		}
	}
	
	public Client(String server){
		m = new Mailbox();
		gui = new GUI(this);
		this.server = server;
	}

}
