package com.qmetry;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
public class QTMGradleExtension
{
	private String qtmUrl;
	private String qtmAutomationApiKey;
	private String automationFramework;
	private String testResultFilePath;
	private String testSuite;
	private String platform;
	private String build;
	private String project;
	private String release;
	private String cycle;
	// New Data Fields testcase_fields and testsuite_fields 
	private String testcase_fields;
	private String testsuite_fields;
	 

	public String getQtmUrl() {
		return this.qtmUrl;
	}

	public String getQtmAutomationApiKey() {
		return this.qtmAutomationApiKey;
	}

	public String getAutomationFramework() {
		return this.automationFramework;
	}

	public String getTestResultFilePath() {
		return this.testResultFilePath;
	}

	public String getTestSuite() {
		return this.testSuite;
	}

	public String getPlatform() {
		return this.platform;
	}

	public String getBuild() {
		return this.build;
	}

	public String getProject() {
		return this.project;
	}

	public String getRelease() {
		return this.release;
	}

	public String getCycle() {
		return this.cycle;
	}
	
	

	
	public String getParsedQtmUrl() throws QTMException 
	{
		if(this.qtmUrl == null)
			throw new QTMException("Please provide your QMetry Test Management URL in qtmConfig block as 'qtmUrl'");
		if(!this.qtmUrl.startsWith("http:/") && !this.qtmUrl.startsWith("https:/"))
			throw new QTMException("Please provide a valid QMetry Test Management URL");
		
		if(!getQtmUrl().endsWith("/")) 
			return getQtmUrl() + "/";
		else
			return getQtmUrl();
	}
	
	public String getParsedQtmAutomationApiKey() throws QTMException
	{
		if(this.qtmAutomationApiKey == null)
			throw new QTMException("Please provide your QMetry Test Management Automation API Key in qtmConfig block as 'qtmAutomationApiKey'");
		return this.qtmAutomationApiKey;
	}
	
	public String getParsedTestResultFilePath() throws QTMException
	{
		if(this.testResultFilePath==null)
			throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
		String filePath = this.testResultFilePath.trim();
		if(filePath.length()<2)
			throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
		return this.testResultFilePath;
	}
	
	public String getParsedProject() throws QTMException
	{
		if(this.project==null)
			throw new QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'");
		String temp_project = this.project.trim();
		if(temp_project.length()<2)
			throw new QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'");
		return temp_project;
	}
	
	public String getParsedRelease() throws QTMException
	{
		if(this.release==null)
			return "";
		return this.release.trim();
	}
	
	public String getParsedCycle() throws QTMException
	{
		if(this.cycle==null) {
			return "";
		} else {
			if((this.release==null || this.release.trim().length()==0)) {
				throw new QTMException("Please provide release value for cycle!");
			} else {
				return this.cycle.trim();
			}
		}
	}
	
	public String getParsedBuild()
	{
		if(this.build == null)
			return "";
		return this.build.trim();
	}
	
	public String getParsedPlatform()
	{
		if(this.platform == null)
			return "";
		return this.platform.trim();
	}
	
	public String getParsedTestSuite()
	{
		if(this.testSuite == null)
			return "";
		return this.testSuite.trim();
	}
	
	public String getParsedAutomationFramework() throws QTMException
	{
		if(this.automationFramework == null)
			throw new QTMException("Please provide your Automation Framework in qtmConfig block as 'automationFramework'");
		if(!(this.automationFramework.equals("JUNIT") 
			|| this.automationFramework.equals("TESTNG")
			|| this.automationFramework.equals("QAS")
			|| this.automationFramework.equals("CUCUMBER")
			|| this.automationFramework.equals("HPUFT")))
			throw new QTMException("Automation Framework '"+automationFramework+"' not supported. Use [JUNIT TESTNG QAS CUCUMBER HPUFT]");
		return this.automationFramework;
	}
	
	public void setQtmUrl(String qtmApiUrl) {
		this.qtmUrl = qtmApiUrl;
	}

	public void setQtmAutomationApiKey(String qtmApiKey) {
		this.qtmAutomationApiKey = qtmApiKey;
	}
	
	public void setAutomationFramework(String autoFramework)
	{
		this.automationFramework = autoFramework;
	}

	public void setTestResultFilePath(String testResultFilePath) {
		this.testResultFilePath = testResultFilePath;
	}
	
	public void setTestSuite(String testName) {
		this.testSuite = testName;
	}
	
	public void setPlatform(String name)
	{
		this.platform = name;
	}
	
	public void setProject(String name)
	{
		this.project = name;
	}
	
	public void setRelease(String name)
	{
		this.release = name;
	}
	
	public void setCycle(String name)
	{
		this.cycle = name;
	}
	

	public void setBuild(String name)
	{
		this.build = name;
	}
	/// New Fields testcase_fields and testsuite_fields Setter
	public void setTestcase_fields(String testCaseFields) {
		this.testcase_fields = testCaseFields;
	}
	public void setTestsuite_fields(String testSuiteFields) {
		this.testsuite_fields = testSuiteFields;
		
	}
	
	//////////////////////////////////////////////
	// New Data Fields testcase_fields and testsuite_fields Getter
	
	public String getTestcase_fields() {
		return this.testcase_fields;
	}
	
	public String getTestsuite_fields() {
		return this.testsuite_fields;
	}
	//   / / // / / / / // / /  // / // / / // / // // / / // / / / // // /
	
	public String getParsedTestCase_fields() throws QTMException{
		if(this.testcase_fields == null)
			return "";
		try {
		JSONParser parser = new JSONParser();
        JSONObject j = (JSONObject) parser.parse(this.testcase_fields);
		return j.toString();
		}
		catch(Exception ex){
			throw new QTMException("Please provide correct Json data of Test case fields !");
		}
	}
	
	public String getParsedTestSuite_fields() throws QTMException{
		if(this.testsuite_fields == null)
			return "";
		try {
		JSONParser parser = new JSONParser();
        JSONObject j = (JSONObject) parser.parse(this.testsuite_fields);
		return j.toString();
		}
		catch(Exception ex){
			throw new QTMException("Please provide correct Json data of Test suite fields !");
		}
	}
}