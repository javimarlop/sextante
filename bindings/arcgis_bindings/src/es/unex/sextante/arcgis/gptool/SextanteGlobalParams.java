

package es.unex.sextante.arcgis.gptool;

import java.util.HashMap;


public class SextanteGlobalParams {

   public static final String             USE_ONLY_SELECTED = "USE_ONLY_SELECTED";

   private static HashMap<String, Object> m_Map             = new HashMap<String, Object>();


   public static void setParameter(final String sParamName,
                                   final Object paramValue) {

      m_Map.put(sParamName, paramValue);

   }


   public static Object getParameter(final String sParamName) {

      return m_Map.get(sParamName);

   }

}
