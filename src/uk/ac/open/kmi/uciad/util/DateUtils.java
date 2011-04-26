/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.kmi.uciad.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author se3535
 */

public class DateUtils {
    
    public static void main(String args[])
    {
                                
        System.out.println(getDate7DaysAgo().toString());
        System.out.println(getCurrentMonthName());
        System.out.println(getCurrentYear());
        System.out.println(getYesterdaysDate().toString());
        List<Date> dates = getListOfDatesBetween("25-Mar-2011", "04-Apr-2011", "dd-MMM-yyyy");
        
    }

    public static Date getDate7DaysAgo()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, - 8);
        Date newDate = calendar.getTime();
        return newDate;

    }
    
    public static String getCurrentMonthName() {
        
        Calendar now = Calendar.getInstance();
        
        String[] strMonths = new String[]{
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        };

        return strMonths[now.get(Calendar.MONTH)];

    }
    
     public static int getCurrentYear() {
         
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) ;

    }
     
    public static int getCurrentMonth() {
         
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MONTH) ;

    }
    
    public static int getCurrentDayOfMonth() {
         
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_MONTH) ;

    }
     
    public static Date getYesterdaysDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, - 1);
        return cal.getTime();
    }         
    
     public static List<Date> getListOfDatesBetween(String startDateStr, String endDateStr, String dateFormatStr) {
        List<Date> dates = new ArrayList<Date>();
        try {
            DateFormat format = new SimpleDateFormat(dateFormatStr);
            Date startDate = format.parse(startDateStr);
            Date endDate = format.parse(endDateStr);          
            
            if (startDate.getTime() > endDate.getTime())
            {
                System.out.println("Please enter earlier date first e.g. 18-Apr-2011 -- 20-Apr-2011");
                System.exit(2);
            }
            
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(startDate);

            while (calendar.getTime().before(endDate)) {
                Date newDate = calendar.getTime();
                dates.add(newDate);
                calendar.add(Calendar.DATE, 1);
            }
            if (!dates.isEmpty())
            {
                dates.add(endDate);
            }
        }catch (ParseException ex) {
            Logger.getLogger(DateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dates;
    }
}
