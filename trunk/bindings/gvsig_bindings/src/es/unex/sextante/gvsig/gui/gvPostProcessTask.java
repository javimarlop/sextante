

package es.unex.sextante.gvsig.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.RasterLibrary;
import org.gvsig.raster.dataset.serializer.RmfSerializerException;
import org.gvsig.raster.datastruct.NoData;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.ObjectAndDescription;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.ILayer;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.additionalResults.TableTools;
import es.unex.sextante.gui.algorithm.iterative.SingleFeatureVectorLayer;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.settings.SextanteGeneralSettings;
import es.unex.sextante.gvsig.core.FileTools;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.outputs.Output3DRasterLayer;
import es.unex.sextante.outputs.OutputRasterLayer;
import es.unex.sextante.outputs.OutputTable;
import es.unex.sextante.outputs.OutputText;
import es.unex.sextante.outputs.OutputVectorLayer;
import es.unex.sextante.outputs.OverwriteOutputChannel;
import es.unex.sextante.parameters.Parameter;
import es.unex.sextante.parameters.RasterLayerAndBand;


public class gvPostProcessTask
         implements
            Runnable {

   private MapContext             m_MapContext;
   private final GeoAlgorithm     m_Algorithm;
   private final OutputObjectsSet m_Output;
   private final boolean          m_bShowResultsWindows;


   public gvPostProcessTask(final GeoAlgorithm algorithm,
                            final boolean bShowResultsWindow) {

      m_Output = algorithm.getOutputObjects();
      m_Algorithm = algorithm;
      m_bShowResultsWindows = bShowResultsWindow;

   }


   public void run() {

      if (m_Output.hasLayers()) {
         setOutputView();
      }

      addResults();

   }


   private void setOutputView() {

      ProjectView view = null;
      final ParametersSet parameters = m_Algorithm.getParameters();
      for (int i = 0; i < parameters.getNumberOfParameters(); i++) {
         final Parameter param = parameters.getParameter(i);
         final Object object = param.getParameterValueAsObject();
         if (object instanceof ILayer) {
            view = getViewFromLayer((ILayer) object);
            m_MapContext = view.getMapContext();
            return;
         }
         else if (object instanceof ArrayList) {
            final ArrayList list = (ArrayList) object;
            for (int j = 0; j < list.size(); j++) {
               final Object obj = list.get(j);
               if (obj instanceof ILayer) {
                  view = getViewFromLayer((ILayer) obj);
                  m_MapContext = view.getMapContext();
                  return;
               }
               else if (obj instanceof RasterLayerAndBand) {
                  final RasterLayerAndBand rlab = (RasterLayerAndBand) obj;
                  view = getViewFromLayer(rlab.getRasterLayer());
                  m_MapContext = view.getMapContext();
                  return;
               }
            }
         }
      }

      //if there is no input view, ask the user
      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList<ProjectDocument> views = project.getDocumentsByType(ProjectViewFactory.registerName);
      final Object[] options = new Object[views.size() + 1];
      options[0] = Sextante.getText("Create_new_view");
      for (int i = 0; i < views.size(); i++) {
         options[i + 1] = views.get(i);
      }

      final Object selectedObject = JOptionPane.showInputDialog(null, Sextante.getText("Select_output_view"),
               Sextante.getText("Output_view"), JOptionPane.PLAIN_MESSAGE, null, options, null);

      if (selectedObject instanceof ProjectView) {
         m_MapContext = ((ProjectView) selectedObject).getMapContext();
      }
      else {
         final ProjectView newView = ProjectFactory.createView("SEXTANTE (" + m_Algorithm.getName() + ")");
         ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject().addDocument(newView);
         final IWindow window = newView.createWindow();
         final Runnable doWorkRunnable = new Runnable() {
            public void run() {
               PluginServices.getMDIManager().addWindow(window);
               m_MapContext = newView.getMapContext();
            }
         };
         try {
            SwingUtilities.invokeAndWait(doWorkRunnable);
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
         }
      }

   }


   private ProjectView getViewFromLayer(ILayer layer) {

      if (layer instanceof SingleFeatureVectorLayer) {
         layer = ((SingleFeatureVectorLayer) layer).getOriginalLayer();
      }

      final FLayer gvSIGBaseLayer = (FLayer) layer.getBaseDataObject();
      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList views = project.getDocumentsByType(ProjectViewFactory.registerName);

      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers layers = view.getMapContext().getLayers();
         final LayersIterator iter = new LayersIterator(layers);
         while (iter.hasNext()) {
            final FLayer gvSIGLayer = iter.nextLayer();
            if (gvSIGLayer.equals(gvSIGBaseLayer)) {
               return view;
            }
         }

      }

      return null;

   }


   private void addResults() {

      final boolean bUseInternalNames = new Boolean(
               SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.USE_INTERNAL_NAMES)).booleanValue();
      final boolean bModiFyResultsNames = new Boolean(SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.MODIFY_NAMES)).booleanValue();

      String sDescription;
      boolean bInvalidate = false;
      boolean bShowAdditionalPanel = false;

      if (m_MapContext != null) {
         m_MapContext.beginAtomicEvent();
      }

      for (int i = 0; i < m_Output.getOutputObjectsCount(); i++) {

         final Output out = m_Output.getOutput(i);
         sDescription = out.getDescription();
         final IOutputChannel channel = out.getOutputChannel();
         final Object object = out.getOutputObject();

         if ((out instanceof OutputRasterLayer) || (out instanceof Output3DRasterLayer) || (out instanceof OutputTable)
             || (out instanceof OutputVectorLayer)) {
            if (bUseInternalNames) {
               sDescription = out.getName();
            }
            else if (bModiFyResultsNames) {
               sDescription = SextanteGUI.modifyResultName(sDescription);
            }
            if ((channel instanceof NullOutputChannel) || (channel == null)) {
               continue;
            }
         }
         if (out instanceof OutputVectorLayer) {
            String sFilename = null;
            if (channel instanceof FileOutputChannel) {
               sFilename = ((FileOutputChannel) channel).getFilename();
               final FLyrVect flayer = (FLyrVect) FileTools.openLayer(sFilename, sDescription,
                        (IProjection) m_Algorithm.getOutputCRS());
               if (flayer != null) {
                  flayer.setName(sDescription);
                  /*final Object legend = SextanteGUI.getDataRenderer().getRenderingForLayer(m_Algorithm.getCommandLineName(),
                           out.getName());
                  try {
                     flayer.setLegend((IVectorLegend) legend);
                  }
                  catch (final LegendLayerException e) {
                     Sextante.addErrorToLog(e);
                  }*/
                  m_MapContext.getLayers().addLayer(flayer);
                  bInvalidate = true;
               }

            }
            else if (channel instanceof OverwriteOutputChannel) {
               //TODO:add support for non file based layer
               final FLyrVect flayer = (FLyrVect) ((OverwriteOutputChannel) channel).getLayer().getBaseDataObject();
               try {
                  flayer.reload();
                  bInvalidate = true;
               }
               catch (final ReloadLayerException e) {

               }
            }

            if (object != null) {
               ((IVectorLayer) object).close();
            }

         }
         else if (out instanceof OutputTable) {
            try {
               final String sFilename = ((FileOutputChannel) channel).getFilename();
               final ProjectTable table = FileTools.openTable(sFilename, sDescription);
               if (table != null) {
                  table.setName(sDescription);
                  ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject().addDocument(table);
                  final JScrollPane jScrollPane = TableTools.getScrollableTablePanelFromITable((ITable) object);
                  AdditionalResults.addComponent(new ObjectAndDescription(sDescription, jScrollPane));
                  bShowAdditionalPanel = true;
               }
            }
            catch (final Exception e) {
               Sextante.addErrorToLog(e);
            }
         }
         else if (out instanceof OutputRasterLayer) {
            final IRasterLayer rasterLayer = (IRasterLayer) object;
            if (channel instanceof FileOutputChannel) {
               final String sFilename = ((FileOutputChannel) channel).getFilename();
               final FLyrRasterSE flayer = (FLyrRasterSE) FileTools.openLayer(sFilename, sDescription,
                        (IProjection) m_Algorithm.getOutputCRS());
               if (flayer != null) {
                  if (rasterLayer != null) {
                     try {
                        flayer.setNoDataType(RasterLibrary.NODATATYPE_USER);
                        flayer.setNoDataValue(rasterLayer.getNoDataValue());
                        flayer.getDataSource().saveObjectToRmf(0, NoData.class,
                                 new NoData(rasterLayer.getNoDataValue(), flayer.getNoDataType()));
                        rasterLayer.close();
                     }
                     catch (final RmfSerializerException e) {
                        Sextante.addErrorToLog(e);
                     }
                  }
                  flayer.setName(sDescription);
                  m_MapContext.getLayers().addLayer(flayer);
                  bInvalidate = true;

               }
            }
         }
         else if (out instanceof OutputText) {
            JTextPane jTextPane;
            JScrollPane jScrollPane;
            jTextPane = new JTextPane();
            jTextPane.setEditable(false);
            jTextPane.setContentType("text/html");
            jTextPane.setText((String) object);
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(jTextPane);
            jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            jTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            AdditionalResults.addComponent(new ObjectAndDescription(sDescription, jScrollPane));
            bShowAdditionalPanel = true;
         }
         else if (object instanceof Component) {
            AdditionalResults.addComponent(new ObjectAndDescription(sDescription, object));
            bShowAdditionalPanel = true;
         }
         else if (out instanceof Output3DRasterLayer) {
            JOptionPane.showMessageDialog(SextanteGUI.getMainFrame(), Sextante.getText("3d_not_supported"),
                     Sextante.getText("Warning"), JOptionPane.WARNING_MESSAGE);
         }

      }

      if (m_MapContext != null) {
         m_MapContext.endAtomicEvent();
      }

      if (bInvalidate) {
         m_MapContext.invalidate();
      }

      if (bShowAdditionalPanel && m_bShowResultsWindows) {
         AdditionalResults.showPanel();
      }

   }

}
