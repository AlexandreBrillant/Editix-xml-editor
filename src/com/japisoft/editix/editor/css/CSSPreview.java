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

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.browser.Browser;
import com.japisoft.framework.ui.browser.BrowserFactory;

public class CSSPreview extends JPanel implements DocumentListener, ActionListener {

	private boolean autoRefresh = false;
	private CSSEditor editor = null;
	private JCheckBox cb = null;
	private Browser browser = null;
	
	CSSPreview( CSSEditor editor ) {
		this.editor = editor;
		editor.getDocument().addDocumentListener( this );
		autoRefresh = Preferences.getPreference( "CSSEditor", "auto-refresh", false );
		setLayout( new BorderLayout() );

		editor.getMainContainer().getEditor().getActionMap().put( "refresh", new RefreshAction() );
		editor.getMainContainer().getEditor().getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ), "refresh" );
		
		JToolBar tb = new JToolBar();
		tb.add( editor.getMainContainer().getEditor().getActionMap().get( "refresh" ) );

		cb = new JCheckBox( "Auto refresh" );
		cb.setSelected( autoRefresh );
		tb.add( cb );

		add( tb, BorderLayout.SOUTH );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		cb.addActionListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		cb.removeActionListener( this );
	}

	public void actionPerformed(ActionEvent e) {
		autoRefresh = cb.isSelected();
	}

	public void dispose( CSSEditor editor ) {
		editor.getDocument().removeDocumentListener( this );
		editor = null;
	}

	public void changedUpdate(DocumentEvent e) {
		if ( autoRefresh ) {
			refresh();
		}
	}

	public void insertUpdate(DocumentEvent e) {
		if ( autoRefresh ) {
			refresh();
		}		
	}

	public void removeUpdate(DocumentEvent e) {
		if ( autoRefresh ) {
			refresh();
		}		
	}

	void refresh() {
		if ( browser == null ) {
			browser = BrowserFactory.getInstance().newBrowser();
			add( browser.getView(), BorderLayout.CENTER );
			invalidate();
			validate();
			repaint();
		}
		
		browser.setHTML( getHTMLPage(), null );
	}
	
	private String getHTMLPage() {
		String cssContent = editor.getText();
		
		// Remove comments
		cssContent = cssContent.replaceAll( "\\/\\*.*\\*\\/", "" );

		String pattern = "^([^\\{\\n\\r]+)\\s*\\{";
		Pattern p = Pattern.compile( pattern, Pattern.MULTILINE );
		Matcher m = p.matcher( cssContent );

		ArrayList<String> ruleName = new ArrayList<String>();

		while ( m.find() ) {
			String cssRule = m.group( 1 );
			ruleName.add( cssRule );
		}

		int id = 1;
		
		for ( String rule : ruleName ) {
			cssContent = cssContent.replace( rule, "#css" + id + " " );
			id++;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append( "<!DOCTYPE html>");
		sb.append( "<html><head><title></title>" );
		sb.append( "<style type='text/css'>" );
		sb.append( cssContent );
		sb.append( "</style>" );
		sb.append( "</head><body>" );

		id = 1;
		for ( String rule : ruleName ) {
			sb.append( "<div id='css" + id + "' style='clear:both'>" );
			sb.append( rule );
			sb.append( "</div>" );
			id++;
		}
		
		sb.append( "</body></html>" );
		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	
	class RefreshAction extends AbstractAction {
	
		public RefreshAction() {
			putValue( 
				Action.SHORT_DESCRIPTION, 
				"Refresh the CSS Preview" 
			);
			putValue( 
				Action.SMALL_ICON, 
				new ImageIcon( 
					getClass().getResource( "refresh.png" )
				) 
			);
		}

		public void actionPerformed(ActionEvent e) {
			refresh();
		}
		
	}
	
}
