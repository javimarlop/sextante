

package es.unex.sextante.gvsig.core;

import java.io.File;
import java.sql.Types;
import java.util.BitSet;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DXFLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.dataObjects.vectorFilters.SelectionFilter;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;
import es.unex.sextante.outputs.OverwriteOutputChannel;


public class gvVectorLayer
         extends
            AbstractVectorLayer {

   private static final int  PRECISION = 5;

   private String            m_sFilename;
   private IWriter           m_Writer;
   private int               m_iGeometry;
   private String            m_sName;
   private IProjection       m_Projection;
   private ReadableVectorial m_RV;
   private FLyrVect          m_Layer;
   private int               m_iShapeType;
   private boolean           m_bDoNotPostprocess;
   private IOutputChannel    m_Channel;


   public void create(final String sName,
                      final IOutputChannel oc,
                      final int iShapeType,
                      final Class[] types,
                      final String[] sFields,
                      final Object crs) {

      int iTypes[];
      LayerDefinition tableDef;

      m_Channel = oc;
      setFilenames(oc);

      m_sName = sName;
      m_iGeometry = 0;
      m_Projection = (IProjection) crs;
      m_iShapeType = iShapeType;

      try {

         if (m_sFilename.toLowerCase().endsWith("dxf")) {
            m_Writer = new DxfWriter();
            ((DxfWriter) m_Writer).setFile(new File(m_sFilename));
            ((DxfWriter) m_Writer).setProjection((IProjection) crs);
            tableDef = new DXFLayerDefinition();
            tableDef.setShapeType(getgvSIGShapeType(iShapeType));

            final DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
            ((DxfWriter) m_Writer).setFieldMapping(fieldsMapping);
         }
         else {
            m_Writer = new ShpWriter();
            ((ShpWriter) m_Writer).setFile(new File(m_sFilename));
            tableDef = new SHPLayerDefinition();
            tableDef.setShapeType(getgvSIGShapeType(iShapeType));
         }

         iTypes = DataTools.getgvSIGTypes(types);

         final FieldDescription[] fields = new FieldDescription[sFields.length];
         for (int i = 0; i < fields.length; i++) {
            fields[i] = new FieldDescription();
            fields[i].setFieldName(sFields[i]);
            fields[i].setFieldType(iTypes[i]);
            fields[i].setFieldLength(getDataTypeLength(iTypes[i]));
            if (iTypes[i] == Types.DOUBLE) {
               fields[i].setFieldDecimalCount(PRECISION);
            }
         }
         tableDef.setFieldsDesc(fields);
         tableDef.setName(m_sFilename);

         m_Writer.initialize(tableDef);
         m_Writer.preProcess();

         m_RV = null;

      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
      }

   }


   private void setFilenames(final IOutputChannel oc) {

      m_bDoNotPostprocess = false;
      if (oc instanceof FileOutputChannel) {
         m_sFilename = ((FileOutputChannel) oc).getFilename();
      }
      else if (oc instanceof OverwriteOutputChannel) {
         final IOutputChannel oc2 = ((OverwriteOutputChannel) oc).getLayer().getOutputChannel();
         if (oc2 instanceof FileOutputChannel) {
            m_sFilename = ((FileOutputChannel) oc2).getFilename();
         }
         else {
            //should not reach this, since we are checking it before.
         }
      }
      else if (oc instanceof NullOutputChannel) {
         //use a temporary file
         m_sFilename = SextanteGUI.getOutputFactory().getTempVectorLayerFilename();
         m_bDoNotPostprocess = true;
      }

   }


   public void create(final FLyrVect flayer) {

      m_Layer = flayer;
      try {
         m_RV = m_Layer.getSource();
         m_Projection = m_Layer.getProjection();
         final FBitSet fbitset = m_RV.getRecordset().getSelection();
         final BitSet bitset = (BitSet) fbitset.clone();
         final SelectionFilter filter = new SelectionFilter(bitset);
         addFilter(filter);
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   public void open() {

      if (m_RV != null) {
         try {
            m_RV.start();
         }
         catch (final InitializeDriverException e) {
            e.printStackTrace();
         }
         catch (final ReadDriverException e) {
            e.printStackTrace();
         }
      }
      else if ((m_sFilename != null) && new File(m_sFilename).exists()) {
         final FLyrVect flayer = (FLyrVect) FileTools.openLayer(m_sFilename, m_sName, m_Projection);
         create(flayer);
         open();
      }

   }


   public void close() {

      if (m_RV != null) {
         try {
            m_RV.stop();
         }
         catch (final ReadDriverException e) {
            e.printStackTrace();
         }
      }

      m_Writer = null;

   }


   /**
    * Returns the length of field
    * 
    * @param dataType
    * @return length of field
    */
   public int getDataTypeLength(final int dataType) {

      switch (dataType) {
         case Types.NUMERIC:
         case Types.DOUBLE:
         case Types.REAL:
         case Types.FLOAT:
         case Types.BIGINT:
         case Types.INTEGER:
         case Types.DECIMAL:
            return 20;
         case Types.CHAR:
         case Types.VARCHAR:
         case Types.LONGVARCHAR:
            return 254;
         case Types.DATE:
            return 8;
         case Types.BOOLEAN:
         case Types.BIT:
            return 1;
      }
      return 0;

   }


   public void addFeature(Geometry geom,
                          final Object[] values) {

      if (m_Writer == null) {
         return;
      }

      //There is a problem in gvSIG's FConverter, which does not support JTS MultiPoints.
      //This is a temporal solution to produce a result in that case (when reading from an input layer, even single points are handled by SEXTANTE as MultiPoints)
      if (geom instanceof MultiPoint) {
         final GeometryFactory gf = new GeometryFactory();
         final Coordinate[] coords = geom.getCoordinates();
         if (coords.length > 0) {
            geom = gf.createPoint(coords[0]);
         }
      }

      final IGeometry iGeo = FConverter.jts_to_igeometry(geom);
      final Value[] gvSIGValues = DataTools.getGVSIGValues(values);
      final DefaultFeature feat = new DefaultFeature(iGeo, gvSIGValues, Integer.toString(m_iGeometry));
      final IRowEdited editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, m_iGeometry);
      m_iGeometry++;
      try {
         m_Writer.process(editFeat);
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   public String getFieldName(final int i) {

      if (m_RV != null) {
         try {
            return m_RV.getRecordset().getFieldName(i);
         }
         catch (final ReadDriverException e) {
            e.printStackTrace();
            return null;
         }
      }
      else if (m_Writer != null) {
         try {
            return m_Writer.getTableDefinition().getFieldsDesc()[i].getFieldName();
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return "";
         }
      }

      return ""; //TODO

   }


   public Class getFieldType(final int i) {

      if (m_RV != null) {
         try {
            return DataTools.getTypeClass(m_RV.getRecordset().getFieldType(i));
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return Object.class;

         }
      }
      else if (m_Writer != null) {
         try {
            return DataTools.getTypeClass(m_Writer.getTableDefinition().getFieldsDesc()[i].getFieldType());
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return Object.class;
         }
      }

      return Object.class; //TODO

   }


   public int getFieldCount() {

      if (m_RV != null) {
         try {
            return m_RV.getRecordset().getFieldCount();
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return 0;
         }
      }
      else if (m_Writer != null) {
         try {
            return m_Writer.getTableDefinition().getFieldsDesc().length;
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return 0;
         }
      }

      return 0; //TODO

   }


   public int getShapeType() {

      if (m_RV != null) {
         try {
            return getShapeTypeFromGvSIGShapeType(m_RV.getShapeType());
         }
         catch (final ReadDriverException e) {
            Sextante.addErrorToLog(e);
            return IVectorLayer.SHAPE_TYPE_WRONG;
         }
      }
      else if (m_Writer != null) {
         return m_iShapeType;
      }

      return IVectorLayer.SHAPE_TYPE_WRONG;

   }


   private int getShapeTypeFromGvSIGShapeType(final int shapeType) {

      switch (shapeType % FShape.Z % FShape.M) {
         case FShape.POLYGON:
            return IVectorLayer.SHAPE_TYPE_POLYGON;
         case FShape.LINE:
            return IVectorLayer.SHAPE_TYPE_LINE;
         case FShape.POINT:
            return IVectorLayer.SHAPE_TYPE_POINT;
         case FShape.MULTIPOINT:
            return IVectorLayer.SHAPE_TYPE_POINT;
         default:
            return IVectorLayer.SHAPE_TYPE_WRONG;
      }

   }


   private int getgvSIGShapeType(final int shapeType) {

      switch (shapeType) {
         case IVectorLayer.SHAPE_TYPE_POLYGON:
            return FShape.POLYGON;
         case IVectorLayer.SHAPE_TYPE_LINE:
            return FShape.LINE;
         case IVectorLayer.SHAPE_TYPE_POINT:
            return FShape.POINT;
         default:
            return FShape.POLYGON;
      }

   }


   public String getName() {

      if (m_Layer != null) {
         return m_Layer.getName();
      }
      else {
         return m_sName;
      }

   }


   public void postProcess() {

      if (!m_bDoNotPostprocess) {
         if (m_Writer == null) {
            return;
         }
         try {
            m_Writer.postProcess();
         }
         catch (final StopWriterVisitorException e) {
            Sextante.addErrorToLog(e);
         }
      }

   }


   public IOutputChannel getOutputChannel() {

      if (m_RV != null) {
         if (m_RV instanceof VectorialFileAdapter) {
            return new FileOutputChannel(((VectorialFileAdapter) m_RV).getFile().getAbsolutePath());
         }
         else {
            return null;
         }
      }
      else {
         return m_Channel;
      }

   }


   public Object getCRS() {

      return m_Projection;

   }


   public void setName(final String name) {

      if (m_Layer != null) {
         m_Layer.setName(name);
      }
      m_sName = name;

   }


   @Override
   public IFeatureIterator iterator() {

      if (m_Layer != null) {
         return new gvFeatureIterator(m_Layer, getFilters());
      }
      else {
         return new gvFeatureIterator(); //cannot iterate layers being edited
      }

   }


   @Override
   public Object getBaseDataObject() {

      return m_Layer;

   }


   @Override
   public void free() {

      m_Layer = null;
      m_RV = null;
      m_Writer = null;

   }


   @Override
   public boolean canBeEdited() {

      //we support only file-based, so we can overwrite
      return true;

   }


}
