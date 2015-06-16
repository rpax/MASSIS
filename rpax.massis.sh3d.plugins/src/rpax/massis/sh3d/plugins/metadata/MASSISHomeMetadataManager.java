package rpax.massis.sh3d.plugins.metadata;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Wall;

public class MASSISHomeMetadataManager implements Serializable {

	private static final long serialVersionUID = 9130109931882693890L;
	public static final String HOME_METADATA_MANAGER_KEY = "#-------HOME_METADATA_MANAGER-------#";
	public static final String ID_KEY = "id";
	public transient Home home;
	private transient HashMap<Serializable, HashMap<String, String>> idMap;
	
	
	private int current_max_id;
	private ArrayList<HashMap<String, String>> wallsMetadata;
	private ArrayList<HashMap<String, String>> roomsMetadata;
	private ArrayList<HashMap<String, String>> hpofMetadata;
	
	
	/**
	 * Constructora de este metadata manager a partir de un <code>Home</code>
	 * 
	 * @param home
	 */
	public MASSISHomeMetadataManager(Home home) {
		this.home = home;
		this.current_max_id = 0;

	}
	
	



	/**
	 * Devuelve el metadataManager correspondiente a un Home. Si no existe, lo
	 * crea.
	 * 
	 * @param home
	 * @return
	 */
	public static MASSISHomeMetadataManager getHomeMetaData(Home home) {
		MASSISHomeMetadataManager manager = (MASSISHomeMetadataManager) home.getVisualProperty(HOME_METADATA_MANAGER_KEY);
		
		if (manager == null)
		{
			System.err
					.println("Metadata Manager not found. Creating a new MetaDataManager");
			manager = new MASSISHomeMetadataManager(home);
			home.setVisualProperty(HOME_METADATA_MANAGER_KEY, manager);
		}
		else
		{
			manager.home = home;
		}
		manager.init();
		return manager;
	}

	/**
	 * 
	 * @return una id unica, incremental
	 */
	private synchronized Integer getID() {
		return ++this.current_max_id;
	}

	
	

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		for (HomePieceOfFurniture f : this.home.getFurniture())
		{
			MetaDataUtils.getMetaData(this.home, f);
		}
		try
		{
			this.wallsMetadata = new ArrayList<HashMap<String, String>>();
			this.roomsMetadata = new ArrayList<HashMap<String, String>>();
			this.hpofMetadata = new ArrayList<HashMap<String, String>>();
			for (Wall wall : this.home.getWalls())
			{
				this.wallsMetadata.add(new HashMap<String, String>(getMetadata(wall)));
			}
			for (Room r : this.home.getRooms())
			{
				this.roomsMetadata.add(new HashMap<String, String>(getMetadata(r)));
			}
			for (HomePieceOfFurniture f : this.home.getFurniture())
			{
				this.hpofMetadata.add(new HashMap<String, String>(getMetadata(f)));
			}
				
			out.defaultWriteObject();

		}
		finally
		{
			this.wallsMetadata = null;
			this.roomsMetadata = null;
			this.hpofMetadata = null;
		}

	}

	public int size() {
		return this.idMap.size();
	}

	public Map<String, String> getMetadata(Serializable obj) {
		init();

		HashMap<String, String> metadata = this.idMap.get(obj);
		if (metadata == null)
		{
			metadata = new HashMap<String, String>();
			metadata.put(ID_KEY, String.valueOf(getID()));
			this.idMap.put(obj, metadata);
		}
		if (!metadata.containsKey(ID_KEY)) {
			metadata.put(ID_KEY, String.valueOf(getID()));
		}
		return Collections.unmodifiableMap(metadata);

	}

	public void removeMetadata(Serializable obj, String key) {
		init();

		HashMap<String, String> metadata = this.idMap.get(obj);
		if (metadata == null)
		{
			metadata = new HashMap<String, String>();
			metadata.put(ID_KEY, String.valueOf(getID()));
			this.idMap.put(obj, metadata);
		}
		if (!metadata.containsKey(ID_KEY)) {
			metadata.put(ID_KEY, String.valueOf(getID()));
		}
		metadata.remove(key);

	}
	public void setMetaData(Serializable obj, String key, String value) {

		if (key==null || key.equals(ID_KEY)) return;
		HashMap<String, String> metadata = this.idMap.get(obj);
		if (metadata == null)
		{
			metadata = new HashMap<String, String>();
			metadata.put(ID_KEY, String.valueOf(getID()));
			this.idMap.put(obj, metadata);
		}
		if (!metadata.containsKey(ID_KEY)) {
			metadata.put(ID_KEY, String.valueOf(getID()));
		}
		metadata.put(key, value);
	}
	public void addMetaData(Serializable obj, Map<String,String> meta) {

		
		HashMap<String, String> metadata = this.idMap.get(obj);
		
		if (metadata == null)
		{
			metadata = new HashMap<String, String>();
			metadata.put(ID_KEY, String.valueOf(getID()));
			this.idMap.put(obj, metadata);
		}
		if (!metadata.containsKey(ID_KEY)) {
			metadata.put(ID_KEY, String.valueOf(getID()));
		}
		for (Entry<String, String> entry : meta.entrySet())
		{
			this.setMetaData(obj, entry.getKey(), entry.getValue());
		}
	}
	public void init() {
		if (this.idMap != null)
			return;
		this.idMap = new HashMap<Serializable, HashMap<String, String>>();
		int i;

		i = 0;
		if (this.wallsMetadata != null)
			for (Wall wall : this.home.getWalls())
			{
				this.idMap.put(wall, this.wallsMetadata.get(i));
				i++;
			}
		i = 0;
		if (this.roomsMetadata != null)
			for (Room r : this.home.getRooms())
			{
				this.idMap.put(r, this.roomsMetadata.get(i));
				i++;
			}
		i = 0;
		if (this.hpofMetadata != null)
			for (HomePieceOfFurniture f : this.home.getFurniture())
			{
				
				this.idMap.put(f, this.hpofMetadata.get(i));
				i++;
			}
		this.wallsMetadata = null;
		this.roomsMetadata = null;
		this.hpofMetadata = null;
	}

	

}

