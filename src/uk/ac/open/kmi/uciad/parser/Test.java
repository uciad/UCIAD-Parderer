package uk.ac.open.kmi.uciad.parser;

import java.util.regex.*;

class Test
{
  public static void main(String[] args)
  {
    //String txt="62.172.77.74 - - [14/Feb/2011:13:29:20 +0000] \"GET /2011 HTTP/1.1\" 301 307 \"\" \"SearchBlox\"";
	  String txt="/piwik/piwik.php";

    //String re1="^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+) \"([^\"]*)\" \"([^\"]+)\"";	// Double Quote String 1
    String re1 = "^/piwik*";

    Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    //Pattern p = Pattern.compile(re1);
    Matcher m = p.matcher(txt);
    
    if (m.find())
    {	
    	for (int i=0; i<=m.groupCount(); i++)
    	{
    		 String string1=m.group(i);
    	     System.out.print(i+"("+string1.toString()+")"+"\n");
    	}       
    }
   
    
//    if (m.matches())
//    {
//    	System.out.println("true");
//    }
  }
}

