package com.japisoft.editix.action.xquery;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.xml.transform.SourceLocator;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;

import com.japisoft.editix.editor.xquery.XQueryEditor;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.CheckableAction;

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
public class CheckAction extends AbstractAction implements CheckableAction {

	public boolean checkDocument(XMLContainer editor, boolean silentMode ) {
		editor.getErrorManager().initErrorProcessing();			

		Configuration config = new Configuration();
		StaticQueryContext staticContext = 
		        new StaticQueryContext( config );

		// Apply parameters
		for (int i = 0; i < 100; i++) {
			String param = (String) editor
					.getProperty( "xquery.param.name." + i);
			String value = (String) editor
					.getProperty( "xquery.param.value." + i);
			if (param != null && value != null && !"".equals(param)
					&& !"".equals(value)) {

				staticContext.declareNamespace(
						param, 
						value 
				);
			}
		}		
		
		try {
			
			XQueryExpression exp = 
			        staticContext.compileQuery( editor.getText() );
			
			if ( !editor.getErrorManager().hasLastError() ) {
				if ( !silentMode )
					EditixFactory.buildAndShowInformationDialog( "Your XQuery document is correct" );
				editor.getErrorManager().notifyNoError(false);
			}

		} catch( XPathException evt ) {
			
			SourceLocator locator = evt.getLocator();
			editor.getErrorManager().notifyError( 
					null, true, null, locator.getLineNumber(), locator.getColumnNumber(), -1, evt.getMessage(), false );
						
		}

		editor.getErrorManager().stopErrorProcessing();

		return !editor.getErrorManager().hasLastError();
	}

	public void actionPerformed(ActionEvent e) {

		//£££

		XQueryEditor editor = ( XQueryEditor )EditixFrame.THIS.getSelectedContainer();
		if ( editor == null ) {
			EditixFactory.buildAndShowErrorDialog( "Can't Check your XQuery document" );
			return;
		}

		checkDocument( editor, false );
		//££
		
	}

}
