import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;


public class terminalmain 
{
    /* Ports: 1701 - routing
     *        1702 - Text
     *        1703 - Send from receiver to client
     *        1704 - Send table to client
     */
	/**
	 * @param args
	 * @throws IOException 
	 */


	public static void main(String[] args) throws IOException 
	{
		/* Listen for connections from receiver on port 1703 */
		
		ArrayList<String> hostList = new ArrayList<String>();
		
		DatagramSocket s = new DatagramSocket(1703);  //Initialize socket to listener port
		s.setSoTimeout(1);
		DatagramPacket p;
		
		DatagramSocket termSock = new DatagramSocket(1704);

		DatagramPacket termPacket;
		boolean tablereceived = false;
		
		String name = "";
		
		ArrayList<String> queue = new ArrayList<String>();
		boolean messagereceived;
		
		Thread tt = new Thread();
		
		Integer count = 0;
		Integer threadNum=0;
		
		while (true)
		{
			//System.out.println("Value of tablereceived is: " + tablereceived);
			if (count.equals(0))
			{
				termSock.setSoTimeout(10000);
			}
			else
			{
			   termSock.setSoTimeout(1);
			}
			//System.out.println("While loop entered");
			//Get updated table from router
			termPacket = new DatagramPacket(new byte[1500],1500);
			
			try
			{
				termSock.receive(termPacket);
				tablereceived = true;
				//String message = new String(termPacket.getData());
				//System.out.println("Current message: " + message);
				
			}
			catch(SocketTimeoutException ste)
			{
				//System.out.println("TermSock timed out");
				tablereceived = false;
			}
			
			if (tablereceived)
			{
				Scanner in = new Scanner(new String(termPacket.getData()).trim());
				hostList.clear();
				name = in.nextLine(); //Get router name
				while (in.hasNext())
				{
					String line = in.nextLine();
					hostList.add(line);
				}
			}
			
		
			p = new DatagramPacket(new byte[1500],1500);
			
			try
			{
				s.receive(p);
				messagereceived = true;	
			}
			catch(SocketTimeoutException ste)
			{
				//Catch socket timeout excaption, keep on running
				messagereceived = false;
			}
			
			if (messagereceived)
			{
				String data = new String(p.getData());
				Scanner in = new Scanner(data.trim());
				
				String source = in.nextLine();
				String destination = in.nextLine();
				String timestamp = in.nextLine();
				String message = in.nextLine();
				
				String hostlist = "";
				String host = "";  //Assume that all information is being printed on a new line each time
				while (in.hasNext())
				{
					host = in.nextLine();
					hostlist = hostlist + host + "\n      ";
				}
				
				//Have retrieved all message information, construct final print statement
				data = "[" + timestamp + " - " + source + "]: " + message + "\n" + "     List of routing hosts: " + hostlist;  
				
				queue.add(data);
				
			}
			if (tt.isAlive()==false)
			{
				tt = new Thread(new terminaltarget(hostList,name,queue));
				tt.start();
                                threadNum++;
				//System.out.println("New Thread started: Thread num = " + threadNum);
				
			}
			
			
			
			
			
		    count ++;
		}

	}

}
