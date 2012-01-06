

package es.unex.sextante.arcgis.gptool;

import java.util.HashMap;
import java.util.Locale;

import es.unex.sextante.arcgis.dataobjects.ArcOutputFactory;
import es.unex.sextante.arcgis.gui.ArcGUIFactory;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.saga.SagaAlgorithmProvider;
import es.unex.sextante.gui.saga.SagaUtils;
import es.unex.sextante.gui.settings.SextanteSagaSettings;


public class SextanteInitializer {

   private static boolean m_bAlreadyInitialized = false;


   public static void initialize() {

      if (!m_bAlreadyInitialized) {
         Locale.setDefault(Locale.ENGLISH);
         final String sPath = System.getProperty("user.dir") + "\\..\\java\\lib\\ext";
         Sextante.initialize(sPath);
         final HashMap<String, String> map = new HashMap<String, String>();
         map.put(SagaUtils.SAGACMD_USE_START_PARAMETER, Boolean.TRUE.toString());
         map.put(SextanteSagaSettings.SAGA_ACTIVATE, Boolean.TRUE.toString());
         SextanteGUI.setSextantePath(sPath);
         SextanteGUI.addAlgorithmProvider(new SagaAlgorithmProvider());
         //SextanteGUI.addAlgorithmProvider(new RAlgorithmProvider());
         SextanteGUI.setCustomDefaultSettings(map);
         //SextanteGUI.addAlgorithmProvider(new GrassAlgorithmProvider());
         SextanteGUI.initialize(sPath);
         SextanteGUI.setOutputFactory(new ArcOutputFactory());
         SextanteGUI.setGUIFactory(new ArcGUIFactory());
         m_bAlreadyInitialized = true;
         System.out.println("* SEXTANTE was correctly initialized with " + Integer.toString(Sextante.getAlgorithmsCount())
                            + " algorithms");
      }
   }
}
