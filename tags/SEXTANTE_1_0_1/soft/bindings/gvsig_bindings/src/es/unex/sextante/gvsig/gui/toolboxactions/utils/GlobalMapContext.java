

package es.unex.sextante.gvsig.gui.toolboxactions.utils;

import org.cresques.cts.IProjection;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.project.Project;


public class GlobalMapContext
         extends
            MapContext {


   protected FLayers _layers;


   public GlobalMapContext() {

      super(new ViewPort(Project.getDefaultProjection()));

      _layers = new GlobalFLayers();

   }


   @Override
   public FLayers getLayers() {
      return _layers;
   }


   @Override
   public void invalidate() {

   }


   @Override
   public IProjection getProjection() {
      return null;
   }


   @Override
   public void setProjection(final IProjection proj) {

   }


   @Override
   public void beginAtomicEvent() {

   }


   @Override
   public void endAtomicEvent() {

   }


}
