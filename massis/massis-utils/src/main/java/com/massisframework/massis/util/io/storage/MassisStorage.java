/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.util.io.storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.RecorderException;

/**
 *
 * @author Rafael Pax
 */
public interface MassisStorage extends Closeable{

    /**
     * Loads the metadata of the building contained in this storage.
     *
     * @return the metadata of the building contained in this storage.
     * @throws IOException if an I/O error occurs
     * @see #loadHome()
     * @see Home
     */
    public BuildingMetadata loadMetadata() throws IOException;

    /**
     * Saves the metadata corresponding to the building contained in this
     * storage.
     *
     * @param metadata the metadata to be saved
     * @throws IOException if an I/O error occurs
     */
    public void saveMetadata(BuildingMetadata metadata) throws IOException;

    /**
     * Loads the home contained in this storage
     *
     * @return the home inside this storage
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException
     * {@link com.eteks.sweethome3d.io.HomeFileRecorder#readHome(java.lang.String)}
     */
    public Home loadHome() throws IOException, ClassNotFoundException;

    /**
     * Stores the home in this storage
     *
     * @param home the home to be stored.
     * @throws IOException if an I/O error occurs
     * @throws RecorderException
     * {@link com.eteks.sweethome3d.io.HomeFileRecorder#writeHome(com.eteks.sweethome3d.model.Home, java.lang.String)}
     */
    public void saveHome(Home home) throws IOException, RecorderException;

   

    /**
     * Creates an {@link InputStream} pointing to the simulation log file
     *
     * @return the inputstream
     * @throws IOException if an I/O error occurs
     */
    public InputStream getLogInputStream() throws IOException;

    /**
     * Loads the compression map used for compressing the logs entries
     *
     * @return the key-values of the compression map
     * @throws IOException if an I/O error occurs
     */
    public String[][] loadCompressionMap() throws IOException;

    /**
     * Saves the compression map used for compressing the logs entries
     *
     * @param compressionMap the compression k-v pairs
     */
    public void saveCompressionMap(String[][] compressionMap) throws IOException;

    /**
     * Deletes the files associated with the logging system. Should be called
     * every time the building is saved in the editor.
     */
    public void deleteLogFiles() throws IOException;
    /**
     * Saves the simulation log file.
     */
    public void saveSimulationLogFile(File logFile) throws IOException;
}
