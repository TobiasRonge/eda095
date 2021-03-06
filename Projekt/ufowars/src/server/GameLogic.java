package server;

public class GameLogic extends Thread {
	private boolean done = false;
	private ServerData data;
	
	public GameLogic(ServerData data){
		this.data = data;
	}
	
	public void run(){
		while(!done){
			try {
				Thread.sleep(1000/30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			data.checkCollision();		
		}
	}
	
	public void close(){
		done = true;
	}
	
	
}
