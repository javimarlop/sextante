

package es.unex.sextante.gvsig.gui.toolboxactions.remotesensing;

import org.gvsig.remotesensing.classification.gui.ClassificationPanel;

import com.iver.andami.PluginServices;

import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.utils.GlobalView;


public class ClassificationToolboxAction
         extends
            ToolboxAction {

   @Override
   public void execute() {

      final GlobalView globalView = new GlobalView();
      globalView.update();
      PluginServices.getMDIManager().addWindow(globalView);
      final ClassificationPanel clasificationDialog = new ClassificationPanel(globalView);
      PluginServices.getMDIManager().addWindow(clasificationDialog);

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "remotesensing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "Classification");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getRasterLayers().length != 0;

   }

}
