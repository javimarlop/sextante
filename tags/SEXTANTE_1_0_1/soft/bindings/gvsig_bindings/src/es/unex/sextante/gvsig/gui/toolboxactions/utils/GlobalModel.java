

package es.unex.sextante.gvsig.gui.toolboxactions.utils;

import java.beans.PropertyChangeListener;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.gui.WindowData;
import com.iver.cit.gvsig.project.documents.view.IProjectView;


public class GlobalModel
         implements
            IProjectView {

   private final GlobalMapContext _mapContext;


   public GlobalModel() {
      _mapContext = new GlobalMapContext();
   }


   @Override
   public void addPropertyChangeListener(final PropertyChangeListener listener) {
   }


   @Override
   public String getExtLink() {
      return null;
   }


   @Override
   public MapContext getMapContext() {
      return _mapContext;
   }


   @Override
   public MapContext getMapOverViewContext() {
      return null;
   }


   @Override
   public String getName() {
      return "";
   }


   @Override
   public Project getProject() {
      return null;
   }


   @Override
   public String getSelectedField() {
      return null;
   }


   @Override
   public int getTypeLink() {
      return 0;
   }


   @Override
   public void setExtLink(final String s) {
   }


   @Override
   public void setMapContext(final MapContext fmap) {
   }


   @Override
   public void setMapOverViewContext(final MapContext fmap) {
   }


   @Override
   public void setSelectedField(final String s) {
   }


   @Override
   public void setTypeLink(final int i) {
   }


   @Override
   public void storeWindowData(final WindowData data) {
   }

}
