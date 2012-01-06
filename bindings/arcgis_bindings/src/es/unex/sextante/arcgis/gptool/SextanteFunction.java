

package es.unex.sextante.arcgis.gptool;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IStandaloneTable;
import com.esri.arcgis.carto.RasterLayer;
import com.esri.arcgis.datasourcesfile.DEShapeFile;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.datasourcesraster.RasterWorkspace;
import com.esri.arcgis.datasourcesraster.RasterWorkspaceFactory;
import com.esri.arcgis.geodatabase.IDEFeatureClass;
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IGPDataType;
import com.esri.arcgis.geodatabase.IGPDomain;
import com.esri.arcgis.geodatabase.IGPMessages;
import com.esri.arcgis.geodatabase.IGPValue;
import com.esri.arcgis.geodatabase.IRasterDataset;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.Table;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.esriFeatureType;
import com.esri.arcgis.geodatabase.esriGPMessageSeverity;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.geoprocessing.BaseGeoprocessingTool;
import com.esri.arcgis.geoprocessing.GPBoolean;
import com.esri.arcgis.geoprocessing.GPBooleanType;
import com.esri.arcgis.geoprocessing.GPCodedValueDomain;
import com.esri.arcgis.geoprocessing.GPDouble;
import com.esri.arcgis.geoprocessing.GPDoubleType;
import com.esri.arcgis.geoprocessing.GPExtent;
import com.esri.arcgis.geoprocessing.GPExtentType;
import com.esri.arcgis.geoprocessing.GPFeatureClassDomain;
import com.esri.arcgis.geoprocessing.GPFeatureLayer;
import com.esri.arcgis.geoprocessing.GPFeatureLayerType;
import com.esri.arcgis.geoprocessing.GPLongType;
import com.esri.arcgis.geoprocessing.GPMultiValue;
import com.esri.arcgis.geoprocessing.GPMultiValueType;
import com.esri.arcgis.geoprocessing.GPParameter;
import com.esri.arcgis.geoprocessing.GPPoint;
import com.esri.arcgis.geoprocessing.GPPointType;
import com.esri.arcgis.geoprocessing.GPRasterLayer;
import com.esri.arcgis.geoprocessing.GPRasterLayerType;
import com.esri.arcgis.geoprocessing.GPString;
import com.esri.arcgis.geoprocessing.GPStringType;
import com.esri.arcgis.geoprocessing.GPTableView;
import com.esri.arcgis.geoprocessing.GPTableViewType;
import com.esri.arcgis.geoprocessing.GPValueTable;
import com.esri.arcgis.geoprocessing.GPValueTableType;
import com.esri.arcgis.geoprocessing.GeoProcessor;
import com.esri.arcgis.geoprocessing.IGPEnvironment;
import com.esri.arcgis.geoprocessing.IGPEnvironmentManager;
import com.esri.arcgis.geoprocessing.IGPFeatureClassDomain;
import com.esri.arcgis.geoprocessing.IGPMultiValue;
import com.esri.arcgis.geoprocessing.IGPMultiValueType;
import com.esri.arcgis.geoprocessing.IGPParameter;
import com.esri.arcgis.geoprocessing.IGPValueTable;
import com.esri.arcgis.geoprocessing.IGPValueTableType;
import com.esri.arcgis.geoprocessing.MdParameter;
import com.esri.arcgis.geoprocessing.esriGPParameterDirection;
import com.esri.arcgis.geoprocessing.esriGPParameterType;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.Array;
import com.esri.arcgis.system.IArray;
import com.esri.arcgis.system.IName;
import com.esri.arcgis.system.ITrackCancel;

import es.unex.sextante.additionalInfo.AdditionalInfoBoolean;
import es.unex.sextante.additionalInfo.AdditionalInfoFixedTable;
import es.unex.sextante.additionalInfo.AdditionalInfoMultipleInput;
import es.unex.sextante.additionalInfo.AdditionalInfoNumericalValue;
import es.unex.sextante.additionalInfo.AdditionalInfoRasterLayer;
import es.unex.sextante.additionalInfo.AdditionalInfoSelection;
import es.unex.sextante.additionalInfo.AdditionalInfoTable;
import es.unex.sextante.additionalInfo.AdditionalInfoTableField;
import es.unex.sextante.additionalInfo.AdditionalInfoVectorLayer;
import es.unex.sextante.arcgis.dataobjects.ArcOutputFactory;
import es.unex.sextante.arcgis.dataobjects.ArcRasterLayer;
import es.unex.sextante.arcgis.dataobjects.ArcTable;
import es.unex.sextante.arcgis.dataobjects.ArcVectorLayer;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.ObjectAndDescription;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.outputs.OutputChart;
import es.unex.sextante.outputs.OutputImage;
import es.unex.sextante.outputs.OutputRasterLayer;
import es.unex.sextante.outputs.OutputTable;
import es.unex.sextante.outputs.OutputText;
import es.unex.sextante.outputs.OutputVectorLayer;
import es.unex.sextante.parameters.FixedTableModel;
import es.unex.sextante.parameters.Parameter;
import es.unex.sextante.parameters.ParameterBand;
import es.unex.sextante.parameters.ParameterBoolean;
import es.unex.sextante.parameters.ParameterFixedTable;
import es.unex.sextante.parameters.ParameterMultipleInput;
import es.unex.sextante.parameters.ParameterNumericalValue;
import es.unex.sextante.parameters.ParameterPoint;
import es.unex.sextante.parameters.ParameterRasterLayer;
import es.unex.sextante.parameters.ParameterSelection;
import es.unex.sextante.parameters.ParameterString;
import es.unex.sextante.parameters.ParameterTable;
import es.unex.sextante.parameters.ParameterTableField;
import es.unex.sextante.parameters.ParameterVectorLayer;
import es.unex.sextante.parameters.RasterLayerAndBand;


public class SextanteFunction
         extends
            BaseGeoprocessingTool {

   private GeoAlgorithm m_Alg;
   private Array        m_Parameters;
   private boolean[]    m_bParameterHasBeenModified;


   public void setAlgorithm(final GeoAlgorithm alg) {

      m_Alg = alg;

   }


   @Override
   public void execute(final IArray gpParams,
                       final ITrackCancel trackCancel,
                       final IGPEnvironmentManager manager,
                       final IGPMessages msg) throws IOException, AutomationException {

      final IGPEnvironment environment = manager.findEnvironment(SextanteGlobalParams.USE_ONLY_SELECTED);
      final GPBoolean booleanValue = (GPBoolean) environment.getValue();
      SextanteGlobalParams.setParameter(SextanteGlobalParams.USE_ONLY_SELECTED, new Boolean(booleanValue.isValue()));

      GeoAlgorithm alg = null;
      try {
         alg = m_Alg.getNewInstance();
      }
      catch (final Exception e) {
         throw new AutomationException(e);
      }
      final ParametersSet params = alg.getParameters();
      for (int i = 0; i < params.getNumberOfParameters(); i++) {
         final IGPParameter param = (IGPParameter) gpParams.getElement(i);
         final IGPValue unpacked = gpUtilities.unpackGPValue(param);
         setParamValue(params.getParameter(i), unpacked, gpParams);
      }

      int iParamCount = params.getNumberOfParameters();
      if (!alg.canDefineOutputExtentFromInput()) {
         IGPParameter param = (IGPParameter) gpParams.getElement(iParamCount);
         IGPValue unpacked = gpUtilities.unpackGPValue(param);
         final IEnvelope env = ((GPExtent) unpacked).getExtent(new int[1]);
         final AnalysisExtent extent = new AnalysisExtent();
         extent.setXRange(env.getXMin(), env.getXMax(), true);
         extent.setYRange(env.getYMin(), env.getYMax(), true);
         param = (IGPParameter) gpParams.getElement(iParamCount + 1);
         unpacked = gpUtilities.unpackGPValue(param);
         extent.setCellSize(((GPDouble) unpacked).IGPDouble_getValue());
         alg.setAnalysisExtent(extent);
         iParamCount += 2;
      }

      OutputObjectsSet outputs = alg.getOutputObjects();

      int j = 0;
      for (int i = 0; i < outputs.getOutputObjectsCount(); i++) {
         final Output output = outputs.getOutput(i);
         if ((output instanceof OutputRasterLayer) || (output instanceof OutputVectorLayer) || (output instanceof OutputTable)) {
            final IGPParameter outputParam = (IGPParameter) gpParams.getElement(j + iParamCount);
            final IGPValue value = gpUtilities.unpackGPValue(outputParam);
            final String sFilename = value.getAsText();
            try {
               gpUtilities.delete(value);
            }
            catch (final Exception e) {
               //ignore this...
            }
            output.setOutputChannel(new FileOutputChannel(checkExtension(sFilename, output.getClass())));
            j++;
         }
      }

      try {
         alg.execute(new ArcTaskController(trackCancel, msg), new ArcOutputFactory());

         j = 0;
         outputs = alg.getOutputObjects();
         for (int i = 0; i < outputs.getOutputObjectsCount(); i++) {
            final Output output = outputs.getOutput(i);
            if ((output instanceof OutputRasterLayer) || (output instanceof OutputVectorLayer) || (output instanceof OutputTable)) {
               final IGPParameter outputParam = (IGPParameter) gpParams.getElement(j + iParamCount);
               final String sFilename = ((FileOutputChannel) output.getOutputChannel()).getFilename();
               final IDataObject object = (IDataObject) output.getOutputObject();
               if ((output instanceof OutputRasterLayer)) {
                  ILayer layer = null;
                  if (object == null) { //layer generated by an external program (i.e. SAGA, GRASS)
                     do {//this is a dirty solution to the problem of the external app returning control before it has finished its task. Should be improved...
                        layer = getRasterLayerFromFilepath(sFilename);
                        if (layer == null) {
                           Thread.sleep(1000);
                        }
                     }
                     while ((layer == null) && trackCancel.esri_continue());
                  }
                  else {
                     layer = (ILayer) object.getBaseDataObject();
                  }
                  final IGPValue value = (IGPValue) gpUtilities.makeGPLayerFromLayer(layer);
                  value.setAsText(sFilename);
                  gpUtilities.packGPValue(value, outputParam);
                  final GeoProcessor geoprocessor = new GeoProcessor();
                  if (geoprocessor.isAddOutputsToMap()) {
                     gpUtilities.getActiveView().getFocusMap().addLayer(layer);
                  }
               }
               else if ((output instanceof OutputVectorLayer)) {
                  ILayer layer = null;
                  if (object == null) { //layer generated by an external program (i.e. SAGA, GRASS)
                     do {
                        layer = getFeatureLayerFromFilepath(sFilename);
                        if (layer == null) {
                           Thread.sleep(1000);
                        }
                     }
                     while ((layer == null) && trackCancel.esri_continue());
                  }
                  else {
                     layer = (ILayer) object.getBaseDataObject();
                  }
                  final IGPValue value = (IGPValue) gpUtilities.makeGPLayerFromLayer(layer);
                  value.setAsText(sFilename);
                  gpUtilities.packGPValue(value, outputParam);
                  final GeoProcessor geoprocessor = new GeoProcessor();
                  if (geoprocessor.isAddOutputsToMap()) {
                     gpUtilities.getActiveView().getFocusMap().addLayer(layer);
                  }
               }
               else {
                  ITable table;
                  String sName;
                  final File file = new File(sFilename);
                  final ShapefileWorkspaceFactory shapefileWorkspaceFactory = new ShapefileWorkspaceFactory();
                  final Workspace workspace = new Workspace(shapefileWorkspaceFactory.openFromFile(file.getParent(), 0));
                  table = new Table(workspace.openTable(file.getName()));
                  if (object == null) {
                     sName = output.getName();
                  }
                  else {
                     sName = object.getName();
                  }
                  final IGPValue value = (IGPValue) gpUtilities.makeGPTableViewFromTable(table);
                  value.setAsText(sFilename);
                  gpUtilities.packGPValue(value, outputParam);
                  gpUtilities.addToMap(value, sName, false, null);
               }
               j++;
            }
            else if (output instanceof OutputText) {
               JTextPane jTextPane;
               JScrollPane jScrollPane;
               try {
                  jTextPane = new JTextPane();
               }
               catch (final Exception e) {
                  jTextPane = new JTextPane(); //throws NPE the first time sometimes ¿¿??
               }
               jTextPane.setEditable(false);
               jTextPane.setContentType("text/html");
               jTextPane.setText((String) output.getOutputObject());
               jScrollPane = new JScrollPane();
               jScrollPane.setViewportView(jTextPane);
               jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
               jTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
               AdditionalResults.addComponent(new ObjectAndDescription(output.getDescription(), jScrollPane));
               j++;
            }
            else if (output instanceof OutputChart) {
               AdditionalResults.addComponent(new ObjectAndDescription(output.getDescription(), output.getOutputObject()));
               j++;
            }
            else if (output instanceof OutputImage) {
               AdditionalResults.addComponent(new ObjectAndDescription(output.getDescription(), output.getOutputObject()));
               j++;
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
         Sextante.addErrorToLog(e);
         throw new AutomationException(e);
      }

   }


   private String checkExtension(String sFilename,
                                 final Class clazz) {

      String sExt = null;
      if (clazz.equals(OutputRasterLayer.class)) {
         sExt = new ArcOutputFactory().getRasterLayerOutputExtensions()[0];
      }
      else if (clazz.equals(OutputVectorLayer.class)) {
         sExt = new ArcOutputFactory().getVectorLayerOutputExtensions()[0];
      }
      else if (clazz.equals(OutputTable.class)) {
         sExt = new ArcOutputFactory().getTableOutputExtensions()[0];
      }

      if (!sFilename.endsWith(sExt)) {
         sFilename = sFilename + "." + sExt;
      }

      return sFilename;

   }


   private void setParamValue(final Parameter parameter,
                              final IGPValue value,
                              final IArray gpParams) throws AutomationException {

      try {
         if (parameter instanceof ParameterRasterLayer) {
            final ArcRasterLayer layer = getSextanteRasterLayerFromValue(value);
            if (layer != null) {
               parameter.setParameterValue(layer);
            }
         }
         else if (parameter instanceof ParameterVectorLayer) {
            final ArcVectorLayer layer = getSextanteVectorLayerFromValue(value);
            if (layer != null) {
               parameter.setParameterValue(layer);
            }

         }
         else if (parameter instanceof ParameterTable) {
            final ArcTable table = new ArcTable();
            final IStandaloneTable decoded = gpUtilities.decodeStandaloneTable(value);
            if (decoded != null) {
               table.create(decoded);
               parameter.setParameterValue(table);
            }
         }
         else if (parameter instanceof ParameterMultipleInput) {
            final AdditionalInfoMultipleInput aimi = (AdditionalInfoMultipleInput) parameter.getParameterAdditionalInfo();
            final int type = aimi.getDataType();
            final ArrayList list = new ArrayList();
            if (type == AdditionalInfoMultipleInput.DATA_TYPE_BAND) {
               final GPValueTable valueTable = ((GPValueTable) value);
               for (int j = 0; j < valueTable.getRecordCount(); j++) {
                  final IGPValue gpValueLayer = valueTable.getValue(j, 0);
                  final ArcRasterLayer layer = new ArcRasterLayer();
                  layer.create((RasterLayer) gpUtilities.decodeLayer(gpValueLayer));
                  final IGPValue gpValueBand = valueTable.getValue(j, 1);
                  int iBand;
                  try {
                     iBand = Integer.parseInt(gpValueBand.getAsText());
                  }
                  catch (final Exception e) {
                     iBand = 0;
                  }
                  iBand = Math.max(iBand, 0);
                  iBand = Math.min(iBand, layer.getBandsCount());
                  final RasterLayerAndBand rlab = new RasterLayerAndBand(layer, iBand);
                  list.add(rlab);
               }
            }
            else {
               final GPMultiValue multi = (GPMultiValue) value;
               for (int i = 0; i < multi.getCount(); i++) {
                  final IGPValue singleValue = multi.getValue(i);
                  switch (type) {
                     case AdditionalInfoMultipleInput.DATA_TYPE_TABLE:
                        final ArcTable table = new ArcTable();
                        table.create(gpUtilities.decodeStandaloneTable(singleValue));
                        list.add(table);
                        break;
                     case AdditionalInfoMultipleInput.DATA_TYPE_RASTER:
                        final ArcRasterLayer rasterLayer = getSextanteRasterLayerFromValue(singleValue);
                        list.add(rasterLayer);
                        break;
                     case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_ANY:
                     case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_POINT:
                     case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_LINE:
                     case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_POLYGON:
                        final ArcVectorLayer vectorLayer = getSextanteVectorLayerFromValue(singleValue);
                        list.add(vectorLayer);
                  }
               }
            }
            parameter.setParameterValue(list);

         }
         else if (parameter instanceof ParameterString) {
            parameter.setParameterValue(value.getAsText());
         }
         else if (parameter instanceof ParameterBoolean) {
            parameter.setParameterValue(new Boolean(((GPBoolean) value).isValue()));
         }
         else if (parameter instanceof ParameterNumericalValue) {
            parameter.setParameterValue(new java.lang.Double(((GPDouble) value).IGPDouble_getValue()));
         }
         else if (parameter instanceof ParameterSelection) {
            final String sValue = ((GPString) value).getValue();
            final AdditionalInfoSelection ais = (AdditionalInfoSelection) parameter.getParameterAdditionalInfo();
            final String[] sValues = ais.getValues();
            int iValue = 0;
            for (int i = 0; i < sValues.length; i++) {
               if (sValue.equals(sValues[i])) {
                  iValue = i;
                  break;
               }
            }
            parameter.setParameterValue(new Integer(iValue));
         }
         else if (parameter instanceof ParameterTableField) {
            final AdditionalInfoTableField aitf = (AdditionalInfoTableField) parameter.getParameterAdditionalInfo();
            final String sParent = aitf.getParentParameterName();
            final ParametersSet params = m_Alg.getParameters();
            for (int i = 0; i < params.getNumberOfParameters(); i++) {
               final IGPParameter param = (IGPParameter) gpParams.getElement(i);
               if (sParent.equals(param.getName())) {
                  final IGPValue unpacked = gpUtilities.unpackGPValue(param);
                  final FeatureLayer layer = (FeatureLayer) gpUtilities.decodeLayer(unpacked);
                  final IFeatureClass fc = layer.getFeatureClass();
                  final int iField = fc.findField(value.getAsText());
                  parameter.setParameterValue(new Integer(iField - 2));
                  break;
               }
            }
         }
         else if (parameter instanceof ParameterBand) {
            final String sValue = ((GPString) value).getValue();
            final int iValue = Integer.parseInt(sValue) - 1;
            parameter.setParameterValue(new Integer(iValue));
         }
         else if (parameter instanceof ParameterFixedTable) {
            final GPValueTable valueTable = ((GPValueTable) value);
            final AdditionalInfoFixedTable aift = (AdditionalInfoFixedTable) parameter.getParameterAdditionalInfo();
            final String[] sCols = aift.getCols();
            final FixedTableModel model = new FixedTableModel(sCols, valueTable.getRecordCount(), false);
            for (int i = 0; i < valueTable.getRecordCount(); i++) {
               for (int j = 0; j < sCols.length; j++) {
                  model.setValueAt(valueTable.getValue(i, j).getAsText(), i, j);
               }
            }
            parameter.setParameterValue(model);
         }
         else if (parameter instanceof ParameterPoint) {
            final String sPt = ((GPPoint) value).getAsText();
            final String[] sCoords = sPt.split(" ");
            final Point2D.Double pt2d = new Point2D.Double(Double.parseDouble(sCoords[0]), Double.parseDouble(sCoords[1]));
            parameter.setParameterValue(pt2d);
         }
      }
      catch (final GeoAlgorithmExecutionException e) {
         JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         Sextante.addErrorToLog(e);
         e.printStackTrace();
         throw new AutomationException(e);
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         e.printStackTrace();
         throw new AutomationException(e);

      }

   }


   private ArcRasterLayer getSextanteRasterLayerFromValue(final IGPValue value) {

      final ArcRasterLayer layer = new ArcRasterLayer();
      final RasterLayer rasterLayer = getRasterLayerFromValue(value);
      if (rasterLayer != null) {
         layer.create(rasterLayer);
         return layer;
      }
      else {
         return null;
      }


   }


   private ArcVectorLayer getSextanteVectorLayerFromValue(final IGPValue value) {

      try {
         final ArcVectorLayer layer = new ArcVectorLayer();
         final FeatureLayer featureLayer = getFeatureLayerFromValue(value);
         final File file = new File(value.getAsText());
         String sFilename = null;
         if (file.exists()) {
            sFilename = file.getAbsolutePath();
         }
         else {
            final GPFeatureLayer gpFeatureLayer = (GPFeatureLayer) value;
            final IDEFeatureClass fc = gpFeatureLayer.getDEFeatureClass();
            if (fc instanceof DEShapeFile) {
               final DEShapeFile shp = (DEShapeFile) fc;
               sFilename = shp.getAsText();
            }
         }
         if (featureLayer != null) {
            layer.create(featureLayer, sFilename);
            return layer;
         }
         else {
            return null;
         }

      }
      catch (final Exception e) {
         return null;
      }

   }


   private FeatureLayer getFeatureLayerFromValue(final IGPValue value) {

      FeatureLayer featureLayer = null;

      try {
         featureLayer = (FeatureLayer) gpUtilities.decodeLayer(value);
      }
      catch (final Exception e) {
         try {
            return getFeatureLayerFromFilepath(value.getAsText());
         }
         catch (final Exception e1) {
            return null;
         }
      }

      return featureLayer;

   }


   private FeatureLayer getFeatureLayerFromFilepath(final String sFilepath) {

      try {
         final File file = new File(sFilepath);
         final ShapefileWorkspaceFactory shapefileWorkspaceFactory = new ShapefileWorkspaceFactory();
         final Workspace workspace = (Workspace) shapefileWorkspaceFactory.openFromFile(file.getParent(), 0);
         final IFeatureClass featureClass = workspace.openFeatureClass(file.getName());
         final FeatureLayer featureLayer = new FeatureLayer();
         featureLayer.setFeatureClassByRef(featureClass);
         return featureLayer;
      }
      catch (final Exception e) {
         return null;
      }


   }


   private RasterLayer getRasterLayerFromValue(final IGPValue value) {

      RasterLayer rasterLayer = null;

      try {
         rasterLayer = (RasterLayer) gpUtilities.decodeLayer(value);
      }
      catch (final Exception e) {
         try {
            return getRasterLayerFromFilepath(value.getAsText());
         }
         catch (final Exception e1) {
            return null;
         }
      }

      return rasterLayer;

   }


   private RasterLayer getRasterLayerFromFilepath(final String sFilepath) {

      try {
         final File file = new File(sFilepath);
         final RasterWorkspaceFactory factory = new RasterWorkspaceFactory();
         final RasterWorkspace workSpace = new RasterWorkspace(factory.openFromFile(file.getParent(), 0));
         final IRasterDataset dataset = workSpace.openRasterDataset(file.getName());
         final RasterLayer rasterLayer = new RasterLayer();
         rasterLayer.createFromDataset(dataset);
         return rasterLayer;
      }
      catch (final Exception e) {
         //e.printStackTrace();
         return null;
      }


   }


   @Override
   public String getDisplayName() throws IOException, AutomationException {

      return m_Alg.getName();

   }


   @Override
   public IName getFullName() throws IOException, AutomationException {

      return (IName) new SextanteFunctionFactory().getFunctionName(m_Alg.getCommandLineName());

   }


   @Override
   public String getMetadataFile() throws IOException, AutomationException {

      return m_Alg.getCommandLineName() + ".xml";

   }


   @Override
   public String getName() throws IOException, AutomationException {

      return m_Alg.getName();

   }


   @Override
   public IArray getParameterInfo() throws IOException, AutomationException {

      m_bParameterHasBeenModified = new boolean[m_Parameters.getCount()];

      return m_Parameters;

   }


   private GPParameter getArcGISParameterFromSextanteOutput(final Output output) throws ParameterCreationException {

      try {
         final GPParameter param = new GPParameter();
         param.setName(output.getName());
         param.setDisplayName(output.getDescription());
         param.setDirection(esriGPParameterDirection.esriGPParameterDirectionOutput);
         if (output instanceof OutputRasterLayer) {
            param.setDataTypeByRef(new GPRasterLayerType());
         }
         else if (output instanceof OutputVectorLayer) {
            param.setDataTypeByRef(new GPFeatureLayerType());
         }
         else if (output instanceof OutputTable) {
            param.setDataTypeByRef(new GPTableViewType());
         }
         else if (output instanceof OutputTable) {
            param.setDataTypeByRef(new GPRasterLayerType());
         }
         else {
            return null;
         }
         return param;
      }
      catch (final Exception e) {
         throw new ParameterCreationException();
      }
   }


   private GPParameter getArcGISParameterFromSextanteParameter(final Parameter parameter) {

      try {
         final GPParameter param = new GPParameter();
         param.setName(parameter.getParameterName());
         param.setDisplayName(parameter.getParameterDescription());
         param.setDirection(esriGPParameterDirection.esriGPParameterDirectionInput);
         if (parameter instanceof ParameterRasterLayer) {
            param.setDataTypeByRef(new GPRasterLayerType());
            final AdditionalInfoRasterLayer airl = (AdditionalInfoRasterLayer) parameter.getParameterAdditionalInfo();
            if (airl.getIsMandatory()) {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            }
            else {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeOptional);
            }
         }
         else if (parameter instanceof ParameterVectorLayer) {
            param.setDataTypeByRef(new GPFeatureLayerType());
            final AdditionalInfoVectorLayer aivl = (AdditionalInfoVectorLayer) parameter.getParameterAdditionalInfo();
            if (aivl.getIsMandatory()) {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            }
            else {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeOptional);
            }

            final IGPFeatureClassDomain fcDomain = new GPFeatureClassDomain();
            switch (aivl.getShapeType()) {

               case IVectorLayer.SHAPE_TYPE_POINT:
                  fcDomain.addFeatureType(esriFeatureType.esriFTSimple);
                  fcDomain.addType(esriGeometryType.esriGeometryPoint);
                  fcDomain.addType(esriGeometryType.esriGeometryMultipoint);
                  param.setDomainByRef((IGPDomain) fcDomain);
                  break;
               case IVectorLayer.SHAPE_TYPE_LINE:
                  fcDomain.addFeatureType(esriFeatureType.esriFTSimple);
                  fcDomain.addType(esriGeometryType.esriGeometryLine);
                  fcDomain.addType(esriGeometryType.esriGeometryPolyline);
                  param.setDomainByRef((IGPDomain) fcDomain);
                  break;
               case IVectorLayer.SHAPE_TYPE_POLYGON:
                  fcDomain.addFeatureType(esriFeatureType.esriFTSimple);
                  fcDomain.addType(esriGeometryType.esriGeometryPolygon);
                  param.setDomainByRef((IGPDomain) fcDomain);
                  break;
            }

         }
         else if (parameter instanceof ParameterTable) {
            param.setDataTypeByRef(new GPTableViewType());
            final AdditionalInfoTable ait = (AdditionalInfoTable) parameter.getParameterAdditionalInfo();
            if (ait.getIsMandatory()) {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            }
            else {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeOptional);
            }
         }
         else if (parameter instanceof ParameterMultipleInput) {
            final AdditionalInfoMultipleInput aimi = (AdditionalInfoMultipleInput) parameter.getParameterAdditionalInfo();
            if (aimi.getIsMandatory()) {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            }
            else {
               param.setParameterType(esriGPParameterType.esriGPParameterTypeOptional);
            }
            if (aimi.getDataType() == AdditionalInfoMultipleInput.DATA_TYPE_BAND) {
               final IGPValueTableType valueTableType = new GPValueTableType();
               final IGPDataType inputLayerType = new GPRasterLayerType();
               valueTableType.addDataType(inputLayerType, "Layer", 600, null);
               final IGPDataType inputLongType = new GPLongType();
               valueTableType.addDataType(inputLongType, "Band", 30, null);
               param.setDataTypeByRef((IGPDataType) valueTableType);
               final IGPValueTable valueTable = new GPValueTable();
               valueTable.addDataType(new GPRasterLayerType());
               valueTable.addDataType(new GPLongType());
               param.setValueByRef((IGPValue) valueTable);
            }
            else {
               IGPDataType inputType = null;
               final IGPMultiValueType mvType = new GPMultiValueType();
               switch (aimi.getDataType()) {
                  case AdditionalInfoMultipleInput.DATA_TYPE_RASTER:
                     inputType = new GPRasterLayerType();
                  case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_ANY:
                  case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_LINE:
                  case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_POINT:
                  case AdditionalInfoMultipleInput.DATA_TYPE_VECTOR_POLYGON:
                     inputType = new GPRasterLayerType();
                     break;
                  case AdditionalInfoMultipleInput.DATA_TYPE_TABLE:
                     inputType = new GPTableViewType();
                     break;
                  default:
                     return null;
               }
               mvType.setMemberDataTypeByRef(inputType);
               param.setDataTypeByRef((IGPDataType) mvType);
               final IGPMultiValue mvValue = new GPMultiValue();
               mvValue.setMemberDataTypeByRef(inputType);
               param.setValueByRef((IGPValue) mvValue);
            }
         }
         else if (parameter instanceof ParameterString) {
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPStringType());
            param.setValueByRef(new GPString());
         }
         else if (parameter instanceof ParameterBoolean) {
            final AdditionalInfoBoolean aib = (AdditionalInfoBoolean) parameter.getParameterAdditionalInfo();
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPBooleanType());
            final GPBoolean bool = new GPBoolean();
            bool.setValue(aib.getDefaultValue());
            param.setValueByRef(bool);
         }
         else if (parameter instanceof ParameterNumericalValue) {
            final AdditionalInfoNumericalValue ainv = (AdditionalInfoNumericalValue) parameter.getParameterAdditionalInfo();
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPDoubleType());
            final GPDouble d = new GPDouble();
            d.setValue(ainv.getDefaultValue());
            param.setValueByRef(d);
         }
         else if (parameter instanceof ParameterSelection) {
            final AdditionalInfoSelection ais = (AdditionalInfoSelection) parameter.getParameterAdditionalInfo();
            final String[] sValues = ais.getValues();
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            final GPCodedValueDomain domain = new GPCodedValueDomain();
            for (final String element : sValues) {
               domain.addStringCode(element, element);
            }
            param.setDataTypeByRef(new GPStringType());
            param.setValueByRef(domain.getValue(0));
            param.setDomainByRef(domain);
         }
         else if (parameter instanceof ParameterTableField) {
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPStringType());
         }
         else if (parameter instanceof ParameterBand) {
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPStringType());
         }
         else if (parameter instanceof ParameterFixedTable) {
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            final AdditionalInfoFixedTable aift = (AdditionalInfoFixedTable) parameter.getParameterAdditionalInfo();
            if (aift.isNumberOfRowsFixed()) {
               return null;
            }
            final IGPValueTableType valueTableType = new GPValueTableType();
            for (int i = 0; i < aift.getColsCount(); i++) {
               final IGPDataType type = new GPStringType();
               valueTableType.addDataType(type, aift.getCols()[i], 100, null);
               param.setDataTypeByRef((IGPDataType) valueTableType);
            }
            final IGPValueTable valueTable = new GPValueTable();
            for (int i = 0; i < aift.getColsCount(); i++) {
               valueTable.addDataType(new GPStringType());
            }
            param.setValueByRef((IGPValue) valueTable);
         }
         else if (parameter instanceof ParameterPoint) {
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPPointType());
         }
         else {
            return null;
         }

         return param;
      }
      catch (final Exception e) {
         return null;
      }

   }


   @Override
   public boolean isLicensed() throws IOException, AutomationException {

      return true;

   }


   @Override
   public void updateMessages(final IArray paramValues,
                              final IGPEnvironmentManager pEnvMgr,
                              final IGPMessages messages) {


      IEnvelope envelope = null;
      double dCellsize = 0;
      try {
         final ParametersSet params = m_Alg.getParameters();
         for (int i = 0; i < params.getNumberOfParameters(); i++) {
            final Parameter param = params.getParameter(i);
            final IGPParameter gpParameter = (IGPParameter) paramValues.getElement(i);
            if (param instanceof ParameterRasterLayer) {
               final IGPValue parameterValue = this.gpUtilities.unpackGPValue(gpParameter);
               final RasterLayer layer = getRasterLayerFromValue(parameterValue);
               if (layer != null) {
                  final IEnvelope env = layer.getExtent();
                  final double d = env.getWidth() / layer.getColumnCount();
                  if (envelope == null) {
                     envelope = env;
                     dCellsize = d;
                  }
                  if ((d != dCellsize) || (env.getXMin() != envelope.getXMin()) || (env.getXMax() != envelope.getXMax())
                      || (env.getYMin() != envelope.getYMin()) || (env.getYMax() != envelope.getYMax())) {
                     messages.replaceError(i, esriGPMessageSeverity.esriGPMessageSeverityError,
                              "Raster layers extents do not match");
                  }
               }
            }
         }

         int iParamCount = params.getNumberOfParameters();
         if (!m_Alg.canDefineOutputExtentFromInput()) {
            iParamCount += 2;
         }
         final GeoProcessor geoprocessor = new GeoProcessor();
         final OutputObjectsSet outputs = m_Alg.getOutputObjects();
         int j = 0;
         for (int i = 0; i < outputs.getOutputObjectsCount(); i++) {
            final Output output = outputs.getOutput(i);
            if ((output instanceof OutputRasterLayer) || (output instanceof OutputVectorLayer) || (output instanceof OutputTable)) {
               final IGPParameter gpParameter = (IGPParameter) paramValues.getElement(j + iParamCount);
               final IGPValue parameterValue = this.gpUtilities.unpackGPValue(gpParameter);
               String sFilename = parameterValue.getAsText();
               sFilename = checkExtension(sFilename, output.getClass());
               final File file = new File(sFilename);
               if (file.exists() && !geoprocessor.isOverwriteOutput()) {
                  messages.replaceError(i, esriGPMessageSeverity.esriGPMessageSeverityError,
                           "File already exists and cannot be overwritten");
               }
               j++;
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public void updateParameters(final IArray gpParams,
                                final IGPEnvironmentManager manager) {


      try {


         final ParametersSet params = m_Alg.getParameters();


         for (int i = 0; i < gpParams.getCount(); i++) {
            final IGPParameter gpParam = (IGPParameter) gpParams.getElement(i);
            if (gpParam.isAltered()) {
               m_bParameterHasBeenModified[i] = true;
            }
         }

         for (int i = 0; i < params.getNumberOfParameters(); i++) {
            final Parameter param = params.getParameter(i);
            final IGPParameter gpParam = (IGPParameter) gpParams.getElement(i);
            if (gpParam.isAltered()) {
               if (param instanceof ParameterVectorLayer) {
                  final IGPValue value = this.gpUtilities.unpackGPValue(gpParam);
                  updateDependentFields(param.getParameterName(), value, gpParams, true);
               }
               else if (param instanceof ParameterTable) {
                  final IGPValue value = this.gpUtilities.unpackGPValue(gpParam);
                  updateDependentFields(param.getParameterName(), value, gpParams, false);
               }
               else if (param instanceof ParameterRasterLayer) {
                  final IGPValue value = this.gpUtilities.unpackGPValue(gpParam);
                  updateDependentBandFields(param.getParameterName(), value, gpParams);
               }
               if (i == 0) {
                  fillOutputParameters(gpParams);
               }
            }
         }

      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }


   private void fillOutputParameters(final IArray gpParams) {


      try {
         final ParametersSet params = m_Alg.getParameters();
         int iParamCount = params.getNumberOfParameters();
         if (!m_Alg.canDefineOutputExtentFromInput()) {
            iParamCount += 2;
         }
         final OutputObjectsSet outputs = m_Alg.getOutputObjects();
         int j = 0;
         for (int i = 0; i < outputs.getOutputObjectsCount(); i++) {
            final Output output = outputs.getOutput(i);
            if ((output instanceof OutputRasterLayer) || (output instanceof OutputVectorLayer) || (output instanceof OutputTable)
            /*|| (output instanceof OutputChart) || (output instanceof OutputText)*/) {
               final MdParameter gpParameter = (MdParameter) gpParams.getElement(j + iParamCount);
               if (m_bParameterHasBeenModified[j + iParamCount] && !gpParameter.getValue().getAsText().trim().equals("")) {
                  continue;
               }
               IGPValue value = null;
               if (output instanceof OutputRasterLayer) {
                  value = new GPRasterLayer();
               }
               else if (output instanceof OutputVectorLayer) {
                  value = new GPFeatureLayer();
               }
               else if (output instanceof OutputTable) {
                  value = new GPTableView();
               }
               else {
                  value = new GPString();
               }
               int iFile = 1;
               String sFullpath;
               if (gpUtilities.getActiveView().getFocusMap().getLayerCount() != 0) {
                  final IWorkspace ws = ((IDataset) gpUtilities.getActiveView().getFocusMap().getLayer(0)).getWorkspace();
                  final String sPath = ws.getPathName();
                  do {
                     final String sFilename = checkExtension(gpParameter.getName() + iFile, output.getClass());
                     sFullpath = sPath + File.separator + sFilename;
                     iFile++;
                  }
                  while (new File(sFullpath).exists());
                  value.setAsText(sFullpath);
                  gpParameter.setValueByRef(value);
                  gpParameter.setAltered(false);
               }
            }
            j++;
         }

      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }


   private void updateDependentBandFields(final String sParameterName,
                                          final IGPValue value,
                                          final IArray gpParams) {


      try {
         final ParametersSet params = m_Alg.getParameters();
         for (int i = 0; i < params.getNumberOfParameters(); i++) {
            final Parameter param = params.getParameter(i);
            if (param instanceof ParameterBand) {
               final AdditionalInfoTableField aitf = (AdditionalInfoTableField) param.getParameterAdditionalInfo();
               final String sParent = aitf.getParentParameterName();
               if (sParent.equals(sParameterName)) {
                  final MdParameter gpParam = (MdParameter) gpParams.getElement(i);
                  final RasterLayer rasterLayer = (RasterLayer) gpUtilities.decodeLayer(value);
                  final int iBandCount = rasterLayer.getBandCount();
                  final String sValues[] = new String[iBandCount];
                  for (int j = 0; j < iBandCount; j++) {
                     sValues[j] = Integer.toString(j + 1);
                  }
                  final GPCodedValueDomain domain = new GPCodedValueDomain();
                  for (final String element : sValues) {
                     domain.addStringCode(element, element);
                  }
                  gpParam.setValueByRef(domain.getValue(0));
                  gpParam.setDomainByRef(domain);
               }
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }


   private void updateDependentFields(final String sParameterName,
                                      final IGPValue paramValue,
                                      final IArray gpParams,
                                      final boolean bIsLayer) {

      int iOffSet = 0;

      if (bIsLayer) {
         iOffSet = 2;
      }
      try {
         final ParametersSet params = m_Alg.getParameters();
         for (int i = 0; i < params.getNumberOfParameters(); i++) {
            final Parameter param = params.getParameter(i);
            if (param instanceof ParameterTableField) {
               final AdditionalInfoTableField aitf = (AdditionalInfoTableField) param.getParameterAdditionalInfo();
               final String sParent = aitf.getParentParameterName();
               if (sParent.equals(sParameterName)) {
                  final MdParameter gpParam = (MdParameter) gpParams.getElement(i);
                  final FeatureLayer featureLayer = (FeatureLayer) gpUtilities.decodeLayer(paramValue);
                  final IFeatureClass fc = featureLayer.getFeatureClass();
                  final String[] sValues = new String[fc.getFields().getFieldCount() - iOffSet];
                  for (int j = 0; j < sValues.length; j++) {
                     sValues[j] = fc.getFields().getField(j + iOffSet).getName();
                  }
                  final GPCodedValueDomain domain = new GPCodedValueDomain();
                  for (final String element : sValues) {
                     domain.addStringCode(element, element);
                  }
                  gpParam.setValueByRef(domain.getValue(0));
                  gpParam.setDomainByRef(domain);
               }
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }


   public boolean initialize() {

      try {
         m_Parameters = new Array();

         final ParametersSet params = m_Alg.getParameters();
         for (int i = 0; i < params.getNumberOfParameters(); i++) {
            final GPParameter param = getArcGISParameterFromSextanteParameter(params.getParameter(i));
            if (param != null) {
               m_Parameters.add(param);
            }
            else {
               return false;
            }
         }

         if (!m_Alg.canDefineOutputExtentFromInput()) {
            GPParameter param = new GPParameter();
            param.setName("Set output extent from");
            param.setDisplayName("Set output extent from");
            param.setDirection(esriGPParameterDirection.esriGPParameterDirectionInput);
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPExtentType());
            m_Parameters.add(param);

            param = new GPParameter();
            param.setName("Cellsize");
            param.setDisplayName("Cellsize");
            param.setDirection(esriGPParameterDirection.esriGPParameterDirectionInput);
            param.setParameterType(esriGPParameterType.esriGPParameterTypeRequired);
            param.setDataTypeByRef(new GPDoubleType());
            m_Parameters.add(param);
         }

         final OutputObjectsSet outputs = m_Alg.getOutputObjects();
         for (int i = 0; i < outputs.getOutputObjectsCount(); i++) {
            final GPParameter out = getArcGISParameterFromSextanteOutput(outputs.getOutput(i));
            if (out != null) {
               m_Parameters.add(out);
            }
         }

         return true;
      }
      catch (final Exception e) {
         return false;
      }


   }


   public GeoAlgorithm getSextanteGeoAlgorithm() {

      return m_Alg;

   }

}
