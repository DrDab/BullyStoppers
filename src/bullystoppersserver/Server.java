package bullystoppersserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Server 
{
	private Stopwatch st;
	private ServerSocket ssock;
	
	public Server(Stopwatch st) throws IOException
	{
		this.st = st;
		Chocolat.println("[" + st.elapsedTime() + "] Server Listener initialized.");
		try
		{
			ssock = new ServerSocket(8002);
		}
		catch (BindException ex0)
		{
			System.err.println("[" + st.elapsedTime() + "] FATAL: There is more than one instance of the BullyStoppers server running. (" + ex0 + ")");
			System.exit(0);
		}
	}
	
	public void run()
	{
	    Chocolat.println("[" + st.elapsedTime() + "] Server Listening...");
	    while (true) 
	    {
	    	Socket sock = null;
			try 
			{
				sock = ssock.accept();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	    	Chocolat.println("[" + st.elapsedTime() + "] Server Connected. [IP: " + sock.getRemoteSocketAddress() +"]");
	        new Thread(new ServerThread(sock, st)).start();
	    }
	}
	
}
class ServerThread implements Runnable
{
	private Socket sock;
	private Stopwatch st;
	private BufferedWriter bw;
	
	public ServerThread(Socket sock, Stopwatch st)
	{
		this.sock = sock;
		this.st = st;
	}

	private String s = "";
	
	@Override
	public void run() 
	{
		try
		{
			String receiveMessage = "";
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			InputStream istream = sock.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			try
			{	
				s = "HTTP/1.1 200 OK\r\n";
				s += "Content-Type: text/html\r\n";
				if((receiveMessage = receiveRead.readLine() )!= null)
				{
					while(receiveRead.ready())
					{
						receiveMessage += (receiveRead.readLine() + "\n");
					}
					// Chocolat.println(receiveMessage);
					boolean status = false;
					double start = st.getElapsedNanoTime();
					try
					{
						if (receiveMessage.substring(receiveMessage.indexOf("GET"), receiveMessage.indexOf("HTTP")).matches("GET / "))
						{
							status = true;
						}
					}
					catch (StringIndexOutOfBoundsException arg0)
					{
					}
					if (receiveMessage.contains("GET /index.html") || status)
					{
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									if (accountType == 0)
									{
										// student page
										s += "\r\n" + 
												"<!DOCTYPE HTML>\n" + 
												"<html>\n" + 
												"<head>\n" + 
												"	<meta charset='utf-8'>\n" + 
												"	<title>BullyStoppers Home</title> \n" + 
												"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
												"	<link rel=\"top\" title=\"BullyStoppers login\" href=\"/\">			\n" + 
												"	<style type=\"text/css\">\n" + 
												"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
												"			font-family:verdana,sans-serif;\n" + 
												"			color:white;\n" + 
												"			margin:0;\n" + 
												"			padding:0;\n" + 
												"			background:none;\n" + 
												"		}\n" + 
												"\n" + 
												"		body {\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-color:#012e57;\n" + 
												"		}\n" + 
												"\n" + 
												"		div#content2 {\n" + 
												"			text-align: center;\n" + 
												"			position:absolute;\n" + 
												"			top:28em;\n" + 
												"			left:0;\n" + 
												"			right:0;\n" + 
												"		}\n" + 
												"\n" + 
												"    	.center-td {\n" + 
												"        	text-align: center;\n" + 
												"    	}\n" +
												"\n" +
												"		.mbox {\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			margin-left: auto;\n" + 
												"			margin-right: auto;\n" + 
												"			margin-top:10px;\n" + 
												"			margin-bottom:10px;\n" + 
												"			padding:2px 0px;\n" + 
												"			width:480px;\n" + 
												"			border-radius: 5px;\n" + 
												"			box-shadow: 0px 0px 5px #000;\n" + 
												"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
												"		}\n" + 
												"\n" + 
												"		#searchbox { padding-bottom:5px; }\n" + 
												"		#searchbox3 { font-size: 80%; }\n" + 
												"		#searchbox4 { font-size: 60%; }\n" + 
												"\n" +
												"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" +
												"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}" +
												"	</style>\n" + 
												"</head>\n" + 
												"<body>\n" +
												"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
												"    <tr>\n" + 
												"        <td class=\"header_bkg\">\n" + 
												"            <ul>\n" + 
												"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
												"            </ul>\n" + 
												"        </td>\n" + 
												"    </tr>\n" + 
												"</table>" + // osdjoof
												"	<div id=\"notices\">\n" + 
												"		\n" + 
												"			<div id=\"notice\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"warning\" style=\"display: none;\"></div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"error\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"	</div>\n<br><br><br><br>" +
												"	<div id=\"searchbox\" class='mbox'>\n" + 
												"		<div id=\"static-index\">\n" + 
												"			<center>\n" + 
												"			<h1 style=\"font-size: 2em;\">BullyStoppers On-Line Reporting System</h1>\n" +
												"			</center>\n" + 
												"		</div>\n" + 
												"	<div id='mainbox'></div>\n" + 
												"</div>\n" + 
												"<br>\n" +
												"<center><div id=\"searchbox\" class='mbox'>\n" + 
												"	<div>\n" + 
												"		<a href=\"/report_bullying.html\" title=\"Report an incident\"><font color=\"FF00CC\"><strong>[ Report an incident here... ]</strong></font></a>\n" + 
												// " 		<p>\n" +
												"	</div>\n" + 
												"	<div>\n" + 
												"		<a href=\"/report_general_concern.html\" title=\"Report a concern\"><font color=\"FF00CC\">[ Report a concern here... ]</font></a>\n" + 
												"	</div>\n" + 
												"	<br><br>" +
												"	<div>\n" + 
												"		<p>To submit a tip via e-mail, please email <a href=\"mailto:" + DataStore.EMAIL_ADDRESS + "\" target=\"_top\"><font color=\"FF00CC\">" + DataStore.EMAIL_ADDRESS +"</font></a>.\n" +
												"	</div><br>\n" + 
												"	<div>\n" + 
												"		<p>If this is an emergency, please call <strong>911</strong> instead.</p>\n" +
												"	</div>\n" + 
												"</div></center>" +
												"<center><br />\n" + 
												"<font size=\"1\">" +
												"        Page generated in " +
												(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
												" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
												"        Server Local Time: " +
												DataStore.refDate.toString() +
												"<br></font></center>" +
												"</body>\n" + 
												"</html>\n";
									}
									else if (accountType == 1)
									{
										// teacher page
										// return this page if teacher in active directory group
										s += "\r\n" + 
												"<!DOCTYPE HTML>\n" + 
												"<html>\n" + 
												"<head>\n" + 
												"	<meta charset='utf-8'>\n" + 
												"	<title>BullyStoppers Home</title> \n" + 
												"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
												"	<link rel=\"top\" title=\"BullyStoppers login\" href=\"/\">			\n" + 
												"	<style type=\"text/css\">\n" + 
												"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
												"			font-family:verdana,sans-serif;\n" + 
												"			color:white;\n" + 
												"			margin:0;\n" + 
												"			padding:0;\n" + 
												"			background:none;\n" + 
												"		}\n" + 
												"\n" + 
												"		body {\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-color:#012e57;\n" + 
												"		}\n" + 
												"\n" + 
												"		div#content2 {\n" + 
												"			text-align: center;\n" + 
												"			position:absolute;\n" + 
												"			top:28em;\n" + 
												"			left:0;\n" + 
												"			right:0;\n" + 
												"		}\n" + 
												"\n" + 
												"    	.center-td {\n" + 
												"        	text-align: center;\n" + 
												"    	}\n" +
												"\n" +
												"		.mbox {\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			margin-left: auto;\n" + 
												"			margin-right: auto;\n" + 
												"			margin-top:10px;\n" + 
												"			margin-bottom:10px;\n" + 
												"			padding:2px 0px;\n" + 
												"			width:480px;\n" + 
												"			border-radius: 5px;\n" + 
												"			box-shadow: 0px 0px 5px #000;\n" + 
												"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
												"		}\n" + 
												"\n" + 
												"		#searchbox { padding-bottom:5px; }\n" + 
												"		#searchbox3 { font-size: 80%; }\n" + 
												"		#searchbox4 { font-size: 60%; }\n" + 
												"\n" +
												"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" +
												"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}" +
												"	</style>\n" + 
												"</head>\n" + 
												"<body>\n" +
												"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
												"    <tr>\n" + 
												"        <td class=\"header_bkg\">\n" + 
												"            <ul>\n" + 
												"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
												"            </ul>\n" + 
												"        </td>\n" + 
												"    </tr>\n" + 
												"</table>" + // osdjoof
												"	<div id=\"notices\">\n" + 
												"		\n" + 
												"			<div id=\"notice\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"warning\" style=\"display: none;\"></div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"error\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"	</div>\n<br><br><br><br>" +
												"	<div id=\"searchbox\" class='mbox'>\n" + 
												"		<div id=\"static-index\">\n" + 
												"			<center>\n" + 
												"			<h1 style=\"font-size: 2em;\">BullyStoppers On-Line Reporting System</h1>\n" +
												"			</center>\n" + 
												"		</div>\n" + 
												"	<div id='mainbox'></div>\n" + 
												"</div>\n" + 
												"<br>\n" +
												"<center><div id=\"searchbox\" class='mbox'>\n" +
												"Welcome to the staff incident management console.<br>" +
												"Click <a href=\"/view_incidents.html\" title=\"View incidents\"><font color=\"FF00CC\"><strong>[ here ]</strong></font></a> to view a list of bullying reports.<br>" +
												"</div></center>" +
												"<center><div id=\"searchbox\" class='mbox'>\n" +
												"Tickets currently open:<br>" +
												"<strong>" + DataStore.getIncidentsOpen() + "</strong><br>" +
												"</div></center>" +
												"<center><br />\n" + 
												"<font size=\"1\">" +
												"        Page generated in " +
												(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
												" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
												"        Server Local Time: " +
												DataStore.refDate.toString() +
												"<br></font></center>" +
												"</body>\n" + 
												"</html>\n";
									}
									else if (accountType == 2)
									{
										// admin page
									}
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else if (receiveMessage.contains("GET /login?username="))
					{
						String filteredString = receiveMessage.substring(receiveMessage.indexOf("?")+1);
						String username = URLDecoder.decode((filteredString.substring(filteredString.indexOf("=") + 1, filteredString.indexOf("&"))), "UTF-8");
						String password = URLDecoder.decode(filteredString.substring(filteredString.indexOf(username) + username.length() + 10, filteredString.indexOf(" HTTP/1.1")), "UTF-8");
						if (username.matches("") || password.matches(""))
						{
							// give a "I don't recognize this login" page.
							s += "\r\n\n" + 
									"<!DOCTYPE HTML>\n" + 
									"<html>\n" + 
									"<head>\n" + 
									"	<meta charset='utf-8'>\n" + 
									"	<title>Login Error</title> \n" + 
									"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
									"	<link rel=\"top\" title=\"Login Error\" href=\"/\">			\n" + 
									"	<style type=\"text/css\">\n" + 
									"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
									"			font-family:verdana,sans-serif;\n" + 
									"			color:white;\n" + 
									"			margin:0;\n" + 
									"			padding:0;\n" + 
									"			background:none;\n" + 
									"		}\n" + 
									"\n" + 
									"		body {\n" + 
									"			background-attachment:fixed;\n" + 
									"			background-position:50% 0%;\n" + 
									"			background-repeat:no-repeat;\n" + 
									"			background-color:#012e57;\n" + 
									"		}\n" + 
									"\n" + 
									"		div#content2 {\n" + 
									"			text-align: center;\n" + 
									"			position:absolute;\n" + 
									"			top:28em;\n" + 
									"			left:0;\n" + 
									"			right:0;\n" + 
									"		}\n" + 
									"\n" + 
									"		.mbox {\n" + 
									"			background-repeat:no-repeat;\n" + 
									"			background-attachment:fixed;\n" + 
									"			background-position:50% 0%;\n" + 
									"			margin-left: auto;\n" + 
									"			margin-right: auto;\n" + 
									"			margin-top:10px;\n" + 
									"			margin-bottom:10px;\n" + 
									"			padding:2px 0px;\n" + 
									"			width:480px;\n" + 
									"			border-radius: 5px;\n" + 
									"			box-shadow: 0px 0px 5px #000;\n" + 
									"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
									"		}\n" + 
									"\n" + 
									"		#searchbox { padding-bottom:5px; }\n" + 
									"		#searchbox3 { font-size: 80%; }\n" + 
									"		#searchbox4 { font-size: 60%; }\n" + 
									"	</style>\n" + 
									"</head>\n" + 
									"<body>\n" + 
									"<br>\n" + 
									"<br>\n" + 
									"<br>\n" + 
									"<br>\n" + 
									"<br>\n" + 
									"<br>\n" + 
									"<div id='searchbox3' class='mbox'>\n" + 
									"	<center>\n" + 
									"	<p><strong>\n" + 
									"	ERROR: Blank Login" +
									"	</p></strong><br>The username and/or password cannot be blank.\n<br><br>" +
									"	<a href=\"/index.html\" title=\"Homepage\">[ Back to login ]</a>" + 
									"	</center>\n" + 
									"</div>\n" + 
									"\n" + 
									"</div>\n" + 
									"<center><br />\n" + 
									"<font size=\"1\">" +
									"        Page generated in " +
									(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
									" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
									"        Server Local Time: " +
									DataStore.refDate.toString() +
									"<br></font></center>" +
									"</body>\n" + 
									"</html>\n";
						}
						else
						{
							if (LoginDB.acctExists(username, password))
							{
								// give the user a login cookie with UUID.
								UUID tmpID = UUID.randomUUID();
								DataStore.authenticated.put(tmpID.toString(), new User(username, password, LoginDB.getUserType(username, password)));
								
								s += "Set-Cookie: user_authtokken=" + tmpID.toString() + "; Path=/; Max-Age=86400" +
										"\r\n" +
										"\n" +
										"<!DOCTYPE HTML>\n" +
										"<meta http-equiv=\"refresh\" content=\"0; url=/index.html\" />\n" + 
										"<html>\n" + 
										"<head>\n" + 
										"	<meta charset='utf-8'>\n" + 
										"	<title>BullyStoppers: Login Success</title> \n" + 
										"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
										"	<link rel=\"top\" title=\"Login Successful\" href=\"/\">			\n" + 
										"	<style type=\"text/css\">\n" + 
										"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
										"			font-family:verdana,sans-serif;\n" + 
										"			color:white;\n" + 
										"			margin:0;\n" + 
										"			padding:0;\n" + 
										"			background:none;\n" + 
										"		}\n" + 
										"\n" + 
										"		body {\n" + 
										"			background-attachment:fixed;\n" + 
										"			background-position:50% 0%;\n" + 
										"			background-repeat:no-repeat;\n" + 
										"			background-color:#012e57;\n" + 
										"		}\n" + 
										"\n" + 
										"		div#content2 {\n" + 
										"			text-align: center;\n" + 
										"			position:absolute;\n" + 
										"			top:28em;\n" + 
										"			left:0;\n" + 
										"			right:0;\n" + 
										"		}\n" + 
										"\n" + 
										"		.mbox {\n" + 
										"			background-repeat:no-repeat;\n" + 
										"			background-attachment:fixed;\n" + 
										"			background-position:50% 0%;\n" + 
										"			margin-left: auto;\n" + 
										"			margin-right: auto;\n" + 
										"			margin-top:10px;\n" + 
										"			margin-bottom:10px;\n" + 
										"			padding:2px 0px;\n" + 
										"			width:480px;\n" + 
										"			border-radius: 5px;\n" + 
										"			box-shadow: 0px 0px 5px #000;\n" + 
										"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
										"		}\n" + 
										"\n" + 
										"		#searchbox { padding-bottom:5px; }\n" + 
										"		#searchbox3 { font-size: 80%; }\n" + 
										"		#searchbox4 { font-size: 60%; }\n" + 
										"	</style>\n" + 
										"</head>\n" + 
										"<body>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<div id='searchbox3' class='mbox'>\n" + 
										"	<center>\n" + 
										"	<p><strong>\n" + 
										"	Login Successful" +
										"	</p></strong><p><a href=\"/index.html\">[ Redirect ]</a></p>\n" + 
										"\n" + 
										"<br>" +
										"	</center>\n" + 
										"</div>\n" + 
										"\n" + 
										"</div>\n" + 
										"<center><br />\n" + 
										"<font size=\"1\">" +
										"        Page generated in " +
										(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
										" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
										"        Server Local Time: " +
										DataStore.refDate.toString() +
										"<br></font></center>" +
										"</body>\n" + 
										"</html>\n";
							}
							else
							{
								// give a "Wrong password" page.
								s += "\r\n\n" + 
										"<!DOCTYPE HTML>\n" + 
										"<html>\n" + 
										"<head>\n" + 
										"	<meta charset='utf-8'>\n" + 
										"	<title>Login Error</title> \n" + 
										"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
										"	<link rel=\"top\" title=\"Error\" href=\"/\">			\n" + 
										"	<style type=\"text/css\">\n" + 
										"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
										"			font-family:verdana,sans-serif;\n" + 
										"			color:white;\n" + 
										"			margin:0;\n" + 
										"			padding:0;\n" + 
										"			background:none;\n" + 
										"		}\n" + 
										"\n" + 
										"		body {\n" + 
										"			background-attachment:fixed;\n" + 
										"			background-position:50% 0%;\n" + 
										"			background-repeat:no-repeat;\n" + 
										"			background-color:#012e57;\n" + 
										"		}\n" + 
										"\n" + 
										"		div#content2 {\n" + 
										"			text-align: center;\n" + 
										"			position:absolute;\n" + 
										"			top:28em;\n" + 
										"			left:0;\n" + 
										"			right:0;\n" + 
										"		}\n" + 
										"\n" + 
										"		.mbox {\n" + 
										"			background-repeat:no-repeat;\n" + 
										"			background-attachment:fixed;\n" + 
										"			background-position:50% 0%;\n" + 
										"			margin-left: auto;\n" + 
										"			margin-right: auto;\n" + 
										"			margin-top:10px;\n" + 
										"			margin-bottom:10px;\n" + 
										"			padding:2px 0px;\n" + 
										"			width:480px;\n" + 
										"			border-radius: 5px;\n" + 
										"			box-shadow: 0px 0px 5px #000;\n" + 
										"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
										"		}\n" + 
										"\n" + 
										"		#searchbox { padding-bottom:5px; }\n" + 
										"		#searchbox3 { font-size: 80%; }\n" + 
										"		#searchbox4 { font-size: 60%; }\n" + 
										"	</style>\n" + 
										"</head>\n" + 
										"<body>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<br>\n" + 
										"<div id='searchbox3' class='mbox'>\n" + 
										"	<center>\n" + 
										"	<p><strong>\n" + 
										"	ERROR: Incorrect login" +
										"	</p></strong><br>The username and/or password is incorrect.\n<br><br>" +
										"	<a href=\"/index.html\" title=\"Homepage\">[ Back to login ]</a>" + 
										"	</center>\n" + 
										"</div>\n" + 
										"\n" + 
										"</div>\n" + 
										"<center><br />\n" + 
										"<font size=\"1\">" +
										"        Page generated in " +
										(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
										" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
										"        Server Local Time: " +
										DataStore.refDate.toString() +
										"<br></font></center>" +
										"</body>\n" + 
										"</html>\n";
							}
							// check if this login exists.
						}
					}
					else if (receiveMessage.contains("GET /logout.html"))
					{
						int p1 = receiveMessage.indexOf("user_authtokken=");
						if (p1 != -1)
						{
							DataStore.authenticated.remove(receiveMessage.substring(p1 + 16, p1 + 16 + 36));
						}
						s += "\r\n" +
								"\n" +
								"<!DOCTYPE HTML>\n" +
								"<meta http-equiv=\"refresh\" content=\"0; url=/index.html\" />\n" + 
								"<html>\n" + 
								"<head>\n" + 
								"	<meta charset='utf-8'>\n" + 
								"	<title>BullyStoppers: Logged Out</title> \n" + 
								"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
								"	<link rel=\"top\" title=\"Logout Successful\" href=\"/\">			\n" + 
								"	<style type=\"text/css\">\n" + 
								"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
								"			font-family:verdana,sans-serif;\n" + 
								"			color:white;\n" + 
								"			margin:0;\n" + 
								"			padding:0;\n" + 
								"			background:none;\n" + 
								"		}\n" + 
								"\n" + 
								"		body {\n" + 
								"			background-attachment:fixed;\n" + 
								"			background-position:50% 0%;\n" + 
								"			background-repeat:no-repeat;\n" + 
								"			background-color:#012e57;\n" + 
								"		}\n" + 
								"\n" + 
								"		div#content2 {\n" + 
								"			text-align: center;\n" + 
								"			position:absolute;\n" + 
								"			top:28em;\n" + 
								"			left:0;\n" + 
								"			right:0;\n" + 
								"		}\n" + 
								"\n" + 
								"		.mbox {\n" + 
								"			background-repeat:no-repeat;\n" + 
								"			background-attachment:fixed;\n" + 
								"			background-position:50% 0%;\n" + 
								"			margin-left: auto;\n" + 
								"			margin-right: auto;\n" + 
								"			margin-top:10px;\n" + 
								"			margin-bottom:10px;\n" + 
								"			padding:2px 0px;\n" + 
								"			width:480px;\n" + 
								"			border-radius: 5px;\n" + 
								"			box-shadow: 0px 0px 5px #000;\n" + 
								"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
								"		}\n" + 
								"\n" + 
								"		#searchbox { padding-bottom:5px; }\n" + 
								"		#searchbox3 { font-size: 80%; }\n" + 
								"		#searchbox4 { font-size: 60%; }\n" + 
								"	</style>\n" + 
								"</head>\n" + 
								"<body>\n" + 
								"<br>\n" + 
								"<br>\n" + 
								"<br>\n" + 
								"<br>\n" + 
								"<br>\n" + 
								"<br>\n" + 
								"<div id='searchbox3' class='mbox'>\n" + 
								"	<center>\n" + 
								"	<p><strong>\n" + 
								"	Logout Successful" +
								"	</p></strong><p><a href=\"/index.html\">[ Redirect ]</a></p>\n" + 
								"\n" + 
								"<br>" +
								"	</center>\n" + 
								"</div>\n" + 
								"\n" + 
								"</div>\n" + 
								"<center><br />\n" + 
								"<font size=\"1\">" +
								"        Page generated in " +
								(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
								" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
								"        Server Local Time: " +
								DataStore.refDate.toString() +
								"<br></font></center>" +
								"</body>\n" + 
								"</html>\n";
					}
					else if (receiveMessage.contains("GET /report_bullying.html"))
					{
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									String yearSelectionText = "";
									Calendar c = Calendar.getInstance();
									for(int i = 2000; i < (c.get(Calendar.YEAR) + 1); i++)
									{
										yearSelectionText += "  				<option value=\"" + i +"\">"+ i +"</option>\n";
									}
									s += "\r\n" +
											"<!DOCTYPE HTML>\n" + 
											"<html>\n" + 
											"<head>\n" + 
											"	<meta charset='utf-8'>\n" + 
											"	<title>Report an incident</title> \n" + 
											"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
											"	<link rel=\"top\" title=\"Report an incident\" href=\"/\">			\n" + 
											"	<style type=\"text/css\">\n" + 
											"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
											"			font-family:verdana,sans-serif;\n" + 
											"			color:white;\n" + 
											"			margin:0;\n" + 
											"			padding:0;\n" + 
											"			background:none;\n" + 
											"		}\n" + 
											"\n" + 
											"		body {\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-color:#012e57;\n" + 
											"		}\n" + 
											"\n" + 
											"		div#content2 {\n" + 
											"			text-align: center;\n" + 
											"			position:absolute;\n" + 
											"			top:28em;\n" + 
											"			left:0;\n" + 
											"			right:0;\n" + 
											"		}\n" + 
											"\n" + 
											"		.mbox {\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			margin-left: auto;\n" + 
											"			margin-right: auto;\n" + 
											"			margin-top:10px;\n" + 
											"			margin-bottom:10px;\n" + 
											"			padding:2px 0px;\n" + 
											"			width:480px;\n" + 
											"			border-radius: 5px;\n" + 
											"			box-shadow: 0px 0px 5px #000;\n" + 
											"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
											"		}\n" + 
											"\n" + 
											"		#searchbox { padding-bottom:5px; }\n" + 
											"		#searchbox3 { font-size: 80%; }\n" + 
											"		#searchbox4 { font-size: 60%; }\n" + 
											"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" + 
											"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}	" +
											"	</style>\n" + 
											"</head>\n" + 
											"<body>\n" + 
											"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
											"    <tr>\n" + 
											"        <td class=\"header_bkg\">\n" + 
											"            <ul>\n" + 
											"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
											"            </ul>\n" + 
											"        </td>\n" + 
											"    </tr>\n" + 
											"</table>\n" +
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<div id='searchbox3' class='mbox'>\n" + 
											"	<center>\n" + 
											"	<p>\n" + 
											"	<strong>Report a bullying incident</strong>" +
											"<br><br>We're here for you. Please fill out the form below and we'll be there to help you shortly.<br><br>" +
											"	</p>\n" +
											"	</center>\n" +
											"	<div class=\"contentcontainer med left\" style=\"margin-left: 50px;\">"+
											"		<form action=\"/submit_bullying_report.html\" method=\"get\">\n" + 
											"			<center><strong>Basic Information</strong><br></center>\n" + 
											"  			(*) Subject:<br>\n" + 
											"  			<input type=\"text\" name=\"subject\" required><br>\n" + 
											"  			(*) School:<br>\n" + 
											"  			<input type=\"text\" name=\"schoolname\" required><br>\n" + 
											"  			(*) Did an injury result from this incident?<br>\n" + 
											"  			<input type=\"radio\" name=\"injuryYN\" value=\"y\"> Yes<br>\n" + 
											"  			<input type=\"radio\" name=\"injuryYN\" value=\"n\" checked> No<br>\n" + 
											"  			(*) Was the target absent from school as a result of the incident?<br>\n" + 
											"  			<input type=\"radio\" name=\"absentYN\" value=\"y\"> Yes<br>\n" + 
											"  			<input type=\"radio\" name=\"absentYN\" value=\"n\" checked> No<br>\n" +
											"  			School adults you've already contacted (if any):<br>\n" + 
											"  			<input type=\"text\" name=\"adultscontacted\"><br>\n" + 
											"  			Describe any physical injuries:<br>\n" + 
											"			<textarea id=\"injuryDescrip\" class=\"text\" cols=\"43\" rows =\"3\" name=\"injuryDescrip\"></textarea><br>\n" +
											"  			How did you learn about the bullying?:<br>\n" + 
											"			<textarea id=\"learnMethodDescrip\" class=\"text\" cols=\"43\" rows =\"3\" name=\"learnMethodDescrip\"></textarea><br>\n" +
											"  			(*) Incident Date: <br>\n" +
											"			<select name=\"month\">\n" + 
											"  				<option value=\"1\">January</option>\n" + 
											"  				<option value=\"2\">February</option>\n" + 
											"  				<option value=\"3\">March</option>\n" + 
											"  				<option value=\"4\">April</option>\n" + 
											"  				<option value=\"5\">May</option>\n" + 
											"  				<option value=\"6\">June</option>\n" + 
											"  				<option value=\"7\">July</option>\n" + 
											"  				<option value=\"8\">August</option>\n" + 
											"  				<option value=\"9\">September</option>\n" + 
											"  				<option value=\"10\">October</option>\n" + 
											"  				<option value=\"11\">November</option>\n" + 
											"  				<option value=\"12\">December</option>\n" + 
											"			</select>/" + 
											"			<select name=\"date\">\n" + 
											"  				<option value=\"1\">1</option>\n" + 
											"  				<option value=\"2\">2</option>\n" + 
											"  				<option value=\"3\">3</option>\n" + 
											"  				<option value=\"4\">4</option>\n" + 
											"  				<option value=\"5\">5</option>\n" + 
											"  				<option value=\"6\">6</option>\n" + 
											"  				<option value=\"7\">7</option>\n" + 
											"  				<option value=\"8\">8</option>\n" + 
											"  				<option value=\"9\">9</option>\n" + 
											"  				<option value=\"10\">10</option>\n" + 
											"  				<option value=\"11\">11</option>\n" + 
											"  				<option value=\"12\">12</option>\n" + 
											"  				<option value=\"13\">13</option>\n" + 
											"  				<option value=\"14\">14</option>\n" + 
											"  				<option value=\"15\">15</option>\n" + 
											"  				<option value=\"16\">16</option>\n" + 
											"  				<option value=\"17\">17</option>\n" + 
											"  				<option value=\"18\">18</option>\n" + 
											"  				<option value=\"19\">19</option>\n" + 
											"  				<option value=\"20\">20</option>\n" + 
											"  				<option value=\"21\">21</option>\n" + 
											"  				<option value=\"22\">22</option>\n" + 
											"  				<option value=\"23\">23</option>\n" + 
											"  				<option value=\"24\">24</option>\n" + 
											"  				<option value=\"25\">25</option>\n" + 
											"  				<option value=\"26\">26</option>\n" + 
											"  				<option value=\"27\">27</option>\n" + 
											"  				<option value=\"28\">28</option>\n" + 
											"  				<option value=\"29\">29</option>\n" + 
											"  				<option value=\"30\">30</option>\n" + 
											"  				<option value=\"31\">31</option>\n" + 
											"			</select>/" +
											"			<select name=\"year\">\n" + 
											yearSelectionText +
											"			</select>\n<br>\n" + 
											"  			(*) Targeted student(s):<br>\n" + 
											"  			<input type=\"text\" name=\"bullied_students\" required><br>\n" + 
											"  			(*) Bully/ies:<br>\n" + 
											"  			<input type=\"text\" name=\"bullies\" required><br>\n" + 
											"  			<br>" +
											"			<center><strong>Reporting person</strong><br></center>\n" + 
											"  			(*) Phone Number:<br>\n" + 
											"  			<input type=\"text\" name=\"phone\" required><br>\n" + 
											"  			(*) Email:<br>\n" + 
											"  			<input type=\"text\" name=\"email\" required><br>\n" + 
											"  			(*) Name:<br>\n" + 
											"  			<input type=\"text\" name=\"name\" required><br>\n" + 
											"			<br>" +
											"			<center><strong>Incident Description</strong><br></center>\n" + 
											"  			(*) Give a summary of the incident:<br>\n" + 
											"			<textarea id=\"incidentDescription\" class=\"text\" cols=\"43\" rows =\"6\" name=\"incidentDescription\"></textarea><br>\n" +
											"  			(*) Where did this incident happen?:<br>\n" + 
											"  			<input type=\"text\" name=\"incidentLocation\" required><br>\n" + 
											"  			Why did this incident happen?:<br>\n" + 
											"  			<input type=\"text\" name=\"incidentReason\"><br>\n" + 
											"  			Any Witness(es)?:<br>\n" + 
											"  			<input type=\"text\" name=\"witnesses\"><br>\n" + 
											" 			<input type=\"checkbox\" name=\"anonymous\" value=\"y\"> Submit this anonymously<br>" +
											"			<br><br><center><input type=\"submit\" value=\"Submit Report\"></center>" +
											"		</form>" +
											"	</div>" +
											"  </div>\n" +
											" </div>" +
											"</div>\n" + 
											"<br><br><br><br><center>Warning: False reports are <strong>illegal</strong> and will be <strong>aggressively investigated.</strong></center><br>" +
											"" +
											"\n" + 
											"</div>\n" + 
											"<center><br />\n" + 
											"<font size=\"1\">" +
											"        Page generated in " +
											(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
											" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
											"        Server Local Time: " +
											DataStore.refDate.toString() +
											"<br></font></center><br><br><br><br>" +
											"</body>\n" + 
											"</html>\n";
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else if (receiveMessage.contains("GET /report_general_concern.html"))
					{
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									String yearSelectionText = "";
									Calendar c = Calendar.getInstance();
									for(int i = 2000; i < (c.get(Calendar.YEAR) + 1); i++)
									{
										yearSelectionText += "  				<option value=\"" + i +"\">"+ i +"</option>\n";
									}
									s += "\r\n" +
											"<!DOCTYPE HTML>\n" + 
											"<html>\n" + 
											"<head>\n" + 
											"	<meta charset='utf-8'>\n" + 
											"	<title>Report an incident</title> \n" + 
											"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
											"	<link rel=\"top\" title=\"Report an incident\" href=\"/\">			\n" + 
											"	<style type=\"text/css\">\n" + 
											"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
											"			font-family:verdana,sans-serif;\n" + 
											"			color:white;\n" + 
											"			margin:0;\n" + 
											"			padding:0;\n" + 
											"			background:none;\n" + 
											"		}\n" + 
											"\n" + 
											"		body {\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-color:#012e57;\n" + 
											"		}\n" + 
											"\n" + 
											"		div#content2 {\n" + 
											"			text-align: center;\n" + 
											"			position:absolute;\n" + 
											"			top:28em;\n" + 
											"			left:0;\n" + 
											"			right:0;\n" + 
											"		}\n" + 
											"\n" + 
											"		.mbox {\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			margin-left: auto;\n" + 
											"			margin-right: auto;\n" + 
											"			margin-top:10px;\n" + 
											"			margin-bottom:10px;\n" + 
											"			padding:2px 0px;\n" + 
											"			width:480px;\n" + 
											"			border-radius: 5px;\n" + 
											"			box-shadow: 0px 0px 5px #000;\n" + 
											"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
											"		}\n" + 
											"\n" + 
											"		#searchbox { padding-bottom:5px; }\n" + 
											"		#searchbox3 { font-size: 80%; }\n" + 
											"		#searchbox4 { font-size: 60%; }\n" + 
											"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" + 
											"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}	" +
											"	</style>\n" + 
											"</head>\n" + 
											"<body>\n" + 
											"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
											"    <tr>\n" + 
											"        <td class=\"header_bkg\">\n" + 
											"            <ul>\n" + 
											"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
											"            </ul>\n" + 
											"        </td>\n" + 
											"    </tr>\n" + 
											"</table>\n" +
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<div id='searchbox3' class='mbox'>\n" + 
											"	<center>\n" + 
											"	<p>\n" + 
											"	<strong>Report general concern</strong>" +
											"<br><br>Please fill in the form below and click <strong>Submit Report</strong>.<br><br>" +
											"	</p>\n" +
											"	</center>\n" +
											"	<div class=\"contentcontainer med left\" style=\"margin-left: 50px;\">"+
											"		<form action=\"/submit_general_concern.html\" method=\"get\">\n" + 
											"			<center><strong>Basic Information</strong><br></center>\n" + 
											"  			(*) Subject:<br>\n" + 
											"  			<input type=\"text\" name=\"subject\" required><br>\n" + 
											"  			(*) School:<br>\n" + 
											"  			<input type=\"text\" name=\"schoolname\" required><br>\n" + 
											"  			(*) Incident Date: <br>\n" +
											"			<select name=\"month\">\n" + 
											"  				<option value=\"1\">January</option>\n" + 
											"  				<option value=\"2\">February</option>\n" + 
											"  				<option value=\"3\">March</option>\n" + 
											"  				<option value=\"4\">April</option>\n" + 
											"  				<option value=\"5\">May</option>\n" + 
											"  				<option value=\"6\">June</option>\n" + 
											"  				<option value=\"7\">July</option>\n" + 
											"  				<option value=\"8\">August</option>\n" + 
											"  				<option value=\"9\">September</option>\n" + 
											"  				<option value=\"10\">October</option>\n" + 
											"  				<option value=\"11\">November</option>\n" + 
											"  				<option value=\"12\">December</option>\n" + 
											"			</select>/" + 
											"			<select name=\"date\">\n" + 
											"  				<option value=\"1\">1</option>\n" + 
											"  				<option value=\"2\">2</option>\n" + 
											"  				<option value=\"3\">3</option>\n" + 
											"  				<option value=\"4\">4</option>\n" + 
											"  				<option value=\"5\">5</option>\n" + 
											"  				<option value=\"6\">6</option>\n" + 
											"  				<option value=\"7\">7</option>\n" + 
											"  				<option value=\"8\">8</option>\n" + 
											"  				<option value=\"9\">9</option>\n" + 
											"  				<option value=\"10\">10</option>\n" + 
											"  				<option value=\"11\">11</option>\n" + 
											"  				<option value=\"12\">12</option>\n" + 
											"  				<option value=\"13\">13</option>\n" + 
											"  				<option value=\"14\">14</option>\n" + 
											"  				<option value=\"15\">15</option>\n" + 
											"  				<option value=\"16\">16</option>\n" + 
											"  				<option value=\"17\">17</option>\n" + 
											"  				<option value=\"18\">18</option>\n" + 
											"  				<option value=\"19\">19</option>\n" + 
											"  				<option value=\"20\">20</option>\n" + 
											"  				<option value=\"21\">21</option>\n" + 
											"  				<option value=\"22\">22</option>\n" + 
											"  				<option value=\"23\">23</option>\n" + 
											"  				<option value=\"24\">24</option>\n" + 
											"  				<option value=\"25\">25</option>\n" + 
											"  				<option value=\"26\">26</option>\n" + 
											"  				<option value=\"27\">27</option>\n" + 
											"  				<option value=\"28\">28</option>\n" + 
											"  				<option value=\"29\">29</option>\n" + 
											"  				<option value=\"30\">30</option>\n" + 
											"  				<option value=\"31\">31</option>\n" + 
											"			</select>/" +
											"			<select name=\"year\">\n" + 
											yearSelectionText +
											"			</select>\n<br>\n" +
											"  			(*) Give a summary of the incident:<br>\n" + 
											"			<textarea id=\"incidentDescription\" class=\"text\" cols=\"43\" rows =\"6\" name=\"incidentDescription\"></textarea><br>\n" +     
											"			<center><strong>Reporting person</strong><br></center>\n" + 
											"  			(*) Phone Number:<br>\n" + 
											"  			<input type=\"text\" name=\"phone\" required><br>\n" + 
											"  			(*) Email:<br>\n" + 
											"  			<input type=\"text\" name=\"email\" required><br>\n" + 
											"  			(*) Name:<br>\n" + 
											"  			<input type=\"text\" name=\"name\" required><br>\n" + 
											" 			<input type=\"checkbox\" name=\"anonymous\" value=\"y\"> Submit this anonymously<br>" +
											"			<br>" +
											"			<br><br><center><input type=\"submit\" value=\"Submit Report\"></center>" +
											"		</form>" +
											"	</div>" +
											"  </div>\n" +
											" </div>" +
											"</div>\n" + 
											"<br><br><br><br><center>Warning: False reports are <strong>illegal</strong> and will be <strong>aggressively investigated.</strong></center><br>" +
											"" +
											"\n" + 
											"</div>\n" + 
											"<center><br />\n" + 
											"<font size=\"1\">" +
											"        Page generated in " +
											(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
											" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
											"        Server Local Time: " +
											DataStore.refDate.toString() +
											"<br></font></center><br><br><br><br>" +
											"</body>\n" + 
											"</html>\n";
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else if (receiveMessage.contains("GET /submit_bullying_report.html"))
					{
						int p1 = receiveMessage.indexOf("user_authtokken=");
						String token = "";
						if (p1 == -1)
						{
							requestUserLogin(start);
						}
						else
						{
							token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
							if (DataStore.authenticated.containsKey(token))
							{
								User tmpusr = DataStore.authenticated.get(token);
								String username = tmpusr.getUsername();
								int accountType = tmpusr.getAccountType();
								// parse the form and return an "Incident Submitted" page regardless of account type.
								try
								{
									String filteredString = receiveMessage.substring(receiveMessage.indexOf("?")+1);
									String[] ari = filteredString.split("&");
									String subject = URLDecoder.decode(ari[0].substring(ari[0].indexOf("=") + 1), "UTF-8");
									String schoolname = URLDecoder.decode(ari[1].substring(ari[1].indexOf("=") + 1), "UTF-8");
									String injuryYN = ari[2].substring(ari[2].indexOf("=") + 1);
									String absentYN = ari[3].substring(ari[3].indexOf("=") + 1);
									String adultscontacted = URLDecoder.decode(ari[4].substring(ari[4].indexOf("=") + 1), "UTF-8");
									String injurydescription = URLDecoder.decode(ari[5].substring(ari[5].indexOf("=") + 1), "UTF-8");
									String learnmethoddescription = URLDecoder.decode(ari[6].substring(ari[6].indexOf("=") + 1), "UTF-8");
									String month = ari[7].substring(ari[7].indexOf("=") + 1);
									String date = ari[8].substring(ari[8].indexOf("=") + 1);
									String year = ari[9].substring(ari[9].indexOf("=") + 1);
									String bulliedstudents = URLDecoder.decode(ari[10].substring(ari[10].indexOf("=") + 1), "UTF-8");
									String bullies = URLDecoder.decode(ari[11].substring(ari[11].indexOf("=") + 1), "UTF-8");
									String phone = URLDecoder.decode(ari[12].substring(ari[12].indexOf("=") + 1), "UTF-8");
									String email = URLDecoder.decode(ari[13].substring(ari[13].indexOf("=") + 1), "UTF-8");
									String name = URLDecoder.decode(ari[14].substring(ari[14].indexOf("=") + 1), "UTF-8");
									String incidentDescription = URLDecoder.decode(ari[15].substring(ari[15].indexOf("=") + 1), "UTF-8");
									String incidentLocation = URLDecoder.decode(ari[16].substring(ari[16].indexOf("=") + 1), "UTF-8");
									String incidentReason = URLDecoder.decode(ari[17].substring(ari[17].indexOf("=") + 1), "UTF-8");
									String witnesses = "";
									boolean anonymous = false;
									try
									{
										if (URLDecoder.decode(ari[19], "UTF-8").contains("anonymous=y"))
										{
											anonymous = true;
										}
									}
									catch (ArrayIndexOutOfBoundsException arg1)
									{
										anonymous = false;
									}
									if (anonymous)
									{
										witnesses = URLDecoder.decode(ari[18].substring(ari[18].indexOf("=") + 1), "UTF-8");
									}
									else
									{
										witnesses = URLDecoder.decode(ari[18].substring(ari[18].indexOf("=") + 1, ari[18].indexOf(" HTTP/1.1")), "UTF-8");
									}
									Calendar cal = Calendar.getInstance();
									cal.set(Calendar.YEAR, Integer.parseInt(year));
									cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
									cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
									Date d8 = cal.getTime();
									Report bullyReport = new Report(DataStore.incidentdb.getNextReportID(), schoolname, 0, subject, anonymous, name, email, phone, injuryYN.matches("y"), absentYN.matches("y"), adultscontacted, injurydescription, learnmethoddescription, incidentReason, d8, bulliedstudents, bullies, incidentDescription, incidentLocation, witnesses);	
									DataStore.incidentdb.addReport(bullyReport);
									// DataStore.reportList.add(bullyReport);
									s += "\r\n" +
											"<!DOCTYPE HTML>\n" + 
											"<html>\n" + 
											"<head>\n" + 
											"	<meta charset='utf-8'>\n" + 
											"	<title>Bullying Report Submitted</title> \n" + 
											"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
											"	<link rel=\"top\" title=\"Bullying Report Submitted\" href=\"/\">			\n" + 
											"	<style type=\"text/css\">\n" + 
											"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
											"			font-family:verdana,sans-serif;\n" + 
											"			color:white;\n" + 
											"			margin:0;\n" + 
											"			padding:0;\n" + 
											"			background:none;\n" + 
											"		}\n" + 
											"\n" + 
											"		body {\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-color:#012e57;\n" + 
											"		}\n" + 
											"\n" + 
											"		div#content2 {\n" + 
											"			text-align: center;\n" + 
											"			position:absolute;\n" + 
											"			top:28em;\n" + 
											"			left:0;\n" + 
											"			right:0;\n" + 
											"		}\n" + 
											"\n" + 
											"		.mbox {\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			margin-left: auto;\n" + 
											"			margin-right: auto;\n" + 
											"			margin-top:10px;\n" + 
											"			margin-bottom:10px;\n" + 
											"			padding:2px 0px;\n" + 
											"			width:480px;\n" + 
											"			border-radius: 5px;\n" + 
											"			box-shadow: 0px 0px 5px #000;\n" + 
											"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
											"		}\n" + 
											"\n" + 
											"		#searchbox { padding-bottom:5px; }\n" + 
											"		#searchbox3 { font-size: 80%; }\n" + 
											"		#searchbox4 { font-size: 60%; }\n" + 
											"	</style>\n" + 
											"</head>\n" + 
											"<body>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<div id='searchbox3' class='mbox'>\n" + 
											"	<center>\n" + 
											"	<p>\n" + 
											"	<strong>Report Submitted Successfully</strong><br><br>Your request has been submitted successfully.<br>Thank you for using BullyStoppers.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
											"	</p>\n" + 
											"	</center>\n" + 
											"</div>\n" + 
											"<br><br>" +
											"" +
											"\n" + 
											"</div>\n" + 
											"<center><br />\n" + 
											"<font size=\"1\">" +
											"        Page generated in " +
											(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
											" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
											"        Server Local Time: " +
											DataStore.refDate.toString() +
											"<br></font></center><br><br><br><br>" +
											"</body>\n" + 
											"</html>\n";
								}
								catch (Exception e)
								{
									// serve a "something went wrong, go back here" page
									e.printStackTrace();
									send400(start);
								}
							}
							else
							{
								requestUserLogin(start);
							}
						}
					}
					else if (receiveMessage.contains("GET /submit_general_concern.html"))
					{
						int p1 = receiveMessage.indexOf("user_authtokken=");
						String token = "";
						if (p1 == -1)
						{
							requestUserLogin(start);
						}
						else
						{
							token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
							if (DataStore.authenticated.containsKey(token))
							{
								User tmpusr = DataStore.authenticated.get(token);
								String username = tmpusr.getUsername();
								int accountType = tmpusr.getAccountType();
								// parse the form and return an "Incident Submitted" page regardless of account type.
								try
								{
									String filteredString = receiveMessage.substring(receiveMessage.indexOf("?")+1);
									String[] ari = filteredString.split("&");
									String subject = URLDecoder.decode(ari[0].substring(ari[0].indexOf("=") + 1), "UTF-8");
									String schoolname = URLDecoder.decode(ari[1].substring(ari[1].indexOf("=") + 1), "UTF-8");
									String month = ari[2].substring(ari[2].indexOf("=") + 1);
									String date = ari[3].substring(ari[3].indexOf("=") + 1);
									String year = ari[4].substring(ari[4].indexOf("=") + 1);
									String incidentDescription = URLDecoder.decode(ari[5].substring(ari[5].indexOf("=") + 1), "UTF-8");
									String phone = URLDecoder.decode(ari[6].substring(ari[6].indexOf("=") + 1), "UTF-8");
									String email = URLDecoder.decode(ari[7].substring(ari[7].indexOf("=") + 1), "UTF-8");
									String name = URLDecoder.decode(ari[8].substring(ari[8].indexOf("=") + 1, ari[8].indexOf("HTTP/1.1") - 1), "UTF-8");
									boolean anonymous = false;
									try
									{
										if (URLDecoder.decode(ari[18].substring(ari[18].indexOf("=") + 1), "UTF-8").contains("anonymous=y"))
										{
											anonymous = true;
										}
									}
									catch (ArrayIndexOutOfBoundsException arg1)
									{
										anonymous = false;
									}
									Calendar cal = Calendar.getInstance();
									cal.set(Calendar.YEAR, Integer.parseInt(year));
									cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
									cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
									Date d8 = cal.getTime();
									Report bullyReport = new Report(DataStore.incidentdb.getNextReportID(), schoolname, 1, subject, anonymous, name, email, phone, incidentDescription, d8);								
									DataStore.incidentdb.addReport(bullyReport);
									s += "\r\n" +
											"<!DOCTYPE HTML>\n" + 
											"<html>\n" + 
											"<head>\n" + 
											"	<meta charset='utf-8'>\n" + 
											"	<title>Bullying Report Submitted</title> \n" + 
											"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
											"	<link rel=\"top\" title=\"Bullying Report Submitted\" href=\"/\">			\n" + 
											"	<style type=\"text/css\">\n" + 
											"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
											"			font-family:verdana,sans-serif;\n" + 
											"			color:white;\n" + 
											"			margin:0;\n" + 
											"			padding:0;\n" + 
											"			background:none;\n" + 
											"		}\n" + 
											"\n" + 
											"		body {\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-color:#012e57;\n" + 
											"		}\n" + 
											"\n" + 
											"		div#content2 {\n" + 
											"			text-align: center;\n" + 
											"			position:absolute;\n" + 
											"			top:28em;\n" + 
											"			left:0;\n" + 
											"			right:0;\n" + 
											"		}\n" + 
											"\n" + 
											"		.mbox {\n" + 
											"			background-repeat:no-repeat;\n" + 
											"			background-attachment:fixed;\n" + 
											"			background-position:50% 0%;\n" + 
											"			margin-left: auto;\n" + 
											"			margin-right: auto;\n" + 
											"			margin-top:10px;\n" + 
											"			margin-bottom:10px;\n" + 
											"			padding:2px 0px;\n" + 
											"			width:480px;\n" + 
											"			border-radius: 5px;\n" + 
											"			box-shadow: 0px 0px 5px #000;\n" + 
											"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
											"		}\n" + 
											"\n" + 
											"		#searchbox { padding-bottom:5px; }\n" + 
											"		#searchbox3 { font-size: 80%; }\n" + 
											"		#searchbox4 { font-size: 60%; }\n" + 
											"	</style>\n" + 
											"</head>\n" + 
											"<body>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<br>\n" + 
											"<div id='searchbox3' class='mbox'>\n" + 
											"	<center>\n" + 
											"	<p>\n" + 
											"	<strong>Report Submitted Successfully</strong><br><br>Your request has been submitted successfully.<br>Thank you for using BullyStoppers.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
											"	</p>\n" + 
											"	</center>\n" + 
											"</div>\n" + 
											"<br><br>" +
											"" +
											"\n" + 
											"</div>\n" + 
											"<center><br />\n" + 
											"<font size=\"1\">" +
											"        Page generated in " +
											(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
											" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
											"        Server Local Time: " +
											DataStore.refDate.toString() +
											"<br></font></center><br><br><br><br>" +
											"</body>\n" + 
											"</html>\n";
								}
								catch (Exception e)
								{
									// serve a "something went wrong, go back here" page
									e.printStackTrace();
									send400(start);
								}
							}
							else
							{
								requestUserLogin(start);
							}
						}
					}
					else if (receiveMessage.contains("GET /view_incidents.html"))
					{
						// TODO: Check if the user is authorized to view the incident list.
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									if (accountType == 1 || accountType == 2)
									{
										// teacher page
										// return this page if teacher in active directory group
										String generatedIncidentList = "";
										ArrayList<Report> al = DataStore.incidentdb.queryReports();
										for(int i = 0; i < al.size(); i++)
										{
											Report rep = al.get(i);
											if(rep.isOpen())
											{
												Date incidentDate = rep.getIncidentDate();
												Calendar cal = Calendar.getInstance();
												cal.setTime(incidentDate);
												int month = cal.get(Calendar.MONTH) + 1;
												int date = cal.get(Calendar.DAY_OF_MONTH);
												int year = cal.get(Calendar.YEAR);
												generatedIncidentList += 
																"<center><div id=\"searchbox\" class='mbox'>\n" +
																"	<div class=\"contentcontainer med left\" style=\"margin-left: 50px;\">";
												if (rep.getIncidentType() == 0)
												{
													// bullying
													generatedIncidentList += 
													"		<strong>Type: Bullying Incident</strong><br>\n" +
													"		<strong>Subject: " + rep.getSubject() +"</strong><br>\n" +
													"		<strong>School: " + rep.getSchool() +"</strong><br>\n" +
													"		Incident Date: " + month + "/" + date + "/" + year;
												}
												else if (rep.getIncidentType() == 1)
												{
													// general incident
													generatedIncidentList += 
													"		<strong>Type: General Concern</strong><br>\n" +
													"		<strong>Subject: " + rep.getSubject() +"</strong><br>\n" +
													"		<strong>School: " + rep.getSchool() +"</strong><br>\n" +
													"		Incident Date: " + month + "/" + date + "/" + year + "<br>";
												}
												generatedIncidentList += 
													"		<br><a href=\"/view_ticket.html?id=" + rep.getID() + "\" title=\"View the ticket\"><font color=\"FF00CC\">[ View the ticket here... ]</font></a>";
												generatedIncidentList += 
																"	</div>" + 
																"</div></center>";
											}
										}
										s += "\r\n" + 
												"<!DOCTYPE HTML>\n" + 
												"<html>\n" + 
												"<head>\n" + 
												"	<meta charset='utf-8'>\n" + 
												"	<title>BullyStoppers Reports</title> \n" + 
												"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
												"	<link rel=\"top\" title=\"BullyStoppers Reports\" href=\"/\">			\n" + 
												"	<style type=\"text/css\">\n" + 
												"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
												"			font-family:verdana,sans-serif;\n" + 
												"			color:white;\n" + 
												"			margin:0;\n" + 
												"			padding:0;\n" + 
												"			background:none;\n" + 
												"		}\n" + 
												"\n" + 
												"		body {\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-color:#012e57;\n" + 
												"		}\n" + 
												"\n" + 
												"		div#content2 {\n" + 
												"			text-align: center;\n" + 
												"			position:absolute;\n" + 
												"			top:28em;\n" + 
												"			left:0;\n" + 
												"			right:0;\n" + 
												"		}\n" + 
												"\n" + 
												"    	.center-td {\n" + 
												"        	text-align: center;\n" + 
												"    	}\n" +
												"\n" +
												"		.mbox {\n" + 
												"			background-repeat:no-repeat;\n" + 
												"			background-attachment:fixed;\n" + 
												"			background-position:50% 0%;\n" + 
												"			margin-left: auto;\n" + 
												"			margin-right: auto;\n" + 
												"			margin-top:10px;\n" + 
												"			margin-bottom:10px;\n" + 
												"			padding:2px 0px;\n" + 
												"			width:480px;\n" + 
												"			border-radius: 5px;\n" + 
												"			box-shadow: 0px 0px 5px #000;\n" + 
												"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
												"		}\n" + 
												"\n" + 
												"		#searchbox { padding-bottom:5px; }\n" + 
												"		#searchbox3 { font-size: 80%; }\n" + 
												"		#searchbox4 { font-size: 60%; }\n" + 
												"\n" +
												"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" +
												"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}" +
												"	</style>\n" + 
												"</head>\n" + 
												"<body>\n" +
												"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
												"    <tr>\n" + 
												"        <td class=\"header_bkg\">\n" + 
												"            <ul>\n" + 
												"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
												"            </ul>\n" + 
												"        </td>\n" + 
												"    </tr>\n" + 
												"</table>" + // osdjoof
												"	<div id=\"notices\">\n" + 
												"		\n" + 
												"			<div id=\"notice\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"warning\" style=\"display: none;\"></div>\n" + 
												"		\n" + 
												"\n" + 
												"		\n" + 
												"			<div id=\"error\" style=\"display:none;\">\n" + 
												"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
												"				<p></p>\n" + 
												"			</div>\n" + 
												"		\n" + 
												"	</div>\n<br><br><br><br>" +
												generatedIncidentList +
												"</div>\n" + 
												"<br>\n" +
												"<center><br />\n" + 
												"<font size=\"1\">" +
												"        Page generated in " +
												(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
												" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
												"        Server Local Time: " +
												DataStore.refDate.toString() +
												"<br></font></center>" +
												"</body>\n" + 
												"</html>\n";
									}
									else
									{
										sendAccessDenied(start);
									}
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else if (receiveMessage.contains("GET /view_ticket.html"))
					{
						// Chocolat.println(receiveMessage);
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									if (accountType == 1 || accountType == 2)
									{
										try
										{
											String idstr = receiveMessage.substring(receiveMessage.indexOf("=") + 1, receiveMessage.indexOf("HTTP/1.1") - 1);
											int reportID = Integer.parseInt(idstr);
											Report rep = DataStore.incidentdb.getReportById(reportID);
											Date incidentDate = rep.getIncidentDate();
											Calendar cal = Calendar.getInstance();
											cal.setTime(incidentDate);
											int month = cal.get(Calendar.MONTH) + 1;
											int date = cal.get(Calendar.DAY_OF_MONTH);
											int year = cal.get(Calendar.YEAR);
											String reportDescription = "";
											if (rep.getIncidentType() == 0)
											{
												reportDescription += ("<strong>Subject:</strong> " + rep.getSubject() + "<br>\n");
												reportDescription += ("<strong>Incident Type:</strong> Bullying<br>\n");
												reportDescription += ("<strong>Date of Incident:</strong> " + month + "/" + date + "/" + year + "<br>\n");
												reportDescription += ("<strong>School:</strong> " + rep.getSchool() + "<br>\n");
												reportDescription += ("<strong>Description:</strong> " + rep.getIncidentDescription() + "<br>\n");
												reportDescription += ("<strong>Injury Resulted (Y/N):</strong> " + (rep.injuryResulted() ? "Yes" : "No") + "<br>\n");
												reportDescription += ("<strong>Absence Resulted (Y/N):</strong> " + (rep.absenceResulted() ? "Yes" : "No") + "<br>\n");
												reportDescription += ("<strong>Adults Contacted:</strong> " + rep.getAdultsContacted() + "<br>\n");
												reportDescription += ("<strong>Injuries Sustained:</strong> " + rep.getInjuriesSustained() + "<br>\n");
												reportDescription += ("<strong>Method of knowledge:</strong> " + rep.getLearnedReason() + "<br>\n");
												reportDescription += ("<strong>Reason for bullying:</strong> " + rep.getBullyingReason() + "<br>\n");
												reportDescription += ("<strong>Students targeted:</strong> " + rep.getTargetedStudents() + "<br>\n");
												reportDescription += ("<strong>Incident location: </strong> " + rep.getIncidentLocation() + "<br>\n");
												reportDescription += ("<strong>Witness Names: </strong> " + rep.getWitnessNames() + "<br>\n");
												reportDescription += ("<strong>Anonymous? (Y/N):</strong> " + (rep.isAnonymous() ? "Yes" : "No") + "<br>\n");
												if (!rep.isAnonymous())
												{
													reportDescription += ("<strong>Reporter Name: </strong> " + rep.getReportingPersonName() + "<br>\n");
													reportDescription += ("<strong>Reporter Email: </strong> " + rep.getReportingPersonEmail() + "<br>\n");
													reportDescription += ("<strong>Reporter Phone: </strong> " + rep.getReportingPersonPhone() + "<br>\n");
												}
											}
											else if (rep.getIncidentType() == 1)
											{
												reportDescription += ("<strong>Subject:</strong> " + rep.getSubject() + "<br>\n");
												reportDescription += ("<strong>Incident Type:</strong> General Concern<br>\n");
												reportDescription += ("<strong>Date of Incident:</strong> " + month + "/" + date + "/" + year + "<br>\n");
												reportDescription += ("<strong>School:</strong> " + rep.getSchool() + "<br>\n");
												reportDescription += ("<strong>Description:</strong> " + rep.getIncidentDescription() + "<br>\n");
												reportDescription += ("<strong>Anonymous? (Y/N):</strong> " + (rep.isAnonymous() ? "Yes" : "No") + "<br>\n");
												if (!rep.isAnonymous())
												{
													reportDescription += ("<strong>Reporter Name: </strong> " + rep.getReportingPersonName() + "<br>\n");
													reportDescription += ("<strong>Reporter Email: </strong> " + rep.getReportingPersonEmail() + "<br>\n");
													reportDescription += ("<strong>Reporter Phone: </strong> " + rep.getReportingPersonPhone() + "<br>\n");
												}
											}
											s += "\r\n" + 
													"<!DOCTYPE HTML>\n" + 
													"<html>\n" + 
													"<head>\n" + 
													"	<meta charset='utf-8'>\n" + 
													"	<title>BullyStoppers Home</title> \n" + 
													"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
													"	<link rel=\"top\" title=\"BullyStoppers login\" href=\"/\">			\n" + 
													"	<style type=\"text/css\">\n" + 
													"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
													"			font-family:verdana,sans-serif;\n" + 
													"			color:white;\n" + 
													"			margin:0;\n" + 
													"			padding:0;\n" + 
													"			background:none;\n" + 
													"		}\n" + 
													"\n" + 
													"		body {\n" + 
													"			background-attachment:fixed;\n" + 
													"			background-position:50% 0%;\n" + 
													"			background-repeat:no-repeat;\n" + 
													"			background-color:#012e57;\n" + 
													"		}\n" + 
													"\n" + 
													"		div#content2 {\n" + 
													"			text-align: center;\n" + 
													"			position:absolute;\n" + 
													"			top:28em;\n" + 
													"			left:0;\n" + 
													"			right:0;\n" + 
													"		}\n" + 
													"\n" + 
													"    	.center-td {\n" + 
													"        	text-align: center;\n" + 
													"    	}\n" +
													"\n" +
													"		.mbox {\n" + 
													"			background-repeat:no-repeat;\n" + 
													"			background-attachment:fixed;\n" + 
													"			background-position:50% 0%;\n" + 
													"			margin-left: auto;\n" + 
													"			margin-right: auto;\n" + 
													"			margin-top:10px;\n" + 
													"			margin-bottom:10px;\n" + 
													"			padding:2px 0px;\n" + 
													"			width:480px;\n" + 
													"			border-radius: 5px;\n" + 
													"			box-shadow: 0px 0px 5px #000;\n" + 
													"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
													"		}\n" + 
													"\n" + 
													"		#searchbox { padding-bottom:5px; }\n" + 
													"		#searchbox3 { font-size: 80%; }\n" + 
													"		#searchbox4 { font-size: 60%; }\n" + 
													"\n" +
													"		.block-menu-top td{background-color:#202224} .header_bkg{background-color:#202224}\n" +
													"		li.noblock{padding-right:14px}ul.dropdown li.noblock a{display:inline-block;padding:7px 0}ul.dropdown li.noblock a:first-child{padding-left:14px}ul.dropdown li.noblock a:empty{display:none}" +
													"	</style>\n" + 
													"</head>\n" + 
													"<body>\n" +
													"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"block-menu-top\">\n" + 
													"    <tr>\n" + 
													"        <td class=\"header_bkg\">\n" + 
													"            <ul>\n" + 
													"                <li class=\"noblock\">Welcome, " + username +" | <a href=\"/logout.html\">Log out</a></li>\n" + 
													"            </ul>\n" + 
													"        </td>\n" + 
													"    </tr>\n" + 
													"</table>" + // osdjoof
													"	<div id=\"notices\">\n" + 
													"		\n" + 
													"			<div id=\"notice\" style=\"display:none;\">\n" + 
													"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
													"				<p></p>\n" + 
													"			</div>\n" + 
													"		\n" + 
													"\n" + 
													"		\n" + 
													"			<div id=\"warning\" style=\"display: none;\"></div>\n" + 
													"		\n" + 
													"\n" + 
													"		\n" + 
													"			<div id=\"error\" style=\"display:none;\">\n" + 
													"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
													"				<p></p>\n" + 
													"			</div>\n" + 
													"		\n" + 
													"	</div>\n<br><br><br><br>" +
													"</div>\n" + 
													"<br>\n" +
													"<center><div id=\"searchbox\" class='mbox'>\n" + 
													reportDescription +
													"</div></center>" +
													"<center><br />\n" + 
													"<center><div id=\"searchbox\" class='mbox'>\n" + 
													"	<a href=\"/close_ticket.html?id=" + reportID + "\" title=\"Close ticket\">[ Close this ticket ]</a>" +
													"</div></center>" +
													"<center><br />\n" + 
													"<font size=\"1\">" +
													"        Page generated in " +
													(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
													" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
													"        Server Local Time: " +
													DataStore.refDate.toString() +
													"<br></font></center>" +
													"</body>\n" + 
													"</html>\n";
										}
										catch (Exception e)
										{
											send400(start);
										}
										
									}
									else
									{
										sendAccessDenied(start);
									}
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else if (receiveMessage.contains("GET /close_ticket.html"))
					{
						// Chocolat.println(receiveMessage);
						if (receiveMessage.indexOf("Cookie: ") == -1)
						{
							// present the login page.
							requestUserLogin(start);
						}
						else
						{
							int p1 = receiveMessage.indexOf("user_authtokken=");
							String token = "";
							if (p1 == -1)
							{
								requestUserLogin(start);
							}
							else
							{
								token = receiveMessage.substring(p1 + 16, p1 + 16 + 36);
								if (DataStore.authenticated.containsKey(token))
								{
									User tmpusr = DataStore.authenticated.get(token);
									String username = tmpusr.getUsername();
									int accountType = tmpusr.getAccountType();
									if (accountType == 1 || accountType == 2)
									{
										try
										{
											String idstr = receiveMessage.substring(receiveMessage.indexOf("=") + 1, receiveMessage.indexOf("HTTP/1.1") - 1);
											int reportID = Integer.parseInt(idstr);
											Report rep = DataStore.incidentdb.getReportById(reportID);
											rep.closeReport();
											DataStore.incidentdb.closeReport(rep);
											s += "\r\n" +
													"<!DOCTYPE HTML>\n" + 
													"<html>\n" + 
													"<head>\n" + 
													"	<meta charset='utf-8'>\n" + 
													"	<title>Report Closed</title> \n" + 
													"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
													"	<link rel=\"top\" title=\"Report Closed\" href=\"/\">			\n" + 
													"	<style type=\"text/css\">\n" + 
													"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
													"			font-family:verdana,sans-serif;\n" + 
													"			color:white;\n" + 
													"			margin:0;\n" + 
													"			padding:0;\n" + 
													"			background:none;\n" + 
													"		}\n" + 
													"\n" + 
													"		body {\n" + 
													"			background-attachment:fixed;\n" + 
													"			background-position:50% 0%;\n" + 
													"			background-repeat:no-repeat;\n" + 
													"			background-color:#012e57;\n" + 
													"		}\n" + 
													"\n" + 
													"		div#content2 {\n" + 
													"			text-align: center;\n" + 
													"			position:absolute;\n" + 
													"			top:28em;\n" + 
													"			left:0;\n" + 
													"			right:0;\n" + 
													"		}\n" + 
													"\n" + 
													"		.mbox {\n" + 
													"			background-repeat:no-repeat;\n" + 
													"			background-attachment:fixed;\n" + 
													"			background-position:50% 0%;\n" + 
													"			margin-left: auto;\n" + 
													"			margin-right: auto;\n" + 
													"			margin-top:10px;\n" + 
													"			margin-bottom:10px;\n" + 
													"			padding:2px 0px;\n" + 
													"			width:480px;\n" + 
													"			border-radius: 5px;\n" + 
													"			box-shadow: 0px 0px 5px #000;\n" + 
													"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
													"		}\n" + 
													"\n" + 
													"		#searchbox { padding-bottom:5px; }\n" + 
													"		#searchbox3 { font-size: 80%; }\n" + 
													"		#searchbox4 { font-size: 60%; }\n" + 
													"	</style>\n" + 
													"</head>\n" + 
													"<body>\n" + 
													"<br>\n" + 
													"<br>\n" + 
													"<br>\n" + 
													"<br>\n" + 
													"<br>\n" + 
													"<br>\n" + 
													"<div id='searchbox3' class='mbox'>\n" + 
													"	<center>\n" + 
													"	<p>\n" + 
													"	<strong>Report ticket closed successfully</strong><br><br>Thank you for using BullyStoppers. The ticket with ID " + reportID + " was closed.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
													"	</p>\n" + 
													"	</center>\n" + 
													"</div>\n" + 
													"<br><br>" +
													"" +
													"\n" + 
													"</div>\n" + 
													"<center><br />\n" + 
													"<font size=\"1\">" +
													"        Page generated in " +
													(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
													" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
													"        Server Local Time: " +
													DataStore.refDate.toString() +
													"<br></font></center><br><br><br><br>" +
													"</body>\n" + 
													"</html>\n";
										}
										catch (Exception e)
										{
											send400(start);
										}
										
									}
									else
									{
										sendAccessDenied(start);
									}
								}
								else
								{
									requestUserLogin(start);
								}
							}
						}
					}
					else
					{
						send404(start);
					}
					bw.write(s);
					bw.close();
				}
			}
			catch (Exception e)
			{
				Chocolat.println("[" + st.elapsedTime() + "] ServerThread was interrupted: " + e);
				e.printStackTrace();
			}
		}
		catch (IOException ioe)
		{
			Chocolat.println("[" + st.elapsedTime() + "] ServerThread failed: " + ioe);
		}
	}
	
	private void requestUserLogin(double start)
	{
		s +=								s += "\r\n" + 
				"<!DOCTYPE HTML>\n" + 
				"<html>\n" + 
				"<head>\n" + 
				"	<meta charset='utf-8'>\n" + 
				"	<title>System Login -- BullyStoppers Reporting System</title> \n" + 
				"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
				"	<link rel=\"top\" title=\"BullyStoppers login\" href=\"/\">			\n" + 
				"	<style type=\"text/css\">\n" + 
				"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
				"			font-family:verdana,sans-serif;\n" + 
				"			color:white;\n" + 
				"			margin:0;\n" + 
				"			padding:0;\n" + 
				"			background:none;\n" + 
				"		}\n" + 
				"\n" + 
				"		body {\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-color:#012e57;\n" + 
				"		}\n" + 
				"\n" + 
				"		div#content2 {\n" + 
				"			text-align: center;\n" + 
				"			position:absolute;\n" + 
				"			top:28em;\n" + 
				"			left:0;\n" + 
				"			right:0;\n" + 
				"		}\n" + 
				"\n" + 
				"    	.center-td {\n" + 
				"        	text-align: center;\n" + 
				"    	}\n" +
				"\n" +
				"		.mbox {\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			margin-left: auto;\n" + 
				"			margin-right: auto;\n" + 
				"			margin-top:10px;\n" + 
				"			margin-bottom:10px;\n" + 
				"			padding:2px 0px;\n" + 
				"			width:480px;\n" + 
				"			border-radius: 5px;\n" + 
				"			box-shadow: 0px 0px 5px #000;\n" + 
				"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
				"		}\n" + 
				"\n" + 
				"		#searchbox { padding-bottom:5px; }\n" + 
				"		#searchbox3 { font-size: 80%; }\n" + 
				"		#searchbox4 { font-size: 60%; }\n" + 
				"	</style>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"	<div id=\"notices\">\n" + 
				"		\n" + 
				"			<div id=\"notice\" style=\"display:none;\">\n" + 
				"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
				"				<p></p>\n" + 
				"			</div>\n" + 
				"		\n" + 
				"\n" + 
				"		\n" + 
				"			<div id=\"warning\" style=\"display: none;\"></div>\n" + 
				"		\n" + 
				"\n" + 
				"		\n" + 
				"			<div id=\"error\" style=\"display:none;\">\n" + 
				"				<div class=\"closebutton\" onclick=\"noticeClose(this.parentNode);\">X</div>\n" + 
				"				<p></p>\n" + 
				"			</div>\n" + 
				"		\n" + 
				"	</div>\n" +
				"<br><br><br><br>\n" +
				"<div><center><h2><strong>Welcome to BullyStoppers!</strong></h2></center></div>\n" +
				"<br>\n" +
				"<center><div id=\"searchbox\" class='mbox'>\n" + 
				"		<h2>Login</h2>\n" + 
				"<div class='section' style=\"width:500px;\">\n" + 
				"\n" + 
				"			<form action=\"/login\" method=\"get\">\n" + 
				"			<center><table>\n" + 
				"				<tr>\n" + 
				"					<td><label for=\"user_name\">Username</label></td>\n" + 
				"					<td align=\"center\"><input id=\"user_name\" name=\"username\" size=\"30\" tabindex=\"1\" type=\"text\"></td>\n" + 
				"				</tr>\n" + 
				"				<tr>\n" + 
				"					<td><label for=\"user_password\">Password</label></td>\n" + 
				"					<td><input id=\"user_password\" name=\"password\" size=\"30\" tabindex=\"1\" type=\"password\"></td>\n" + 
				"				</tr>\n" +  
				"			</table>\n" +
				"				<input type=\"submit\" value=\"Login\">" +
				"			</center>\n" + 
				"		</form>\n" + 
				"</div></center>" +
				"<center><br />\n" + 
				"<font size=\"1\">" +
				"        Page generated in " +
				(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
				" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
				"        Server Local Time: " +
				DataStore.refDate.toString() +
				"<br></font></center>" +
				"</body>\n" + 
				"</html>\n";
	}
	
	private void send400(double start)
	{
		s += "\r\n" +
				"<!DOCTYPE HTML>\n" + 
				"<html>\n" + 
				"<head>\n" + 
				"	<meta charset='utf-8'>\n" + 
				"	<title>400: Bad Request</title> \n" + 
				"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
				"	<link rel=\"top\" title=\"400 Bad Request\" href=\"/\">			\n" + 
				"	<style type=\"text/css\">\n" + 
				"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
				"			font-family:verdana,sans-serif;\n" + 
				"			color:white;\n" + 
				"			margin:0;\n" + 
				"			padding:0;\n" + 
				"			background:none;\n" + 
				"		}\n" + 
				"\n" + 
				"		body {\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-color:#012e57;\n" + 
				"		}\n" + 
				"\n" + 
				"		div#content2 {\n" + 
				"			text-align: center;\n" + 
				"			position:absolute;\n" + 
				"			top:28em;\n" + 
				"			left:0;\n" + 
				"			right:0;\n" + 
				"		}\n" + 
				"\n" + 
				"		.mbox {\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			margin-left: auto;\n" + 
				"			margin-right: auto;\n" + 
				"			margin-top:10px;\n" + 
				"			margin-bottom:10px;\n" + 
				"			padding:2px 0px;\n" + 
				"			width:480px;\n" + 
				"			border-radius: 5px;\n" + 
				"			box-shadow: 0px 0px 5px #000;\n" + 
				"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
				"		}\n" + 
				"\n" + 
				"		#searchbox { padding-bottom:5px; }\n" + 
				"		#searchbox3 { font-size: 80%; }\n" + 
				"		#searchbox4 { font-size: 60%; }\n" + 
				"	</style>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<div id='searchbox3' class='mbox'>\n" + 
				"	<center>\n" + 
				"	<p>\n" + 
				"	<strong>ERROR 400: Bad Request</strong><br><br>We're sorry, something went wrong and we couldn't parse your request.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
				"	</p>\n" + 
				"	</center>\n" + 
				"</div>\n" + 
				"<br><br>" +
				"" +
				"\n" + 
				"</div>\n" + 
				"<center><br />\n" + 
				"<font size=\"1\">" +
				"        Page generated in " +
				(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
				" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
				"        Server Local Time: " +
				DataStore.refDate.toString() +
				"<br></font></center><br><br><br><br>" +
				"</body>\n" + 
				"</html>\n";
	}
	
	private void send404(double start)
	{
		s += "\r\n" +
				"<!DOCTYPE HTML>\n" + 
				"<html>\n" + 
				"<head>\n" + 
				"	<meta charset='utf-8'>\n" + 
				"	<title>404: Not Found</title> \n" + 
				"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
				"	<link rel=\"top\" title=\"404 Not Found\" href=\"/\">			\n" + 
				"	<style type=\"text/css\">\n" + 
				"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
				"			font-family:verdana,sans-serif;\n" + 
				"			color:white;\n" + 
				"			margin:0;\n" + 
				"			padding:0;\n" + 
				"			background:none;\n" + 
				"		}\n" + 
				"\n" + 
				"		body {\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-color:#012e57;\n" + 
				"		}\n" + 
				"\n" + 
				"		div#content2 {\n" + 
				"			text-align: center;\n" + 
				"			position:absolute;\n" + 
				"			top:28em;\n" + 
				"			left:0;\n" + 
				"			right:0;\n" + 
				"		}\n" + 
				"\n" + 
				"		.mbox {\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			margin-left: auto;\n" + 
				"			margin-right: auto;\n" + 
				"			margin-top:10px;\n" + 
				"			margin-bottom:10px;\n" + 
				"			padding:2px 0px;\n" + 
				"			width:480px;\n" + 
				"			border-radius: 5px;\n" + 
				"			box-shadow: 0px 0px 5px #000;\n" + 
				"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
				"		}\n" + 
				"\n" + 
				"		#searchbox { padding-bottom:5px; }\n" + 
				"		#searchbox3 { font-size: 80%; }\n" + 
				"		#searchbox4 { font-size: 60%; }\n" + 
				"	</style>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<div id='searchbox3' class='mbox'>\n" + 
				"	<center>\n" + 
				"	<p>\n" + 
				"	<strong>ERROR 404: Page Not Found.</strong><br><br>We're sorry, the page requested was not found on this server.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
				"	</p>\n" + 
				"	</center>\n" + 
				"</div>\n" + 
				"<br><br>" +
				"" +
				"\n" + 
				"</div>\n" + 
				"<center><br />\n" + 
				"<font size=\"1\">" +
				"        Page generated in " +
				(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
				" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
				"        Server Local Time: " +
				DataStore.refDate.toString() +
				"<br></font></center><br><br><br><br>" +
				"</body>\n" + 
				"</html>\n";
	}
	
	private void sendAccessDenied(double start)
	{
		s += "\r\n" +
				"<!DOCTYPE HTML>\n" + 
				"<html>\n" + 
				"<head>\n" + 
				"	<meta charset='utf-8'>\n" + 
				"	<title>Access Denied</title> \n" + 
				"    	<meta name=\"theme-color\" content=\"#00549e\">\n" + 
				"	<link rel=\"top\" title=\"Access Denied\" href=\"/\">			\n" + 
				"	<style type=\"text/css\">\n" + 
				"		body,div,h1,h2,h3,h4,h5,h6,p,ul,li,dd,dt {\n" + 
				"			font-family:verdana,sans-serif;\n" + 
				"			color:white;\n" + 
				"			margin:0;\n" + 
				"			padding:0;\n" + 
				"			background:none;\n" + 
				"		}\n" + 
				"\n" + 
				"		body {\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-color:#012e57;\n" + 
				"		}\n" + 
				"\n" + 
				"		div#content2 {\n" + 
				"			text-align: center;\n" + 
				"			position:absolute;\n" + 
				"			top:28em;\n" + 
				"			left:0;\n" + 
				"			right:0;\n" + 
				"		}\n" + 
				"\n" + 
				"		.mbox {\n" + 
				"			background-repeat:no-repeat;\n" + 
				"			background-attachment:fixed;\n" + 
				"			background-position:50% 0%;\n" + 
				"			margin-left: auto;\n" + 
				"			margin-right: auto;\n" + 
				"			margin-top:10px;\n" + 
				"			margin-bottom:10px;\n" + 
				"			padding:2px 0px;\n" + 
				"			width:480px;\n" + 
				"			border-radius: 5px;\n" + 
				"			box-shadow: 0px 0px 5px #000;\n" + 
				"			text-shadow:0px 0px 2px black, 0px 0px 6px black;\n" + 
				"		}\n" + 
				"\n" + 
				"		#searchbox { padding-bottom:5px; }\n" + 
				"		#searchbox3 { font-size: 80%; }\n" + 
				"		#searchbox4 { font-size: 60%; }\n" + 
				"	</style>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<br>\n" + 
				"<div id='searchbox3' class='mbox'>\n" + 
				"	<center>\n" + 
				"	<p>\n" + 
				"	<strong>Access Denied</strong><br><br>This resource cannot be served because your account does not have<br>the adequate permissions to access this resource.<br><br><a href=\"/index.html\" title=\"Homepage\">[ Back to home ]</a>" +
				"	</p>\n" + 
				"	</center>\n" + 
				"</div>\n" + 
				"<br><br>" +
				"" +
				"\n" + 
				"</div>\n" + 
				"<center><br />\n" + 
				"<font size=\"1\">" +
				"        Page generated in " +
				(double)((st.getElapsedNanoTime() - start)/ 1000000000.0) +
				" seconds [ 100% Java (BullyStoppers WebServer) ]       <br />\n" + 
				"        Server Local Time: " +
				DataStore.refDate.toString() +
				"<br></font></center><br><br><br><br>" +
				"</body>\n" + 
				"</html>\n";
	}
	
}