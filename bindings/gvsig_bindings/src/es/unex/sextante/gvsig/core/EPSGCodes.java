package es.unex.sextante.gvsig.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import es.unex.sextante.gui.core.SextanteGUI;

public class EPSGCodes {

   private static String[] m_Codes;


   public static String[] getCodes() {

      return m_Codes;

   }


   public static void loadCodes() {

      final ArrayList<String> codes = new ArrayList<String>();
      InputStreamReader isr = null;
      BufferedReader br = null;
      String line = null;
      try {
         final URL url = SextanteGUI.class.getClassLoader().getResource("data/epsg.data");
         isr = new InputStreamReader(url.openStream());
         br = new BufferedReader(isr);
         while ((line = br.readLine()) != null) {
            codes.add(line);
         }
         m_Codes = (String[]) codes.toArray(new String[0]);
      }
      catch (final Exception e) {
         //Sextante.addErrorToLog(e);
      }
      finally {
         try {
            br.close();
            isr.close();
         }
         catch (final Exception e) {
            //Sextante.addErrorToLog(e);
         }
      }


   }

}
