package uk.ac.open.kmi.uciad.parser;

import java.util.regex.*;

class Test
{
  public static void main(String[] args)
  {
    //String txt="62.172.77.74 - - [14/Feb/2011:13:29:20 +0000] \"GET /2011 HTTP/1.1\" 301 307 \"\" \"SearchBlox\"";
//	  String txt="/piwik/piwik.php";
//
//    //String re1="^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+) \"([^\"]*)\" \"([^\"]+)\"";	// Double Quote String 1
//    String re1 = "^/piwik*";
//
//    Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//    //Pattern p = Pattern.compile(re1);
//    Matcher m = p.matcher(txt);
//    
//    if (m.find())
//    {	
//    	for (int i=0; i<=m.groupCount(); i++)
//    	{
//    		 String string1=m.group(i);
//    	     System.out.print(i+"("+string1.toString()+")"+"\n");
//    	}       
//    }
//      String pattern = "[,\\s]+";
//    String colours = "Red,White, Blue   Green        Yellow, Orange";
//
//    Pattern splitter = Pattern.compile(pattern);
//    String[] result = splitter.split(colours);
//
//    for (String colour : result) {
//      System.out.println("Colour = \"" + colour + "\"");
//    }
      
      //String REGEX = "(<*>)+ ";
      String REGEX = "(from)";
                Pattern p = Pattern.compile(REGEX);
                
                String[] items = p.split("from <http://ONE.info/data/web06_04-May-2011> from <http://TWO.info/data/web06_05-May-2011> from <http://THREE.info/data/web06_05-May-2011>");
//                Matcher m = p.matcher("from <http://ONE.info/data/web06_04-May-2011> from <http://TWO.info/data/web06_05-May-2011> from <http://THREE.info/data/web06_05-May-2011>");
//                boolean test = m.matches();
//                int j = m.groupCount();
//                for (int i =1; i <= m.groupCount(); i++)
//                {
//                    System.out.println("Matches = \"" + m.group(i) + "\"");
//                }
                for (String colour : items) {
      System.out.println("Items = \"" + colour + "\"");
    }
      
               
   
    
//    if (m.matches())
//    {
//    	System.out.println("true");
//    }
  }
}

