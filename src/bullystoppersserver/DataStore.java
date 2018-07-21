package bullystoppersserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DataStore 
{
	public static final String VERSION_ID = "0.1-nightly";
	
	public static String EMAIL_ADDRESS = "noreplybullystoppers@gmail.com";
	
	public static Date refDate = null;
	
	public static HashMap<String, User> authenticated = new HashMap<String, User>();
	
	public static ArrayList<Report> reportList = new ArrayList<Report>();
}
