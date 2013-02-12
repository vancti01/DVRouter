import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.TimerTask;


public class TableTask extends TimerTask {

    ArrayList<String> hostList;	
    ArrayList<String> neighbors;
    ArrayList<Integer> costList;
    String myName;
    dvrouter r;
    
	
	public TableTask(dvrouter router) 
	{
		//Get references to host and cost lists, as well as this host's name
		super();
	   	hostList = router.getHostList();
	   	costList = router.getCostList();
	   	myName = router.getName();
	   	neighbors = router.getNeighborList();
	   	r = router;
	}

	@Override
	public void run() 
	{
		for (String host : neighbors)
		{         
			Thread t = new dvtransferthread(r,host);
            //System.out.println("[TableTask] -- Starting new transfer thread for host : " + host);
			t.start();
		}

	}

}
