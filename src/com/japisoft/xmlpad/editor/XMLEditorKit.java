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

package com.japisoft.xmlpad.editor;

import javax.swing.text.*;

import com.japisoft.framework.preferences.Preferences;

/**
 * Here a swing EditorKit for XML
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.2 */
public class XMLEditorKit extends DefaultEditorKit implements ViewFactory {
	private ViewPainterListener listener;

	public XMLEditorKit(ViewPainterListener listener ) {
		this.listener = listener;
	}

	/**
	 * View factory for text element */
	public ViewFactory getViewFactory() {
		return this;
	}

	private boolean syntaxColor = true;

	/** Reset it to support XML syntax color. By default <code>true</code> */
	public void setSyntaxColor( boolean syntaxColor ) {
		this.syntaxColor = syntaxColor;
		if ( !syntaxColor && lastView != null )
			lastView.setSyntaxColor( false );
	}
	
	private boolean dtdMode = false;
	
	public void setDTDMode( boolean dtdMode ) {
		this.dtdMode = dtdMode;
		if ( dtdMode && lastView != null )
			lastView.setDTDMode( true );
	}

	XMLViewable lastView;
	
	private boolean displaySpace = false;
	
	public void setDisplaySpace( boolean displaySpace ) {
		this.displaySpace = displaySpace;
		lastView.setDisplaySpace( displaySpace );
	}

	public boolean isDisplaySpace() {
		return displaySpace;
	}
	
	/**
	 * @param elem element to draw. Call only once ! */
	public View create(Element elem) {
		boolean wrappedMode = Preferences.getPreference( "editor", "wrappedMode", false );		
		if ( wrappedMode )
			lastView = new WrappedXMLView( elem, displaySpace );
		else
			lastView = new XMLTextView( elem, displaySpace );
		lastView.setSyntaxColor( syntaxColor );
		return (View)lastView;
	}

	/**
	 * @return the default document */
	public Document createDefaultDocument() {
		return new XMLPadDocument( null );
	}

	public String getContentType() {
		return "text/plain";
	}

}
