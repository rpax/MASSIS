package com.massisframework.massis.displays.displays3d;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.undo.UndoableEditSupport;

import com.eteks.sweethome3d.model.Camera;
import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.model.Wall;
import com.eteks.sweethome3d.viewcontroller.ContentManager;
import com.eteks.sweethome3d.viewcontroller.HomeController3D;
import com.eteks.sweethome3d.viewcontroller.ViewFactory;

public class HomeControllerDisplay3D extends HomeController3D {

	private Home home;
	private SimulationTopCameraState simulationTopCameraState;

	public HomeControllerDisplay3D(Home home, UserPreferences preferences,
			ViewFactory viewFactory, ContentManager contentManager,
			UndoableEditSupport undoSupport)
	{
		super(home, preferences, viewFactory, contentManager, undoSupport);
		this.home = home;
		this.simulationTopCameraState = new SimulationTopCameraState(
				preferences);
		setCameraState(
				home.getCamera() == home.getTopCamera()
						? this.simulationTopCameraState
						: this.getObserverCameraState());
	}

	public void exit()
	{
		this.getTopCameraState().exit();
		this.getObserverCameraState().exit();
	}

	public void enter()
	{
		(home.getCamera() == home.getTopCamera() ? this.simulationTopCameraState
				: this.getObserverCameraState()).enter();
	}

	/**
	 * Top camera controller state.
	 */
	private class SimulationTopCameraState extends CameraControllerState {

		private final float MIN_WIDTH = 1000;
		private final float MIN_DEPTH = 1000;
		private final float MIN_HEIGHT = 20;
		private Camera topCamera;
		private float[] aerialViewBoundsLowerPoint;
		private float[] aerialViewBoundsUpperPoint;
		private final float minDistanceToAerialViewCenter = 10;
		private final float maxDistanceToAerialViewCenter = Float.MAX_VALUE / 2;
		private boolean aerialViewCenteredOnSelectionEnabled;

		public SimulationTopCameraState(UserPreferences preferences)
		{
			this.aerialViewCenteredOnSelectionEnabled = preferences
					.isAerialViewCenteredOnSelectionEnabled();
			preferences
					.addPropertyChangeListener(
							UserPreferences.Property.AERIAL_VIEW_CENTERED_ON_SELECTION_ENABLED,
							new UserPreferencesChangeListener(this));
		}

		@Override
		public void enter()
		{
			this.topCamera = home.getCamera();
			updateCameraFromHomeBounds(false);
			// for (Level level : home.getLevels())
			// {
			// level.addPropertyChangeListener(this.objectChangeListener);
			// }
			// home.addLevelsListener(this.levelsListener);
			// for (Wall wall : home.getWalls())
			// {
			// wall.addPropertyChangeListener(this.objectChangeListener);
			// }
			// home.addWallsListener(this.wallsListener);
			// for (HomePieceOfFurniture piece : home.getFurniture())
			// {
			// piece.addPropertyChangeListener(this.objectChangeListener);
			// }
			// home.addFurnitureListener(this.furnitureListener);
			// for (Room room : home.getRooms())
			// {
			// room.addPropertyChangeListener(this.objectChangeListener);
			// }
			// home.addRoomsListener(this.roomsListener);
			// home.addSelectionListener(this.selectionListener);
		}

		/**
		 * Sets whether aerial view should be centered on selection or not.
		 */
		public void setAerialViewCenteredOnSelectionEnabled(
				boolean aerialViewCenteredOnSelectionEnabled)
		{
			this.aerialViewCenteredOnSelectionEnabled = aerialViewCenteredOnSelectionEnabled;
			updateCameraFromHomeBounds(false);
		}

		/**
		 * Updates camera location from home bounds.
		 */
		private void updateCameraFromHomeBounds(
				boolean firstPieceOfFurnitureAddedToEmptyHome)
		{
			if (this.aerialViewBoundsLowerPoint == null)
			{
				updateAerialViewBounds(
						this.aerialViewCenteredOnSelectionEnabled);
			}
			float distanceToCenter = getCameraToAerialViewCenterDistance();
			updateAerialViewBounds(this.aerialViewCenteredOnSelectionEnabled);
			updateCameraIntervalToAerialViewCenter();
			placeCameraAt(distanceToCenter,
					firstPieceOfFurnitureAddedToEmptyHome);
		}

		/**
		 * Returns the distance between the current camera location and home
		 * bounds center.
		 */
		private float getCameraToAerialViewCenterDistance()
		{
			return 0;
			// return (float) Math
			// .sqrt(Math
			// .pow((this.aerialViewBoundsLowerPoint[0] +
			// this.aerialViewBoundsUpperPoint[0])
			// / 2 - this.topCamera.getX(), 2)
			// + Math.pow(
			// (this.aerialViewBoundsLowerPoint[1] +
			// this.aerialViewBoundsUpperPoint[1])
			// / 2 - this.topCamera.getY(), 2)
			// + Math.pow(
			// (this.aerialViewBoundsLowerPoint[2] +
			// this.aerialViewBoundsUpperPoint[2])
			// / 2 - this.topCamera.getZ(), 2));
		}

		private boolean boundsComputed = false;

		/**
		 * Sets the bounds that includes walls, furniture and rooms, or only
		 * selected items if <code>centerOnSelection</code> is <code>true</code>
		 * .
		 */
		private void updateAerialViewBounds(boolean centerOnSelection)
		{
			if (boundsComputed)
			{
				return;
			} else
			{
				boundsComputed = true;
			}
			this.aerialViewBoundsLowerPoint = this.aerialViewBoundsUpperPoint = null;
			List<Selectable> selectedItems = Collections.emptyList();
			if (centerOnSelection)
			{
				selectedItems = new ArrayList<Selectable>();
				for (Selectable item : home.getSelectedItems())
				{
					if (item instanceof Elevatable
							&& isItemAtVisibleLevel((Elevatable) item)
							&& (!(item instanceof HomePieceOfFurniture)
									|| ((HomePieceOfFurniture) item)
											.isVisible()))
					{
						selectedItems.add(item);
					}
				}
			}
			boolean selectionEmpty = selectedItems.size() == 0
					|| !centerOnSelection;

			// Compute plan bounds to include rooms, walls and furniture
			boolean containsVisibleWalls = false;
			for (Wall wall : selectionEmpty ? home.getWalls() : Home
					.getWallsSubList(selectedItems))
			{
				if (isItemAtVisibleLevel(wall))
				{
					containsVisibleWalls = true;

					float wallElevation = wall.getLevel() != null ? wall
							.getLevel().getElevation() : 0;
					float minZ = selectionEmpty ? 0 : wallElevation;

					Float height = wall.getHeight();
					float maxZ;
					if (height != null)
					{
						maxZ = wallElevation + height;
					} else
					{
						maxZ = wallElevation + home.getWallHeight();
					}
					Float heightAtEnd = wall.getHeightAtEnd();
					if (heightAtEnd != null)
					{
						maxZ = Math.max(maxZ, wallElevation + heightAtEnd);
					}
					for (float[] point : wall.getPoints())
					{
						updateAerialViewBounds(point[0], point[1], minZ, maxZ);
					}
				}
			}

			for (HomePieceOfFurniture piece : selectionEmpty ? home
					.getFurniture() : Home.getFurnitureSubList(selectedItems))
			{
				if (piece.isVisible() && isItemAtVisibleLevel(piece))
				{
					float minZ;
					float maxZ;
					if (selectionEmpty)
					{
						minZ = Math.max(0, piece.getGroundElevation());
						maxZ = Math.max(0,
								piece.getGroundElevation() + piece.getHeight());
					} else
					{
						minZ = piece.getGroundElevation();
						maxZ = piece.getGroundElevation() + piece.getHeight();
					}
					for (float[] point : piece.getPoints())
					{
						updateAerialViewBounds(point[0], point[1], minZ, maxZ);
					}
				}
			}

			for (Room room : selectionEmpty ? home.getRooms() : Home
					.getRoomsSubList(selectedItems))
			{
				if (isItemAtVisibleLevel(room))
				{
					float minZ = 0;
					float maxZ = MIN_HEIGHT;
					Level roomLevel = room.getLevel();
					if (roomLevel != null)
					{
						minZ = roomLevel.getElevation()
								- roomLevel.getFloorThickness();
						maxZ = roomLevel.getElevation();
						if (selectionEmpty)
						{
							minZ = Math.max(0, minZ);
							maxZ = Math.max(MIN_HEIGHT,
									roomLevel.getElevation());
						}
					}
					for (float[] point : room.getPoints())
					{
						updateAerialViewBounds(point[0], point[1], minZ, maxZ);
					}
				}
			}

			if (this.aerialViewBoundsLowerPoint == null)
			{
				this.aerialViewBoundsLowerPoint = new float[] {
						0, 0, 0
				};
				this.aerialViewBoundsUpperPoint = new float[] {
						MIN_WIDTH,
						MIN_DEPTH, MIN_HEIGHT
				};
			} else if (containsVisibleWalls && selectionEmpty)
			{
				// If home contains walls, ensure bounds are always minimum 10
				// meters wide centered in middle of 3D view
				if (MIN_WIDTH > this.aerialViewBoundsUpperPoint[0]
						- this.aerialViewBoundsLowerPoint[0])
				{
					this.aerialViewBoundsLowerPoint[0] = (this.aerialViewBoundsLowerPoint[0]
							+ this.aerialViewBoundsUpperPoint[0])
							/ 2 - MIN_WIDTH / 2;
					this.aerialViewBoundsUpperPoint[0] = this.aerialViewBoundsLowerPoint[0]
							+ MIN_WIDTH;
				}
				if (MIN_DEPTH > this.aerialViewBoundsUpperPoint[1]
						- this.aerialViewBoundsLowerPoint[1])
				{
					this.aerialViewBoundsLowerPoint[1] = (this.aerialViewBoundsLowerPoint[1]
							+ this.aerialViewBoundsUpperPoint[1])
							/ 2 - MIN_DEPTH / 2;
					this.aerialViewBoundsUpperPoint[1] = this.aerialViewBoundsLowerPoint[1]
							+ MIN_DEPTH;
				}
				if (MIN_HEIGHT > this.aerialViewBoundsUpperPoint[2]
						- this.aerialViewBoundsLowerPoint[2])
				{
					this.aerialViewBoundsLowerPoint[2] = (this.aerialViewBoundsLowerPoint[2]
							+ this.aerialViewBoundsUpperPoint[2])
							/ 2 - MIN_HEIGHT / 2;
					this.aerialViewBoundsUpperPoint[2] = this.aerialViewBoundsLowerPoint[2]
							+ MIN_HEIGHT;
				}
			}

		}

		/**
		 * Adds the point at the given coordinates to aerial view bounds.
		 */
		private void updateAerialViewBounds(float x, float y, float minZ,
				float maxZ)
		{
			if (this.aerialViewBoundsLowerPoint == null)
			{
				this.aerialViewBoundsLowerPoint = new float[] {
						x, y, minZ
				};
				this.aerialViewBoundsUpperPoint = new float[] {
						x, y, maxZ
				};
			} else
			{
				this.aerialViewBoundsLowerPoint[0] = Math.min(
						this.aerialViewBoundsLowerPoint[0], x);
				this.aerialViewBoundsUpperPoint[0] = Math.max(
						this.aerialViewBoundsUpperPoint[0], x);
				this.aerialViewBoundsLowerPoint[1] = Math.min(
						this.aerialViewBoundsLowerPoint[1], y);
				this.aerialViewBoundsUpperPoint[1] = Math.max(
						this.aerialViewBoundsUpperPoint[1], y);
				this.aerialViewBoundsLowerPoint[2] = Math.min(
						this.aerialViewBoundsLowerPoint[2], minZ);
				this.aerialViewBoundsUpperPoint[2] = Math.max(
						this.aerialViewBoundsUpperPoint[2], maxZ);
			}
		}

		/**
		 * Returns <code>true</code> if the given <code>item</code> is at a
		 * visible level.
		 */
		private boolean isItemAtVisibleLevel(Elevatable item)
		{
			return item.getLevel() == null || item.getLevel().isVisible();
		}

		/**
		 * Updates the minimum and maximum distances of the camera to the center
		 * of the aerial view.
		 */
		private void updateCameraIntervalToAerialViewCenter()
		{
			// float homeBoundsWidth = this.aerialViewBoundsUpperPoint[0]
			// - this.aerialViewBoundsLowerPoint[0];
			// float homeBoundsDepth = this.aerialViewBoundsUpperPoint[1]
			// - this.aerialViewBoundsLowerPoint[1];
			// float homeBoundsHeight = this.aerialViewBoundsUpperPoint[2]
			// - this.aerialViewBoundsLowerPoint[2];
			// float halfDiagonal = (float) Math.sqrt(homeBoundsWidth
			// * homeBoundsWidth + homeBoundsDepth * homeBoundsDepth
			// + homeBoundsHeight * homeBoundsHeight) / 2;
			// this.minDistanceToAerialViewCenter = halfDiagonal * 1.05f;
			// this.maxDistanceToAerialViewCenter = Math.max(
			// 5 * this.minDistanceToAerialViewCenter, 2500);
		}

		@Override
		public void moveCamera(float delta)
		{
			// Use a 5 times bigger delta for top camera move
			delta *= 5;
			// float newDistanceToCenter = getCameraToAerialViewCenterDistance()
			// - delta;
			this.topCamera.setX(this.topCamera.getX()
					+ (float) (Math.sin(this.topCamera.getYaw()) * delta));
			this.topCamera.setY(this.topCamera.getY()
					- (float) (Math.cos(this.topCamera.getYaw()) * delta));
			this.topCamera.setZ(this.topCamera.getZ()
					+ (float) Math.sin(this.topCamera.getPitch()) * delta);

		}

		private void placeCameraAt(float distanceToCenter,
				boolean firstPieceOfFurnitureAddedToEmptyHome)
		{
			// Check camera is always outside the sphere centered in home center
			// and with a radius equal to minimum distance
			distanceToCenter = Math.max(distanceToCenter,
					this.minDistanceToAerialViewCenter);
			// Check camera isn't too far
			distanceToCenter = Math.min(distanceToCenter,
					this.maxDistanceToAerialViewCenter);
			if (firstPieceOfFurnitureAddedToEmptyHome)
			{
				// Get closer to the first piece of furniture added to an empty
				// home when that is small
				distanceToCenter = Math.min(distanceToCenter,
						3 * this.minDistanceToAerialViewCenter);
			}
			double distanceToCenterAtGroundLevel = distanceToCenter
					* Math.cos(this.topCamera.getPitch());
			this.topCamera
					.setX(this.topCamera.getX()
							+ (float) (Math.sin(this.topCamera.getYaw())
									* distanceToCenterAtGroundLevel));
			this.topCamera
					.setY(this.topCamera.getY()
							- (float) (Math.cos(this.topCamera.getYaw())
									* distanceToCenterAtGroundLevel));
			this.topCamera.setZ(this.topCamera.getZ()
					+ (float) Math.sin(this.topCamera.getPitch())
							* distanceToCenter);
		}

		@Override
		public void rotateCameraYaw(float delta)
		{
			float newYaw = this.topCamera.getYaw() + delta;
			double distanceToCenterAtGroundLevel = getCameraToAerialViewCenterDistance()
					* Math.cos(this.topCamera.getPitch());
			// Change camera yaw and location so user turns around home
			this.topCamera.setYaw(newYaw);
			this.topCamera
					.setX(this.topCamera.getX()
							+ (float) (Math.sin(newYaw)
									* distanceToCenterAtGroundLevel));
			this.topCamera
					.setY(this.topCamera.getY()
							- (float) (Math.cos(newYaw)
									* distanceToCenterAtGroundLevel));
		}

		@Override
		public void rotateCameraPitch(float delta)
		{
			float newPitch = this.topCamera.getPitch() + delta;
			// Check new pitch is between 0 and PI
			newPitch = Math.max(newPitch, (float) 0);
			newPitch = Math.min(newPitch, (float) Math.PI);
			// Compute new z to keep the same distance to view center
			double distanceToCenter = getCameraToAerialViewCenterDistance();
			double distanceToCenterAtGroundLevel = distanceToCenter
					* Math.cos(newPitch);
			// Change camera pitch
			this.topCamera.setPitch(newPitch);
			this.topCamera
					.setX(this.topCamera.getX()
							+ (float) (Math.sin(this.topCamera.getYaw())
									* distanceToCenterAtGroundLevel));
			this.topCamera
					.setY(this.topCamera.getY()
							- (float) (Math.cos(this.topCamera.getYaw())
									* distanceToCenterAtGroundLevel));
			this.topCamera.setZ(this.topCamera.getZ()
					+ (float) (distanceToCenter * Math.sin(newPitch)));
		}

		@Override
		public void goToCamera(Camera camera)
		{
			this.topCamera.setCamera(camera);
			this.topCamera.setTime(camera.getTime());
			this.topCamera.setLens(camera.getLens());
			updateCameraFromHomeBounds(false);
		}

		@Override
		public void exit()
		{
			this.topCamera = null;

		}
	}

	/**
	 * Preferences property listener bound to top camera state with a weak
	 * reference to avoid strong link between user preferences and top camera
	 * state.
	 */
	private static class UserPreferencesChangeListener implements
			PropertyChangeListener {

		private WeakReference<SimulationTopCameraState> topCameraState;

		public UserPreferencesChangeListener(
				SimulationTopCameraState topCameraState)
		{
			this.topCameraState = new WeakReference<SimulationTopCameraState>(
					topCameraState);
		}

		public void propertyChange(PropertyChangeEvent ev)
		{
			// If top camera state was garbage collected, remove this listener
			// from preferences
			SimulationTopCameraState topCameraState = this.topCameraState.get();
			UserPreferences preferences = (UserPreferences) ev.getSource();
			if (topCameraState == null)
			{
				preferences.removePropertyChangeListener(
						UserPreferences.Property.valueOf(ev.getPropertyName()),
						this);
			} else
			{
				topCameraState
						.setAerialViewCenteredOnSelectionEnabled(preferences
								.isAerialViewCenteredOnSelectionEnabled());
			}
		}
	}

	@Override
	protected void setCameraState(CameraControllerState state)
	{
		if (this.getObserverCameraState() == state)
		{
			super.setCameraState(state);
		} else
		{
			if (this.simulationTopCameraState != null)
			{
				super.setCameraState(simulationTopCameraState);
			}
		}
	}
}