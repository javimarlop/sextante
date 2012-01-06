package es.unex.sextante.gvsig.core;

import java.awt.geom.AffineTransform;
import java.awt.image.DataBuffer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.cresques.cts.IProjection;
import org.gvsig.raster.buffer.RasterBuffer;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.IBuffer;
import org.gvsig.raster.dataset.Params;

import com.iver.andami.Utilities;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.Sextante;

public class RasterDriver {

   public static final int      RASTER_DATA_TYPE_FLOAT  = DataBuffer.TYPE_FLOAT;
   public static final int      RASTER_DATA_TYPE_DOUBLE = DataBuffer.TYPE_DOUBLE;
   public static final int      RASTER_DATA_TYPE_INT    = DataBuffer.TYPE_INT;
   public static final int      RASTER_DATA_TYPE_SHORT  = DataBuffer.TYPE_SHORT;
   public static final int      RASTER_DATA_TYPE_BYTE   = DataBuffer.TYPE_BYTE;

   private final AnalysisExtent m_GridExtent;
   private IBuffer              buf                     = null;
   private String               name                    = null;


   public RasterDriver(final AnalysisExtent ae,
                       final int iDataType,
                       final int iNumBands) {

      super();

      buf = RasterBuffer.getBuffer(iDataType, ae.getNX(), ae.getNY(), iNumBands, true);
      m_GridExtent = ae;

   }


   public RasterDriver(final AnalysisExtent ae,
                       final int iDataType) {

      this(ae, iDataType, 1);

   }


   public AnalysisExtent getGridExtent() {

      return m_GridExtent;

   }


   public IBuffer getRasterBuf() {

      return this.buf;

   }


   public void reset() {

      this.buf = null;

   }


   public void setCellValue(final int x,
                            final int y,
                            final double dValue) {

      setCellValue(x, y, 0, dValue);

   }


   public void setNoData(final int x,
                         final int y) {

      setNoData(x, y, 0);

   }


   private void setNoData(final int x,
                          final int y,
                          final int iBand) {

      setCellValue(x, y, iBand, getNoDataValue());

   }


   public void setCellValue(final int x,
                            final int y,
                            final int iBand,
                            final double dValue) {

      if (isInGrid(x, y) && (iBand < buf.getBandCount())) {
         try {
            switch (buf.getDataType()) {
               case IBuffer.TYPE_BYTE:
                  buf.setElem(y, x, iBand, (byte) dValue);
                  break;
               case IBuffer.TYPE_SHORT:
                  buf.setElem(y, x, iBand, (short) dValue);
                  break;
               case IBuffer.TYPE_INT:
                  buf.setElem(y, x, iBand, (int) dValue);
                  break;
               case IBuffer.TYPE_FLOAT:
                  buf.setElem(y, x, iBand, (float) dValue);
                  break;
               case IBuffer.TYPE_DOUBLE:
               default:
                  buf.setElem(y, x, iBand, dValue);
                  break;
            }
         }
         catch (final InterruptedException e) {}
      }

   }


   public double getNoDataValue() {

      return buf.getNoDataValue();

   }


   public void setNoDataValue(final double dNoDataValue) {

      buf.setNoDataValue(dNoDataValue);

   }


   public double getCellValue(final int x,
                              final int y) {

      return getCellValue(x, y, 0);

   }


   public double getCellValue(final int x,
                              final int y,
                              final int iBand) {

      try {
         if (isInGrid(x, y) && (iBand < buf.getBandCount())) {
            switch (buf.getDataType()) {
               case IBuffer.TYPE_BYTE:
                  return buf.getElemByte(y, x, iBand);
               case IBuffer.TYPE_SHORT:
                  return buf.getElemShort(y, x, iBand);
               case IBuffer.TYPE_INT:
                  return buf.getElemInt(y, x, iBand);
               case IBuffer.TYPE_FLOAT:
                  return buf.getElemFloat(y, x, iBand);
               case IBuffer.TYPE_DOUBLE:
                  return buf.getElemDouble(y, x, iBand);
               default:
                  return getNoDataValue();
            }
         }
         else {
            return getNoDataValue();
         }
      }
      catch (final InterruptedException e) {
         return getNoDataValue();
      }

   }


   public boolean isNoDataValue(final double dNoDataValue) {

      return (getNoDataValue() == dNoDataValue);

   }


   public boolean isInGrid(final int x,
                           final int y) {

      if ((x < 0) || (y < 0)) {
         return false;
      }

      if ((x >= m_GridExtent.getNX()) || (y >= m_GridExtent.getNY())) {
         return false;
      }

      return true;

   }


   public double getCellSize() {

      return m_GridExtent.getCellSize();

   }


   public static String getFilename(String sRoot,
                                    final String sExtension) {

      String sFilename;
      int i = 1;

      sRoot = sRoot.toLowerCase();
      sRoot = sRoot.replaceAll(" ", "_");
      sRoot = sRoot.replaceAll("\\)", "");
      sRoot = sRoot.replaceAll("\\(", "_");
      sRoot = sRoot.replaceAll("\\[", "_");
      sRoot = sRoot.replaceAll("\\]", "");
      sRoot = sRoot.replaceAll("<", "_");
      sRoot = sRoot.replaceAll(">", "_");
      sRoot = sRoot.replaceAll("__", "_");

      while (true) {
         sFilename = Utilities.createTempDirectory() + File.separator + sRoot + Integer.toString(i) + "." + sExtension;
         final File file = new File(sFilename);
         if (file.exists()) {
            i++;
         }
         else {
            return sFilename;
         }
      }

   }


   private boolean exportToArcInfoASCIIFile(final String sFilename) {

      try {
         final FileWriter f = new FileWriter(sFilename);
         final BufferedWriter fout = new BufferedWriter(f);
         final DecimalFormat df = new DecimalFormat("##.###");
         df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
         df.setDecimalSeparatorAlwaysShown(true);

         fout.write("ncols " + Integer.toString(m_GridExtent.getNX()));
         fout.newLine();
         fout.write("nrows " + Integer.toString(m_GridExtent.getNY()));
         fout.newLine();
         fout.write("xllcorner " + Double.toString(m_GridExtent.getXMin()));
         fout.newLine();
         fout.write("yllcorner " + Double.toString(m_GridExtent.getYMin()));
         fout.newLine();
         fout.write("cellsize " + Double.toString(m_GridExtent.getCellSize()));
         fout.newLine();
         fout.write("nodata_value " + Double.toString(getNoDataValue()));
         fout.newLine();

         for (int i = 0; i < m_GridExtent.getNY(); i++) {
            for (int j = 0; j < m_GridExtent.getNX(); j++) {
               fout.write(df.format(getCellValue(j, i)) + " ");
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
         writerBufferServer.setBuffer(buf, -1);
         final Params params = GeoRasterWriter.getWriter(sFilename).getParams();
         final AffineTransform affineTransform = new AffineTransform(m_GridExtent.getCellSize(), 0, 0,
                  -m_GridExtent.getCellSize(), m_GridExtent.getXMin(), m_GridExtent.getYMax());
         final GeoRasterWriter writer = GeoRasterWriter.getWriter(writerBufferServer, sFilename, buf.getBandCount(),
                  affineTransform, buf.getWidth(), buf.getHeight(), buf.getDataType(), params, projection);
         writer.dataWrite();
         writer.writeClose();

      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         return false;
      }

      return true;


   }


   public void setName(final String name) {
      this.name = name;
   }


   public String getName() {
      return this.name;
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


   public void free() {

      buf = null;

   }


}
