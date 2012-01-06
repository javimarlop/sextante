

package es.unex.sextante.gvsig.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.gui.cmd.BSHDialog;
import es.unex.sextante.gui.core.DefaultGUIFactory;
import es.unex.sextante.gui.core.NameAndIcon;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.core.ToolboxAction;
import es.unex.sextante.gui.dataExplorer.DataExplorerDialog;
import es.unex.sextante.gui.exceptions.WrongViewNameException;
import es.unex.sextante.gui.grass.GrassAlgorithmProvider;
import es.unex.sextante.gui.history.HistoryDialog;
import es.unex.sextante.gui.modeler.ModelAlgorithm;
import es.unex.sextante.gvsig.extensions.VersionChecker;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.BufferToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.ClipToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.DifferenceToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.DissolveToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.IntersectionToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.MergeToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.SpatialJoinToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.geoprocess.UnionToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.remotesensing.ClassificationToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.remotesensing.DecisionTreeToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.remotesensing.MosaicToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.remotesensing.TransformationToolboxAction;
import es.unex.sextante.gvsig.gui.toolboxactions.remotesensing.VectorizeToolboxAction;


public class gvGUIFactory
         extends
            DefaultGUIFactory {

   private final static ImageIcon GVSIG_ICON                       = new ImageIcon(
                                                                            GrassAlgorithmProvider.class.getClassLoader().getResource(
                                                                                     "images/gvsig.gif"));


   public static final String     IS_NOT_FIRST_TIME_USING_SEXTANTE = "isFirstTimeUsingSextante";


   @Override
   public void showToolBoxDialog() {

      showFirstTimeWarning();

      final ToolboxDialog toolbox = new ToolboxDialog();
      m_Toolbox = toolbox.getToolboxPanel();
      PluginServices.getMDIManager().addWindow(toolbox);

   }


   private URL getCEWarningWebpageURL() throws MalformedURLException {

      return new File(SextanteGUI.getSextantePath() + File.separator + "ce_warning.html").toURL();

   }


   private void showFirstTimeWarning() {

      final boolean b = new Boolean(SextanteGUI.getSettingParameterValue(IS_NOT_FIRST_TIME_USING_SEXTANTE
                                                                         + Sextante.getVersionNumber())).booleanValue();

      if (!b) {
         final JEditorPane jEditorPane = new JEditorPane();
         jEditorPane.setEditable(false);
         final JScrollPane scrollPane = new JScrollPane();
         scrollPane.setPreferredSize(new Dimension(1000, 800));
         scrollPane.setSize(new Dimension(1000, 800));
         scrollPane.setMaximumSize(new Dimension(1000, 800));
         scrollPane.setViewportView(jEditorPane);
         jEditorPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
         jEditorPane.setContentType("text/html");
         try {
            if (VersionChecker.isGvSIGCE()) {
               jEditorPane.setPage(getFirstTimeWebpageURL());
            }
            else {
               jEditorPane.setPage(getCEWarningWebpageURL());
            }
         }
         catch (final Exception e) {
            return;
         }
         jEditorPane.setPreferredSize(new Dimension(600, 500));
         jEditorPane.setCaretPosition(0);

         showGenericInfoDialog(scrollPane, "SEXTANTE");
         SextanteGUI.setSettingParameterValue(IS_NOT_FIRST_TIME_USING_SEXTANTE + Sextante.getVersionNumber(),
                  Boolean.TRUE.toString());
         SextanteGUI.saveSettings();

      }

   }


   private URL getFirstTimeWebpageURL() throws MalformedURLException {

      return new File(SextanteGUI.getSextantePath() + File.separator + "first_time.html").toURL();

   }


   @Override
   public void showModelerDialog() {

      showFirstTimeWarning();

      final ModelerDialog dialog = new ModelerDialog(m_Toolbox);
      PluginServices.getMDIManager().addWindow(dialog);


   }


   @Override
   public void showModelerDialog(final ModelAlgorithm alg) {

      final ModelerDialog dialog = new ModelerDialog(m_Toolbox);
      dialog.getModelerPanel().checkChangesAndOpenModel(alg.getFilename());
      PluginServices.getMDIManager().addWindow(dialog);

   }


   @Override
   public void showHistoryDialog() {

      showFirstTimeWarning();

      SextanteGUI.getInputFactory().createDataObjects();

      final HistoryDialog dialog = new HistoryDialog(SextanteGUI.getMainFrame());
      SextanteGUI.setLastCommandOrigin(SextanteGUI.HISTORY);
      SextanteGUI.setLastCommandOriginParentDialog(dialog);
      m_History = dialog.getHistoryPanel();
      dialog.pack();
      dialog.setVisible(true);

      if (m_Toolbox == null) {
         SextanteGUI.getInputFactory().clearDataObjects();
      }

      m_History = null;

   }


   @Override
   public void showCommandLineDialog() {

      showFirstTimeWarning();

      SextanteGUI.getInputFactory().createDataObjects();

      final BSHDialog dialog = new BSHDialog(SextanteGUI.getMainFrame());
      SextanteGUI.setLastCommandOrigin(SextanteGUI.COMMANDLINE);
      SextanteGUI.setLastCommandOriginParentDialog(dialog);
      dialog.pack();
      dialog.setVisible(true);

      if (m_Toolbox == null) {
         SextanteGUI.getInputFactory().clearDataObjects();
      }

   }


   @Override
   public void showDataExplorer() {

      showFirstTimeWarning();

      SextanteGUI.getInputFactory().createDataObjects();

      final DataExplorerDialog dialog = new DataExplorerDialog(SextanteGUI.getMainFrame());
      dialog.pack();
      dialog.setVisible(true);

      if (m_Toolbox == null) {
         SextanteGUI.getInputFactory().clearDataObjects();
      }

   }


   @Override
   public void updateToolbox() {

      super.updateToolbox();

   }


   public void setToolboxHidden() {

      m_Toolbox = null;

   }


   @Override
   public void addToView(final IDataObject obj,
                         final String viewName) throws WrongViewNameException {

      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList<ProjectDocument> views = project.getDocumentsByType(ProjectViewFactory.registerName);
      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers layers = view.getMapContext().getLayers();
         final Object layer = obj.getBaseDataObject();
         if (layer instanceof FLayer) {
            layers.addLayer((FLayer) layer);
         }
         return;
      }

      throw new WrongViewNameException();

   }


   @Override
   public HashMap<NameAndIcon, ArrayList<ToolboxAction>> getToolboxActions() {

      final HashMap<NameAndIcon, ArrayList<ToolboxAction>> map = new HashMap<NameAndIcon, ArrayList<ToolboxAction>>();
      if (VersionChecker.isGvSIGCE()) {
         final NameAndIcon nai = new NameAndIcon("gvSIG", GVSIG_ICON);
         final ArrayList<ToolboxAction> algs = new ArrayList<ToolboxAction>();
         algs.add(new BufferToolboxAction());
         algs.add(new DifferenceToolboxAction());
         algs.add(new ClipToolboxAction());
         algs.add(new DissolveToolboxAction());
         algs.add(new MergeToolboxAction());
         algs.add(new SpatialJoinToolboxAction());
         algs.add(new IntersectionToolboxAction());
         algs.add(new UnionToolboxAction());
         algs.add(new MosaicToolboxAction());
         algs.add(new DecisionTreeToolboxAction());
         algs.add(new TransformationToolboxAction());
         algs.add(new ClassificationToolboxAction());
         algs.add(new VectorizeToolboxAction());
         map.put(nai, algs);
      }

      return map;

   }


   @Override
   public void showGenericDialog(final String sName,
                                 final Component component) {

      final GenericDialog dialog = new GenericDialog(sName, component);
      PluginServices.getMDIManager().addWindow(dialog);

   }
}
