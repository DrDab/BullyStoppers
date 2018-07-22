package bullystoppersserver;

import java.util.LinkedList;
import java.util.Queue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	private Stopwatch st;
	
	private Queue<Report> toAdd = new LinkedList<Report>();
	private Queue<Request> toGet = new LinkedList<Request>();
	private Queue<Report> toClose = new LinkedList<Report>();
	 
	 // q.remove will remove the one last.
	 
	 private Connection con;
	 
	 
	 public IncidentDB(Stopwatch st)
	 {
		 this.st = st;
	 }
	 
	 public void initSQL(String filePath)
	 {
		 Chocolat.println("[" + st.elapsedTime() + "] Initializing SQLite connection with path " + filePath + ".");
		 try 
		 {
			Class.forName("org.sqlite.JDBC");
		 } 
		 catch (ClassNotFoundException e)
		 {
			e.printStackTrace();
		 }
		 
		 try
		 {
			 con = DriverManager.getConnection("jdbc:sqlite:" + filePath);
			 Statement statement = con.createStatement();
			 DatabaseMetaData dbm = con.getMetaData();
			 ResultSet tables = dbm.getTables(null, null, "incidents", null);
			 if(tables.next())
			 {
				 Chocolat.println("[" + st.elapsedTime() + "] SQLite table incidents found successfully!");
			 }
			 else
			 {
				 Chocolat.println("[" + st.elapsedTime() + "] SQLite table incidents doesn't exist, creating new table of incidents.");
				 statement.executeUpdate("drop table incidents");
				 statement.executeUpdate("create table incidents (subject text, type int, anonymous int, month int, day int, year int, description text, school text, injurybool int, absencebool int, adultscontacted text, injuriessustained text, learnmethod text, bullyingreason text, targetedstudents text, bullynames text, incidentlocation text, witnessnames text, name text, email text, phone text)");
			 }
		 }
		 catch (SQLException e)
		 {
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public void run()
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
	 
	 public void addReport(Report report)
	 {
		toAdd.add(report);
	 }
	 
	 public static void updateReport(Report report)
	 {
		 
	 }
}

class Request
{
	
}