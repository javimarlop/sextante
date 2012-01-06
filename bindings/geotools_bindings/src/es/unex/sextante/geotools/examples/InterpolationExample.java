package es.unex.sextante.geotools.examples;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.geotools.GTVectorLayer;
import es.unex.sextante.gridTools.closeGaps.CloseGapsAlgorithm;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.rasterize.rasterizeVectorLayer.RasterizeVectorLayerAlgorithm;

public class InterpolationExample {

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
       * This will load all the algorithms and resource strings.
       * Since no language code is passed, default language(en)
       * will be used
       */
      Sextante.initialize();

   }


   private static void doProcessing() throws GeoAlgorithmExecutionException, IOException {

      /*
       * Let's open a shapefile with contour lines
       */
      final FeatureSource fs = openShapefile("/home/my_user_name/lines.shp");

      /*
       * To use this data we need to wrap it with an object
       * that implements the IVectorLayer, so SEXTANTE algorithms
       * can access it.
       * Since it is a Geotools object, we will use the Geotools
       * wrapper class ShpLayer
       */
      final GTVectorLayer layer = new GTVectorLayer();
      layer.create(fs);

      /*
       * Now we will rasterize that layer and get a new raster
       * layer with values only in those cells which fall under
       * the contour lines. The remaining cells will have a nodata
       * value
       *
       */
      final RasterizeVectorLayerAlgorithm alg = new RasterizeVectorLayerAlgorithm();
      ParametersSet params = alg.getParameters();
      params.getParameter("LAYER").setParameterValue(layer);
      params.getParameter("FIELD").setParameterValue(layer.getFieldIndexByName("Elevation"));

      /*
       * We have to define the characteristics of the resulting
       * raster layer (extent and cellsize)
       * When executing an algorithm such as slope calculation from
       * a DEM, this is not necessary, since the algorithm can take
       * that information from the input layers (i.e. it will create
       * a slope layer with the same characteristics as the input DEM)
       * In this case however, this is not possible, so a grid extent
       * has to be defined explicitly
       */

      /*
       * This will create a grid extent that has the full extent of our
       * input layer
       */
      final AnalysisExtent extent = new AnalysisExtent(layer);

      /*
       * And we will use a cell size of 25 meters
       */
      extent.setCellSize(25.);

      /*
       * And now we set the extent as the one to use to create new raster
       * layers within the rasterizing algorithm.
       */
      alg.setAnalysisExtent(extent);

      /*
       *  This algorithm will generate a new raster layer.
       * We can select "where" to put the result. To do this, we
       * retrieve the output container and set the output channel,
       * which contains information about the destiny of the resulting
       * data. The most common way of using this is setting
       * a FileOutputChannel, which contains the information needed to
       * put the output to a file (basically a filename).
       * If we omit this, a FileOutputChannel will be used,
       * using a temporary filename.
       */
      OutputObjectsSet outputs = alg.getOutputObjects();
      final Output rasterizedLayer = outputs.getOutput("RESULT");
      rasterizedLayer.setOutputChannel(new FileOutputChannel("/home/my_user_name/rasterized.tif"));

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
       * Now, to fill all those no-data cells in the resulting layer
       * with valid values, we will run a void filling algorithm
       */
      final CloseGapsAlgorithm cgaps = new CloseGapsAlgorithm();

      params = cgaps.getParameters();
      /*
       * The input for this algorithm is the output of the previous one
       */
      params.getParameter("INPUT").setParameterValue(rasterizedLayer.getOutputObject());

      outputs = cgaps.getOutputObjects();
      final Output filledLayer = outputs.getOutput("RESULT");
      filledLayer.setOutputChannel(new FileOutputChannel("/home/my_user_name/filled.tif"));

      alg.execute(null, m_OutputFactory);


   }


   private static FeatureSource openShapefile(final String sFile) throws IOException {

      final FileDataStore store = FileDataStoreFinder.getDataStore(new File(sFile));
      final FeatureSource featureSource = store.getFeatureSource();

      return featureSource;

   }
}
