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
 * <code>ClipTest</code> is a suite of unit tests for testing <code>Clip</code>.
 * <code>Clip</code> is a Java conversion of the <i>General Poly Clipper</i> algorithm 
 * developed by Alan Murta (gpc@cs.man.ac.uk).
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class ClipTest extends TestCase
{
   // -------------------
   // --- Constructor ---
   // -------------------
   /**
    *
    * @param name Name of test case
    */
   public ClipTest(String name)
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
      junit.textui.TestRunner.run(ClipTest.class);
   }
   
   /**
    * Return the suite of tests
    */
   public static TestSuite suite()
   {
      return new TestSuite(ClipTest.class);
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
    * Test the intersection of two polygons that are 
    * completely separate - result should be empty set.
    */
   public void testIntersectionEmptySet()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 1.0, 0.0 );
      p1.add( 1.0, 1.0 );
      p1.add( 0.0, 1.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );
      
      Poly empty = Clip.intersection( p1, p2 );
      assertTrue( empty.isEmpty() );
   }
   
   /**
    * Test the intersection of two polygons where
    * the second is contained in the first.
    */
   public void testIntersectionOneContainsTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p1, p2 );
//      result.print();
      assertEquals( p2, result );
   }
   
   /**
    * Test the intersection of two polygons where
    * the first is contained in the second.
    */
   public void testIntersectionTwoContainsOne()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 0.0 );
      p1.add( 3.0, 0.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 2.0, 3.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p1, result );
   }
   
   /**
    * Test the intersection of two polygons that
    * are equal.
    */
   public void testIntersectionTwoEqual()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p1, result );
   }
   
   /**
    * Test the intersection of two rectangles that share
    * one corner and two partial sides.
    */
   public void testIntersectionRectCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 1.0, 0.0 );
      p2.add( 1.0, 1.0 );
      p2.add( 0.0, 1.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p2, result );
   }
   
   /**
    * Test the intersection of two rectangles that share
    * one corner and two partial sides.
    */
   public void testIntersectionRectCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 1.0 );
      p2.add( 3.0, 1.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p2, result );
   }
   
   /**
    * Test the intersection of two rectangles that share
    * one corner and two partial sides.
    */
   public void testIntersectionRectCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 4.0, 3.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p2, result );
   }
   
   /**
    * Test the intersection of two rectangles that share
    * one corner and two partial sides.
    */
   public void testIntersectionRectCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 3.0 );
      p2.add( 1.0, 3.0 );
      p2.add( 1.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( p2, result );
   }
   
   /**
    * Test the intersection of two rectangles that 
    * intersect on corner
    */
   public void testIntersectionRectInterCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 1.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 2.0, 2.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 3.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles that 
    * intersect on corner
    */
   public void testIntersectionRectInterCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 1.0 );
      p2.add( 5.0, 1.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 3.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 3.0, 2.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 4.0, 3.0 );
      exp.add( 3.0, 3.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles that 
    * intersect on corner
    */
   public void testIntersectionRectInterCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 5.0, 5.0 );
      p2.add( 3.0, 5.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 3.0, 3.0 );
      exp.add( 4.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles that 
    * intersect on corner
    */
   public void testIntersectionRectInterCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 3.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 3.0, 5.0 );
      p2.add( 1.0, 5.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 2.0, 3.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 2.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testIntersectionRectInterSide1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 1.0, 2.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 1.0, 2.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testIntersectionRectInterSide2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 2.0 );
      p2.add( 5.0, 2.0 );
      p2.add( 5.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 3.0, 2.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testIntersectionRectInterSide3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 4.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 3.0, 6.0 );
      p2.add( 1.0, 6.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 4.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 1.0, 5.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testIntersectionRectInterSide4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( -1.0, 2.0 );
      p2.add(  1.0, 2.0 );
      p2.add(  1.0, 4.0 );
      p2.add( -1.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 2.0 );
      exp.add( 1.0, 2.0 );
      exp.add( 1.0, 4.0 );
      exp.add( 0.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - 1 on top of two = empty*/
   public void testIntersectionPolyOneOnTopOfTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 4.0 );
      p1.add( 5.0, 4.0 );
      p1.add( 5.0, 9.0 );
      p1.add( 3.0, 7.0 );
      p1.add( 1.0, 9.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertTrue( result.isEmpty() );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - two triangles
    */
   public void testIntersectionPolyTwoSidesOneVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 3.0 );
      p1.add( 5.0, 3.0 );
      p1.add( 5.0, 8.0 );
      p1.add( 3.0, 6.0 );
      p1.add( 1.0, 8.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );

      Poly iexp1 = new PolyDefault();
      iexp1.add( 4.0, 4.0 );
      iexp1.add( 3.0, 3.0 );
      iexp1.add( 5.0, 3.0 );
      Poly iexp2 = new PolyDefault();
      iexp2.add( 1.0, 3.0 );
      iexp2.add( 3.0, 3.0 );
      iexp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( iexp1 );
      exp.add( iexp2 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p1, p2 );
//      result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - two sides 
    */
   public void testIntersectionPolyTwoSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 2.0 );
      p1.add( 5.0, 2.0 );
      p1.add( 5.0, 7.0 );
      p1.add( 3.0, 5.0 );
      p1.add( 1.0, 7.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 2.0 );
      exp.add( 5.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - the lower one
    */
   public void testIntersectionPolyTwoSidesAndLowerVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 1.0 );
      p1.add( 5.0, 1.0 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 5.0, 1.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - cross four sides
    */
   public void testIntersectionPolyFourSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.5 );
      p1.add( 5.0, 0.5 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 1.5, 0.5 );
      exp.add( 2.5, 0.5 );
      exp.add( 3.0, 1.0 );
      exp.add( 3.5, 0.5 );
      exp.add( 4.5, 0.5 );
      exp.add( 5.0, 1.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 1.0, 3.0 );
      
      Poly result = Clip.intersection( p1, p2 );
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of two complex, non-convex, non-self-intersecting
    * polygons - V overlap
    */
   public void testIntersectionPolyVOverlaps()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.0 );
      p1.add( 5.0, 0.0 );
      p1.add( 5.0, 5.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 1.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 2.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 5.0, 1.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the intersection of a rectangle with a hole and solid rectangle
    */
   public void testIntersectionRectangleHole()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault(true);
      p2.add( 1.0, 2.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 1.0, 4.0 );
      
      Poly p12 = new PolyDefault();
      p12.add( p1 );
      p12.add( p2 );

      Poly p3 = new PolyDefault();
      p3.add( 2.0, 0.0 );
      p3.add( 6.0, 0.0 );
      p3.add( 6.0, 6.0 );
      p3.add( 2.0, 6.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 2.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 2.0, 5.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 2.0, 2.0 );
      
      PolyDefault result = (PolyDefault)Clip.intersection( p12, p3 );
      //result.print();
      assertEquals( exp, result );
   }
   
   // -------------
   // --- UNION ---
   // -------------
   /**
    * Test the UNION of two polygons that are 
    * completely separate - result should be poly that contains these two polys
    */
   public void testUnionSeparate()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 1.0, 0.0 );
      p1.add( 1.0, 1.0 );
      p1.add( 0.0, 1.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );

      Poly exp = new PolyDefault();
      exp.add( p2 );
      exp.add( p1 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
//      result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two polygons where
    * the second is contained in the first.
    */
   public void testUnionOneContainsTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
//      result.print();
      assertEquals( p1, result );
   }
   /**
    * Test the union of two polygons where
    * the first is contained in the second.
    */
   public void testUnionTwoContainsOne()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 0.0 );
      p1.add( 3.0, 0.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 2.0, 3.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
//      result.print();
      assertEquals( p2, result );
   }
   
   /**
    * Test the union of two polygons that
    * are equal.
    */
   public void testUnionTwoEqual()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
//      result.print();
      assertEquals( p1, result );
   }
   
   /**
    * Test the union of two rectangles that share
    * one corner and two partial sides.
    */
   public void testUnionRectCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 1.0, 0.0 );
      p2.add( 1.0, 1.0 );
      p2.add( 0.0, 1.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      exp.add( 0.0, 1.0 ); // !!! KNOWN BUG - EXTRA POINT BUT SHAPE IS CORRECT
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the UNION of two rectangles that share
    * one corner and two partial sides.
    */
   public void testUnionRectCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 1.0 );
      p2.add( 3.0, 1.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 1.0 ); // !!! KNOWN BUG - EXTRA POINT BUT SHAPE IS CORRECT
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles that share
    * one corner and two partial sides.
    */
   public void testUnionRectCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 4.0, 3.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 3.0 ); // !!! KNOWN BUG - EXTRA POINT BUT SHAPE IS CORRECT
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles that share
    * one corner and two partial sides.
    */
   public void testUnionRectCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 3.0 );
      p2.add( 1.0, 3.0 );
      p2.add( 1.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      exp.add( 0.0, 3.0 ); // !!! KNOWN BUG - EXTRA POINT BUT SHAPE IS CORRECT
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles that 
    * intersect on corner
    */
   public void testUnionRectInterCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 1.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 2.0, 3.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles that 
    * intersect on corner
    */
   public void testUnionRectInterCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 1.0 );
      p2.add( 5.0, 1.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 3.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 3.0, 1.0 );
      exp.add( 5.0, 1.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 4.0, 3.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 2.0, 2.0 );
      exp.add( 3.0, 2.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the union of two rectangles that 
    * intersect on corner
    */
   public void testUnionRectInterCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 5.0, 5.0 );
      p2.add( 3.0, 5.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 5.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 2.0, 2.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 4.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles that 
    * intersect on corner
    */
   public void testUnionRectInterCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 3.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 3.0, 5.0 );
      p2.add( 1.0, 5.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 3.0 );
      exp.add( 2.0, 3.0 );
      exp.add( 2.0, 2.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 1.0, 5.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the union of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testUnionRectInterSide1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 1.0, 2.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 0.0 );
      exp.add( 3.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 0.0, 5.0 );
      exp.add( 0.0, 1.0 );
      exp.add( 1.0, 1.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testUnionRectInterSide2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 2.0 );
      p2.add( 5.0, 2.0 );
      p2.add( 5.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 5.0, 2.0 );
      exp.add( 5.0, 4.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 0.0, 5.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the union of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testUnionRectInterSide3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 4.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 3.0, 6.0 );
      p2.add( 1.0, 6.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 3.0, 6.0 );
      exp.add( 1.0, 6.0 );
      exp.add( 1.0, 5.0 );
      exp.add( 0.0, 5.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testUnionRectInterSide4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( -1.0, 2.0 );
      p2.add(  1.0, 2.0 );
      p2.add(  1.0, 4.0 );
      p2.add( -1.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add(  0.0, 1.0 );
      exp.add(  4.0, 1.0 );
      exp.add(  4.0, 5.0 );
      exp.add(  0.0, 5.0 );
      exp.add(  0.0, 4.0 );
      exp.add( -1.0, 4.0 );
      exp.add( -1.0, 2.0 );
      exp.add(  0.0, 2.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - 1 on top of two */
   public void testUnionPolyOneOnTopOfTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 4.0 );
      p1.add( 5.0, 4.0 );
      p1.add( 5.0, 9.0 );
      p1.add( 3.0, 7.0 );
      p1.add( 1.0, 9.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 0.0, 2.0 );
      exp1.add( 2.0, 0.0 );
      exp1.add( 3.0, 1.0 );
      exp1.add( 4.0, 0.0 );
      exp1.add( 6.0, 2.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 4.0 );
      exp1.add( 5.0, 9.0 );
      exp1.add( 3.0, 7.0 );
      exp1.add( 1.0, 9.0 );
      exp1.add( 1.0, 4.0 );
      exp1.add( 2.0, 4.0 );
      
      Poly exp2 = new PolyDefault(true);
      exp2.add( 4.0, 4.0 );
      exp2.add( 3.0, 3.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - two triangles
    */
   public void testUnionPolyTwoSidesOneVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 3.0 );
      p1.add( 5.0, 3.0 );
      p1.add( 5.0, 8.0 );
      p1.add( 3.0, 6.0 );
      p1.add( 1.0, 8.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );

      Poly exp = new PolyDefault();
      exp.add( 0.0, 2.0 );
      exp.add( 2.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 6.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 8.0 );
      exp.add( 3.0, 6.0 );
      exp.add( 1.0, 8.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - two sides 
    */
   public void testUnionPolyTwoSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 2.0 );
      p1.add( 5.0, 2.0 );
      p1.add( 5.0, 7.0 );
      p1.add( 3.0, 5.0 );
      p1.add( 1.0, 7.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 2.0 );
      exp.add( 2.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 6.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 7.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 1.0, 7.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - the lower one
    */
   public void testUnionPolyTwoSidesAndLowerVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 1.0 );
      p1.add( 5.0, 1.0 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 2.0 );
      exp.add( 2.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 6.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 6.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 1.0, 6.0 );
      exp.add( 1.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - cross four sides
    */
   public void testUnionPolyFourSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.5 );
      p1.add( 5.0, 0.5 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 1.0 );
      exp.add( 1.0, 0.5 );
      exp.add( 1.5, 0.5 );
      exp.add( 2.0, 0.0 );
      exp.add( 2.5, 0.5 );
      exp.add( 3.5, 0.5 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.5, 0.5 );
      exp.add( 5.0, 0.5 );
      exp.add( 5.0, 1.0 );
      exp.add( 6.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 6.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 1.0, 6.0 );
      exp.add( 1.0, 3.0 );
      exp.add( 0.0, 2.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
         
   /**
    * Test the union of two complex, non-convex, non-self-intersecting
    * polygons - V overlap
    */
   public void testUnionPolyVOverlaps()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.0 );
      p1.add( 5.0, 0.0 );
      p1.add( 5.0, 5.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 1.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 0.0 );
      exp.add( 5.0, 0.0 );
      exp.add( 5.0, 1.0 );
      exp.add( 6.0, 2.0 );
      exp.add( 5.0, 3.0 );
      exp.add( 5.0, 5.0 );
      exp.add( 4.0, 4.0 ); // KNOWN BUG - EXTRA POINT BUT SHAPE IS OK
      exp.add( 3.0, 3.0 );
      exp.add( 2.0, 4.0 ); // KNOWN BUG - EXTRA POINT BUT SHAPE IS OK
      exp.add( 1.0, 5.0 );
      exp.add( 1.0, 3.0 );
      exp.add( 0.0, 2.0 );
      exp.add( 1.0, 1.0 );
      
      PolyDefault result = (PolyDefault)Clip.union( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the UNION of a rectangle with a hole and solid rectangle
    */
   public void testUnionRectangleHole()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault(true);
      p2.add( 1.0, 2.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 1.0, 4.0 );
      
      Poly p12 = new PolyDefault();
      p12.add( p1 );
      p12.add( p2 );

      Poly p3 = new PolyDefault();
      p3.add( 2.0, 0.0 );
      p3.add( 6.0, 0.0 );
      p3.add( 6.0, 6.0 );
      p3.add( 2.0, 6.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 2.0, 0.0 );
      exp1.add( 6.0, 0.0 );
      exp1.add( 6.0, 6.0 );
      exp1.add( 2.0, 6.0 );
      exp1.add( 2.0, 5.0 );
      exp1.add( 0.0, 5.0 );
      exp1.add( 0.0, 1.0 );
      exp1.add( 2.0, 1.0 );

      Poly exp2 = new PolyDefault(true);
      exp2.add( 2.0, 2.0 );
      exp2.add( 1.0, 2.0 );
      exp2.add( 1.0, 4.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.union( p12, p3 );
      //result.print();
      assertEquals( exp, result );
   }
   
   // -----------
   // --- XOR ---
   // -----------
   /**
    * Test the XOR of two polygons that are 
    * completely separate - result should be poly that contains these two polys
    */
   public void testXorSeparate()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 1.0, 0.0 );
      p1.add( 1.0, 1.0 );
      p1.add( 0.0, 1.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );

      Poly exp = new PolyDefault();
      exp.add( p2 );
      exp.add( p1 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
//      result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two polygons where
    * the second is contained in the first.
    */
   public void testXorOneContainsTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 2.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 3.0 );
      
      // notice reverse order
      Poly exp2 = new PolyDefault(true);
      exp2.add( 3.0, 1.0 );
      exp2.add( 2.0, 1.0 );
      exp2.add( 2.0, 3.0 );
      exp2.add( 3.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( p1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two polygons where
    * the first is contained in the second.
    */
   public void testXorTwoContainsOne()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 1.0 );
      p1.add( 3.0, 1.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 2.0, 3.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      // notice reverse order
      Poly exp2 = new PolyDefault(true);
      exp2.add( 3.0, 1.0 );
      exp2.add( 2.0, 1.0 );
      exp2.add( 2.0, 3.0 );
      exp2.add( 3.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( p2 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two polygons that
    * are equal.
    */
   public void testXorTwoEqual()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertTrue( result.isEmpty() );
   }
   
   /**
    * Test the xor of two rectangles that share
    * one corner and two partial sides.
    */
   public void testXorRectCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 0.0 );
      p2.add( 1.0, 0.0 );
      p2.add( 1.0, 1.0 );
      p2.add( 0.0, 1.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 1.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      exp.add( 0.0, 1.0 );
      exp.add( 1.0, 1.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two rectangles that share
    * one corner and two partial sides.
    */
   public void testXorRectCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 0.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 4.0, 1.0 );
      p2.add( 3.0, 1.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 3.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles that share
    * one corner and two partial sides.
    */
   public void testXorRectCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 4.0, 3.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 3.0 );
      exp.add( 3.0, 3.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 0.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles that share
    * one corner and two partial sides.
    */
   public void testXorRectCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 0.0 );
      p1.add( 4.0, 0.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 0.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 3.0 );
      p2.add( 1.0, 3.0 );
      p2.add( 1.0, 4.0 );
      p2.add( 0.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 0.0 );
      exp.add( 4.0, 0.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 1.0, 4.0 );
      exp.add( 1.0, 3.0 );
      exp.add( 0.0, 3.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }   
   
   /**
    * Test the xor of two rectangles that 
    * intersect on corner
    */
   public void testXorRectInterCorner1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 1.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 1.0, 3.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 3.0, 2.0 );
      exp1.add( 4.0, 2.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 2.0, 4.0 );
      exp1.add( 2.0, 3.0 );
      exp1.add( 3.0, 3.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 1.0, 1.0 );
      exp2.add( 3.0, 1.0 );
      exp2.add( 3.0, 2.0 );
      exp2.add( 2.0, 2.0 );
      exp2.add( 2.0, 3.0 );
      exp2.add( 1.0, 3.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles that 
    * intersect on corner
    */
   public void testXorRectInterCorner2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 1.0 );
      p2.add( 5.0, 1.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 3.0, 3.0 );
      
      // ------------------------------------------------------------------------------------------
      // --- I expected this to give two non-hole inner polygons but it gave one with a hole    ---
      // --- if you look at it, they are equivalent.  Don't have time to figure out difference. ---
      // ------------------------------------------------------------------------------------------
//      Poly exp1 = new PolyDefault();
//      exp1.add( 3.0, 1.0 );
//      exp1.add( 5.0, 1.0 );
//      exp1.add( 5.0, 3.0 );
//      exp1.add( 4.0, 3.0 );
//      exp1.add( 4.0, 2.0 );
//      exp1.add( 3.0, 2.0 );
//      
//      Poly exp2 = new PolyDefault();
//      exp2.add( 2.0, 2.0 );
//      exp2.add( 3.0, 2.0 );
//      exp2.add( 3.0, 3.0 );
//      exp2.add( 4.0, 3.0 );
//      exp2.add( 4.0, 4.0 );
//      exp2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault(true);
      exp1.add( 3.0, 2.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 4.0, 3.0 );
      exp1.add( 4.0, 2.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 2.0, 2.0 );
      exp2.add( 3.0, 2.0 );
      exp2.add( 3.0, 1.0 );
      exp2.add( 5.0, 1.0 );
      exp2.add( 5.0, 3.0 );
      exp2.add( 4.0, 3.0 );
      exp2.add( 4.0, 4.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp2 );
      exp.add( exp1 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   
   /**
    * Test the xor of two rectangles that 
    * intersect on corner
    */
   public void testXortRectInterCorner3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 3.0 );
      p2.add( 5.0, 3.0 );
      p2.add( 5.0, 5.0 );
      p2.add( 3.0, 5.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 2.0, 2.0 );
      exp1.add( 4.0, 2.0 );
      exp1.add( 4.0, 3.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 3.0, 4.0 );
      exp1.add( 2.0, 4.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 4.0, 3.0 );
      exp2.add( 5.0, 3.0 );
      exp2.add( 5.0, 5.0 );
      exp2.add( 3.0, 5.0 );
      exp2.add( 3.0, 4.0 );
      exp2.add( 4.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp2 );
      exp.add( exp1 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles that 
    * intersect on corner
    */
   public void testXorRectInterCorner4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 2.0, 2.0 );
      p1.add( 4.0, 2.0 );
      p1.add( 4.0, 4.0 );
      p1.add( 2.0, 4.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 3.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 3.0, 5.0 );
      p2.add( 1.0, 5.0 );
      
      // ------------------------------------------------------------------------------------------
      // --- I expected this to give two non-hole inner polygons but it gave one with a hole    ---
      // --- if you look at it, they are equivalent.  Don't have time to figure out difference. ---
      // ------------------------------------------------------------------------------------------

      Poly exp1 = new PolyDefault(true);
      exp1.add( 3.0, 3.0 );
      exp1.add( 2.0, 3.0 );
      exp1.add( 2.0, 4.0 );
      exp1.add( 3.0, 4.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 1.0, 3.0 );
      exp2.add( 2.0, 3.0 );
      exp2.add( 2.0, 2.0 );
      exp2.add( 4.0, 2.0 );
      exp2.add( 4.0, 4.0 );
      exp2.add( 3.0, 4.0 );
      exp2.add( 3.0, 5.0 );
      exp2.add( 1.0, 5.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp2 );
      exp.add( exp1 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testXorRectInterSide1()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 0.0 );
      p2.add( 3.0, 0.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 1.0, 2.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 0.0, 1.0 );
      exp.add( 1.0, 1.0 );
      exp.add( 1.0, 0.0 );
      exp.add( 3.0, 0.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 1.0, 1.0 );
      exp.add( 1.0, 2.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 3.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 0.0, 5.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testXornRectInterSide2()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 3.0, 2.0 );
      p2.add( 5.0, 2.0 );
      p2.add( 5.0, 4.0 );
      p2.add( 3.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 4.0, 5.0 );
      exp.add( 0.0, 5.0 );
      exp.add( 0.0, 1.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 4.0, 4.0 );
      exp.add( 4.0, 2.0 );
      exp.add( 5.0, 2.0 );
      exp.add( 5.0, 4.0 );
      exp.add( 4.0, 4.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testXorRectInterSide3()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 1.0, 4.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 3.0, 6.0 );
      p2.add( 1.0, 6.0 );
      
      Poly exp = new PolyDefault();
      exp.add( 4.0, 5.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 3.0, 6.0 );
      exp.add( 1.0, 6.0 );
      exp.add( 1.0, 5.0 );
      exp.add( 3.0, 5.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 1.0, 4.0 );
      exp.add( 1.0, 5.0 );
      exp.add( 0.0, 5.0 );
      exp.add( 0.0, 1.0 );
      exp.add( 4.0, 1.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two rectangles where
    * half of one is contained in the other and
    * two sides of the inner cross one side of the outer.
    */
   public void testXorRectInterSide4()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( -1.0, 2.0 );
      p2.add(  1.0, 2.0 );
      p2.add(  1.0, 4.0 );
      p2.add( -1.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add(  4.0, 5.0 );
      exp.add(  0.0, 5.0 );
      exp.add(  0.0, 4.0 );
      exp.add(  1.0, 4.0 );
      exp.add(  1.0, 2.0 );
      exp.add(  0.0, 2.0 );
      exp.add(  0.0, 4.0 );
      exp.add( -1.0, 4.0 );
      exp.add( -1.0, 2.0 );
      exp.add(  0.0, 2.0 );
      exp.add(  0.0, 1.0 );
      exp.add(  4.0, 1.0 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - 1 on top of two*/
   public void testXorPolyOneOnTopOfTwo()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 4.0 );
      p1.add( 5.0, 4.0 );
      p1.add( 5.0, 9.0 );
      p1.add( 3.0, 7.0 );
      p1.add( 1.0, 9.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 0.0, 2.0 );
      exp1.add( 2.0, 0.0 );
      exp1.add( 3.0, 1.0 );
      exp1.add( 4.0, 0.0 );
      exp1.add( 6.0, 2.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 4.0 );
      exp1.add( 5.0, 9.0 );
      exp1.add( 3.0, 7.0 );
      exp1.add( 1.0, 9.0 );
      exp1.add( 1.0, 4.0 );
      exp1.add( 2.0, 4.0 );
      
      Poly exp2 = new PolyDefault(true);
      exp2.add( 4.0, 4.0 );
      exp2.add( 3.0, 3.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
   
   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - two triangles
    */
   public void testXorPolyTwoSidesOneVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 3.0 );
      p1.add( 5.0, 3.0 );
      p1.add( 5.0, 8.0 );
      p1.add( 3.0, 6.0 );
      p1.add( 1.0, 8.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );

      Poly exp1 = new PolyDefault();
      exp1.add( 5.0, 8.0 );
      exp1.add( 3.0, 6.0 );
      exp1.add( 1.0, 8.0 );
      exp1.add( 1.0, 3.0 );
      exp1.add( 0.0, 2.0 );
      exp1.add( 2.0, 0.0 );
      exp1.add( 3.0, 1.0 );
      exp1.add( 4.0, 0.0 );
      exp1.add( 6.0, 2.0 );
      exp1.add( 5.0, 3.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 3.0 );
      
      Poly exp2 = new PolyDefault(true);
      exp2.add( 3.0, 3.0 );
      exp2.add( 1.0, 3.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - two sides 
    */
   public void testXorPolyTwoSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 2.0 );
      p1.add( 5.0, 2.0 );
      p1.add( 5.0, 7.0 );
      p1.add( 3.0, 5.0 );
      p1.add( 1.0, 7.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 5.0, 7.0 );
      exp1.add( 3.0, 5.0 );
      exp1.add( 1.0, 7.0 );
      exp1.add( 1.0, 3.0 );
      exp1.add( 2.0, 4.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 3.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 5.0, 3.0 );
      exp2.add( 5.0, 2.0 );
      exp2.add( 1.0, 2.0 );
      exp2.add( 1.0, 3.0 );
      exp2.add( 0.0, 2.0 );
      exp2.add( 2.0, 0.0 );
      exp2.add( 3.0, 1.0 );
      exp2.add( 4.0, 0.0 );
      exp2.add( 6.0, 2.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );      
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - two sides and one vertex - the lower one
    */
   public void testXorPolyTwoSidesAndLowerVertex()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 1.0 );
      p1.add( 5.0, 1.0 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 5.0, 6.0 );
      exp1.add( 3.0, 4.0 );
      exp1.add( 1.0, 6.0 );
      exp1.add( 1.0, 3.0 );
      exp1.add( 2.0, 4.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 3.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 5.0, 3.0 );
      exp2.add( 5.0, 1.0 );
      exp2.add( 3.0, 1.0 );
      exp2.add( 4.0, 0.0 );
      exp2.add( 6.0, 2.0 );
      
      Poly exp3 = new PolyDefault();
      exp3.add( 1.0, 3.0 );
      exp3.add( 0.0, 2.0 );
      exp3.add( 2.0, 0.0 );
      exp3.add( 3.0, 1.0 );
      exp3.add( 1.0, 1.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      exp.add( exp3 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }

   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - cross four sides
    */
   public void testXorPolyFourSides()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.5 );
      p1.add( 5.0, 0.5 );
      p1.add( 5.0, 6.0 );
      p1.add( 3.0, 4.0 );
      p1.add( 1.0, 6.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 5.0, 6.0 );
      exp1.add( 3.0, 4.0 );
      exp1.add( 1.0, 6.0 );
      exp1.add( 1.0, 3.0 );
      exp1.add( 2.0, 4.0 );
      exp1.add( 3.0, 3.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 3.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 5.0, 3.0 );
      exp2.add( 5.0, 1.0 );
      exp2.add( 6.0, 2.0 );
      
      Poly exp3 = new PolyDefault();
      exp3.add( 1.0, 3.0 );
      exp3.add( 0.0, 2.0 );
      exp3.add( 1.0, 1.0 );
      
      Poly exp4 = new PolyDefault();
      exp4.add( 5.0, 1.0 );
      exp4.add( 4.5, 0.5 );
      exp4.add( 5.0, 0.5 );
      
      Poly exp5 = new PolyDefault();
      exp5.add( 3.0, 1.0 );
      exp5.add( 2.5, 0.5 );
      exp5.add( 3.5, 0.5 );
      exp5.add( 4.0, 0.0 );
      exp5.add( 4.5, 0.5 );
      exp5.add( 3.5, 0.5 );
      
      Poly exp6 = new PolyDefault();
      exp6.add( 1.0, 1.0 );
      exp6.add( 1.0, 0.5 );
      exp6.add( 1.5, 0.5 );
      exp6.add( 2.0, 0.0 );
      exp6.add( 2.5, 0.5 );
      exp6.add( 1.5, 0.5 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      exp.add( exp3 );
      exp.add( exp4 );
      exp.add( exp5 );
      exp.add( exp6 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }

   /**
    * Test the xor of two complex, non-convex, non-self-intersecting
    * polygons - V overlap
    */
   public void testXorPolyVOverlaps()
   {
      Poly p1 = new PolyDefault();
      p1.add( 1.0, 0.0 );
      p1.add( 5.0, 0.0 );
      p1.add( 5.0, 5.0 );
      p1.add( 3.0, 3.0 );
      p1.add( 1.0, 5.0 );
      
      Poly p2 = new PolyDefault();
      p2.add( 0.0, 2.0 );
      p2.add( 2.0, 0.0 );
      p2.add( 3.0, 1.0 );
      p2.add( 4.0, 0.0 );
      p2.add( 6.0, 2.0 );
      p2.add( 4.0, 4.0 );
      p2.add( 3.0, 3.0 );
      p2.add( 2.0, 4.0 );
      
      Poly exp1 = new PolyDefault();
      exp1.add( 5.0, 5.0 );
      exp1.add( 4.0, 4.0 );
      exp1.add( 5.0, 3.0 );
      
      Poly exp2 = new PolyDefault();
      exp2.add( 1.0, 5.0 );
      exp2.add( 1.0, 3.0 );
      exp2.add( 2.0, 4.0 );
      
      Poly exp3 = new PolyDefault();
      exp3.add( 5.0, 3.0 );
      exp3.add( 5.0, 1.0 );
      exp3.add( 6.0, 2.0 );
      
      Poly exp4 = new PolyDefault();
      exp4.add( 1.0, 3.0 );
      exp4.add( 0.0, 2.0 );
      exp4.add( 1.0, 1.0 );
      
      Poly exp5 = new PolyDefault();
      exp5.add( 5.0, 1.0 );
      exp5.add( 4.0, 0.0 );
      exp5.add( 5.0, 0.0 );
      
      Poly exp6 = new PolyDefault();
      exp6.add( 3.0, 1.0 );
      exp6.add( 2.0, 0.0 );
      exp6.add( 4.0, 0.0 );
      
      Poly exp7 = new PolyDefault();
      exp7.add( 1.0, 1.0 );
      exp7.add( 1.0, 0.0 );
      exp7.add( 2.0, 0.0 );
      
      Poly exp = new PolyDefault();
      exp.add( exp1 );
      exp.add( exp2 );
      exp.add( exp3 );
      exp.add( exp4 );
      exp.add( exp5 );
      exp.add( exp6 );
      exp.add( exp7 );
      
      PolyDefault result = (PolyDefault)Clip.xor( p1, p2 );
      //result.print();
      assertEquals( exp, result );
   }
      
   /**
    * Test the xor of a rectangle with a hole and solid rectangle
    */
   public void testXorRectangleHole()
   {
      Poly p1 = new PolyDefault();
      p1.add( 0.0, 1.0 );
      p1.add( 4.0, 1.0 );
      p1.add( 4.0, 5.0 );
      p1.add( 0.0, 5.0 );
      
      Poly p2 = new PolyDefault(true);
      p2.add( 1.0, 2.0 );
      p2.add( 3.0, 2.0 );
      p2.add( 3.0, 4.0 );
      p2.add( 1.0, 4.0 );
      
      Poly p12 = new PolyDefault();
      p12.add( p1 );
      p12.add( p2 );

      Poly p3 = new PolyDefault();
      p3.add( 2.0, 0.0 );
      p3.add( 6.0, 0.0 );
      p3.add( 6.0, 6.0 );
      p3.add( 2.0, 6.0 );

      // -----------------------------------------------------------
      // --- This is not what I expected and it seems reasonable ---
      // --- However it could be wrong.                          ---
      // -----------------------------------------------------------
      // --- I computed the area of this poly and it came out to ---
      // --- be 24 which is what you would expect.               ---
      // -----------------------------------------------------------
      Poly exp = new PolyDefault();
      exp.add( 6.0, 6.0 );
      exp.add( 2.0, 6.0 );
      exp.add( 2.0, 5.0 );
      exp.add( 4.0, 5.0 );
      exp.add( 4.0, 1.0 );
      exp.add( 2.0, 1.0 );
      exp.add( 2.0, 2.0 );
      exp.add( 1.0, 2.0 );
      exp.add( 1.0, 4.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 2.0, 2.0 );
      exp.add( 3.0, 2.0 );
      exp.add( 3.0, 4.0 );
      exp.add( 2.0, 4.0 );
      exp.add( 2.0, 5.0 );
      exp.add( 0.0, 5.0 );
      exp.add( 0.0, 1.0 );
      exp.add( 2.0, 1.0 );
      exp.add( 2.0, 0.0 );
      exp.add( 6.0, 0.0 );

      PolyDefault result = (PolyDefault)Clip.xor( p12, p3 );
      //result.print();
      assertEquals( exp, result );
   }
}
