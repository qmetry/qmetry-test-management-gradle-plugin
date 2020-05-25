package com.qmetry;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils 
{
	private String extension;
	private String xmlExtension = "xml";
	
	public ZipUtils(String extension)
	{
		this.extension = extension;
	}
	
	public void zipDirectory(File sourceDir, String zipFileName) throws QTMException 
	{
		FileOutputStream fout = null;
		ZipOutputStream zout = null;
		try
		{
			System.out.println("QMetry Test Management Gradle Plugin : Creating zip archive at directory '"+sourceDir+"'");
			fout = new FileOutputStream((sourceDir.getAbsolutePath() + "/" + zipFileName), false);
			zout = new ZipOutputStream(fout);
			zipSubDirectory("", sourceDir, zout);
			zout.close();
			System.out.println("QMetry Test Management Gradle Plugin :  Zip file created successfully '"+zipFileName+"'");
		}
		catch(IOException e)
		{
			throw new QTMException("Failed to create zip archive in directory '"+sourceDir+"'");
		}
		finally
		{
			/*try {
			//if(fout != null) fout.close();
			//if(zout != null) zout.close();
			} catch(IOException e) {
				e.printStackTrace();
			}*/
		}
	}

	private void zipSubDirectory(String basePath, File dir, ZipOutputStream zout) throws IOException, QTMException
	{
		FileInputStream fin = null;
		try
		{
			byte[] buffer = new byte[4096];
			File[] files = dir.listFiles();
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					String path = basePath + file.getName() + "/";
					zout.putNextEntry(new ZipEntry(path));
					zipSubDirectory(path, file, zout);
					zout.closeEntry();
				}
				else if(file.getName().endsWith(extension) || file.getName().endsWith(xmlExtension))
				{
					System.out.println("QMetry Test Management Gradle Plugin : adding result file to zip archive : '"+file.getAbsolutePath()+"'");
					fin = new FileInputStream(file);
					zout.putNextEntry(new ZipEntry(basePath + file.getName()));
					int length;
					while ((length = fin.read(buffer)) > 0) 
					{
						zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
			}
		} catch (IOException e) {
			throw new QTMException("Failed to create zip archive");
		} catch (NullPointerException e) {
			throw new QTMException("Failed to create zip archive");
		} finally {
			try {
				if(fin!=null) fin.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}
