/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;


/**
 * Class responsible for displaying different views, in a layered way
 *
 * @author rpax
 */
@SuppressWarnings("serial")
public class DrawableTabbedFrame extends javax.swing.JFrame {

    /**
     * Hashmap linking the drawable zones with the tabs.
     */
    private final HashMap<DrawableZone, Integer> drawableZoneTabsMap = new HashMap<>();
    private String welcomeHTMLText="";
    /**
     * Creates new form BuildingMap
     */
    public DrawableTabbedFrame()
    {
        initComponents();
    }

    /**
     * Inicializacion con drawable zones
     */
    public DrawableTabbedFrame(final Collection<? extends DrawableZone> drawableZones,
            String welcomeHTMLText,
            final DrawableLayer...layers)
    {
        if (welcomeHTMLText!=null) {
            this.welcomeHTMLText=welcomeHTMLText;
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                /*
                 * BuildingMap initialization
                 */
                DrawableTabbedFrame.this.initiate(drawableZones, Arrays.asList(layers));
            }
        });

    }

    

    /**
     * Refreshes the active {@link PanAndZoomJPanel}
     */
    public void refresh()
    {
        tabbedPane.getSelectedComponent().repaint();
    }

    

    /**
     * Configures this BuildingMap according to a building and a set of layers
     *
     * @param building the building
     * @param layers a list of {@link FloorMapLayer} that will be shown in the
     * GUI
     */
    private void initiate(final Collection<? extends DrawableZone> drawableZones,
            final Collection<? extends DrawableLayer> layers)
    {
        /*
         * Basic components initialization
         */
        initComponents();
        /*
         *Configuration of every layer in this map
         */
        for (final DrawableLayer layer : layers)
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
         * Iterates over every drawable zone, assigning a JPanel to it.
         */
        for (DrawableZone drawableZone : drawableZones)
        {
            /*
             * Creation of the panel
             */
            final PanAndZoomJPanel panAndZoomJPanel = new PanAndZoomJPanel(
                    drawableZone, layers);
            /*
             * TODO
             * Does NOT work with a GLG2DCanvas (HW acceleration library).
             * It would be great if  in a future this could be done.
             * Seems to be something related to the machine's configuration, because
             * in theory it would ba as easy as 
             * new GLG2DCanvas(panAndZoomJPanel);
             */
            Component panel = panAndZoomJPanel;
            this.tabbedPane.addTab(drawableZone.getName(), panel);
            this.drawableZoneTabsMap.put(drawableZone, this.tabbedPane.getTabCount() - 1);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        tabbedPane = new javax.swing.JTabbedPane();
        welcomePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        layerMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        //jLabel2.setText("<html>\n<head></head><body>\n<p><span style=\"font-size: x-large;\"><strong><span style=\"font-family: verdana, geneva;\"><span style=\"color: #3366ff;\"> Welcome to Simulation 2D display</span></span></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><span style=\"color: #3366ff;\"><br /></span></strong></span></p>\n<p style=\"color: #ff0000;\"><span style=\"font-family: verdana, geneva; font-size: medium;\"><span style=\"font-size: large;\"><strong><span style=\"color: #000000;\">Layers</span></strong></span><span style=\"text-decoration: underline; color: #666699;\"><strong><br /></strong></span></span></p>\n<p style=\"color: #ff0000;\"><span style=\"color: #000000; font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The simulation 2D display system is organized in <strong>Layers</strong>.&nbsp;Each layer shows different elements from the simulation.Layers can be be enabled or disabled from the top left Layers menu.</span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><br /></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Zooming</strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><span style=\"font-size: small;\">You can zoom moving the mouse wheel.</span></span></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Panning</strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The display allows moving the map, in a drag'drop fashion. Just click with the left mouse button and move it.</span></p>\n<p>&nbsp;</p>\n</body>\n</html>");
        jLabel2.setText(this.welcomeHTMLText);
		jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(welcomePanel);
        welcomePanel.setLayout(welcomePanelLayout);
        welcomePanelLayout.setHorizontalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addGap(446, 446, 446)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        welcomePanelLayout.setVerticalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Welcome", welcomePanel);

        layerMenu.setText("Layers");
        menuBar.add(layerMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane))
        );

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
            java.util.logging.Logger.getLogger(DrawableTabbedFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(DrawableTabbedFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(DrawableTabbedFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(DrawableTabbedFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                new DrawableTabbedFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu layerMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel welcomePanel;
    // End of variables declaration//GEN-END:variables
}
