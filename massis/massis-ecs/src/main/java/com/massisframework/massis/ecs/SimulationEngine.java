package com.massisframework.massis.ecs;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;
import com.massisframework.massis.ecs.system.Loggable;
import com.massisframework.massis.ecs.system.ai.AISystem;
import com.massisframework.massis.ecs.system.graphics.jfx.FloorSceneGraphSystem;
import com.massisframework.massis.ecs.system.graphics.jfx.HomeObjectsFXSystem;
import com.massisframework.massis.ecs.system.graphics.jfx.JavaFXSystem;
import com.massisframework.massis.ecs.system.location.LocationSystem;
import com.massisframework.massis.ecs.system.sweethome3d.loader.FloorLevelsSystem;
import com.massisframework.massis.ecs.system.sweethome3d.loader.SweetHome3DSystem;

public class SimulationEngine implements Loggable {

	private ScheduledExecutorService executor;
	private long framerate = 1;
	private World world;

	public SimulationEngine()
	{
		/**
		 * @formatter:off
		 */
		WorldConfiguration config = new WorldConfigurationBuilder()
				.with(new EntityLinkManager())
					.with(new SweetHome3DSystem(loadHome()))
					.with(new FloorLevelsSystem())
				.with(new LocationSystem())
				.with(new AISystem())
				.with(new JavaFXSystem())
					.with(new FloorSceneGraphSystem())
					.with(new HomeObjectsFXSystem())
//				.with(new Graphics2DSystem())
//					.with(new FloorLayersSystem())
				.build();
		/**
		 * @formatter:on
		 */
		this.world = new World(config);
	}

	private Home loadHome()
	{
		try
		{
			return new HomeFileRecorder()
					.readHome("src/main/resources/square.sh3d");

		} catch (RecorderException e)
		{
			throw new RuntimeException(e);
		}
	}

	public synchronized void start()
	{
		if (this.executor != null)
			throw new IllegalStateException("Cannot be started twice");
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.executor.scheduleAtFixedRate(this::update, 0,
				1000 / this.framerate, TimeUnit.MILLISECONDS);
	}

	public synchronized void stop()
	{
		if (this.executor == null)
			return;
		this.executor.shutdown();
		try
		{
			this.executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		this.executor = null;
		this.world.dispose();
	}

	private void update()
	{
		try
		{
			this.world.setDelta(1f / this.framerate);
			this.world.process();
		} catch (Exception e)
		{
			logger().log(Level.SEVERE, "Error when processing simulation", e);
		}
	}

	public static void main(String[] args)
	{
		SimulationEngine engine = new SimulationEngine();
		engine.start();
		Scanner sc = new Scanner(System.in);
		new Thread(() -> {
			try
			{
				Thread.sleep(60000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}).start();
		while (!sc.nextLine().trim().toLowerCase().equals("quit"))
		{
		}
		engine.stop();
	}
}
