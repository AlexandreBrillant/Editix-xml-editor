package com.japisoft.editix.action.fop;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.AbstractAction;

import com.japisoft.framework.application.descriptor.ActionModel;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.job.HeavyJob;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.fo.ValidationException;
//import org.apache.xalan.processor.TransformerFactoryImpl;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

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
public class FOPAction extends AbstractAction implements HeavyJob {
	
	public void actionPerformed( ActionEvent e ) {
				
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}
		
		ActionModel.activeActionById( ActionModel.SAVE, e );
		if ( !ActionModel.LAST_ACTION_STATE )
			return;
		
		FOPDialog dialog = new FOPDialog();
		dialog.init( container );
		dialog.setVisible( true );
		dialog.dispose();
		if ( dialog.isOk() ) {
			dialog.store( container );
			if ( !container.hasProperty( "fo.output") ) {
				EditixFactory.buildAndShowErrorDialog( "No output file" );
			} else
				applyFO( EditixFrame.THIS.getSelectedContainer(), true );
		}
	}

	// JOB
	
	XMLContainer fopContainer = null;
	
	public String getName() {
		return "FOP Transforming";
	}
	public void dispose() {
		fopContainer = null;
	}
	public Object getSource() {
		return null;
	}
	public boolean isAlone() {
		return false;
	}
	
	boolean errors = false;
	
	public void run() {
		errors = false;
		// Reset errors
		fopContainer.getErrorManager().notifyNoError(false);
		if ( !applyFO( fopContainer ) ) {
			errors = true;
		}
	}
	
	public boolean hasErrors() {
		return errors;
	}
	
	public void stopIt() {
	}
	
	public String getErrorMessage() { return null; }
		
	public static void applyFO( XMLContainer container, boolean background ) {
		if ( !background )
			applyFO( container );
		else {
			FOPAction a = ( FOPAction )ActionModel.restoreAction( "fot" );
			if ( a == null ) {
				System.err.println( "Inner Error => Can't find action for 'fot'" );
				return;
			}
			a.fopContainer = container;
			JobManager.addJob( ( Job )a );
		}
	}
	
	public static boolean applyFO( IXMLPanel panel ) {
 		String render = ( String ) panel.getProperty( "fo.render" );
		String output = ( String ) panel.getProperty( "fo.output" );
		boolean viewer = "true".equals( panel.getProperty( "fo.viewer" ) );
		OutputStream out = null;

		if ( output != null && 
					output.length() > 1 ) {
			try {
				out = new FileOutputStream( output );
			} catch (Throwable th) {
				if ( panel.getMainContainer() != null )
					panel.getMainContainer().getErrorManager().notifyError(
						"Can't use " + output );
				return false;
			}

		} else {
			if ( !"-".equals( output ) ) {
				EditixFactory.buildAndShowErrorDialog( "No output choosen !" );
				return false;
			}
		}

		String source = ( String )panel.getProperty( "fo.source" );

		URL sourceUrl = null;
		
		// Relative access
		try { 
			sourceUrl = new File( source ).getParentFile().toURI().toURL();
		} catch( Exception exc ) {
		}

		FopFactory fopfactory = null;
		
		try {
			fopfactory = EditixFOPFactory.newInstance( sourceUrl );
		} catch( Exception exc ) {
			// exc.printStackTrace();
			EditixFactory.buildAndShowErrorDialog( "Can't initialize FOP :" + exc.getMessage() );
			return false;
		}
		
		Fop fop = null;
		
		try {
			if ( "PDF".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_PDF, out );
			}
			else if ( "PRINT".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_FOP_PRINT );
			}
			else if ( "PCL".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_PCL, out );
			}
			else if ( "PS".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_POSTSCRIPT, out );
			}
			else if ( "TXT".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_PLAIN_TEXT, out );
			}
			else if ( "SVG".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_SVG, out );
			}
			else if ( "RTF".equals( render ) ) {
				fop = fopfactory.newFop( MimeConstants.MIME_RTF, out );
			}
		} catch (FOPException e1) {
			EditixFactory.buildAndShowErrorDialog( "Error :" + e1.getMessage() );
		}
				
		try {
			try {
				//Setup input
				//Reader in =
				//	com.japisoft.editix.toolkit.Toolkit.getReaderForFile(
				//		container.getCurrentDocumentLocation() );

	            // Setup JAXP using identity transformer
				
				try {
					TransformerFactory factory = XSLTTransformer.getTransformerFactory();

					try {
/*						factory.setAttribute(
								TransformerFactoryImpl.FEATURE_SOURCE_LOCATION,
								Boolean.TRUE ); */
					} catch (Throwable exc) {
					}
					
					Transformer transformer = factory.newTransformer(); // identity transformer
					Source src;
					
					
					if ( source == null ) {
						if ( panel.getMainContainer() != null ) {
							source = panel.getMainContainer().getCurrentDocumentLocation();
							panel.setProperty( "fo.source", source );
						}
					}
					
					// Setup input stream
					if ( source.indexOf( "://" ) == -1 ) {
						src = new StreamSource( new File( source ) );
					}
					else {
						src = new StreamSource( source );
					}

					// Resulting SAX events (the generated FO) must be piped through to FOP
					Result res = new SAXResult(fop.getDefaultHandler());

					// Start XSLT transformation and FOP processing
					transformer.transform(src, res);
		
					if ( viewer ) {
												
						Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " +
								output ) ;

					}

				} catch (TransformerConfigurationException e) {
					if ( panel.getMainContainer() != null )
						panel.getMainContainer().getErrorManager().notifyError(
							e.getMessage(),
							e.getLocator() != null ? e.getLocator().getLineNumber() : -1 );
					else
						return false;
				} catch (FOPException e) {
					if ( panel.getMainContainer() != null ) {
						panel.getMainContainer().getErrorManager().notifyError(
								e.getMessage() 
					); } else
						return false;
				} catch (TransformerException e) {
					String message = null;
					int line = -1;
					
					if ( e.getException() instanceof SAXException ) {
						SAXException e2 = ( SAXException )e.getException();						
						message = e2.getMessage();
						if ( e2 instanceof ValidationException ) {
							ValidationException ve = ( ValidationException )e2;
						}
						// Extract the line value
						if ( line == -1 && message != null ) {
							int i = message.indexOf( ':' );
							if ( i > -1 ) {
								int j = message.indexOf( ':', i + 1 );
								if ( j > -1 ) {
									String tmp = message.substring( i + 1, j );
									try {
										line = Integer.parseInt( tmp );
									} catch (NumberFormatException e1) {
									}
								}
							}
						}
						if ( line == -1 && message != null ) {
							int i = message.indexOf( '(' );
							if ( i > -1 ) {
								int j = message.indexOf( '/', i + 1 );
								if ( j > -1 ) {
									String tmp = message.substring( i + 1, j );
									try {
										line = Integer.parseInt( tmp );
									} catch (NumberFormatException e1) {
									}				
								}
							}
						}
					}

					if ( message == null )
						message = e.getLocalizedMessage();
					
					int i = message.lastIndexOf( "Exception" );
					if  ( i > -1 )
						message = message.substring( i + 10 );

					if ( panel.getMainContainer() != null ) {
						panel.getMainContainer().getErrorManager().
							notifyError( null, true, null, line - 1, 0, -1, message, false );
						panel.getMainContainer().getErrorManager().stopErrorProcessing();
					}
					return false;
				}				

			} finally {
				if ( out != null ) {
					try {
						out.close();
					} catch ( Throwable th ) {
					}
				}
			}

		} catch ( FileNotFoundException exc ) {
			EditixFactory.buildAndShowErrorDialog(
				"Can't use " + panel.getMainContainer().getCurrentDocumentLocation());
			return false;
		} catch( IOException exc2) {
			EditixFactory.buildAndShowErrorDialog(
				"Can't use " + panel.getMainContainer().getCurrentDocumentLocation());
		}
		return true;
	}

}
