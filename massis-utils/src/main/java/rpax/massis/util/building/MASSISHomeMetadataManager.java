package rpax.massis.util.building;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import rpax.massis.util.SimObjectProperty;
import rpax.massis.util.io.storage.BuildingMetadata;

public class MASSISHomeMetadataManager {

    private final HashMap<Selectable, HashMap<String, String>> idMap;
    private Home home;
    private int current_max_id;
    protected static final String ID_KEY = SimObjectProperty.ID.toString();

    public MASSISHomeMetadataManager(Home home, File metadataFile) throws FileNotFoundException
    {

        this(home, new Gson().fromJson(new FileReader(metadataFile),
                BuildingMetadata.class));
    }

    public MASSISHomeMetadataManager(Home home, InputStream metadataStream)
    {

        this(home, new Gson().fromJson(new InputStreamReader(metadataStream),
                BuildingMetadata.class));
    }

    public MASSISHomeMetadataManager(Home home)
    {

        this.home = home;
        this.idMap = new HashMap<>();
    }

    public MASSISHomeMetadataManager(Home home, BuildingMetadata metadata)
    {

        this(home);
        Wall[] walls = home.getWalls().toArray(new Wall[0]);
        Room[] rooms = home.getRooms().toArray(new Room[0]);
        HomePieceOfFurniture[] furniture = home.getFurniture().toArray(
                new HomePieceOfFurniture[0]);
        for (int i = 0; i < walls.length; i++)
        {
            this.addMetaData(walls[i], metadata.getWalls().get(i));
        }
        for (int i = 0; i < rooms.length; i++)
        {
            this.addMetaData(rooms[i], metadata.getRooms().get(i));
        }
        for (int i = 0; i < furniture.length; i++)
        {
            this.addMetaData(furniture[i], metadata.getFurniture().get(i));
        }
        //retrieve current_max_id
        for (int i = 0; i < walls.length; i++)
        {
            this.current_max_id = Math.max(Integer.parseInt(this.getMetadata(
                    walls[i]).get(ID_KEY)), current_max_id);
        }
        for (int i = 0; i < rooms.length; i++)
        {
            this.current_max_id = Math.max(Integer.parseInt(this.getMetadata(
                    rooms[i]).get(ID_KEY)), current_max_id);
        }
        for (int i = 0; i < furniture.length; i++)
        {
            this.current_max_id = Math.max(Integer.parseInt(this.getMetadata(
                    furniture[i]).get(ID_KEY)), current_max_id);
        }
    }

    /**
     * Devuelve el metadataManager correspondiente a un Home. Si no existe, lo
     * crea.
     *
     * @param home
     * @return
     */
    public static MASSISHomeMetadataManager getHomeMetaData(Home home)
    {

        return BuildingData.getBuildingData(home).getMetadataManager();
    }

    /**
     *
     * @return una id unica, incremental
     */
    private synchronized Integer getID()
    {
        return ++this.current_max_id;
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException
    {
        //nothing. This class should not be serialized
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        //nothing. This class should not be serialized
    }

    public int size()
    {
        return this.idMap.size();
    }

    public final Map<String, String> getMetadata(Selectable element)
    {
        Objects.requireNonNull(element);
        return getOrCreateMetadata(element);
    }

    public void removeMetadata(Selectable element, String key)
    {
        Objects.requireNonNull(element);
        HashMap<String, String> metadata = getOrCreateMetadata(element);
        metadata.remove(key);
        syncWithDescription(element, metadata);
    }

    public void setMetaData(Selectable element, String key, String value)
    {
        Objects.requireNonNull(element);
        HashMap<String, String> metadata = getOrCreateMetadata(element);
        metadata.put(key, value);
        syncWithDescription(element, metadata);
    }

    public void setMetaData(Selectable element, Map<String, String> new_metadata)
    {
        Objects.requireNonNull(element);
        HashMap<String, String> metadata = new HashMap<>(new_metadata);
        this.idMap.put(element, metadata);
        syncWithDescription(element, metadata);
    }

    public final void addMetaData(Selectable element,
            Map<String, String> new_metadata)
    {

        Objects.requireNonNull(element);
        HashMap<String, String> metadata = getOrCreateMetadata(element);
        metadata.putAll(new_metadata);
        syncWithDescription(element, metadata);
    }

    private HashMap<String, String> getOrCreateMetadata(Selectable element)
    {
        if (!this.idMap.containsKey(element))
        {
            HashMap<String, String> metadata = new HashMap<>();
            if (element instanceof HomePieceOfFurniture)
            {
                HomePieceOfFurniture hpof = (HomePieceOfFurniture) element;
                try
                {
                    if (hpof.getDescription() != null && !"".equals(
                            hpof.getDescription()))
                    {
                        metadata = new Gson().fromJson(hpof.getDescription(),
                                HashMap.class);
                    }
                } catch (Exception e)
                {
//                    metadata = new HashMap<>();
                }
            }
            metadata.put(ID_KEY, String.valueOf(getID()));
            this.idMap.put(element, metadata);
            syncWithDescription(element, metadata);
        }
        return this.idMap.get(element);
    }

    public void writeMetadata(Appendable writer) throws IOException
    {

        new GsonBuilder().setPrettyPrinting().create().toJson(
                getBuildingMetadata(), writer);

    }

    public BuildingMetadata getBuildingMetadata() throws IOException
    {
        BuildingMetadata metadata = new BuildingMetadata();
        for (Wall wall : this.home.getWalls())
        {
            metadata.getWalls().add(getMetadata(wall));
        }
        for (Room room : this.home.getRooms())
        {
            metadata.getRooms().add(getMetadata(room));
        }
        for (HomePieceOfFurniture hpof : this.home.getFurniture())
        {
            metadata.getFurniture().add(getMetadata(hpof));
        }
        return metadata;

    }

    private void syncWithDescription(Selectable element,
            HashMap<String, String> metadata)
    {
        if (element instanceof HomePieceOfFurniture)
        {
            final HomePieceOfFurniture hpof = (HomePieceOfFurniture) element;
            hpof.setDescription(new Gson().toJson(metadata));
        }
    }
}
