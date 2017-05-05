package server;

public class BroadcastThread extends Thread{
	
	private Mailbox m;
	private ParticipantMonitor pm;
	
	@Override
	public void run() {
		super.run();
		
		while(true){
			String s = m.readString();
			pm.broadcastMessage(s);
		}
	}

	public BroadcastThread(Mailbox m,ParticipantMonitor pm){
		this.m = m;
		this.pm = pm;
	}
}
