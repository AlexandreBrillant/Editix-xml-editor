package com.japisoft.editix.editor.xquery;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import com.japisoft.editix.editor.xquery.helper.CommentHandler;
import com.japisoft.editix.editor.xquery.helper.KeywordsHandler;
import com.japisoft.editix.editor.xquery.kit.XQueryEditorKit;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.EditorContext;
import com.japisoft.xmlpad.editor.XMLEditor;

//.xq, .xql, and .xquery
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
public class XQueryEditor extends XMLContainer {

	public XQueryEditor() {
		super( new ComponentFactoryForCssEditor() );
		getUIAccessibility().setToolBarAvailable( false );
		setStatusBarAvailable( false );
		getUIAccessibility().setPopupAvailable( false );
		getUIAccessibility().setTreePopupAvailable( false );
		setAutoNewDocument( false );
		setErrorPanelAvailable( true );
		getDocumentInfo().setRealTimeTree( false );
		setDisposeAction(false);
		setTreeAvailable( false );
		
		getDocument().putProperty(PlainDocument.tabSizeAttribute,
				new Integer(Preferences.getPreference("file", "tab-size", 2)));

		ArrayList assistant = new ArrayList();
		assistant.add( new CommentHandler() );
		assistant.add( new KeywordsHandler() );
		
		getHelperManager().resetHandlers( assistant, false );
	}

	static class ComponentFactoryForCssEditor extends ComponentFactory {
		public XMLEditor getNewXMLEditor(EditorContext context) {
			return new CustomXMLEditorForCss( context );
		}
	}

	static class CustomXMLEditorForCss extends XMLEditor {
		CustomXMLEditorForCss(EditorContext context) {
			super(context);
		}
		public EditorKit getEditorKit() {
			return new XQueryEditorKit();
		}
	}

	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new XQueryEditor().getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}

}
