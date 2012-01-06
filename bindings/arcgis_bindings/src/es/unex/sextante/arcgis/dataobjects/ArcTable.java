

package es.unex.sextante.arcgis.dataobjects;

import java.io.File;
import java.io.IOException;

import com.esri.arcgis.carto.IStandaloneTable;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.Fields;
import com.esri.arcgis.geodatabase.ICursor;
import com.esri.arcgis.geodatabase.IRowBuffer;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.geodatabase.Table;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.esriFieldType;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractTable;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class ArcTable
         extends
            AbstractTable {

   private String     m_sName;
   private String     m_sFilename;
   private ITable     m_Table;
   private ICursor    m_Insert;
   private boolean    m_bIsEditable;
   private IRowBuffer m_Buffer;
   private int        m_iRowsAdded;


   public void create(final String sName,
                      final String sFilename,
                      final String[] sFields,
                      final Class[] types) {

      try {
         m_sFilename = sFilename;
         m_sName = sName;
         final File file = new File(m_sFilename);

         final ShapefileWorkspaceFactory shapefileWorkspaceFactory = new ShapefileWorkspaceFactory();
         final Workspace workspace = (Workspace) shapefileWorkspaceFactory.openFromFile(file.getParent(), 0);

         final Fields fields = new Fields();
         for (int i = 0; i < sFields.length; i++) {
            final Field field = new Field();
            field.setLength(30); //TODO:******
            field.setName(sFields[i]);
            field.setEditable(true);
            field.setType(getArcFieldTypeFromClass(types[i]));
            fields.addField(field);
         }

         m_Table = new Table(workspace.createTable(file.getName(), fields, null, null, ""));
         m_Insert = m_Table.insert(true);
         m_Buffer = m_Table.createRowBuffer();
         m_bIsEditable = true;
         m_iRowsAdded = 0;
      }
      catch (final IOException e) {
         e.printStackTrace();
      }


   }


   public void create(final IStandaloneTable table) {

      try {
         m_Table = table.getTable();
         m_sName = table.getName();
         m_bIsEditable = false;
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public void addRecord(final Object[] values) {

      if (m_bIsEditable) {
         try {
            for (int i = 0; i < values.length; i++) {
               m_Buffer.setValue(i + 1, values[i]);
            }
            m_Insert.insertRow(m_Buffer);
            if (++m_iRowsAdded % 100 == 0) {
               m_Insert.flush();
            }
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
         }
      }

   }


   @Override
   public IRecordsetIterator iterator() {

      return new ArcRecordsetIterator(m_Table);

   }


   @Override
   public String getFieldName(final int i) {


      try {
         return m_Table.getFields().getField(i + 1).getName();
      }
      catch (final Exception e) {
         return "";
      }


   }


   @Override
   public Class<?> getFieldType(final int i) {

      try {
         return getClassFromArcFieldType(m_Table.getFields().getField(i + 1).getType());
      }
      catch (final Exception e) {
         return String.class;
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
   public int getFieldCount() {

      try {
         return m_Table.getFields().getFieldCount();
      }
      catch (final Exception e) {
         return 0;
      }

   }


   @Override
   public long getRecordCount() {


      try {
         return m_Table.rowCount(null);
      }
      catch (final Exception e) {
         return 0;
      }

   }


   @Override
   public void close() {

   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public void open() {

   }


   private int getArcFieldTypeFromClass(final Class clazz) {

      if (clazz.equals(Integer.class) || clazz.equals(Long.class)) {
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


   @Override
   public void postProcess() {

      try {
         m_Insert.flush();
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
      }

   }


   @Override
   public void setName(final String name) {

      m_sName = name;

   }


   @Override
   public void free() {
   }


   @Override
   public Object getBaseDataObject() {

      return m_Table;

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }


}
