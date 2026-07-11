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

package com.japisoft.editix.xslt.debug;

import javax.swing.SwingUtilities;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.bookmark.DefaultBookmarkContext;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
abstract class CommonTraceListener implements TracableListener {

	boolean waitMode = false;
	boolean noWait = false;
	boolean continueMode = false;
	IXMLPanel panel;
	DefaultBookmarkContext bmContext;

	public CommonTraceListener(IXMLPanel panel) {
		this.panel = panel;
		bmContext = 
			(DefaultBookmarkContext) panel.getBookmarkContext();
	}

	protected void showCurrentLine(String uri,int cl) {
		if ( cl <= 0 )
			return;
		final int currentLine = cl;
		final String furi = uri;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setProperty( 
						XSLTEditor.DEBUG_CURRENT_LINE, 
						new Object[] { furi, currentLine } 
				);
			}
		});
	}

	protected void unhighLight(boolean background) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if ( panel != null ) {
					panel.setProperty( 
						XSLTEditor.DEBUG_CURRENT_LINE, 
						new Object[] { null, -1 } 
					);
				}
			}
		});		
	}

	protected void refreshResult() {
		if (panel instanceof XSLTEditor) {
			( ( XSLTEditor ) panel ).loadResultFile();
		}
	}

	protected void waitForDebug() {
		try {

			waitMode = true;
			
			while ( waitMode ) {
				Thread.sleep( 50 );
			}
			
		} catch (InterruptedException exc) {
		}
	}

	public synchronized void continueBreakpoint() {
		continueMode = false;
		waitMode = false;
		notify();
	}

	public synchronized void continueNextElement() {
		continueMode = true;
		waitMode = false;
		notify();
	}

	public synchronized void terminateAll() {
		unhighLight( false );
		waitMode = false;
		noWait = true;
		notify();
	}

	public void dispose() {
		refreshResult();
		unhighLight( false );
		panel = null;
		bmContext = null;
	}

}