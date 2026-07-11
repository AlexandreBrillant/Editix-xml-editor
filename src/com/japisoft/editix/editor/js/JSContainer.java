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

package com.japisoft.editix.editor.js;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import com.japisoft.editix.editor.js.helper.CommentHandler;
import com.japisoft.editix.editor.js.helper.ObjectInScopeHandler;
import com.japisoft.editix.editor.js.helper.ParametersHandler;
import com.japisoft.editix.editor.js.kit.JSEditorKit;
import com.japisoft.editix.ui.EditixErrorPanel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.EditorContext;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.tree.parser.Parser;

public class JSContainer extends XMLContainer {

	static boolean STANDALONE = false;
	
	private Action parseAction = null;
	
	public JSContainer( Action parseAction ) {
		super( new ComponentFactoryForJSEditor() );
		this.parseAction = parseAction;
		getUIAccessibility().setToolBarAvailable( false );
		setStatusBarAvailable( false );
		getUIAccessibility().setPopupAvailable( false );
		getUIAccessibility().setTreePopupAvailable( false );
		setAutoNewDocument( false );
		setErrorPanelAvailable( true );
		getDocumentInfo().setRealTimeTree( true );		

		setEnabledTreeLocationForCaret( true );
		
		setDisposeAction(false);
		setTreeAvailable( true );
		setTreePopupAvailable( false );
		setTreeElementViewAvailable( false );

		getDocument().putProperty(PlainDocument.tabSizeAttribute,
				new Integer(Preferences.getPreference("file", "tab-size", 2)));

		ArrayList assistant = new ArrayList();
		assistant.add( new CommentHandler() );
		assistant.add( new ObjectInScopeHandler() );
		assistant.add( new ParametersHandler() );
		getHelperManager().resetHandlers( assistant, false );

		getUIAccessibility().setErrorView(new EditixErrorPanel());
		setDisposeAction(false);		
	}

	@Override
	public Parser createNewParser( boolean lightweightMode ) {
		return new JSParser();
	}	
	
	public String getPreferenceGroupe() {
		return "JSEditor";
	}

	public Action getAction(String actionId) {
		if ( "parse".equals( actionId ) ) {
			return parseAction;
		} else
			return super.getAction( actionId );
	}

	static class ComponentFactoryForJSEditor extends ComponentFactory {
		public XMLEditor getNewXMLEditor(EditorContext context) {
			return new CustomXMLEditorForJS( context );
		}
		@Override
		public ElementView getNewElementView(XMLContainer container) {
			return null;
		}
		@Override
		public JToolBar getNewTreeToolBar() {
			return null;
		}		
	}

	static class CustomXMLEditorForJS extends XMLEditor {
		CustomXMLEditorForJS( EditorContext context ) {
			super( context );
		}
		public EditorKit getEditorKit() {
			return new JSEditorKit( "JSEditor" );
		}
	}
	
	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new JSContainer( null ).getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}
	
}
