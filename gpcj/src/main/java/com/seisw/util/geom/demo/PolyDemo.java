/*
 * The SEI Software Open Source License, Version 1.0
 *
 * Copyright (c) 2004, Solution Engineering, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Solution Engineering, Inc. (http://www.seisw.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 3. The name "Solution Engineering" must not be used to endorse or
 *    promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    admin@seisw.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL SOLUTION ENGINEERING, INC. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package com.seisw.util.geom.demo;

import javax.swing.JFrame ;
import javax.swing.JDialog ;
import javax.swing.JPanel ;
import javax.swing.JLabel ;
import javax.swing.ImageIcon ;
import java.awt.BorderLayout ;
import java.awt.FlowLayout ;
import java.awt.Rectangle ;
import java.awt.Dimension ;
import java.awt.Toolkit ;
import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;
import java.awt.event.ComponentAdapter ;
import java.awt.event.ComponentEvent ;

/**
 * <code>PolyDemo</code> is the main class of the PolyDemo application.  It owns the main frame
 * of the application and is responsible for constructing the rest of the objects
 * and placing them into the main frame.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class PolyDemo
{   
   // -----------------
   // --- Constants ---
   // -----------------
   private static final String PRODUCT_NAME = "PolyDemo" ;
   
   private static final String TITLE = PRODUCT_NAME ;
   
   private static final String ICON_STR_PD = "icons/pd_icon.gif" ;
   
   private static final String SELECT_POINTS = "Click on white panel to enter points.  Select on Done Entering or Poly X when done." ;
   private static final String SELECT_OPERATION = "Select a set operation." ;
      
   // ------------------------
   // --- Member Variables ---
   // ------------------------
   private JFrame       m_MainFrame ;
   private MenuBar      m_MenuBar ;
   private PolyCanvas   m_Canvas ;
   private ControlPanel m_ControlPanel ;
   
   // --------------------
   // --- Constructors ---
   // --------------------
   /** Creates a new instance of PolyDemo */
   public PolyDemo()
   {
      m_MainFrame = createFrame();
   }
   
   // ----------------------
   // --- Public Methods ---   
   // ----------------------

   /**
    *
    */
   public void show()
   {
      m_MainFrame.pack();
      
      Dimension screenDim = Toolkit.getDefaultToolkit ().getScreenSize(); 
      Rectangle winDim = m_MainFrame.getBounds();
      m_MainFrame.setLocation((screenDim.width - winDim.width) / 2, 
                              (screenDim.height - winDim.height) / 2);  
      
      m_MainFrame.setVisible( true );
   }
   
   // -------------------------
   // --- Private Methods ---   
   // -------------------------

   /**
    *
    */
   private JFrame createFrame()
   {
      ImageIcon icon = new ImageIcon(/* this.getClass().getResource(ICON_STR_PD)*/);

      JFrame retFrame = new JFrame();
      retFrame.setTitle( TITLE );
      retFrame.getContentPane().setLayout( new BorderLayout() );
      retFrame.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
      retFrame.addComponentListener( new FrameComponentListener() );
      retFrame.setIconImage( icon.getImage() );
      
      m_Canvas       = new PolyCanvas();
      m_ControlPanel = new ControlPanel(m_Canvas);
      
      m_MenuBar = new MenuBar( retFrame );
      retFrame.setJMenuBar( m_MenuBar );
      
      retFrame.addWindowListener(
         new WindowAdapter()
         {
      	   public void windowClosing(WindowEvent e)
      	   {
               m_MenuBar.exit();
      	   }
         }
      );

      StatusBar bar = new StatusBar();
      m_Canvas.addStateChangedListener( bar );
      
      retFrame.getContentPane().add( m_Canvas,       BorderLayout.CENTER );
      retFrame.getContentPane().add( m_ControlPanel, BorderLayout.EAST );
      retFrame.getContentPane().add( bar,            BorderLayout.SOUTH );
      
      return retFrame ;
   }
   
   // ---------------------
   // --- Inner Classes ---
   // ---------------------
   /**
    * Limit resizing of main frame
    */
   private class FrameComponentListener extends ComponentAdapter
   {
      private static final int MIN_WIDTH = 500;
      private static final int MIN_HEIGHT = 550;
      
      public FrameComponentListener()
      {
      }
      
      public void componentResized(ComponentEvent e)
      {
         // handle resizing of frame
         int width = e.getComponent().getWidth();
         int height = e.getComponent().getHeight();
         
         if( width <= MIN_WIDTH )
         {
            width = MIN_WIDTH ;
         }
         if( height <= MIN_HEIGHT )
         {
            height = MIN_HEIGHT ;
         }
         e.getComponent().setSize(width, height);
      }
   }

   /**
    * Show information about what the user should do next.
    */
   private class StatusBar extends JPanel implements StateChangedListener
   {
      private JLabel m_Message = new JLabel(SELECT_POINTS);
      
      public StatusBar()
      {
         super( new FlowLayout(FlowLayout.LEFT) );
         add( m_Message );
      }
      
      public void stateChanged( CanvasState newState )
      {
         String msg = " ";
         if( (newState == CanvasState.ENTERING_POLY_1) || (newState == CanvasState.ENTERING_POLY_2) )
         {
            msg =  SELECT_POINTS;
         }
         else
         {
            msg = SELECT_OPERATION ;
         }
         m_Message.setText( msg );
      }
   }
   
   // --------------------
   // --- Main Program ---
   // --------------------

   /**
    * The main to the PolyDemo application
    */
   public static void main( String[] argv )
   {
      try
      {
//         com.l2fprod.gui.plaf.skin.Skin skin = com.l2fprod.gui.plaf.skin.SkinLookAndFeel.loadDefaultThemePack();
//         com.l2fprod.gui.plaf.skin.SkinLookAndFeel.setSkin(skin);
//         javax.swing.UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
      }
      catch( Exception e )
      {
         System.out.println("Error occured setting look and feel, msg="+e.getMessage());
         System.exit(0);
      }

      PolyDemo pd = new PolyDemo();
      pd.show();
   }
}
