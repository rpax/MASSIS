package com.massisframework.massis.javafx.test;

import java.io.IOException;

/**
 * 
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

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
		// launchScene(stage,CameraCanvas.class);
		// launch3D(stage);
		Scene scene;

		// loader.setRoot(new CameraCanvas());
		FXMLLoader loader = new FXMLLoader();
		try
		{
			Parent parent = loader
					.load(getClass().getResource("BasicApp.fxml"));
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