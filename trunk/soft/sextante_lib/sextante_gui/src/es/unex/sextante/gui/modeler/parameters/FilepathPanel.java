package es.unex.sextante.gui.modeler.parameters;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import es.unex.sextante.additionalInfo.AdditionalInfoFilepath;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.exceptions.NullParameterAdditionalInfoException;
import es.unex.sextante.gui.modeler.ModelerPanel;
import es.unex.sextante.parameters.Parameter;
import es.unex.sextante.parameters.ParameterString;

public class FilepathPanel
         extends
            ParameterPanel {

   private JTextField jTextFieldDefault;
   private JLabel     jLabelDefault;


   public FilepathPanel(final JDialog parent,
                      final ModelerPanel panel) {

      super(parent, panel);

   }


   public FilepathPanel(final ModelerPanel panel) {

      super(panel);

   }


   @Override
   protected void initGUI() {

      super.initGUI();
      
      super.setTitle(Sextante.getText("modeler_add_par_filepath"));
      
      super.setPreferredSize(new java.awt.Dimension(400, 150));
      
      try {
         {
            final TableLayout thisLayout = new TableLayout
            	(new double[][] { { TableLayoutConstants.MINIMUM, 5.0, TableLayoutConstants.FILL },
                     { TableLayoutConstants.MINIMUM, } });
            thisLayout.setHGap(5);
            thisLayout.setVGap(5);
            
            //TODO: need a boolean to indicate whether we are browsing for a folder
            
            jPanelMiddle.setLayout(thisLayout);
            {
               jLabelDefault = new JLabel();
               jPanelMiddle.add(jLabelDefault, "0, 0");
               jLabelDefault.setText(Sextante.getText("file_extension"));
            }
            {
                jTextFieldDefault = new JTextField();
                jPanelMiddle.add(jTextFieldDefault, "2, 0");
             }
         }
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
      }

   }


   @Override
   public String getParameterDescription() {

      return Sextante.getText("Filepath");

   }


   @Override
   protected boolean prepareParameter() {


      final String sDescription = jTextFieldDescription.getText();
      String[] sDefault;
      
      sDefault = new String[1];
      sDefault[0]=jTextFieldDefault.getText();
      
      if (sDescription.length() != 0) {
         final AdditionalInfoFilepath ai = new AdditionalInfoFilepath();
         ai.setExtensions(sDefault);
         m_Parameter = new ParameterString();
         m_Parameter.setParameterDescription(sDescription);
         m_Parameter.setParameterAdditionalInfo(ai);
         return true;
      }
      else {
         JOptionPane.showMessageDialog(null, Sextante.getText("Invalid_description"), Sextante.getText("Warning"),
                  JOptionPane.WARNING_MESSAGE);
         return false;
      }


   }


   @Override
   public void setParameter(final Parameter param) {

      super.setParameter(param);

      try {
         final AdditionalInfoFilepath ai = (AdditionalInfoFilepath) param.getParameterAdditionalInfo();
         jTextFieldDefault.setText(ai.getExtensions()[0]);
      }
      catch (final NullParameterAdditionalInfoException e) {
         e.printStackTrace();
      }

   }


   @Override
   public boolean parameterCanBeAdded() {

      return true;

   }

}
