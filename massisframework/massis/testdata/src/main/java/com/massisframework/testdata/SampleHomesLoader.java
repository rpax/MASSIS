package com.massisframework.testdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataHomeRecorder;

/**
 * Class for loading sample homes, included in com.massisframework.testdata
 * 
 * @author rpax
 *
 */
public class SampleHomesLoader {

	private static final String BUILDING_DIR = "com/massisframework/testdata/";

	/**
	 * Loads a sample home.
	 * 
	 * @param homeName
	 *            the name of the home. The name should be contained in the list
	 *            returned by {@link #listAvailable()}
	 * @throws IOException
	 * @throws RecorderException
	 */
	public static Home load(String homeName)
			throws IOException, RecorderException
	{
		if (!listAvailable().contains(homeName))
		{
			Logger.getLogger(SampleHomesLoader.class.getName()).log(
					Level.WARNING,
					"Building not present in when listing available buildings. Returning null");
			return null;
		} else
		{
			/*
			 * Copy home file to temporary folder & load it.
			 */
			final File destFile = OperatingSystem
					.createTemporaryFile("MASSIS_", ".sh3d");
			try (InputStream is = SampleHomesLoader.class.getClassLoader()
					.getResourceAsStream(BUILDING_DIR + homeName))
			{
				try (FileOutputStream os = new FileOutputStream(destFile))
				{
					IOUtils.copy(is, os);
				}
			}
			final Home home = new AdditionalDataHomeRecorder()
					.readHome(destFile.getAbsolutePath());
			return home;
		}
	}

	/**
	 * @return a list of homes that this loader can load.
	 */
	public static List<String> listAvailable()
	{
		List<String> available = Collections.emptyList();
		try (InputStream is = SampleHomesLoader.class.getClassLoader()
				.getResourceAsStream(BUILDING_DIR))
		{
			if (is != null)
			{
				available = IOUtils.readLines(is, Charsets.UTF_8);
				/*
				 * Filter by extension
				 */
				for (final Iterator<String> it = available.iterator(); it
						.hasNext();)
				{
					final String name = it.next();
					if (!name.endsWith(".sh3d"))
					{
						it.remove();
					}
				}
			} else
			{
				Logger.getLogger(SampleHomesLoader.class.getName()).log(
						Level.WARNING, "Invalid directory");
			}
		} catch (final IOException e)
		{
			Logger.getLogger(SampleHomesLoader.class.getName()).log(
					Level.SEVERE, "Error when listing available buildings", e);
		}
		return available;
	}

	public static void main(String[] args)
	{
		System.out.println(SampleHomesLoader.listAvailable());
		for (final String homeName : SampleHomesLoader.listAvailable())
		{
			System.out.println("Loading "+homeName);
			try
			{
				SampleHomesLoader.load(homeName);
			} catch (IOException | RecorderException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
