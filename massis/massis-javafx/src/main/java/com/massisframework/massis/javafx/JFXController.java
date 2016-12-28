package com.massisframework.massis.javafx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public interface JFXController {

	public default void inject()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass()
				.getResource(getClass().getSimpleName().concat(".fxml")));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try
		{
			fxmlLoader.load();
		} catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}
	}
}
