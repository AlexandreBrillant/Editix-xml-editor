package com.japisoft.xflows.task.parsing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xflows.task.AbstractTaskUI;
import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.ui.XFlowsFactory;

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
public class ParsingUI extends AbstractTaskUI {

	public ParsingUI() {
		super( "parsing" );

		try {
			jbInit();

			tfSourcePath.setName( SOURCEPATH );
			cbSourceFilter.setName( SOURCEFILTER );
			cbValidating.setName( VALIDATING );

			tfSourcePath.setDirectoryMode( true );
			tfSourcePath.setPreferenceBinding( "defaultpath", "source" );
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	public static final String SOURCEPATH = FilesTaskRunner.SOURCEPATH;
	public static final String SOURCEFILTER = FilesTaskRunner.SOURCEFILTER;
	public static final String VALIDATING = "validating";

	TitledBorder titledBorder1;
	JLabel lblSource = new JLabel();
	FileTextField tfSourcePath = new FileTextField(null, "xml");
	JLabel lblSourceFilter = new JLabel();
	JComboBox cbSourceFilter = XFlowsFactory.getSourceFilter("xml");
	JLabel lblParams = new JLabel();
	JLabel lblTarget = new JLabel();
	JLabel lblRootTag = new JLabel( "Validating" );
	FileTextField tfTargetPath = new FileTextField(null, (String)null);
	JCheckBox cbValidating = new JCheckBox();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(
				new Color(153, 153, 153), 2), "Parsing XML");
		lblSource.setText("XML Source path");
		this.setBorder(titledBorder1);
		this.setLayout(gridBagLayout1);
		tfSourcePath.setText("");
		lblSourceFilter.setText("XML Source filter");
		cbSourceFilter.setEditable(true);
		lblParams.setText("Parameters");
		lblTarget.setToolTipText("");
		lblTarget.setText("XML Target file");
		tfTargetPath.setText("");
		
		this.add(lblSource, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						17, 0, 0), 0, 0));
		this.add(tfSourcePath, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 9, 0, 14), 434, 4));
		this.add(lblSourceFilter, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						20, 17, 0, 0), 0, 0));
		this.add(cbSourceFilter, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(16, 9, 0, 14), 406, -1));
		
		this.add(lblRootTag, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						18, 17, 0, 31), 0, 0));
		this.add(cbValidating, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(14, 9, 0, 14), 434, 4));		
		
	}	
	
}
