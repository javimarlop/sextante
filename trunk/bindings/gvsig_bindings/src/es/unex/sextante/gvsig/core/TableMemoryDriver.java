package es.unex.sextante.gvsig.core;

import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;

public class TableMemoryDriver
         implements
            ObjectDriver {

   private DefaultTableModel m_TableModel;
   private int               m_FieldTypes[];


   public TableMemoryDriver(final String[] sNames,
                            final int[] iTypes) {

      super();

      m_TableModel = new DefaultTableModel(sNames, 0);
      m_FieldTypes = iTypes;

   }


   /**
    * Devuelve el modelo de la tabla.
    *
    * @return modelo de la tabla.
    */
   public DefaultTableModel getTableModel() {

      return m_TableModel;

   }


   /**
    * A�ade una fila.
    *
    * @param row
    *                fila.
    */
   public void addRow(final Object[] row) {

      m_TableModel.addRow(row);

   }


   /* (non-Javadoc)
    * @see com.hardcode.driverManager.Driver#getName()
    */
   public String getName() {

      return "Table Memory Driver";

   }


   /**
    * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldType(int)
    */
   public int getFieldType(final int i) {

      if (i >= 0 && i < m_FieldTypes.length) {
         return m_FieldTypes[i];
      }
      return 0;
   }


   public boolean isWritable() {

      return true;

   }


   public void setFieldTypes(final int[] types) {

      m_FieldTypes = types;

   }


   /* (non-Javadoc)
    * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldValue(long, int)
    */
   public Value getFieldValue(final long rowIndex,
                              final int fieldId) {

      return (Value) m_TableModel.getValueAt((int) rowIndex, fieldId);

   }


   /* (non-Javadoc)
    * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldCount()
    */
   public int getFieldCount() {
      return m_TableModel.getColumnCount();
   }


   /* (non-Javadoc)
    * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldName(int)
    */
   public String getFieldName(final int fieldId) {
      return m_TableModel.getColumnName(fieldId);
   }


   /* (non-Javadoc)
    * @see com.hardcode.gdbms.engine.data.ReadDriver#getRowCount()
    */
   public long getRowCount() {
      return m_TableModel.getRowCount();
   }


   /* (non-Javadoc)
    * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
    */
   public void setDataSourceFactory(final DataSourceFactory dsf) {}


   /* (non-Javadoc)
    * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#reLoad()
    */
   public void reLoad() throws IOException {

      m_TableModel = new DefaultTableModel();

   }


   public int getFieldWidth(final int fieldId) {

      // TODO
      return 30;
   }


   public int[] getPrimaryKeys() {
      return null;
   }


   public void write(final DataWare dataWare) {

   }


   public void reload() {

      m_TableModel = new DefaultTableModel();

   }

}
