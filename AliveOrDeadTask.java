import java.util.TimerTask;


public class AliveOrDeadTask extends TimerTask 
{
   dvrouter router;
   String host;
   
	public AliveOrDeadTask(dvrouter r,String h) 
	{
        host = h;
		router = r;
	}

	@Override
	public void run() 
	
	{
		router.updateHost(host, 9999);
		System.out.println("[AliveOrDeadTask --- Dead host: " + host);
		for (String target : router.getNeighborList())
		{

		    System.out.println("[AliveOrDeadTask] --- Sending out table updates via transferthread");
			Thread transfer = new dvtransferthread(router,target); 
			transfer.start();
		}

	}

}
