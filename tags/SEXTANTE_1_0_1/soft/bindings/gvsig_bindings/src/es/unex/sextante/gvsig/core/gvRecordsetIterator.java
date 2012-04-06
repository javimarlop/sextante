package es.unex.sextante.gvsig.core;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

import es.unex.sextante.dataObjects.IRecord;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.dataObjects.RecordImpl;

public class gvRecordsetIterator
         implements
            IRecordsetIterator {

   private final Object m_Object;
   long           m_iIndex;


   public gvRecordsetIterator(final Object obj) {

      m_Object = obj;
      m_iIndex = 0;

   }


   public boolean hasNext() {

      long iRecordCount = 0;
      try {
         if (m_Object instanceof TableMemoryDriver) {
            final TableMemoryDriver table = (TableMemoryDriver) m_Object;
            iRecordCount = table.getRowCount();
         }
         else {
            final ProjectTable table = (ProjectTable) m_Object;
            iRecordCount = table.getModelo().getRowCount();
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

      return iRecordCount > m_iIndex;

   }


   public IRecord next() {

      IRecord record = null;
      int iFieldCount;
      try {
         if (m_Object instanceof TableMemoryDriver) {
            final TableMemoryDriver table = (TableMemoryDriver) m_Object;
            iFieldCount = table.getFieldCount();
            final Object[] obj = new Object[iFieldCount];
            for (int i = 0; i < iFieldCount; i++) {
               obj[i] = table.getFieldValue(m_iIndex, i);
            }
            record = new RecordImpl(obj);
         }
         else {
            final ProjectTable table = (ProjectTable) m_Object;
            final Value[] obj = table.getModelo().getRecordset().getRow(m_iIndex);
            record = new RecordImpl(DataTools.getSextanteValues(obj));
         }
      }
      catch (final Exception e) {}

      m_iIndex++;

      return record;

   }


   public void close() {}

}
