import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;


public class dvserverthread extends Thread
{
	
	dvrouter r;
	DatagramSocket mySock;
	DatagramSocket termSock;
    DatagramPacket packet;
    DatagramPacket termPacket;

 
    
	
	public dvserverthread(DatagramSocket s,dvrouter router)
	{
		
		super();
		r = router;
		mySock = s;
	}

        public dvserverthread(DatagramPacket p, dvrouter router, DatagramSocket s, DatagramSocket ts)
        {
           super();
           r = router;
           packet = p;
           mySock = s;
           termSock = ts; 
           
        }
	
	public void run()
	{
		//Make scanner around string made out of byte[] from packet
		byte[] data = packet.getData();
		
		String dataString = new String(data);   // Decode data from packet
		Scanner scan = new Scanner(dataString.trim());
		
		

		String host = scan.nextLine().trim().toLowerCase();   //Get [From host], in lower case characters
		
		System.out.println("[ServerThread] --- Table Update Receieved from: " + host);
		System.out.println("[ServerThread] --- Received table: " + "\n" + "\n" + dataString + "\n");
		
		System.out.println("[ServerThread] --- Re-starting timer for host: " + host);
		for (String h : r.getNeighborList())
		{
			System.out.println("Neighbor List items: " + h);
		}
                
			
		
		r.cancelTimer(host);
		r.startTimer(host);
		
		
		Long myTime = r.getNeighborTime(host);                     //Get last known timestamp for [From host]          
                //System.out.println("[ServerThread debug] --- This is fromHostTime after split : " + (((scan.nextLine().split("."))));

        String splitstring = scan.nextLine().trim();
        Integer splitInt = splitstring.indexOf(".");
        if (splitInt.equals(-1))
        {
        	splitInt = splitstring.length();
        }
		Long fromHostTime = Long.decode(splitstring.substring(0,(splitInt)));  //Get [From host] timestamp
		scan.nextLine();                                         //Skip past number of list items field
		String line = scan.nextLine();                          //Get first host - cost pair
		
		System.out.println("[ServerThread] --- Updating routing table from host : " + host);
		while (scan.hasNext())
		{
		
		   if (myTime < fromHostTime)  //current router timestamp for host earlier than new host timestamp
		   {
		       
		       //System.out.println("[ServerThread] --- Host timestamp is : " + fromHostTime);
			   String[] splitline = line.split(" ");
			
			   String hostitem = splitline[0];
			   //System.out.println("Current value of splitline = " + splitline[0]);
			   Integer costitem = Integer.decode(splitline[1]);
			   
			   r.updateNeighborTime(host,fromHostTime);
			   
			   if (r.isHostPresent(hostitem))
			   {
				   //If host is already in table, update cost if it is cheaper
				
				   Integer myCostItem = r.getCostList().get(r.getHostList().indexOf(hostitem));
		    	
				   if (myCostItem > costitem + r.getCostItem(r.getHostList().indexOf(host)))
				   {
					   r.updateHost(hostitem, costitem + r.getCostItem(r.getHostList().indexOf(host)));
					   r.setViaHost(hostitem, host);
					   
					   for (String target : r.getNeighborList())
						{

						    System.out.println("[ServerThread] --- Sending out table updates via transferthread");
							Thread transfer = new dvtransferthread(r,target); 
							transfer.start();
						}
				   }
				   else if (myCostItem < costitem && r.getViaHost(hostitem).equals(host)) 
				   {
					   r.updateHost(hostitem, costitem);

				   }
		    	
			    }
		 
			    else
			    {
				  r.addHost(hostitem, costitem + r.getCostItem(r.getHostList().indexOf(host)));
				  r.setViaHost(hostitem, host);
				  
				  for (String target : r.getNeighborList())
				  {

					    System.out.println("[ServerThread] --- Sending out table updates via transferthread");
						Thread transfer = new dvtransferthread(r,target); 
						transfer.start();
				  }
				  
			    }
			
		   }
		  
		   line = scan.nextLine();  //Get next host-cost pair
		   
		   System.out.println("[ServerThread] --- Current table is : " + "\n" + r.getTable());
		}
		
		//Send data out to terminal
		//byte[] tableBytes = r.getTable().getBytes();
		//termPacket = new DatagramPacket(tableBytes, tableBytes.length);
		
		//termSock.connect(r.getTerminalAddress(), 1703);
		/*
		try 
		{
			termSock.send(termPacket);
			System.out.println("[ServerThread] --- Sent updated table to terminal");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		*/
		//All data has been read and table modified as needed, send updates to all necessary hosts 
		

	
   }
	
	
}
