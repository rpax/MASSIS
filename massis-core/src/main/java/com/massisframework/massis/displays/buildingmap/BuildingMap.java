/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.displays.buildingmap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;

import com.massisframework.massis.displays.MASSISIcon;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.displays.floormap.layers.FloorMapLayer;
import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.SimulationObject;

/**
 * Class responsible for displaying the different views of the building, which
 * is composed of layers.
 *
 * @author rpax
 */
@SuppressWarnings("serial")
public class BuildingMap extends javax.swing.JFrame implements
        SimulationDisplay {

    /**
     * Hashmap linking the floors with the tabs.
     */
    private final HashMap<Floor, Integer> floorTabsMap = new HashMap<>();

    /**
     * Creates new form BuildingMap
     */
    public BuildingMap()
    {
        initComponents();
    }

    /**
     * Inicializacion con floors
     */
    public BuildingMap(final Building building, final FloorMapLayer... layers)
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                /*
                 * BuildingMap initialization
                 */
                BuildingMap.this.initiate(building, Arrays.asList(layers));
            }
        });

    }

    @Override
    public void animate(SimulationObject obj)
    {
        refresh();

    }

    /**
     * Refreshes the active {@link PanAndZoomJPanel}
     */
    public void refresh()
    {
        tabbedPane.getSelectedComponent().repaint();
    }

    @Override
    public boolean isDisplayEnabled()
    {
        return this.isVisible();
    }

    /**
     * Configures this BuildingMap according to a building and a set of layers
     *
     * @param building the building
     * @param layers a list of {@link FloorMapLayer} that will be shown in the
     * GUI
     */
    private void initiate(final Building building,
            final List<FloorMapLayer> layers)
    {
        /*
         * Basic components initialization
         */
        initComponents();
        /*
         *Configuration of every layer in this map
         */
        for (final FloorMapLayer layer : layers)
        {
            /*
             * Checkbox for enabling/disabling the layer
             */
            JCheckBoxMenuItem layerCheckbox = new JCheckBoxMenuItem(
                    layer.getName());
            if (layer.isEnabled())
            {
                layerCheckbox.setSelected(true);

            }
            layerMenu.add(layerCheckbox);
            layerCheckbox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (layer.isEnabled())
                    {
                        layer.setEnabled(false);
                    } else
                    {
                        layer.setEnabled(true);
                    }
                }
            });
        }
        /*
         * Iterates over every floor, assigning a JPanel to it.
         */
        for (Floor floor : building.getFloors())
        {
            /*
             * Creation of the panel
             */
            final PanAndZoomJPanel panAndZoomJPanel = new PanAndZoomJPanel(
                    floor, building, layers);
            /*
             * TODO
             * Does NOT work with a GLG2DCanvas (HW acceleration library).
             * It would be great if  in a future this could be done.
             * Seems to be something related to the machine's configuration, because
             * in theory it would ba as easy as 
             * new GLG2DCanvas(panAndZoomJPanel);
             */
            Component panel = panAndZoomJPanel;
            this.tabbedPane.addTab(floor.getName(), panel);
            this.floorTabsMap.put(floor, this.tabbedPane.getTabCount() - 1);
            panAndZoomJPanel.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e)
                {
                    panAndZoomJPanel.mouseWheelMoved(e);
                }
            });
            /*
             *Delegation of listeners to the panel. 
             */
            this.tabbedPane.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e)
                {
                }

                @Override
                public void keyReleased(KeyEvent e)
                {
                    panAndZoomJPanel.keyReleased(e);
                }

                @Override
                public void keyPressed(KeyEvent e)
                {
                    panAndZoomJPanel.keyPressed(e);
                }
            });
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		tabbedPane = new javax.swing.JTabbedPane();
		welcomePanel = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		menuBar = new javax.swing.JMenuBar();
		layerMenu = new javax.swing.JMenu();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setIcon(new MASSISIcon().setDimensionAndReturn(new Dimension(
				300, 300)));
		jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		jLabel2.setText("<html>\n<head></head><body>\n<p><span style=\"font-size: x-large;\"><strong><span style=\"font-family: verdana, geneva;\"><span style=\"color: #3366ff;\"> Welcome to Simulation 2D display</span></span></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><span style=\"color: #3366ff;\"><br /></span></strong></span></p>\n<p style=\"color: #ff0000;\"><span style=\"font-family: verdana, geneva; font-size: medium;\"><span style=\"font-size: large;\"><strong><span style=\"color: #000000;\">Layers</span></strong></span><span style=\"text-decoration: underline; color: #666699;\"><strong><br /></strong></span></span></p>\n<p style=\"color: #ff0000;\"><span style=\"color: #000000; font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The simulation 2D display system is organized in <strong>Layers</strong>.&nbsp;Each layer shows different elements from the simulation.Layers can be be enabled or disabled from the top left Layers menu.</span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><br /></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Zooming</strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><span style=\"font-size: small;\">You can zoom moving the mouse wheel.</span></span></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Panning</strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The display allows moving the map, in a drag'drop fashion. Just click with the left mouse button and move it.</span></p>\n<p>&nbsp;</p>\n</body>\n</html>");
		jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(
				welcomePanel);
		welcomePanel.setLayout(welcomePanelLayout);
		welcomePanelLayout
				.setHorizontalGroup(welcomePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								welcomePanelLayout
										.createSequentialGroup()
										.addGap(35, 35, 35)
										.addComponent(
												jLabel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												399,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jLabel2,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												388, Short.MAX_VALUE)
										.addContainerGap()));
		welcomePanelLayout
				.setVerticalGroup(welcomePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								welcomePanelLayout
										.createSequentialGroup()
										.addGap(40, 40, 40)
										.addGroup(
												welcomePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jLabel1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																406,
																Short.MAX_VALUE)
														.addComponent(jLabel2))
										.addContainerGap()));

		tabbedPane.addTab("Welcome", welcomePanel);

		layerMenu.setText("Layers");
		menuBar.add(layerMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				tabbedPane));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap()
						.addComponent(tabbedPane)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">
		/*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase
         * /tutorial/uiswing/lookandfeel/plaf.html
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(BuildingMap.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(BuildingMap.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(BuildingMap.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(BuildingMap.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                new BuildingMap().setVisible(true);
            }
        });
    }
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JMenu layerMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JPanel welcomePanel;
	// End of variables declaration//GEN-END:variables
}
