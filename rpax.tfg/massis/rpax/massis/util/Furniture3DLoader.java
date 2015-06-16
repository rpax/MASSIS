package rpax.massis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.building.Building;
import rpax.massis.model.location.Location;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.sh3d.plugins.metadata.MASSISHomeMetadataManager;
import cern.colt.Arrays;

import com.eteks.sweethome3d.model.CatalogPieceOfFurniture;
import com.eteks.sweethome3d.model.Content;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.tools.URLContent;
/**
 * Class for loading 3D objects into MASSIS
 * @author rpax
 *
 */
public final class Furniture3DLoader {

	public static HomePieceOfFurniture load3DFurniture(
			String modelPropertiesFile) throws RecorderException,
			FileNotFoundException, IOException {

		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(modelPropertiesFile)));

		String id = null;
		String name = properties.getProperty("name");
		String description = "";

		String iconPath = properties.getProperty("icon");
		Content icon = getContent(iconPath);
		String modelPath = properties.getProperty("model");
		float width = Float.parseFloat(properties.getProperty("width"));
		float depth = Float.parseFloat(properties.getProperty("depth"));
		float height = Float.parseFloat(properties.getProperty("height"));
		boolean movable = true;

		float elevation = 0;

		float[][] modelRotation = new float[][] { { 1, 0, 0 }, { 0, 1, 0 },
				{ 0, 0, 1 } };
		String creator = null;
		boolean resizable = true;

		BigDecimal price = null;
		BigDecimal valueAddedTaxPercentage = null;
		Content model = getContent(modelPath);

		/**
		 * @formatter:on
		 */
		CatalogPieceOfFurniture cpof = new CatalogPieceOfFurniture(id, name,
				description, icon, model, width, depth, height, elevation,
				movable, modelRotation, creator, resizable, price,
				valueAddedTaxPercentage);

		return new HomePieceOfFurniture(cpof);
	}

	private static Content getContent(String contentPath)
			throws MalformedURLException {
		return new URLContent(new File(contentPath).toURI().toURL());
	}

	public static <AGENT extends Agent> AGENT insert3DAgentElementIntoSimulation(
			Location l, Building building, String name,
			String furniture3DPropertiesFilePath, Class<AGENT> agentClass)
			throws NoSuchMethodException, SecurityException,
			FileNotFoundException, RecorderException, IOException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		//
		HomePieceOfFurniture hpof = Furniture3DLoader
				.load3DFurniture(furniture3DPropertiesFilePath);

		double x = l.getX();
		double y = l.getY();

		hpof.setX((float) x);
		hpof.setY((float) y);
		hpof.setLevel(l.getFloor().getLevel());
		hpof.setName(name == null ? "NONAME" : name);
		// hpof.setColor(checkpointColors[checkPointNumber %
		// checkpointColors.length]);
		building.getHome().addPieceOfFurniture(hpof);
		Map<String, String> metadata = MASSISHomeMetadataManager
				.getHomeMetaData(building.getHome()).getMetadata(hpof);
		SimLocation checkPointLoc = new SimLocation(hpof, l.getFloor());
		System.out.println(Arrays.toString(agentClass.getConstructors()));

		Constructor<AGENT> constructor = agentClass.getConstructor(
				java.util.Map.class, SimLocation.class, MovementManager.class,
				EnvironmentManager.class,String.class);
		AGENT agent = constructor.newInstance(metadata, checkPointLoc,
				building.getMovementManager(), building.getEnvironmentManager(),""

		);
		building.addSH3DRepresentation(agent, hpof);
		l.getFloor().addPerson(agent);
		// this.setLocation(checkPoint, l);
		agent.getLocation();
		// this.checkPoints.put(checkPointNumber, checkPoint);

		System.err.println("3D agent created at " + agent.getLocation());
		return agent;
	}
}
