package com.japisoft.xmlpad.editor;

import javax.swing.text.*;
import com.japisoft.xmlpad.SharedProperties;

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

	public boolean wrappedMode = false;

	/** Wrapped the line automatically if <code>true</code> */
	public void setWrappedMode( boolean wrappedMode ) {
		this.wrappedMode = wrappedMode;
	}

	XMLViewable lastView;
	
	private boolean displaySpace = false;
	
	public void setDisplaySpace( boolean displaySpace ) {
		this.displaySpace = displaySpace;
		if ( lastView != null && lastView instanceof XMLTextView ) {
			( ( XMLTextView )lastView ).setDisplaySpace( displaySpace );
		}
	}

	public boolean isDisplaySpace() {
		return displaySpace;
	}

	/**
	 * @param elem element to draw. Call only once ! */
	public View create(Element elem) {		
		if ( syntaxColor ) {			
			if ( SharedProperties.WRAPPED_LINE ) {
				lastView = new WrappedXMLView( elem ); 
			} else {
				if ( SharedProperties.FULL_TEXT_VIEW ) {
					lastView = new XMLTextView( elem, displaySpace );
					( ( XMLTextView )lastView ).setViewPainterListener( listener );
				} else {
					lastView = new XMLView( elem );
					( ( XMLView )lastView ).setViewPainterListener( listener );
				}
			}

			return ( View )lastView;
		}
		else
			return ( View )( lastView = new XMLView( elem ) );
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
