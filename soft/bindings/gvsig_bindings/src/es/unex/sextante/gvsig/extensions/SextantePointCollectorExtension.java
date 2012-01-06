package es.unex.sextante.gvsig.extensions;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

public class SextantePointCollectorExtension
         extends
            Extension {

   private final static String TOOL_NAME = "sextantePointCollector";


   public void execute(final String s) {
      final View vista = (View) PluginServices.getMDIManager().getActiveWindow();
      final MapControl mapCtrl = vista.getMapControl();

      final PointSelectorListener psl = new PointSelectorListener();
      final StatusBarListener sbl = new StatusBarListener(mapCtrl);
      mapCtrl.addMapTool(TOOL_NAME, new Behavior[] { new PointBehavior(psl), new MouseMovementBehavior(sbl) });
      mapCtrl.setTool(TOOL_NAME);

   }


   /**
    * @see com.iver.mdiApp.plugins.IExtension#isVisible()
    */
   public boolean isEnabled() {
      final com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

      if (f == null) {
         return false;
      }

      return (f.getClass() == View.class);

   }


   /**
    * @see com.iver.andami.plugins.IExtension#initialize()
    */
   public void initialize() {


   }


   /**
    * @see com.iver.andami.plugins.IExtension#isEnabled()
    */
   public boolean isVisible() {

      final com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();

      if (f == null) {
         return false;
      }

      return (f instanceof View);

   }
}
