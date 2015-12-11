package com.massisframework.testdata;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class SampleHomeDescription {

	private String shortdescription;
	private String filename;
	private String description;
	private String image;

	
	public String getShortdescription()
	{
		return this.shortdescription;
	}

	public String getFilename()
	{
		return this.filename;
	}
	
	public String getDescription()
	{
		return this.description;
	}

	public ImageIcon getImage() throws IOException
	{
		try (InputStream is = SampleHomesLoader.class.getClassLoader()
				.getResourceAsStream(
						SampleHomesLoader.SAMPLES_BUILDING_DIR
								+ SampleHomeDescription.this.image))
		{
			return new ImageIcon(ImageIO.read(is));
		}
		// open stream to image path.
	}
}
