/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.building;

import com.eteks.sweethome3d.model.Home;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpax.massis.util.io.storage.BuildingMetadata;
import rpax.massis.util.io.storage.MassisStorage;

/**
 *
 * @author Rafael Pax
 */
public class BuildingData {

    private Home home;
    private MASSISHomeMetadataManager metadataManager;
    private MassisStorage storage;
    protected static final String SH3D_BUILDING_FILENAME;
    protected static final String METADATA_FILENAME;
    private static final Map<Home, BuildingData> homeData = new ConcurrentHashMap<>();

    static
    {
        ResourceBundle resource = ResourceBundle.getBundle(
                BuildingData.class.getName());
        SH3D_BUILDING_FILENAME = resource.getString("sh3dBuildingFileName");
        METADATA_FILENAME = resource.getString("metadataFileName");

    }

    public BuildingData(MassisStorage storage)
    {
        try
        {
            this.storage = storage;
            loadData();
            homeData.put(home, this);
        } catch (IOException ex)
        {
            Logger.getLogger(BuildingData.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new RuntimeException(ex);
        }

    }

    public BuildingData(Home home, BuildingMetadata metadata)
    {
        this.home = home;
        this.metadataManager = new MASSISHomeMetadataManager(home,metadata);
    }

    private BuildingData(Home home)
    {
        this.home = home;
        this.metadataManager = new MASSISHomeMetadataManager(home);
    }

    public static BuildingData getBuildingData(Home home)
    {
        if (!homeData.containsKey(home))
        {
            homeData.put(home, new BuildingData(home));
        }
        return homeData.get(home);
    }

    private void loadData() throws IOException
    {
        try
        {
            this.home = storage.loadHome();
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(BuildingData.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        this.metadataManager = new MASSISHomeMetadataManager(this.home,
                storage.loadMetadata());
        //

    }

    public Home getHome()
    {
        return home;
    }

    public MASSISHomeMetadataManager getMetadataManager()
    {
        return metadataManager;
    }

    public void setHome(Home home)
    {
        this.home = home;
    }

    public void setMetadataManager(MASSISHomeMetadataManager metadataManager)
    {
        this.metadataManager = metadataManager;
    }
}
