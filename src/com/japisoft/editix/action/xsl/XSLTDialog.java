// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.action.xsl;

import javax.swing.table.TableModel;
import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.xmlpad.IXMLPanel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class XSLTDialog extends EditixDialog {

	public XSLTDialog() {
		this("XSLT", 
				"XSLT parameters", 
					"Choose your XSLT parameters\nFix it once and use ctrl-J for applying it.\nStore these parameters using a project for the next time\n", false);
	}

	public XSLTDialog( 
			String title, 
			String subTitle, 
			String comment, 
			boolean xqueryMode ) {
		super(title, 
				subTitle, 
					comment );
		jbInit( xqueryMode );
		pack();
		setSize( getWidth(), getHeight() - 70 );
	}

	private XSLTConfigPanel panel = null;
	
	private void jbInit( boolean xqueryMode ) {
				
		panel = new XSLTConfigPanel( xqueryMode );
		getContentPane().add( panel );
		if ( xqueryMode ) {
			panel.setEnabledFOP( false );
		}

	}

	public String getXSLTFile() { 
		return panel.getXSLTFile();
	}

	public String getDataFile() {
		return panel.getDataFile();
	}

	public String getResultFile() {
		return panel.getResultFile();
	}

	public boolean isOpenNewDocument() {
		return panel.isOpenNewDocument();
	}

	public TableModel getParameters() {
		return panel.getParameters();
	}

	public void init( IXMLPanel container ) {
		panel.init( container );
	}

	public void store( IXMLPanel container ) {
		panel.store( container );
	}

}

