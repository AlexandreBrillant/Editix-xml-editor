package com.japisoft.framework.xml.imp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class CSVPanel extends JPanel implements ActionListener, CSVImportParams {
	
	JPanel pnlDelimiters = new JPanel();
	TitledBorder titledBorder1;
	JCheckBox cbTab = new JCheckBox();
	JCheckBox cbSemiColon = new JCheckBox();
	JCheckBox cbComma = new JCheckBox();
	JCheckBox cbSpace = new JCheckBox();
	JCheckBox cbOther = new JCheckBox();
	JTextField tfOther = new JTextField();
	JLabel lbTextQualifier = new JLabel();
	JComboBox cbTextQualifier = new JComboBox();
	JLabel lbStartingRow = new JLabel();
	JSpinner spStartingRow = new JSpinner();
	JLabel lbColName = new JLabel();
	JScrollPane spColumnName = new JScrollPane();
	JList lstColumnName = new JList();
	JTextField tfColumnName = new JTextField();
	JButton btnColumnAdd = new JButton();
	JButton btnColumnRemove = new JButton();
	JLabel lblRowName = new JLabel();
	JTextField tfRow = new JTextField();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	
	public CSVPanel() {
		try {
			jbInit();			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setTabSelected( boolean tabSelected ) {
		cbTab.setSelected( tabSelected );
	}

	public boolean isTabSelected() { 
		return cbTab.isSelected();
	}

	public void setSemiColonSelected( boolean semiColonSelected ) {
		cbSemiColon.setSelected( semiColonSelected );
	}
	
	public boolean isSemiColonSelected() {
		return cbSemiColon.isSelected();
	}
	
	public void setCommaSelected( boolean commaSelected ) {
		cbComma.setSelected( commaSelected );
	}
	
	public boolean isCommaSelected() {
		return cbComma.isSelected();
	}
	
	public void setSpaceSelected( boolean spaceSelected ) {
		cbSpace.setSelected( spaceSelected );
	}

	public boolean isSpaceSelected() {
		return cbSpace.isSelected();
	}

	public void setOtherSelected( boolean otherSelected ) {
		cbOther.setSelected( otherSelected );
		tfOther.setEnabled( otherSelected );
	}
	
	public boolean isOtherSelected() {
		return cbOther.isSelected();
	}
	
	public void setOther( String other ) {
		tfOther.setText( other );
	}
	
	public String getOther() {
		return tfOther.getText();
	}
	
	public void setTextQualifier( String text ) {
		cbTextQualifier.setSelectedItem( text );
	}

	public String getTextQualifier() {
		return ( String )cbTextQualifier.getSelectedItem();
	}

	public void setStartingRow( int startingRow ) {
		spStartingRow.setValue( new Integer( startingRow ) );
	}
	
	public int getStartingRow() {
		return ( ( Integer )spStartingRow.getValue() ).intValue();
	}

	public void setColumnName( ListModel model ) {
		lstColumnName.setModel( model );
	}
	
	public ListModel getColumnName() {
		return lstColumnName.getModel();
	}
	
	public void setRowName( String row ) {
		tfRow.setText( row );
	}
	
	public String getRowName() {
		return tfRow.getText();
	}
	
	// --------------------------------------------------------------------------
	
	public void addNotify() {
		super.addNotify();
		btnColumnAdd.addActionListener(this);
		btnColumnRemove.addActionListener(this);
		cbOther.addActionListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		btnColumnAdd.removeActionListener(this);
		btnColumnRemove.removeActionListener(this);
		cbOther.removeActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnColumnAdd) {
			String col = tfColumnName.getText();
			if (col != null && !"".equals(col))
				((DefaultListModel) lstColumnName.getModel()).addElement(col);
		} else if (e.getSource() == btnColumnRemove) {
			if (lstColumnName.getSelectedIndex() != -1) {
				((DefaultListModel) lstColumnName.getModel())
						.remove(lstColumnName.getSelectedIndex());
			}
		} else if (e.getSource() == cbOther) {
			tfOther.setEnabled(cbOther.isSelected());
		}
	}

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(
				new Color(153, 153, 153), 2), "Delimiters");
		this.setLayout(gridBagLayout1);
		pnlDelimiters.setBorder(titledBorder1);
		pnlDelimiters.setLayout(null);
		cbTab.setText("Tab");
		cbTab.setBounds(new Rectangle(14, 22, 67, 22));
		cbSemiColon.setText("SemiColon");
		cbSemiColon.setBounds(new Rectangle(87, 22, 88, 22));
		cbComma.setText("Comma");
		cbComma.setBounds(new Rectangle(196, 22, 88, 22));
		cbSpace.setText("Space");
		cbSpace.setBounds(new Rectangle(14, 58, 71, 22));
		cbOther.setText("Other");
		cbOther.setBounds(new Rectangle(87, 58, 72, 22));
		tfOther.setEnabled(false);
		tfOther.setText("");
		tfOther.setBounds(new Rectangle(196, 60, 32, 19));
		lbTextQualifier.setText("Text Qualifier");
		lbStartingRow.setText("Starting row");
		lbColName.setText("Column name");
		btnColumnAdd.setText("Add");
		btnColumnRemove.setText("Remove");
		tfColumnName.setText("");
		lblRowName.setText("Row name");
		tfRow.setText("row");
		this.add(pnlDelimiters, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						9, 16, 0, 13), 545, 97));
		pnlDelimiters.add(cbTab, null);
		pnlDelimiters.add(cbSemiColon, null);
		pnlDelimiters.add(cbComma, null);
		pnlDelimiters.add(cbSpace, null);
		pnlDelimiters.add(cbOther, null);
		pnlDelimiters.add(tfOther, null);
		this.add(lbTextQualifier, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						16, 16, 0, 0), 0, 0));
		this.add(cbTextQualifier, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(11, 8, 0, 0), 36, 0));
		this.add(lbStartingRow, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						16, 16, 0, 0), 8, 0));
		this.add(spStartingRow, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(13, 8, 0, 44), 0, 0));
		this.add(lbColName, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						21, 16, 0, 0), 0, 0));
		this.add(spColumnName, new GridBagConstraints(2, 3, 1, 4, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						19, 13, 0, 13), 121, -27));
		spColumnName.getViewport().add(lstColumnName, null);
		this.add(btnColumnAdd, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						8, 16, 0, 17), 0, 0));
		this.add(btnColumnRemove, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 16, 0, 0), 0, 0));
		this.add(lblRowName, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						13, 16, 0, 17), 0, 0));
		this.add(tfRow, new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 16, 20, 0), 127, 0));
		this.add(tfColumnName, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(9, 16, 0, 0), 148, 0));

		cbTextQualifier.addItem("None");
		cbTextQualifier.addItem("\"");
		cbTextQualifier.addItem("\'");
		cbTextQualifier.setSelectedIndex(0);

		tfColumnName.setText("col");

		lstColumnName.setModel(new DefaultListModel());
		ButtonGroup bg = new ButtonGroup();
		bg.add(cbComma);
		bg.add(cbOther);
		bg.add(cbSemiColon);
		bg.add(cbSpace);
		bg.add(cbTab);

		spStartingRow.setValue(new Integer(1));
		cbComma.setSelected(true);
	}

}
