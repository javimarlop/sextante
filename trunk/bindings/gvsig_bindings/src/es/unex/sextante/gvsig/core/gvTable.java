package es.unex.sextante.gvsig.core;

import java.io.File;
import java.io.IOException;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.AbstractTable;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;

public class gvTable
         extends
            AbstractTable {


   private String         m_sName;
   private String         m_sFilename;
   private Object         m_Table;
   private IOutputChannel m_OutputChannel;


   public String getName() {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         return table.getName();
      }
      else {
         return m_sName;
      }

   }


   public void create(final ProjectTable table) {

      m_Table = table;

   }


   public void create(final String sName,
                      final IOutputChannel oc,
                      final Class[] types,
                      final String[] sFields) {

      m_OutputChannel = oc;
      if (oc instanceof NullOutputChannel) {
         m_sFilename = SextanteGUI.getOutputFactory().getTempRasterLayerFilename();
      }
      else {
         m_sFilename = ((FileOutputChannel) oc).getFilename();
      }

      final TableMemoryDriver table = new TableMemoryDriver(sFields, DataTools.getgvSIGTypes(types));
      m_sName = sName;
      m_Table = table;

   }


   public void addRecord(final Object[] record) {

      if (m_Table instanceof TableMemoryDriver) {
         final TableMemoryDriver table = (TableMemoryDriver) m_Table;
         table.addRow(DataTools.getGVSIGValues(record));
      }

   }


   public IRecordsetIterator iterator() {

      return new gvRecordsetIterator(m_Table);

   }


   public String getFieldName(final int i) {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            return table.getModelo().getRecordset().getFieldName(i);
         }
         catch (final Exception e) {
            return "";
         }
      }
      else {
         final TableMemoryDriver table = (TableMemoryDriver) m_Table;
         return table.getFieldName(i);
      }

   }


   public Class getFieldType(final int i) {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            final int iType = table.getModelo().getRecordset().getFieldType(i);
            return DataTools.getTypeClass(iType);
         }
         catch (final Exception e) {
            return String.class;
         }
      }
      else {
         final TableMemoryDriver table = (TableMemoryDriver) m_Table;
         int iType;
         iType = table.getFieldType(i);
         return DataTools.getTypeClass(iType);
      }

   }


   public int getFieldCount() {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            return table.getModelo().getRecordset().getFieldCount();
         }
         catch (final Exception e) {
            return 0;
         }
      }
      else {
         final TableMemoryDriver table = (TableMemoryDriver) m_Table;
         return table.getFieldCount();
      }

   }


   public long getRecordCount() {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            return table.getModelo().getRecordset().getRowCount();
         }
         catch (final Exception e) {
            return 0;
         }
      }
      else {
         final TableMemoryDriver table = (TableMemoryDriver) m_Table;
         return table.getRowCount();
      }

   }


   public void postProcess() {

      if (m_OutputChannel instanceof NullOutputChannel) {
         return;
      }

      SelectableDataSource source;
      ITableDefinition orgDef;
      FileDriver driver;
      File file;
      try {
         LayerFactory.getDataSourceFactory().addDataSource((TableMemoryDriver) m_Table, m_sName);
         final DataSource dataSource = LayerFactory.getDataSourceFactory().createRandomDataSource(m_sName);
         dataSource.start();
         final SelectableDataSource sds = new SelectableDataSource(dataSource);
         final EditableAdapter auxea = new EditableAdapter();
         auxea.setOriginalDataSource(sds);
         final ProjectTable table = ProjectFactory.createTable(m_sName, auxea);
         file = new File(m_sFilename);
         driver = (FileDriver) LayerFactory.getDM().getDriver("gdbms dbf driver");
         source = table.getModelo().getRecordset();
         source.start();
         orgDef = table.getModelo().getTableDefinition();
      }
      catch (final Exception e) {
         return;
      }

      try {
         if (!file.exists()) {
            driver.createSource(file.getAbsolutePath(), new String[] { "0" }, new int[] { Types.INTEGER });
            file.createNewFile();
         }
         driver.open(file);
      }
      catch (final IOException e) {
         e.printStackTrace();
         return;
      }
      catch (final ReadDriverException ex) {
         ex.printStackTrace();
         return;
      }

      final IWriter writer = ((IWriteable) driver).getWriter();
      try {
         writer.initialize(orgDef);
         writer.preProcess();
         final SourceIterator sourceIter = new SourceIterator(source);
         IFeature feature;
         int i = 0;
         while (sourceIter.hasNext()) {
            feature = sourceIter.nextFeature();

            final DefaultRowEdited edRow = new DefaultRowEdited(feature, IRowEdited.STATUS_ADDED, i);
            writer.process(edRow);
            i++;
         }
         writer.postProcess();
      }
      catch (final Exception e) {
         return;
      }

   }


   public void open() {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            table.getModelo().getRecordset().start();
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
         }
      }
      else {
         if (m_sFilename != null) {
            final ProjectTable table = FileTools.openTable(m_sFilename, m_sName);
            create(table);
            open();
         }
      }

   }


   public void close() {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         try {
            table.getModelo().getRecordset().stop();
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
         }
      }

   }


   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }


   public void setName(final String name) {

      if (m_Table instanceof ProjectTable) {
         final ProjectTable table = (ProjectTable) m_Table;
         table.setName(name);
      }
      else {
         m_sName = name;
      }

   }


   @Override
   public Object getBaseDataObject() {

      return m_Table;

   }


   @Override
   public void free() {

      m_Table = null;

   }


}
