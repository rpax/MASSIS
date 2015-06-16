package rpax.massis.util.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import rpax.massis.model.building.SimulationObject;
import rpax.massis.util.logs.file.LogFileWriter;
import rpax.massis.util.logs.file.async.AsyncLogFileWriter;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class SimulationSaver implements Steppable, Stoppable {

	private static final long serialVersionUID = 1L;
	long currentStep;
	private boolean closed = false;
	private final HashSet<SimulationObject> changedObjects = new HashSet<>();
	
	private LogFileWriter writer;
	private final String file;

	
	public SimulationSaver(String filePath) throws IOException {
		this.file = filePath;
		try
		{
			
			if (new File(file).exists())
				new File(file).delete();
			this.writer = new AsyncLogFileWriter(file);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		if (this.closed)
			return;
		try
		{
			this.writer.close();
			this.closed = true;

			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.stop();
	}

	@Override
	public void step(SimState simState) {
		this.currentStep = simState.schedule.getSteps();
		try
		{
			
			JsonState[] states = new JsonState[this.changedObjects.size()];
			int i=0;
			for (SimulationObject so : this.changedObjects)
			{
				states[i]=so.getState();
				i++;
			}
			this.writer.write(this.currentStep, states);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		this.changedObjects.clear();

	}

	public void notifyChanged(SimulationObject simulationObject) {
		if (!this.changedObjects.contains(simulationObject))
		{
			
			this.changedObjects.add(simulationObject);
		}

	}

}
