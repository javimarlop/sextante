

package es.unex.sextante.arcgis.gui;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import es.unex.sextante.gui.core.DefaultGUIFactory;
import es.unex.sextante.gui.core.SextanteGUI;


public class ArcGUIFactory
         extends
            DefaultGUIFactory {

   private boolean m_bIsResultsDialogVisible = false; ;


   @Override
   public void showAdditionalResultsDialog(final ArrayList components) {

      if ((components.size() != 0) && !m_bIsResultsDialogVisible) {
         m_bIsResultsDialogVisible = true;
         final Runnable runnable = new Runnable() {
            public void run() {
               final AdditionalResultsDialog dialog = new AdditionalResultsDialog(components, SextanteGUI.getMainFrame());
               dialog.pack();
               dialog.setVisible(true);
            }
         };

         if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
         }
         else {
            try {
               SwingUtilities.invokeAndWait(runnable);
            }
            catch (final Exception e) {
            }
         }
      }

   }


   public void setIsResultsDialogVisible(final boolean b) {

      m_bIsResultsDialogVisible = b;

   }

}
