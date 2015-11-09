package com.massisframework.massis.archetypes.simulator.sim;

import com.massisframework.massis.app.LauncherProgressMonitor;
import com.massisframework.massis.sim.Simulation;
import com.massisframework.massis.sim.SimulationWithUI;

import sim.display.Console;
import sim.display.GUIState;

public class GUILauncher {

	public static void main(String[] args) {

		final String buildingFilePath = ClassLoader.getSystemResource("tutorial.sh3d").getFile();
		/*
		 * Not needed, really. We are not going to load any kind of resources
		 * during the simulation.
		 */
		final String resourceFolderPath = "";

		Simulation simState = new Simulation(System.currentTimeMillis(), buildingFilePath, resourceFolderPath, null,
				new LauncherProgressMonitor());
		GUIState vid = new SimulationWithUI(simState);

		Console c = new Console(vid);

		c.setIncrementSeedOnStop(false);
		//
		c.pressPlay();
		c.pressPause();
		c.setVisible(true);

	}

}
