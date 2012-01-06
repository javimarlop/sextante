package es.unex.sextante.gvsig.extensions;

import com.iver.andami.plugins.Extension;

import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.history.History;


public class SextanteHistoryExtension
         extends
            Extension {

   public void initialize() {

      History.startSession();

   }


   public void execute(final String actionCommand) {

      SextanteGUI.getGUIFactory().showHistoryDialog();

   }


   public boolean isEnabled() {

      return true;

   }


   public boolean isVisible() {

      return true;

   }


}
