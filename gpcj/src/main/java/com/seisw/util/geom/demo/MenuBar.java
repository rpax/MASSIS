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
import javax.swing.JMenu ;
import javax.swing.JMenuBar ;
import javax.swing.JMenuItem ;
import javax.swing.ImageIcon ;
import javax.swing.Action ;
import javax.swing.AbstractAction ;
import java.awt.event.ActionEvent ;
import java.awt.event.KeyEvent ;

/**
 * <code>MenuBar</code> is the menu bar accros the top of the frame.
 *
 * @author  Daniel Bridenbecker, Solution Engineering, Inc.
 */
public class MenuBar extends JMenuBar
{
   // -----------------
   // --- Constants ---
   // -----------------
      
   private static final String LBL_FILE = "File";
   private static final String LBL_HELP = "Help" ;
   
   private static final String LBL_EXIT = "Exit" ;
   
   private static final String LBL_ABOUT   = "About PolyDemo...";
   
   private static final String ICON_STR_HELP  = "icons/Help16.gif" ;
   private static final String ICON_STR_ABOUT = "icons/About16.gif" ;
   
   // ------------------------
   // --- Member Variables ---
   // ------------------------
   private JFrame m_MainFrame ;
   
   private Action m_ExitAction = new ExitAction();

   // --------------------
   // --- Constructors ---
   // --------------------
   /**
    * Creates a new instance of MenuBar
    */
   public MenuBar( JFrame frame )
   {
      m_MainFrame  = frame ;
      createMenuBar();
   }
   
   // -------------------------
   // --- Interface Methods ---
   // -------------------------
   
   // ----------------------
   // --- Public Methods ---
   // ----------------------
   
   /**
    * Exit the application
    */
   public void exit()
   {
      m_ExitAction.actionPerformed(null);
   }
   
   // -----------------------
   // --- Private Methods ---
   // -----------------------
   /**
    *
    */
   private void createMenuBar()
   {
      // Keep a handle to fileMenu: it changes during run-time
      JMenu file_menu = createFileMenu();
      JMenu help_menu = createHelpMenu();
      
      add( file_menu );
      add( help_menu );
   }
   
   /**
    *
    */
   private JMenu createFileMenu()
   {
      JMenuItem item = null ;
      JMenu retMenu = new JMenu(LBL_FILE);
      retMenu.setMnemonic(KeyEvent.VK_F);
      
      item = new JMenuItem( m_ExitAction );
      item.setMnemonic(KeyEvent.VK_X);
      retMenu.add(item); 
      
      return retMenu ;
   }
   
   /**
    *
    */
   private JMenu createHelpMenu()
   {
      JMenuItem item = null ;
      
      JMenu retMenu = new JMenu(LBL_HELP);
      retMenu.setMnemonic(KeyEvent.VK_H);
            
      item = new JMenuItem(new AboutAction());
      item.setMnemonic(KeyEvent.VK_A);
      retMenu.add(item);
      
      return retMenu ;
   }
   
   // -------------------
   // --- Inner Class ---
   // -------------------
   /**
    * Close the frame and exit the application
    */
   private class ExitAction extends AbstractAction
   {
      public ExitAction()
      {
         super(LBL_EXIT);
      }
      
      public void actionPerformed(ActionEvent e)
      {
         m_MainFrame.dispose();
         System.exit(0);
      }
   }
      
   /**
    * Show the information about application
    */
   private class AboutAction extends AbstractAction
   {
      public AboutAction()
      {
         super(LBL_ABOUT, new ImageIcon( MenuBar.this.getClass().getResource(ICON_STR_ABOUT)));
      }
      public void actionPerformed(ActionEvent e)
      {
         AboutDialog dialog = new AboutDialog(m_MainFrame);
         dialog.show();
      }         
   }
}
