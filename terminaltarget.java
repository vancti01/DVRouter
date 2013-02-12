import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;


public class terminaltarget implements Runnable {

    ArrayList<String> hostList;
    ArrayList<String> queue;
	String name;
	DatagramSocket mySock;
	
	public terminaltarget(ArrayList<String> a, String n,ArrayList<String> q) throws SocketException 
	{
       hostList = a;
       name = n;
       mySock = new DatagramSocket();
       queue = q;
	}

	@Override
	public void run() 
	{
		
		
		String target = "";
		String message = "[TerminalTarget] --- Initial message: If you see this you (or the idiot across from you) did something wrong";
		
		System.out.println("Select the target host: ");
	
		
		//System.out.println("Values in hostList are: \n");
		
		for (int i=0;i<hostList.size();i++)
		{
			System.out.println("[" + i + "]" + " " + hostList.get(i));
		}
		
		Scanner in = new Scanner(System.in);
		Integer selection = Integer.decode(in.nextLine());
		target = hostList.get(selection);
		
		
		//Create packet with message
		System.out.println("Input message to send to host " + target + ":");
				
		message = in.nextLine();
	    String timestamp = ((Long)new Date().getTime()).toString();  //Update timestamp before adding to message
		message = name + "\n" + target + "\n" + timestamp + "\n" + message + "\n";
		
		//System.out.println("[TerminalTarget] --- Message to be sent is: \n" + message);
             DatagramPacket packet = new DatagramPacket(message.getBytes(),message.getBytes().length);
		
		
        try 
        {
		mySock.connect(InetAddress.getLocalHost(),1702);
		System.out.println("[TerminalTarget] --- Sending message");
	        mySock.send(packet);
	        mySock.close();
		} 
        catch (UnknownHostException e) 
        {
			e.printStackTrace();
		} 
                catch (IOException e) 
                {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

        if (queue.isEmpty()==false)
        {
	   for (int i=0;i<queue.size();i++)
	   {
		System.out.println(queue.remove(i));
	   }
        }

    
	}	

}
