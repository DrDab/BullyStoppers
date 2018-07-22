package bullystoppersserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
	 
	private HashMap hm = new HashMap<Double, ArrayList<Report>>();
	
	 // q.remove will remove the one last.
	 
	 private Connection con;
	 private Statement statement;
	 
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
			 statement = con.createStatement();
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
				 statement.executeUpdate("create table incidents (id int, subject text, type int, anonymous int, month int, day int, year int, description text, school text, injurybool int, absencebool int, adultscontacted text, injuriessustained text, learnmethod text, bullyingreason text, targetedstudents text, bullynames text, incidentlocation text, witnessnames text, name text, email text, phone text, isclosed int)");
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
				 try
				 {
					 Report report = toAdd.remove();
					 Date incidentDate = report.getIncidentDate();
					 Calendar cal = Calendar.getInstance();
					 cal.setTime(incidentDate);
					 int id = report.getID();
					 String subject = report.getSubject();
					 int incidentType = report.getIncidentType();
					 int anonymous = report.isAnonymous() ? 1 : 0;
					 int month = cal.get(Calendar.MONTH) + 1;
					 int date = cal.get(Calendar.DAY_OF_MONTH);
					 int year = cal.get(Calendar.YEAR);
					 String description = report.getIncidentDescription();
					 String school = report.getSchool();
					 int injuryResulted = report.injuryResulted() ? 1 : 0;
					 int absenceResulted = report.absenceResulted() ? 1 : 0;
					 String adultsContacted = report.getAdultsContacted();
					 String injuriesSustained = report.getInjuriesSustained();
					 String howDidYouLearnAboutThis = report.getLearnedReason();
					 String bullyingReason = report.getBullyingReason();
					 String targetedStudents = report.getTargetedStudents();
					 String bullyNames = report.getBullyNames();
					 String incidentLocation = report.getIncidentLocation();
					 String witnessNames = report.getWitnessNames();
					 String name = report.getReportingPersonName();
					 String email = report.getReportingPersonEmail();
					 String phone = report.getReportingPersonPhone();
					 int isClosed = !report.isOpen() ? 1 : 0;
					 statement.executeUpdate("insert into incidents values(" + id + ",'" + subject + "', " + incidentType + "," + anonymous + "," + month + "," + date + "," + year + ",'" + description + "','" + school + "'," + injuryResulted + "," + absenceResulted + ",'" + adultsContacted + "','" + injuriesSustained + "','" + howDidYouLearnAboutThis + "','" + bullyingReason + "','" + targetedStudents + "','" + bullyNames + "','" + incidentLocation + "','" + witnessNames + "','" + name + "','" + email + "','" + phone + "'," + isClosed + ")");
				 }
				 catch (Exception e)
				 {
					 e.printStackTrace();
				 }
			 }
			 while(!toGet.isEmpty())
			 {
				 Request req = toGet.remove();
				 double requestTime = req.getRequestTime();
				 ArrayList<Report> toReturn = new ArrayList<Report>();
				 ResultSet rs;
				 try
				 {
					rs = statement.executeQuery("select * fom incidents");
					while(rs.next())
					{
						
					}
				 }
				 catch (SQLException e)
				 {
					e.printStackTrace();
				 }
			 }
			 while(!toClose.isEmpty())
			 {
				 try
				 {
					 Report report = toClose.remove();
					 int id = report.getID();
					 statement.executeUpdate("UPDATE incidents SET isclosed = 1 WHERE id = " + id);
				 }
				 catch (Exception e)
				 {
					 e.printStackTrace();
				 }
				
			 }
		 }
	 }
	 
	 public void addReport(Report report)
	 {
		toAdd.add(report);
	 }
	 
	 public ArrayList<Report> queryReports()
	 {
		 double tmpRequest = System.nanoTime();
		 toGet.add(new Request(tmpRequest));
		 for(;;)
		 {
			 if (hm.containsKey(tmpRequest))
			 {
				 return (ArrayList<Report>) hm.get(tmpRequest);
			 }
		 }
	 }
	 
	 public void closeReport(Report report)
	 {
		 toClose.add(report);
	 }
	 
}

class Request
{

	private double tmpRequest;
	
	public Request(double tmpRequest) 
	{
		this.tmpRequest = tmpRequest;
	}
	
	public double getRequestTime()
	{
		return tmpRequest;
	}
	
}