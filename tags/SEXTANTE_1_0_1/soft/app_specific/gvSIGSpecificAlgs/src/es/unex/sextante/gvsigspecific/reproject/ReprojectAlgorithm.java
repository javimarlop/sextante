

package es.unex.sextante.gvsigspecific.reproject;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.crs.Crs;
import org.gvsig.crs.CrsException;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;

import es.unex.sextante.additionalInfo.AdditionalInfoVectorLayer;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.dataObjects.vectorFilters.BoundingBoxFilter;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.RepeatedParameterNameException;
import es.unex.sextante.gvsig.core.DataTools;
import es.unex.sextante.gvsig.core.EPSGCodes;


public class ReprojectAlgorithm
         extends
            GeoAlgorithm {

   public static final String EPSG_INPUT  = "EPSG_INPUT";
   public static final String EPSG_OUTPUT = "EPSG_OUTPUT";
   public static final String OUTPUT      = "OUTPUT";
   public static final String INPUT       = "INPUT";


   @Override
   public void defineCharacteristics() {

      setName(Sextante.getText("Reproject"));
      setGroup(Sextante.getText("Tools_for_vector_layers"));
      setUserCanDefineAnalysisExtent(true);

      try {
         m_Parameters.addInputVectorLayer(INPUT, Sextante.getText("Layer"), AdditionalInfoVectorLayer.SHAPE_TYPE_ANY, true);
         final String[] codes = EPSGCodes.getCodes();
         m_Parameters.addSelection(EPSG_OUTPUT, Sextante.getText("EPSG_output"), codes);
         addOutputVectorLayer(OUTPUT, Sextante.getText("Result"));
      }
      catch (final RepeatedParameterNameException e) {
         e.printStackTrace();
      }

   }


   @Override
   public boolean processAlgorithm() throws GeoAlgorithmExecutionException {

      try {
         final IVectorLayer layer = m_Parameters.getParameterValueAsVectorLayer(INPUT);
         if (!m_bIsAutoExtent) {
            layer.addFilter(new BoundingBoxFilter(m_AnalysisExtent));
         }
         final FLyrVect inputLayer = (FLyrVect) layer.getBaseDataObject();
         String sProj = EPSGCodes.getCodes()[m_Parameters.getParameterValueAsInt(EPSG_OUTPUT)];
         sProj = sProj.substring(0, 10).trim();
         IProjection from;
         try {
            from = new Crs(sProj);
         }
         catch (final CrsException e) {
            throw new GeoAlgorithmExecutionException(Sextante.getText("Wrong_EPSG_code"));
         }
         final IProjection to = inputLayer.getProjection();
         final ICoordTrans ct = from.getCT(to);

         final IVectorLayer output = getNewVectorLayer(OUTPUT, layer.getName(), layer.getShapeType(), layer.getFieldTypes(),
                  layer.getFieldNames());
         final ReadableVectorial source = inputLayer.getSource();
         final int iShapeCount = source.getShapeCount();
         for (int i = 0; (i < iShapeCount) && setProgress(i, iShapeCount); i++) {
            final IGeometry geom = source.getShape(i);
            final IGeometry clone = geom.cloneGeometry();
            clone.reProject(ct);
            final Value[] gvSIGValues = source.getRecordset().getRow(i);
            final Object[] values = DataTools.getSextanteValues(gvSIGValues);
            output.addFeature(clone.toJTSGeometry(), values);
         }

         return !m_Task.isCanceled();
      }
      catch (final Exception e) {
         throw new GeoAlgorithmExecutionException("Reprojection_error");
      }

   }

}
