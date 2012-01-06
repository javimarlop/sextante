package es.unex.sextante.geotools.examples;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.gce.arcgrid.ArcGridReader;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.geotools.GTRasterLayer;
import es.unex.sextante.geotools.GTVectorLayer;
import es.unex.sextante.gridTools.clipGrid.ClipGridAlgorithm;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

public class GridCropExample {

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


      final GridCoverage2D gc = openRasterLayer("c:/dem.asc");
      final GTRasterLayer raster = new GTRasterLayer();
      raster.create(gc);

      final FeatureSource fs = openShapefile("c:/poly.shp");
      final GTVectorLayer vector = new GTVectorLayer();
      vector.create(fs);


      final ClipGridAlgorithm alg = new ClipGridAlgorithm();
      final ParametersSet params = alg.getParameters();
      params.getParameter(ClipGridAlgorithm.INPUT).setParameterValue(raster);
      params.getParameter(ClipGridAlgorithm.POLYGONS).setParameterValue(vector);

      final OutputObjectsSet outputs = alg.getOutputObjects();

      final Output twi = outputs.getOutput(ClipGridAlgorithm.RESULT);
      twi.setOutputChannel(new FileOutputChannel("c:/clipped.tif"));

      alg.execute(null, m_OutputFactory);

   }


   private static GridCoverage2D openRasterLayer(final String sFilename) throws IOException {

      final URL url = new File(sFilename).toURL();
      final ArcGridReader agr = new ArcGridReader(url);
      final GridCoverage2D coverage = agr.read(null);

      return coverage;

   }


   private static FeatureSource openShapefile(final String sFile) throws IOException {

      final FileDataStore store = FileDataStoreFinder.getDataStore(new File(sFile));
      final FeatureSource featureSource = store.getFeatureSource();

      return featureSource;

   }

}
