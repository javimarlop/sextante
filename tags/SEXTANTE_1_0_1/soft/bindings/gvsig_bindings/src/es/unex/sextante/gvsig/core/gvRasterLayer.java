package es.unex.sextante.gvsig.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.buffer.RasterBufferInvalidException;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.IBuffer;
import org.gvsig.raster.dataset.Params;
import org.gvsig.raster.grid.Grid;
import org.gvsig.raster.grid.GridException;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractRasterLayer;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;


/**
 * 
 * @author volaya, nacho brodin (nachobrodin@gmail.com)
 * 
 */
public class gvRasterLayer
         extends
            AbstractRasterLayer {

   private String         m_sFilename;
   private IProjection    m_Projection;
   private Grid           m_Grid;
   private IBuffer        m_Buffer;
   private AnalysisExtent m_AnalysisExtent;
   private int            m_iDataType;
   private String         m_sName;
   private FLyrRasterSE   m_Layer;
   private int            m_iBands;
   private IOutputChannel m_OutputChannel;
   private double         m_dNoData;


   public void create(final FLyrRasterSE layer) {

      m_Layer = layer;
      m_iDataType = m_Layer.getDataType()[0];
      final AnalysisExtent extent = new AnalysisExtent();
      extent.setCellSize(m_Layer.getCellSize());
      extent.setXRange(m_Layer.getMinX(), m_Layer.getMaxX(), true);
      extent.setYRange(m_Layer.getMinY(), m_Layer.getMaxY(), true);
      m_AnalysisExtent = extent;
      m_Projection = m_Layer.getProjection();
      if (m_Layer.getDataSource() != null)
    	  m_sFilename = m_Layer.getDataSource().getDataset(0)[0].getFName();
      m_sName = m_Layer.getName();
      m_iBands = m_Layer.getBandCount();
      m_OutputChannel = null;
      m_dNoData = m_Layer.getNoDataValue();

   }


   public void create(final String sName,
                      final IOutputChannel oc,
                      final AnalysisExtent ae,
                      final int iDataType,
                      final int iNumBands,
                      final IProjection crs) {

      m_OutputChannel = oc;
      m_iBands = iNumBands;
      final int[] bands = new int[iNumBands];
      for (int i = 0; i < bands.length; i++) {
         bands[i] = i;
      }
      final gvGridExtent gridExtent = new gvGridExtent(ae);
      try {
         m_Grid = new Grid(gridExtent, gridExtent, iDataType, bands);
      }
      catch (final RasterBufferInvalidException e) {
         throw new RuntimeException("Error creating a new empty writable Grid with: GridExtent = " + gridExtent
                                    + ", data type = " + iDataType + ", bands = " + iNumBands, e);
      }
      m_dNoData = SextanteGUI.getOutputFactory().getDefaultNoDataValue();

      m_iDataType = iDataType;
      m_AnalysisExtent = ae;
      m_Buffer = m_Grid.getRasterBuf();
      if (oc instanceof NullOutputChannel) {
         m_sFilename = SextanteGUI.getOutputFactory().getTempRasterLayerFilename();
      }
      else {
         m_sFilename = ((FileOutputChannel) oc).getFilename();
      }
      m_Projection = crs;
      m_sName = sName;

   }


   public void create(final String sName,
                      final IOutputChannel oc,
                      final AnalysisExtent ge,
                      final int iDataType,
                      final IProjection crs) {

      create(sName, oc, ge, iDataType, 1, crs);

   }


   public int getDataType() {

      return m_iDataType;

   }


   public void setCellValue(final int x,
                            final int y,
                            final int iBand,
                            final double dValue) {

      if (m_Layer != null) {
         return;
      }

      if (!isInGrid(x, y, iBand)) {
         return;
      }

      try {
         switch (m_iDataType) {
            case IBuffer.TYPE_DOUBLE:
               m_Buffer.setElem(y, x, iBand, dValue);
               break;
            case IBuffer.TYPE_FLOAT:
               m_Buffer.setElem(y, x, iBand, (float) dValue);
               break;
            case IBuffer.TYPE_INT:
               m_Buffer.setElem(y, x, iBand, (int) dValue);
               break;
            case IBuffer.TYPE_SHORT:
            case IBuffer.TYPE_USHORT:
               m_Buffer.setElem(y, x, iBand, (short) dValue);
               break;
            case IBuffer.TYPE_BYTE:
               m_Buffer.setElem(y, x, iBand, (byte) dValue);
               break;
         }
      }
      catch (final InterruptedException e) {
         throw new RuntimeException("Interrupted while setting value of cell x = " + x + ", y = " + y + ", band = " + iBand
                                    + ", value = " + dValue, e);
      }

   }


   public void setNoDataValue(final double dNoDataValue) {

      m_dNoData = dNoDataValue;

   }


   @Override
   public void setNoData(final int x,
                         final int y) {

      if (m_Grid == null) {
         return;
      }

      setCellValue(x, y, m_dNoData);

   }


   public double getNoDataValue() {

      return m_dNoData;

   }


   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      try {
         switch (m_iDataType) {
            case IBuffer.TYPE_DOUBLE:
               return m_Buffer.getElemDouble(y, x, band);
            case IBuffer.TYPE_FLOAT:
               return m_Buffer.getElemFloat(y, x, band);
            case IBuffer.TYPE_INT:
               return m_Buffer.getElemInt(y, x, band);
            case IBuffer.TYPE_SHORT:
            case IBuffer.TYPE_USHORT:
               return m_Buffer.getElemShort(y, x, band);
            case IBuffer.TYPE_BYTE:
               return (m_Buffer.getElemByte(y, x, band) & 0xff);
            default:
               return m_Grid.getNoDataValue();
         }
      }
      catch (final InterruptedException e) {
         throw new RuntimeException("Interrupted while getting value of cell x = " + x + ", y = " + y + ", band = " + band, e);
      }

   }


   public boolean isInGrid(final int x,
                           final int y,
                           final int iBand) {

      if ((x < 0) || (y < 0)) {
         return false;
      }

      if ((x >= m_AnalysisExtent.getNX()) || (y >= m_AnalysisExtent.getNY())) {
         return false;
      }

      return iBand < m_iBands;

   }


   public int getBandsCount() {

      return m_iBands;

   }


   public String getName() {

      return m_sName;

   }


   public void postProcess() {

      if (m_OutputChannel instanceof NullOutputChannel) {
         return;
      }

      if ((m_Layer == null) && (m_sFilename != null)) {
         export(m_sFilename, m_Projection);
      }

   }


   public boolean export(final String sFilename,
                         final IProjection projection) {

      if (sFilename.endsWith("asc")) {
         return exportToArcInfoASCIIFile(sFilename);
      }
      else {
         return exportUsinggvSIG(sFilename, projection);
      }

   }


   private boolean exportToArcInfoASCIIFile(final String sFilename) {

      try {
         final FileWriter f = new FileWriter(sFilename);
         final BufferedWriter fout = new BufferedWriter(f);
         final DecimalFormat df = new DecimalFormat("##.###");
         df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
         df.setDecimalSeparatorAlwaysShown(true);

         fout.write("ncols " + Integer.toString(m_AnalysisExtent.getNX()));
         fout.newLine();
         fout.write("nrows " + Integer.toString(m_AnalysisExtent.getNY()));
         fout.newLine();
         fout.write("xllcorner " + Double.toString(m_AnalysisExtent.getXMin()));
         fout.newLine();
         fout.write("yllcorner " + Double.toString(m_AnalysisExtent.getYMin()));
         fout.newLine();
         fout.write("cellsize " + Double.toString(m_AnalysisExtent.getCellSize()));
         fout.newLine();
         fout.write("nodata_value " + Double.toString(getNoDataValue()));
         fout.newLine();

         for (int i = 0; i < m_AnalysisExtent.getNY(); i++) {
            for (int j = 0; j < m_AnalysisExtent.getNX(); j++) {
               fout.write(df.format(getCellValueAsDouble(j, i)) + " ");
            }
            fout.newLine();
         }
         fout.close();
         f.close();
      }
      catch (final Exception e) {
         return false;
      }

      return true;

   }


   public boolean exportUsinggvSIG(final String sFilename,
                                   final IProjection projection) {

      try {
         final WriterBufferServer writerBufferServer = new WriterBufferServer();
         writerBufferServer.setBuffer(m_Buffer, -1);
         final Params params = GeoRasterWriter.getWriter(sFilename).getParams();
         final AffineTransform affineTransform = new AffineTransform(m_AnalysisExtent.getCellSize(), 0, 0,
                  -m_AnalysisExtent.getCellSize(), m_AnalysisExtent.getXMin(), m_AnalysisExtent.getYMax());
         final GeoRasterWriter writer = GeoRasterWriter.getWriter(writerBufferServer, sFilename, m_Buffer.getBandCount(),
                  affineTransform, m_Buffer.getWidth(), m_Buffer.getHeight(), m_Buffer.getDataType(), params, projection);
         writer.dataWrite();
         writer.writeClose();

      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         return false;
      }

      return true;


   }


   public void open() {

      if (m_Layer != null) {
         try {
            m_Grid = m_Layer.getReadOnlyFullGrid(false);
            m_Buffer = m_Grid.getRasterBuf();
         }
         catch (final GridException e) {
            Sextante.addErrorToLog(e);
         }
         catch (final InterruptedException e) {
            Sextante.addErrorToLog(e);
         }
      }
      else {
         final FLyrRasterSE flayer = (FLyrRasterSE) FileTools.openLayer(m_sFilename, m_sName, (IProjection) getCRS());
         if (flayer != null) {
            final double dNoDataValue = getNoDataValue();
            create(flayer);
            open();
            m_dNoData = dNoDataValue;
         }
      }

   }


   public void close() {

      if ((m_Layer != null) && (m_Grid != null)) {
         m_Grid.freeAndClose();
      }

   }


   public Rectangle2D getFullExtent() {

      return m_AnalysisExtent.getAsRectangle2D();

   }


   public AnalysisExtent getLayerGridExtent() {

      return m_AnalysisExtent;

   }


   public double getLayerCellSize() {

      return m_AnalysisExtent.getCellSize();

   }


   public IOutputChannel getOutputChannel() {

      if (m_OutputChannel != null) {
         return m_OutputChannel;
      }
      else if (m_sFilename != null) {
         return new FileOutputChannel(m_sFilename);
      }
      else {
         return null;
      }

   }


   public Object getCRS() {

      return m_Projection;

   }


   public void setName(final String sName) {

      if (m_Layer != null) {
         m_Layer.setName(sName);
      }
      m_sName = sName;

   }


   @Override
   public Object getBaseDataObject() {

      if (m_Layer != null) {
         return m_Layer;
      }
      else {
         return m_Grid;
      }

   }


   @Override
   public void free() {

      if (m_Layer == null) {//only free layers created by an algorithm
         m_Buffer.free();
         m_Buffer = null;
         m_Grid = null;
      }

   }


}
