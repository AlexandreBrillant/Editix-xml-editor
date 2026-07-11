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

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.preferences.Preferences;

import java.awt.*;
import java.util.*;

/**
 * View for wrapping the content into several line
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.2
 */
final class WrappedXMLView extends WrappedPlainView implements XMLViewable {

	private XMLTextView view;
	
	public WrappedXMLView( Element element, boolean displaySpace ) {
		super( element );
		view = new XMLTextView( element, displaySpace );
	}

	@Override
	public void setSyntaxColor(boolean enabled) {	
		view.setSyntaxColor( enabled );
	}
	@Override
	public void setDTDMode(boolean enabled) {
		view.setDTDMode( enabled );
	}
	
	@Override
	public void setDisplaySpace(boolean space) {
		view.setDisplaySpace( space );
	}

	public void setViewPainterListener( ViewPainterListener listener ) {
		view.setViewPainterListener( listener );
	}	

	private int previousLine = -1;
	
    private void drawLineImpl(int p0, int p1, Graphics g, float x, float y,
            boolean useFPAPI) {
    	try {
			Element lineMap = getElement();
			int index = lineMap.getElementIndex(p0);
			view.drawLine( this, getDocument(), (XMLEditor)getContainer(), getDefaultColor(), index, p0, p1, (Graphics2D)g, x, y, previousLine != index );
			previousLine = index;
    	} catch( Throwable th ) {
    		th.printStackTrace();
    	}
    }

    protected void drawLine(int p0, int p1, Graphics2D g, float x, float y) {
    	drawLineImpl(p0, p1, g, x, y, true);
    }
	
	protected Color getDefaultColor() {
		return getContainer().getForeground();
	}
		
	
	@Override
	protected short getRightInset() {
		return 2;
	}
	
}