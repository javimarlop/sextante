package es.unex.sextante.gvsig.gui;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.gui.core.IPostProcessTaskFactory;

public class gvPostProcessTaskFactory
         implements
            IPostProcessTaskFactory {

   public Runnable getPostProcessTask(final GeoAlgorithm alg,
                                      final boolean bShowResultsWindow) {

      return new gvPostProcessTask(alg, bShowResultsWindow);

   }

}
