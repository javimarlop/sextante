

package es.unex.sextante.gvsig.gui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;

import es.unex.sextante.core.AbstractInputFactory;
import es.unex.sextante.core.NamedExtent;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.gvsig.core.FileTools;
import es.unex.sextante.gvsig.core.gvRasterLayer;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;


public class gvInputFactory
         extends
            AbstractInputFactory {

   public void createDataObjects() {

      final ArrayList list = new ArrayList();

      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList views = project.getDocumentsByType(ProjectViewFactory.registerName);
      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers layers = view.getMapContext().getLayers();
         final LayersIterator iter = new LayersIterator(layers);
         while (iter.hasNext()) {
            try {
               final FLayer layer = iter.nextLayer();
               if (layer instanceof FLyrRasterSE) {
                  final gvRasterLayer rasterLayer = new gvRasterLayer();
                  rasterLayer.create((FLyrRasterSE) layer);
                  list.add(rasterLayer);
               }
               else if (layer instanceof FLyrVect) {
                  final gvVectorLayer vectorLayer = new gvVectorLayer();
                  vectorLayer.create((FLyrVect) layer);
                  list.add(vectorLayer);
               }
            }
            catch (final Exception e) {
               //if an error occur with a given layer, we simply ignore that layer
               //and do not add it to the collection of data object.
               //This happens, for instance, with layers that are not visible, since,
               //for some reason, they are not available for analysis.
            }
         }
      }

      final ArrayList tables = project.getDocumentsByType(ProjectTableFactory.registerName);
      for (int i = 0; i < tables.size(); i++) {
         final gvTable table = new gvTable();
         table.create((ProjectTable) tables.get(i));
         list.add(table);
      }

      m_Objects = new IDataObject[list.size()];
      for (int i = 0; i < list.size(); i++) {
         m_Objects[i] = (IDataObject) list.get(i);
      }

   }


   public NamedExtent[] getPredefinedExtents() {

      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList views = project.getDocumentsByType(ProjectViewFactory.registerName);
      final NamedExtent ne[] = new NamedExtent[views.size()];
      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final Rectangle2D extent = view.getMapContext().getViewPort().getAdjustedExtent();
         final String sName = view.getName();
         ne[i] = new NamedExtent(sName, extent);
      }

      return ne;
   }


   public String[] getRasterLayerInputExtensions() {

      return FileTools.RASTER_EXT_IN;

   }


   public String[] get3DRasterLayerInputExtensions() {

      return FileTools.RASTER3D_EXT_IN;

   }


   public String[] getVectorLayerInputExtensions() {

      return FileTools.VECTOR_EXT_IN;

   }


   public String[] getTableInputExtensions() {

      return FileTools.TABLE_EXT;

   }


   public IDataObject openDataObjectFromFile(final String sFilename) {

      final Object object = FileTools.open(sFilename);

      if (object == null) {
         return null;
      }
      else if (object instanceof FLyrRasterSE) {
         final gvRasterLayer layer = new gvRasterLayer();
         layer.create((FLyrRasterSE) object);
         return layer;
      }
      else if (object instanceof FLyrVect) {
         final gvVectorLayer layer = new gvVectorLayer();
         layer.create((FLyrVect) object);
         return layer;
      }
      else if (object instanceof ProjectTable) {
         final gvTable table = new gvTable();
         table.create((ProjectTable) object);
         return table;
      }
      else if (object instanceof I3DRasterLayer) {
         return (I3DRasterLayer) object;
      }
      else {
         return null;
      }

   }


   public void close(final String sName) {

      final IDataObject dataObject = this.getInputFromName(sName);

      final Object obj = dataObject.getBaseDataObject();
      if (obj instanceof FLayer) {
         removeLayer((FLayer) obj);
      }
      else {
         final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
         project.delDocument((ProjectDocument) obj);
      }

      removeDataObject(sName);

   }


   private static void removeLayer(final FLayer baseLayer) {

      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList<ProjectDocument> views = project.getDocumentsByType(ProjectViewFactory.registerName);
      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers layers = view.getMapContext().getLayers();
         final LayersIterator iter = new LayersIterator(layers);
         while (iter.hasNext()) {
            final FLayer layer = iter.nextLayer();
            if (layer.equals(baseLayer)) {
               layers.removeLayer(baseLayer);
               return;
            }
         }

      }

   }


   @Override
   public I3DRasterLayer[] get3DRasterLayers() {

      return new I3DRasterLayer[0];

   }


}
