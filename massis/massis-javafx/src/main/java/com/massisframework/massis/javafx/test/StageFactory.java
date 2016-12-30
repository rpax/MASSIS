package com.massisframework.massis.javafx.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class StageFactory extends Application {

	private static final AtomicBoolean LAUNCHED = new AtomicBoolean(false);
	private static final CountDownLatch APP_LATCH = new CountDownLatch(1);
	private static StageFactory INSTANCE = null;

	public static void shutdown()
	{
		Platform.exit();
	}

	public static void initialize()
	{
		start();
	}

	public static void runLater(Runnable r)
	{
		start().thenAccept(sf -> {
			r.run();
		});
	}
	
	public static void newStage(Consumer<Stage> action)
	{
		logger().info(() -> "Reguesting new Stage");
		start().thenAccept(sf -> {
			logger().info(() -> "Returning stage");
			action.accept(new Stage());
		});
	}

	public static Future<Stage> newStage()
	{
		CompletableFuture<Stage> cf = new CompletableFuture<>();
		newStage(cf::complete);
		return cf;
	}

	private static CompletableFuture<StageFactory> start()
	{

		CompletableFuture<StageFactory> cf = new CompletableFuture<>();
		if (!LAUNCHED.getAndSet(true))
		{
			logger().info(() -> "Launching application instance");
			Thread t = new Thread(() -> {
				Application.launch(StageFactory.class);
			});
			t.start();

		}
		try
		{
			APP_LATCH.await();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		Platform.runLater(() -> {
			logger().info(() -> "Returning instance");
			cf.complete(INSTANCE);
		});
		return cf;
	}

	@Override
	public void init() throws Exception
	{
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Platform.setImplicitExit(false);
		INSTANCE = this;
		System.err.println("start(primaryStage). Firing latch");
		APP_LATCH.countDown();
	}

	@Override
	public void stop() throws Exception
	{
		super.stop();
	}

	private static Logger logger()
	{
		return Logger.getLogger(StageFactory.class.getName());
	}

}
