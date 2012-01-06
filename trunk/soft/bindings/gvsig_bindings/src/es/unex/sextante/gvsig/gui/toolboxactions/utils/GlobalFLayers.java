

package es.unex.sextante.gvsig.gui.toolboxactions.utils;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;

import es.unex.sextante.core.Sextante;


public class GlobalFLayers
         extends
            FLayers {

   protected ArrayList<FLayer> _layers = new ArrayList<FLayer>();
   private MapContext          _outputMapContext;
   private FLayer              _lastUsedLayer;


   public GlobalFLayers() {

      update();

   }


   @Override
   public void addLayer(final FLayer layer) {

      if (_outputMapContext == null) {

         final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
         final ArrayList<ProjectDocument> views = project.getDocumentsByType(ProjectViewFactory.registerName);

         for (int i = 0; i < views.size(); i++) {
            final ProjectView view = (ProjectView) views.get(i);
            final FLayers flayers = view.getMapContext().getLayers();
            for (int j = 0; j < flayers.getLayersCount(); j++) {
               final FLayer lyr = flayers.getLayer(j);
               if (lyr.equals(_lastUsedLayer)) {
                  _outputMapContext = view.getMapContext();
                  break;
               }

               final ArrayList layerList = new ArrayList();
               splitLayerGroup(lyr, layerList);
               for (int k = 0; k < layerList.size(); k++) {
                  final FLayer lyr2 = ((FLayer) layerList.get(k));
                  if (lyr2.equals(_lastUsedLayer)) {
                     _outputMapContext = view.getMapContext();
                     break;
                  }
               }
            }
         }

         if (_outputMapContext == null) {
            final Object[] options = new Object[views.size() + 1];
            options[0] = Sextante.getText("Create_new_view");
            for (int i = 0; i < views.size(); i++) {
               options[i + 1] = views.get(i);
            }

            final Object selectedObject = JOptionPane.showInputDialog(null, Sextante.getText("Select_output_view"),
                     Sextante.getText("Output_view"), JOptionPane.PLAIN_MESSAGE, null, options, null);

            if (selectedObject instanceof ProjectView) {
               _outputMapContext = ((ProjectView) selectedObject).getMapContext();
            }
            else {
               final ProjectView newView = ProjectFactory.createView("View");
               ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject().addDocument(newView);
               final IWindow window = newView.createWindow();
               final Runnable doWorkRunnable = new Runnable() {
                  public void run() {
                     PluginServices.getMDIManager().addWindow(window);
                     _outputMapContext = newView.getMapContext();
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
      }

      _outputMapContext.beginAtomicEvent();
      _outputMapContext.getLayers().addLayer(layer);
      _outputMapContext.endAtomicEvent();
      _outputMapContext.invalidate();

      //we can assume that the process has already run and we can remove the phantom view.
      PluginServices.getMDIManager().closeWindow(GlobalView.getGlobalView());

   }


   @Override
   public void addLayer(final int pos,
                        final FLayer layer) {

      addLayer(layer);

   }


   @Override
   public FLayer getLayer(final int index) {
      _lastUsedLayer = _layers.get(index);
      return _lastUsedLayer;

   }


   /*
    * (non-Javadoc)
    * @see com.iver.cit.gvsig.fmap.layers.layerOperations.LayerCollection#getLayer(java.lang.String)
    */
   @Override
   public FLayer getLayer(final String layerName) {
      FLayer lyr;
      FLayer lyr2;
      ArrayList layerList;

      for (int i = 0; i < _layers.size(); i++) {
         lyr = (_layers.get(i));

         if (lyr.getName().compareToIgnoreCase(layerName) == 0) {
            _lastUsedLayer = lyr;
            return lyr;
         }

         layerList = new ArrayList();
         splitLayerGroup(lyr, layerList);
         for (int j = 0; j < layerList.size(); j++) {
            lyr2 = ((FLayer) layerList.get(j));
            if (lyr2.getName().compareToIgnoreCase(layerName) == 0) {
               _lastUsedLayer = lyr2;
               return lyr2;
            }
         }
      }

      return null;
   }


   private void splitLayerGroup(final FLayer layer,
                                final ArrayList result) {
      int i;
      FLayers layerGroup;
      if (layer instanceof FLayers) {
         layerGroup = (FLayers) layer;
         for (i = 0; i < layerGroup.getLayersCount(); i++) {
            splitLayerGroup(layerGroup.getLayer(i), result);
         }
      }
      else {
         result.add(layer);
      }
   }


   @Override
   public int getLayersCount() {
      return _layers.size();
   }


   @Override
   public MapContext getMapContext() {
      return null;
   }


   public FLayer getLastUsedLayer() {

      return _lastUsedLayer;

   }


   public void update() {

      _layers.clear();
      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList views = project.getDocumentsByType(ProjectViewFactory.registerName);
      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers layers = view.getMapContext().getLayers();
         //return layers;
         final LayersIterator iter = new LayersIterator(layers);
         while (iter.hasNext()) {
            final FLayer layer = iter.nextLayer();
            if ((layer instanceof FLyrRasterSE) || (layer instanceof FLyrVect)) {
               _layers.add(layer);
            }
         }
      }

      _outputMapContext = null;

   }

}
