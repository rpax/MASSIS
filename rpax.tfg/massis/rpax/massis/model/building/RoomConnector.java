package rpax.massis.model.building;

import java.util.List;

import straightedge.geom.vision.Occluder;
/**
 * Connects one or more rooms
 * @author rpax
 *
 */
public interface RoomConnector extends Occluder{

	public List<SimRoom> getConnectedRooms();
}
