package es.unex.sextante.gvsig.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.WindowInfo;

import es.unex.sextante.gui.modeler.ModelerPanel;
import es.unex.sextante.gui.toolbox.ToolboxPanel;


public class ModelerDialog
         extends
            JPanel
         implements
            IWindow,
            IWindowListener {

   private WindowInfo   viewInfo;
   private ModelerPanel m_Panel;


   //private final ToolboxPanel m_Toolbox;


   public ModelerDialog(final ToolboxPanel toolbox) {

      super();

      //      m_Toolbox = toolbox;
      //
      //      if (SextanteGUI.getInputFactory().getDataObjects() == null) {
      //         SextanteGUI.getInputFactory().createDataObjects();
      //      }

      initGUI();

   }


   private void initGUI() {

      m_Panel = new ModelerPanel(null);
      final BorderLayout thisLayout = new BorderLayout();
      this.setLayout(thisLayout);
      this.setSize(new Dimension(m_Panel.getWidth(), m_Panel.getHeight()));
      this.add(m_Panel);

   }


   public WindowInfo getWindowInfo() {

      if (viewInfo == null) {
         viewInfo = new WindowInfo(WindowInfo.MAXIMIZABLE | WindowInfo.RESIZABLE | WindowInfo.MODELESSDIALOG);
         viewInfo.setTitle(PluginServices.getText(this, "SEXTANTE - " + "Modeler"));
      }
      return viewInfo;

   }


   public Object getWindowProfile() {
      return WindowInfo.DIALOG_PROFILE;
   }


   public void windowActivated() {}


   public void windowClosed() {

   //      if (m_Toolbox == null) {
   //         SextanteGUI.getInputFactory().clearDataObjects();
   //      }

   }


   public ModelerPanel getModelerPanel() {

      return m_Panel;

   }


}
