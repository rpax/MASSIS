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

package com.seisw.util.geom;

import junit.framework.*;

/**
 * <code>PolyDefaultTest</code> is a set of unit tests using JUnit for the
 * class <code>PolyDefalt</code>.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class PolyDefaultTest extends TestCase
{
   // -------------------
   // --- Constructor ---
   // -------------------
   /**
    *
    * @param name Name of test case
    */
   public PolyDefaultTest(String name)
   {
      super(name);
   }
   
   // ----------------------
   // --- Static Methods ---
   // ----------------------
   /**
    * Provides the ability to run the tests contained here in.
    *
    * @param args Command line arguements.
    */
   public static void main(String args[])
   {
      junit.textui.TestRunner.run(PolyDefaultTest.class);
   }
   
   /**
    * Return the suite of tests
    */
   public static TestSuite suite()
   {
      return new TestSuite(PolyDefaultTest.class);
   }
   
   // ------------------------
   // --- TestCase Methods ---
   // ------------------------
   /**
    * Construct and initialize any objects that will be used in multiple tests.
    * tests.  Currently Empty.
    */
   protected void setUp()
   {
   }
   
   protected void tearDown()
   {
   }
   
   // -------------
   // --- Tests ---
   // -------------
   /**
    * Test the equality operator when empty
    */
   public void testEqualsEmpty()
   {
      Poly p0a = new PolyDefault();
      Poly p0b = new PolyDefault();
      
      assertTrue( p0a != p0b );
      assertEquals( p0a, p0b );
      assertEquals( p0b, p0a );
      assertEquals( p0a, p0a );
   }
   
   /**
    * Test the equality operator when same
    */
   public void testEqualsSame()
   {  
      Poly p1a = new PolyDefault();
      p1a.add( 0.0, 0.0 );
      p1a.add( 4.0, 0.0 );
      p1a.add( 4.0, 4.0 );
      p1a.add( 0.0, 4.0 );
      
      Poly p1b = new PolyDefault();
      p1b.add( 0.0, 0.0 );
      p1b.add( 4.0, 0.0 );
      p1b.add( 4.0, 4.0 );
      p1b.add( 0.0, 4.0 );
      
      assertTrue( p1a != p1b );
      assertEquals( p1a, p1b );
      assertEquals( p1b, p1a );
      assertEquals( p1a, p1a );
   }      
   
   /**
    * Test the equality operator when same but different order
    */
   public void testEqualsDifferentOrder()
   {  
      Poly p1a = new PolyDefault();
      p1a.add( 0.0, 0.0 );
      p1a.add( 4.0, 0.0 );
      p1a.add( 4.0, 4.0 );
      p1a.add( 0.0, 4.0 );
      
      Poly p1b = new PolyDefault();
      p1b.add( 0.0, 4.0 );
      p1b.add( 0.0, 0.0 );
      p1b.add( 4.0, 0.0 );
      p1b.add( 4.0, 4.0 );
      
      assertTrue( p1a != p1b );
      assertEquals( p1a, p1b );
      assertEquals( p1b, p1a );
   }
   
   /**
    * Test the equality operator when same points but not it same order
    */
   public void testEqualsBadOrder()
   {  
      Poly p1a = new PolyDefault();
      p1a.add( 0.0, 0.0 );
      p1a.add( 4.0, 0.0 );
      p1a.add( 4.0, 4.0 );
      p1a.add( 0.0, 4.0 );
      
      Poly p1b = new PolyDefault();
      p1b.add( 0.0, 0.0 );
      p1b.add( 0.0, 4.0 );
      p1b.add( 4.0, 0.0 );
      p1b.add( 4.0, 4.0 );
      
      assertTrue( !p1a.equals( p1b ) );
      assertTrue( !p1b.equals( p1a ) );
   }
   
   /**
    * Test the equality operator when polys with different number of points
    */
   public void testEqualsDifferentNumPoints()
   {  
      Poly p1a = new PolyDefault();
      p1a.add( 0.0, 0.0 );
      p1a.add( 4.0, 0.0 );
      
      Poly p1b = new PolyDefault();
      p1b.add( 0.0, 0.0 );
      p1b.add( 4.0, 0.0 );
      p1b.add( 4.0, 4.0 );
      p1b.add( 0.0, 4.0 );
      
      assertTrue( !p1a.equals( p1b ) );
      assertTrue( !p1b.equals( p1a ) );
   }
   
   /**
    * Test the equality operator when smae number of points but one point value different
    */
   public void testEqualsDifferentValue()
   {  
      Poly p1a = new PolyDefault();
      p1a.add( 0.0, 0.0 );
      p1a.add( 4.0, 0.0 );
      p1a.add( 4.0, 4.0 );
      p1a.add( 0.0, 4.0 );
      
      Poly p1b = new PolyDefault();
      p1b.add( 0.0, 0.0 );
      p1b.add( 4.0, 0.0 );
      p1b.add( 4.0, 4444444.0 );
      p1b.add( 0.0, 4.0 );
      
      assertTrue( !p1a.equals( p1b ) );
      assertTrue( !p1b.equals( p1a ) );
   }
   
   /**
    * Test the getting the area of a triangle
    */
   public void testAreaTriangle()
   {
      Poly p = new PolyDefault();
      p.add( 0.0, 0.0 );
      p.add( 2.0, 0.0 );
      p.add( 2.0, 2.0 );
      
      assertTrue( 2.0 == p.getArea() );
   }
   
   /**
    * Test the getting the area of a square
    */
   public void testAreaSquare()
   {
      Poly p = new PolyDefault();
      p.add( 0.0, 0.0 );
      p.add( 2.0, 0.0 );
      p.add( 2.0, 2.0 );
      p.add( 0.0, 2.0 );
      
      assertTrue( 4.0 == p.getArea() );
   }
   
   /**
    * Test the getting the area of a non-convex polygon
    */
   public void testAreaNonConvex()
   {
      Poly p = new PolyDefault();
      p.add( 0.0, 0.0 );
      p.add( 0.0, 8.0 );
      p.add( 6.0, 4.0 );
      p.add( 4.0, 2.0 );
      p.add( 2.0, 4.0 );
      
      assertTrue( 20.0 == p.getArea() );
   }
   
   /**
    * Test the getting the area of a polygon with a hole
    */
   public void testAreaWithHole()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault(true);
      p2.add( 1.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 1.0, 3.0 );
      
      Poly p = new PolyDefault();
      p.add( p1 );
      p.add( p2 );
      
      assertTrue( 12.0 == p.getArea() );
   }
   
   /**
    * Test the getting the area of a self-intersecting polygon
    */
   public void testSelfIntersecting()
   {
      Poly p = new PolyDefault();
      p.add( 6.0, 6.0 );
      p.add( 2.0, 6.0 );
      p.add( 2.0, 5.0 );
      p.add( 4.0, 5.0 );
      p.add( 4.0, 1.0 );
      p.add( 2.0, 1.0 );
      p.add( 2.0, 2.0 );
      p.add( 1.0, 2.0 );
      p.add( 1.0, 4.0 );
      p.add( 2.0, 4.0 );
      p.add( 2.0, 2.0 );
      p.add( 3.0, 2.0 );
      p.add( 3.0, 4.0 );
      p.add( 2.0, 4.0 );
      p.add( 2.0, 5.0 );
      p.add( 0.0, 5.0 );
      p.add( 0.0, 1.0 );
      p.add( 2.0, 1.0 );
      p.add( 2.0, 0.0 );
      p.add( 6.0, 0.0 );
      
      assertTrue( 24.0 == p.getArea() );
   }
}
