

package es.unex.sextante.arcgis.results;

import java.io.IOException;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.interop.AutomationException;

import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.core.SextanteGUI;


public class SextanteResultsButton
         extends
            Button {

   /**
    * Called when the button is clicked.
    * 
    * @exception java.io.IOException
    *                    if there are interop problems.
    * @exception com.esri.arcgis.interop.AutomationException
    *                    if the component throws an ArcObjects exception.
    */
   @Override
   public void onClick() throws IOException, AutomationException {

      SextanteGUI.getGUIFactory().showAdditionalResultsDialog(AdditionalResults.getComponents());

   }

}
