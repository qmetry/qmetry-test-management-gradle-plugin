package com.qmetry;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.Input;

import java.util.Map;
import java.io.File;
import java.io.FilenameFilter;

public class PublishResults extends DefaultTask 
{
	public PublishResults()
	{
		setDescription("Faster way to linking automated test result to QMetry Test Management, to ship high quality products.");
		setGroup("QMetry Test Management");
	}
	
    @TaskAction
    public void publishResults() throws QTMException
	{

		String pluginName = "QMetry Test Management Gradle Plugin";
		try 
		{
			QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
			if(config == null) throw new QTMException("Could not find QTM configuration! please provide qmetryConfig {... } block with appropriate parameters in your build.gradle file!");									
			String displayName = pluginName + " : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);
					
			if(!config.getParsedTestSuite().isEmpty())
			{
				System.out.println(pluginName + " : Using Test Suite '"+config.getParsedTestSuite()+"'");
			}
			if(!config.getParsedPlatform().isEmpty())
			{
				System.out.println(pluginName + " : Using Platform '"+config.getParsedPlatform()+"'");
			}
			if(!config.getParsedBuild().isEmpty())
			{
				System.out.println(pluginName + " : Using Build (or Drop) '"+config.getParsedBuild()+"'");
			}
			if(!config.getParsedProject().isEmpty())
			{
				System.out.println(pluginName + " : Project '"+config.getParsedProject()+"'");
			}
			if(!config.getParsedRelease().isEmpty())
			{
				System.out.println(pluginName + " : Using Release '"+config.getParsedRelease()+"'");
			}
			if(!config.getParsedCycle().isEmpty())
			{
				System.out.println(pluginName + " : Using Cycle '"+config.getParsedCycle()+"'");
			}
			
			
			if(!config.getParsedTestCase_fields().isEmpty())
			{
				System.out.println(pluginName + " : Using Test Case Fields '"+config.getParsedTestCase_fields()+"'");
			}
			if(!config.getParsedTestSuite_fields().isEmpty())
			{
				System.out.println(pluginName + " : Using Test Suite Fields '"+config.getParsedTestSuite_fields()+"'");
			}

			String compfilepath = getProject().getBuildDir().toString() + File.separator.toString() + config.getParsedTestResultFilePath();
			File resultFile = new File(compfilepath);
			if(resultFile==null || !resultFile.exists()) 
				throw new QTMException("Result file(s) '"+compfilepath+"' not Found!");
			
			QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getParsedQtmAutomationApiKey());
			synchronized (conn) 
			{
				System.out.println(pluginName + " : Reading result files from Directory for QAS/json files '" + compfilepath+ "'");
				String automationFramework = config.getParsedAutomationFramework();
				if(automationFramework.equals("QAS"))
				{
					File dirs[] = resultFile.listFiles();
					if(dirs==null) {
						throw new QTMException("Could not find result file(s) at given path!");
					}
					
					Long last_mod = Long.valueOf(0);
					File latest_dir = null;
					System.out.println(1);
					for(File adir : dirs)
					{   System.out.println(2);
						if (adir.isDirectory() && adir.lastModified() > last_mod) 
						{
							latest_dir = adir;
							last_mod = adir.lastModified();
						}
					}
					ZipUtils zipUtils = new ZipUtils("json");
					if(latest_dir == null)
						throw new QTMException("Results' directory of type QAS not found in given directory '"+resultFile.getAbsolutePath()+"'");
					zipUtils.zipDirectory(latest_dir, "qmetry_result.zip");
					File zipArchive = new File(latest_dir, "qmetry_result.zip");
					if(zipArchive==null || !zipArchive.exists())
						throw new QTMException("Failed to create zip archive for QAS results at directory '"+latest_dir.getAbsolutePath()+"'");
					conn.uploadFileToTestSuite(zipArchive.getAbsolutePath(),
														config.getParsedTestSuite(),
														config.getParsedAutomationFramework(), 
														config.getParsedBuild(), 
														config.getParsedPlatform(),
														config.getParsedProject(),
														config.getParsedRelease(),
														config.getParsedCycle(),
														config.getParsedTestCase_fields(),
														config.getParsedTestSuite_fields());
					System.out.println(pluginName + " : Result file successfully uploaded!");
				}
				else if(resultFile.isDirectory()) 
				{
					System.out.println(pluginName + " : Reading result files from Directory '" + compfilepath+ "'");
					File[] listOfFiles = resultFile.listFiles();

					for (int i = 0; i < listOfFiles.length; i++) 
					{
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml") || listOfFiles[i].getName().endsWith(".json"))) 
						{
							System.out.println("\n" +pluginName+ " : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println(pluginName + " : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), 
														config.getParsedTestSuite(),
														config.getParsedAutomationFramework(), 
														config.getParsedBuild(), 
														config.getParsedPlatform(),
														config.getParsedProject(),
														config.getParsedRelease(),
														config.getParsedCycle(),
														config.getParsedTestCase_fields(),
														config.getParsedTestSuite_fields());
							System.out.println(pluginName + " : Result file successfully uploaded!");
						}
					}
				}
				else if(resultFile.isFile())
				{
					System.out.println(pluginName + " : Reading result file '" + compfilepath+ "'");
					System.out.println(pluginName + " : Uploading result file...");
					conn.uploadFileToTestSuite(compfilepath, 
												config.getParsedTestSuite(), 
												config.getParsedAutomationFramework(),
												config.getParsedBuild(), 
												config.getParsedPlatform(),
												config.getParsedProject(),
												config.getParsedRelease(),
												config.getParsedCycle(),
												config.getParsedTestCase_fields(),
												config.getParsedTestSuite_fields());
					System.out.println(pluginName + " : Result file successfully uploaded!");
				}
				else
				{
					throw new QTMException("Failed to read result file '"+compfilepath+"'");
				}
			}
		} 
		catch (QTMException e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.getMessage());
		} 
		catch (Exception e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.toString());
		}
		System.out.println("\n" + pluginName + " : Finished Post Build Action!");
    }
}