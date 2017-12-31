package com.japisoft.editix.action.xml.format;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.format.Formatter;
import com.japisoft.framework.xml.format.FormatterConfig;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;
import com.japisoft.p3.Manager;
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
public class FormatAction extends AbstractAction {
	
	protected FormatterConfig getFormatterConfig() {
		return new FormatterConfig();
	}

	protected AbstractRefactor getRefactor() {
		return null;
	}

	public static AbstractRefactor LAST_REFACTOR = null;

	public void actionPerformed(ActionEvent e) {

		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}

		format( container, getRefactor(), getFormatterConfig(), (String)getValue( "param" ) );
		
	}

	
	public static void format( 
			XMLContainer container, 
			AbstractRefactor ar, 
			FormatterConfig config,
			String silenceParamValue ) {
	
		String content = container.getAccessibility().getText();
		try {

			if ( ar != null ) {
								
				LAST_REFACTOR = ar;				
			}

			if ( config == null )
				config = new FormatterConfig();
			
			String location = null;
			
			FPNode node = 
				container.getCurrentNode();
			
			if ( node != null )
				location = node.getXPathLocation();
			
			content = Formatter.format(
					content, 
					null, 
					config, 
					ar );

			container.getAccessibility().setText( content );
			container.setModifiedState( true );
	
			// Restore the current location
			
			if ( location != null ) {
				
				JobManager.addJob(
						new RestoreLocationJob( container, location ) );

			}
			
		} catch (Throwable e1) {
			ApplicationModel.debug( e1 );
			if ( !"".equals( e1.getMessage() ) && !"silence".equals( silenceParamValue ) )	// Silent exception
				EditixFactory.buildAndShowErrorDialog( "Can't format : " + e1.getMessage() );
		}
	
	}
	
	static class RestoreLocationJob extends BasicJob {
		
		private XMLContainer container;
		private String location;
		
		RestoreLocationJob( XMLContainer container, String location ) {
			this.container = container;
			this.location = location;
		}
		
		public void dispose() {
			container = null;
		}

		public Object getSource() {
			return null;
		}

		public boolean isAlone() {
			return false;
		}

		public void stopIt() {
		}

		public void run() {
			try {
				
				FPNode rootNode = ( FPNode )container.getTree().getModel().getRoot();
				if ( rootNode != null ) {
					FPNode node = rootNode.getNodeForXPathLocation( location, true );
					if ( node != null ) {
						try {
							container.getEditor().setCaretPosition( node.getStartingOffset() + 1 );
						} catch( IllegalArgumentException exc ) {
						}
					}
				}

			} finally {
				dispose();
			}
		}
		
	}
	
}
