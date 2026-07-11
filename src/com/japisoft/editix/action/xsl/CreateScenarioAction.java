// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.xflows.XFlowsEditor;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

/*
 * <scenario>
	<task name="XSLT_1" type="XSLT">
		<params>
			<param key="sourcefilter" value="(.*)\.(.*)" type="0"/>
			<param key="stylesheet" value="ddddd.xsl" type="0"/>
			<param key="targetname" value="$1.html" type="0"/>
			<param key="targetpath" value="ddd" type="0"/>
			<param key="sourcepath" value="ddd" type="0"/>
			<param key="version" value="2.0" type="0"/>
		</params>
	</task>
</scenario>
 */
public class CreateScenarioAction extends AbstractAction {

	@Override
	public void actionPerformed( ActionEvent ae ) {
		// XSLT source, file,
		/*
		DATAFILE_PROPERTY = XSLT + ".data.file";
		XSLTFILE_PROPERTY = XSLT + ".xslt.file";
		XSLTRESULT_PROPERTY = XSLT + ".result.file"; 
		*/
		
		String key = "xslt";
		String stylesheet = "stylesheet";
		
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if ( "XQR".equals( panel.getMainContainer().getDocumentInfo().getType() ) ) {
			key = "xquery";
			stylesheet = "xquery";
		}
		
		
		String source = ( String )panel.getProperty( key + ".data.file" );
		String xslt = ( String )panel.getProperty( key + ".xslt.file" );
		String result = ( String )panel.getProperty( key + ".result.file" );
		
		if ( source == null || xslt == null || result == null ) {
			EditixFactory.buildAndShowErrorDialog( "Invalid parameters for building the scenario" );
			return;
		}
		
		String sourceExt = FileToolkit.fileExt( source );
		String resultExt = FileToolkit.fileExt( result );
		
		StringBuffer sb = new StringBuffer( "<?xml version='1.0'?>\n" );
		sb.append( "<scenario>\n" );
		sb.append( "\t<task name=\"myTask\" type=\"" + key.toUpperCase() + "\">\n" );
		sb.append( "\t\t<params>\n" );
		sb.append( "\t\t\t<param key=\"sourcefilter\" value=\"(.*)\\." ).append( sourceExt ).append( "\" type=\"0\"/>\n" );		
		sb.append( "\t\t\t<param key=\"" + stylesheet + "\" value=\"" ).append( xslt ).append( "\" type=\"0\"/>\n" );
		sb.append( "\t\t\t<param key=\"targetname\" value=\"$1." ).append( resultExt ).append( "\" type=\"0\"/>\n" );
		sb.append( "\t\t\t<param key=\"targetpath\" value=\"" ).append( FileToolkit.parentPath( result ) ).append( "\" type=\"0\"/>\n" );
		sb.append( "\t\t\t<param key=\"sourcepath\" value=\"" ).append( FileToolkit.parentPath( source ) ).append( "\" type=\"0\"/>\n" );
		sb.append( "\t\t</params>\n" );
		sb.append( "\t\t</task>\n" );
		sb.append( "</scenario>" );

		XMLDocumentInfo doc = 
				DocumentModel.getDocumentForType( "XSC" );
		IXMLPanel editor = 
			EditixFactory.getPanelForDocument( doc );
		
		editor.getMainContainer().setText( sb.toString() );
		
		XMLContainer container = 
			panel.getMainContainer();
		XMLDocumentInfo newDoc = 
			doc.cloneDocument();
		editor.setDocumentInfo( newDoc );
		
		EditixFrame.THIS.addContainer( editor );
		EditixFrame.THIS.updateCurrentXMLContainer( editor );

	}

}
