
package spice.tspice;


import java.io.*;
import java.util.*;
import spice.basic.*;
import spice.testutils.JNITestutils;
import spice.testutils.Testutils;
import static spice.basic.AngularUnits.*;

/**
Class TestSpiceQuaternion provides methods that
implement test families for the class SpiceQuaternion.

<p>Version 1.0.0 13-DEC-2009 (NJB)
*/
public class TestSpiceQuaternion extends Object
{

   //
   // Class constants
   //


   //
   // Class variables
   //


   //
   // Methods
   //

   /**
   Test SpiceQuaternion and associated classes.
   */
   public static boolean f_SpiceQuaternion()

      throws SpiceException
   {
      //
      // Constants
      //
      final double                      TIGHT_TOL = 1.e-12;
      final double                      MED_TOL   = 1.e-9;

      final double                      SQ2       = Math.sqrt(2.0);
      final double                      SQ3       = Math.sqrt(3.0);

      final double[]                    zeroArray = new double[4];

      final SpiceQuaternion             qi = new
                                     SpiceQuaternion( 0.0d, 1.0d, 0.0d, 0.0d );
      final SpiceQuaternion             qj = new
                                     SpiceQuaternion( 0.0d, 0.0d, 1.0d, 0.0d );
      final SpiceQuaternion             qk = new
                                     SpiceQuaternion( 0.0d, 0.0d, 0.0d, 1.0d );



      //
      // Local variables
      //
      AxisAndAngle                      axisAng;

      Matrix33                          r;
      Matrix33                          r0;
      Matrix33                          r1;
      Matrix33                          r2;
      Matrix33                          xr;

      SpiceQuaternion                   dq;
      SpiceQuaternion                   qStar;
      SpiceQuaternion                   quat0;
      SpiceQuaternion                   quat1;
      SpiceQuaternion                   quat2;

      Vector3                           av;
      Vector3                           axis;
      Vector3                           v0;
      Vector3                           v1;

      boolean                           ok;

      double                            angle;
      double                            dist;
      double                            elt0;
      double[]                          quatvals;
      double[]                          xvals;

      //
      //  We enclose all tests in a try/catch block in order to
      //  facilitate handling unexpected exceptions.  Unexpected
      //  exceptions are trapped by the catch block at the end of
      //  the routine; expected exceptions are handled locally by
      //  catch blocks associated with error handling test cases.
      //
      //  Therefore, JNISpice calls that are expected to succeed don't
      //  have any subsequent "chckxc" type calls following them, nor
      //  are they wrapped in in try/catch blocks.
      //
      //  Expected exceptions that are *not* thrown are tested
      //  via a call to {@link spice.testutils.Testutils#dogDidNotBark}.
      //

      try
      {

         JNITestutils.topen ( "f_SpiceQuaternion" );




         // ***********************************************************
         //
         //    Error cases
         //
         // ***********************************************************


         //
         // We don't need to test every exception that can occur, but
         // we must:
         //
         //    - Test every exception generated in the Java API layer.
         //
         //    - Test at least one exception generated by CSPICE for
         //      every CSPICE class wrapper.
         //



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase (  "Error: construct SpiceQuaternion using " +
                               "an array of the wrong dimension."          );

         try
         {
            quatvals = new double[2];

            quat0    = new SpiceQuaternion( quatvals );

            //
            // If an exception is *not* thrown, we'll hit this call.
            //

            Testutils.dogDidNotBark (  "SPICE(BADDIMENSION)" );

         }
         catch ( SpiceException ex )
         {
            ok = JNITestutils.chckth ( true, "SPICE(BADDIMENSION)", ex );
         }


         try
         {
            quatvals = new double[5];

            quat0    = new SpiceQuaternion( quatvals );

            //
            // If an exception is *not* thrown, we'll hit this call.
            //

            Testutils.dogDidNotBark (  "SPICE(BADDIMENSION)" );

         }
         catch ( SpiceException ex )
         {
            ok = JNITestutils.chckth ( true, "SPICE(BADDIMENSION)", ex );
         }



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase (  "Error: fetch SpiceQuaternion element using " +
                               "an invalid index."                           );

         try
         {
            quat0 = new SpiceQuaternion( zeroArray );

            elt0  = quat0.getElt( -1 );

            //
            // If an exception is *not* thrown, we'll hit this call.
            //
            Testutils.dogDidNotBark (  "SPICE(INDEXOUTOFRANGE)" );

         }
         catch ( SpiceException ex )
         {
            ok = JNITestutils.chckth ( true, "SPICE(INDEXOUTOFRANGE)", ex );
         }



         try
         {
            quat0 = new SpiceQuaternion( zeroArray );

            elt0  = quat0.getElt( 4 );

            //
            // If an exception is *not* thrown, we'll hit this call.
            //

            Testutils.dogDidNotBark (  "SPICE(INDEXOUTOFRANGE)" );

         }
         catch ( SpiceException ex )
         {
            ok = JNITestutils.chckth ( true, "SPICE(INDEXOUTOFRANGE)", ex );
         }





         // ***********************************************************
         //
         //    Normal cases
         //
         // ***********************************************************


         //
         // Constructor tests:
         //


         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test zero-args constructor." );


         quat0 = new SpiceQuaternion();

         ok    = JNITestutils.chckad( "quat0",
                                      quat0.toArray(),
                                      "~~",
                                      zeroArray,
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test array input constructor." );


         xvals = new double[4];

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = (double)(-i);
         }

         quat0 = new SpiceQuaternion( xvals );


         ok    = JNITestutils.chckad( "quat0",
                                      quat0.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );

         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test scalar constructor." );


         quat0 = new SpiceQuaternion( 1., 2., 3., 4. );

         xvals = new double[4];

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = (double)(i+1);
         }

         ok    = JNITestutils.chckad( "quat0",
                                      quat0.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test rotation matrix constructor." );

         axis          = new Vector3 ( 0.0, 0.0, 1.0 );

         Matrix33 rmat = new Matrix33( axis, Math.PI/2 );

         quat0         = new SpiceQuaternion( rmat );

         xvals         = new double[4];

         xvals[0]      = SQ2/2;
         xvals[1]      = 0.0;
         xvals[2]      = 0.0;
         xvals[3]      = SQ2/2;


         ok    = JNITestutils.chckad( "quat0",
                                      quat0.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );

         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test copy constructor." );


         xvals = new double[4];

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = (double)(-i);
         }

         //
         // Create quat1 from quat0; make sure that changing quat0
         // doesn't affect quat1.
         //
         quat0 = new SpiceQuaternion( xvals );
         quat2 = new SpiceQuaternion( xvals );

         quat1 = new SpiceQuaternion( quat0 );

         quat0 = new SpiceQuaternion( zeroArray );


         ok    = JNITestutils.chckad( "quat1",
                                      quat1.toArray(),
                                      "~~",
                                      quat2.toArray(),
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test getElt." );


         quat0 = new SpiceQuaternion( 1., 2., 3., 4. );

         xvals = new double[4];

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = (double)(i+1);
         }

         //
         // We expect an exact match for each element.
         //
         for ( int i = 0;  i < 4;  i++ )
         {
            ok    = JNITestutils.chcksd( "quat0 elt " + i,
                                         quat0.getElt(i),
                                         "=",
                                         xvals[i],
                                         0.0             );
         }



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test getScalar." );


         quat0 = new SpiceQuaternion( 1., 2., 3., 4. );

         //
         // We expect an exact match.
         //
         ok    = JNITestutils.chcksd( "scalar",
                                      quat0.getScalar(),
                                      "=",
                                      1.0,
                                      0.0                );




         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test getVector." );


         quat0 = new SpiceQuaternion( 1., 2., 3., 4. );

         xvals = new double[3];

         for ( int i = 0;  i < 3;  i++ )
         {
            xvals[i] = (double)(i+2);
         }

         v0 = quat0.getVector();

         //
         // We expect an exact match for each element.
         //
         for ( int i = 0;  i < 3;  i++ )
         {
            ok    = JNITestutils.chcksd( "v0 elt " + i,
                                         v0.getElt(i),
                                         "=",
                                         xvals[i],
                                         0.0             );
         }



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test toArray." );


         quat0 = new SpiceQuaternion( 1., 2., 3., 4. );

         xvals = new double[4];

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = (double)(i+1);
         }

         //
         // We expect an exact match for each element.
         //
         ok    = JNITestutils.chckad( "quat0",
                                      quat0.toArray(),
                                      "=",
                                      xvals,
                                      0.0             );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test add." );


         quat0 = new SpiceQuaternion( 1.0, 2.0, 3.0, 4.0 );
         quat1 = new SpiceQuaternion( 2.0, 3.0, 4.0, 5.0 );
         quat2 = quat1.add( quat0 );

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = quat0.getElt(i) + quat1.getElt(i);
         }

         ok    = JNITestutils.chckad( "quat2",
                                      quat2.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test norm." );


         quat0 = new SpiceQuaternion( 9.0, 12.0, 12.0,  16.0 );

         ok    = JNITestutils.chcksd( "mag",
                                      quat0.norm(),
                                      "~",
                                      25.0,
                                      TIGHT_TOL        );

         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test conjugate." );

         quat0 = new SpiceQuaternion( 1.0,  2.0,  3.0,  4.0 );

         quat1 = new SpiceQuaternion( 1.0, -2.0, -3.0, -4.0 );

         ok    = JNITestutils.chckad( "conjugate",
                                      quat0.conjugate().toArray(),
                                      "~",
                                      quat1.toArray(),
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test dist." );


         quat0 = new SpiceQuaternion( 9.0, 12.0, 12.0,  16.0 );

         ok    = JNITestutils.chcksd( "dist",
                                      quat0.dist(  quat0.negate() ),
                                      "~",
                                      50.0,
                                      TIGHT_TOL        );




         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test sub." );


         quat0 = new SpiceQuaternion( 1.0, 2.0, 3.0, 4.0 );
         quat1 = new SpiceQuaternion( 2.0, 3.0, 4.0, 5.0 );
         quat2 = quat0.sub( quat1 );

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = quat0.getElt(i) - quat1.getElt(i);
         }

         ok    = JNITestutils.chckad( "quat2",
                                      quat2.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test scale." );


         quat0 = new SpiceQuaternion( 1.0, 2.0, 3.0,  4.0 );
         quat1 = quat0.scale( 3.0 );

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = quat0.getElt(i) * 3.0;
         }

         ok    = JNITestutils.chckad( "quat1",
                                      quat1.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test negate." );


         quat0 = new SpiceQuaternion( 1.0, 2.0, 3.0,  4.0 );
         quat1 = quat0.negate();

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = - quat0.getElt(i);
         }

         ok    = JNITestutils.chckad( "quat1",
                                      quat1.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test negate." );


         quat0 = new SpiceQuaternion( 1.0, 2.0, 3.0,  4.0 );
         quat1 = quat0.negate();

         for ( int i = 0;  i < 4;  i++ )
         {
            xvals[i] = - quat0.getElt(i);
         }

         ok    = JNITestutils.chckad( "quat1",
                                      quat1.toArray(),
                                      "~~",
                                      xvals,
                                      TIGHT_TOL        );



         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test getAngularVelocity" );


         quat0 = new SpiceQuaternion(  new Matrix33(
                                   new Vector3( 1.0, 1.0, 1.0 ), Math.PI/4 ) );

         dq    = new SpiceQuaternion( 0.05, 0.1, 0.15, 0.2 );

         av    = quat0.getAngularVelocity( dq );

         //
         // Compute the quaternion representing the expected av.
         //


         qStar = quat0.conjugate();

         quat1 = qStar.mult( dq );

         quat2 = quat1.scale( -2.0 );

         ok    = JNITestutils.chckad( "av",
                                      av.toArray(),
                                      "~~",
                                      quat2.getVector().toArray(),
                                      TIGHT_TOL                   );

         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test multiplication of pure imaginary " +
                              "basis vectors" );


         quat0 = qi.mult( qj );

         ok    = JNITestutils.chckad( "qi x qj",
                                      quat0.toArray(),
                                      "~~",
                                      qk.toArray(),
                                      TIGHT_TOL        );


         quat0 = qj.mult( qk );

         ok    = JNITestutils.chckad( "qj x qk",
                                      quat0.toArray(),
                                      "~~",
                                      qi.toArray(),
                                      TIGHT_TOL        );

         quat0 = qk.mult( qi );

         ok    = JNITestutils.chckad( "qk x qi",
                                      quat0.toArray(),
                                      "~~",
                                      qj.toArray(),
                                      TIGHT_TOL        );


         //
         // --------Case-----------------------------------------------
         //

         JNITestutils.tcase ( "Test multiplication of quaternions " +
                              "representing rotations." );



         r0 = new Matrix33( new Vector3(  1.0, 2.0, 3.0 ), Math.PI/6 );
         r1 = new Matrix33( new Vector3( -1.0, 2.0, 3.0 ), Math.PI/8 );


         quat0 = new SpiceQuaternion( r0 );
         quat1 = new SpiceQuaternion( r1 );

         quat2 = quat1.mult( quat0 );

         r2    = quat2.toMatrix();
         xr    = r1.mxm( r0 );

         for ( int i = 0;  i < 3;  i++ )
         {
            ok    = JNITestutils.chckad( "r2 row " + i,
                                         r2.toArray()[i],
                                         "~~",
                                         xr.toArray()[i],
                                         TIGHT_TOL        );
         }



         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test toMatrix." );

         //
         // Create the expected matrix.
         //
         axis   = new Vector3 ( 0.0, 0.0, 1.0 );

         xr     = new Matrix33( axis, Math.PI/2 );

         quat0  = new SpiceQuaternion( xr );

         r      = quat0.toMatrix();

         //
         // Check `r', row by row.
         //

         for ( int i = 0;  i < 3;  i++ )
         {
            ok    = JNITestutils.chckad( "r row " + i,
                                         r.toArray()[i],
                                         "~~",
                                         xr.toArray()[i],
                                         TIGHT_TOL        );
         }




         //
         // --------Case-----------------------------------------------
         //
         JNITestutils.tcase ( "Test toString." );

         String endl = System.getProperty( "line.separator" );

         quat0 = new SpiceQuaternion( -1.0e-100, -2.0e-100, -3.0e-100,
                                                                   -4.0e-100 );

         String outStr = quat0.toString();

         String xStr   = "(-1.0000000000000000e-100," + endl +
                         " -2.0000000000000000e-100, " +
                          "-3.0000000000000000e-100, " +
                          "-4.0000000000000000e-100)";
         //
         // For debugging:
         //
         //System.out.println( outStr );
         //System.out.println( xStr );

         ok = JNITestutils.chcksc( "outStr",
                                   outStr,
                                   "=",
                                   xStr      );
      }

      catch ( SpiceException ex )
      {
         //
         //  Getting here means we've encountered an unexpected
         //  SPICE exception.  This is analogous to encountering
         //  an unexpected SPICE error in CSPICE.
         //

         ex.printStackTrace();

         ok = JNITestutils.chckth ( false, "", ex );
      }

      //
      // Retrieve the current test status.
      //
      ok = JNITestutils.tsuccess();

      return ( ok );
   }

}

