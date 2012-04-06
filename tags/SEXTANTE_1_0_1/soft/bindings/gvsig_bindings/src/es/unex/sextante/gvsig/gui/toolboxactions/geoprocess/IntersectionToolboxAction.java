

package es.unex.sextante.gvsig.gui.toolboxactions.geoprocess;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoProcessingOverlayPanel2;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoprocessPaneContainer;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.intersection.IntersectionGeoprocessController;

import es.unex.sextante.core.AbstractInputFactory;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.utils.GlobalFLayers;


public class IntersectionToolboxAction
         extends
            ToolboxAction {

   @Override
   public void execute() {

      final IGeoprocessUserEntries panel = new GeoProcessingOverlayPanel2(new GlobalFLayers(), getName());
      final GeoprocessPaneContainer container = new GeoprocessPaneContainer((JPanel) panel);
      final IntersectionGeoprocessController controller = new IntersectionGeoprocessController();
      controller.setView(panel);
      container.setCommand(controller);
      container.validate();
      container.repaint();
      PluginServices.getMDIManager().addWindow(container);

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "Geoprocessing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "Intersection");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getVectorLayers(AbstractInputFactory.SHAPE_TYPE_ANY).length != 0;

   }

}
