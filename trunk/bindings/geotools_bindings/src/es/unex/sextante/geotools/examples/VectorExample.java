package es.unex.sextante.geotools.examples;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.geotools.GTVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.vectorTools.linesToEquispacedPoints.LinesToEquispacedPointsAlgorithm;

public class VectorExample {

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
         System.out.println("Error opening shapefile");
         System.exit(1);
      }

   }


   private static void initialize() {

      /*
       * Initialize the library.
       * This will load resource strings.
       * Since no language code is passed, default language(en)
       * will be used
       */
      Sextante.initialize();

   }


   private static void doProcessing() throws GeoAlgorithmExecutionException, IOException {

      /*
       * First we need some data.
       * Let's open a shapefile with some lines
       */
      final FeatureSource fs = openShapefile("../lines.shp");

      /*
       * To use this data we need to wrap it with an object
       * that implements the IVectorLayer, so SEXTANTE algorithms
       * can access it.
       * Since it is a Geotools object, we will use the Geotools
       * wrapper class GTVectorLayer
       */
      final GTVectorLayer layer = new GTVectorLayer();
      layer.create(fs);

      /*
       * And now, let's do some processing with that layer.
       * We will convert those lines to equispaced points.
       * Instead of calling the SEXTANTE algorithm, we will use
       * a GeoTools process that wraps this algorithm, to
       * demonstrate its usage
       */
      final LinesToEquispacedPointsAlgorithm alg = new LinesToEquispacedPointsAlgorithm();

      /*
       * The first thing we have to do is to set up the input parameters
       */
      final ParametersSet params = alg.getParameters();
      params.getParameter(LinesToEquispacedPointsAlgorithm.LINES).setParameterValue(layer);
      params.getParameter(LinesToEquispacedPointsAlgorithm.DISTANCE).setParameterValue(new Double(5000));

      /* This algorithm will generate a new vector layer.
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
      final Output out = outputs.getOutput(LinesToEquispacedPointsAlgorithm.RESULT);
      out.setOutputChannel(new FileOutputChannel("/home/volaya/points.shp"));

      /*
       * Execute the algorithm. We use no task monitor,
       * so we will not be able to monitor the progress
       * of the execution. SEXTANTE provides a DefaultTaskMonitor,
       * which shows a simple progress bar, or you could make your
       * own one, implementing the ITaskMonitor interface
       */
      alg.execute(null, m_OutputFactory);

      /*
       * Now the result can be taken from the output container
       */
      final IVectorLayer result = (IVectorLayer) out.getOutputObject();

      /*
       * And now we can do with it whatever we want.
       * Let's print some information to see that the layer has
       * been correctly created.
       * Before accesing the layer, we have to call the open() method;
	  */
      result.open();
      System.out.println("This layer contains " + Integer.toString(result.getShapesCount()) + " points.");

   }


   private static FeatureSource openShapefile(final String sFile) throws IOException {

      final FileDataStore store = FileDataStoreFinder.getDataStore(new File(sFile));
      final FeatureSource featureSource = store.getFeatureSource();

      return featureSource;

   }

}
