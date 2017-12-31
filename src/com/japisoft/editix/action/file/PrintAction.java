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
