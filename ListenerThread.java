import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;


public class ListenerThread extends Thread 
{
	dvrouter r;
	DatagramPacket p;
	
	public ListenerThread(dvrouter router)
	{
		r = router;
	}
	
	public void run()
	{
		DatagramSocket serverSocket=null;
		DatagramSocket terminalSocket = null;
		
		try 
		{
          
			serverSocket = new DatagramSocket(1701);
			terminalSocket = new DatagramSocket();
			
		}
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		
		while (true)
		{
			 try 
			 {
              System.out.println("[ListenerThread] -- Waiting for incoming connections");
              byte[] payload = new byte[1500];
              p = new DatagramPacket(payload,1500);             //Make sure this does not cause problems for parsing the string on the dvserverthread
                    			
              serverSocket.receive(p);
		      System.out.println("[ListenerThread] --- Data packet received, starting server thread");
		      Thread serverthread = new dvserverthread(p,r,serverSocket, terminalSocket);
			  serverthread.start();	
			  
				
				
			 } 
			 catch (IOException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		}
	}
	

}
