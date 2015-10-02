/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.io.storage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Contains the metadata of a building. The order of the elements is the same as
 * the order of the elements of a SH3D home. The metadata corresponding to the
 * furniture element at index {@code i} can be retrieved calling
 * {@link #getFurniture()}.get(i)
 *
 * @see com.eteks.sweethome3d.model.Home
 * @author Rafael Pax
 */
public class BuildingMetadata {

    private ArrayList<Map<String, String>> walls;
    private ArrayList<Map<String, String>> rooms;
    private ArrayList<Map<String, String>> furniture;

    public BuildingMetadata()
    {
        this.walls = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.furniture = new ArrayList<>();
    }

    public ArrayList<Map<String, String>> getWalls()
    {
        return walls;
    }

    public void setWalls(
            ArrayList<Map<String, String>> walls)
    {
        this.walls = walls;
    }

    public ArrayList<Map<String, String>> getRooms()
    {
        return rooms;
    }

    public void setRooms(
            ArrayList<Map<String, String>> rooms)
    {
        this.rooms = rooms;
    }

    public ArrayList<Map<String, String>> getFurniture()
    {
        return furniture;
    }

    public void setFurniture(
            ArrayList<Map<String, String>> furniture)
    {
        this.furniture = furniture;
    }
}
