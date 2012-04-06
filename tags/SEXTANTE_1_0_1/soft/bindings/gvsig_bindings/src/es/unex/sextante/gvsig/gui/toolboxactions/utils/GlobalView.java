

package es.unex.sextante.gvsig.gui.toolboxactions.utils;

import java.awt.Color;

import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.MapOverview;
import com.iver.cit.gvsig.project.documents.view.ProjectViewBase;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.console.JConsole;
import com.iver.utiles.console.JDockPanel;
import com.iver.utiles.console.ResponseListener;


public class GlobalView
         extends
            View {


   private static final String VIEW_NAME = "SEXTANTE_HIDDEN_GLOBAL_VIEW";

   private static GlobalView   m_View;

   private final MapControl    _mapControl;
   private final IProjectView  _model;

   static {
      m_View = new GlobalView();
   }


   public GlobalView() {
      super();
      _model = new GlobalModel();
      _mapControl = new GlobalMapControl();
   }


   @Override
   public void initialize() {
   }


   @Override
   public void setModel(final ProjectViewBase model) {
   }


   @Override
   public JConsole getConsolePanel() {
      return null;
   }


   private JDockPanel getDockConsole() {
      return null;
   }


   @Override
   public void addConsoleListener(final String prefix,
                                  final ResponseListener listener) {
   }


   @Override
   public void removeConsoleListener(final ResponseListener listener) {
   }


   @Override
   public void focusConsole(final String text) {
   }


   @Override
   public void hideConsole() {
   }


   @Override
   public void showConsole() {
   }


   @Override
   public void windowActivated() {
   }


   @Override
   public void windowClosed() {
   }


   @Override
   public void toPalette() {
   }


   @Override
   public void restore() {
   }


   public static void setDefaultMapOverViewBackColor(final Color color) {
   }


   public static Color getDefaultMapOverViewBackColor() {
      return null;
   }


   public static Color getDefaultBackColor() {
      return null;
   }


   public static void setDefaultBackColor(final Color color) {
   }


   @Override
   public MapOverview getMapOverview() {
      return null;
   }


   @Override
   public Object getWindowProfile() {
      return null;
   }


   @Override
   public IProjectView getModel() {
      return _model;
   }


   @Override
   public MapControl getMapControl() {

      return _mapControl;
   }


   @Override
   public Object getWindowModel() {
      return _model;
   }


   @Override
   public WindowInfo getWindowInfo() {
      if (m_viewInfo == null) {
         m_viewInfo = new WindowInfo();
         m_viewInfo.setTitle(VIEW_NAME);
         m_viewInfo.setWidth(0);
         m_viewInfo.setHeight(0);
      }
      return m_viewInfo;
   }


   public void update() {

      ((GlobalFLayers) getModel().getMapContext().getLayers()).update();

   }


   public static GlobalView getGlobalView() {

      return m_View;

   }

}
