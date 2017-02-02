package com.massisframework.massis.sim.scheduler.mason;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.SimulationSteppable;

import sim.display.Console;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Stoppable;

@SuppressWarnings("serial")
class MasonScheduler extends SimState implements SimulationScheduler {

	private Map<SimulationSteppable, Stoppable> steppableMap;

	public MasonScheduler()
	{
		super(System.nanoTime());
		this.steppableMap = new HashMap<>();
		System.out.println("Created " + this.getClass());
		Console c = new Console(new MasonGUI(this));
		c.setIncrementSeedOnStop(false);
		//
		c.pressPlay();
		c.pressPause();
		c.setVisible(true);
	}

	private static class MasonGUI extends GUIState {

		public MasonGUI(SimState state)
		{
			super(state);
		}

	}

	@Override
	public <T> Future<T> enqueueTask(Callable<T> task)
	{
		CompletableFuture<T> fut = new CompletableFuture<>();
		this.schedule.scheduleOnce((ss) -> {
			try
			{
				T r = task.call();
				fut.complete(r);
			} catch (Exception e)
			{
				fut.completeExceptionally(e);
			}
		});
		return fut;
	}

	@Override
	public void start()
	{
		super.start();
	}

	@Override
	public void enqueueTask(Runnable task)
	{
		this.schedule.scheduleOnce((ss) -> task.run());
	}

	@Override
	public void scheduleRepeating(final SimulationSteppable steppable)
	{
		Stoppable stoppable = this.schedule
				.scheduleRepeating(new SteppableWrapper(steppable));
		this.steppableMap.put(steppable, stoppable);
	}

	@Override
	public void removeFromSchedule(final SimulationSteppable steppable)
	{
		Stoppable stoppable = this.steppableMap.remove(steppable);
		stoppable.stop();
	}

}
