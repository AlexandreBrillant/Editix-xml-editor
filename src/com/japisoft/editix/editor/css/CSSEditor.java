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

package com.japisoft.editix.editor.css;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import com.japisoft.editix.editor.css.helper.CommentHandler;
import com.japisoft.editix.editor.css.helper.PropertiesHandler;
import com.japisoft.editix.editor.css.helper.SelectorHandler;
import com.japisoft.editix.editor.css.helper.ValuesHandler;
import com.japisoft.editix.editor.css.kit.CssEditorKit;

import com.japisoft.editix.ui.EditixErrorPanel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.EditorContext;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.tree.parser.Parser;

public class CSSEditor extends XMLContainer {

	static boolean STANDALONE = false;
	
	private Action parseAction = null;
	
	public CSSEditor( Action parseAction ) {
		super( new ComponentFactoryForCSSEditor() );
		this.parseAction = parseAction;
		getUIAccessibility().setToolBarAvailable( false );
		setStatusBarAvailable( false );
		getUIAccessibility().setPopupAvailable( false );
		getUIAccessibility().setTreePopupAvailable( false );
		setAutoNewDocument( false );
		setErrorPanelAvailable( true );
		getDocumentInfo().setRealTimeTree( true );		
		setEnabledTreeLocationForCaret( true );
		setTreeDragDrop( false );
		
		setDisposeAction(false);
		setTreeAvailable( true );
		setTreePopupAvailable( false );
		setTreeElementViewAvailable( false );

		getDocument().putProperty(PlainDocument.tabSizeAttribute,
				new Integer(Preferences.getPreference("file", "tab-size", 2)));

		ArrayList assistant = new ArrayList();
		assistant.add( new CommentHandler() );
		assistant.add( new PropertiesHandler() );
		assistant.add( new ValuesHandler() );
		assistant.add( new SelectorHandler() );
		
		getHelperManager().resetHandlers( assistant, false );
		
		getUIAccessibility().setErrorView(new EditixErrorPanel());
	}

	public Action getAction(String actionId) {
		if ( "parse".equals( actionId ) ) {
			return parseAction;
		} else
			return super.getAction( actionId );
	}
		
	@Override
	public Parser createNewParser( boolean lightweightMode ) {
		return new CSSParser();
	}	
	
	static class CustomXMLEditorForCSS extends XMLEditor {
		CustomXMLEditorForCSS( EditorContext context ) {
			super( context );
		}
		public EditorKit getEditorKit() {
			return new CssEditorKit( "CSSEditor" );
		}
	}

	static class ComponentFactoryForCSSEditor extends ComponentFactory {
		public XMLEditor getNewXMLEditor( EditorContext context ) {
			return new CustomXMLEditorForCSS( context );
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
	
	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new CSSEditor( null ).getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}

}
