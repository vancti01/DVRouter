import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;



public class dvtransferthread extends Thread
{

	/**
	 * @param args
	 */
	ArrayList<String> hostList;
	ArrayList<Integer> costList;
	String targethost;
	String myName;
	dvrouter router;
	
	 
	 
	 public dvtransferthread(dvrouter r, String th)
	 {
		 
		 super();
		 hostList = r.getHostList();
		 costList = r.getCostList();
		 myName = r.getName();
		 targethost = th;
		 router = r;
	 }
	
	
	
     public void run()
     {
    	 sendTable();
    	 
     }
     
     public void sendTable()
     {
    	 try 
    	 {
    		String tableString = "";                        //Create new packet with current table information 
    		
			Date currentTime = new Date();
			tableString = tableString + myName + "\n";
			String time = ((Long)currentTime.getTime()).toString();
			tableString = tableString + time + "\n";
            tableString = tableString + (((Integer)hostList.size()).toString()) + "\n";  //Number of items in list
            
            
			
			for (int i=0;i < hostList.size(); i++)
			{
				tableString = tableString + hostList.get(i).toString() + " ";
				tableString = tableString + costList.get(i).toString() + "\n";
			}
			
			
			byte[] payload = tableString.getBytes();
			InetAddress addr = InetAddress.getByName(targethost);
            
			//System.out.println("Target address is " + ipaddress);
			DatagramPacket packet = new DatagramPacket(payload,payload.length,addr,1701);   //Create packet with table ensconced
			
            System.out.println("[TransferThread] --- Creating socket for target host: " + targethost);
			DatagramSocket dSock = new DatagramSocket();

            //System.out.println("Socket addr " + addr);
            //System.out.println("Datagram packet addr " + packet.getAddress());
			
			dSock.connect(addr,1701);
                        
			dSock.send(packet); //Send packet to neighbor (host)
			
			dSock.close();
			
			
			//Send data out to terminal
			DatagramPacket termPacket;
			DatagramSocket termSock = new DatagramSocket();
			
			
			//Create new packet that has first entry of my name, then has all hosts in hostlist
			String hoststring = router.getName() + "\n";
			for (int i=0;i<router.getHostList().size();i++)
			{
				hoststring = hoststring + router.getHostList().get(i) + "\n";
			}
			
		    payload = hoststring.getBytes();
			termPacket = new DatagramPacket(payload, payload.length);
			

			termSock.connect(InetAddress.getLocalHost(), 1704);
			try 
			{
				termSock.send(termPacket);
				System.out.println("[TransferThread] --- Sent updated table to terminal");
				termSock.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		 }
    	 
    	 catch (SocketException e) 
		 {
			// TODO Auto-generated catch block
		 	e.printStackTrace();
		 } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	 
     
     }

}
