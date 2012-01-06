

package es.unex.sextante.inmemory.core;

import java.awt.geom.Rectangle2D;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.dataObjects.AbstractRasterLayer;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.outputs.IOutputChannel;


public class InMemoryRasterLayer
         extends
            AbstractRasterLayer {

   private double[][]     data;
   private AnalysisExtent m_Extent;


   public void create(final AnalysisExtent extent) {

      m_Extent = extent;
      data = new double[extent.getNX()][extent.getNY()];

   }


   public void create(final double[][] data,
                      final double dMinX,
                      final double dMinY,
                      final double dCellsize) {

      this.data = data;
      m_Extent = new AnalysisExtent();
      m_Extent.setCellSize(dCellsize);
      m_Extent.setXRange(dMinX, dMinX + dCellsize * data.length, true);
      m_Extent.setYRange(dMinY, dMinY + dCellsize * data[0].length, true);

   }


   @Override
   public int getBandsCount() {

      return 1;

   }


   @Override
   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      if (isIn(x, y)) {
         return data[x][y];
      }
      else {
         return getNoDataValue();
      }

   }


   private boolean isIn(final int x,
                        final int y) {

      return (x < data.length) && (x >= 0) && (y < data[0].length) && (y >= 0);
   }


   @Override
   public int getDataType() {

      return IRasterLayer.RASTER_DATA_TYPE_DOUBLE;

   }


   @Override
   public double getLayerCellSize() {

      return m_Extent.getCellSize();

   }


   @Override
   public AnalysisExtent getLayerGridExtent() {

      return m_Extent;
   }


   @Override
   public double getNoDataValue() {

      return -99999.;
   }


   @Override
   public void setCellValue(final int x,
                            final int y,
                            final int band,
                            final double value) {

      if (isIn(x, y)) {
         data[x][y] = value;
      }

   }


   @Override
   public void setNoDataValue(final double noDataValue) {


   }


   @Override
   public Object getCRS() {

      return null;

   }


   @Override
   public Rectangle2D getFullExtent() {

      return m_Extent.getAsRectangle2D();

   }


   @Override
   public void close() {
   }


   @Override
   public void free() {
   }


   @Override
   public Object getBaseDataObject() {

      return data;

   }


   @Override
   public String getName() {

      return "";

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return null;

   }


   @Override
   public void open() {
   }


   @Override
   public void postProcess() throws Exception {
   }


   @Override
   public void setName(final String name) {
   }


}
