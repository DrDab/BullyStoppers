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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IncidentDB 
{
	private Stopwatch st;

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
				 statement.executeUpdate("create table incidents (id int, subject text, type int, anonymous int, month int, date int, year int, description text, school text, injurybool int, absencebool int, adultscontacted text, injuriessustained text, learnmethod text, bullyingreason text, targetedstudents text, bullynames text, incidentlocation text, witnessnames text, name text, email text, phone text, isclosed int)");
			 }
		 }
		 catch (SQLException e)
		 {
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public void addReport(Report report)
	 {
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
		 Chocolat.println("insert into incidents values(" + id + ",'" + subject + "', " + incidentType + "," + anonymous + "," + month + "," + date + "," + year + ",'" + description + "','" + school + "'," + injuryResulted + "," + absenceResulted + ",'" + adultsContacted + "','" + injuriesSustained + "','" + howDidYouLearnAboutThis + "','" + bullyingReason + "','" + targetedStudents + "','" + bullyNames + "','" + incidentLocation + "','" + witnessNames + "','" + name + "','" + email + "','" + phone + "'," + isClosed + ")");
		 try {
			statement.executeUpdate("insert into incidents values(" + id + ",'" + subject + "', " + incidentType + "," + anonymous + "," + month + "," + date + "," + year + ",'" + description + "','" + school + "'," + injuryResulted + "," + absenceResulted + ",'" + adultsContacted + "','" + injuriesSustained + "','" + howDidYouLearnAboutThis + "','" + bullyingReason + "','" + targetedStudents + "','" + bullyNames + "','" + incidentLocation + "','" + witnessNames + "','" + name + "','" + email + "','" + phone + "'," + isClosed + ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 @SuppressWarnings("unchecked")
	 public ArrayList<Report> queryReports()
	 {
		 ArrayList<Report> toReturn = new ArrayList<Report>();
		 ResultSet rs;
		 try
		 {
			rs = statement.executeQuery("select * from incidents");
			while(rs.next())
			{
				if (rs.getInt("type") == 0)
				{
					// bullying
					int id = rs.getInt("id");
					String subject = rs.getString("subject");
					boolean anonymous = (rs.getInt("anonymous") == 1);
					int month = rs.getInt("month");
					int date = rs.getInt("date");
					int year = rs.getInt("year");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, month - 1);
					cal.set(Calendar.DAY_OF_MONTH, date);
					Date d8 = cal.getTime();
					String incidentDescription = rs.getString("description");
					String school = rs.getString("school");
					boolean injuryResulted = (rs.getInt("injurybool") == 1);
					boolean absenceResulted = (rs.getInt("absencebool") == 1);
					String adultsContacted = rs.getString("adultscontacted");
					String injuriesSustained = rs.getString("injuriessustained");
					String learnMethod = rs.getString("learnmethod");
					String bullyingReason = rs.getString("bullyingreason");
					String targetedStudents = rs.getString("targetedstudents");
					String bullyNames = rs.getString("bullynames");
					String incidentLocation = rs.getString("incidentlocation"); 
					String witnessNames = rs.getString("witnessnames");
					String name = rs.getString("name");
					String email = rs.getString("email");
					String phone = rs.getString("phone");
					boolean isClosed = (rs.getInt("isclosed") == 1);
					Report r = new Report(id, school, 0, subject, anonymous, name, email, phone, injuryResulted, absenceResulted, adultsContacted, injuriesSustained, learnMethod, bullyingReason, d8, targetedStudents, bullyNames, incidentDescription, incidentLocation, witnessNames);
					if (isClosed)
					{
						r.closeReport();
					}
					toReturn.add(r);
				}
				else
				{
					// not bullying
					int id = rs.getInt("id");
					String subject = rs.getString("subject");
					boolean anonymous = (rs.getInt("anonymous") == 1);
					String school = rs.getString("school");
					String name = rs.getString("name");
					String email = rs.getString("email");
					String phone = rs.getString("phone");
					String incidentDescription = rs.getString("description");
					int month = rs.getInt("month");
					int date = rs.getInt("date");
					int year = rs.getInt("year");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, month - 1);
					cal.set(Calendar.DAY_OF_MONTH, date);
					Date d8 = cal.getTime();
					Report r = new Report(id, school, 1, subject, anonymous, name, email, phone, incidentDescription, d8);
					boolean isClosed = (rs.getInt("isclosed") == 1);
					if (isClosed)
					{
						r.closeReport();
					}
					toReturn.add(r);
				}
			}
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
		 }
		 return toReturn;
	 }
	 
	 public Report getReportById(int id)
	 {
		 try {
			ResultSet rs = statement.executeQuery("select * from incidents WHERE id = " + id);
			if(rs.next())
			{
				if (rs.getInt("type") == 0)
				{
					// bullying
					int id2 = rs.getInt("id");
					String subject = rs.getString("subject");
					boolean anonymous = (rs.getInt("anonymous") == 1);
					int month = rs.getInt("month");
					int date = rs.getInt("date");
					int year = rs.getInt("year");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, month - 1);
					cal.set(Calendar.DAY_OF_MONTH, date);
					Date d8 = cal.getTime();
					String incidentDescription = rs.getString("description");
					String school = rs.getString("school");
					boolean injuryResulted = (rs.getInt("injurybool") == 1);
					boolean absenceResulted = (rs.getInt("absencebool") == 1);
					String adultsContacted = rs.getString("adultscontacted");
					String injuriesSustained = rs.getString("injuriessustained");
					String learnMethod = rs.getString("learnmethod");
					String bullyingReason = rs.getString("bullyingreason");
					String targetedStudents = rs.getString("targetedstudents");
					String bullyNames = rs.getString("bullynames");
					String incidentLocation = rs.getString("incidentlocation"); 
					String witnessNames = rs.getString("witnessnames");
					String name = rs.getString("name");
					String email = rs.getString("email");
					String phone = rs.getString("phone");
					boolean isClosed = (rs.getInt("isclosed") == 1);
					Report r = new Report(id2, school, 0, subject, anonymous, name, email, phone, injuryResulted, absenceResulted, adultsContacted, injuriesSustained, learnMethod, bullyingReason, d8, targetedStudents, bullyNames, incidentDescription, incidentLocation, witnessNames);
					if (isClosed)
					{
						r.closeReport();
					}
					return (r);
				}
				else
				{
					// not bullying
					int id2 = rs.getInt("id");
					String subject = rs.getString("subject");
					boolean anonymous = (rs.getInt("anonymous") == 1);
					String school = rs.getString("school");
					String name = rs.getString("name");
					String email = rs.getString("email");
					String phone = rs.getString("phone");
					String incidentDescription = rs.getString("description");
					int month = rs.getInt("month");
					int date = rs.getInt("date");
					int year = rs.getInt("year");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, month - 1);
					cal.set(Calendar.DAY_OF_MONTH, date);
					Date d8 = cal.getTime();
					return (new Report(id2, school, 1, subject, anonymous, name, email, phone, incidentDescription, d8));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	 }
	 
	 public void closeReport(Report report)
	 {
		 try
		 {
			 int id = report.getID();
			 String query = "UPDATE incidents SET isclosed=1 WHERE id='?'";
			 PreparedStatement statement = con.prepareStatement(query);
			 statement.setInt(1, id);
			 statement.executeUpdate();
			 con.commit();
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 
	 public int getNextReportID()
	 {
		 int best = 0;
		 ResultSet rs;
		 try
		 {
			rs = statement.executeQuery("select * from incidents");
			while(rs.next())
			{
				int idNum = rs.getInt("id");
				if (idNum > best)
				{
					best = idNum;
				}
			}
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
		 }
		 return best + 1;
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