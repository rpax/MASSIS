/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.additionaldata;

import com.eteks.sweethome3d.model.Home;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Rafael Pax
 */
public interface AdditionalDataWriter {

    public void writeAdditionalData(Home home, File homeFile) throws IOException;
    
}
