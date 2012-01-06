

package es.unex.sextante.gvsig.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocumentListener;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.toolbox.IToolboxDialog;
import es.unex.sextante.gui.toolbox.ToolboxPanel;


public class ToolboxDialog
         extends
            JPanel
         implements
            SingletonWindow,
            IWindowListener,
            LayerCollectionListener,
            PropertyChangeListener,
            ProjectDocumentListener,
            IToolboxDialog {

   private static final long        serialVersionUID = 1L;
   private final ArrayList<FLayers> listLayers       = new ArrayList<FLayers>();
   private WindowInfo               viewInfo;
   private ToolboxPanel             m_Panel;


   public ToolboxDialog() {

      super();

      if (SextanteGUI.getInputFactory().getDataObjects() == null) {
         SextanteGUI.getInputFactory().createDataObjects();
      }

      initGUI();
      addListeners();

   }


   private void addListeners() {

      //Register a propertyChangeListener to capture events when a new document is added in gvSIG
      final Project p = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      p.addPropertyChangeListener(this);

      //Register addLayerCollectionListener in existing views
      final IWindow[] window = PluginServices.getMDIManager().getAllWindows();
      for (int i = 0; i < window.length; i++) {
         if (window[i] instanceof BaseView) {
            final FLayers layers = ((BaseView) window[i]).getMapControl().getMapContext().getLayers();
            if (listLayers.indexOf(layers) == -1) {
               layers.addLayerCollectionListener(this);
               listLayers.add(layers);
            }
         }
      }


   }


   private void removeListeners() {
      return;
   }


   private void initGUI() {

      ImageIcon img;
      final URL res = getClass().getClassLoader().getResource("images/sextante_toolbox.png");
      if (res != null) {
         img = new ImageIcon(res);
      }
      else {
         img = null;
      }

      m_Panel = new ToolboxPanel(this, null, img);
      final BorderLayout thisLayout = new BorderLayout();
      this.setLayout(thisLayout);
      this.setSize(new Dimension(m_Panel.getWidth(), m_Panel.getHeight()));
      this.add(m_Panel);

   }


   public WindowInfo getWindowInfo() {

      if (viewInfo == null) {
         viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG | WindowInfo.RESIZABLE);
      }
      return viewInfo;

   }


   public Object getWindowProfile() {
      return WindowInfo.TOOL_PROFILE;
   }


   public Object getWindowModel() {

      return "SEXTANTE";

   }


   public ToolboxPanel getToolboxPanel() {

      return m_Panel;
   }


   public void cancel() {

      removeListeners();

      if (PluginServices.getMainFrame() == null) {
         ((JDialog) (getParent().getParent().getParent().getParent())).dispose();
      }
      else {
         PluginServices.getMDIManager().closeWindow(ToolboxDialog.this);
      }

   }


   public void windowActivated() {
   }


   public void windowClosed() {

      removeListeners();
      SextanteGUI.getInputFactory().clearDataObjects();
      ((gvGUIFactory) SextanteGUI.getGUIFactory()).setToolboxHidden();

   }


   /**
    * Event thrown when a new document is added
    */
   public void propertyChange(final PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("addDocument")) {
         if (evt.getNewValue() instanceof ProjectView) {
            final ProjectView pd = (ProjectView) evt.getNewValue();
            pd.addProjectViewListener(this);
         }
      }
   }


   /**
    * Event thrown when a window (View) is created. This method registers a listener in FLayers. When a new layer is going to be
    * added or removed the methods layerAdded and layerRemoved will catch this event.
    */
   public void windowCreated(final IWindow window) {
      if (window instanceof BaseView) {
         final FLayers layers = ((BaseView) window).getMapControl().getMapContext().getLayers();
         if (listLayers.indexOf(layers) == -1) {
            layers.addLayerCollectionListener(this);
            listLayers.add(layers);
         }
      }
   }


   public void layerAdded(final LayerCollectionEvent e) {

      // this could be done more elegantly, just adding the new layers...
      // but it works fine this way ;-)
      SextanteGUI.getInputFactory().clearDataObjects();
      SextanteGUI.getInputFactory().createDataObjects();

   }


   public void layerRemoved(final LayerCollectionEvent e) {

      SextanteGUI.getInputFactory().clearDataObjects();
      SextanteGUI.getInputFactory().createDataObjects();

   }


   public void layerMoved(final LayerPositionEvent e) {
   }


   public void layerAdding(final LayerCollectionEvent e) throws CancelationException {
   }


   public void layerMoving(final LayerPositionEvent e) throws CancelationException {
   }


   public void layerRemoving(final LayerCollectionEvent e) throws CancelationException {
   }


   public void visibilityChanged(final LayerCollectionEvent e) throws CancelationException {
   }


   public JDialog getDialog() {
      return null;
   }


   /*
    * (non-Javadoc)
    * @see es.unex.sextante.gui.toolbox.IToolboxDialog#setAlgorithmsCount(int)
    */
   public void setAlgorithmsCount(final int iCount) {
      getWindowInfo().setTitle("SEXTANTE - " + Integer.toString(iCount) + " " + Sextante.getText("Algorithms"));
   }

}
