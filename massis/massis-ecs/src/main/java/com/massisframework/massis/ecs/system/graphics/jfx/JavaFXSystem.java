package com.massisframework.massis.ecs.system.graphics.jfx;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.artemis.BaseSystem;
import com.massisframework.massis.javafx.test.StageFactory;

import javafx.animation.AnimationTimer;

public class JavaFXSystem extends BaseSystem {

	private Queue<Runnable> javaFXTasks;
	private Queue<JFXTickListener> initializingListeners;
	private Collection<JFXTickListener> runningListeners;
	private AnimationTimer animationTimer;

	public JavaFXSystem()
	{

	}

	@Override
	protected void initialize()
	{
		super.initialize();
		this.initializingListeners = new ConcurrentLinkedQueue<>();
		this.runningListeners = new ConcurrentLinkedQueue<>();
		this.javaFXTasks = new ConcurrentLinkedQueue<>();
		StageFactory.runLater(this::initFX);
	}

	public void runOnJFXThread(Runnable r)
	{
		this.javaFXTasks.add(r);
	}

	private void initFX()
	{
		this.animationTimer = createAnimationTimer(this::javaFXTick);
		this.animationTimer.start();
	}

	private void javaFXTick(final float tpf)
	{
		while (!this.javaFXTasks.isEmpty())
		{
			this.javaFXTasks.poll().run();
		}
		for (JFXTickListener l : this.runningListeners)
		{
			l.javaFXTick(tpf);
		}
		while (!this.initializingListeners.isEmpty())
		{
			JFXTickListener l = this.initializingListeners.poll();
			l.javaFXInitialize();
			this.runningListeners.add(l);
		}
	}

	@Override
	protected void processSystem()
	{

	}

	@Override
	protected void dispose()
	{
		super.dispose();
		StageFactory.shutdown();
	}

	public void addJFXTickListener(JFXTickListener listener)
	{
		this.initializingListeners.add(listener);
	}

	public void removeJFXTickListener(JFXTickListener listener)
	{
		this.initializingListeners.remove(listener);
		this.runningListeners.remove(listener);
	}

	private static AnimationTimer createAnimationTimer(Consumer<Float> action)
	{
		return new AnimationTimer() {
			private long before = -1;
			private final float ONE_SECOND = 1000000000.0f;

			@Override
			public void handle(long now)
			{
				if (before > 0)
				{
					action.accept((now - before) / ONE_SECOND);
				}
				before = now;
			}
		};
	}

	public static interface JFXTickListener {

		public void javaFXInitialize();

		public void javaFXTick(float tpf);
	}

}
