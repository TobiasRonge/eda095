package server;

import java.util.Vector;

public class ParticipantMonitor {
	private volatile Vector<Participant> participants;
	
	public ParticipantMonitor(){
		participants = new Vector<Participant>();
	}
	
	public synchronized void addParticipant(Participant p){
		participants.add(p);
		p.start();
	}
	
	public synchronized void removeParticipant(Participant p){
		participants.remove(p);
	}
	
	public synchronized void broadcastMessage(String m){
		for(Participant p:participants){
			p.sendMessage(m);
		}
	}
	
	public synchronized int getNumberOfPlayers(){
		return participants.size();
	}
	
	public synchronized void sendMessageTo(byte id,String m){
		for(Participant p:participants){
			if(p.getParticipantId()==id){
				p.sendMessage(m);
				return;
			}
		}
	}
	
	public void close(){
		for(Participant p:participants){
			p.close();
		}
	}
}
