
package spice.basic;

/**
Class GFOccultationSearch supports searches for
occultation events.

<h2>Files</h2>

<p>Appropriate SPICE kernels must be loaded by the calling program
   before methods of this class are called.

<p>The following data are required:

<ul>
<li>
        SPK data: the calling application must load ephemeris data
        for the targets, source and observer that cover the time
        period specified by the window `cnfine'. If aberration
        corrections are used, the states of the target bodies and of
        the observer relative to the solar system barycenter must be
        calculable from the available ephemeris data. Typically
        ephemeris data are made available by loading one or more SPK
        files via {@link KernelDatabase#load}.
</li>
<li> PCK data: bodies modeled as triaxial ellipsoids must have
        semi-axis lengths provided by variables in the kernel pool.
        Typically these data are made available by loading a text
        PCK file via {@link KernelDatabase#load}.
</li>
<li>
        FK data: if either of the reference frames designated by
        `bframe' or `fframe' are not built in to the SPICE system,
        one or more FKs specifying these frames must be loaded.
</li>
</ul>

<p>
   The following data may be required:
<ul>
<li>    DSK data: if either `fshape' or `bshape' indicates that DSK
        data are to be used, DSK files containing topographic data
        for the target body must be loaded. If a surface list is
        specified, data for at least one of the listed surfaces must
        be loaded.

<p>
        Surface name-ID associations: if surface names are specified
        in `fshape' or `bshape', the association of these names with
        their corresponding surface ID codes must be established by
        assignments of the kernel variables
<pre>
   NAIF_SURFACE_NAME
   NAIF_SURFACE_CODE
   NAIF_SURFACE_BODY
</pre>
        Normally these associations are made by loading a text
        kernel containing the necessary assignments. An example
        of such a set of assignments is
<pre>
   NAIF_SURFACE_NAME += 'Mars MEGDR 128 PIXEL/DEG'
   NAIF_SURFACE_CODE += 1
   NAIF_SURFACE_BODY += 499
</pre>
      
<li>
        CK data: either of the body-fixed frames to which `fframe' or
        `bframe' refer might be a CK frame. If so, at least one CK
        file will be needed to permit transformation of vectors
        between that frame and the J2000 frame.
</li>
<li>
        SCLK data: if a CK file is needed, an associated SCLK
        kernel is required to enable conversion between encoded SCLK
        (used to time-tag CK data) and barycentric dynamical time
        (TDB).
</li>
</ul>
<p>
   Kernel data are normally loaded once per program run, NOT every
   time a method of this class is called.


 

<h2>Particulars</h2>

<p>
   This class determines a set of one or more time intervals
   within the confinement window when a specified type of
   occultation occurs. The resulting set of intervals is returned as
   a SPICE window.
<p>
   Below we discuss in greater detail aspects of this class's
   solution process that are relevant to correct and efficient
   use of this class in user applications.

<h3> The Search Process </h3>

<p>
   The search for occultations is treated as a search for state
   transitions: times are sought when the state of the `back' body
   changes from "not occulted" to "occulted" or vice versa.

<h3>Step Size</h3>

<p>
   Each interval of the confinement window is searched as follows:
   first, the input step size is used to determine the time separation
   at which the occultation state will be sampled. Starting at the left
   endpoint of the interval, samples of the occultation state will be
   taken at each step. If a state change is detected, a root has been
   bracketed; at that point, the "root"--the time at which the state
   change occurs---is found by a refinement process, for example, via
   binary search.
<p>
   Note that the optimal choice of step size depends on the lengths
   of the intervals over which the occultation state is constant:
   the step size should be shorter than the shortest occultation
   duration and the shortest period between occultations, within
   the confinement window.
<p>
   Having some knowledge of the relative geometry of the targets and
   observer can be a valuable aid in picking a reasonable step size.
   In general, the user can compensate for lack of such knowledge by
   picking a very short step size; the cost is increased computation
   time.
<p>
   Note that the step size is not related to the precision with which
   the endpoints of the intervals of the result window are computed.
   That precision level is controlled by the convergence tolerance.


<h3>Convergence Tolerance</h3>

<p>
   Once a root has been bracketed, a refinement process is used to
   narrow down the time interval within which the root must lie. This
   refinement process terminates when the location of the root has been
   determined to within an error margin used the "convergence
   tolerance." The convergence tolerance used by this class is set
   via the parameter {@link GF#CNVTOL}.
<p>
   The value of {@link GF#CNVTOL} is set to a "tight" value so that the
   tolerance doesn't limit the accuracy of solutions found by this
   class. In general the accuracy of input data will be the limiting
   factor.
<p>
   Making the tolerance tighter than
   {@link GF#CNVTOL} is unlikely to be useful, since the results are
   unlikely to be more accurate. Making the tolerance looser will speed
   up searches somewhat, since a few convergence steps will be omitted.
   However, in most cases, the step size is likely to have a much
   greater effect on processing time than would the convergence
   tolerance.


<h3>The Confinement Window</h3>

<p>
   The simplest use of the confinement window is to specify a time
   interval within which a solution is sought.
<p>
   The confinement window also can be used to restrict a search to
   a time window over which required data (typically ephemeris
   data, in the case of occultation searches) are known to be
   available.
<p>
   In some cases, the confinement window be used to make searches
   more efficient. Sometimes it's possible to do an efficient search
   to reduce the size of the time period over which a relatively
   slow search of interest must be performed. See the "CASCADE"
   example program in gf.req for a demonstration.


 
<h3>Using DSK data</h3>

 
<p><b>DSK loading and unloading</b>
 
<p>DSK files providing data used by this routine are loaded by 
      calling {@link KernelDatabase#load} and can be unloaded by 
      calling {@link KernelDatabase#unload} or
      {@link KernelDatabase#clear}. See the documentation of 
      {@link KernelDatabase#load} for limits on numbers 
      of loaded DSK files. 
 
      For run-time efficiency, it's desirable to avoid frequent 
      loading and unloading of DSK files. When there is a reason to 
      use multiple versions of data for a given target body---for 
      example, if topographic data at varying resolutions are to be 
      used---the surface list can be used to select DSK data to be 
      used for a given computation. It is not necessary to unload 
      the data that are not to be used. This recommendation presumes 
      that DSKs containing different versions of surface data for a 
      given body have different surface ID codes. 
 
 
<p><b>DSK data priority</b>

 
<p>   A DSK coverage overlap occurs when two segments in loaded DSK 
      files cover part or all of the same domain---for example, a 
      given longitude-latitude rectangle---and when the time 
      intervals of the segments overlap as well. 
 
<p>   When DSK data selection is prioritized, in case of a coverage 
      overlap, if the two competing segments are in different DSK 
      files, the segment in the DSK file loaded last takes 
      precedence. If the two segments are in the same file, the 
      segment located closer to the end of the file takes 
      precedence. 
 
<p>   When DSK data selection is unprioritized, data from competing 
      segments are combined. For example, if two competing segments 
      both represent a surface as a set of triangular plates, the 
      union of those sets of plates is considered to represent the 
      surface.  
 
<p>   Currently only unprioritized data selection is supported. 
      Because prioritized data selection may be the default behavior 
      in a later version of the routine, the UNPRIORITIZED keyword is 
      required in the `fshape' and `bshape' arguments. 
 
       
<p> <b>Syntax of the shape input arguments for the DSK case</b>

 
<p>   The keywords and surface list in the target shape arguments
      `bshape' and `fshape' are called "clauses." The clauses may
      appear in any order, for example
<pre> 
   "DSK/&#60surface list&#62/UNPRIORITIZED"
   "DSK/UNPRIORITIZED/&#60surface list&#62"
   "UNPRIORITIZED/&#60surface list&#62/DSK"
</pre> 
      The simplest form of the `method' argument specifying use of 
      DSK data is one that lacks a surface list, for example: 
<pre> 
   "DSK/UNPRIORITIZED" 
</pre>
<p>
      For applications in which all loaded DSK data for the target 
      body are for a single surface, and there are no competing 
      segments, the above string suffices. This is expected to be 
      the usual case. 

<p> 
      When, for the specified target body, there are loaded DSK 
      files providing data for multiple surfaces for that body, the 
      surfaces to be used by this routine for a given call must be 
      specified in a surface list, unless data from all of the 
      surfaces are to be used together. 
 
<p>
      The surface list consists of the string 
<pre>
   "SURFACES = "
</pre>
<p> 
      followed by a comma-separated list of one or more surface 
      identifiers. The identifiers may be names or integer codes in 
      string format. For example, suppose we have the surface 
      names and corresponding ID codes shown below: 
 
<pre>
   Surface Name                              ID code 
   ------------                              ------- 
   "Mars MEGDR 128 PIXEL/DEG"                1 
   "Mars MEGDR 64 PIXEL/DEG"                 2 
   "Mars_MRO_HIRISE"                         3 
</pre>

<p>
      If data for all of the above surfaces are loaded, then 
      data for surface 1 can be specified by either 
<pre>
   "SURFACES = 1" 
</pre>
or 
<pre> 
   "SURFACES = \"Mars MEGDR 128 PIXEL/DEG\"" 
</pre> 
      Escaped double quotes are used to delimit the surface name because 
      it contains blank characters.  
      
<p>    
      To use data for surfaces 2 and 3 together, any 
      of the following surface lists could be used: 
<pre>
   "SURFACES = 2, 3" 
 
   "SURFACES = \"Mars MEGDR  64 PIXEL/DEG\", 3" 
 
   "SURFACES = 2, Mars_MRO_HIRISE" 
 
   "SURFACES = \"Mars MEGDR 64 PIXEL/DEG\", Mars_MRO_HIRISE" 
</pre>   
      An example of a shape argument that could be constructed 
      using one of the surface lists above is 
<pre>
   "DSK/UNPRIORITIZED/SURFACES = \"Mars MEGDR 64 PIXEL/DEG\", 3" 
</pre>



<h2>Code Examples</h2>

<p>
The numerical results shown for these examples may differ across
platforms. The results depend on the SPICE kernels used as
input, the compiler and supporting libraries, and the machine
specific arithmetic implementation.

<ol>

<li>
<p>  Find occultations of the Sun by the Moon (that is, solar
      eclipses) as seen from the center of the Earth over the month
      December, 2001.

<p>      Use light time corrections to model apparent positions of Sun
      and Moon. Stellar aberration corrections are not specified
      because they don't affect occultation computations.

<p>      We select a step size of 3 minutes, which means we
      ignore occultation events lasting less than 3 minutes,
      if any exist.

<p>      Use the meta-kernel shown below to load the required SPICE
      kernels.

<pre>
KPL/MK

File name: GFOccultationSearchEx1.tm

This meta-kernel is intended to support operation of SPICE
example programs. The kernels shown here should not be
assumed to contain adequate or correct versions of data
required by SPICE-based user applications.

In order for an application to use this meta-kernel, the
kernels referenced here must be present in the user's
current working directory.

The names and contents of the kernels referenced 
by this meta-kernel are as follows: 

  File name                        Contents 
  ---------                        -------- 
  de430.bsp                        Planetary ephemeris 
  pck00010.tpc                     Planet orientation and 
                                   radii 
  naif0012.tls                     Leapseconds 
 
\begindata

  KERNELS_TO_LOAD = ( 'de430.bsp',
                      'pck00010.tpc',
                      'naif0012.tls'  )

\begintext

</pre>

<p> Example code begins here.

<pre>

   import spice.basic.*;
   import static spice.basic.GFOccultationSearch.*;


   class GFOccultationSearchEx1
   {
      //
      // Load the JNISpice shared object library
      // at initialization time.
      //
      static { System.loadLibrary( "JNISpice" ); }

      public static void main ( String[] args )
      {
         try {

            //
            // Constants
            //
            final String META    = "GFOccultationSearchEx1.tm";

            final String TIMFMT  =
               "YYYY MON DD HR:MN:SC.###### (TDB)::TDB";

            final int    NINTVLS = 100;

            //
            // Declare the SPICE windows we'll need for the searches
            // and window arithmetic. The result window will be
            // assigned values later; the confinement window must
            // be non-null before it's used.
            //
            SpiceWindow result     = null;
            SpiceWindow cnfine     = new SpiceWindow();

            //
            // Declare and assign values to variables required to
            // specify the geometric condition to search for.
            //

            AberrationCorrection
               abcorr = new AberrationCorrection( "CN" );
            Body back              = new Body ( "Sun" );
            Body front             = new Body ( "Moon" );
            Body observer          = new Body ( "Earth"   );
            ReferenceFrame bframe  = new ReferenceFrame( "IAU_SUN" );
            ReferenceFrame fframe  = new ReferenceFrame( "IAU_MOON" );
            String bshape          = GF.EDSHAP;
            String fshape          = GF.EDSHAP;

            //
            // Load kernels.
            //
            KernelDatabase.load ( META );

            //
            // Store the time bounds of our search interval in
            // the `cnfine' confinement window.
            //
            TDBTime et0 = new TDBTime ( "2001 Dec 1 00:00:00" );
            TDBTime et1 = new TDBTime ( "2002 Jan 1 00:00:00" );

            cnfine.insert( et0.getTDBSeconds(),
                           et1.getTDBSeconds() );
            //
            // Select a 3-minute step. We'll ignore any occultations
            // lasting less than 3 minutes.  Units are TDB seconds.
            //
            double step = 3 * 60.0;

            GFOccultationSearch occultationSearch =

               new GFOccultationSearch ( ANY,
                                         front,  fshape,  fframe,
                                         back,   bshape,  bframe,
                                         abcorr, observer         );

            //
            // Run the search.
            //
            // Specify the maximum number of intervals in the result
            // window.
            //
            result = occultationSearch.run( cnfine, step, NINTVLS );

            //
            // Display results.
            //
            int count = result.card();

            if ( count == 0 )
            {
               System.out.format ( "No occultation was found.%n%n" );
            }
            else
            {
               //
               // Fetch and display each occultation interval.
               //
               double[] interval = new double[2];
               String   begstr;
               String   endstr;

               for ( int i = 0;  i < count;  i++ )
               {
                  //
                  // Fetch the endpoints of the Ith interval
                  // of the result window.
                  //
                  interval = result.getInterval( i );

                  begstr = ( new TDBTime(interval[0]) ).toString(TIMFMT);
                  endstr = ( new TDBTime(interval[1]) ).toString(TIMFMT);

                  System.out.format( "Interval %d%n", i );
                  System.out.format( "   Start time: %s %n",   begstr );
                  System.out.format( "   Stop time:  %s %n",   endstr );
               }
            }
         }
         catch ( SpiceException exc ) {
            exc.printStackTrace();
         }
      }
   }

</pre>

<p>    When this program was executed on a PC/Linux/gcc/64-bit/Java 1.5 
   platform, the output was:   

<pre>
Interval 0
   Start time: 2001 DEC 14 20:10:14.196039 (TDB)
   Stop time:  2001 DEC 14 21:35:50.317886 (TDB)

</pre>
</li>




<li>
      Find occultations of Titan by Saturn or of Saturn by
      Titan as seen from the center of the Earth over the
      last four months of 2008. Model both target bodies as
      ellipsoids. Search for every type of occultation.

      <p>
      Use light time corrections to model apparent positions of
      Saturn and Titan. Stellar aberration corrections are not
      specified because they don't affect occultation computations.

      <p>
      We select a step size of 15 minutes, which means we
      ignore occultation events lasting less than 15 minutes,
      if any exist.


<p>      Use the meta-kernel shown below to load the required SPICE
      kernels.

<pre>
KPL/MK

File name: GFOccultationSearchEx2.tm

This meta-kernel is intended to support operation of SPICE
example programs. The kernels shown here should not be
assumed to contain adequate or correct versions of data
required by SPICE-based user applications.

In order for an application to use this meta-kernel, the
kernels referenced here must be present in the user's
current working directory.

The names and contents of the kernels referenced
by this meta-kernel are as follows:

   File name                     Contents
   ---------                     --------
   de430.bsp                     Planetary ephemeris
   sat375.bsp                    Satellite ephemeris for
                                 Saturn
   pck00010.tpc                  Planet orientation and
                                 radii
   naif0012.tls                  Leapseconds

\begindata

   KERNELS_TO_LOAD = ( 'de430.bsp',
                       'sat375.bsp',
                       'pck00010.tpc',
                       'naif0012.tls'  )

\begintext

End of meta-kernel


</pre>


<p> Example code begins here.

<pre>

//
// Program GFOccultationSearchEx2
//

import spice.basic.*;

//
//  Find occultations of Titan by Saturn or of Saturn by
//  Titan as seen from the center of the Earth over the
//  last four months of 2008. Model both target bodies as
//  ellipsoids. Search for every type of occultation.
//
public class GFOccultationSearchEx2
{
   //
   // Load SPICE shared library.
   //
   static{ System.loadLibrary( "JNISpice" ); }


   public static void main( String[] args )

      throws SpiceException
   {
      //
      // Local constants
      //
      final String                      META   = "GFOccultationSearchEx2.tm";
      final String                      TIMFMT = 
                                      "YYYY MON DD HR:MN:SC.###### (TDB)::TDB";

      final int                         MAXWIN = 200;
      final int                         NTYPES = 4;
      
      //
      // Local variables
      //
      AberrationCorrection              abcorr = 
                                           new AberrationCorrection( "CN" );

      Body                              back;
      Body                              front;
      Body                              obsrvr;
      
      GFOccultationSearch               occ;

      ReferenceFrame                    bframe;
      ReferenceFrame                    fframe;

      SpiceWindow                       cnfine;
      SpiceWindow                       result;

      String                            bshape = "ELLIPSOID";
      String                            fshape = "EllIPSOID";
      String                            line;

      String[]                          occtyp = 
                                        { 
                                           "FULL",
                                           "ANNULAR",
                                           "PARTIAL",
                                           "ANY"
                                        };

      String                            templt = 
                                        "Condition: %s occultation of %s by %s";
                                       
      String                            title;
      String                            wnstr0 = "2008 SEP 01 00:00:00 TDB";
      String                            wnstr1 = "2009 JAN 01 00:00:00 TDB";

      TDBTime                           et0;
      TDBTime                           et1;
      TDBTime                           finish;
      TDBTime                           start;

      double[]                          intval;
      double                            step;

      int                               i;
      int                               j;
      int                               k;


      try
      {
         //
         // Load kernels.
         //
         KernelDatabase.load( META );

         //
         // Obtain the TDB time bounds of the confinement
         // window, which is a single interval in this case.
         //
         et0 = new TDBTime( wnstr0 );
         et1 = new TDBTime( wnstr1 );

         //
         // Insert the time bounds into the confinement
         // window.
         //
         cnfine = new SpiceWindow();

         cnfine.insert ( et0.getTDBSeconds(),
                         et1.getTDBSeconds() );
         //
         // Select a 15-minute step. We'll ignore any occultations
         // lasting less than 15 minutes. Units are TDB seconds.
         //
         step = 900.0;

         //
         // The observer is the Earth.
         //
         obsrvr = new Body("Earth" );

         //
         // Loop over the occultation types.
         //
         for ( i = 0;  i < NTYPES;  i++ )
         {
            //
            // For each type, do a search for both transits of
            // Titan across Saturn and occultations of Titan by
            // Saturn.
            //
            for ( j = 0;  j < 2;  j++ )
            {
               if ( j == 0 )
               {
                  front  = new Body( "TITAN" );
                  fframe = new ReferenceFrame( "IAU_TITAN" );
                  back   = new Body( "SATURN" );
                  bframe = new ReferenceFrame( "IAU_SATURN" );
               }
               else
               {
                  front  = new Body( "SATURN" );
                  fframe = new ReferenceFrame( "IAU_SATURN" );
                  back   = new Body( "TITAN" );
                  bframe = new ReferenceFrame( "IAU_TITAN" );
               }

               //
               // Perform the search. The target body shapes
               // are modeled as ellipsoids.
               //      
               occ = new GFOccultationSearch ( occtyp[i],
                                               front,  fshape, fframe,
                                               back,   bshape, bframe,
                                               abcorr, obsrvr          );

               result = occ.run( cnfine, step, MAXWIN );

               //
               // Display the results. 
               //
               System.out.format ( "%n" );

               //
               // Substitute the occultation type and target
               // body names into the title string:
               //
               title = String.format( templt, occtyp[i], back, front );


               System.out.format ( "%s%n", title );



               if ( result.card() == 0 )
               {
                  System.out.format ( " Result window is empty: " +
                           "no occultation was found.%n" );
               }
               else
               {
                  System.out.format ( " Result window start, stop times:%n" );

                  for ( k = 0;  k < result.card();  k++ )
                  { 
                     //
                     // Fetch the endpoints of the kth interval
                     // of the result window.
                     //
                     intval = result.getInterval( k );

                     start  = new TDBTime( intval[0] );
                     finish = new TDBTime( intval[1] );

                     line = String.format( "  %s  %s",
                                           start.toString ( TIMFMT ),
                                           finish.toString( TIMFMT )  );

                     System.out.format ( "%s%n", line );
                  }
               }
               //
               // We've finished displaying the results of the
               // current search.
               //
            }
            //
            // We've finished displaying the results of the
            // searches using the current occultation type.
            //
         }
         System.out.format ( "%n" );

      } // End of try block

      catch ( SpiceException exc )
      {
         exc.printStackTrace();
      }

   } // End of main method 
   
}

</pre>


<p>    When this program was executed on a PC/Linux/gcc/64-bit/Java 1.5 
   platform, the output was:   

<pre>
Condition: FULL occultation of SATURN by TITAN
 Result window is empty: no occultation was found.

Condition: FULL occultation of TITAN by SATURN
 Result window start, stop times:
  2008 OCT 27 22:08:01.492916 (TDB)  2008 OCT 28 01:05:03.501021 (TDB)
  2008 NOV 12 21:21:59.177643 (TDB)  2008 NOV 13 02:06:05.110052 (TDB)
  2008 NOV 28 20:49:02.341360 (TDB)  2008 NOV 29 02:13:59.028494 (TDB)
  2008 DEC 14 20:05:09.190392 (TDB)  2008 DEC 15 01:44:53.560149 (TDB)
  2008 DEC 30 19:00:56.519842 (TDB)  2008 DEC 31 00:42:43.260245 (TDB)

Condition: ANNULAR occultation of SATURN by TITAN
 Result window start, stop times:
  2008 OCT 19 21:29:20.325555 (TDB)  2008 OCT 19 22:53:34.783881 (TDB)
  2008 NOV 04 20:15:38.545175 (TDB)  2008 NOV 05 00:18:59.201235 (TDB)
  2008 NOV 20 19:38:59.595157 (TDB)  2008 NOV 21 00:35:26.762136 (TDB)
  2008 DEC 06 18:58:34.023280 (TDB)  2008 DEC 07 00:16:17.675855 (TDB)
  2008 DEC 22 18:02:46.240554 (TDB)  2008 DEC 22 23:26:52.741217 (TDB)

Condition: ANNULAR occultation of TITAN by SATURN
 Result window is empty: no occultation was found.

Condition: PARTIAL occultation of SATURN by TITAN
 Result window start, stop times:
  2008 OCT 19 20:44:30.568511 (TDB)  2008 OCT 19 21:29:20.325555 (TDB)
  2008 OCT 19 22:53:34.783881 (TDB)  2008 OCT 19 23:38:25.997394 (TDB)
  2008 NOV 04 19:54:40.438594 (TDB)  2008 NOV 04 20:15:38.545175 (TDB)
  2008 NOV 05 00:18:59.201235 (TDB)  2008 NOV 05 00:39:58.496878 (TDB)
  2008 NOV 20 19:21:46.750804 (TDB)  2008 NOV 20 19:38:59.595157 (TDB)
  2008 NOV 21 00:35:26.762136 (TDB)  2008 NOV 21 00:52:40.524532 (TDB)
  2008 DEC 06 18:42:36.142641 (TDB)  2008 DEC 06 18:58:34.023280 (TDB)
  2008 DEC 07 00:16:17.675855 (TDB)  2008 DEC 07 00:32:16.258839 (TDB)
  2008 DEC 22 17:47:10.814494 (TDB)  2008 DEC 22 18:02:46.240554 (TDB)
  2008 DEC 22 23:26:52.741217 (TDB)  2008 DEC 22 23:42:28.791732 (TDB)

Condition: PARTIAL occultation of TITAN by SATURN
 Result window start, stop times:
  2008 OCT 27 21:37:17.142039 (TDB)  2008 OCT 27 22:08:01.492916 (TDB)
  2008 OCT 28 01:05:03.501021 (TDB)  2008 OCT 28 01:35:49.084061 (TDB)
  2008 NOV 12 21:01:47.183796 (TDB)  2008 NOV 12 21:21:59.177643 (TDB)
  2008 NOV 13 02:06:05.110052 (TDB)  2008 NOV 13 02:26:18.129355 (TDB)
  2008 NOV 28 20:31:28.570159 (TDB)  2008 NOV 28 20:49:02.341360 (TDB)
  2008 NOV 29 02:13:59.028494 (TDB)  2008 NOV 29 02:31:33.622968 (TDB)
  2008 DEC 14 19:48:27.132486 (TDB)  2008 DEC 14 20:05:09.190392 (TDB)
  2008 DEC 15 01:44:53.560149 (TDB)  2008 DEC 15 02:01:36.301757 (TDB)
  2008 DEC 30 18:44:23.520266 (TDB)  2008 DEC 30 19:00:56.519842 (TDB)
  2008 DEC 31 00:42:43.260245 (TDB)  2008 DEC 31 00:59:16.974818 (TDB)

Condition: ANY occultation of SATURN by TITAN
 Result window start, stop times:
  2008 OCT 19 20:44:30.568511 (TDB)  2008 OCT 19 23:38:25.997394 (TDB)
  2008 NOV 04 19:54:40.438594 (TDB)  2008 NOV 05 00:39:58.496878 (TDB)
  2008 NOV 20 19:21:46.750804 (TDB)  2008 NOV 21 00:52:40.524532 (TDB)
  2008 DEC 06 18:42:36.142641 (TDB)  2008 DEC 07 00:32:16.258839 (TDB)
  2008 DEC 22 17:47:10.814494 (TDB)  2008 DEC 22 23:42:28.791732 (TDB)

Condition: ANY occultation of TITAN by SATURN
 Result window start, stop times:
  2008 OCT 27 21:37:17.142039 (TDB)  2008 OCT 28 01:35:49.084061 (TDB)
  2008 NOV 12 21:01:47.183796 (TDB)  2008 NOV 13 02:26:18.129355 (TDB)
  2008 NOV 28 20:31:28.570159 (TDB)  2008 NOV 29 02:31:33.622968 (TDB)
  2008 DEC 14 19:48:27.132486 (TDB)  2008 DEC 15 02:01:36.301757 (TDB)
  2008 DEC 30 18:44:23.520266 (TDB)  2008 DEC 31 00:59:16.974818 (TDB)

</pre>


</li>


<li>  
 Find occultations of the Mars Reconaissance Orbiter (MRO)
         by Mars, or transits of the MRO spacecraft across Mars,
         as seen from the DSN station DSS-14 over a period of a
         few hours on FEB 28 2015.
<p> 
         Use both ellipsoid and DSK shape models for Mars.
<p>
         Use light time corrections to model apparent positions of Mars
         and MRO. Stellar aberration corrections are not specified
         because they don't affect occultation computations.
<p> 
         We select a step size of 3 minutes, which means we ignore
         occultation events lasting less than 3 minutes, if any exist.
 
<p>      Use the meta-kernel shown below to load the required SPICE
      kernels.
<pre>
KPL/MK

File: GFOccultationSearchEx3.tm

This meta-kernel is intended to support operation of SPICE
example programs. The kernels shown here should not be
assumed to contain adequate or correct versions of data
required by SPICE-based user applications.

In order for an application to use this meta-kernel, the
kernels referenced here must be present in the user's
current working directory.

The names and contents of the kernels referenced
by this meta-kernel are as follows:

   File name                        Contents
   ---------                        --------
   de410.bsp                        Planetary ephemeris
   mar063.bsp                       Mars satellite ephemeris
   pck00010.tpc                     Planet orientation and
                                    radii
   naif0012.tls                     Leapseconds
   earthstns_itrf93_050714.bsp      DSN station ephemeris
   earth_latest_high_prec.bpc       Earth orientation
   mro_psp34.bsp                    MRO ephemeris
   megr90n000cb_plate.bds           Plate model based on
                                    MEGDR DEM, resolution
                                    4 pixels/degree.

\begindata

   PATH_SYMBOLS    = ( 'MRO', 'GEN' )

   PATH_VALUES     = (
                       '/ftp/pub/naif/pds/data+'
                       '/mro-m-spice-6-v1.0/+'
                       'mrosp_1000/data/spk',
                       '/ftp/pub/naif/generic_kernels'
                     )

   KERNELS_TO_LOAD = ( '$MRO/de410.bsp',
                       '$MRO/mar063.bsp',
                       '$MRO/mro_psp34.bsp',
                       '$GEN/spk/stations/+'
                       'earthstns_itrf93_050714.bsp',
                       '$GEN/pck/earth_latest_high_prec.bpc',
                       'pck00010.tpc',
                       'naif0012.tls',
                       'megr90n000cb_plate.bds'
                     )
\begintext


</pre>


<p> Example code begins here.

<pre>

//
// Program GFOccultationSearchEx3
//

import spice.basic.*;

//
//  Find occultations of Titan by Saturn or of Saturn by
//  Titan as seen from the center of the Earth over the
//  last four months of 2008. Model both target bodies as
//  ellipsoids. Search for every type of occultation.
//
public class GFOccultationSearchEx3
{
   //
   // Load SPICE shared library.
   //
   static{ System.loadLibrary( "JNISpice" ); }


   public static void main( String[] args )

      throws SpiceException
   {
      //
      // Local constants
      //
      final String                      META   = "GFOccultationSearchEx3.tm";
      final String                      TIMFMT = 
                                        "YYYY MON DD HR:MN:SC" +
                                        ".###### (TDB)::TDB";

      final int                         MAXWIN = 200;
      final int                         NTYPES = 4;
      
      //
      // Local variables
      //
      AberrationCorrection              abcorr = 
                                           new AberrationCorrection( "CN" );

      Body                              back;
      Body                              front;
      Body                              obsrvr = new Body( "DSS-14" );
      
      GFOccultationSearch               occ;

      ReferenceFrame                    bframe;
      ReferenceFrame                    fframe;

      SpiceWindow                       cnfine;
      SpiceWindow                       result;

      String                            bshape = "ELLIPSOID";
      String                            fshape = "EllIPSOID";
      String                            line;

      String                            occtyp = "ANY";

      String                            templt = 
                                        "Condition: %s occultation of %s by %s";
                                       
      String                            title;
      String                            wnstr0 = "2015 FEB 28 07:00:00 TDB";
      String                            wnstr1 = "2015 FEB 28 12:00:00 TDB";

      TDBTime                           et0;
      TDBTime                           et1;
      TDBTime                           finish;
      TDBTime                           start;

      double[]                          intval;
      double                            step;

      int                               i;
      int                               j;
      int                               k;


      try
      {
         //
         // Load kernels.
         //
         KernelDatabase.load( META );

         //
         // Obtain the TDB time bounds of the confinement
         // window, which is a single interval in this case.
         //
         et0 = new TDBTime( wnstr0 );
         et1 = new TDBTime( wnstr1 );

         //
         // Insert the time bounds into the confinement
         // window.
         //
         cnfine = new SpiceWindow();

         cnfine.insert ( et0.getTDBSeconds(),
                         et1.getTDBSeconds() );
         //
         // Select a 3-minute step. We'll ignore any occultations
         // lasting less than 3 minutes. Units are TDB seconds.
         //
         step = 180.0;

         //
         // Perform both spacecraft occultation and spacecraft
         // transit searches.
         //

         //
         // Loop over the occultation types.
         //
         for ( i = 0;  i < 2;  i++ )
         {
            if ( i == 0 )
            {
               //
               // Perform a spacecraft occultation search.
               //
               front  = new Body( "MARS" );
               fframe = new ReferenceFrame( "IAU_MARS" );
               back   = new Body( "MRO" );
               bshape = "POINT";

               //
               // For point targets, the frame is not used. We
               // can provide the name of an arbitrary built-in
               // frame.
               //
               bframe = new ReferenceFrame( "J2000" );
            }
            else
            {
               //
               // Perform a spacecraft transit search.
               //
               front  = new Body( "MRO" );
               //
               // For point targets, the frame is not used. We
               // can provide the name of an arbitrary built-in
               // frame.
               //
               fshape = "POINT";

               fframe = new ReferenceFrame( "J2000" );
               back   = new Body( "MARS" );
               bframe = new ReferenceFrame( "IAU_MARS" );
            }

            for ( j = 0;  j < 2;  j++ )
            {
               if ( j == 0 )
               {
                  //
                  // Model the planet shape as an ellipsoid.
                  //
                  if ( i == 0 )
                  {
                     fshape = "ELLIPSOID";
                  }
                  else
                  {
                     bshape = "ELLIPSOID";
                  }
               }
               else
               {
                  //
                  // Model the planet shape using DSK data.
                  //
                  if ( i == 0 )
                  {
                     fshape = "DSK/UNPRIORITIZED";
                  }
                  else
                  {
                     bshape = "DSK/UNPRIORITIZED";
                  }
               }
 
               //
               // Perform the spacecraft occultation or
               // transit search.
               //
               System.out.format ( "%n" );

               if ( i == 0 )
               {
                  System.out.format ( "Using shape model %s%n" +
                                      "Starting occultation search...%n", 
                                      fshape                             );
               }
               else
               {
                  System.out.format ( "Using shape model %s%n" +
                                      "Starting transit search...%n", 
                                      bshape                             );
               }

               //
               // Perform the search.
               //
               occ = new GFOccultationSearch ( occtyp,
                                               front,  fshape, fframe,
                                               back,   bshape, bframe,
                                               abcorr, obsrvr          );

               result = occ.run( cnfine, step, MAXWIN );
           
 
               if ( result.card() == 0 )
               {
                  System.out.format ( " No event was found.%n" );
               }
               else
               {
                  for ( k = 0;  k < result.card();  k++ )
                  { 
                     //
                     // Fetch the endpoints of the kth interval
                     // of the result window.
                     //
                     intval = result.getInterval( k );

                     start  = new TDBTime( intval[0] );
                     finish = new TDBTime( intval[1] );
                     
                     System.out.format( "Interval %d%n"       +
                                        "   Start time: %s%n" + 
                                        "   Stop time:  %s%n",
                                        k,
                                        start.toString ( TIMFMT ),
                                        finish.toString( TIMFMT )  );
                  }
               }

           }
           //
           // End of target shape loop.
           //
        }
        //
        // End of occultation vs transit loop.
        //

        System.out.format ( "%n" );
 
      } // End of try block

      catch ( SpiceException exc )
      {
         exc.printStackTrace();
      }

   } // End of main method 
   
}

</pre>


<p>    When this program was executed on a PC/Linux/gcc/64-bit/Java 1.5 
   platform, the output was:   

<pre>

Using shape model ELLIPSOID
Starting occultation search...
Interval 0
   Start time: 2015 FEB 28 07:17:35.379879 (TDB)
   Stop time:  2015 FEB 28 07:50:37.710284 (TDB)
Interval 1
   Start time: 2015 FEB 28 09:09:46.920140 (TDB)
   Stop time:  2015 FEB 28 09:42:50.497193 (TDB)
Interval 2
   Start time: 2015 FEB 28 11:01:57.845730 (TDB)
   Stop time:  2015 FEB 28 11:35:01.489716 (TDB)

Using shape model DSK/UNPRIORITIZED
Starting occultation search...
Interval 0
   Start time: 2015 FEB 28 07:17:38.130608 (TDB)
   Stop time:  2015 FEB 28 07:50:38.310802 (TDB)
Interval 1
   Start time: 2015 FEB 28 09:09:50.314903 (TDB)
   Stop time:  2015 FEB 28 09:42:55.369626 (TDB)
Interval 2
   Start time: 2015 FEB 28 11:02:01.756296 (TDB)
   Stop time:  2015 FEB 28 11:35:08.368384 (TDB)

Using shape model ELLIPSOID
Starting transit search...
Interval 0
   Start time: 2015 FEB 28 08:12:21.112018 (TDB)
   Stop time:  2015 FEB 28 08:45:48.401746 (TDB)
Interval 1
   Start time: 2015 FEB 28 10:04:32.682324 (TDB)
   Stop time:  2015 FEB 28 10:37:59.920302 (TDB)
Interval 2
   Start time: 2015 FEB 28 11:56:39.757564 (TDB)
   Stop time:  2015 FEB 28 12:00:00.000000 (TDB)

Using shape model DSK/UNPRIORITIZED
Starting transit search...
Interval 0
   Start time: 2015 FEB 28 08:12:15.750020 (TDB)
   Stop time:  2015 FEB 28 08:45:43.406870 (TDB)
Interval 1
   Start time: 2015 FEB 28 10:04:29.031706 (TDB)
   Stop time:  2015 FEB 28 10:37:55.565509 (TDB)
Interval 2
   Start time: 2015 FEB 28 11:56:34.634642 (TDB)
   Stop time:  2015 FEB 28 12:00:00.000000 (TDB)

</pre>
</li>

</ol>


<h3> Version 2.0.0 28-NOV-2016 (NJB)</h3>

   Updated to support DSK-based surface models.

   <p> Occultation type parameters, target  
   shape parameters, and convergence parameters
   were moved to the class GF.

<h3> Version 1.0.0 29-DEC-2009 (NJB)</h3>
*/
public class GFOccultationSearch extends GFBinaryStateSearch
{

   //
   // Fields
   //
   private String                    occtype;
   private Body                      front;
   private String                    fshape;
   private ReferenceFrame            fframe;
   private Body                      back;
   private String                    bshape;
   private ReferenceFrame            bframe;
   private AberrationCorrection      abcorr;
   private Body                      observer;


   //
   // Constructors
   //

   /**
   Create a GFOccultationSearch instance.

   <p>GFOccultationSearch instances represent geometric
   conditions

   <h3>Brief I/O</h3>
<pre>
   VARIABLE        I/O  DESCRIPTION
   --------------- ---  -------------------------------------------------
   occtyp           I   Type of occultation.
   front            I   Name of body occulting the other.
   fshape           I   Type of shape model used for front body.
   fframe           I   Body-fixed, body-centered frame for front body.
   back             I   Name of body occulted by the other.
   bshape           I   Type of shape model used for back body.
   bframe           I   Body-fixed, body-centered frame for back body.
   abcorr           I   Aberration correction flag.
   obsrvr           I   Name of the observing body.
</h3>
   */
   public GFOccultationSearch ( String                    occtype,
                                Body                      front,
                                String                    fshape,
                                ReferenceFrame            fframe,
                                Body                      back,
                                String                    bshape,
                                ReferenceFrame            bframe,
                                AberrationCorrection      abcorr,
                                Body                      observer )
   {
      //
      // Just save the inputs. It would be nice to perform
      // error checking at this point; the only practical way
      // to do that is call the ZZGFOCU initialization entry point.
      //
      // Maybe later.
      //
      this.occtype           = occtype;
      this.front             = front;
      this.fshape            = fshape;
      this.fframe            = fframe;
      this.back              = back;
      this.bshape            = bshape;
      this.bframe            = bframe;
      this.abcorr            = abcorr;
      this.observer          = observer;
   }


   /**
   Run a search over a specified confinement window, using
   a specified step size (units are TDB seconds).
   */
   public SpiceWindow run ( SpiceWindow   confinementWindow,
                            double        step,
                            int           maxResultIntervals )

      throws SpiceException
   {

      double[] resultArray = CSPICE.gfoclt ( occtype,
                                             front.getName(),
                                             fshape,
                                             fframe.getName(),
                                             back.getName(),
                                             bshape,
                                             bframe.getName(),
                                             abcorr.getName(),
                                             observer.getName(),
                                             step,
                                             maxResultIntervals,
                                             confinementWindow.toArray() );

      return (  new SpiceWindow( resultArray )  );
   }



   /**
   Run a search over a specified confinement window, using
   custom search step and refinement utilities.
   */
   public SpiceWindow run ( SpiceWindow     confinementWindow,
                            GFSearchUtils   utils,
                            int             maxResultIntervals )

      throws SpiceException
   {

      double[] resultArray = CSPICE.gfocce ( occtype,
                                             front.getName(),
                                             fshape,
                                             fframe.getName(),
                                             back.getName(),
                                             bshape,
                                             bframe.getName(),
                                             abcorr.getName(),
                                             observer.getName(),
                                             maxResultIntervals,
                                             confinementWindow.toArray(),
                                             utils                         );

      return (  new SpiceWindow( resultArray )  );
   }
}

