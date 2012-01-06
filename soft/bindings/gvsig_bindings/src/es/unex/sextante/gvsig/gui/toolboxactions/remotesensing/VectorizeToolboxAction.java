

package es.unex.sextante.gvsig.gui.toolboxactions.remotesensing;

import javax.swing.JOptionPane;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.IProcessActions;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.raster.util.RasterUtilities;
import org.gvsig.rastertools.statistics.StatisticsProcess;
import org.gvsig.rastertools.vectorizacion.MainDialog;
import org.gvsig.rastertools.vectorizacion.MainListener;
import org.gvsig.rastertools.vectorizacion.MainPanel;
import org.gvsig.rastertools.vectorizacion.clip.ClipData;
import org.gvsig.rastertools.vectorizacion.clip.ClipListener;
import org.gvsig.rastertools.vectorizacion.clip.ui.ClipPanel;
import org.gvsig.rastertools.vectorizacion.filter.GrayConversionData;
import org.gvsig.rastertools.vectorizacion.filter.GrayConversionListener;
import org.gvsig.rastertools.vectorizacion.filter.ui.GrayConversionPanel;
import org.gvsig.rastertools.vectorizacion.stretch.StretchData;
import org.gvsig.rastertools.vectorizacion.stretch.StretchListener;
import org.gvsig.rastertools.vectorizacion.stretch.ui.StretchPanel;
import org.gvsig.rastertools.vectorizacion.vector.VectorData;
import org.gvsig.rastertools.vectorizacion.vector.VectorListener;
import org.gvsig.rastertools.vectorizacion.vector.ui.VectorPanel;

import com.iver.andami.PluginServices;

import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;


public class VectorizeToolboxAction
         extends
            ToolboxAction
         implements
            IProcessActions {

   private static final int SIZE_MAX = 20;
   private boolean          grayScaleConversion;


   @Override
   public void execute() {


      final IRasterLayer[] layers = SextanteGUI.getInputFactory().getRasterLayers();
      final Object selectedObject = JOptionPane.showInputDialog(null, PluginServices.getText(null, "Select_input_layer"),
               PluginServices.getText(null, "layer"), JOptionPane.PLAIN_MESSAGE, null, layers, null);

      if (selectedObject != null) {
         grayScaleConversion = true;
         final FLyrRasterSE lyr = (FLyrRasterSE) ((IRasterLayer) selectedObject).getBaseDataObject();

         final long size = RasterUtilities.getBytesFromRasterBufType(lyr.getDataType()[0]);

         if ((lyr.getBandCount() * lyr.getPxWidth() * lyr.getPxHeight() * size) > (SIZE_MAX * 1000000)) {
            if (!RasterToolsUtil.messageBoxYesOrNot("source_too_big", this)) {
               return;
            }
         }

         if (lyr.getBandCount() == 1) {
            if (RasterToolsUtil.messageBoxYesOrNot("datatype_not_byte", this)) {
               grayScaleConversion = false;
            }
         }
         StatisticsProcess.launcher(lyr, this);
      }

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "remotesensing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "Vectorize");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getRasterLayers().length != 0;

   }


   //code taken from the VectorizationTocMenuEntry class, by Nacho Brodin
   public void end(final Object param) {
      FLyrRasterSE lyr = null;
      if (param instanceof FLyrRasterSE) {
         lyr = (FLyrRasterSE) param;
      }
      else {
         return;
      }

      //1-Creamos los modelos de datos
      final ClipData clipData = new ClipData();
      final GrayConversionData grayConvData = new GrayConversionData();
      final StretchData stretchData = new StretchData();
      final VectorData vectorData = new VectorData();
      vectorData.setProjLayer(lyr.getMapContext().getProjection());

      //2-Creamos los paneles
      final ClipPanel clipPanel = new ClipPanel();
      final GrayConversionPanel grayConvPanel = new GrayConversionPanel();
      final StretchPanel stretchPanel = new StretchPanel();
      final VectorPanel vectorPanel = new VectorPanel();

      //3-Creamos los listener
      final ClipListener clipList = new ClipListener(lyr, clipPanel, clipData);
      final GrayConversionListener grayConvList = new GrayConversionListener(lyr, grayConvPanel, grayConvData);
      final StretchListener stretchListener = new StretchListener(lyr, stretchPanel, stretchData);
      final VectorListener vectorList = new VectorListener(lyr, vectorPanel, vectorData);

      //4-Asignamos los observadores
      clipData.addObserver(clipPanel);
      grayConvData.addObserver(grayConvPanel);
      vectorData.addObserver(vectorPanel);
      stretchData.addObserver(stretchPanel);

      //5-Panel general: creamos el modelo de datos, panel y listener
      MainPanel mainPanel = null;
      if (grayScaleConversion) {
         mainPanel = new MainPanel(lyr, grayConvList.getPreviewRender());
         mainPanel.setPanel(clipPanel);
         mainPanel.setPanel(grayConvPanel);
      }
      else {
         mainPanel = new MainPanel(lyr, stretchListener.getPreviewRender());
         mainPanel.setPanel(clipPanel);
         mainPanel.setPanel(stretchPanel);
      }
      mainPanel.setPanel(vectorPanel);
      mainPanel.initialize();

      final MainListener vectListener = new MainListener(lyr, mainPanel, grayConvList, clipList, vectorList, stretchListener);
      if (grayScaleConversion) {
         vectListener.setPreviewRender(grayConvList.getPreviewRender());
         grayConvList.setPreviewPanel(mainPanel.getPreviewBasePanel());
      }
      else {
         vectListener.setPreviewRender(stretchListener.getPreviewRender());
         stretchListener.setPreviewPanel(mainPanel.getPreviewBasePanel());
      }

      //6-Actualizamos los datos
      clipData.updateObservers();
      grayConvData.updateObservers();
      vectorData.updateObservers();
      stretchData.updateObservers();

      //7-Creamos el dialogo
      final MainDialog dialog = new MainDialog(620, 485, lyr.getName(), mainPanel);
      vectListener.setDialog(dialog);
      RasterToolsUtil.addWindow(dialog);
   }


   public void interrupted() {
   }

}
