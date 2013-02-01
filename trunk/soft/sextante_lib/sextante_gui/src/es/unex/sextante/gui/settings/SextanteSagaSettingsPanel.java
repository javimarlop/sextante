package es.unex.sextante.gui.settings;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.algorithm.FileSelectionPanel;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.saga.SagaAlgorithmProvider;
import es.unex.sextante.gui.saga.SagaExecutionException;
import es.unex.sextante.gui.saga.SagaUtils;


public class SextanteSagaSettingsPanel
extends
SettingPanel {

	private JCheckBox          jActivateCheckBox;
	private JCheckBox          jPortableCheckBox;
	private JLabel             jLabelSagaFolder;
	private FileSelectionPanel jSagaFolder;
	private JLabel             jLabelUpdate;
	private JButton            jButtonUpdate;   



	@Override
	protected void initGUI() {

		final TableLayout thisLayout = new TableLayout(new double[][] {
				{ SextanteConfigurationDialog.SPACER_SMALL,
					TableLayoutConstants.FILL,
					TableLayoutConstants.FILL,
					SextanteConfigurationDialog.SPACER_SMALL},
					{ SextanteConfigurationDialog.SPACER_SMALL,
						TableLayoutConstants.MINIMUM, // row 1 
						TableLayoutConstants.MINIMUM, // row 2
						TableLayoutConstants.MINIMUM, // row 3
						TableLayoutConstants.MINIMUM, // row 4
						TableLayoutConstants.MINIMUM, // row 5
						TableLayoutConstants.MINIMUM, // row 6
						TableLayoutConstants.FILL,
						TableLayoutConstants.MINIMUM, // row 8
						SextanteConfigurationDialog.SPACER_SMALL } });
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		this.setLayout(thisLayout);

		jActivateCheckBox = new JCheckBox(Sextante.getText("ActivateProvider") + " SAGA GIS");
		final String sActivate = SextanteGUI.getSettingParameterValue(SextanteSagaSettings.SAGA_ACTIVATE);
		final boolean bActivate = Boolean.parseBoolean(sActivate);
		jActivateCheckBox.setSelected(bActivate);
		this.add(jActivateCheckBox, "1, 1");

		this.add(new JSeparator(SwingConstants.HORIZONTAL), "1, 2, 2, 2");
		/* ----------------------------------------------------------- */		

		jPortableCheckBox = new JCheckBox(Sextante.getText("Portable"));
		final String sActivatePortable = SextanteGUI.getSettingParameterValue(SextanteSagaSettings.SAGA_PORTABLE);
		final boolean bActivatePortable = Boolean.parseBoolean(sActivatePortable);
		jPortableCheckBox.setEnabled(bActivate);
		jPortableCheckBox.setSelected(bActivatePortable);
		this.add(jPortableCheckBox, "1, 3");		

		jLabelSagaFolder = new JLabel();
		jLabelSagaFolder.setEnabled(bActivate);
		if (bActivatePortable == true) {
			jLabelSagaFolder.setEnabled(false);
		}
		this.add(jLabelSagaFolder, "1, 4");
		jLabelSagaFolder.setText(Sextante.getText("Saga_folder"));
		jSagaFolder = new FileSelectionPanel(true, true, (String[]) null, Sextante.getText("selector_choose_folder"));
		jSagaFolder.getTextField().setEnabled(bActivate);
		jSagaFolder.getButton().setEnabled(bActivate);
		if (bActivatePortable == true) {
			jSagaFolder.getTextField().setEnabled(false);
			jSagaFolder.getButton().setEnabled(false);
		}
		final String sFolder = SextanteGUI.getSettingParameterValue(SextanteSagaSettings.SAGA_FOLDER);
		jSagaFolder.setFilepath(sFolder);
		this.add(jSagaFolder, "2, 4");

		this.add(new JSeparator(SwingConstants.HORIZONTAL), "1, 5, 2, 5");
		/* ----------------------------------------------------------- */

		jLabelUpdate = new JLabel();
		jLabelUpdate.setText( Sextante.getText("update_library") );
		jLabelUpdate.setEnabled(bActivate);
		this.add(jLabelUpdate, "1, 6");      
		jButtonUpdate = new JButton(Sextante.getText("load_SAGA_modules"));
		jButtonUpdate.setEnabled(bActivate);
		this.add(jButtonUpdate, "2, 6");

		/* add provider logo and URL */
		final URL res = getClass().getClassLoader().getResource("images/logo_saga.png");
		if (res != null) {
			final ImageIcon logo = new ImageIcon(res);
			JLabel logoLabel = new JLabel(logo);
			logoLabel.setIconTextGap(4);
			logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			logoLabel.setText("<html><i><a href=http://www.saga-gis.org/>http://www.saga-gis.org/</a></i></html>");
			this.add(logoLabel,"1, 8, 1, 8");
		}

		/**********************************/
		/** Action listeners for widgets **/
		/**********************************/		

		jActivateCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				SextanteGUI.setSettingParameterValue(SextanteSagaSettings.SAGA_ACTIVATE,
						new Boolean(jActivateCheckBox.isSelected()).toString());
				SextanteGUI.updateAlgorithmProvider(SagaAlgorithmProvider.class);
				/* toggle remaining widgets on or off */
				boolean active = jActivateCheckBox.isSelected();
				jPortableCheckBox.setEnabled(active);
				jLabelSagaFolder.setEnabled(active);
				jSagaFolder.getTextField().setEnabled(active);
				jSagaFolder.getButton().setEnabled(active);
				jLabelUpdate.setEnabled(active);
				jButtonUpdate.setEnabled(active);				
				jActivateCheckBox.getParent().repaint();
				active = jPortableCheckBox.isSelected();
				if ( active == true ) {
					jLabelSagaFolder.setEnabled(false);
					jSagaFolder.getTextField().setEnabled(false);
					jSagaFolder.getButton().setEnabled(false);
				}				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});		

		jPortableCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {        	 
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				SextanteGUI.setSettingParameterValue(SextanteSagaSettings.SAGA_PORTABLE,
						new Boolean(jPortableCheckBox.isSelected()).toString());             
				//Set portable SAGA bin dir
				SextanteGUI.checkDir ( Sextante.PORTABLE_SAGA_FOLDER, true, "SAGA GIS" );
				String sPath = new String (SextanteGUI.getSextantePath() + File.separator + 
						Sextante.PORTABLE_SAGA_FOLDER);
				SextanteGUI.setSettingParameterValue(SextanteSagaSettings.SAGA_FOLDER, sPath);
				jSagaFolder.setFilepath(sPath);
				//Activate/deactivate the remaining widgets on this page
				final boolean active = jPortableCheckBox.isSelected();
				if ( active == true ) {
					jLabelSagaFolder.setEnabled(false);
					jSagaFolder.getTextField().setEnabled(false);
					jSagaFolder.getButton().setEnabled(false);

				} else {
					jLabelSagaFolder.setEnabled(true);
					jSagaFolder.getTextField().setEnabled(true);
					jSagaFolder.getButton().setEnabled(true);					

				}
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});		

		jButtonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				SextanteGUI.setSettingParameterValue(SextanteSagaSettings.SAGA_FOLDER, jSagaFolder.getFilepath());
				setupSaga();
			}
		});		

	}


	protected void setupSaga() {

		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			SagaUtils.installSaga();			
			SextanteGUI.updateAlgorithmProvider(SagaAlgorithmProvider.class);
			final HashMap<String, GeoAlgorithm> algs = Sextante.getAlgorithms().get("SAGA");
			int iNumAlgs = 0;
			if (algs != null) {
				iNumAlgs = algs.size();
			}
			this.setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(null, Sextante.getText("SagaAlgorithmsLoaded") + " " + iNumAlgs + ".",
					Sextante.getText("SAGA"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch (final SagaExecutionException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Sextante.getText("ErrorInstallingSaga"), Sextante.getText("SAGA"),
					JOptionPane.ERROR_MESSAGE);
		}

	}


	@Override
	public HashMap<String, String> getValues() {

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put(SextanteSagaSettings.SAGA_ACTIVATE, new Boolean(jActivateCheckBox.isSelected()).toString());
		map.put(SextanteSagaSettings.SAGA_PORTABLE, new Boolean(jPortableCheckBox.isSelected()).toString());
		final String path = jSagaFolder.getFilepath();
		if (path != null) {
			map.put(SextanteSagaSettings.SAGA_FOLDER, path);
		}		
		return map;

	}

}
