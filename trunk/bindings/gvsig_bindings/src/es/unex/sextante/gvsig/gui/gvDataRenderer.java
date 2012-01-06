

package es.unex.sextante.gvsig.gui;

import java.io.File;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

import es.unex.sextante.dataObjects.ILayer;
import es.unex.sextante.gui.core.DefaultDataRenderer;
import es.unex.sextante.gui.core.SextanteGUI;


public class gvDataRenderer
         extends
            DefaultDataRenderer {

   public gvDataRenderer() {

      super(SextanteGUI.getSextantePath() + File.separator + "rendering.data");
      open();

   }


   public Object getRenderingDataFromLayer(final ILayer layer) {

      final FLayer gvLayer = (FLayer) layer.getBaseDataObject();

      if (gvLayer instanceof FLyrVect) {
         return ((FLyrVect) gvLayer).getLegend();
      }
      else if (gvLayer instanceof FLyrRasterSE) {
         return ((FLyrRasterSE) gvLayer).getLegend();
      }

      return null;

   }


}
