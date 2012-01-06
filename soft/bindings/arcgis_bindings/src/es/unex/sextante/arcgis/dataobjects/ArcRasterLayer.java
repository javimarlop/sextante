

package es.unex.sextante.arcgis.dataobjects;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import com.esri.arcgis.carto.IRasterLayer;
import com.esri.arcgis.carto.RasterLayer;
import com.esri.arcgis.datasourcesraster.RasterWorkspace;
import com.esri.arcgis.datasourcesraster.RasterWorkspaceFactory;
import com.esri.arcgis.geodatabase.IRaster;
import com.esri.arcgis.geodatabase.IRasterDataset;
import com.esri.arcgis.geodatabase.rstPixelType;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.Point;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractRasterLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.rasterWrappers.GridCell;


public class ArcRasterLayer
         extends
            AbstractRasterLayer {

   static final double    DEFAULT_NO_DATA = -99999d;

   private IRasterLayer   m_Layer;
   private IRaster        m_Raster;

   private String         m_sFilename;
   private String         m_sName         = "Raster Layer";
   private Rectangle2D    m_ExtentRectangle;
   private double         m_dCellsize;
   private ESRIRaster     m_Wrapper;

   private AnalysisExtent m_LayerGridExtent;

   private int            m_XOffset;
   private int            m_YOffset;


   public void create(final IRasterLayer rl) {

      try {
         m_Layer = rl;
         m_sFilename = rl.getFilePath();
         m_Raster = rl.getRaster();
         m_sName = rl.getName();
         final IEnvelope extent = m_Layer.getAreaOfInterest();
         m_ExtentRectangle = new Rectangle2D.Double(extent.getXMin(), extent.getYMin(), extent.getWidth(), extent.getHeight());
         m_dCellsize = extent.getWidth() / m_Layer.getColumnCount();
         final File file = new File(m_sFilename);
         m_Wrapper = new ESRIRaster(file.getParent(), file.getName(), m_Layer.getRowCount(), m_Layer.getColumnCount());
         createLayerGridExtent();
         m_XOffset = 0;
         m_YOffset = 0;
      }
      catch (final Exception e) {
         e.printStackTrace();
         Sextante.addErrorToLog(e);
      }

   }


   public void create(final String sName,
                      final IOutputChannel oc,
                      final AnalysisExtent ae,
                      final int iDataType,
                      final int iNumBands,
                      final ISpatialReference crs) {

      try {

         m_ExtentRectangle = ae.getAsRectangle2D();
         final File file = new File(((FileOutputChannel) oc).getFilename());
         final RasterWorkspaceFactory factory = new RasterWorkspaceFactory();
         final RasterWorkspace workSpace = new RasterWorkspace(factory.openFromFile(file.getParent(), 0));
         final Point originPt = new Point();
         originPt.setX(ae.getXMin());
         originPt.setY(ae.getYMin());
         final IRasterDataset outDataset = workSpace.createRasterDataset(file.getName(), "TIFF", originPt, ae.getNX(),
                  ae.getNY(), ae.getCellSize(), ae.getCellSize(), iNumBands, getArcDataTypeFromSextanteDataType(iDataType), crs,
                  true);
         m_Layer = new RasterLayer();
         m_Layer.createFromDataset(outDataset);
         m_dCellsize = ae.getCellSize();
         m_Raster = m_Layer.getRaster();
         //m_sName = sName;
         m_Wrapper = new ESRIRaster(file.getParent(), file.getName(), m_Layer.getRowCount(), m_Layer.getColumnCount());
         m_sFilename = ((FileOutputChannel) oc).getFilename();
         m_sName = m_sFilename;
         createLayerGridExtent();
         m_XOffset = 0;
         m_YOffset = 0;
      }
      catch (final Exception e) {
         e.printStackTrace();
         Sextante.addErrorToLog(e);
      }

   }


   private void createLayerGridExtent() {

      try {
         final IEnvelope extent = m_Layer.getAreaOfInterest();
         m_LayerGridExtent = new AnalysisExtent();
         m_LayerGridExtent.setCellSize(getLayerCellSize());
         m_LayerGridExtent.setXRange(extent.getXMin(), extent.getXMax(), true);
         m_LayerGridExtent.setYRange(extent.getYMin(), extent.getYMax(), true);
      }
      catch (final Exception e) {//TODO:_change this
      }
   }


   private int getArcDataTypeFromSextanteDataType(final int iDataType) {

      switch (iDataType) {
         case AbstractRasterLayer.RASTER_DATA_TYPE_BYTE:
            return rstPixelType.PT_CHAR;
         case AbstractRasterLayer.RASTER_DATA_TYPE_FLOAT:
            return rstPixelType.PT_FLOAT;
         case AbstractRasterLayer.RASTER_DATA_TYPE_INT:
            return rstPixelType.PT_SHORT;
         case AbstractRasterLayer.RASTER_DATA_TYPE_DOUBLE:
         default:
            return rstPixelType.PT_DOUBLE;
      }

   }


   @Override
   public int getBandsCount() {

      try {
         return m_Layer.getBandCount();
      }
      catch (final Exception e) {
         return 0;
      }

   }


   @Override
   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      /*try {
         return m_Wrapper.getPixelValue(x, y, band);
      }
      catch (final IOException e) {
         return getNoDataValue();
      }*/
      //this is not used, since we are not using grid wrappers
      return 0;


   }


   @Override
   public int getDataType() {

      try {
         return getSextanteDataTypeFromArcType(m_Wrapper.getDataType());
      }
      catch (final Exception e) {
         return es.unex.sextante.dataObjects.IRasterLayer.RASTER_DATA_TYPE_DOUBLE;
      }
   }


   private int getSextanteDataTypeFromArcType(final int iPixelType) {

      switch (iPixelType) {
         case rstPixelType.PT_CHAR:
            return AbstractRasterLayer.RASTER_DATA_TYPE_BYTE;
         case rstPixelType.PT_FLOAT:
            return AbstractRasterLayer.RASTER_DATA_TYPE_FLOAT;
         case rstPixelType.PT_SHORT:
            return AbstractRasterLayer.RASTER_DATA_TYPE_INT;
         case rstPixelType.PT_DOUBLE:
         default:
            return AbstractRasterLayer.RASTER_DATA_TYPE_DOUBLE;
      }

   }


   @Override
   public double getLayerCellSize() {

      return m_dCellsize;

   }


   @Override
   public AnalysisExtent getLayerGridExtent() {

      return m_LayerGridExtent;

   }


   @Override
   public double getNoDataValue() {

      try {
         return m_Wrapper.getNoDataValue();
      }
      catch (final Exception e) {
         return DEFAULT_NO_DATA;
      }

   }


   @Override
   public void setCellValue(final int x,
                            final int y,
                            final int band,
                            final double value) {

      if (isInWindow(x, y)) {
         try {
            m_Wrapper.setPixelValue(value, x, y, band);
         }
         catch (final IOException e1) {
            e1.printStackTrace();
         }
      }

   }


   @Override
   public void setNoData(final int x,
                         final int y) {

      setCellValue(x, y, DEFAULT_NO_DATA);

   }


   @Override
   public void setNoData(final int x,
                         final int y,
                         final int iBand) {

      setCellValue(x, y, iBand, DEFAULT_NO_DATA);

   }


   @Override
   public void setNoDataValue(final double dNoDataValue) {

      m_Wrapper.setNoDataValue(dNoDataValue);


   }


   @Override
   public Object getCRS() {

      try {
         return m_Wrapper.getSpatialReference();
      }
      catch (final Exception e) {
         return null;
      }
   }


   @Override
   public Rectangle2D getFullExtent() {

      return m_ExtentRectangle;

   }


   @Override
   public void close() {
   }


   @Override
   public void free() {
   }


   @Override
   public Object getBaseDataObject() {

      return m_Layer;

   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);
   }


   @Override
   public void open() {
   }


   @Override
   public void postProcess() throws Exception {

      m_Wrapper.commitPixelWrites();
      m_Layer = new RasterLayer();
      m_Layer.createFromRaster(m_Raster);

   }


   @Override
   public void setName(final String sName) {

      //      try {
      //         m_sName = name;
      //         if (m_Layer != null) {
      //            m_Layer.setName(name);
      //         }
      //      }
      //      catch (final Exception e) {
      //      }

   }


   ////////////////////////////////////////////////


   @Override
   public byte getCellValueAsByte(final int x,
                                  final int y,
                                  final int band) {

      return (byte) getCellValueAsDouble(x, y);

   }


   @Override
   public byte getCellValueAsByte(final int x,
                                  final int y) {

      return (byte) getCellValueAsDouble(x, y);

   }


   @Override
   public double getCellValueAsDouble(final int x,
                                      final int y,
                                      final int band) {

      final int x2 = x + m_XOffset;
      final int y2 = y + m_YOffset;
      try {
         if (isInWindow(x2, y2)) { //check band??
            return m_Wrapper.getPixelValue(x2, y2, band);
         }
         else {
            return getNoDataValue();
         }

      }
      catch (final IOException e) {
         return getNoDataValue();
      }

   }


   @Override
   public double getCellValueAsDouble(final int x,
                                      final int y) {

      return getCellValueAsDouble(x, y, 0);

   }


   @Override
   public float getCellValueAsFloat(final int x,
                                    final int y,
                                    final int band) {

      return (float) getCellValueAsDouble(x, y, band);

   }


   @Override
   public float getCellValueAsFloat(final int x,
                                    final int y) {

      return (float) getCellValueAsDouble(x, y);

   }


   @Override
   public int getCellValueAsInt(final int x,
                                final int y,
                                final int band) {

      return (int) getCellValueAsDouble(x, y, band);

   }


   @Override
   public int getCellValueAsInt(final int x,
                                final int y) {

      return (int) getCellValueAsDouble(x, y);

   }


   @Override
   public short getCellValueAsShort(final int x,
                                    final int y,
                                    final int band) {

      return (short) getCellValueAsDouble(x, y, band);

   }


   @Override
   public short getCellValueAsShort(final int x,
                                    final int y) {

      return (short) getCellValueAsDouble(x, y);

   }


   @Override
   public int getNX() {

      return m_LayerGridExtent.getNX();

   }


   @Override
   public int getNY() {

      return m_LayerGridExtent.getNX();

   }


   @Override
   public double getValueAt(final double x,
                            final double y,
                            final int band) {

      final GridCell cell = m_LayerGridExtent.getGridCoordsFromWorldCoords(x, y);
      return getCellValueAsDouble(cell.getX(), cell.getY());

   }


   @Override
   public double getValueAt(final double x,
                            final double y) {

      final GridCell cell = m_LayerGridExtent.getGridCoordsFromWorldCoords(x, y);
      return getCellValueAsDouble(cell.getX(), cell.getY());
   }


   @Override
   public double getWindowCellSize() {

      return m_LayerGridExtent.getCellSize();

   }


   @Override
   public AnalysisExtent getWindowGridExtent() {

      return getLayerGridExtent();
   }


   @Override
   public boolean isInWindow(final int x,
                             final int y) {

      if ((x < 0) || (y < 0)) {
         return false;
      }

      if ((x >= m_LayerGridExtent.getNX()) || (y >= m_LayerGridExtent.getNY())) {
         return false;
      }

      return true;
   }


   @Override
   public void setFullExtent() {

      setConstants();
      setStatisticsHaveToBeCalculated();


   }


   @Override
   public void setWindowExtent(final AnalysisExtent gridExtent) {

      //we ignore this method because, from arcgis, the gridExtent parameter is always going to be the outputextent (which equals the layer extent)
      //There are a few exceptions, when the passed extent fits into the layer one, but has an offset.
      //we deal with them using the offset values.

      m_XOffset = (int) ((gridExtent.getXMin() - m_LayerGridExtent.getXMin()) / m_LayerGridExtent.getCellSize());
      m_YOffset = (int) ((m_LayerGridExtent.getYMax() - gridExtent.getYMax()) / m_LayerGridExtent.getCellSize());

      super.setConstants();
      super.setStatisticsHaveToBeCalculated();

   }


   @Override
   public void setInterpolationMethod(final int iMethod) {

      //do nothing

   }


}
