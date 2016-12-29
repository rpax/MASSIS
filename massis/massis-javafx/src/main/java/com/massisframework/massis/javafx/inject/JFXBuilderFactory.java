package com.massisframework.massis.javafx.inject;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class JFXBuilderFactory implements BuilderFactory {

	private JavaFXBuilderFactory javaFXBuilderFactory;

	public JFXBuilderFactory()
	{
		this.javaFXBuilderFactory = new JavaFXBuilderFactory();
	}

	@Override
	public Builder<?> getBuilder(Class<?> type)
	{
		if (JFXController.class.isAssignableFrom(type))
		{
			return new JFXBuider<>(type);
		} else
		{
			return javaFXBuilderFactory.getBuilder(type);
		}
	}

	private static class JFXBuider<T extends JFXController>
			implements Builder<T> {

		private Class<?> type;

		public JFXBuider(Class<?> type)
		{
			this.type = type;
		}

		@Override
		public T build()
		{
			try
			{
				T item = (T) type.newInstance();
				FXMLLoader fxmlLoader = new FXMLLoader(item.getFMLLocation());
				fxmlLoader.setBuilderFactory(new JFXBuilderFactory());
				fxmlLoader.setRoot(item);
				fxmlLoader.setController(item);
				fxmlLoader.load();
				// item.inject();

				return item;
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}

		}
	}
}
