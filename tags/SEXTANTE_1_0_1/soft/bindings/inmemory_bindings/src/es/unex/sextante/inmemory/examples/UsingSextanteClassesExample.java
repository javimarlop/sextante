

package es.unex.sextante.inmemory.examples;

import java.io.IOException;

import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.inmemory.core.InMemoryRasterLayer;
import es.unex.sextante.inmemory.examples.tools.Raster;
import es.unex.sextante.inmemory.examples.tools.RasterReader;


/**
 * This is an example of how to use SEXTANTE classes for your own algorithms, even if they are not SEXTANTE algorithms (i.e. they
 * do not extend the GeoAlgorithm class). SEXTANTE wrapper classes can be used to ease working with both raster and vector layers,
 * as shown in this example.
 */
public class UsingSextanteClassesExample {

   /**
    * Visibility calculations for a given point and a given sun position
    * 
    * @param dem
    *                the DEM with the terrain
    * @param worldX
    *                the x coordinate of the point to analize
    * @param worldY
    *                the x coordinate of the point to analize
    * @param sunAzimuth
    *                the azimuth of the sun position, in degrees
    * @param sunHeightDegrees
    *                the sun height, in degrees
    * @return
    */
   public static boolean getVisibility(final IRasterLayer dem,
                                       final double worldX, //these are world coordinates, not grid ones
                                       final double worldY,
                                       final double sunAzimuthDegrees, //in degrees
                                       final double sunHeightDegrees) {


      double x = worldX;
      double y = worldY;
      final double dElevation = dem.getValueAt(x, y);
      final double sunHeight = Math.toRadians(sunHeightDegrees);
      final double sunAzimuth = Math.toRadians(sunAzimuthDegrees);
      double dx, dy, d = 0;
      final AnalysisExtent ext = dem.getWindowGridExtent();

      dx = Math.abs(Math.sin(sunAzimuth));
      dy = Math.abs(Math.cos(sunAzimuth));

      if ((dx > 0.0) || (dy > 0.0)) {
         if (dx > dy) {
            dx /= ext.getCellSize();
            dy /= dx;
            dx = ext.getCellSize();
         }
         else {
            dy /= ext.getCellSize();
            dx /= dy;
            dy = ext.getCellSize();
         }

         final double dDeltaD = Math.sqrt(dx * dx + dy * dy);

         for (d = 0.0;; d += dDeltaD, x += dx, y += dy) {
            if (ext.contains(x, y)) {
               final double dElevation2 = dem.getValueAt(x, y);
               final double dAngle = Math.atan2(dElevation2 - dElevation, d);
               if (dAngle > sunHeight) {
                  return false;
               }
            }
            else {
               return true;
            }
         }
      }

      return false;

   }


   public static void main() {

      final InMemoryRasterLayer layer = new InMemoryRasterLayer();
      try {
         //We read the asc file using the provided classes, which create a rather simple Raster object
         //from which it is easy to take the data we want to use for the analysis
         final Raster raster = new RasterReader().readRaster("dem.asc");
         layer.create(raster.getData(), raster.getXll(), raster.getYll(), raster.getCellsize());

         //We need the observer's coordinate. We take the center of the grid as the observer position.
         final double x = raster.getXll() + raster.getCols() / 2. * raster.getCellsize();
         final double y = raster.getYll() + raster.getRows() / 2. * raster.getCellsize();

         //Now we call the algorithm. Sun position is hardcoded to standard values
         final boolean vis = getVisibility(layer, x, y, 45., 315.);

         //Print out the result.
         System.out.println(vis);

      }
      catch (final IOException e) {
         System.out.println("Error reading input DEM file");
         e.printStackTrace();
      }


   }

}
