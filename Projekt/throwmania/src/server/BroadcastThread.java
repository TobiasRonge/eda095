package server;

public class BroadcastThread extends Thread{
	
	private Mailbox m;
	private ParticipantMonitor pm;
	private boolean done = false;
	
	@Override
	public void run() {
		super.run();
		
		while(!done){
			String s = m.readString();
			if(s!=null&&!s.equals(""))
				pm.broadcastMessage(s);
		}
	}

	public BroadcastThread(Mailbox m,ParticipantMonitor pm){
		this.m = m;
		this.pm = pm;
	}
	
	public void close(){
		done = true;
		m.notifyAll();
	}
}
