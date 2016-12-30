package com.massisframework.massis.javafx.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ApplicationLauncher extends Application {

	private static CountDownLatch latch = new CountDownLatch(1);
	private static Application application;
	private static AtomicInteger active = new AtomicInteger(0);

	public static void main(String[] args)
	{
		for (int i = 0; i < 3; i++)
		{
			launchWrappedApplication((stage, app) -> {
				Scene scene = new Scene(new AnchorPane());
				stage.setScene(scene);
				stage.show();
			});

		}
		System.err.println("--------Sleeping");
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.err.println("Running again");
		for (int i = 0; i < 3; i++)
		{
			launchWrappedApplication((stage, app) -> {
				Scene scene = new Scene(new AnchorPane());
				stage.setScene(scene);
				stage.show();
			});
		}
	}

	public static <T extends Application> void launchWrappedApplication(
			BiConsumer<Stage, Application> action,
			String... args)
	{
		launchWrappedApplication_internal(action, args);
	}

	public static <T extends Application> void launchWrappedApplication_internal(
			BiConsumer<Stage, Application> action,
			String... args)
	{

		synchronized (latch)
		{
			if (application == null)
			{
				Thread t = new Thread(() -> {
					Application.launch(ApplicationLauncher.class, args);
				});
				t.start();
			} else
			{
				System.err.println("Already active: " + active + ". Waiting");
			}
			try
			{
				latch.await();
				System.err.println("Waken up. Active count " + active);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setOnHidden(evt -> {
				System.err.println("Hiding!. Active stages: " + active);
			});
			action.accept(stage, application);
		});

	}

	@Override
	public void init() throws Exception
	{
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		try
		{
			Platform.setImplicitExit(false);
			primaryStage.close();
			application = this;
			latch.countDown();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception
	{
		super.stop();
	}

}
