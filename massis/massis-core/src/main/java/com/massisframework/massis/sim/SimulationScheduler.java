package com.massisframework.massis.sim;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface SimulationScheduler {

	public <T> Future<T> enqueueTask(Callable<T> task);

	public void enqueueTask(Runnable task);
	
	public void scheduleRepeating(SimulationSteppable steppable);

	void removeFromSchedule(SimulationSteppable steppable);
	
	public void start();
}
