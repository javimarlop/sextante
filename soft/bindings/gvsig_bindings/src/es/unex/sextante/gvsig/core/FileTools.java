package es.unex.sextante.gvsig.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.dataset.RasterDataset;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.grass.GrassUtils;
import es.unex.sextante.io3d.ASCII3DFileTools;


public class FileTools {

   public final static String[] RASTER_EXT_IN     = { "tif", "asc", "dat", "tiff", "bmp", "gif", "img", "jpg", "png", "vrt",
            "lan", "gis", "pix", "aux", "adf", "mpr", "mpl", "map", "hdr" };
   public final static String[] RASTER_DRIVERS_IN = { "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver"                 };

   public final static String[] RASTER3D_EXT_IN   = { "asc3d" };

   public final static String[] VECTOR_EXT_IN     = { "shp", "gml", "dxf", "dgn", "dwg" };
   public final static String[] VECTOR_DRIVERS_IN = { "gvSIG shp driver", "gvSIG GML Memory Driver", "gvSIG DXF Memory Driver",
            "gvSIG DGN Memory Driver", "gvSIG DWG Memory Driver" };
   public final static String[] TABLE_EXT         = { "dbf" };
   public static final String[] LAYERS_EXT_IN     = { "tif", "asc", "dat", "tiff", "bmp", "gif", "img", "jpg", "png", "vrt",
            "lan", "gis", "pix", "aux", "adf", "mpr", "mpl", "map", "shp", "gml", "dxf", "dgn", "dwg" };
   public static final String[] LAYER_DRIVERS_IN  = { "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver", "gvSIG Image Driver",
            "gvSIG shp driver", "gvSIG GML Memory Driver", "gvSIG DXF Memory Driver", "gvSIG DGN Memory Driver",
            "gvSIG DWG Memory Driver"            };


   public static FLayer openLayer(final String sFilename,
                                  final String sName,
                                  final IProjection projection) {

      final String sExtension = sFilename.substring(sFilename.lastIndexOf('.') + 1, sFilename.length());

      final String[] extensionSupported = RasterDataset.getExtensionsSupported();
      FLyrRasterSE rlayer = null;
      for (int i = 0; i < extensionSupported.length; i++) {
         if (sExtension.equals(extensionSupported[i])) {
            try {
               rlayer = FLyrRasterSE.createLayer(sName, new File(sFilename), projection);
            }
            catch (final LoadLayerException e) {
               e.printStackTrace();
               return null;
            }
         }
         if ((rlayer != null) && rlayer.isAvailable()) {
            return rlayer;
         }
      }

      for (int i = 0; i < LAYERS_EXT_IN.length; i++) {
         if (sExtension.equals(LAYERS_EXT_IN[i])) {
            try {
               FLayer layer;
               final Driver driver = LayerFactory.getDM().getDriver(FileTools.LAYER_DRIVERS_IN[i]);

               layer = LayerFactory.createLayer(sName, (VectorialFileDriver) driver, new File(sFilename), projection);


               if ((layer != null) && layer.isAvailable()) {
                  return layer;
               }
               else {
                  return null;
               }
            }
            catch (final Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }

      return null;
   }


   public static I3DRasterLayer open3DRasterLayer(final String sFilename) {

      final String sExtension = sFilename.substring(sFilename.lastIndexOf('.') + 1, sFilename.length());

      for (int i = 0; i < RASTER3D_EXT_IN.length; i++) {
         if (sExtension.equals(RASTER3D_EXT_IN[i])) {
            try {
               final I3DRasterLayer layer3d = ASCII3DFileTools.readFile(new File(sFilename));
               return layer3d;
            }
            catch (final Exception e) {
               return null;
            }
         }
      }

      return null;

   }


   public static ProjectTable openTable(final String sFilename,
                                        final String sName) {

      LayerFactory.getDataSourceFactory().addFileDataSource("gdbms dbf driver", sName, sFilename);
      DataSource dataSource;
      try {
         dataSource = LayerFactory.getDataSourceFactory().createRandomDataSource(sName, DataSourceFactory.AUTOMATIC_OPENING);
         final SelectableDataSource sds = new SelectableDataSource(dataSource);
         final EditableAdapter ea = new EditableAdapter();
         ea.setOriginalDataSource(sds);
         final ProjectTable pt = ProjectFactory.createTable(sName, ea);
         return pt;
      }
      catch (final Exception e) {
         return null;
      }

   }


   public static Object open(final String sFilename) {

      final FLayer layer = openLayer(sFilename, sFilename, Project.getDefaultProjection());
      if (layer != null) {
         return layer;
      }

      final ProjectTable table = openTable(sFilename, sFilename);
      if (table != null) {
         return table;
      }

      final I3DRasterLayer layer3D = open3DRasterLayer(sFilename);
      return layer3D;


   }


   /*
    * This method writes a complete metafile (RMF) for a new SEXTANTE
    * layer. If there is a color table file present, then that will
    * be written into the RMF file, as well.
    */
   private static void writeRMF(String filename,
                                final FLyrRasterSE rlayer) {
      try {
         final StringBuffer buffer = new StringBuffer();
         filename = filename.substring(0, filename.lastIndexOf("."));
         final File inFile = new File(filename + "." + GrassUtils.colorTableExt);
         if (inFile.exists()) {
            final BufferedReader in = new BufferedReader(new FileReader(inFile));
            String line = in.readLine();
            /* write XML header */
            buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n");
            buffer.append("<RasterMetaFile>\n");
            /* append (fake) raster statistics */
            buffer.append("<Statistics>\n");
            buffer.append("\t<BandCount>1</BandCount>\n");
            buffer.append("\t<Band>\n");
            buffer.append("\t\t<Max>" + "1.0" + "</Max>\n");
            buffer.append("\t\t<Min>" + "0.0" + "</Min>\n");
            buffer.append("\t\t<SecondMax>" + "1.0" + "</SecondMax>\n");
            buffer.append("\t\t<SecondMin>" + "0.0" + "</SecondMin>\n");
            buffer.append("\t\t<MaxRGB>" + "255.0" + "</MaxRGB>\n");
            buffer.append("\t\t<MinRGB>" + "0.0" + "</MinRGB>\n");
            buffer.append("\t\t<SecondMaxRGB>" + "255.0" + "</SecondMaxRGB>\n");
            buffer.append("\t\t<SecondMinRGB>" + "0.0" + "</SecondMinRGB>\n");
            buffer.append("\t\t<Mean>" + "0.5" + "</Mean>\n");
            buffer.append("\t\t<Variance>" + "0.0" + "</Variance>\n");
            buffer.append("\t</Band>\n");
            buffer.append("</Statistics>\n");
            if (line.contains(GrassUtils.colorTableIdentifier)) {
               /* append color table definition */
               Double from = 0.0;
               Double to = 0.0;
               int R = 0;
               int G = 0;
               int B = 0;
               buffer.append("<ColorTable name=\"" + "GRASS output" + "\" interpolated=\"1\" version=\"1.1\">\n");
               while ((line = in.readLine()) != null) {
                  String token;
                  if (line.contains("FROM:")) {
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     from = Double.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     R = Integer.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     G = Integer.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     B = Integer.valueOf(token);
                     buffer.append("\t<Color value=\"" + from + "\"" + " name=\"\"" + " rgb=\"" + R + "," + G + "," + B + "\""
                                   + " interpolated=\"50.0\" />\n");
                     buffer.append("\t<Alpha value=\"" + from + "\"" + " alpha=\"255\"" + " interpolated=\"50.0\" />\n");
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     to = Double.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     R = Integer.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     G = Integer.valueOf(token);
                     line = in.readLine();
                     token = new String(line.substring(line.indexOf(":") + 1).trim());
                     B = Integer.valueOf(token);
                  }
               }
               /* write last interval and close color table definition */
               buffer.append("\t<Color value=\"" + to + "\"" + " name=\"\"" + " rgb=\"" + R + "," + G + "," + B + "\""
                             + " interpolated=\"50.0\" />\n");
               buffer.append("\t<Alpha value=\"" + to + "\"" + " alpha=\"255\"" + " interpolated=\"50.0\" />\n");
               buffer.append("</ColorTable>\n");
            }
            /* Write no data value */
            buffer.append("<NoData>\n");
            buffer.append("\t<Data value=\"" + SextanteGUI.getOutputFactory().getDefaultNoDataValue() + "\" type=\"2\"/>\n");
            buffer.append("</NoData>\n");

            /* Write XML footer */
            buffer.append("</RasterMetaFile>\n");

            /* Create new RMF */
            final File rmfFile = new File(filename + "." + "rmf");
            final BufferedWriter rmfOut = new BufferedWriter(new FileWriter(rmfFile));
            rmfOut.write(buffer.toString());
            rmfOut.close();

            /* DEBUG */
            //System.err.println ( buffer.toString() );
         }
      }
      catch (final Exception e) {
         //System.err.println ("*** DEBUG: I/O ERROR");
         Sextante.addErrorToLog(Sextante.getText("grass_error_color_table"));
      }
   }
}
