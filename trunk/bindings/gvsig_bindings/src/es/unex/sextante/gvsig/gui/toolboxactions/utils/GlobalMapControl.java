

package es.unex.sextante.gvsig.gui.toolboxactions.utils;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;


public class GlobalMapControl
         extends
            MapControl {

   private final GlobalMapContext _mapContext;


   public GlobalMapControl() {

      _mapContext = new GlobalMapContext();

   }


   @Override
   public MapContext getMapContext() {

      return _mapContext;

   }

}
