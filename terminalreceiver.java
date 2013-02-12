import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;


/**This class will take incoming messages and pass them along to the appropriate host, according to the routing table */

public class terminalreceiver implements Runnable {

	DatagramSocket dSock;
	DatagramPacket packet;
	dvrouter router;
	
	String sourceHost;
	String destinationHost;
	String message;
	String timestamp = "Initial timestamp";
	MessageQueue queue;
	//print out route in run
	
	public terminalreceiver(dvrouter r) throws SocketException 
	{
		router = r;
		dSock = new DatagramSocket(1702);

	
	}

	@Override
	public void run() 
	{
		while (true)
		{

			System.out.println("[TerminalReceiver] --- Thread is still running");
			
			packet = new DatagramPacket(new byte[1500],1500);
			try
			{
				System.out.println("Entered terminalreceiver try block");
				dSock.receive(packet);
				System.out.println("Finished receiving a packet");
				Scanner in = new Scanner(new String(packet.getData()).trim());
				sourceHost = in.nextLine();
				//System.out.println("[TerminalReceiver] --- Received message from: " + sourceHost);
				destinationHost = in.nextLine();
				timestamp = in.nextLine(); 
				message = in.nextLine();
				System.out.println("[TerminalReceiver] --- Received message from " + sourceHost + " at " + timestamp + "\n");
				
				//check it its my message, if so add to display queue
				if (destinationHost.equals(router.getName()))
				{		    
				    //Send out present queue to terminal client
				    DatagramSocket s = new DatagramSocket();

				    packet.setAddress(InetAddress.getLocalHost());
				    packet.setPort(1703);
				    //s.connect(InetAddress.getLocalHost(), 1703);
                    System.out.println("[TerminalReceiver] --- Received message for me");
				    s.send(packet);
				   // s.close();
				  // System.out.println("{" + sourceHost + " at " + timestamp +  "} :" + message);	
				}
				else
				{
					//Not my message, just pass along
					
					String d = new String(packet.getData());
					d = d.trim() + "\n" + router.getName().trim() + "\n";        //Add me to list of forwarding routers
					String viaHost = router.getViaHost(destinationHost);
					packet.setData(d.getBytes());
					packet.setAddress(InetAddress.getByName(viaHost));
					packet.setPort(1702);
					System.out.println("[TerminalReceiver] --- Received message to forward to " + viaHost);
                                        System.out.println("[TerminalReceiver] --- Receieved message is: " + d + "\n");
					//dSock.connect(InetAddress.getByName(viaHost), 1702);   //Pass message to next router
					dSock.send(packet);
					//dSock.close();
				}
			} 
			catch (IOException e) 
			{
                System.out.println("[TerminalReceiver] --- Caught IO Exception");
				e.printStackTrace();
			}
			
			System.out.println("[TerminalReceiver] --- Reached end of while loop");
		}

	}

}
