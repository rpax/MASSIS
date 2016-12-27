package com.massisframework.massis.ecs;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;
import com.massisframework.massis.ecs.system.graphics.FloorLayersSystem;
import com.massisframework.massis.ecs.system.graphics.Graphics2DSystem;
import com.massisframework.massis.ecs.system.sweethome3d.FloorSystem;
import com.massisframework.massis.ecs.system.sweethome3d.SweetHome3DSystem;

public class SimulationEngine {

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
				.with(new FloorSystem())
				.with(new Graphics2DSystem())
					.with(new FloorLayersSystem())
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
	}

	private void update()
	{
		this.world.setDelta(1f / this.framerate);
		this.world.process();
	}

	public static void main(String[] args)
	{
		SimulationEngine engine = new SimulationEngine();
		engine.start();
		Scanner sc = new Scanner(System.in);
		while (!sc.nextLine().trim().toLowerCase().equals("quit"))
		{
		}
		engine.stop();
	}
}
