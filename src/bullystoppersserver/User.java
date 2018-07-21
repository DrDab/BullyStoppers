package bullystoppersserver;

public class User 
{
	private String username;
	private String password;
	
	private int accountType;
	// -1 = unauthenticated
	// 0 = student
	// 1 = teacher
	// 2 = admin
	
	public User(String username, String password, int accountType)
	{
		this.username = username;
		this.password = password;
		this.accountType = accountType;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public int getAccountType()
	{
		return accountType;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setAccountType(int accountType)
	{
		this.accountType = accountType;
	}
	
}
