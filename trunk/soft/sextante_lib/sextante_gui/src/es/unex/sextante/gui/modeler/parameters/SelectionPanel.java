package es.unex.sextante.gui.modeler.parameters;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import es.unex.sextante.additionalInfo.AdditionalInfoSelection;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.modeler.ModelerPanel;
import es.unex.sextante.gui.modeler.SelectionAndChoices;
import es.unex.sextante.parameters.Parameter;
import es.unex.sextante.parameters.ParameterSelection;

public class SelectionPanel
extends
ParameterPanel {

	SelectionTreePanel tree;


	public SelectionPanel(final JDialog parent,
			final ModelerPanel panel) {

		super(parent, panel);

	}


	public SelectionPanel(final ModelerPanel panel) {

		super(panel);

	}


	@Override
	public String getParameterDescription() {

		return Sextante.getText("option_setting");

	}


	@Override
	protected void initGUI() {

		super.initGUI();

		super.setTitle(Sextante.getText("modeler_add_par_selection"));

		super.setPreferredSize(new java.awt.Dimension(400, 540));      

		super.setResizable(true);
		
		final TableLayout thisLayout = new TableLayout(new double[][] {
				{ 	TableLayoutConstants.FILL },	
				{ TableLayoutConstants.FILL} });
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		jPanelMiddle.setLayout(thisLayout);		
		
		try {
			tree = new SelectionTreePanel();
			jPanelMiddle.add(tree, "0, 0");
		}
		catch (final Exception e) {
			Sextante.addErrorToLog(e);
		}

	}


	@Override
	protected boolean prepareParameter() {


		final String sDescription = jTextFieldDescription.getText();

		if (sDescription.length() != 0) {
			m_Parameter = new ParameterSelection();
			final SelectionAndChoices sac = tree.getSelectedList();
			if (sac != null) {
				final AdditionalInfoSelection ai = new AdditionalInfoSelection(sac.getChoices());
				m_Parameter.setParameterAdditionalInfo(ai);
				if (sDescription.trim().equals("")) {
					m_Parameter.setParameterDescription(sac.getDescription());
				}
				else {
					m_Parameter.setParameterDescription(sDescription);
				}
				
		        m_Parameter.setColorR(m_Color.getRed());        
		        m_Parameter.setColorG(m_Color.getGreen());        
		        m_Parameter.setColorB(m_Color.getBlue());        
		        m_Parameter.setColorAlpha(m_Color.getAlpha());				
				
				return true;
			}
			else {
				return false;
			}
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

		//TODO:

	}


	@Override
	public boolean parameterCanBeAdded() {

		return true;

	}

}
