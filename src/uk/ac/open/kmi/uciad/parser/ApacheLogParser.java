package uk.ac.open.kmi.uciad.parser;

import iglu.util.StringTools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.open.kmi.uciad.helper.ActorAgent;
import uk.ac.open.kmi.uciad.helper.ActorAgentSetting;
import uk.ac.open.kmi.uciad.helper.HTTPAction;
import uk.ac.open.kmi.uciad.helper.Page;
import uk.ac.open.kmi.uciad.helper.ParameterValue;
import uk.ac.open.kmi.uciad.helper.Trace;
import uk.ac.open.kmi.uciad.util.MD5Generator;

public class ApacheLogParser implements LogParser {
	
	private String logPattern = "";
	private String logFilePath = "";
	private int NUM_FIELDS = 0;
	private String serverURI = "";
	Date dateToParseLogFor = null;
	// Defines the format date is being used in the logs
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ssZ");
	// Defines the shorter format to comapre just the date leaving out minutes, seconds etc.
	// Will be used in main to decide which date to use.
	public static DateFormat dateFormatToParseLogFor = new SimpleDateFormat("dd/MMM/yyyy");
	
	public ApacheLogParser(String logPattern, String logFilePath, int numberOfFields, String serverURI, Date dateToParseLogFor) {
		this.setLogPattern(logPattern);
		this.logFilePath = logFilePath;
		this.NUM_FIELDS = numberOfFields;
		this.serverURI = serverURI;
		this.dateToParseLogFor = dateToParseLogFor;
	}
	
	@Override
	public Vector<Trace> parseLog() {
		//String logEntryPattern = "^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\"";
		//logEntryPattern = "^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+) \"([^\"]*)\" \"([^\"]+)\"";	
		
		String logEntryPattern = this.logPattern;		
		System.out.println("Reg Exp used: "+this.logPattern);		
		System.out.println("Log Path: "+this.logFilePath);	
		Pattern p = Pattern.compile(logEntryPattern);
		Matcher matcher = null;
		BufferedReader in = null;
		Vector<Trace> traceVector = new Vector<Trace>();
		try {
			in = new BufferedReader(new FileReader(this.logFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		String str;
		int count = 1;
		try {
			System.out.println("Starting to parse log entries...");
			while ((str = in.readLine()) != null) {	
				//System.out.println(dateFormatToParseLogFor.format(this.dateToParseLogFor));
				if (str.contains(dateFormatToParseLogFor.format(this.dateToParseLogFor)))
				{
					matcher = p.matcher(str);	
				}
				else
				{
					continue;
				}
				
				if (!matcher.matches() || NUM_FIELDS != matcher.groupCount()) {
					System.err.println("Bad log entry (or problem with RE?):");
					System.err.println(str);
                                        continue;
				}
				Trace newTraceEntry = new Trace();
				try {
					newTraceEntry.setTraceID(MD5Generator.getMD5(matcher.group(4)+this.logFilePath+matcher.group(5)));
					
					//ParameterValues
					String traceURLStr = matcher.group(5);
					traceURLStr = StringTools.dropAll(traceURLStr, "HTTP/1.0");
					traceURLStr = StringTools.dropAll(traceURLStr, "HTTP/1.1");
					URL traceURL = null;
					if (traceURLStr.contains("/"))
					{
						traceURL = new URL("http:/"+traceURLStr.substring(traceURLStr.indexOf("/")));
					}
					else 
					{
						traceURL = new URL("http://"+traceURLStr);
					}
										
					
					if (traceURL.getQuery() != null){
						String params [] = traceURL.getQuery().split("&");
						if(params.length > 1){
							Map<String, String> paramValueMap = new HashMap<String, String>();					
						    for (String param : params)  
						    {  
						        String name = param.split("=")[0];
						        String value = null;
						        if (!param.endsWith("=")){
						        	value = param.split("=")[1];
						        } else {
						        	value = "";
						        }		
						        paramValueMap.put(name, value);  
						    }
						    ParameterValue paramValue = new ParameterValue();
						    paramValue.setParameterValueMap(paramValueMap);
						    newTraceEntry.setParameterValues(paramValue);
						}
					}	
					
					//Date
					dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ssZ");
					Date date = dateFormat.parse(matcher.group(4));					
					newTraceEntry.setTime(date);
					
					//Actor Agent
					ActorAgent actorAgent = new ActorAgent();
					actorAgent.setActorAgentId(matcher.group(9));
					
					//Agent Agent Setting
					ActorAgentSetting actorAgentSetting = new ActorAgentSetting();
					actorAgentSetting.setIP(matcher.group(1));
					actorAgentSetting.setActorAgent(actorAgent);					
					newTraceEntry.setActorAgentSetting(actorAgentSetting);
					
					//Page
					Page tracePage = new Page();
					tracePage.setURL(matcher.group(5));
					tracePage.setOnServer(serverURI);
					newTraceEntry.setPageInvolved(tracePage);
				    
				    //Action
				    String actionCommand = matcher.group(5).substring(0,matcher.group(5).indexOf("/"));
				    HTTPAction httpAction = new HTTPAction();
				    httpAction.setHttpMethod(actionCommand);
				    newTraceEntry.setAction(httpAction);
				    
				    //Response
				    newTraceEntry.setResponse(matcher.group(6));
				    
				    //Response Size
				    if(!matcher.group(7).equals("-"))
				    {
				    	newTraceEntry.setResponseSize(Integer.parseInt((matcher.group(7))));
				    }				    				    
				    
				    //FollowTrace AKA referrer
				    newTraceEntry.setFollowTrace(matcher.group(8));
				    
				    traceVector.add(newTraceEntry);
				}	    
				catch (MalformedURLException e) {
						System.out.println("MalformedURLException: "+matcher.group(5));
						count--;
						e.printStackTrace();
				}catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("ArrayIndexOutOfBoundsException: "+matcher.group(5));
					count--;
					e.printStackTrace();
				}catch (NoSuchAlgorithmException e) {
					count--;					
					e.printStackTrace();
				} catch (ParseException e) {
					count--;
					e.printStackTrace();
				}
//				System.out.println("IP Address: " + matcher.group(1));
//				System.out.println("Date&Time: " + matcher.group(4));
//				System.out.println("Request: " + matcher.group(5));
//				System.out.println("Response: " + matcher.group(6));
//				System.out.println("Bytes Sent: " + matcher.group(7));
//				if (!matcher.group(8).equals("-"))
//					System.out.println("Referrer: " + matcher.group(8));
//				System.out.println("Browser: " + matcher.group(9));								
				count++;
			}
                        if (count > 1){
                            System.out.println(count+" Log entries parsed...");
                        }
                        else {
                            System.out.println("No mathcing entries found in the log...");
                        }
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return traceVector;

	}

	@Override
	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
		
	}

	@Override
	public String getLogPattern() {
		return this.logPattern;
	}

	@Override
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;		
	}

	@Override
	public String getLogFilePath() {		
		return this.logFilePath;
	}

	@Override
	public void setNumberOfFields(int numberOfFields) {
		this.NUM_FIELDS = numberOfFields;
	}

	@Override
	public int getNumberOfFileds() {
		return this.NUM_FIELDS;
	}	
	
	public void setServerURI (String serverURI) {
		this.serverURI = serverURI;	
	}
	
	public String getServerURI () {
		return this.serverURI;
	}	
	
	public static DateFormat getApacheLogParserDateFormat(){
		return dateFormat;
	}
	
}
