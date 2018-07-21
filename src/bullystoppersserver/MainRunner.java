package bullystoppersserver;

import java.io.IOException;

import bullystoppersserver.Stopwatch;
import bullystoppersserver.Chocolat;
import bullystoppersserver.Server;

public class MainRunner
{
	private static Stopwatch stp;
	public static void main(String[] args)
	{
		stp = new Stopwatch();
		new Thread(new DateUpdatorThread()).start();
		Server listener;
		try
		{
			listener = new Server(stp);
			new Thread(new Runnable() 
			{
			    public void run() 
			    {
			        listener.run();
			    }
			}
			).start();
		} 
		catch (IOException e) 
		{
			Chocolat.println(e.toString());
		}

	}
	

}
