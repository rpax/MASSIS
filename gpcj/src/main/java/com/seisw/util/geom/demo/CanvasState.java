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
 *        and Alan Murta (http://www.cs.man.ac.uk/aig/staff/alan/software/)."
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

/**
 * <code>CanvasState</code> is an "enum" type object that has fixed objects for each state
 * of the <code>PolyCanvas</code>.  These objects are used with the <code>StateChangedListener</code>
 * so that users of <code>PolyCanvas</code> can get information on the state that the canvas
 * has changed too.
 *
 * @see StateChangedListener
 * @see PolyCanvas
 * 
 * @author Daniel Bridenbecker, Solution Engineering, Inc.
 */
public class CanvasState
{
   // -----------------
   // --- Constants ---
   // -----------------
   /** The canvas is accepting points for the first polygon. */
   public static final CanvasState ENTERING_POLY_1 = new CanvasState( "Entering Poly 1" );
   /** The canvas is accepting points for the second polygon. */
   public static final CanvasState ENTERING_POLY_2 = new CanvasState( "Entering Poly 2" );
   /** Points are not being accepted for either polygon and expecting the user  to select an operation or clear. */
   public static final CanvasState DONE_ENTERING   = new CanvasState( "Done Entering" );
   /** Indicates that the current set operation is intersection. */
   public static final CanvasState INTERSECTION    = new CanvasState( "Intersection" );
   /** Indicates that the current set operation is union. */
   public static final CanvasState UNION           = new CanvasState( "Union" );   
   /** Indicates that the current set operation is xor. */
   public static final CanvasState XOR             = new CanvasState( "XOR" );
   
   // ------------------------
   // --- Member Variables ---
   // ------------------------
   private String m_Name ;
   
   // --------------------
   // --- Constructors ---
   // --------------------
   
   /** Creates a new instance of CanvasState */
   private CanvasState( String name )
   {
      m_Name = name ;
   }
   
   // ------------------------
   // --- Object Overrides ---
   // ------------------------
   /**
    * Return true if the input object is equal to this object, else false
    */
   public boolean equals( Object obj )
   {
      if( !(obj instanceof CanvasState) )
      {
         return false;
      }
      CanvasState that = (CanvasState)obj;
      if( !this.m_Name.equals( that.m_Name ) ) return false ;
      return true ;
   }
   
   /**
    * Return the hashCode of the object.
    *
    * @return an integer value that is the same for two objects
    * whenever their internal representation is the same (equals() is true)
    **/
   public int hashCode()
   {
      int result = 17;
      result += 37*result + m_Name.hashCode();
      return result;
   }

   public String toString()
   {
      return m_Name ;
   }
   
   // ----------------------
   // --- Public Methods ---
   // ----------------------
}
