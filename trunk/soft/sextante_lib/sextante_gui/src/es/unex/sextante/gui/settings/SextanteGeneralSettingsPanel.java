

package es.unex.sextante.gui.settings;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.exceptions.WrongSettingValuesException;
import es.unex.sextante.gui.toolbox.AlgorithmGroupConfiguration;
import es.unex.sextante.gui.toolbox.AlgorithmGroupsOrganizer;


public class SextanteGeneralSettingsPanel
         extends
            SettingPanel {


   private JCheckBox  jCheckBoxChangeNames;
   private JLabel     jLabelNoDataValue;
   private JLabel     jLabelToolboxSettings;
   private JLabel     jLabelConfigPath;
   private JTextField jTextFieldNoData;
   private JCheckBox  jCheckBoxUseInternalNames;
   private JButton    jButtonConfigureGroups;
   private JCheckBox  jCheckBoxShowMostRecent;


   @Override
   protected void initGUI() {

      final TableLayout thisLayout = new TableLayout(new double[][] {
               { SextanteConfigurationDialog.SPACER_SMALL, 
            	   TableLayout.FILL, TableLayout.FILL,
            	   SextanteConfigurationDialog.SPACER_SMALL },
               { SextanteConfigurationDialog.SPACER_SMALL, // row 0 (spacer)
            	   TableLayout.MINIMUM, // row 1
            	   TableLayout.MINIMUM, // row 2
            	   TableLayout.MINIMUM, // row 3
            	   TableLayout.MINIMUM, // row 4
            	   SextanteConfigurationDialog.SPACER_MEDIUM, // row 5
                   TableLayout.MINIMUM, // row 6
                   TableLayout.FILL, // row 7
                   TableLayout.MINIMUM, // row 8
                   SextanteConfigurationDialog.SPACER_SMALL } }); //row 9
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      this.setLayout(thisLayout);
      {
         final boolean bUseInternalNames = new Boolean(
                  SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.USE_INTERNAL_NAMES)).booleanValue();
         final boolean bModiFyResultsNames = new Boolean(
                  SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.MODIFY_NAMES)).booleanValue();
         final boolean bShowMostRecent = new Boolean(
                  SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.SHOW_MOST_RECENT)).booleanValue();
         jCheckBoxChangeNames = new JCheckBox();
         jCheckBoxChangeNames.setText(Sextante.getText("Modify_output_names"));
         jCheckBoxChangeNames.setSelected(bModiFyResultsNames);
         this.add(jCheckBoxChangeNames, "1, 1, 2, 1");
         jCheckBoxUseInternalNames = new JCheckBox();
         jCheckBoxUseInternalNames.setText(Sextante.getText("Use_internal_names_for_outputs"));
         jCheckBoxUseInternalNames.setSelected(bUseInternalNames);
         this.add(jCheckBoxUseInternalNames, "1, 2, 2, 2");
         jCheckBoxShowMostRecent = new JCheckBox(Sextante.getText("ShowMostRecent"));
         jCheckBoxShowMostRecent.setSelected(bShowMostRecent);
         this.add(jCheckBoxShowMostRecent, "1, 3, 2, 3");         
         jLabelNoDataValue = new JLabel();
         jLabelNoDataValue.setText(Sextante.getText("Default_no_data_value"));
         this.add(jLabelNoDataValue, "1, 4");
         jTextFieldNoData = new JTextField();
         final String sNoDataValue = Double.toString(SextanteGUI.getOutputFactory().getDefaultNoDataValue());
         jTextFieldNoData.setText(sNoDataValue);
         this.add(jTextFieldNoData, "2, 4");
         jButtonConfigureGroups = new JButton("<html><i>" + Sextante.getText("ConfigureAlgGroups") + "</i></html>");
         jButtonConfigureGroups.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
               configureGroups();
            }
         });
         this.add(jButtonConfigureGroups, "2, 6, 2, 6");
         jLabelToolboxSettings = new JLabel();
         jLabelToolboxSettings.setText(Sextante.getText("SEXTANTE_toolbox"));
         this.add(jLabelToolboxSettings, "1, 6");
         jLabelConfigPath = new JLabel();
         jLabelConfigPath.setText("<html><i>" + Sextante.getText("Config_path_label") + " " + SextanteGUI.getUserFolder() + "</i></html>");
         this.add(jLabelConfigPath, "1, 8");
      }

   }


   protected void configureGroups() {

      final AlgorithmGroupsConfigurationDialog dialog = new AlgorithmGroupsConfigurationDialog();
      dialog.setVisible(true);

      final HashMap<String, AlgorithmGroupConfiguration> map = dialog.getGrouppingsMap();
      if (map != null) {
         AlgorithmGroupsOrganizer.setConfiguration(map);
         AlgorithmGroupsOrganizer.saveSettings();
      }

   }


   @Override
   public HashMap<String, String> getValues() throws WrongSettingValuesException {

      final HashMap<String, String> map = new HashMap<String, String>();
      map.put(SextanteGeneralSettings.MODIFY_NAMES, new Boolean(jCheckBoxChangeNames.isSelected()).toString());
      map.put(SextanteGeneralSettings.SHOW_MOST_RECENT, new Boolean(jCheckBoxShowMostRecent.isSelected()).toString());
      map.put(SextanteGeneralSettings.USE_INTERNAL_NAMES, new Boolean(jCheckBoxUseInternalNames.isSelected()).toString());
      try {
         final double dValue = Double.parseDouble(jTextFieldNoData.getText());
         SextanteGUI.getOutputFactory().setDefaultNoDataValue(dValue);
      }
      catch (final Exception e) {
         throw new WrongSettingValuesException();
      }
      map.put(SextanteGeneralSettings.DEFAULT_NO_DATA_VALUE, jTextFieldNoData.getText());

      return map;

   }

}
