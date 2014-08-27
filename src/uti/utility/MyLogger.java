package uti.utility;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Logging class for the pools.
 */
public class MyLogger
{
	public Logger log = Logger.getLogger(MyLogger.class);
	public String ClassName = "";

	public void SetClassName(String ClassName)
	{
		this.ClassName = ClassName;

		log = Logger.getLogger(this.ClassName);
	}

	/**
	 * 
	 * @param LogConfigPath: Đường dẫn đến file log4j.properties
	 * @param ClassName
	 */
	public MyLogger(String LogConfigPath, String ClassName)
	{
		this.ClassName = ClassName;
		log = Logger.getLogger(this.ClassName);
		PropertyConfigurator.configure(LogConfigPath);
	}

	public static void WriteDataLog(String Folder, String Data) throws Exception
	{
		try
		{
			String CurrentDate = MyConfig.Get_DateFormat_yyyyMMdd().format(Calendar.getInstance().getTime());
			String FileName = (new StringBuilder("DataLog_")).append(CurrentDate).append(".txt").toString();
			String Path = (new StringBuilder(String.valueOf(Folder))).append(FileName).toString();
			Data = MyConfig.Get_DateFormat_LongFormat().format(Calendar.getInstance().getTime()) + " || " + Data;
			MyFile.WriteToFile(Path, Data);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static void WriteDataLog(String Folder, String LastName, String Data)
	{
		try
		{
			String CurrentDate = MyConfig.Get_DateFormat_yyyyMMdd().format(Calendar.getInstance().getTime());
			String FileName = (new StringBuilder("DataLog_")).append(CurrentDate).append(LastName).append(".txt").toString();
			String Path = (new StringBuilder(String.valueOf(Folder))).append(FileName).toString();
			Data = MyConfig.Get_DateFormat_LongFormat().format(Calendar.getInstance().getTime()) + " || " + Data;
			MyFile.WriteToFile(Path, Data);
		}
		catch (Exception ex)
		{
			Logger log = Logger.getLogger(MyLogger.class);
			log.error(ex);
		}
	}

}
