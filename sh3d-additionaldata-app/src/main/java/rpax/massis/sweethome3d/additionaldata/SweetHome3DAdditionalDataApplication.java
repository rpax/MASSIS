/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.additionaldata;

import com.eteks.sweethome3d.SweetHome3D;
import com.eteks.sweethome3d.model.HomeRecorder;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Rafael Pax
 */
public class SweetHome3DAdditionalDataApplication extends SweetHome3D {

    private HomeRecorder additionalDataHomeRecorder;
    private HomeRecorder additionalDataCompressedHomeRecorder;
    private final List<? extends AdditionalDataReader> additionalDataReaders;
    private final List<? extends AdditionalDataWriter> additionalDataWriters;

    public static void runWithAdditionalReaderWriters(String[] args,
            List<? extends AdditionalDataReader> additionalDataReaders,
            List<? extends AdditionalDataWriter> additionalDataWriters)
    {

        new SweetHome3DAdditionalDataApplication(additionalDataReaders,
                additionalDataWriters).init(args);
    }

    public SweetHome3DAdditionalDataApplication(
            List<? extends AdditionalDataReader> additionalDataReaders,
            List<? extends AdditionalDataWriter> additionalDataWriters)
    {
        super();
        this.additionalDataReaders = additionalDataReaders;
        this.additionalDataWriters = additionalDataWriters;
    }

    public SweetHome3DAdditionalDataApplication()
    {
        super();
        this.additionalDataReaders = Arrays.asList(new AdditionalDataReaderAdapter());
        this.additionalDataWriters = Arrays.asList(new AdditionalDataWriterAdapter());
    }

    @Override
    public HomeRecorder getHomeRecorder()
    {
        if (this.additionalDataHomeRecorder == null)
        {
            this.additionalDataHomeRecorder =
                    new AdditionalDataHomeRecorder(0, false,
                    getUserPreferences(),
                    false,
                    additionalDataReaders,
                    additionalDataWriters,this);
        }
        return this.additionalDataHomeRecorder;
    }

    public HomeRecorder getHomeCompressedRecorder()
    {
        if (this.additionalDataCompressedHomeRecorder == null)
        {
            this.additionalDataCompressedHomeRecorder = new AdditionalDataHomeRecorder(
                    9,
                    false, getUserPreferences(), false,
                    additionalDataReaders,
                    additionalDataWriters,this);
        }
        return this.additionalDataCompressedHomeRecorder;
    }

    @Override
    public HomeRecorder getHomeRecorder(HomeRecorder.Type type)
    {
        switch (type)
        {
            case DEFAULT:
                return getHomeRecorder();
            case COMPRESSED:
                return getHomeCompressedRecorder();
            default:
                throw new UnsupportedOperationException();
        }
    }
}
