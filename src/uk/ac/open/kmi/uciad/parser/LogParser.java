package uk.ac.open.kmi.uciad.parser;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Vector;

import uk.ac.open.kmi.uciad.helper.Trace;

/**
 * Parse an Apache log file with Regular Expressions
 */
public interface LogParser {
	
	/**
	 * Parses the server log according to the logPattern 
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws ParseException 
	 */
	public Vector<Trace> parseLog();
	
	/**
	 * @param logPattern the logPattern to set
	 */
	public void setLogPattern(String logPattern);

	/**
	 * @return the logPattern
	 */
	public String getLogPattern();
	
	/**
	 * @param logFilePath the logFilePath to set
	 */
	public void setLogFilePath(String logFilePath);

	/**
	 * @return the logFilePath
	 */
	public String getLogFilePath();
	
	/**
	 * @param numberOfFields 
	 */
	public void setNumberOfFields(int numberOfFields);

	/**
	 * @return the numberOfFields
	 */
	public int getNumberOfFileds(); 
	
	
}
