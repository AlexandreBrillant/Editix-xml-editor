package com.japisoft.editix.action.docbook;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.xml.transform.ErrorListener;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.fop.FOPAction;
import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.job.HeavyJob;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
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
public class DocBookAction extends AbstractAction implements HeavyJob {

	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}

		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
			com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION,
			container,
			container.getEditor() );

		if ( !ok )
			return;

		container.setProperty( "xslt.data.file", container.getCurrentDocumentLocation() );
		
		DocBookDialog dialog = new DocBookDialog();
		dialog.init( container );
		dialog.setVisible( true );

		if ( dialog.isOk() ) {
			dialog.store( container );
			applyTransformation( container, true );
		}
		dialog.dispose();
	}
	
	// JOB ----------------
	
	private XMLContainer docbookContainer;
	
	public void dispose() {
		docbookContainer = null;
	}
	
	public String getName() {
		return "DocBook Transforming";
	}

	public Object getSource() {
		return null;
	}

	public boolean isAlone() {
		return false;
	}
	
	public boolean hasErrors() {
		return errorMessage != null;
	}

	public void run() {
		errorMessage = null;
		boolean ok = applyTransformation( docbookContainer );
		if ( !ok )
			errorMessage = "Error found";
	}

	String errorMessage = null;
	
	public String getErrorMessage() { return errorMessage; }
	
	public void stopIt() {

	}
	
	private static URL checkForResource( String resource ) {
		URL u = ClassLoader.getSystemResource( resource );
		if ( u != null )
			return u;	
// 		For debugging spaces
//		resource = "C:/Program Files/editix/res/" + resource;		
		File f = new File( resource );
		if ( f.exists() ) {
			try {
				return f.toURI().toURL();
			} catch( MalformedURLException exc ) {
			}
		}
		EditixFactory.buildAndShowErrorDialog( "Can't find " + resource );
		return null;
	}

	public static void applyTransformation( XMLContainer container, boolean background ) {
		if ( background ) {
			DocBookAction dba = ( DocBookAction )ActionModel.restoreAction( "docbookt" );
			if ( dba == null ) {
				System.err.println( "Can't find the docbookt action" );
			} else {
				dba.docbookContainer = container;
				JobManager.addJob( dba );
			}
		} else
			applyTransformation( container );
	}
	
	public static boolean applyTransformation( XMLContainer container ) {
		String renderer = ( String )container.getProperty( "docBook.render" );
		String output = ( String )container.getProperty( "docBook.output" );
		
		boolean viewer = "true".equals( container.getProperty( "docBook.application" ) );
		
		container.setProperty( "xslt.data.file", container.getCurrentDocumentLocation() );
		
		URL url = null;
		boolean foMode = false;

		if ( "HTML".equals( renderer ) ) {
			url = checkForResource( 
					Preferences.getPreference( "docbook", "htmlstylesheet", "docbook/html/docbook.xsl" ) 
			);

			if ( url == null )
				return false;

		} else
		if ( "HTML Help".equals( renderer ) ) {
			viewer = false;
			url = checkForResource( 
					Preferences.getPreference( "docbook", "htmlHelperstylesheet", "docbook/htmlhelp/htmlhelp.xsl" ) 
			);
			
			if ( url == null )
				return false;
			
			container.setProperty( "xslt.param.name.0", "base.dir" );
			
			File f = new File( output );
			
			
//			String suffix = null;
			if ( output.lastIndexOf( "." ) > -1 ) {
				f = f.getParentFile();
			}

			output = "docbook.tmp";

			container.setProperty( "xslt.param.value.0", f.toString() + "/" );
			container.setProperty( "xslt.param.name.1", "manifest.in.base.dir" );
			container.setProperty( "xslt.param.value.1", "1" );
//			if ( suffix != null ) {
//				container.setProperty( "xslt.param.name.2", "htmlhelp.hhp" );			
//				container.setProperty( "xslt.param.value.2", suffix );	
//			}
			
		} else
		if ( "XHTML".equals( renderer ) ) {	
			url = checkForResource( 
					Preferences.getPreference( "docbook", "xhtmlstylesheet", "docbook/xhtml/docbook.xsl" ) 
			);

			if ( url == null )
				return false;
			
		} else
		if ( "Java Help".equals( renderer ) ) {
			viewer = false;
			url = checkForResource( 
					Preferences.getPreference( "docbook", "javaHelpstylesheet", "docbook/javahelp/javahelp.xsl" ) 
			);
			
			if ( url == null )
				return false;
			
		} else
		if ( "FO".equals( renderer ) ) {
			viewer = false;
			url = checkForResource( 
					Preferences.getPreference( "docbook", "fostylesheet", "docbook/fo/docbook.xsl" ) 
			);
			
			if ( url == null )
				return false;
			
		} else {
			url = checkForResource( 
					Preferences.getPreference( "docbook", "fostylesheet", "docbook/fo/docbook.xsl" ) 
			);
			
			if ( url == null )
				return false;
		
			output = new File( EditixApplicationModel.getAppUserPath(), "editix.tmp" ).toString();

			// Faire d'abord la conversion en FO
			container.setProperty( "fo.render", renderer );
			foMode = true;
		}

		container.setProperty( "xslt.result.file", output );
		container.setProperty( "xslt.xslt.file", com.japisoft.framework.toolkit.FileToolkit.toFile( url ) );
		container.setProperty( "docbook.ok", "true" );

		EditixApplicationModel.debug( "Stylesheet " + container.getProperty( "xslt.xslt.file" ) );
		
		ErrorListener xsltAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXSLT" );		

		
		boolean ok = XSLTAction.applyTransformation( container, false, false, false, xsltAction );

		if ( foMode && ok ) {
			container.setProperty( "fo.output", container.getProperty( "docBook.output" ) );
			
			String currentLocation = container.getCurrentDocumentLocation();
			try {
				container.setProperty( "file.checker.ignore", "true" );
				container.getDocumentInfo().setCurrentDocumentLocation( output );
				FOPAction.applyFO( container );
			} finally {
				container.getDocumentInfo().setCurrentDocumentLocation( currentLocation );
				container.setProperty( "file.checker.ignore", null );
			}
		}

		if ( viewer ) { 
		
			// Restore the initial one
			output = ( String )container.getProperty( "docBook.output" );
			
			if ( output.toLowerCase().endsWith( "pdf") ) {
				
				try {
					if ( BrowserCaller.isWindowsPlatform() ) {
						Runtime.getRuntime().exec(
								"rundll32 SHELL32.DLL,ShellExec_RunDLL " +
								output 
						) ;
					}
				} catch ( IOException e ) {
				}
				
			} else {
				
				if ( output.toLowerCase().endsWith( "html" ) || 
						output.toLowerCase().endsWith( "htm" ) ) {
					
					BrowserCaller.displayURL( output );
					
				}
				
			}
			
		}
		
		return ok;
	}

	public static void main( String[] args ) {
		
	}
	
}
