import java.util.ArrayList;


public class MessageQueue 
{
   ArrayList<String[]> queue;
	
   public MessageQueue()
   {
	   queue = new ArrayList<String[]>();
   }
   
   public synchronized void enqueueMessage(String[] s)
   {
	   queue.add(s);
   }
   
   public synchronized String[] dequeueMessage()
   {
	  return queue.remove(0); 
   }
   
}
