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

package com.japisoft.editix.action.file;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JEditorPane;

import com.japisoft.editix.ui.EditixEditorFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class PrintAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		if ( panel == null )
			return;
		
		/*
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		DocumentRenderer dr = new DocumentRenderer();
		dr.print( container.getEditor() );
		*/
		
		Object toPrint = panel.print();
		
		if ( toPrint instanceof XMLContainer ) {
			toPrint = ( ( XMLContainer )toPrint ).getEditor();
		}

		if ( toPrint instanceof JEditorPane ) {
			DocumentRenderer dr = new DocumentRenderer();
			dr.print( ( JEditorPane )toPrint );
		} else
			if ( toPrint instanceof JComponent ) {
				PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setPrintable( new ComponentPrintable( ( JComponent )toPrint ), pj.defaultPage() );
				try {
					pj.print();
				} catch( PrinterException pe ) {
					EditixFactory.buildAndShowErrorDialog( pe.getMessage() );
				}
			}
	}

	private class ComponentPrintable implements Printable {
		private JComponent component;
		ComponentPrintable( JComponent component ) {
			this.component = component;
		}
		
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {			
			if (pageIndex > 0)
			      return NO_SUCH_PAGE;
			Graphics2D g2 = (Graphics2D) graphics;
			g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			component.setDoubleBuffered( false );
			component.paint( graphics );
			component.setDoubleBuffered( true );
			return 0;
		}
		
	}
	
}
