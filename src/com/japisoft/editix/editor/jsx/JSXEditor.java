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

package com.japisoft.editix.editor.jsx;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import com.japisoft.editix.editor.js.helper.CommentHandler;
import com.japisoft.editix.editor.js.helper.ObjectInScopeHandler;
import com.japisoft.editix.editor.js.helper.ParametersHandler;
import com.japisoft.editix.editor.js.kit.JSEditorKit;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.EditorContext;
import com.japisoft.xmlpad.editor.XMLEditor;

public class JSXEditor extends XMLContainer {

	public JSXEditor() {
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
		assistant.add( new ObjectInScopeHandler() );
		assistant.add( new ParametersHandler() );
		assistant.add( new DOMHandler() );
		getHelperManager().resetHandlers( assistant, false );
		
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
			return new JSEditorKit( "JSEditor" );
		}
	}
	
	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new JSXEditor().getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}

}

