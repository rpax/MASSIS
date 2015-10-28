/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.additionaldata;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.UserPreferences;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Rafael Pax
 */
public class AdditionalDataHomeRecorder extends HomeFileRecorder {

    private final List<? extends AdditionalDataReader> additionalDataReaders;
    private final List<? extends AdditionalDataWriter> additionalDataWriters;
    private final HomeApplication application;

    public AdditionalDataHomeRecorder(int compressionLevel,
            boolean includeOnlyTemporaryContent,
            UserPreferences preferences,
            boolean preferPreferencesContent,
            List<? extends AdditionalDataReader> additionalDataReaders,
            List<? extends AdditionalDataWriter> additionalDataWriters)
    {
        this(compressionLevel, includeOnlyTemporaryContent, preferences,
                preferPreferencesContent, additionalDataReaders,
                additionalDataWriters, null);
    }

    public AdditionalDataHomeRecorder(int compressionLevel,
            boolean includeOnlyTemporaryContent,
            UserPreferences preferences,
            boolean preferPreferencesContent,
            List<? extends AdditionalDataReader> additionalDataReaders,
            List<? extends AdditionalDataWriter> additionalDataWriters,
            HomeApplication application)
    {
        super(compressionLevel, includeOnlyTemporaryContent, preferences,
                preferPreferencesContent);
        this.additionalDataReaders = additionalDataReaders;
        this.additionalDataWriters = additionalDataWriters;
        this.application = application;
    }

    @Override
    public void writeHome(Home home, String name) throws RecorderException
    {
        super.writeHome(home, name);
        File homeFile = new File(name);
        for (AdditionalDataWriter additionalDataWriter : this.additionalDataWriters)
        {
            try
            {
                additionalDataWriter.writeAdditionalData(home, homeFile);
            } catch (IOException ex)
            {
                throw new RecorderException("Failed to write additional data.",
                        ex);
            }
        }

    }

    @Override
    public Home readHome(String name) throws RecorderException
    {

        //restore metadata from home
        Home home = super.readHome(name);
        final File homeFile = new File(name);
        try
        {
            for (AdditionalDataReader additionalDataReader : additionalDataReaders)
            {
                additionalDataReader.readAdditionalData(this.application, home,
                        homeFile);
            }
            return home;
        } catch (IOException ex)
        {
            throw new RecorderException("Error when loading additional data", ex);
        }
    }
}
