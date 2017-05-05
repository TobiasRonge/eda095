package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class OutputThread extends Thread {

	private BufferedWriter writer;
	private Mailbox m;
	private boolean done = false;
	
	public OutputThread(OutputStream outputStream,Mailbox m) {
		this.m = m;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		done = true;
	}
	
	public void run(){
		
		while(!done){
			try {
				String s = m.getLine();
				writer.write(s+"\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}
