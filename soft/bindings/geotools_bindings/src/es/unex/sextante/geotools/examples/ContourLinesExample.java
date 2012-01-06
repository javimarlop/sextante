package es.unex.sextante.geotools.examples;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.geotools.GTRasterLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.vectorize.contourLines.ContourLinesAlgorithm;

public class ContourLinesExample {

   /*
    * The output factory to use when calling geoalgorithms
    * This tells the algorithm how to create new data objects (layers
    * and tables)
    * The GTOutputFactory creates objects based on geotools
    * data objects (DataStore and GridCoverage)
    */
   private static OutputFactory m_OutputFactory = new GTOutputFactory();


   public static void main(final String[] args) {

      initialize();

      try {
         doProcessing();
         System.exit(0);
      }
      catch (final GeoAlgorithmExecutionException e) {
         System.out.println("Error executing algorithm");
         System.exit(1);
      }
      catch (final IOException e) {
         System.out.println("Error opening raster layer");
         System.exit(1);
      }

   }


   private static void initialize() {

      /*
       * Initialize the library.
       * This will load all the algorithms and resource strings.
       * Since no language code is passed, default language(en)
       * will be used
       */
      Sextante.initialize();

   }


   private static void doProcessing() throws GeoAlgorithmExecutionException, IOException {
      /*
       * First we need some data.
       * Let's open a DEM
       */
      final GridCoverage2D gc = openRasterLayer("d:\\gisdata\\dem.asc");
      /*
       * To use this data we need to wrap it with an object
       * that implements the IRasterLayer, so SEXTANTE algorithms
       * can access it.
       * Since it is a Geotools object, we will use the Geotools
       * wrapper class GTRasterLayer
       */
      final GTRasterLayer layer = new GTRasterLayer();
      layer.create(gc);

      /*
       * Now we will calculate contour lines from that DEM
       */
      final ContourLinesAlgorithm alg = new ContourLinesAlgorithm();
      final ParametersSet params = alg.getParameters();
      params.getParameter(ContourLinesAlgorithm.LAYER).setParameterValue(layer);
      params.getParameter(ContourLinesAlgorithm.DISTANCE).setParameterValue(new Double(100.));
      params.getParameter(ContourLinesAlgorithm.MIN).setParameterValue(new Double(0.));
      params.getParameter(ContourLinesAlgorithm.MAX).setParameterValue(new Double(10000.));

      /*
       *  This algorithm will generate a new vector layer.
       * We can select "where" to put the result. To do this, we
       * retrieve the output container and set the output channel,
       * which contains information about the destiny of the resulting
       * data. The most common way of using this is setting
       * a FileOutputChannel, which contains the information needed to
       * put the output to a file (basically a filename).
       * If we omit this, a FileOutputChannel will be used,
       * using a temporary filename.
       */
      final OutputObjectsSet outputs = alg.getOutputObjects();
      final Output contours = outputs.getOutput(ContourLinesAlgorithm.RESULT);
      contours.setOutputChannel(new FileOutputChannel("d:\\gisdata\\contours2.shp"));

      /*
       * Execute the algorithm. We use no task monitor,
       * so we will not be able to monitor the progress
       * of the execution. SEXTANTE also provides a DefaultTaskMonitor,
       * which shows a simple progress bar, or you could make your
       * own one, implementing the ITaskMonitor interface
       *
       * The execute method returns true if everything went OK, or false if it
       * was canceled. Since we are not giving the user the chance to
       * cancel it (there is no task monitor), we do not care about the
       * return value.
       *
       * If something goes wrong, it will throw an exception.
       */
      alg.execute(null, m_OutputFactory);

      /*
       * Now the result can be taken from the output container
       */
      final IVectorLayer result = (IVectorLayer) contours.getOutputObject();

      /*
       * And now we can do with it whatever we want.
       * Let's print some information to see that the layer has
       * been correctly created.
       * Before accesing the layer, we have to call the open() method;
       */
      result.open();
      System.out.println("This layer contains " + Integer.toString(result.getShapesCount()) + " lines.");

   }


   private static GridCoverage2D openRasterLayer(final String sFilename) throws IOException {

      final URL url = new File(sFilename).toURL();
      final ArcGridReader agr = new ArcGridReader(url);
      final GridCoverage2D coverage = agr.read(null);

      return coverage;

   }

}
