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

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The <code>AboutDialog</code> shows information on the development of the application.
 *
 * @author Daniel Bridenbecker, Solution Engineering, Inc.
 */
public final class AboutDialog
{
   // -----------------
   // --- Constants ---
   // -----------------
   // Build_1 - Initial release
   
   private static final String COMPANY_NAME = "Solution Engineering, Inc." ;
   private static final String PRODUCT_NAME = "PolyDemo" ;
   private static final String VERSION      = "V1.0";
   private static final String RELEASE      = "Initial";
   private static final String DATE         = "1/4/04";
   
   private static final String PRODUCT_VERSION = ""
   + VERSION
   + " - "
   + RELEASE
   + " - "
   + DATE ;

   private static final String THANKS = ""
   + "<html><body>"
   + "<center>"
   + "Developed by Daniel Bridenbecker.<br>"
   + "<br>"
   + "Thanks to Alan Murta (gpc@cs.man.ac.uk)<br>"
   + "for his General Poly Clipper algorithm.<br>"
   + "<br>"
   + "Thanks to Joseph O'Rourke (orourke@cs.smith.edu)<br>"
   + "for his polygon area algorithm."
   + "</center>"
   + "</body></html>" ;

   // ------------------------
   // --- Member Variables ---
   // ------------------------   
   private JDialog m_Dialog;
   
   // --------------------
   // --- Constructors ---
   // --------------------
   
   /** Creates a new instance of AboutDialog */
   public AboutDialog(Frame parent)
   {
      m_Dialog = new JDialog(parent, "About PolyDemo", true);
      m_Dialog.setResizable(false);
      
      Box about_stack = Box.createVerticalBox();
      about_stack.add(Box.createVerticalStrut(5));
      
      Box icon_box = Box.createHorizontalBox();
      java.net.URL sei_url = AboutDialog.class.getResource("icons/sei_logo.gif");
      
      ImageIcon sei_icon = new ImageIcon(sei_url);
      
      icon_box.add(Box.createHorizontalStrut(25));
      icon_box.add(new JLabel(sei_icon));
      icon_box.add(Box.createHorizontalStrut(25));
      
      icon_box.setAlignmentX(Component.CENTER_ALIGNMENT);
      about_stack.add(icon_box);
      about_stack.add(Box.createVerticalStrut(20));
      
      JLabel blurb1 = new JLabel(PRODUCT_NAME);
      blurb1.setFont(blurb1.getFont().deriveFont(24.0f));
      blurb1.setAlignmentX(Component.CENTER_ALIGNMENT);
      about_stack.add(blurb1);
      about_stack.add(Box.createVerticalStrut(15));
      
      JLabel blurb1b = new JLabel(PRODUCT_VERSION);
      blurb1b.setFont(blurb1b.getFont().deriveFont(18.0f));
      blurb1b.setAlignmentX(Component.CENTER_ALIGNMENT);
      about_stack.add(blurb1b);
      about_stack.add(Box.createVerticalStrut(15));
      
      JLabel blurb2 = new JLabel(THANKS);
      blurb2.setAlignmentX(Component.CENTER_ALIGNMENT);
      Box blurb_box = Box.createHorizontalBox();
      blurb_box.add(Box.createHorizontalStrut(25));
      blurb_box.add(blurb2);
      blurb_box.add(Box.createHorizontalStrut(25));
      
      about_stack.add(blurb_box);
      
      about_stack.add(Box.createVerticalStrut(15));
      JButton dismiss_button = new JButton("OK");
      dismiss_button.setAlignmentX(Component.CENTER_ALIGNMENT);
      dismiss_button.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            m_Dialog.hide();
         }
      });
      about_stack.add(dismiss_button);
      about_stack.add(Box.createGlue());
      about_stack.add(Box.createVerticalStrut(15));
      
      m_Dialog.getContentPane().add(about_stack, BorderLayout.CENTER);
      
      m_Dialog.pack();
      
      Dimension screenDim = Toolkit.getDefaultToolkit ().getScreenSize(); 
      Rectangle winDim = m_Dialog.getBounds();
      m_Dialog.setLocation((screenDim.width - winDim.width) / 2, 
                           (screenDim.height - winDim.height) / 2);  
   }
   
   /**
    * Show the dialog
    */
   public void show()
   {
      m_Dialog.show();
   }
   
   // ---------------
   // --- Testing ---
   // ---------------
   /**
    * Simple test program to display the dialog.
    */
   public static void main( String[] args )
   {
      try
      {
//         com.l2fprod.gui.plaf.skin.Skin skin = com.l2fprod.gui.plaf.skin.SkinLookAndFeel.loadDefaultThemePack();
//         com.l2fprod.gui.plaf.skin.SkinLookAndFeel.setSkin(skin);
   //      javax.swing.UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
      }
      catch( Exception e )
      {
         System.out.println("Error occured setting look and feel, msg="+e.getMessage());
         System.exit(0);
      }
      AboutDialog dialog = new AboutDialog(null);
      dialog.show();
      System.exit(0);
   }
}
