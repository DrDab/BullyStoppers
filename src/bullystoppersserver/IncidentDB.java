package bullystoppersserver;

import java.util.LinkedList;
import java.util.Queue;

public class IncidentDB 
{
	// a wrapper of functions to store and retrieve the bullying reports.
	// it should work this way:
	// all requests should be synchronous and not asynchronous, even if the 
	// IncidentDB runs in a separate thread.
	// Each time we request an action, we should add something to a queue, so the actions will be handled FIFO.
	// there should be one queue for adding entries,
	// one queue for querying for entries,
	// one queue for closing entries.
	// for querying for entries, the call should be given a unique ID number. (UUID or something)
	// the result should be stored in a HashMap with the ID mapped to it. Once we get the ID,
	// the ID-map should be destroyed.
	// IncidentDB should run in a while(true) loop, and repeatedly check if any of the queues
	// are not empty.
	 private static Queue<Request> toAdd = new LinkedList<Request>();
	 private static Queue<Request> toGet = new LinkedList<Request>();
	 private static Queue<Request> toClose = new LinkedList<Request>();
	 
	 // q.remove will remove the one last.
	 
	 public static void run()
	 {
		 for(;;)
		 {
			 while(!toAdd.isEmpty())
			 {
				 
			 }
			 while(!toGet.isEmpty())
			 {
				 
			 }
			 while(!toClose.isEmpty())
			 {
				 
			 }
		 }
	 }
	
}

class Request
{
	
}
