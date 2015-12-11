package com.massisframework.testdata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class SampleHomeDescription {

	private String shortdescription;
	private String filename;
	private String description;
	private String image;
	private URL imageURL;

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

	public String getImage() throws IOException
	{
		return this.image;
	}

	public URL getImageURL()
	{
		return this.imageURL;
	}

	protected void setImageURL(URL imageURL)
	{
		this.imageURL = imageURL;
	}

}
