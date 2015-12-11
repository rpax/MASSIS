package com.massisframework.testdata;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ListSelectionEvent;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massisframework.testdata.gui.SampleHomeGUIControl;
import com.massisframework.testdata.gui.SampleHomesGUI;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

/**
 * Class for loading sample homes, included in com.massisframework.testdata
 * 
 * @author rpax
 *
 */
public class SampleHomesLoader {

	// public static final String SAMPLES_BUILDING_DIR =
	// "com.massisframework.testdata";
	private static final String TESTDATA_PACKAGE = "com.massisframework.testdata";
	// private static final ResourceFilter jsonFilter = new ResourceFilter()
	// .packageName("com.massisframework.testdata").resourceName("*.json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting()
			.create();

	/**
	 * Loads a sample home.
	 * 
	 * @param homeName
	 *            the name of the home. The name should be contained in the list
	 *            returned by {@link #listAvailable()}
	 * @throws IOException
	 * @throws RecorderException
	 */
	public static File loadHomeTempFile(String homeName) throws IOException

	{
		// Safer
		homeName = FilenameUtils.removeExtension(homeName);
		final SampleHomeDescription desc = loadDescription(homeName);

		/*
		 * Copy home file to temporary folder & load it.
		 */
		final File destFile = OperatingSystem
				.createTemporaryFile("MASSIS_", ".sh3d");
		try (InputStream is = getURLFor(homeName + ".sh3d")
				.openStream())
		{
			try (FileOutputStream os = new FileOutputStream(destFile))
			{
				IOUtils.copy(is, os);
			}
		}

		return destFile;
	}

	/**
	 * @return a list of homes that this loader can load.
	 */
	public static List<SampleHomeDescription> listAvailable()
	{
		final List<SampleHomeDescription> available = new ArrayList<>();
		for (final URL furl : CPScanner.scanResources(new ResourceFilter()
				.packageName(TESTDATA_PACKAGE).resourceName("*.json")))
		{
			try
			{
				available.add(loadDescription(furl));
			} catch (final IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return available;
	}

	private static SampleHomeDescription loadDescription(URL furl)
			throws IOException
	{
		try (Reader r = new InputStreamReader(furl.openStream()))
		{
			return gson.fromJson(r, SampleHomeDescription.class);
		}

	}

	public static SampleHomeDescription loadDescription(String homeName)
			throws IOException
	{
		// Safer
		// final int upperBound = homeName.length() - simpleName.length();
		String filterfolder = TESTDATA_PACKAGE + "."
				+ homeName.replace("/", ".");
		filterfolder = filterfolder.substring(0, filterfolder.lastIndexOf('.'));
		try (InputStream is = getURLFor(homeName + ".json").openStream())
		{
			final String jsonContent = IOUtils.toString(is);
			final SampleHomeDescription desc = gson.fromJson(jsonContent,
					SampleHomeDescription.class);
			desc.setImageURL(CPScanner.scanResources(new ResourceFilter()
					.packageName(filterfolder)
					.resourceName(FilenameUtils.getName(desc.getImage())))
					.get(0));
			return desc;
		}
	}

	private static URL getURLFor(String subElementFile)
	{
		final String extension = FilenameUtils.getExtension(subElementFile);
		final String subElementFileNoExtension = subElementFile.substring(0,
				subElementFile.length() - extension.length()-1);
		String filterfolder = TESTDATA_PACKAGE + "."
				+ subElementFileNoExtension.replace("/", ".");
		filterfolder = filterfolder.substring(0, filterfolder.lastIndexOf('.'));
		final ResourceFilter filter = new ResourceFilter()
				.packageName(filterfolder)
				.resourceName(FilenameUtils.getName(subElementFile));

		final List<URL> res = CPScanner.scanResources(filter);
		return res.get(0);
	}

}
