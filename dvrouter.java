import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;


public class dvrouter
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	ArrayList<String> hostList;
	ArrayList<Integer> costList;
	ArrayList<String> neighborList;
	Long[] neighborTimeList;
	String name;
	Long timestamp;
	
	boolean canPrintQueue = false;  //Initialize queue control variable to false. Do not print queue until this is set to true and later checked by a terminal thread 
	
	InetAddress termAddr;
	
	//Post init
    Timer timer;
    ArrayList<Timer> timerList;
    ArrayList<String> messageQueue;
    ArrayList<String> viaList;
	
	
    public dvrouter()
    {
        hostList = new ArrayList<String>();
        costList  = new ArrayList<Integer>();
        neighborList = new ArrayList<String>();
        neighborTimeList = new Long[5];
        
        name = "";
        Date current = new Date();
        timestamp = current.getTime();
        
        timerList = new ArrayList<Timer>();
        		
        
    }
    
    public dvrouter(String n, ArrayList<String> h,ArrayList<Integer> c, ArrayList<String> neighbors)
    {
    	hostList = h;
    	costList = c;
    	neighborList = neighbors;
    	name = n;
    	
    	Date current = new Date();
        timestamp = current.getTime(); 
        
        timerList = new ArrayList<Timer>(); 
        neighborTimeList = new Long[5];
        
        for (int i=0;i<neighborList.size();i++)
        {
        	timerList.add(new Timer());
        }
        
        
        for (int i=0;i<5;i++)
        {
        	neighborTimeList[i] = new Long(0);
        }
        
        viaList = new ArrayList<String>();
        
        for (int i=0;i<40;i++)
        {
        	viaList.add("");
        }
        
        Iterator iterator = neighborList.iterator();
        Integer index = 0;
        while (iterator.hasNext())
        {
        	viaList.set(index, (String)iterator.next());
        	index ++;
        }
    }
    
    public String getName()
    {
    	return name;
    }
    
    public void setName(String n)
    {
    	name = n;
    
    }
    
    public Long getNeighborTime(String n)
    {
    	Integer index = neighborList.indexOf(n);
    	return neighborTimeList[index];
    }
    
    public synchronized void updateNeighborTime(String host, Long l)
    {
    	Integer index = neighborList.indexOf(host);
    	neighborTimeList[index] = l;
    }
    
    public Integer getIndexOfNeighbor(String n)
    {
    	Integer index = neighborList.indexOf(n);
    	return index;
    }
    
    public ArrayList<String> getNeighborList()
    {
    	return neighborList;
    }
    
    public synchronized String getHostItem(Integer index)
    {
    	return hostList.get(index);
    }
    
    public boolean isHostPresent(String h)
    {
    	return hostList.contains(h);
    }
    
    public synchronized Integer getCostItem(Integer index)
    {
    	return costList.get(index);
    }
    
    public synchronized ArrayList<String> getHostList()
    {
    	return hostList;
    }
    
    public synchronized ArrayList<Integer> getCostList()
    {
    	return costList;
    }
    
    public synchronized void addHostItem(String s)
    {
    	hostList.add(s);
    	
    	Date current = new Date();
        timestamp = current.getTime(); 
    }
    
    public synchronized void addCostItem(Integer i)
    {
    	costList.add(i);
    	
    	Date current = new Date();
        timestamp = current.getTime(); 
    }
    
    public synchronized void updateHost(String host, Integer newcost)
    {
    	//Update costs of a host in the table
    	//Update timestamp on every update, use local epoch
    	
       int index = hostList.indexOf(host);
       costList.set(index, newcost);
       
       Date current = new Date();
       timestamp = current.getTime();
   
    	
    }
    
    public synchronized void addHost(String host, Integer cost)
    {
    	 hostList.add(host);
    	 costList.add(cost);
    	 
    	 Date current = new Date();
         timestamp = current.getTime(); 
    }
    
    public void updateTimestamp()
    {
    	timestamp = new Date().getTime();
    }
    
    public Long getTimestamp()
    {
    	return timestamp;
    }
    
    public synchronized String getTable()
    {
    	String tableString = "";
    	Date currentTime = new Date();
		tableString = tableString + name + "\n";
		String time = ((Long)currentTime.getTime()).toString();
		tableString = tableString + time + "\n";
        tableString = tableString + (((Integer)hostList.size()).toString()) + "\n";  //Number of items in list
        
        
		
		for (int i=0;i < hostList.size(); i++)
		{
			tableString = tableString + hostList.get(i).toString() + " ";
			tableString = tableString + costList.get(i).toString() + "\n";
		}
		
		return tableString;
    }
    

/* TIMER METHODS */    

    
    public synchronized void startTimer(String host)
    {
    	Timer timer = new Timer();
    	timerList.set(getIndexOfNeighbor(host),timer);
        
    	//System.out.println("Size of timerList : " + timerList.size());
    	System.out.println("[Router] --- Starting timer for host: " + host);
        //System.out.println("This is index in startTimer : " + getIndexOfNeighbor(host));
        //System.out.println("This is timerList : " + timerList.toString());
    	timer.schedule(new AliveOrDeadTask(this,host), 30000, 30000);
    }
    
    public synchronized void cancelTimer(String host)
    {
    	Integer index = neighborList.indexOf(host);
       // System.out.println("This is timerList in cancel: " + timerList.toString());
       // System.out.println("This is index in cancel: " + index.toString());
    	Timer t = timerList.get(index);
    	System.out.println("[Router] --- Canceling timer for host: " + host);
    	t.cancel();
    	
    }
    
    /* ----- ViaHost Methods ----- */
    
    public synchronized void setViaHost(String origh, String s)
    {
        if (s.equals(this.getName()))
        {
           Integer index = hostList.indexOf(origh);
    	viaList.set(index, origh);
        }
    	else
       {
          Integer index = hostList.indexOf(origh);
    	  viaList.set(index, s);
       } 
    }
    
	public ArrayList<String> getViaList() 
	{
		return viaList;
	}
    
    public synchronized String getViaHost(String s)
    {
    	Integer index = hostList.indexOf(s);
    	return viaList.get(index);
    }
    
    /* ----- Terminal Methods ----- */
    
    public void setTerminalAddress(InetAddress i)
    {
    	termAddr = i;
    }
    
    public InetAddress getTerminalAddress()
    {
    	return termAddr;
    }
    
    
    /* ----- Message Queue Methods ----- */
    
    public synchronized void addMessage(String s)
    {
    	messageQueue.add(s);
    }
    
    public String getMessageAtIndex(Integer i)
    {
    	return messageQueue.get(i);
    }
    
    


}
