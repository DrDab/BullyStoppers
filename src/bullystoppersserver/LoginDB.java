package bullystoppersserver;

public class LoginDB 
{
	public static boolean acctExists(String username, String password)
	{
		if (username.matches("student") && password.matches("password"))
		{
			return true;
		}
		else if (username.matches("student2") && password.matches("password"))
		{
			return true;
		}
		else if (username.matches("teacher") && password.matches("password"))
		{
			// TODO: In actual implementation, only allow teacher login if teacher in "BullyStoppers" Active Directory Group.
			// else, return false.
			return true;
		}
		else if (username.matches("sysadmin") && password.matches("password"))
		{
			return true;
		}
		return false;
	}
	
	public static int getUserType(String username, String password)
	{
		if (username.matches("student") && password.matches("password"))
		{
			return 0;
		}
		else if (username.matches("student2") && password.matches("password"))
		{
			return 0;
		}
		else if (username.matches("teacher") && password.matches("password"))
		{
			return 1;
		}
		else if (username.matches("sysadmin") && password.matches("password"))
		{
			return 2;
		}
		return 0;
	}
}
