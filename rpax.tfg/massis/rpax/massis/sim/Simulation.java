package rpax.massis.sim;

import java.io.IOException;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.building.Building.BuildingProgressMonitor;
import rpax.massis.model.building.Floor;
import rpax.massis.model.building.SimRoom;
import rpax.massis.util.io.SimulationSaver;

public class Simulation extends AbstractSimulation {

	private static final long serialVersionUID = 1L;
	protected SimulationSaver simulationSaver;

	public Simulation(long seed, String buildingFilePath, String resourcesPath,
			BuildingProgressMonitor buildingProgress, String logFileLocation)
			throws IOException {
		super(seed, buildingFilePath, resourcesPath, buildingProgress,
				logFileLocation);
		if (this.logFileLocation != null)
		{
			this.simulationSaver = new SimulationSaver(this.logFileLocation);
		}

	}

	public Simulation(long seed, String buildingFilePath, String resourcesPath,
			String saveLocation) throws IOException {
		this(seed, buildingFilePath, resourcesPath, null, saveLocation);
	}

	public SimulationSaver getSimulationSaver() {
		return simulationSaver;
	}

	@Override
	public void start() {
		super.start();
		// Schedule a todos los agentes
		for (Floor f : this.building.getFloors())
		{
			for (Agent a : f.getPeople())
			{
				this.schedule.scheduleRepeating(a);
			}
			// todos los cuartos -> Cached values
			for (SimRoom r : f.getRooms())
			{
				this.schedule.scheduleRepeating(r);
			}
		}
		if (this.simulationSaver != null)
		{
			this.schedule.scheduleRepeating(this.simulationSaver);
		}
	}

	@Override
	protected void endSimulation() {
		for (Floor f : this.building.getFloors())
		{
			for (Agent a : f.getPeople())
			{
				a.stop();
			}
			// todos los cuartos
			for (SimRoom r : f.getRooms())
			{
				r.stop();
			}
		}
		if (this.simulationSaver != null)
			this.simulationSaver.stop();
	}

}
