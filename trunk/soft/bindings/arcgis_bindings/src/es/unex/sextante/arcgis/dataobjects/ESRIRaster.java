

package es.unex.sextante.arcgis.dataobjects;

import java.io.IOException;

import com.esri.arcgis.datasourcesraster.DblPnt;
import com.esri.arcgis.datasourcesraster.IPixelBlock3;
import com.esri.arcgis.datasourcesraster.IPixelBlock3Proxy;
import com.esri.arcgis.datasourcesraster.IRasterBand;
import com.esri.arcgis.datasourcesraster.IRasterBandCollection;
import com.esri.arcgis.datasourcesraster.IRasterBandCollectionProxy;
import com.esri.arcgis.datasourcesraster.IRasterProps;
import com.esri.arcgis.datasourcesraster.IRasterPropsProxy;
import com.esri.arcgis.datasourcesraster.IRawPixels;
import com.esri.arcgis.datasourcesraster.IRawPixelsProxy;
import com.esri.arcgis.datasourcesraster.RasterWorkspaceFactory;
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IDatasetProxy;
import com.esri.arcgis.geodatabase.IGeoDataset;
import com.esri.arcgis.geodatabase.IGeoDatasetProxy;
import com.esri.arcgis.geodatabase.IPixelBlock;
import com.esri.arcgis.geodatabase.IPixelBlockProxy;
import com.esri.arcgis.geodatabase.IPnt;
import com.esri.arcgis.geodatabase.IRasterDataset2;
import com.esri.arcgis.geodatabase.IRasterDataset2Proxy;
import com.esri.arcgis.geodatabase.IRasterWorkspace2;
import com.esri.arcgis.geodatabase.IRasterWorkspace2Proxy;
import com.esri.arcgis.geodatabase.ISaveAs;
import com.esri.arcgis.geodatabase.ISaveAsProxy;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geodatabase.IWorkspaceProxy;
import com.esri.arcgis.geodatabase.rstPixelType;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.UnknownCoordinateSystem;

import es.unex.sextante.core.Sextante;


/**
 * A wrapper around ESRI Arc Objects raster code. This provides simpler more coherent access to a raster than the raw arc objects
 * code.
 * <p>
 * <p/> An ESRIRaster caches raster data in order to speed up reads and writes. The size of this cache is determined by the block
 * size property. When a read or write occurs the ESRIRaster will determine if the coordinate is in the current cache and if not
 * it will create a new cached block whose width and height are the block size. If possible, this new block will be centered
 * around the coordinate. The default block size is 200.
 * <p>
 * <p/> The are currently two ways to write to a raster. The first {@link #writePixelValue(double, int, int, int) writePixelValue}
 * sets a single pixel in a specified band to a specified value <b>and</b> commits that write. This is good for a one-shot pixel
 * set and write. <p/> However, if you will be setting a larger groups of contiguous pixels then using
 * {@link #setPixelValue(double, int, int, int) setPixelValue} and {@link #commitPixelWrites() commitPixelWrites} will be more
 * efficient.
 * 
 * @author Nick Collier, Victor Olaya
 * @version $revision$ $date$
 */
public class ESRIRaster {

   private final IRasterDataset2 rasterDataset;
   private PixelBlock[]          pixelBlock;
   private final int             blockSize = 500;
   private final int             m_iCols;
   private final int             m_iRows;
   private double                m_dNoData;
   private final int             m_iDataType;


   // encapsulates pixel access
   private class PixelBlock {

      private int                         x, y, width, height;
      private IPixelBlock3                pixelBlock;
      private final IRawPixels            rawPix;
      private final IRasterProps          rasProps;
      private final IRasterBand           band;
      private final IRasterBandCollection bands;
      private Object                      array;
      private ArrayAccessor               accessor;
      private final int                   iBand;


      public PixelBlock(final int iBand) throws IOException {
         bands = new IRasterBandCollectionProxy(rasterDataset);
         band = bands.item(iBand);
         rawPix = new IRawPixelsProxy(band);
         rasProps = new IRasterPropsProxy(rawPix);
         this.iBand = iBand;
      }


      public void reset(final int x,
                        final int y) throws IOException {

         final long lTime = System.currentTimeMillis();

         final int xMin = Math.max(x - blockSize, 0);
         final int xMax = Math.min(x + blockSize, m_iCols);
         final int yMin = Math.max(y - blockSize, 0);
         final int yMax = Math.min(y + blockSize, m_iRows);

         final IPnt pnt = new DblPnt();
         // start with the top left
         pnt.setCoords(xMin, yMin);
         final IPnt size = new DblPnt();
         final int width = xMax - xMin;
         final int height = yMax - yMin;
         size.setCoords(width, height);
         final IPixelBlock pixelBlk = rawPix.createPixelBlock(size);
         // reads the data into the pixelBlk
         rawPix.read(pnt, pixelBlk);
         pixelBlock = new IPixelBlock3Proxy(pixelBlk);
         // get the value at the first band and the first cell in the block
         array = pixelBlock.getPixelData(0);
         setArray(array);
         this.x = xMin;
         this.y = yMin;
         this.width = width;
         this.height = height;

         final long lTime2 = System.currentTimeMillis();
         System.out.println("Reseting pixel block:" + Long.toString(lTime2 - lTime));

      }


      public void writePixel(final int x,
                             final int y,
                             final double val) throws IOException {
         accessor.setValue(x - this.x, y - this.y, val);
      }


      public void write() throws IOException {
         if (pixelBlock != null) {
            pixelBlock.setPixelData(iBand, array);
            final IPixelBlock pblock = new IPixelBlockProxy(pixelBlock);
            final IPnt pnt = new DblPnt();
            pnt.setCoords(this.x, this.y);
            rawPix.write(pnt, pblock);
         }
      }


      private boolean contains(final int x,
                               final int y) {
         return (x >= this.x) && (x < this.x + width) && (y >= this.y) && (y < this.y + height);
      }


      private void setArray(final Object obj) {

         if (accessor == null) {
            final Class arrayType = obj.getClass().getComponentType();
            if (arrayType.equals(int[].class)) {
               accessor = new IntArray();
            }
            else if (arrayType.equals(double[].class)) {
               accessor = new DoubleArray();
            }
            else if (arrayType.equals(float[].class)) {
               accessor = new FloatArray();
            }
            else if (arrayType.equals(long[].class)) {
               accessor = new LongArray();
            }
            else if (arrayType.equals(short[].class)) {
               accessor = new ShortArray();
            }
            else if (arrayType.equals(byte[].class)) {
               accessor = new ByteArray();
            }
            else {
               throw new RuntimeException("Illegal raster pixel type");
            }
         }

         accessor.setArray(obj);

      }


      public double getValue(final int x,
                             final int y) throws IOException {

         return accessor.getValue(x - this.x, y - this.y);

      }


      public void setValue(final int x,
                           final int y,
                           final double dValue) {


         accessor.setValue(x - this.x, y - this.y, dValue);

      }


      public double getNoDataValue() {

         try {
            return ((Number) rasProps.getNoDataValue()).doubleValue();
         }
         catch (final Exception e) {
            return ArcRasterLayer.DEFAULT_NO_DATA;
         }

      }


      public int getDataType() {

         try {
            return rasProps.getPixelType();
         }
         catch (final Exception e) {
            return rstPixelType.PT_DOUBLE;
         }

      }


      public void setNoDataValue(final double dNoDataValue) {
         try {
            rasProps.setNoDataValue(new Double(dNoDataValue));
         }
         catch (final Exception e) {
            Sextante.addErrorToLog("Could not set No Data Value");
         }
      }


      public ISpatialReference getSpatialReference() {

         try {
            return rasProps.getSpatialReference();
         }
         catch (final Exception e) {

            try {
               return new UnknownCoordinateSystem();
            }
            catch (final Exception e1) {
               return null;
            }

         }

      }


   }


   //////////////////////////////

   public ESRIRaster(final String path,
                     final String rasterName,
                     final int rows,
                     final int cols) throws IOException {

      final IWorkspaceFactory factory = new RasterWorkspaceFactory();
      final IRasterWorkspace2 workspace = new IRasterWorkspace2Proxy(factory.openFromFile(path, 0));
      rasterDataset = new IRasterDataset2Proxy(workspace.openRasterDataset(rasterName));

      m_iRows = rows;
      m_iCols = cols;
      final IGeoDataset geoDataset = new IGeoDatasetProxy(rasterDataset);
      final IRasterBandCollection rasterBands = new IRasterBandCollectionProxy(geoDataset);
      pixelBlock = new PixelBlock[rasterBands.getCount()];
      for (int i = 0; i < pixelBlock.length; i++) {
         pixelBlock[i] = new PixelBlock(i);
      }
      m_dNoData = pixelBlock[0].getNoDataValue();
      m_iDataType = pixelBlock[0].getDataType();

   }


   /**
    * Gets the IRasterDataset2 around which this ESRIRaster is a wrapper.
    * 
    * @return the IRasterDataset2 around which this ESRIRaster is a wrapper.
    */
   public IRasterDataset2 getRasterDataset() {
      return rasterDataset;
   }


   /**
    * Gets the current cache block size.<p/> <p/> An ESRIRaster caches raster data in order to speed up reads and writes. The
    * size of this cache is determined by the block size property. When a read or write occurs the ESRIRaster will determine if
    * the coordinate is in the current cache and if not it will create a new cached block whose width and height are the block
    * size. If possible, this new block will be centered around the coordinate.
    * 
    * @return the current cache block size.
    */
   public int getBlockSize() {
      return blockSize * 2;
   }


   /**
    * Saves this raster to the specified path and name
    * 
    * @param path
    * @param name
    * @throws IOException
    *                 if the save cannot be completed
    */
   public void saveAs(final String path,
                      final String name) throws IOException {
      final IWorkspaceFactory factory = new RasterWorkspaceFactory();
      final IRasterWorkspace2 workspace = new IRasterWorkspace2Proxy(factory.openFromFile(path, 0));
      final ISaveAs saver = new ISaveAsProxy(rasterDataset);
      saver.saveAs(name, new IWorkspaceProxy(workspace), "GRID");
   }


   /**
    * Deletes this raster.
    * 
    * @return returns true if the deletion is successful, otherwise false.
    * @throws IOException
    */
   public boolean delete() throws IOException {
      final IDataset dataset = new IDatasetProxy(rasterDataset);
      if (dataset.canDelete()) {
         dataset.delete();
         return true;
      }

      return false;
   }


   /**
    * Gets the number of bands in this ESRIRaster.
    * 
    * @return the number of bands in this ESRIRaster.
    */
   public int getBandCount() throws IOException {

      return pixelBlock.length;

   }


   /**
    * Gets the value of the pixel at the specified pixel grid coordinate and band. If the coordinate is outside of the range of
    * the raseter, this will return the raster's no data value if possible. Otherwise, this will return Double.NaN as a last
    * resort.
    * 
    * @param x
    * @param y
    * @param bandIndex
    * @return the value of the pixel at the specified coordinate and band. If the coordinate is outside of the range of the
    *         raseter, this will return the raster's no data value if possible. Otherwise, this will return Double.NaN as a last
    *         resort.
    * @throws IOException
    *                 if the pixel read fails.
    */
   public double getPixelValue(final int x,
                               final int y,
                               final int bandIndex) throws IOException {


      if (pixelBlock[bandIndex].contains(x, y)) {
         return pixelBlock[bandIndex].getValue(x, y);
      }

      pixelBlock[bandIndex].reset(x, y);
      return pixelBlock[bandIndex].getValue(x, y);
   }


   /**
    * Sets the value of the pixel in the specified band at the specified coordinate. <b>This will not necessarily immediately
    * write this value to the raster. Call <code>commitPixelWrites</code> to ensure that any writes are commited.</b>
    * 
    * @param val
    *                the new value of the pixel. The value will be converted to the type appropriate to the raster.
    * @param x
    * @param y
    * @param bandIndex
    * @throws IOException
    */
   public void setPixelValue(final double val,
                             final int x,
                             final int y,
                             final int bandIndex) throws IOException {


      if (!pixelBlock[bandIndex].contains(x, y)) {
         pixelBlock[bandIndex].write();
         pixelBlock[bandIndex].reset(x, y);
      }
      pixelBlock[bandIndex].writePixel(x, y, val);

   }


   /**
    * Commits any pending pixel writes.
    * 
    * @throws IOException
    */
   public void commitPixelWrites() throws IOException {

      for (int i = 0; i < pixelBlock.length; i++) {
         if (pixelBlock[i] != null) {
            pixelBlock[i].write();
         }
      }

   }


   @Override
   public void finalize() {

      pixelBlock = null;

   }


   public double getNoDataValue() {

      return m_dNoData;

   }


   public int getDataType() {

      return m_iDataType;

   }


   public void setNoDataValue(final double dNoDataValue) {

      for (int i = 0; i < pixelBlock.length; i++) {
         pixelBlock[i].setNoDataValue(dNoDataValue);
      }

      m_dNoData = dNoDataValue;

   }


   public ISpatialReference getSpatialReference() {

      return pixelBlock[0].getSpatialReference();

   }


   ////////////////////////////////////////


   // interface for doing the access and casting on
   // the pixel data provided by a pixel block
   private static interface ArrayAccessor {

      void setValue(int x,
                    int y,
                    double val);


      double getValue(int x,
                      int y);


      void setArray(Object obj);
   }

   private static class IntArray
            implements
               ArrayAccessor {
      private int[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = (int) val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (int[][]) obj;
      }
   }

   private static class LongArray
            implements
               ArrayAccessor {
      private long[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = (long) val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (long[][]) obj;
      }

   }

   private static class FloatArray
            implements
               ArrayAccessor {
      private float[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = (float) val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (float[][]) obj;
      }
   }

   private static class ShortArray
            implements
               ArrayAccessor {
      private short[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = (short) val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (short[][]) obj;
      }
   }

   private static class ByteArray
            implements
               ArrayAccessor {
      private byte[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = (byte) val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (byte[][]) obj;
      }
   }

   private static class DoubleArray
            implements
               ArrayAccessor {
      private double[][] array;


      public void setValue(final int x,
                           final int y,
                           final double val) {
         array[x][y] = val;
      }


      public double getValue(final int x,
                             final int y) {
         return array[x][y];
      }


      public void setArray(final Object obj) {
         array = (double[][]) obj;
      }
   }


}
