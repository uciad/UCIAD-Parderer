package uk.ac.open.kmi.uciad.parser;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.open.kmi.uciad.helper.Trace;
import uk.ac.open.kmi.uciad.renderer.ApacheLogRDFRenderer;
import uk.ac.open.kmi.uciad.util.DateUtils;

public class UCIAD {
	
    public static void main(String args[]) {	
    //String args[] = {"27/Mar/2011", "--", "02/Apr/2011"};
        // Reading properties
    Properties properties = new Properties();            
    try {
                    properties.load(new FileInputStream("conf/uciad.conf"));
                    for (Object key : properties.keySet().toArray()) {
                System.setProperty((String) key, properties.getProperty((String) key));
            }
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }

            String logPattern = System.getProperty("logPattern");				
            String logFileDirectory = System.getProperty("logFileDirectory");		

            Map collection = getLogFileNameAndDateToParse();
            String logFilePath = logFileDirectory  + "/" +(String)collection.get("fileName");
            Date dateToParseLogFor = (Date)collection.get("dateToParse");
            
            
            int numberOfFields = Integer.parseInt(System.getProperty("numberOfFields"));
            String serverURI = System.getProperty("serverURI");

            DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
// ********* Following lines are for test purpose only ****************            
//            try {
//                dateToParseLogFor = df.parse("13/Feb/2011");
//            } catch (ParseException ex) {
//                Logger.getLogger(UCIAD.class.getName()).log(Level.SEVERE, null, ex);
//            }
            if (args.length > 0) {
                if (args.length == 3 && args[1].equals("--")) {
                    if ((args[0].contains("-") || !args[0].contains("/")) || (args[2].contains("-") || !args[2].contains("/"))) {
                        System.out.println("Please input dates in dd/MMM/yyyy format only.");
                        System.exit(2);
                    }
                    List<Date> dates = DateUtils.getListOfDatesBetween(args[0], args[2], "dd/MMM/yyyy");
                    Iterator datesItr = dates.iterator();
                    while (datesItr.hasNext()) {
                        dateToParseLogFor = (Date) datesItr.next();
                        System.out.println(dateToParseLogFor.toString());
                        parseAndRender(logPattern, logFilePath, numberOfFields, serverURI, dateToParseLogFor);
                    }
                } else {
                    for (String date : args) {
                        try {
                            if (date.contains("-") || !date.contains("/")) {
                                System.out.println("Please input dates in dd/MMM/yyyy format only.");
                                System.exit(2);
                            }
                            dateToParseLogFor = df.parse(date);
                            System.out.println(dateToParseLogFor.toString());
                            parseAndRender(logPattern, logFilePath, numberOfFields, serverURI, dateToParseLogFor);
                        } catch (ParseException ex) {
                            Logger.getLogger(UCIAD.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
           }
           else{
               System.out.println(dateToParseLogFor.toString());
               parseAndRender(logPattern, logFilePath, numberOfFields, serverURI, dateToParseLogFor);
           }


    }

    private static void parseAndRender(String logPattern, String logFilePath, int numberOfFields, String serverURI, Date dateToParseLogFor) {
        ApacheLogParser apacheParser = new ApacheLogParser(logPattern, logFilePath, numberOfFields, serverURI, dateToParseLogFor);
        Vector<Trace> traceVector = apacheParser.parseLog();
        ApacheLogRDFRenderer rdfRenderer = new ApacheLogRDFRenderer();
        rdfRenderer.renderRDF(traceVector, dateToParseLogFor);
    }

//	@SuppressWarnings({ "rawtypes" })
//	private static Map getLogFileNameAndDateToParse() {
//		Map<String, Object> collection = new HashMap<String, Object>();
//		String fileNamePattern = System.getProperty("logFileNamePattern");
//		
//		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
//		int currentMonth = 0;
//		int currentDay = 0;
//		int dayOfMonthToParseLogFor = 0;
//		Date dateToParseLogFor = null;
//                
//		//Add 1 as the Jan = 0 
//		currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
//		currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//		if (currentMonth == 1 && currentDay == 1)
//		{
//			currentMonth = 12;
//			currentYear = currentYear -1;
//			dayOfMonthToParseLogFor = 31;
//		}
//		else if (currentMonth == 2 && currentDay == 1)
//		{
//			currentMonth = 01;
//			dayOfMonthToParseLogFor = 31;				
//		}
//		else if (currentMonth == 3 && currentDay == 1)
//		{
//			currentMonth = 2;
//			if (currentYear%4 == 0)
//			{
//				dayOfMonthToParseLogFor = 29;
//			}
//			else {
//				dayOfMonthToParseLogFor = 28;
//			}
//			
//		}
//		else if (currentMonth == 4 && currentDay == 1)
//		{
//			currentMonth = 3;
//			dayOfMonthToParseLogFor = 31;			
//		}
//		else if (currentMonth == 5 && currentDay == 1)
//		{
//			currentMonth = 4;
//			dayOfMonthToParseLogFor = 30;			
//		}
//		else if (currentMonth == 6 && currentDay == 1)
//		{
//			currentMonth = 5;
//			dayOfMonthToParseLogFor = 31;			
//		}
//		else if (currentMonth == 7 && currentDay == 1)
//		{
//			currentMonth = 6;
//			dayOfMonthToParseLogFor = 30;			
//		}
//		else if (currentMonth == 8 && currentDay == 1)
//		{
//			currentMonth = 7;
//			dayOfMonthToParseLogFor = 31;			
//		}
//		else if (currentMonth == 9 && currentDay == 1)
//		{
//			currentMonth = 8;
//			dayOfMonthToParseLogFor = 31;			
//		}
//		else if (currentMonth == 10 && currentDay == 1)
//		{
//			currentMonth = 9;
//			dayOfMonthToParseLogFor = 30;			
//		}
//		else if (currentMonth == 11 && currentDay == 1)
//		{
//			currentMonth = 10;
//			dayOfMonthToParseLogFor = 31;			
//		}
//		else if (currentMonth == 12 && currentDay == 1)
//		{
//			currentMonth = 11;
//			dayOfMonthToParseLogFor = 30;			
//		}			
//		
//		
//		if (fileNamePattern.contains("%m"))
//		{
//			String strCurrentMonth = currentMonth+"";
//			fileNamePattern = fileNamePattern.replace("%m", strCurrentMonth);
//		}
//		if (fileNamePattern.contains("%Y"))
//		{
//			String strCurrentYear = currentYear+"";
//			fileNamePattern = fileNamePattern.replace("%Y", strCurrentYear);
//		}
//		else if (fileNamePattern.contains("%y"))
//		{
//			String strCurrentYear = currentYear+"";
//			fileNamePattern = fileNamePattern.replace("%y", strCurrentYear.subSequence(2, 4));
//		}
//		
//		if (dayOfMonthToParseLogFor == 0)
//		{
//			dayOfMonthToParseLogFor = currentDay - 1;
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(currentYear, currentMonth-1, dayOfMonthToParseLogFor);
//			dateToParseLogFor = cal.getTime();
//		}
//		else
//		{
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(currentYear, currentMonth-1, dayOfMonthToParseLogFor);
//			dateToParseLogFor = cal.getTime();
//		}
//		
//		collection.put("fileName", fileNamePattern);
//		collection.put("dateToParse", dateToParseLogFor);
//		
//		return collection;
//		
//	}
    
    @SuppressWarnings({ "rawtypes" })
	private static Map getLogFileNameAndDateToParse() {
		Map<String, Object> collection = new HashMap<String, Object>();
		String fileNamePattern = System.getProperty("logFileNamePattern");
		
		int currentYear = DateUtils.getCurrentYear();
                //Add 1 as the Jan = 0 
		int currentMonth = DateUtils.getCurrentMonth() + 1;
                
		int currentDayOfMonth = DateUtils.getCurrentDayOfMonth();
		
                Date dateToParseLogFor = DateUtils.getYesterdaysDate();
                
                if (currentDayOfMonth == 1)
                {                   
                    currentMonth = currentMonth - 1;
                }
                
                if (currentDayOfMonth == 1 && currentMonth == 1)
                {                   
                    currentYear = currentYear - 1;
                }               
		
		if (fileNamePattern.contains("%m"))
		{       
                        String strCurrentMonth = "";
                        if (currentMonth < 10)
                        {
                            strCurrentMonth = "0"+currentMonth;
                        }
                        else{
                            strCurrentMonth = currentMonth+"";
                        }
			fileNamePattern = fileNamePattern.replace("%m", strCurrentMonth);
		}
		if (fileNamePattern.contains("%Y"))
		{
			String strCurrentYear = currentYear+"";
			fileNamePattern = fileNamePattern.replace("%Y", strCurrentYear);
		}
		else if (fileNamePattern.contains("%y"))
		{
			String strCurrentYear = currentYear+"";
			fileNamePattern = fileNamePattern.replace("%y", strCurrentYear.subSequence(2, 4));
		}
		
		collection.put("fileName", fileNamePattern);
		collection.put("dateToParse", dateToParseLogFor);
		
		return collection;
		
	}

}
