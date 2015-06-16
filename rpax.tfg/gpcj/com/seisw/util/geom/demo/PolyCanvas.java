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

import javax.swing.JComponent ;
import java.awt.Color ;
import java.awt.Dimension ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.event.MouseListener ;
import java.awt.event.MouseMotionListener ;
import java.awt.event.MouseEvent ;
import java.awt.geom.Point2D ;
import java.awt.Polygon ;
import java.awt.Cursor ;
import java.awt.FontMetrics ;

import java.util.ArrayList ;
import java.util.List ;
import java.util.Iterator ;

import com.seisw.util.geom.Poly ;
import com.seisw.util.geom.PolyDefault ;

/**
 * <code>PolyCanvas</code> is the central UI object of the PolyDemo application.
 * It allows the user to put points on the canvas to define a complex polygon of
 * a single contour.  The clipper algorithm supports more complex polygons, but
 * the polygons created by the user are still complex enough to show the capabilities
 * of the algorithm.
 * <p>
 * <code>PolyCanvas</code> can be in one of the of states specified in <code>CanvasState</code>.
 * It supports the <i>Observer/Listener</i> pattern by accepting <code>StateChangedListener</code>
 * objects and then telling these objects when a state change has been completed.
 * <p>
 * <strong>Refactoring:</strong> There is plenty of opportunity for refactoring within this
 * class, but I'm out of time.  It would be nice to create an inner class that contained
 * a polygon and its associated list of points.  It could possibly be the <code>StatePoly</code>
 * class.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class PolyCanvas extends JComponent
{
   // -----------------
   // --- Constants ---
   // -----------------
   private static final int HALF_POINT_WIDTH = 3 ;
   
   private static final String CLICK_HERE = "Click here to enter points." ;

   // ------------------------
   // --- Member Variables ---
   // ------------------------
   private Poly m_Poly1  = new PolyDefault();
   private Poly m_Poly2  = new PolyDefault();
   private Poly m_PolyOp = new PolyDefault();
   
   private List m_PointsPoly1 = new ArrayList();
   private List m_PointsPoly2 = new ArrayList();
   
   private List m_Listeners = new ArrayList();
   
   private State m_StateCurrent ;
   private State m_StateEnteringPoly1 = new StatePoly(           CanvasState.ENTERING_POLY_1, Color.RED,  m_Poly1, m_PointsPoly1 );
   private State m_StateEnteringPoly2 = new StatePoly(           CanvasState.ENTERING_POLY_2, Color.BLUE, m_Poly2, m_PointsPoly2 );
   private State m_StateStopEntering  = new StateOperation(      CanvasState.DONE_ENTERING );
   private State m_StateIntersection  = new StateOperationInter( CanvasState.INTERSECTION ) ;
   private State m_StateUnion         = new StateOperationUnion( CanvasState.UNION );
   private State m_StateXor           = new StateOperationXor(   CanvasState.XOR );
   
   // --------------------
   // --- Constructors ---
   // --------------------
   /** Creates a new instance of PolyCanvas */
   public PolyCanvas()
   {
      setBackground( Color.white );
      setPreferredSize( new Dimension(300, 300) );
      
      clear();
   }
   
   // --------------------------
   // --- JComponent Methods ---
   // --------------------------

   /**
    * Draw the points and polygons.  Also display what to do message when
    * nothing else displayed and we're in a state to add points.
    */
   public void paint( Graphics g )
   {
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor( Color.white );
      g2.fillRect(0,0, getWidth()-1,getHeight()-1);
      
      if( m_PointsPoly1.isEmpty() && m_PointsPoly2.isEmpty() && 
          m_Poly1.isEmpty() && m_Poly2.isEmpty() && m_PolyOp.isEmpty() &&
          ( (m_StateCurrent == m_StateEnteringPoly1) || (m_StateCurrent == m_StateEnteringPoly2) ) )
      {
         FontMetrics fm = g2.getFontMetrics();
         int width = fm.stringWidth( CLICK_HERE );
         int x = getWidth()/2 - width/2;
         int y = getHeight()/2 ;
         
         g2.setColor( Color.BLACK );
         g2.drawString( CLICK_HERE, x, y );
      }
      else
      {
         drawPointList( g2, Color.RED,  m_PointsPoly1 );
         drawPointList( g2, Color.BLUE, m_PointsPoly2 );

         drawPoly( g2, true, Color.GREEN, m_PolyOp );
         drawPoly( g2, false, Color.RED,   m_Poly1 );
         drawPoly( g2, false, Color.BLUE,  m_Poly2 );
      }
   }

   // -------------------------------
   // --- Private Drawing Methods ---
   // -------------------------------
   private void drawPointList( Graphics2D g2, Color clr, List points )
   {
      g2.setColor( clr );
      Point2D prev = null ;
      for( Iterator it = points.iterator() ; it.hasNext() ; )
      {
         Point2D next = (Point2D)it.next();
         drawPoint( g2, clr, next );
         drawLine( g2, clr, prev, next );
         prev = next ;
      }
   }
   
   private void drawPoint( Graphics2D g2, Color clr, Point2D p )
   {
      int x = (int)p.getX()-HALF_POINT_WIDTH ;
      int y = (int)p.getY()-HALF_POINT_WIDTH ;
      int width = 2*HALF_POINT_WIDTH;
      int height = 2*HALF_POINT_WIDTH;
      g2.setColor( clr );
      g2.fillOval( x, y, width, height );
   }
   
   private void drawLine( Graphics2D g2, Color clr, Point2D prev, Point2D next )
   {
      if( (prev == null) || (next == null ) ) return ;
      
      g2.setColor( clr );
      int x1 = (int)prev.getX();
      int y1 = (int)prev.getY();
      int x2 = (int)next.getX();
      int y2 = (int)next.getY();
      g2.drawLine( x1, y1, x2, y2 );
   }
   
   private void drawPoly( Graphics2D g2, boolean fill, Color clr, Poly poly )
   {
      for( int i = 0 ; i < poly.getNumInnerPoly() ; i++ )
      {
         Poly ip = poly.getInnerPoly(i);
         drawInnerPoly( g2, fill, clr, ip );
      }
   }

   private void drawInnerPoly( Graphics2D g2, boolean fill, Color clr, Poly ip )
   {
      if( ip.isHole() )
      {
         g2.setColor( Color.white );
      }
      else
      {
         g2.setColor( clr );
      }
      
      Polygon jp = new Polygon();
      for( int i = 0 ; i < ip.getNumPoints(); i++ )
      {
         jp.addPoint( (int)ip.getX(i), (int)ip.getY(i) );
      }
      
      if( fill )
      {
         g2.fill( jp );
      }
      else
      {
         g2.draw( jp );
      }
   }
   
   // ----------------------
   // --- Public Methods ---
   // ----------------------

   /**
    * Return true if the set operations can be performed.
    */
   public boolean canPerformOperations()
   {
      return !( (m_StateCurrent == m_StateEnteringPoly1) || (m_StateCurrent == m_StateEnteringPoly2) );
   }

   /**
    * Return true if data points can be added.
    */
   public boolean isEnteringData()
   {
      return (m_StateCurrent == m_StateEnteringPoly1) || (m_StateCurrent == m_StateEnteringPoly2) ;
   }

   /**
    * Return true if the current state is intersection.
    */
   public boolean isIntersectionState()
   {
      return (m_StateCurrent == m_StateIntersection);
   }
   
   /**
    * Return true if the current state is union.
    */
   public boolean isUnionState()
   {
      return (m_StateCurrent == m_StateUnion);
   }
   
   /**
    * Return true if the current state is xor.
    */
   public boolean isXorState()
   {
      return (m_StateCurrent == m_StateXor);
   }
   
   /**
    * Return the area of the polygon number 1.
    * It returns -1 if data is currently being accepted for the
    * polygon.
    */
   public double getPolyArea1()
   {
      double area = -1.0 ;
      if( m_StateCurrent != m_StateEnteringPoly1 )
      {
         area = m_Poly1.getArea();
      }
      return area ;
   }
   
   /**
    * Return the area of the polygon number 2.
    * It returns -1 if data is currently being accepted for the
    * polygon.
    */
   public double getPolyArea2()
   {
      double area = -1.0 ;
      if( m_StateCurrent != m_StateEnteringPoly2 )
      {
         area = m_Poly2.getArea();
      }
      return area ;
   }
   
   /**
    * Return the area of the resulting polygon of the selected set operation.
    * It returns -1 if the canvas is not in a set operation state.
    */
   public double getPolyAreaOp()
   {
      double area = -1.0 ;
      if( (m_StateCurrent == m_StateIntersection) || 
          (m_StateCurrent == m_StateUnion       ) || 
          (m_StateCurrent == m_StateXor         ) )
      {
         area = m_PolyOp.getArea();
      }
      return area ;
   }

   /**
    * Change the state of the canvas to entering points for Polygon 1.
    */
   public void startEnteringPoly1()
   {
      changeState( m_StateEnteringPoly1 );
   }
   
   /**
    * Change the state of the canvas to entering points for Polygon 2.
    */
   public void startEnteringPoly2()
   {
      changeState( m_StateEnteringPoly2 );
   }

   /**
    * Change the state of the canvas to stop/done.
    */
   public void stopEntering()
   {
      changeState( m_StateStopEntering );
   }
   
   /**
    * Change the state of the canvas to intersection.
    */
   public void intersection()
   {
      changeState( m_StateIntersection );
   }
   
   /**
    * Change the state of the canvas to union.
    */
   public void union()
   {
      changeState( m_StateUnion );
   }
   
   /**
    * Change the state of the canvas to xor.
    */
   public void xor()
   {
      changeState( m_StateXor );
   }

   /**
    * Clear the points and polygons of the canvas and reset the
    * state to entering points for polygon 1.
    */
   public void clear()
   {
      if( m_StateCurrent != null )
      {
         m_StateCurrent.stop();
      }
      m_Poly1.clear();
      m_Poly2.clear();
      m_PolyOp.clear();
      m_StateCurrent = m_StateEnteringPoly1 ;
      m_StateCurrent.start();
      fireStateChanged(m_StateCurrent.getCanvasState());
   }

   /**
    * Add a listener for changes in state of the canvas.
    */
   public void addStateChangedListener( StateChangedListener listener )
   {
      m_Listeners.add( listener );
   }
   
   // -----------------------
   // --- Private Methods ---
   // -----------------------
   private void changeState( State newState )
   {
      m_StateCurrent.stop();
      m_StateCurrent = newState ;
      m_StateCurrent.start();
      fireStateChanged(m_StateCurrent.getCanvasState());
   }
   
   private void fireStateChanged( CanvasState newState )
   {
      for( Iterator it = m_Listeners.iterator() ; it.hasNext() ; )
      {
         StateChangedListener listener = (StateChangedListener)it.next();
         listener.stateChanged( newState );
      }
      firePointsChanged();
   }
   
   private void firePointsChanged()
   {
      repaint();
   }
   
   // ---------------------
   // --- Inner Classes ---
   // ---------------------
   
   private abstract class State
   {
      private CanvasState m_CanvasState ;
      
      public State( CanvasState state )
      {
         m_CanvasState = state ;
      }
      public String toString()
      {
         return m_CanvasState.toString() ;
      }
      public CanvasState getCanvasState()
      {
         return m_CanvasState ;
      }
      public abstract void start();
      public abstract void stop();
   }
   
   private class StatePoly extends State
   {
      private Poly m_Poly ;
      private List m_Points ;
      private NewPointListener m_NewPointListener ;
      
      public StatePoly( CanvasState state, Color clr, Poly poly, List points )
      {
         super( state );
         m_Poly = poly ;
         m_Points = points ;
         m_NewPointListener = new NewPointListener( clr, m_Points );
      }
      public void start()
      {
         m_Points.clear();
         m_Poly.clear();
         m_PolyOp.clear();
         PolyCanvas.this.addMouseListener( m_NewPointListener );
         PolyCanvas.this.addMouseMotionListener( m_NewPointListener );
         PolyCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
      
      public void stop()
      {
         for( Iterator it = m_Points.iterator() ; it.hasNext() ; )
         {
            Point2D p = (Point2D)it.next();
            m_Poly.add( p );
         }
         m_Points.clear();
         PolyCanvas.this.removeMouseListener( m_NewPointListener );
         PolyCanvas.this.removeMouseMotionListener( m_NewPointListener );
         PolyCanvas.this.setCursor(Cursor.getDefaultCursor());
      }
   }
   
   private class StateOperation extends State
   {
      public StateOperation( CanvasState state )
      {
         super( state );
      }
      
      public void start()
      {
      }
      
      public void stop()
      {
         m_PolyOp.clear();
      }
   }
   
   private class StateOperationInter extends StateOperation
   {
      public StateOperationInter( CanvasState state )
      {
         super( state );
      }
      
      public void start()
      {
         super.start();
         m_PolyOp = m_Poly1.intersection( m_Poly2 );
      }
   }
   
   private class StateOperationUnion extends StateOperation
   {
      public StateOperationUnion( CanvasState state )
      {
         super( state );
      }
      
      public void start()
      {
         super.start();
         m_PolyOp = m_Poly1.union( m_Poly2 );
      }
   }
   
   private class StateOperationXor extends StateOperation
   {
      public StateOperationXor( CanvasState state )
      {
         super( state );
      }
      
      public void start()
      {
         super.start();
         m_PolyOp = m_Poly1.xor( m_Poly2 );
      }
   }
   
   /**
    * React mouse actions on the canvas.
    */
   private class NewPointListener implements MouseListener, MouseMotionListener
   {
      private boolean m_IsDragging = false ;
      private List m_Points ;
      private Color m_Color ;
      private Point2D m_DragPoint = new Point2D.Double(-10.0,-10.0);
      
      public NewPointListener( Color clr, List points )
      {
         m_Color = clr ;
         m_Points = points ;
      }
      
      public void mouseClicked(MouseEvent e)
      {
      }
      
      public void mouseEntered(MouseEvent e)
      {
      }
      
      public void mouseExited(MouseEvent e)
      {
         mouseReleased(e);
      }
      
      public void mousePressed(MouseEvent e)
      {
         if( e.getButton() == MouseEvent.BUTTON1 )
         {
            m_IsDragging = true ;
         }
      }
      
      public void mouseReleased(MouseEvent e)
      {
         if( m_IsDragging )
         {
            Point2D p = new Point2D.Double( (double)e.getX(), (double)e.getY() );
            m_Points.add( p );
            m_DragPoint.setLocation( -10, -10 );
            firePointsChanged();
         }
         m_IsDragging = false ;
      }
      
      public void mouseDragged(MouseEvent e)
      {
         if( m_IsDragging )
         {
            Point2D last_point = null ;
            if( m_Points.size() > 0 )
            {
               last_point = (Point2D)m_Points.get( m_Points.size()-1 );
            }
            Graphics2D g2 = (Graphics2D)PolyCanvas.this.getGraphics();
            g2.setXORMode( Color.white );
            
            // Erase Previous point and line
            if( m_DragPoint.getX() > 0.0 )
            {
               PolyCanvas.this.drawLine( g2, m_Color, last_point, m_DragPoint );
               PolyCanvas.this.drawPoint( g2, m_Color, m_DragPoint );
            }
            
            // Draw next point
            m_DragPoint.setLocation( (double)e.getX(), (double)e.getY() );
            PolyCanvas.this.drawPoint( g2, m_Color, m_DragPoint );
            PolyCanvas.this.drawLine( g2, m_Color, last_point, m_DragPoint );
         }
      }
      
      public void mouseMoved(MouseEvent e)
      {
      }      
   }
}
