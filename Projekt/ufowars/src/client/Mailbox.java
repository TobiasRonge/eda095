package client;

public class Mailbox {
	
	private String s = "";
	
	public Mailbox(){
	}

	public synchronized void putLine(String s){
		while(this.s!=null&&!this.s.equals("")){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		this.s = s;
		notifyAll();
	}

	public synchronized String getLine(){
		while(s==null||s.equals("")){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String s2 = s;
		s = "";
		notifyAll();
		return s2;
	}
}
