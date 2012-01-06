

package es.unex.sextante.arcgis.dataobjects;

import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ITable;

import es.unex.sextante.dataObjects.IRecord;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.dataObjects.RecordImpl;
import es.unex.sextante.exceptions.IteratorException;


public class ArcRecordsetIterator
         implements
            IRecordsetIterator {

   private final ITable m_Table;
   private int          m_iRecord;
   private int          m_iTotal;


   public ArcRecordsetIterator(final ITable table) {

      m_Table = table;
      m_iRecord = 0;
      try {
         m_iTotal = table.rowCount(null);
      }
      catch (final Exception e) {
         m_iTotal = 0;
      }

   }


   @Override
   public boolean hasNext() {

      return m_iRecord < m_iTotal;

   }


   @Override
   public IRecord next() throws IteratorException {

      try {
         final IRow row = m_Table.getRow(m_iRecord);
         final Object[] obj = new Object[row.getFields().getFieldCount() - 1];
         for (int i = 0; i < obj.length; i++) {
            obj[i] = row.getValue(i + 1);
         }
         final RecordImpl record = new RecordImpl(obj);
         m_iRecord++;
         return record;
      }
      catch (final Exception e) {
         throw new IteratorException();
      }

   }


   @Override
   public void close() {

   }

}
