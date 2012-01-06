package es.unex.sextante.gvsig.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.WindowInfo;


public class GenericDialog
         extends
            JPanel
         implements
            IWindow,
            IWindowListener {

   private WindowInfo      viewInfo;
   private final Component m_Component;
   private final String    m_sName;


   public GenericDialog(final String sName,
                        final Component component) {

      super();

      m_sName = sName;
      m_Component = component;

      initGUI();

   }


   private void initGUI() {

      final BorderLayout thisLayout = new BorderLayout();
      this.setLayout(thisLayout);
      this.setSize(new Dimension(m_Component.getWidth(), m_Component.getHeight()));
      this.add(m_Component);

   }


   public WindowInfo getWindowInfo() {

      if (viewInfo == null) {
         viewInfo = new WindowInfo(WindowInfo.MAXIMIZABLE | WindowInfo.RESIZABLE | WindowInfo.MODELESSDIALOG);
         viewInfo.setTitle(m_sName);
      }
      return viewInfo;

   }


   public Object getWindowProfile() {
      return WindowInfo.DIALOG_PROFILE;
   }


   public void windowActivated() {}


   public void windowClosed() {}


}
