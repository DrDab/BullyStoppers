package bullystoppersserver;

public class Chocolat 
{
	static boolean verboseLogging = true;
	public static void print(String sk) 
	{
		if (verboseLogging)
		{
			System.out.print(sk);
		}
	}
	public static void println(String sk) 
	{
		if (verboseLogging)
		{
			System.out.println(sk);
		}
	}
}
	