/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.testdata.gui;

import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author rpax
 */
public interface SampleHomeGUIControl {

    public void sampleHomesListValueChanged(SampleHomesGUI aThis, ListSelectionEvent evt);

    public void loadHomeButtonActionPerformed(SampleHomesGUI aThis, ActionEvent evt);
    
}
