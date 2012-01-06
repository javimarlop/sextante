

package es.unex.sextante.gvsig.gui.toolboxactions.remotesensing;

import org.gvsig.remotesensing.decisiontrees.gui.DecisionTreeDialog;

import com.iver.andami.PluginServices;

import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.utils.GlobalView;


public class DecisionTreeToolboxAction
         extends
            ToolboxAction {

   @Override
   public void execute() {

      final GlobalView globalView = new GlobalView();
      globalView.update();
      PluginServices.getMDIManager().addWindow(globalView);
      final DecisionTreeDialog decisionTreeDialog = new DecisionTreeDialog(600, 500, globalView);
      PluginServices.getMDIManager().addWindow(decisionTreeDialog);

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "remotesensing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "DecisionTree");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getRasterLayers().length != 0;

   }

}
