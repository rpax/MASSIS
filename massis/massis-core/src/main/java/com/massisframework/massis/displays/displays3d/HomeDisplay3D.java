package com.massisframework.massis.displays.displays3d;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.j3d.HomePieceOfFurniture3D;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.google.inject.Inject;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.Orientation;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.systems.sh3d.BuildingSystem;
import com.massisframework.massis.model.systems.sh3d.SweetHome3DFurniture;
import com.massisframework.massis.model.systems.sh3d.SweetHome3DLevel;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class HomeDisplay3D extends JFrame implements SimulationSystem {

	private static final long serialVersionUID = -6696779235522417183L;
	private HomeControllerDisplay3D planController;
	HomeComponentDisplay3D homeComponent3D;
	private boolean initiated = false;
	@Inject
	private SimulationEngine<?> engine;
	@FilterParams(all = {
			SweetHome3DFurniture.class,
			Position2D.class
	})
	private ComponentFilter<?> furnitureFilter;

	private Home home;
	private boolean ready = false;

	private static Transform3D getPieceOFFurnitureNormalizedModelTransformation(
			HomePieceOfFurniture piece)
	{
		// Set piece size
		final Transform3D scale = new Transform3D();
		float pieceWidth = piece.getWidth();
		// If piece model is mirrored, inverse its width
		if (piece.isModelMirrored())
		{
			pieceWidth *= -1;
		}
		scale.setScale(new Vector3d(pieceWidth, piece.getHeight(), piece
				.getDepth()));
		// Change its angle around y axis
		final Transform3D orientation = new Transform3D();
		orientation.rotY(-piece.getAngle());
		orientation.mul(scale);
		// Translate it to its location
		final Transform3D pieceTransform = new Transform3D();
		float z = piece.getElevation() + piece.getHeight() / 2;
		if (piece.getLevel() != null)
		{
			z += piece.getLevel().getElevation();
		}
		pieceTransform.setTranslation(new Vector3f(piece.getX(), z, piece
				.getY()));
		pieceTransform.mul(orientation);
		return pieceTransform;
	}

	private void init()
	{
		if (engine.getSystem(BuildingSystem.class) == null)
			return;
		System.setProperty("com.eteks.sweethome3d.j3d.useOffScreen3DView",
				"true");
		this.initiated = true;
		this.home = engine.getSystem(BuildingSystem.class).getHome();
		final UserPreferences fileUserPreferences = new FileUserPreferences();

		this.planController = new HomeControllerDisplay3D(this.home,
				fileUserPreferences,
				new SwingViewFactory(), new FileContentManager(
						fileUserPreferences),
				null);
		this.homeComponent3D = new HomeComponentDisplay3D(this.home,
				fileUserPreferences,
				this.planController);

		getContentPane().add(this.homeComponent3D);
		System.err.println("Making outer walls invisible...");
		VisualOps.makeOuterWallsInvisible(this.home);
		System.err.println("done.");

		pack();
		setSize(600, 500);
		registerListeners();
		this.homeComponent3D.removeHomeListeners();
		this.ready = true;

	}

	private void unregisterListeners()
	{
		// planController.exit();
	}

	private void registerListeners()
	{
		// planController.enter();
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
	}

	@Override
	public void initialize()
	{
		setTitle("HomeDisplay3D");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private List<OLDSimulationEntity<?>> entities = new ArrayList<>();

	@Override
	public void update(float deltaTime)
	{

		if (this.isVisible() && !this.initiated)
		{
			init();
		}
		if (!ready)
		{
			return;
		}

		for (OLDSimulationEntity<?> obj : this.engine.getEntitiesFor(
				furnitureFilter,
				entities))
		{

			final HomePieceOfFurniture hpof = obj
					.get(SweetHome3DFurniture.class).getFurniture();

			final long floorId = obj.get(FloorReference.class)
					.getFloorId();

			final Level floorLevel = engine.asSimulationEntity(floorId)
					.get(SweetHome3DLevel.class).getLevel();

			if (hpof == null)
			{
				System.err.println(obj + " has no representation");
				return;
			}
			if (hpof.getLevel() != floorLevel)
			{
				hpof.setLevel(floorLevel);
			}
			hpof.setAngle(
					(float) (obj.get(Orientation.class).getAngle()
							- Math.PI / 2));
			hpof.setX((float) obj.get(Position2D.class).getX());
			hpof.setY((float) obj.get(Position2D.class).getY());
			final HomePieceOfFurniture3D hpof3D = ((HomePieceOfFurniture3D) this.homeComponent3D.homeObjects
					.get(hpof));
			// si todavia no se ha cargado, fuera
			if (hpof3D == null)
			{
				return;
			}
			// animateFurniture3D(hpof3D);
			final Transform3D pieceTransform = getPieceOFFurnitureNormalizedModelTransformation(
					hpof);
			// Change model transformation
			((TransformGroup) hpof3D.getChild(0)).setTransform(pieceTransform);
		}

	}
}
