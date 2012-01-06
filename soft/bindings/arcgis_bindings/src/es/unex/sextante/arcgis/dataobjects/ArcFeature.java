

package es.unex.sextante.arcgis.dataobjects;

import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IRecord;


public class ArcFeature
         implements
            IFeature {

   private final com.esri.arcgis.geodatabase.IFeature m_Feature;


   public ArcFeature(final com.esri.arcgis.geodatabase.IFeature feature) {

      m_Feature = feature;

   }


   @Override
   public Geometry getGeometry() {

      try {
         return GeometryTools.toJTS(m_Feature.getShape());
      }
      catch (final Exception e) {
         return null;
         //TODO improve this;
      }
   }


   @Override
   public IRecord getRecord() {


      try {
         final Object[] values = new Object[m_Feature.getFields().getFieldCount() - 2];
         for (int i = 0; i < values.length; i++) {
            values[i] = m_Feature.getValue(i + 2);
         }
         return new ArcRecord(values);
      }
      catch (final Exception e) {
         return null;
         //TODO improve this;
      }
   }

}
