

package es.unex.sextante.inmemory.core;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.outputs.IOutputChannel;


public class InMemoryVectorLayer
         extends
            AbstractVectorLayer {


   private ArrayList<FeatureImpl> m_Features;
   private String[]               m_sFields;
   private Class[]                m_Types;
   private int                    m_iShapeType;


   public void create(final int shapeType,
                      final Class[] types,
                      final String[] fields) {

      m_Features = new ArrayList<FeatureImpl>();
      m_iShapeType = shapeType;
      m_Types = types;
      m_sFields = fields;

   }


   public void create(final ArrayList<FeatureImpl> features,
                      final Class[] types,
                      final String[] fields) {

      m_Features = features;
      m_Types = types;
      m_sFields = fields;
      if (features.size() != 0) {
         final Class<?> type = features.get(0).getClass();
         if (type.isAssignableFrom(Polygon.class) || type.isAssignableFrom(MultiPolygon.class)) {
            m_iShapeType = IVectorLayer.SHAPE_TYPE_POLYGON;
         }
         else if (type.isAssignableFrom(LineString.class) || type.isAssignableFrom(MultiLineString.class)) {
            m_iShapeType = IVectorLayer.SHAPE_TYPE_LINE;
         }
         else {
            m_iShapeType = IVectorLayer.SHAPE_TYPE_POINT;
         }
      }
      else {
         m_iShapeType = IVectorLayer.SHAPE_TYPE_POINT;
      }

   }


   @Override
   public Object getBaseDataObject() {

      return m_Features;

   }


   @Override
   public void addFeature(final Geometry geometry,
                          final Object[] attributes) {

      m_Features.add(new FeatureImpl(geometry, attributes));

   }


   @Override
   public boolean canBeEdited() {

      return true;

   }


   @Override
   public int getFieldCount() {

      return m_sFields.length;

   }


   @Override
   public String getFieldName(final int index) {

      return m_sFields[index];
   }


   @Override
   public Class getFieldType(final int index) {

      return m_Types[index];
   }


   @Override
   public int getShapeType() {

      return m_iShapeType;

   }


   @Override
   public IFeatureIterator iterator() {

      return new InMemoryFeatureIterator(m_Features.iterator());

   }


   @Override
   public Object getCRS() {

      return null;

   }


   @Override
   public void close() {
   }


   @Override
   public void free() {
   }


   @Override
   public String getName() {

      return "";

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return null;

   }


   @Override
   public void open() {
   }


   @Override
   public void postProcess() throws Exception {
   }


   @Override
   public void setName(final String name) {
   }


}
