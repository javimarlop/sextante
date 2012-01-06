

package es.unex.sextante.inmemory.core;

import javax.swing.JDialog;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.UnsupportedOutputChannelException;
import es.unex.sextante.outputs.IOutputChannel;


public class InMemoryOutputFactory
         extends
            OutputFactory {

   @Override
   public Object getDefaultCRS() {

      return "DefaultCRS";

   }


   @Override
   public IRasterLayer getNewRasterLayer(final String name,
                                         final int dataType,
                                         final AnalysisExtent extent,
                                         final int bands,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      final InMemoryRasterLayer layer = new InMemoryRasterLayer();
      layer.create(extent);

      return layer;
   }


   @Override
   public ITable getNewTable(final String name,
                             final Class[] types,
                             final String[] fields,
                             final IOutputChannel channel) throws UnsupportedOutputChannelException {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public IVectorLayer getNewVectorLayer(final String name,
                                         final int shapeType,
                                         final Class[] types,
                                         final String[] fields,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {


      final InMemoryVectorLayer layer = new InMemoryVectorLayer();
      layer.create(shapeType, types, fields);

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

      //not used, just a dummy array
      return new String[] { "" };

   }


   @Override
   public String[] getTableOutputExtensions() {

      //not used, just a dummy array
      return new String[] { "" };


   }


   @Override
   public ITaskMonitor getTaskMonitor(final String title,
                                      final boolean determinate,
                                      final JDialog parent) {
      //not used
      return null;
   }


   @Override
   public String getTempFolder() {

      //not used
      return null;

   }


   @Override
   public String[] getVectorLayerOutputExtensions() {

      //not used, just a dummy array
      return new String[] { "" };


   }

}
