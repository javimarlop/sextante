

package es.unex.sextante.arcgis.dataobjects;

import java.io.File;
import java.io.IOException;

import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.FeatureClass;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.Fields;
import com.esri.arcgis.geodatabase.GeometryDef;
import com.esri.arcgis.geodatabase.IFeatureBuffer;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.esriFeatureType;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.esriGeometryType;
import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class ArcVectorLayer
         extends
            AbstractVectorLayer {

   private FeatureLayer   m_Layer;
   private String         m_sFilename;
   private String         m_sName;
   private boolean        m_bIsEditable;
   private IFeatureBuffer m_ipFB;
   private IFeatureCursor m_ipFC;
   private int            m_iFeaturesAdded;


   public void create(final FeatureLayer layer,
                      final String sFilename) {

      m_Layer = layer;
      m_bIsEditable = false;
      m_sFilename = sFilename;
      try {
         m_sName = layer.getName();
      }
      catch (final Exception e) {
      }

   }


   public void create(final String sName,
                      final int iShapeType,
                      final Class[] types,
                      final String[] sFields,
                      final IOutputChannel channel,
                      final Object crs) {

      m_sFilename = ((FileOutputChannel) channel).getFilename();
      final File file = new File(m_sFilename);
      try {

         final String geometryShapeFieldName = "Shape";

         final ShapefileWorkspaceFactory shapefileWorkspaceFactory = new ShapefileWorkspaceFactory();
         final Workspace workspace = (Workspace) shapefileWorkspaceFactory.openFromFile(file.getParent(), 0);

         final GeometryDef geometryDef = new GeometryDef();
         geometryDef.setGeometryType(getArcGeomTypeFromSextanteGeomType(iShapeType));
         geometryDef.setSpatialReferenceByRef((ISpatialReference) crs);
         geometryDef.setGridCount(1);
         geometryDef.setGridSize(0, 0);
         geometryDef.setHasM(false);
         geometryDef.setHasZ(false);

         final Field geometryShapeField = new Field();
         geometryShapeField.setName(geometryShapeFieldName);
         geometryShapeField.setEditable(true);
         geometryShapeField.setType(esriFieldType.esriFieldTypeGeometry);
         geometryShapeField.setGeometryDefByRef(geometryDef);

         final Fields fields = new Fields();
         fields.addField(geometryShapeField);
         for (int i = 0; i < sFields.length; i++) {
            final Field field = new Field();
            field.setLength(30); //TODO:******
            field.setName(sFields[i]);
            field.setEditable(true);
            field.setType(getArcFieldTypeFromClass(types[i]));
            fields.addField(field);
         }

         final FeatureClass featureClass = new FeatureClass(workspace.createFeatureClass(file.getName(), fields, null, null,
                  esriFeatureType.esriFTSimple, geometryShapeFieldName, ""));

         m_Layer = new FeatureLayer();
         m_Layer.setFeatureClassByRef(featureClass);
         m_sName = m_sFilename;
         m_Layer.setName(sName);
         m_bIsEditable = true;

         m_ipFC = featureClass.IFeatureClass_insert(true);
         m_ipFB = featureClass.createFeatureBuffer();
         m_iFeaturesAdded = 0;

      }
      catch (final IOException e) {
         Sextante.addErrorToLog(e);
      }

   }


   private int getArcFieldTypeFromClass(final Class clazz) {

      if (clazz.equals(Integer.class)) {
         return esriFieldType.esriFieldTypeInteger;
      }
      else if (clazz.equals(Double.class) || clazz.equals(Float.class)) {
         return esriFieldType.esriFieldTypeDouble;
      }
      else if (clazz.equals(String.class)) {
         return esriFieldType.esriFieldTypeString;
      }

      return esriFieldType.esriFieldTypeString;

   }


   private int getSextanteGeomTypeFromArcGeomType(final int shapeType) {

      switch (shapeType) {
         case esriGeometryType.esriGeometryMultipoint:
         case esriGeometryType.esriGeometryPoint:
            return IVectorLayer.SHAPE_TYPE_POINT;
         case esriGeometryType.esriGeometryLine:
         case esriGeometryType.esriGeometryPolyline:
            return IVectorLayer.SHAPE_TYPE_LINE;
         case esriGeometryType.esriGeometryPolygon:
         default:
            return IVectorLayer.SHAPE_TYPE_POLYGON;
      }

   }


   private int getArcGeomTypeFromSextanteGeomType(final int shapeType) {

      switch (shapeType) {
         case IVectorLayer.SHAPE_TYPE_POINT:
            return esriGeometryType.esriGeometryMultipoint;
         case IVectorLayer.SHAPE_TYPE_LINE:
            return esriGeometryType.esriGeometryPolyline;
         case IVectorLayer.SHAPE_TYPE_POLYGON:
         default:
            return esriGeometryType.esriGeometryPolygon;
      }

   }


   @Override
   public Object getBaseDataObject() {

      return m_Layer;

   }


   @Override
   public void addFeature(final Geometry geometry,
                          final Object[] attributes) {

      if (m_bIsEditable) {
         try {
            final IGeometry geom = GeometryTools.toArc(geometry);
            m_ipFB.setShapeByRef(geom);
            for (int i = 0; i < attributes.length; i++) {
               m_ipFB.setValue(i + 2, attributes[i]);
            }
            m_ipFC.insertFeature(m_ipFB);
            if (++m_iFeaturesAdded % 100 == 0) {
               m_ipFC.flush();
            }
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
         }
      }

   }


   @Override
   public boolean canBeEdited() {

      return false;

   }


   @Override
   public int getFieldCount() {

      try {
         return m_Layer.getFieldCount() - 2;
      }
      catch (final Exception e) {
         return 0;
      }
   }


   @Override
   public String getFieldName(final int index) {

      try {
         return m_Layer.getFieldInfo(index + 2).getAlias();
      }
      catch (final Exception e) {
         return "";
      }

   }


   @Override
   public Class getFieldType(final int index) {

      try {
         return getClassFromArcFieldType(m_Layer.getFields().getField(index + 2).getType());
      }
      catch (final Exception e) {
         return Object.class;
      }

   }


   private Class<?> getClassFromArcFieldType(final int iType) {

      switch (iType) {
         case esriFieldType.esriFieldTypeInteger:
            return Integer.class;
         case esriFieldType.esriFieldTypeDouble:
            return Double.class;
         case esriFieldType.esriFieldTypeString:
         default:
            return String.class;
      }

   }


   @Override
   public int getShapeType() {

      try {
         return getSextanteGeomTypeFromArcGeomType(m_Layer.getFeatureClass().getShapeType());
      }
      catch (final Exception e) {
         return IVectorLayer.SHAPE_TYPE_WRONG;
      }

   }


   @Override
   public IFeatureIterator iterator() {

      try {
         return new ArcFeatureIterator(m_Layer);
      }
      catch (final Exception e) {
         return new ArcFeatureIterator();
      }

   }


   @Override
   public Object getCRS() {

      try {
         return m_Layer.getSpatialReference();
      }
      catch (final Exception e) {
         return null;
      }

   }


   @Override
   public void close() {
   }


   @Override
   public void free() {
   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }


   @Override
   public void open() {
   }


   @Override
   public void postProcess() throws Exception {

      m_ipFC.flush();

   }


   public void setName(final String sName) {

      //      try {
      //         m_sName = name;
      //         if (m_Layer != null) {
      //            m_Layer.setName(name);
      //         }
      //      }
      //      catch (final Exception e) {
      //      }

   }


}
