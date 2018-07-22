package bullystoppersserver;

import java.util.Date;

public class Report 
{
	// generic report info. (not have to be bullying-specific)
	private String subject = "";
	private int incidentType = -1;
	// -1 = not initialized
	// 0 = bullying
	// 1 = general incident
	private boolean anonymous = false;
	private Date incidentDate;
	private String incidentDescription;
	private String school;
	
	// basic info. (bullying-specific)
	private boolean injuryResulted = false;
	private boolean absenceResulted = false;
	private String schoolAdultsContacted = "";
	private String injuriesSustained = "";
	private String howDidYouLearnAboutThis = "";
	private String bullyingReason = "";
	private String targetedStudents = "";
	private String bullyNames = "";
	private String bullyIncidentLocation = "";
	private String witnessNames = "";
	
	// if report is not anonymous, add these details.
	private String reportingPersonName = "";
	private String reportingPersonEmail = "";
	private String reportingPersonPhoneNumber = "";
	
	// details on ticket information.
	private boolean isClosed = false;
	
	// constructor (for general incident)
	public Report(String school, int incidentType, String subject, boolean anonymous, String reportingPersonName, String reportingPersonEmail, String reportingPersonPhoneNumber, String incidentDescription, Date incidentDate)
	{
		this.school = school;
		this.incidentType = incidentType;
		this.subject = subject;
		this.anonymous = anonymous;
		this.reportingPersonName = reportingPersonName;
		this.reportingPersonEmail = reportingPersonEmail;
		this.reportingPersonPhoneNumber = reportingPersonPhoneNumber;
		this.incidentDescription = incidentDescription;
		this.incidentDate = incidentDate;
	}
	
	// constructor (for bullying incident)
	public Report(String school, int incidentType, String subject, boolean anonymous, String reportingPersonName, String reportingPersonEmail, String reportingPersonPhoneNumber, boolean injuryResulted, boolean absenceResulted, String schoolAdultsContacted, String injuriesSustained, String howDidYouLearnAboutThis, String bullyingReason, Date incidentDate, String targetedStudents, String bullyNames, String incidentDescription, String bullyingIncidentLocation, String witnessNames)
	{
		this.school = school;
		this.incidentType = incidentType;
		this.subject = subject;
		this.anonymous = anonymous;
		this.reportingPersonName = reportingPersonName;
		this.reportingPersonEmail = reportingPersonEmail;
		this.reportingPersonPhoneNumber = reportingPersonPhoneNumber;
		this.injuryResulted = injuryResulted;
		this.schoolAdultsContacted = schoolAdultsContacted;
		this.injuriesSustained = injuriesSustained;
		this.howDidYouLearnAboutThis = howDidYouLearnAboutThis;
		this.bullyingReason = bullyingReason;
		this.incidentDate = incidentDate;
		this.targetedStudents = targetedStudents;
		this.bullyNames = bullyNames;
		this.incidentDescription = incidentDescription;
		this.bullyIncidentLocation = bullyingIncidentLocation;
		this.witnessNames = witnessNames;
	}
	
	public String getSchool()
	{
		return school;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public int getIncidentType()
	{
		return incidentType;
	}
	
	public boolean isAnonymous()
	{
		return anonymous;
	}
	
	// bullying-specific stuff below here.
	
	public boolean injuryResulted()
	{
		return injuryResulted;
	}
	
	public boolean absenceResulted()
	{
		return absenceResulted;
	}
	
	public String getAdultsContacted()
	{
		return schoolAdultsContacted;
	}
	
	public String getInjuriesSustained()
	{
		return injuriesSustained;
	}
	
	public String getBullyingReason()
	{
		return bullyingReason;
	}
	
	public String getLearnedReason()
	{
		return howDidYouLearnAboutThis;
	}
	
	public Date getIncidentDate()
	{
		return incidentDate;
	}
	
	public String getTargetedStudents()
	{
		return targetedStudents;
	}
	
	public String getBullyNames()
	{
		return bullyNames;
	}
	
	public String getIncidentDescription()
	{
		return incidentDescription;
	}
	
	public String getIncidentLocation()
	{
		return bullyIncidentLocation;
	}
	
	public String getWitnessNames()
	{
		return witnessNames;
	}
	
	public String getReportingPersonName()
	{
		return reportingPersonName;
	}
	
	public String getReportingPersonEmail()
	{
		return reportingPersonEmail;
	}
	
	public String getReportingPersonPhone()
	{
		return reportingPersonPhoneNumber;
	}
	
	public void closeReport()
	{
		isClosed = true;
	}
	
	public void openReport()
	{
		isClosed = false;
	}
	
	public boolean isOpen()
	{
		return !isClosed;
	}
	
}
