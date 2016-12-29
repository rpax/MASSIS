package com.massisframework.massis.javafx.test;

import java.io.IOException;

/**
 * 
 */
import javafx.application.Application;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * An application with a zoomable and pannable canvas.
 */
public class JFXTests extends Application {
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		FXMLLoader loader = null;
		// Injector injector = Guice.createInjector(new JFXModule());
		// FXMLLoader loader=injector.getProvider(FXMLLoader.class).get();
		loader = new FXMLLoader(getClass().getResource("BasicApp.fxml"));
		loader.getNamespace().addListener(
				(MapChangeListener<? super String, ? super Object>) evt -> {
					System.out.println("EVENT: "+evt);
				});
		
		// loader.setBuilderFactory(new JFXBuilderFactory());
		// loader.setControllerFactory(injector::getInstance);
		// loader.setBuilderFactory(new BuilderFactory() {
		// @Override
		// public Builder<?> getBuilder(Class<?> type)
		// {
		// return new Builder<Object>() {
		//
		// @Override
		// public Object build()
		// {
		// if (JFXController.class.isAssignableFrom(type))
		// {
		//
		// JFXController obj = (JFXController) injector
		// .getInstance(type);
		// JFXModule.injectFXML(obj);
		// }
		// return null;
		// }
		// };
		// }
		// });
		// launchScene(stage,CameraCanvas.class);
		// launch3D(stage);
		Scene scene;

		// loader.setRoot(new CameraCanvas());
		try
		{

			Parent parent = loader.load();
			
			// JFXController.injectOn(parent);
			scene = new Scene(parent);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void launch3D(Stage stage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 800, 800);
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-1000);
		camera.setNearClip(0.1);
		camera.setFarClip(2000.0);
		camera.setFieldOfView(35);
		scene.setCamera(camera);
		stage.setScene(scene);
		stage.show();

	}

}