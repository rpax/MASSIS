package com.massisframework.massis.javafx;

import java.io.IOException;
import java.net.URL;

import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

public interface JFXController {

	public default URL getFMLLocation()
	{
		return getClass()
				.getResource(getClass().getSimpleName().concat(".fxml"));
	}
	
	public default void inject()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(this.getFMLLocation());
			fxmlLoader.setRoot(this);
			fxmlLoader.setController(this);
			fxmlLoader.load();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
