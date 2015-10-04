/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.io.storage;

import com.eteks.sweethome3d.io.DefaultHomeInputStream;
import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author Rafael Pax
 */
public class DefaultMassisStorage implements MassisStorage {

    private static final String SH3D_BUILDING_FILENAME;
    private static final String METADATA_FILENAME;
    private static final String SIMULATION_LOG_FILENAME = "logs/simulation.log";
    private static final String COMPRESSION_MAP_FILENAME = "logs/compression_map.json";
    private FileSystem zipfs;
    private static final HashMap<String, DefaultMassisStorage> storages = new HashMap<>();

    static
    {
        ResourceBundle resource = ResourceBundle.getBundle(
                DefaultMassisStorage.class.getName());
        SH3D_BUILDING_FILENAME = resource.getString("sh3dBuildingFileName");
        METADATA_FILENAME = resource.getString("metadataFileName");

    }

    public static DefaultMassisStorage getStorage(File zipFile) throws IOException
    {
        if (!storages.containsKey(zipFile.getAbsolutePath()))
        {
            storages.put(zipFile.getAbsolutePath(), new DefaultMassisStorage(
                    zipFile));
        }
        return storages.get(zipFile.getAbsolutePath());
    }
    public static void closeAll() throws IOException{
        for (DefaultMassisStorage value : storages.values())
        {
            value.close();
        }
        storages.clear();
    }
    private DefaultMassisStorage(File zipFile) throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        final String uriStr = "jar:" + zipFile.toURI();
        URI uri = URI.create(uriStr);
        zipfs = FileSystems.newFileSystem(uri, env);
    }

    public static void main(String[] args) throws Throwable
    {
    }

    public void copy(File from, String filePath) throws IOException
    {
        Path pathInZipfile = zipfs.getPath(filePath);
        Files.copy(from.toPath(), pathInZipfile,
                StandardCopyOption.REPLACE_EXISTING);
    }

    public void delete(String filePath) throws IOException
    {
        Path pathInZipfile = zipfs.getPath(filePath);
        Files.deleteIfExists(pathInZipfile);
    }

    private InputStream newInputStream(String filePath) throws IOException
    {
        return Files.newInputStream(zipfs.getPath(filePath),
                StandardOpenOption.READ);
    }

    private OutputStream newOutputStream(String filePath) throws IOException
    {
        Path parent = zipfs.getPath(filePath).getParent();
        if (parent!=null)
        Files.createDirectories(parent);
        return Files.newOutputStream(zipfs.getPath(filePath),
                StandardOpenOption.CREATE);
    }

    @Override
    public Home loadHome() throws IOException, ClassNotFoundException
    {
        try (InputStream is = this.newInputStream(SH3D_BUILDING_FILENAME))
        {
            try (DefaultHomeInputStream in = new DefaultHomeInputStream(is))
            {
                return in.readHome();
            }
        }
    }

    @Override
    public BuildingMetadata loadMetadata() throws IOException
    {
        try (InputStream is = this.newInputStream(METADATA_FILENAME))
        {
            return new Gson().fromJson(new InputStreamReader(is),
                    BuildingMetadata.class);
        }
    }

    @Override
    public void close() throws IOException
    {
        zipfs.close();
    }

    @Override
    public void saveHome(Home home) throws IOException, RecorderException
    {
        /*
         * File creation for saving the home
         */
        File tempFile = OperatingSystem.createTemporaryFile(
                "_MASSIS_HOME_", ".sh3d");
        tempFile.deleteOnExit();
        /*
         * Dump home data
         */
        new HomeFileRecorder().writeHome(home, tempFile.getAbsolutePath());
        /*
         * Copy from the temporary file to this storage
         */
        this.copy(tempFile, SH3D_BUILDING_FILENAME);
    }

    @Override
    public void saveMetadata(BuildingMetadata metadata) throws IOException
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                this.newOutputStream(
                METADATA_FILENAME)))
        {
            gson.toJson(metadata, writer);
        }
    }

    @Override
    public OutputStream getLogOutputStream() throws IOException
    {
        return this.newOutputStream(SIMULATION_LOG_FILENAME);
    }

    @Override
    public InputStream getLogInputStream() throws IOException
    {
        return this.newInputStream(SIMULATION_LOG_FILENAME);
    }

    @Override
    public String[][] loadCompressionMap() throws IOException
    {
        try (InputStreamReader r = new InputStreamReader(this.newInputStream(
                COMPRESSION_MAP_FILENAME)))
        {
            return new Gson().fromJson(r, String[][].class);
        }
    }

    @Override
    public void saveCompressionMap(String[][] compressionMap) throws IOException
    {
        try (OutputStreamWriter w = new OutputStreamWriter(this.newOutputStream(
                COMPRESSION_MAP_FILENAME)))
        {
            new Gson().toJson(compressionMap, w);
        }
    }

    @Override
    public void deleteLogFiles() throws IOException
    {
        this.delete(SIMULATION_LOG_FILENAME);
        this.delete(COMPRESSION_MAP_FILENAME);
    }
}