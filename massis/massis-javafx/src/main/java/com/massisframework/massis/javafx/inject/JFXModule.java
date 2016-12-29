package com.massisframework.massis.javafx.inject;

import java.io.IOException;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

public class JFXModule extends AbstractModule {

	@Override
	public void configure()
	{
//		Matcher typeMatcher = Matchers.subclassesOf(JFXController.class);
//		TypeListener listener = new TypeListener() {
//			@Override
//			public <I> void hear(TypeLiteral<I> type,
//					TypeEncounter<I> encounter)
//			{
//				System.out.println("hear()");
//				encounter.register(new InjectionListener<I>() {
//					@Override
//					public void afterInjection(Object i)
//					{
//						System.out.println("After Injection!");
//						JFXController m = (JFXController) i;
//						injectFXML(m);
//					}
//				});
//
//			}
//		};
//		bindListener(typeMatcher, listener);
	}

//	@Provides
//	public FXMLLoader newFXMLLoader(Injector injector)
//	{
//		FXMLLoader loader=new FXMLLoader();
//		loader.setControllerFactory(new Callback<Class<?>, Object>() {
//			@Override
//			public Object call(Class<?> param)
//			{
//				System.out.println("New instance");
//				return injector.getInstance(param);
//			}
//		});
//		return loader;
//	}

	public static void injectFXML(JFXController obj)
	{
		FXMLLoader fxmlLoader = new FXMLLoader(obj.getFMLLocation());
		fxmlLoader.setRoot(obj);
		fxmlLoader.setController(obj);
		try
		{
			fxmlLoader.load();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
