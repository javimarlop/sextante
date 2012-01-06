/*******************************************************************************
GTDiskTable.java
Copyright (C) 2009 ETC-LUSI http://etc-lusi.eionet.europa.eu/

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *******************************************************************************/
package es.unex.sextante.geotools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.geotools.data.shapefile.dbf.DbaseFileException;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileWriter;

import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractTable;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;

/**
 * 
 * @author Cesar Martinez Izquierdo
 */
public class GTDiskTable
         extends
            AbstractTable {

   private String          m_sName;
   private String          m_sFilename;
   private int             m_numRows;
   private Class[]         m_fieldTypes;
   private String[]        m_fieldNames;
   private DbaseFileWriter writer;
   private DbaseFileHeader header;
   private FileChannel     outChannel;
   private DbaseFileReader m_BaseDataObject;


   public void addRecord(final Object[] values) {
      if (writer != null) {
         try {
            writer.write(values);
            m_numRows++;
         }
         catch (final DbaseFileException e) {
            Sextante.addErrorToLog(e);
         }
         catch (final IOException e) {
            Sextante.addErrorToLog(e);
         }
      }
      else {
         throw new RuntimeException(
                  "Method: create(String sName, String sFilename, Class[] types, String[] sFields) should be called before adding records.");
      }
   }


   public void create(final String sName,
                      final String sFilename,
                      final Class<?>[] fields,
                      final String[] sFields) {

      try {
         m_sFilename = sFilename;
         m_sName = sName;
         this.m_fieldNames = sFields;
         this.m_fieldTypes = fields;
         m_BaseDataObject = null;
         m_numRows = 0;
         header = createDbaseHeader(sFields, fields);
         writer = getWriter(header);
         ///////
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
      }

   }


   private DbaseFileWriter getWriter(final DbaseFileHeader header) {
      DbaseFileWriter writer = null;
      try {
         outChannel = new FileOutputStream(m_sFilename).getChannel();
         writer = new DbaseFileWriter(header, outChannel);
      }
      catch (final DbaseFileException e) {
         Sextante.addErrorToLog(e);
      }
      catch (final IOException e) {
         Sextante.addErrorToLog(e);
      }
      return writer;
   }


   private DbaseFileReader getReader() {
      try {
         final FileInputStream fis = new FileInputStream(m_sFilename);
         final FileChannel channel = fis.getChannel();
         return new DbaseFileReader(channel, false, Charset.forName("ISO-8859-1"));
      }
      catch (final IOException e) {
         Sextante.addErrorToLog(e);
      }
      return null;
   }


   public IRecordsetIterator iterator() {
      if (m_BaseDataObject != null) {
         return new GTDbfIterator(getReader());
      }
      else {
         throw new RuntimeException(
                  "Method open() [and postproces() if an editing session is active] must be called before reading the table contents.");
      }
   }


   public String getFieldName(final int i) {
      if ((m_BaseDataObject != null) && (m_BaseDataObject instanceof DbaseFileReader)) {
         final DbaseFileReader reader = m_BaseDataObject;
         return reader.getHeader().getFieldName(i);
      }
      else {
         return m_fieldNames[i];
      }
   }


   public Class getSextanteType(final char dbfType,
                                final int decimalCount) {
      switch (dbfType) {
         case 'N':
         case 'n':
            return (decimalCount == 0 ? Integer.class : Double.class);
         case 'F':
         case 'f':
            return Double.class;
         case 'D':
         case 'd':
            return java.util.Date.class;
         case 'L':
         case 'l':
            return Boolean.class;
         case 'C':
         case 'c':
         default:
            return CharSequence.class;
      }
   }


   public Class<?> getFieldType(final int i) {
      if ((m_BaseDataObject != null) && (m_BaseDataObject instanceof DbaseFileReader)) {
         final DbaseFileReader reader = m_BaseDataObject;
         return getSextanteType(reader.getHeader().getFieldType(i), reader.getHeader().getFieldDecimalCount(i));
      }
      else {
         return m_fieldTypes[i];
      }
   }


   public int getFieldCount() {
      if ((m_BaseDataObject != null) && (m_BaseDataObject instanceof DbaseFileReader)) {
         final DbaseFileReader reader = m_BaseDataObject;
         return reader.getHeader().getNumFields();
      }
      else {
         return m_fieldNames.length;
      }
   }


   public long getRecordCount() {
      if ((m_BaseDataObject != null) && (m_BaseDataObject instanceof DbaseFileReader)) {
         final DbaseFileReader reader = m_BaseDataObject;
         return reader.getHeader().getNumRecords();
      }
      else {
         return m_numRows;
      }
   }


   public void close() {
      if ((m_BaseDataObject != null) && (m_BaseDataObject instanceof DbaseFileReader)) {
         final DbaseFileReader reader = m_BaseDataObject;
         try {
            reader.close();
            m_BaseDataObject = null;
         }
         catch (final IOException e) {
            Sextante.addErrorToLog(e);
         }
      }
   }


   public String getName() {

      return m_sName;
   }


   public void open() {
      m_BaseDataObject = getReader();
   }


   public void postProcess() {
      if (writer != null) {
         try {
            // TODO: remove this lines when bug #GEOT-2794 gets solved in GeoTools
            header.setNumRecords(this.m_numRows);
            outChannel.position(0);
            header.writeHeader(outChannel);
            // end TODO

            writer.close();
         }
         catch (final IOException e) {
            Sextante.addErrorToLog(e);
         }
      }
   }


   protected DbaseFileHeader createDbaseHeader(final String fieldNames[],
                                               final Class fieldTypes[]) throws IOException, DbaseFileException {

      final DbaseFileHeader header = new DbaseFileHeader();

      for (int i = 0, count = fieldNames.length; i < count; i++) {
         final Class colType = fieldTypes[i];
         final String colName = fieldNames[i];

         if ((colType == Integer.class) || (colType == Short.class) || (colType == Byte.class)) {
            header.addColumn(colName, 'N', 10, 0);
         }
         else if (colType == Long.class) {
            header.addColumn(colName, 'N', 19, 0);
         }
         else if ((colType == Double.class) || (colType == Float.class) || (colType == Number.class)) {
            header.addColumn(colName, 'N', 33, 16);
         }
         else if (java.util.Date.class.isAssignableFrom(colType)) {
            header.addColumn(colName, 'D', 8, 0);
         }
         else if (colType == Boolean.class) {
            header.addColumn(colName, 'L', 1, 0);
         }
         else if (CharSequence.class.isAssignableFrom(colType)) {
            header.addColumn(colName, 'C', 254, 0);
         }
         else if (Geometry.class.isAssignableFrom(colType)) {
            continue;
         }
         else {
            throw new IOException("Unable to write : " + colType.getName());
         }
      }
      return header;
   }


   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }


   public void setName(final String name) {

      m_sName = name;

   }


   @Override
   public void free() {}


   @Override
   public Object getBaseDataObject() {

      return m_BaseDataObject;

   }

}
