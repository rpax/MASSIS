package com.massisframework.massis.sim;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;
import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.Building.BuildingProgressMonitor;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataHomeRecorder;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataReader;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;

import sim.engine.MakesSimState;
import sim.engine.SimState;

public abstract class AbstractSimulation extends SimState {

	private boolean finishCalled = false;
	protected final String resourcesPath;
	protected BuildingProgressMonitor buildingProgress;
	protected File buildingFile;
	protected String outputFileLocation;
	protected Building building;

	public AbstractSimulation(long seed, String buildingFilePath, String resourcesPath, String outputFileLocation,
			BuildingProgressMonitor buildingProgress) {
		super(seed);
		this.buildingFile = new File(buildingFilePath);
		this.resourcesPath = resourcesPath;
		this.outputFileLocation = outputFileLocation;
		this.buildingProgress = buildingProgress;
	}

	public AbstractSimulation(long seed, String buildingFilePath, String resourcesPath, String logFileLocation) {
		this(seed, buildingFilePath, resourcesPath, logFileLocation, null);
	}

	private static final long serialVersionUID = 575438688820685250L;

	public static void runSimulation(final Class<? extends AbstractSimulation> c, String[] args) {

		if (!keyExists("-building", args)) {
			System.err.println("Building filepath argument not provided. Exiting now");
			System.exit(-1);
		}

		final String buildingFilePath = argumentForKey("-building", args);
		final String saveLocation = argumentForKey("-logfile", args);
		final String resourcesPath = argumentForKey("-resources", args);
		doLoop(new MakesSimState() {
			@Override
			public SimState newInstance(long seed, String[] args) {
				try {
					return (c.getDeclaredConstructor(
							/**
							 * Seed
							 */
							Long.TYPE,
							/**
							 * buildingFilePath
							 */
							String.class,
							/**
							 * resourcesPath
							 */
							String.class,
							/**
							 * saveLocation
							 */
							String.class).newInstance(seed, buildingFilePath, resourcesPath, saveLocation));
				} catch (final Exception e) {
					final StringBuilder sb = new StringBuilder();
					sb.append("Exception occurred while trying to construct the simulation ");
					sb.append(c);
					sb.append("\n");
					sb.append("Available constructors: \n");
					for (@SuppressWarnings("rawtypes") final
					Constructor constructor : c.getDeclaredConstructors()) {
						sb.append(Arrays.toString(constructor.getParameterTypes()));
						sb.append("\n");
					}

					throw new RuntimeException(sb.toString(), e);

				}
			}

			@Override
			public Class<? extends AbstractSimulation> simulationClass() {
				return c;
			}
		}, args);

		System.exit(0);
	}

	@Override
	public void start() {
		try {
			this.building = this.createBuilding();
		} catch (final RecorderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.start();

	}

	protected Building createBuilding() throws RecorderException {
		final HomeFileRecorder recorder = new AdditionalDataHomeRecorder(
				(AdditionalDataReader) new HomeMetadataLoader());
		final Home home = recorder.readHome(this.buildingFile.getAbsolutePath());
		if (this.buildingProgress != null) {
			return new Building(home, this.resourcesPath, this.buildingProgress);
		} else {
			return new Building(home, this.resourcesPath);
		}

	}

	static boolean keyExists(String key, String[] args) {
		for (int x = 0; x < args.length; x++) {
			if (args[x].equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	static String argumentForKey(String key, String[] args) {
		for (int x = 0; x < args.length - 1; x++)
		// if a key has an argument, it can't be the last string
		{
			if (args[x].equalsIgnoreCase(key)) {
				return args[x + 1];
			}
		}
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		this.finish();
	}

	@Override
	public void finish() {
		if (!this.finishCalled) {
			this.finishCalled = true;
			this.endSimulation();

		}
	}

	protected abstract void endSimulation();

	public Building getBuilding()
	{
		return this.building;
	}
}
