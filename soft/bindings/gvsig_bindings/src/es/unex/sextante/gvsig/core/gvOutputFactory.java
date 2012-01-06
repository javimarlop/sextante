package es.unex.sextante.gvsig.core;

import javax.swing.JDialog;

import org.cresques.cts.IProjection;

import com.iver.andami.Utilities;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.project.Project;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.UnsupportedOutputChannelException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;
import es.unex.sextante.outputs.OverwriteOutputChannel;

/**
 * An OutputFactory based on the gvSIG data model. Supports only file-based outputs.
 * 
 * @author volaya
 * 
 */
public class gvOutputFactory
         extends
            OutputFactory {


   @Override
   public IVectorLayer getNewVectorLayer(final String sName,
                                         final int iShapeType,
                                         final Class[] types,
                                         final String[] sFields,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      if ((channel instanceof FileOutputChannel) || (channel instanceof OverwriteOutputChannel)
          || (channel instanceof NullOutputChannel)) {
         final gvVectorLayer layer = new gvVectorLayer();
         layer.create(sName, channel, iShapeType, types, sFields, crs);
         return layer;
      }
      else {
         throw new UnsupportedOutputChannelException();
      }

   }


   @Override
   public IRasterLayer getNewRasterLayer(final String sName,
                                         final int iDataType,
                                         final AnalysisExtent extent,
                                         final int iBands,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      if ((channel instanceof FileOutputChannel) || (channel instanceof NullOutputChannel)) {
         final gvRasterLayer layer = new gvRasterLayer();
         layer.create(sName, channel, extent, iDataType, iBands, (IProjection) crs);
         return layer;
      }
      else {
         throw new UnsupportedOutputChannelException();
      }

   }


   @Override
   public ITable getNewTable(final String sName,
                             final Class types[],
                             final String[] sFields,
                             final IOutputChannel channel) throws UnsupportedOutputChannelException {

      if ((channel instanceof FileOutputChannel) || (channel instanceof NullOutputChannel)) {
         final gvTable table = new gvTable();
         table.create(sName, channel, types, sFields);
         return table;
      }
      else {
         throw new UnsupportedOutputChannelException();
      }

   }


   @Override
   public String getTempFolder() {

      return Utilities.createTempDirectory();

   }


   @Override
   public String[] getRasterLayerOutputExtensions() {

      return FileTools.RASTER_EXT_IN;

   }


   @Override
   public String[] getVectorLayerOutputExtensions() {

      return new String[] { "shp", "dxf" };

   }


   @Override
   public String[] getTableOutputExtensions() {

      return new String[] { "dbf" };

   }


   public void addMessage(final String s) {

      NotificationManager.addInfo(s, null);

   }


   @Override
   public ITaskMonitor getTaskMonitor(final String sTitle,
                                      final boolean bDeterminate,
                                      final JDialog parent) {

      return new DefaultTaskMonitor(sTitle, bDeterminate, parent);

   }


   @Override
   public Object getDefaultCRS() {

      return Project.getDefaultProjection();

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

}
