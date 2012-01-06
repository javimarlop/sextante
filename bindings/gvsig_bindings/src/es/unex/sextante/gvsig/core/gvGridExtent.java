package es.unex.sextante.gvsig.core;

import java.awt.geom.Point2D;

import org.gvsig.raster.grid.GridCell;
import org.gvsig.raster.grid.GridExtent;

import es.unex.sextante.core.AnalysisExtent;

/**
 * This class defines a grid system (coordinates and cellsize)
 * 
 * @author Victor Olaya
 */
public class gvGridExtent
         extends
            GridExtent {

   double cellSizeX = 1;
   double cellSizeY = -1;
   int    m_iNX;
   int    m_iNY;


   public gvGridExtent() {}


   /**
    * Assign the extension value and cell size.
    * 
    * @param minX
    *                minimun value in X coordinate
    * @param minY
    *                minimun value in Y coordinate
    * @param maxX
    *                maximun value in X coordinate
    * @param maxY
    *                maximun value in Y coordinate
    * @param dCellSize
    *                cell size
    */
   public gvGridExtent(final AnalysisExtent extent) {
      super(extent.getXMin(), extent.getYMax(), extent.getXMax(), extent.getYMin(), extent.getCellSize());
      cellSizeX = extent.getCellSize();
      cellSizeY = -extent.getCellSize();
      m_iNX = extent.getNX();
      m_iNY = extent.getNY();
   }


   @Override
   public void setXRange(final double dXMin,
                         final double dXMax) {
      getMin().setLocation(Math.min(dXMin, dXMax), minY());
      getMax().setLocation(Math.max(dXMin, dXMax), maxY());
      recalculateNXAndNY();
   }


   @Override
   public void setYRange(final double dYMin,
                         final double dYMax) {
      getMin().setLocation(minX(), Math.min(dYMin, dYMax));
      getMax().setLocation(maxX(), Math.max(dYMin, dYMax));
      recalculateNXAndNY();

   }


   /**
    * Get cell size
    * 
    * @return cell size in double value
    */
   @Override
   public double getCellSize() {
      return cellSizeX;
   }


   /**
    * Set cell size and recalculate pixel distance in both axis
    * 
    * @param cellSize
    *                cell size in double value
    */
   @Override
   public void setCellSize(double cellSize) {
      this.cellSizeX = cellSize;
      this.cellSizeY = -cellSize;
      recalculateNXAndNY();
   }


   /**
    * Get pixel width
    * 
    * @return A integer with the pixel width value
    */
   @Override
   public int getNX() {
      return m_iNX;
   }


   /**
    * Get pixel height
    * 
    * @return A integer with the pixel height value
    */
   @Override
   public int getNY() {
      return m_iNY;
   }


   /**
    * Calculates pixel width and pixel height
    */
   private void recalculateNXAndNY() {
      m_iNY = (int) Math.abs(Math.floor((minY() - maxY()) / cellSizeY));
      m_iNX = (int) Math.abs(Math.floor((maxX() - minX()) / cellSizeX));
   }


   @Override
   public boolean contains(final double x,
                           final double y) {
      return ((x >= minX()) && (x <= maxX()) && (y >= minY()) && (y <= maxY()));
   }


   public boolean fitsIn(final gvGridExtent extent) {

      boolean bFitsX, bFitsY;
      double dOffset;
      double dOffsetCols;
      double dOffsetRows;

      if ((extent.getCellSizeX() != this.getCellSizeX()) || (extent.getCellSizeY() != this.getCellSizeY())) {
         return false;
      }
      dOffset = Math.abs(extent.minX() - minX());
      dOffsetCols = dOffset / getCellSizeX();
      bFitsX = (dOffsetCols == Math.floor(dOffsetCols));

      dOffset = Math.abs(extent.maxY() - maxY());
      dOffsetRows = dOffset / getCellSizeY();
      bFitsY = (dOffsetRows == Math.floor(dOffsetRows));

      return bFitsX && bFitsY;

   }


   /**
    * Compare a extent with the current GridExtent
    * 
    * @param extent
    *                extent to compare
    * @return true if two extents are equals and false if not
    */
   public boolean equals(final gvGridExtent extent) {
      return ((minX() == extent.minX()) && (maxX() == extent.maxX()) && (minY() == extent.minY()) && (maxY() == extent.maxY())
              && (cellSizeX == extent.getCellSizeX()) && (cellSizeY == extent.getCellSizeY()));
   }


   /**
    * Add the layer extent as current extent
    * 
    * @param layer
    *                Layer to set the extent
    */
   /*public void addRasterLayerToExtent(FLyrRaster layer){
           getMin().setLocation(Math.min(layer.getMinX(), minX()), Math.min(layer.getMinY(), minY()));
           getMax().setLocation(Math.max(layer.getMaxX(), maxX()), Math.max(layer.getMaxY(), maxY()));

           cellSize = Math.min(layer.getGrid().getXCellSize(), cellSize);
           recalculateNXAndNY();
   }*/

   @Override
   public GridCell getGridCoordsFromWorldCoords(final Point2D pt) {
      final int x = (int) Math.floor((pt.getX() - minX()) / cellSizeX);
      final int y = (int) Math.ceil((maxY() - pt.getY()) / Math.abs(cellSizeY));
      final GridCell cell = new GridCell(x, y, 0.0);

      return cell;
   }


   @Override
   public Point2D getWorldCoordsFromGridCoords(final GridCell cell) {
      final double x = minX() + (cell.getX()) * cellSizeX;
      final double y = maxY() - (cell.getY()) * Math.abs(cellSizeY);

      final Point2D pt = new Point2D.Double(x, y);

      return pt;
   }


   @Override
   public double getCellSizeX() {
      return cellSizeX;
   }


   @Override
   public void setCellSizeX(final double cellSizeX) {
      this.cellSizeX = cellSizeX;
      recalculateNXAndNY();
   }


   @Override
   public double getCellSizeY() {
      return cellSizeY;
   }


   @Override
   public void setCellSizeY(final double cellSizeY) {
      this.cellSizeY = cellSizeY;
      recalculateNXAndNY();
   }
}
