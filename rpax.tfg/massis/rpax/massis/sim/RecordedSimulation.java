package rpax.massis.sim;

import java.io.IOException;

import rpax.massis.launcher.LauncherProgressMonitor;
import rpax.massis.model.building.Building;
import rpax.massis.util.io.JsonState;
import rpax.massis.util.logs.file.LogFileReader;
import rpax.massis.util.logs.file.async.AsyncLogFileReader;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

import com.eteks.sweethome3d.model.RecorderException;

public class RecordedSimulation extends AbstractSimulation {

	private static final long serialVersionUID = 1L;
	private SimulationPlayer simPlayer;

	public RecordedSimulation(long seed, String buildingFilePath,
			String resourcesPath, LauncherProgressMonitor progressMonitor,
			String logFileLocation) {
		super(seed, buildingFilePath, resourcesPath, progressMonitor,
				logFileLocation);
	}

	public RecordedSimulation(long seed, String buildingFilePath,
			String resourcesPath, String logFileLocation) {
		super(seed, buildingFilePath, resourcesPath, logFileLocation);
	}

	@Override
	public void start() {
		super.start();
		try
		{
			this.simPlayer = new SimulationPlayer(this.logFileLocation);
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new RuntimeException(e);
		}
		this.schedule.scheduleRepeating(this.simPlayer);
	}

	@Override
	protected void endSimulation() {
		this.simPlayer.stop();
	}

	@Override
	protected Building createBuilding() throws RecorderException {
		if (buildingProgress != null)
			return new Building(this, buildingFilePath, this.resourcesPath,
					this.buildingProgress);
		else
			return super.createBuilding();
	}

	private class SimulationPlayer implements Steppable, Stoppable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final LogFileReader lr;

		public SimulationPlayer(String zipFilePath)
				throws ClassNotFoundException, IOException {
			lr = new AsyncLogFileReader(zipFilePath);
		}

		@Override
		public void step(SimState state) {
			try
			{

				for (JsonState ks : lr.nextStep())
				{
					ks.restore(building);
				}

			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void stop() {
			try
			{
				this.lr.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

}
