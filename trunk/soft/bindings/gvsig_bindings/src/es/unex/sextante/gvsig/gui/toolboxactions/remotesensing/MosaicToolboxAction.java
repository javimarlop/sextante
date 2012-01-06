

package es.unex.sextante.gvsig.gui.toolboxactions.remotesensing;

import org.gvsig.remotesensing.mosaic.gui.MosaicDialog;

import com.iver.andami.PluginServices;

import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.utils.GlobalView;


public class MosaicToolboxAction
         extends
            ToolboxAction {

   @Override
   public void execute() {

      final GlobalView globalView = new GlobalView();
      globalView.update();
      final MosaicDialog mosaicDialog = new MosaicDialog(600, 500, globalView);
      PluginServices.getMDIManager().addWindow(mosaicDialog);

   }


   @Override
   public String getGroup() {

      return PluginServices.getText(null, "remotesensing");

   }


   @Override
   public String getName() {

      return PluginServices.getText(null, "Mosaic");

   }


   @Override
   public boolean isActive() {

      return SextanteGUI.getInputFactory().getRasterLayers().length != 0;

   }

}
