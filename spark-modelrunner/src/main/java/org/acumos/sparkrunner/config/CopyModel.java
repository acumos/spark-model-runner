package org.acumos.sparkrunner.config;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Java program to copy files from one directory to another directory.
 * Java IO API doesn't provide any direct way to copy files but you can copy files
 * by copying its contents from InputStream to OutputStream. Though there are some
 * better ways to do it like Using Apache Commons Utils library has FileUtils class
 * to copy files in Java
 *
 * @author Javin
 */
import java.io.*;
import java.util.Enumeration;
import java.util.jar.*;

public class CopyModel {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	public CopyModel(String src, String dest) {
	       File sourceFileOrDir = new File(src);
	       File destDir = new File(dest);
	       try{
	       if (sourceFileOrDir.isFile()) {
	           try {
				copyJarFile(new JarFile(sourceFileOrDir), destDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Error while copying file" + e.getMessage());
			}
	       } else if (sourceFileOrDir.isDirectory()) {
	           File[] files = sourceFileOrDir.listFiles(new FilenameFilter() {
	               public boolean accept(File dir, String name) {
	                   return name.endsWith(".jar");
	               }
	           });
	           for (File f : files) {
	               try {
					copyJarFile(new JarFile(f), destDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Error while copying file" + e.getMessage());
				}
	           }
	       }
	      } catch (Exception e){
	    	  log.error("Error while copying file" + e.getMessage());
	      }
	   }
	 
	   public static void copyJarFile(JarFile jarFile, File destDir) throws IOException {
	       String fileName = jarFile.getName();
	       String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
	       File destFile = new File(destDir, fileNameLastPart);
	 
	       JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile));
	       Enumeration<JarEntry> entries = jarFile.entries();
	 
	       while (entries.hasMoreElements()) {
	           JarEntry entry = entries.nextElement();
	           InputStream is = jarFile.getInputStream(entry);
	 
	           //jos.putNextEntry(entry);
	           //create a new entry to avoid ZipException: invalid entry compressed size
	           jos.putNextEntry(new JarEntry(entry.getName()));
	           byte[] buffer = new byte[4096];
	           int bytesRead = 0;
	           while ((bytesRead = is.read(buffer)) != -1) {
	               jos.write(buffer, 0, bytesRead);
	           }
	           is.close();
	           jos.flush();
	           jos.closeEntry();
	       }
	       System.out.println("Done");
	       jos.close();
	   }
	
}
