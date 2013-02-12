import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;


/* Created by Tim Van Cleave, with debug assistance from Erik Tingelstad */





public class dvmain {

	
	
	
	
	/**
	 * @param args
	 * @throws SocketException 
	 */
	public static void main(String[] args) throws SocketException {
		
		
        //Get initial table from file
		String myName = "";
		String tableString = "";
		InetAddress termAddr = null;
		
		try 
		{
			Scanner scan = new Scanner(new File("dvroute.ini"));
			ArrayList<String> neighborList = new ArrayList<String>();
			
			Integer pass = 0;
			String word = scan.next().toLowerCase();
			ArrayList<String> hostList = new ArrayList<String>();
			ArrayList<Integer> costList = new ArrayList<Integer>();
			
			
			while (scan.hasNext())
			{
				
				if (pass.equals(0))
				{
				   	myName = word;
				   	pass++;
				   	System.out.println(myName + " is my name");
				}
				else
				{
				neighborList.add(word);
				hostList.add(word);
				costList.add(Integer.valueOf(scan.next()));
				}
				
				if (scan.hasNext())
				{
					word = scan.next().toLowerCase();
				}
				
				
			}
	
			System.out.println("Contents of hostList are: " + hostList.toString());
			System.out.println("Contents of costList are: " + costList.toString());
			System.out.println("Contents of neighborList = " + neighborList.toString());
			
			
			dvrouter router = new dvrouter(myName,hostList,costList, neighborList);
			System.out.println("Router created");
			
			//Get address of terminal
			//System.out.println("Input address of terminal device (IP or DNS name): ");
			//Scanner in = new Scanner(System.in);
			//String addr = in.nextLine();
			/*
			try 
			{
				termAddr = InetAddress.getByName(addr);
			} 
			catch (UnknownHostException e) 
			{
			
				e.printStackTrace();
			}
			*/
			//router.setTerminalAddress(termAddr);
			
			for (String host : neighborList)
			{  
			   System.out.println("Creating transfer thread for host: " + host);
		       Thread transfer = new dvtransferthread(router, host);   //Send out initial host table
			   transfer.start();
			   router.startTimer(host);
			}
			
			//Send initial table to terminal
			//DatagramPacket termPacket = new DatagramPacket(router.getTable().getBytes(),router.getTable().getBytes().length);
			//DatagramSocket sock = new DatagramSocket();
			//sock.connect(router.getTerminalAddress(),1703);
            //sock.send(termPacket);
            //System.out.println("[DVMain] --- Sent inital table to terminal");

			TableTask task = new TableTask(router);
			Long period = new Long(10000);
			Timer timer = new Timer();
			
			timer.schedule(task,10000,period);
			
			
		    //Start listening for other routers
			
			ListenerThread lt = new ListenerThread(router);
			lt.start();
			
			Thread tr = new Thread(new terminalreceiver(router));
			tr.start();
		
			
			
			
			 
			
			
		} 
		catch (FileNotFoundException e) 
		{
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
