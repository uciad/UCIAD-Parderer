package uk.ac.open.kmi.uciad.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

public class DataCompressor {
   static final int BUFFER = 2048;
   public static void zip (String intputFileName, String outputFilePath) {
      try {
         BufferedInputStream origin = null;
         FileOutputStream dest = new 
           FileOutputStream(outputFilePath);
         ZipOutputStream out = new ZipOutputStream(new 
           BufferedOutputStream(dest));
         //out.setMethod(ZipOutputStream.DEFLATED);
         byte data[] = new byte[BUFFER];
         // get a list of files from current directory
         File fileToZip = new File("data/"+intputFileName);
         //String files[] = f.list();
         FileInputStream fileInPutStream = new  FileInputStream(fileToZip);
         origin = new BufferedInputStream(fileInPutStream, BUFFER);
         ZipEntry entry = new ZipEntry(fileToZip.getName());
            out.putNextEntry(entry);
            int count;
            while((count = origin.read(data, 0, 
              BUFFER)) != -1) {
               out.write(data, 0, count);
            }
            origin.close();
        
         out.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
	public static void unZip(String inputZipFilePath) {
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(inputZipFilePath);
			Enumeration e = zipfile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				System.out.println("Extracting: " + entry);
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream("data/unZip/"+entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}