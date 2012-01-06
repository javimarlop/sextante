

package es.unex.sextante.gvsig.extensions;

import java.awt.Frame;
import java.io.File;
import java.util.HashMap;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.grass.GrassAlgorithmProvider;
import es.unex.sextante.gui.r.RAlgorithmProvider;
import es.unex.sextante.gui.saga.SagaAlgorithmProvider;
import es.unex.sextante.gui.settings.SextanteGrassSettings;
import es.unex.sextante.gui.settings.SextanteRSettings;
import es.unex.sextante.gui.settings.SextanteSagaSettings;
import es.unex.sextante.gvsig.core.EPSGCodes;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.gui.gvDataRenderer;
import es.unex.sextante.gvsig.gui.gvGUIFactory;
import es.unex.sextante.gvsig.gui.gvInputFactory;
import es.unex.sextante.gvsig.gui.gvPostProcessTaskFactory;


public class SextanteToolboxExtension
         extends
            Extension {

   public void initialize() {

      final HashMap<String, String> map = new HashMap<String, String>();
      map.put(gvGUIFactory.IS_NOT_FIRST_TIME_USING_SEXTANTE + Sextante.getVersionNumber(), Boolean.FALSE.toString());
      EPSGCodes.loadCodes();

      Sextante.initialize();
      SextanteGUI.setSextantePath(System.getProperty("user.dir") + File.separator + "gvSIG" + File.separator + "extensiones"
                                  + File.separator + "es.unex.sextante");
      SextanteGUI.addAlgorithmProvider(new SagaAlgorithmProvider());
      SextanteGUI.addAlgorithmProvider(new GrassAlgorithmProvider());
      SextanteGUI.addAlgorithmProvider(new RAlgorithmProvider());
      if (VersionChecker.isGvSIGCE()) {
         map.put(SextanteSagaSettings.SAGA_ACTIVATE, Boolean.TRUE.toString());
         map.put(SextanteRSettings.R_ACTIVATE, Boolean.TRUE.toString());
         map.put(SextanteGrassSettings.GRASS_ACTIVATE, Boolean.TRUE.toString());
      }
      SextanteGUI.setCustomDefaultSettings(map);
      SextanteGUI.initialize();
      SextanteGUI.setMainFrame(((Frame) PluginServices.getMainFrame()));
      SextanteGUI.setOutputFactory(new gvOutputFactory());
      SextanteGUI.setGUIFactory(new gvGUIFactory());
      SextanteGUI.setInputFactory(new gvInputFactory());
      SextanteGUI.setPostProcessTaskFactory(new gvPostProcessTaskFactory());
      SextanteGUI.setDataRenderer(new gvDataRenderer());

   }


   public void execute(final String actionCommand) {

      SextanteGUI.getGUIFactory().showToolBoxDialog();

   }


   public boolean isEnabled() {

      return true;

   }


   public boolean isVisible() {

      return true;

   }

}
