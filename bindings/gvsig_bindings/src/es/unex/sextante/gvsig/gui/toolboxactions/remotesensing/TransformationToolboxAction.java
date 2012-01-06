

package es.unex.sextante.gvsig.gui.toolboxactions.remotesensing;

import org.gvsig.remotesensing.tasseledcap.TransformationPanel;

import com.iver.andami.PluginServices;

import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.utils.GlobalView;


public class TransformationToolboxAction
         extends
            ToolboxAction {

   @Override
   public void execute() {

      GlobalView.getGlobalView().update();
      final TransformationPanel pcPanel = new TransformationPanel(GlobalView.getGlobalView());
      PluginServices.getMDIManager().addWindow(pcPanel);

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "remotesensing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "MultispectralTransformation");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getRasterLayers().length != 0;

   }

}
