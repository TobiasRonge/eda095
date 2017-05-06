package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	
	private Game game;
	private Mailbox m;
	private String server;
	private int port;
	private GUI gui;
	
	public static void main(String[] args) {
		new Client(args[0],Integer.parseInt(args[1]));
	}
	
	public void connect(){	
		try {
			Socket s = new Socket(server,port);
			byte id = (byte)s.getInputStream().read();
			SharedData data = new SharedData(id,m);
			InputThread it = new InputThread(s.getInputStream(),data);
			OutputThread ot = new OutputThread(s.getOutputStream(),m);
			it.start();
			//ot.start();
			game = new Game(data,m,s,it,ot);
			gui.registerGame(game,data);
			game.start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client(String server,int port){
		m = new Mailbox();
		gui = new GUI(this);
		this.server = server;
		this.port = port;
	}

}
