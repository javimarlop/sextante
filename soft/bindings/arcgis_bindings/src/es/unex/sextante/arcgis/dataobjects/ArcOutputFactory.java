

package es.unex.sextante.arcgis.dataobjects;

import java.io.File;

import javax.swing.JDialog;

import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.UnknownCoordinateSystem;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.UnsupportedOutputChannelException;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class ArcOutputFactory
         extends
            OutputFactory {

   @Override
   public Object getDefaultCRS() {

      try {
         return new UnknownCoordinateSystem();
      }
      catch (final Exception e) {
         return null;
      }

   }


   @Override
   public IRasterLayer getNewRasterLayer(final String name,
                                         final int dataType,
                                         final AnalysisExtent extent,
                                         final int bands,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      final ArcRasterLayer layer = new ArcRasterLayer();
      layer.create(name, channel, extent, dataType, bands, (ISpatialReference) crs);

      return layer;

   }


   @Override
   public ITable getNewTable(final String name,
                             final Class[] types,
                             final String[] fields,
                             final IOutputChannel channel) throws UnsupportedOutputChannelException {

      final ArcTable table = new ArcTable();
      table.create(name, ((FileOutputChannel) channel).getFilename(), fields, types);
      return table;

   }


   @Override
   public IVectorLayer getNewVectorLayer(final String name,
                                         final int shapeType,
                                         final Class[] types,
                                         final String[] fields,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      final ArcVectorLayer layer = new ArcVectorLayer();
      layer.create(name, shapeType, types, fields, channel, crs);

      return layer;

   }


   @Override
   public IVectorLayer getNewVectorLayer(final String name,
                                         final int shapeType,
                                         final Class[] types,
                                         final String[] fields,
                                         final IOutputChannel channel,
                                         final Object crs,
                                         final int[] fieldSize) throws UnsupportedOutputChannelException {

      return getNewVectorLayer(name, shapeType, types, fields, channel, crs);

   }


   @Override
   public String[] getRasterLayerOutputExtensions() {

      return new String[] { "tif" };
   }


   @Override
   public String[] getTableOutputExtensions() {

      return new String[] { "dbf" };
   }


   @Override
   public ITaskMonitor getTaskMonitor(final String title,
                                      final boolean determinate,
                                      final JDialog parent) {
      // this is not used in these bindings
      return null;
   }


   @Override
   public String getTempFolder() {

      final String dir = System.getProperty("java.io.tmpdir");
      final File file = new File(dir);
      if (!file.exists()) {
         file.mkdir();
      }
      return dir;

   }


   @Override
   public String[] getVectorLayerOutputExtensions() {

      return new String[] { "shp" };

   }
}
