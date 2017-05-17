package server;

public class Mailbox {
	private String s = "";
	
	public synchronized void writeString(String s){
		while(!this.s.equals("")){
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
	
	public synchronized String readString(){
		
		while(s.equals("")){
		
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String newString = s;
		s = "";
		notifyAll();
		return newString;
	}
}
