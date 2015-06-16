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

import javax.swing.JPanel ;
import javax.swing.JLabel ;
import javax.swing.JButton ;
import javax.swing.JRadioButton ;
import javax.swing.JToggleButton ;
import javax.swing.JTextField ;
import javax.swing.ButtonGroup ;
import javax.swing.AbstractAction ;
import javax.swing.BorderFactory ;
import javax.swing.BoxLayout ;
import javax.swing.Box ;
import java.awt.GridBagConstraints ;
import java.awt.GridBagLayout ;
import java.awt.Insets ;
import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.text.DecimalFormat ;

/**
 * <code>ControlPanel</code> is the UI object that interacts/controls the <code>PolyCanvas</code>.
 * It provides your basic buttons and text objects that allow the user
 * to change the different states of the canvas.
 * <p>
 * The <code>ControlPanel</code> has a multiple <code>StateChangeListener</code>'s for responding to changes
 * of state in the canvas.  Depending upon the state of the canvas, it will
 * change what options are available.
 *
 * @see StateChangedListener
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class ControlPanel extends JPanel
{
   // -----------------
   // --- Constants ---
   // -----------------
   private static final String LBL_ENTER_POLY = "Enter data for:" ;
   private static final String LBL_POLY_1     = "Poly 1" ;
   private static final String LBL_POLY_2     = "Poly 2" ;
   private static final String LBL_DONE       = "Done Entering" ;
   private static final String LBL_OPS        = "Operations:" ;
   private static final String LBL_INT        = "Intersection" ;
   private static final String LBL_UNION      = "Union" ;
   private static final String LBL_XOR        = "XOR" ;
   private static final String LBL_CLEAR      = "Clear" ;
   private static final String LBL_AREA       = "Area (sq px)" ;
   
   // ------------------------
   // --- Member Variables ---
   // ------------------------
   private PolyCanvas   m_Canvas ;
   private JRadioButton m_Poly1 ;
   private JRadioButton m_Poly2 ;
   private InfoField    m_PolyArea1 ;
   private InfoField    m_PolyArea2 ;
   private InfoField    m_PolyAreaOp ;
   
   private JToggleButton m_Inter ;
   private JToggleButton m_Union ;
   private JToggleButton m_Xor ;
   
   private JButton m_Done ;
   
   private boolean m_ClearingSelection = false ;
   
   // --------------------
   // --- Constructors ---
   // --------------------
   /** Creates a new instance of ControlPanel */
   public ControlPanel( PolyCanvas canvas )
   {
      create();
      m_Canvas = canvas ;

      ButtonEnabledListener bel = new ButtonEnabledListener();
      AreaListener al = new AreaListener();

      bel.stateChanged(null);
      al.stateChanged(null);
      
      m_Canvas.addStateChangedListener(bel);
      m_Canvas.addStateChangedListener(al);
   }
   
   // ----------------------
   // --- Public Methods ---
   // ----------------------
   
   // -----------------------
   // --- Private Methods ---
   // -----------------------
   private void create()
   {
      JLabel lbl_enter = new JLabel(LBL_ENTER_POLY);
      JLabel lbl_area  = new JLabel(LBL_AREA);
      JLabel lbl_ops  = new JLabel(LBL_OPS);
      JLabel lbl_area_op  = new JLabel(LBL_AREA+":");

      m_Poly1 = new JRadioButton(new PolySelectedAction(LBL_POLY_1));
      m_Poly2 = new JRadioButton(new PolySelectedAction(LBL_POLY_2));
      ButtonGroup bg = new ButtonGroup();
      bg.add( m_Poly1 );
      bg.add( m_Poly2 );

      m_Poly1.setSelected(true);
      
      m_PolyArea1 = new InfoField();
      m_PolyArea2 = new InfoField();
      m_PolyAreaOp = new InfoField();
      
      m_Done  = new JButton( new DoneAction());
      
      // -----------------------------------------------------------
      // --- These toggle buttons are NOT in a ButtonGroup       ---
      // --- because I wanted the ability to have none selected. ---
      // --- When they are in a ButtonGroup, you can not have    ---
      // --- no buttons selected once you select one.            ---
      // -----------------------------------------------------------
      m_Inter = new JToggleButton( new InterAction() );
      m_Union = new JToggleButton( new UnionAction() );
      m_Xor   = new JToggleButton( new XorAction()   );
      
      JButton clear = new JButton( new ClearAction());

      setBorder( BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,10,10,10),
                                                    BorderFactory.createEtchedBorder() ));
      setLayout( new GridBagLayout() );
      
      GridBagConstraints gbc = new GridBagConstraints();

      int c = 0 ;
      int r = 0 ;
      int w = 2 ;
      int h = 10 ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(10,10,10,10);
      add( lbl_enter, gbc );
      
      c++;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.EAST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(10,5,5,15);
      add( lbl_area, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(5,15,5,10);
      add( m_Poly1, gbc );
      
      c = 1 ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(5,5,5,10);
      add( m_PolyArea1, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(5,15,5,10);
      add( m_Poly2, gbc );
      
      c = 1 ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(5,5,5,10);
      add( m_PolyArea2, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = w;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(10,10,10,10);
      add( m_Done, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(30,10,10,10);
      add( lbl_ops, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = w;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5,10,5,10);
      add( m_Inter, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = w;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5,10,5,10);
      add( m_Union, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = w;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5,10,10,10);
      add( m_Xor, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(5,10,10,5);
      add( lbl_area_op, gbc );
      
      c = 1 ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.EAST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5,10,10,10);
      add( m_PolyAreaOp, gbc );
      
      c = 0 ; r++ ;
      gbc.gridx = c ;
      gbc.gridy = r ;
      gbc.gridwidth = w;
      gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(20,10,10,10);
      add( clear, gbc );
   }

   // ---------------------
   // --- Inner Classes ---
   // ---------------------
   /**
    * Listens for changes that affect the operation and done buttons.
    */
   private class ButtonEnabledListener implements StateChangedListener
   {
      public void stateChanged( CanvasState state )
      {
         boolean enable = m_Canvas.canPerformOperations();
         m_Inter.setEnabled( enable );
         m_Union.setEnabled( enable );
         m_Xor.setEnabled( enable );
         
         m_Inter.setSelected( m_Canvas.isIntersectionState() );
         m_Union.setSelected( m_Canvas.isUnionState() );
         m_Xor.setSelected(   m_Canvas.isXorState() );
         
         boolean entering = m_Canvas.isEnteringData();
         m_Done.setEnabled( entering );
      }
   }
   
   /**
    * List for changes in the area
    */
   private class AreaListener implements StateChangedListener
   {
      public void stateChanged( CanvasState state )
      {
         double p1 = m_Canvas.getPolyArea1();
         double p2 = m_Canvas.getPolyArea2();
         double op = m_Canvas.getPolyAreaOp();
         m_PolyArea1.setArea( p1 );
         m_PolyArea2.setArea( p2 );
         m_PolyAreaOp.setArea( op );
         m_Poly1.setSelected( p1 < 0.0 );
         m_Poly2.setSelected( p2 < 0.0 );
      }
   }
   
   /**
    * The action taken by Radio buttons that asks the canvas to change state
    */
   private class PolySelectedAction extends AbstractAction
   {
      public PolySelectedAction( String name )
      {
         super(name);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         if( m_Poly1.isSelected() )
         {
            m_Canvas.startEnteringPoly1();
         }
         else
         {
            m_Canvas.startEnteringPoly2();
         }
      }
   }
   
   /**
    * The action take by the done button.
    * It asks the canvas to enter the stop state.
    */
   private class DoneAction extends AbstractAction
   {
      public DoneAction()
      {
         super(LBL_DONE);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_Canvas.stopEntering();
      }
   }
            
   /**
    * The action take by the Intersection button to tell the
    * canvas to enter the intersection state
    */
   private class InterAction extends AbstractAction
   {
      public InterAction()
      {
         super(LBL_INT);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_Canvas.intersection();
      }
   }
      
   /**
    * The action take by the Union button to tell the
    * canvas to enter the union state
    */
   private class UnionAction extends AbstractAction
   {
      public UnionAction()
      {
         super(LBL_UNION);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_Canvas.union();
      }
   }
      
   /**
    * The action take by the Xor button to tell the
    * canvas to enter the xor state
    */
   private class XorAction extends AbstractAction
   {
      public XorAction()
      {
         super(LBL_XOR);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_Canvas.xor();
      }
   }
      
   /**
    * The action taken by the Clear button that tells the
    * canvas to clear its current state and reset.
    */
   private class ClearAction extends AbstractAction
   {
      public ClearAction()
      {
         super(LBL_CLEAR);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_Canvas.clear();
      }
   }
      
   /**
    * InfoField is an object that displays a small amount of text.
    * It was created to display the area values.
    */
   private class InfoField extends JPanel
   {
      private JLabel m_Field = new JLabel(" ");
      private DecimalFormat m_Format = new DecimalFormat( "###0.0" );
      public InfoField()
      {
         setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEtchedBorder(),
                                                        BorderFactory.createEmptyBorder(2,3,2,3) ) );
         setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
         add( Box.createHorizontalGlue() );
         add( m_Field );
         setPreferredSize( new Dimension( 75, 25 ) );
      }
      
      public void setArea( double area )
      {
         String text = " ";
         if( area >= 0.0 )
         {
            text = m_Format.format( area );
         }
         m_Field.setText( text );
      }
   }
   
   // ---------------
   // --- Testing ---
   // ---------------

   /**
    * Simple test program for showing the panel
    */
   public static void main( String[] args )
   {
      try
      {
//         com.l2fprod.gui.plaf.skin.Skin skin = com.l2fprod.gui.plaf.skin.SkinLookAndFeel.loadDefaultThemePack();
//         com.l2fprod.gui.plaf.skin.SkinLookAndFeel.setSkin(skin);
       //  javax.swing.UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
      }
      catch( Throwable e )
      {
         System.out.println("Error occured setting look and feel, msg="+e.getMessage());
         System.exit(0);
      }
      try
      {
         ControlPanel panel = new ControlPanel(new PolyCanvas());
         javax.swing.JDialog dialog = new javax.swing.JDialog((javax.swing.JFrame)null,"ControlPanel Test", true );
         dialog.setDefaultCloseOperation( javax.swing.JDialog.DISPOSE_ON_CLOSE );
         dialog.getContentPane().setLayout( new java.awt.BorderLayout() );
         dialog.getContentPane().add( panel );
         dialog.pack();
         dialog.show();
      }
      catch( Throwable e )
      {
         e.printStackTrace();
      }
      System.exit(0);
   }   
}
