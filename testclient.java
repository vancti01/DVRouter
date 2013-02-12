import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;


public class testclient {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		DatagramSocket s = new DatagramSocket();
		DatagramPacket p = new DatagramPacket("Test message".getBytes(),"Test message".getBytes().length);
		
		Scanner in = new Scanner(System.in);
		String keys="";
		
		while (keys!="exit")
		{
			System.out.println("Press 1 then enter to send a message");
			keys = in.nextLine();
			s.connect(InetAddress.getLocalHost(),1800);
			s.send(p);
		}
		
		
	}

}
