package bullystoppersserver;

import java.util.Date;

public class DateUpdatorThread implements Runnable
{
	@Override
	public void run()
	{
		for(;;)
		{
			DataStore.refDate = new Date(System.currentTimeMillis());
			try 
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}