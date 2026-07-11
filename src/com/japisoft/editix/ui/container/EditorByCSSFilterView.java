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

package com.japisoft.editix.ui.container;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.Factory;
import com.japisoft.stylededitor.LocationListener;
import com.japisoft.stylededitor.model.XMLDocument;
import com.japisoft.xmlpad.helper.HelperManager;

public class EditorByCSSFilterView implements
		Factory,
		FilterView, 
		ActionListener,
		LocationListener {

	private JPanel view = null; 
	private JCheckBox cbTagMode = null;
	
	public void init(
		HelperManager helper,
		String location, 
		Document xmlContent ) throws Exception {
		getEditor().setDocument( 
			new XMLDocument( location, xmlContent )
		);
		if ( cbTagMode != null ) {
			cbTagMode.addActionListener( this );
		}
	}

	public void dispose() {
		if ( cbTagMode != null ) {
			cbTagMode.removeActionListener( this );
		}
		getEditor().setLocationListener( null );
	}

	public void requestFocus() {
		getEditor().requestFocus();
	}
	
	public void setLocation( Node n ) {
		StringBuffer sb = new StringBuffer();
		while ( n != null ) {
			if ( n instanceof Element ) {
				sb.insert( 
					0, 
					"/" + n.getNodeName() 
				);
			}
			n = n.getParentNode();
		}
		String location = sb.toString();
		ApplicationModel.fireApplicationValue( "location", location );
	}

	public void copy() {
		editor.copy();
	}
	
	public void cut() {
		editor.cut();
	}

	public void paste() {
		editor.paste();
	}
	
	public String getName() {
		return "CSS";
	}

	private EditorByCSS editor = null;
	
	public JComponent getView() {
		if ( view == null ) {
			view = new JPanel();
			view.setLayout( 
				new BorderLayout() 
			);
			view.add( 
				BorderLayout.CENTER, 
				new JScrollPane( 
					getEditor() ) 
			);
			
			JToolBar tb = new JToolBar();
			tb.setFloatable( false );

			cbTagMode = new JCheckBox( "Display tag" );
			cbTagMode.addActionListener( this );
			tb.add( cbTagMode );

			view.add( 
				BorderLayout.SOUTH, 
				tb 
			);
		}
		return view;
	}

	public void actionPerformed(ActionEvent e) {
		getEditor().setDisplayTag( 
			cbTagMode.isSelected() 
		);
		getEditor().requestFocus();
	}

	private EditorByCSS getEditor() {
		if ( editor == null ) {
			editor = new EditorByCSS();
			editor.setDisplayTag( false );
			editor.setLocationListener( this );
			editor.setFactory( this );
		}
		return editor;
	}

	public boolean isModified() {
		getEditor().prepareDOMDocument();
		return true;
	}

	public void restoreState(String serialize) {
	}

	public String serializeState() {
		return null;
	}
	
	// Factory for the Visual Component
	
	public boolean confirm( String message ) {
		return EditixFactory.buildAndShowConfirmDialog( message ); 
	}

	private String lastNodeNameQuery = null;
	
	public String getNodeName( String defaultValue ) {
		if ( defaultValue == null )
			defaultValue = lastNodeNameQuery; 
		return lastNodeNameQuery = EditixFactory.buildAndShowInputDialog( "Choose a node name ?", defaultValue );
	}

}
