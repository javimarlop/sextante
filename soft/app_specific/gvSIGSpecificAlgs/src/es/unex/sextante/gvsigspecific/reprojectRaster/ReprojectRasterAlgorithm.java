

package es.unex.sextante.gvsigspecific.reprojectRaster;

import org.cresques.cts.IProjection;
import org.gvsig.crs.Crs;
import org.gvsig.crs.CrsException;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.rastertools.reproject.Reproject;
import org.gvsig.rastertools.reproject.ReprojectException;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.RepeatedParameterNameException;
import es.unex.sextante.gvsig.core.EPSGCodes;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;


public class ReprojectRasterAlgorithm
         extends
            GeoAlgorithm {

   public static final String EPSG_INPUT  = "EPSG_INPUT";
   public static final String EPSG_OUTPUT = "EPSG_OUTPUT";
   public static final String OUTPUT      = "OUTPUT";
   public static final String INPUT       = "INPUT";


   @Override
   public void defineCharacteristics() {

      setName(Sextante.getText("Reproject"));
      setGroup(Sextante.getText("Basic_tools_for_raster_layers"));
      setUserCanDefineAnalysisExtent(false);
      setIsDeterminatedProcess(false);

      try {
         m_Parameters.addInputRasterLayer(INPUT, Sextante.getText("Layer"), true);
         final String[] codes = EPSGCodes.getCodes();
         m_Parameters.addSelection(EPSG_OUTPUT, Sextante.getText("EPSG_output"), codes);
         addOutputRasterLayer(OUTPUT, Sextante.getText("Result"));
      }
      catch (final RepeatedParameterNameException e) {
         e.printStackTrace();
      }

   }


   @Override
   public boolean processAlgorithm() throws GeoAlgorithmExecutionException {

      final IRasterLayer layer = m_Parameters.getParameterValueAsRasterLayer(INPUT);
      final FLyrRasterSE inputLayer = (FLyrRasterSE) layer.getBaseDataObject();
      String sProj = EPSGCodes.getCodes()[m_Parameters.getParameterValueAsInt(EPSG_OUTPUT)];
      sProj = sProj.substring(0, 10).trim();
      IProjection to;
      try {
         to = new Crs(sProj);
      }
      catch (final CrsException e) {
         throw new GeoAlgorithmExecutionException(Sextante.getText("Wrong_EPSG_code"));
      }
      final IProjection from = inputLayer.getProjection();

      final Output out = m_OutputObjects.getOutput(OUTPUT);
      final String sFilename = ((FileOutputChannel) getOutputChannel(out.getName())).getFilename();
      final Reproject reproject = new Reproject(inputLayer, sFilename);
      try {
         final int result = reproject.warp(to, from);
         if (result != 0) {
            throw new GeoAlgorithmExecutionException("Reprojection_error");
         }
      }
      catch (final ReprojectException e) {
         throw new GeoAlgorithmExecutionException(e.getMessage());
      }

      return !m_Task.isCanceled();

   }
}
