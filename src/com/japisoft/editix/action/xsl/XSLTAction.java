package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.Receiver;

// import net.sf.saxon.FeatureKeys;

//import org.apache.xalan.processor.TransformerFactoryImpl;
import net.sf.saxon.lib.FeatureKeys;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.w3c.dom.Document;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.fop.FOPAction;
import com.japisoft.editix.action.xquery.XQueryAction;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.steps.EditixEntityResolver;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.xslt.XSLTConsoleMode;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.editix.xslt.debug.XSLTManager;
import com.japisoft.framework.job.HeavyJob;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.XMLPadSAXParserFactory;

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
public class XSLTAction extends AbstractAction implements HeavyJob,
		ErrorListener {

	// For parameters
//	public static String XSLT = "xslt";
	
	// ErrorListener

	public void error(TransformerException exception)
		throws TransformerException {
		XSLTManager.processError( containerXSLT != null ? containerXSLT.getMainContainer() : workingContainer, exception );
	}

	public void fatalError(TransformerException exception)
			throws TransformerException {
		XSLTManager.processError( containerXSLT != null ? containerXSLT.getMainContainer() : workingContainer, exception );
	}

	public void warning(TransformerException exception)
			throws TransformerException {}

	private boolean transformationError = false;
	private static XMLContainer workingContainer;
	
	protected void initAction() {		
/*		XSLT = "xslt";
		
		DATAFILE_PROPERTY = XSLT + ".data.file";
		XSLTFILE_PROPERTY = XSLT + ".xslt.file";
		XSLTRESULT_PROPERTY = XSLT + ".result.file"; */
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		initAction();
		transformationError = false;
		
		workingContainer = EditixFrame.THIS.getSelectedContainer();
		if ( workingContainer == null ) return;
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if (EditixFactory.mustSaveDialog(workingContainer)) {
			return;
		}

		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
				com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION, workingContainer,
				workingContainer.getEditor());
		
		if ( !ok )
			return;
		
		//workingContainer.setProperty(getPropertyForDataFile(), workingContainer
		//		.getCurrentDocumentLocation());

		XSLTDialog dialog = getDialog();
		dialog.init(panel);
		dialog.setVisible(true);
		dialog.dispose();
		if (dialog.isOk()) {
			dialog.store(panel);

			transform(panel, true, false, false, this);
		} else
			workingContainer = null;

	}

	protected XSLTDialog getDialog() { return EditixFactory.getConfigDialog( false ); }

	// JOB

	protected IXMLPanel containerXSLT;

	public String getName() {
		
		if ( this instanceof XQueryAction ) {
			return "XQuery Transforming";
		} else
			return "XSLT Transforming";
	}

	public void dispose() {
		containerXSLT = null;
		workingContainer = null;
	}

	public Object getSource() {
		return null;
	}

	public boolean isAlone() {
		return false;
	}

	public String errorMessage = null;

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean hasErrors() {
		return errorMessage != null;
	}
	
	public void run() {
		errorMessage = null;
		containerXSLT.getMainContainer().getErrorManager().initErrorProcessing();
		String type = containerXSLT.getMainContainer().getDocumentInfo().getType(); 
		boolean ok = finalTransform(type,containerXSLT, DEBUG_MODE, false, this);
		if (!ok) {
			errorMessage = "Error found";	
		}
		containerXSLT.getMainContainer().getErrorManager().stopErrorProcessing();
	}

	public void stopIt() {}

/*	public static String DATAFILE_PROPERTY = XSLT + ".data.file";
	public static String XSLTFILE_PROPERTY = XSLT + ".xslt.file";
	public static String XSLTRESULT_PROPERTY = XSLT + ".result.file"; */

	protected String prefixForParameter = "xslt";
	
	protected String getPropertyForDataFile() {
		return prefixForParameter + ".data.file";
	}

	protected String getPropertyForXsltFile() {
		return prefixForParameter + ".data.file";
	}
	
	protected String getPropertyForResFile() {
		return prefixForParameter + ".data.file";
	}
	
	public static javax.xml.transform.TransformerFactory getTransformerFactoryV1(
			boolean debugMode) {
		javax.xml.transform.TransformerFactory tFactory = null;

		if ( debugMode ) {
			System.setProperty( "javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" );
			System.setProperty( "javax.xml.parsers.SAXParserFactory",
					"org.apache.xerces.jaxp.SAXParserFactoryImpl" );
			// TAKE SAXON 1 !
			tFactory = new com.icl.saxon.TransformerFactoryImpl();
		} else {
			// Take the default one
			tFactory = XSLTTransformer.getTransformerFactory();
		}

		try {
			tFactory.setAttribute(
					TransformerFactoryImpl.FEATURE_SOURCE_LOCATION,
					Boolean.TRUE );
		} catch (Throwable exc) {
		}
		
		return tFactory;
	}

	public static javax.xml.transform.TransformerFactory getTransformerFactoryV2(
			boolean debugMode) {
		javax.xml.transform.TransformerFactory tFactory = null;

		if ( debugMode ) {
			System.setProperty( "javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" );
			System.setProperty( "javax.xml.parsers.SAXParserFactory",
					"org.apache.xerces.jaxp.SAXParserFactoryImpl" );
		}

		// USE ONLY SAXON 2 !
		tFactory = new net.sf.saxon.TransformerFactoryImpl();	
		
		try {
			tFactory.setAttribute(FeatureKeys.LINE_NUMBERING, Boolean.TRUE);
		} catch (RuntimeException e) {
		}	

		try {
			tFactory.setAttribute( FeatureKeys.OPTIMIZATION_LEVEL, new Integer( 0 ) );
		} catch (RuntimeException e) {
		}	
		
		return tFactory;
	}
		
	public static final boolean ERROR = false;
	public static final boolean OK = true;

	public static boolean DEBUG_MODE = false;

	/** For overriding in the case of XQuery */ 
	public boolean transform( 
			IXMLPanel container,
			boolean background, 
			boolean debugMode,
			boolean profilerMode,
			ErrorListener errorListener ) {		
		return applyTransformation( 
				container, 
				background, 
				debugMode, 
				profilerMode, 
				errorListener );
	}

	public static boolean applyTransformation(
			IXMLPanel container,
			boolean background, 
			boolean debugMode,
			boolean profilerMode,
			ErrorListener errorListener ) {
		DEBUG_MODE = debugMode;
		if ( !background || 
				debugMode || profilerMode ) {
			try {
				container.getMainContainer().getErrorManager().initErrorProcessing();
				return finalTransform(container.getMainContainer().getDocumentInfo().getType(), container, debugMode, profilerMode, errorListener);
			} finally {
				container.getMainContainer().getErrorManager().stopErrorProcessing();
				workingContainer = null;
			}
		}
		else {
			XSLTAction a = ( XSLTAction ) ActionModel
					.restoreAction( "transformFromXSLT" );
			if (a == null) {
				System.err.println( "Can't find XSLT action transformFromXSLT" );
				return false;
			}
			a.containerXSLT = container;
			a.errorMessage = null;
			JobManager.addJob( ( Job )a );
			return true;
		}
	}

	public static boolean finalTransform(
			String type,
			IXMLPanel container,
			boolean debugMode,
			boolean profilerMode,
			ErrorListener errorListener) {

		String data = ( String ) container.getProperty( "xslt.data.file" );
		String xslt = ( String ) container.getProperty( "xslt.xslt.file" );
		String res = ( String ) container.getProperty( "xslt.result.file" );

		if ( data == null || 
				"".equals( data ) ) {
			EditixFactory.buildAndShowErrorDialog( "No data file" );
			return ERROR;
		}

		if ( xslt == null || 
				"".equals( xslt ) ) {
			EditixFactory.buildAndShowErrorDialog( "No stylesheet ?" );
			return ERROR;
		}
				
		XMLContainer cont = container.getMainContainer();
		if ( cont != null ) {
			if ( cont.hasErrorMessage() ) {
				cont.getErrorManager().notifyNoError( false );
			}
		}

		boolean openDocument = "true".equals( ( String ) container
				.getProperty( "xslt.openFile" ) );
		boolean displayDocument = "true".equals( ( String ) container
				.getProperty( "xslt.displayFile" ) );
		boolean fop = "true".equals( ( String ) container
				.getProperty( "xslt.fop" ) );
		
		// Check the XSLT version

		double version = 1.0;

		if ( "XSLT".equals( type ) || 
				"XSLT2".equals( type ) ) {

			if ( cont != null ) {			
				if ( cont.getRootNode() != null ) {
					String str = cont.getRootNode().getAttribute( "version" );
					if (str != null) {
						try {
							version = Double.parseDouble( str );
						} catch ( NumberFormatException exc ) {
						}
					}
				}
			} else
			if ( "XSLT2".equals( type ) )
				version = 2.0;

		} else {

			// Must read the XSLT file
			try {
				BufferedReader reader = new BufferedReader( new FileReader( xslt ) );
				try {
					String line = null;
					// Skip the XML prolog
					reader.readLine();
					boolean stylesheet = false;
					all:while ( ( line = reader.readLine() ) != null ) {
						if ( line.indexOf( "stylesheet" ) > - 1 ) {
							// Get the number of the version
							stylesheet = true;
						}
						if ( stylesheet ) {
							int i = 0;
							String sTmp = null;
							if ( ( i = line.indexOf( "version" ) ) > -1 ) {
								for ( int j = i + 7; j < line.length(); j++ ) {
									if ( line.charAt( j ) == '"' || line.charAt( j ) == '\'' ) {
										if ( sTmp != null ) {
											version = Double.parseDouble( sTmp );
											break all;
										}
										sTmp = "";
									} else
										if ( sTmp != null )
											sTmp += line.charAt( j );
								}
							}
						}
					}
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			} catch (FileNotFoundException e) {
			} catch( IOException e ) {}
		}

		TransformerFactory factory = null;
		
		if (version >= 2.0) {
			factory = getTransformerFactoryV2( debugMode );
		} else
		if ( version == 1.0 ) {
			factory = getTransformerFactoryV1( debugMode );
		}

		factory.setErrorListener( errorListener );

		return applyCommonTransformation(
					(int)version,
					openDocument, 
					displayDocument, 
					fop,
					data, 
					xslt, 
					res, 
					cont,
					container,
					debugMode,
					profilerMode,
					factory );
	
	}

	private static boolean applyCommonTransformation(
			int version,
			boolean openDocument,
			boolean displayDocument,
			boolean fop,
			String data, 
			String xslt, 
			String res, 
			XMLContainer cont,
			IXMLPanel container, 
			boolean debugMode,
			boolean profilerMode,
			javax.xml.transform.TransformerFactory tFactory ) {

		// Enabled console
		if ( container instanceof XSLTConsoleMode ) {
			// ( ( XSLTConsoleMode )container ).setEnabledConsole( true );
		}
		try {
		
			return applyCommonTransformationConsole(
					version,
					openDocument,
					displayDocument,
					fop,
					data, 
					xslt, 
					res, 
					cont,
					container, 
					debugMode,
					profilerMode,
					tFactory );
			
		} finally {
			
			if ( container instanceof XSLTConsoleMode ) {
				// ( ( XSLTConsoleMode )container ).setEnabledConsole( false );
			}
			
		}
		
	}
		
	private static boolean applyCommonTransformationConsole(
				int version,
				boolean openDocument,
				boolean displayDocument,
				boolean fop,
				String data, 
				String xslt, 
				String res, 
				XMLContainer cont,
				IXMLPanel container, 
				boolean debugMode,
				boolean profilerMode,
				javax.xml.transform.TransformerFactory tFactory ) {
		
		
		if ( "".equals( res) ) 
			res = "temp.xml";
		
		if ( debugMode )
			XSLTManager
					.updateDebugListenerForFactory( 
							container, 
							tFactory );

		if ( profilerMode )
			XSLTManager
			.updateProfilerListenerForFactory( 
					container, 
					tFactory );

		// For XML Catalogs
		tFactory.setURIResolver( EditixEntityResolver.getInstance() );
		
		if ( "true".equals(
				System.getProperty( "editix.debug" ) ) )
			System.out.println( "Transformer=" + tFactory );

		String media = null, title = null, charset = null;

		if ( cont != null )
			cont.getErrorManager().notifyNoError( false );

		try {
			Transformer transformer = null;
			try {

				String encoding = Toolkit.getCurrentFileEncoding();
				if ( "DEFAULT".equals( encoding ) || 
						"AUTOMATIC".equals( encoding ))
					charset = null;
				else
					charset = encoding;

				javax.xml.transform.Source stylesheet = null;

				try {
					stylesheet = tFactory.getAssociatedStylesheet(
							new StreamSource( xslt ), 
							media, 
							title, 
							charset );
				} catch (TransformerConfigurationException th) {
				}

				if ( stylesheet == null ) {
					transformer = tFactory
							.newTransformer(
									new javax.xml.transform.stream.StreamSource(
									xslt ) );
				} else
					transformer = tFactory.newTransformer( stylesheet );

			} catch ( TransformerException exc ) {
				if ( cont != null ) {
					XSLTManager.processError( cont, exc );
				}
				return ERROR;
			}

			if ( res == null && cont != null ) {
				res = cont.getCurrentDocumentLocation();
				File f = new File( res ).getParentFile();
				res = new File( f, "XSLResult" ).toString();
			}

			// Apply parameters
			for (int i = 0; i < 100; i++) {
				String param = (String) container
						.getProperty( "xslt.param.name." + i);
				String value = (String) container
						.getProperty( "xslt.param.value." + i);
				if (param != null && value != null && !"".equals(param)
						&& !"".equals(value)) {

					ApplicationModel.debug( "Parameter : " + param + " = " + value );
					
					transformer.setParameter(param, value);
				}
			}

			StreamResult sr = null;
			Document d = null;

			if ( !debugMode ) {
				DocumentBuilder builder = XMLPadSAXParserFactory.getNewDocumentBuilder( false, false );
				if ( SharedProperties.DEFAULT_ENTITY_RESOLVER != null )
					builder.setEntityResolver( SharedProperties.DEFAULT_ENTITY_RESOLVER );
				try {
					d = builder.parse( new File( data ) );				
				} catch( Throwable th  ) {
					if ( cont != null )
						cont.getErrorManager().notifyError(
							"Can't parse the data source " + data + " Please fix it before transforming", 0 );
					return ERROR;
				}
			}

			Source source = null;
			if ( d != null )
				source = new DOMSource( d );
			else
				source = new StreamSource( data );

			// Bug if whitespaces inside the path
			//sr = new StreamResult( new File( res ) );
			
			String fopExt = null;
			String fopResultFile = null;
			
			if ( fop ) {
				// Chance the file ext to fo
				int i = res.lastIndexOf( "." );
				if ( i > -1 ) {
					// FOP target
					fopExt = res.substring( i + 1 ).toLowerCase();
					fopResultFile = res;
					res = res.substring( 0, i + 1 ) + "fo";
				}
			}

			if ( version == 1 )
				sr = new StreamResult( res );
			else
				sr = new StreamResult( new File( res ) );
			
			workingContainer = container.getMainContainer();

			MessageReceiver mr = null;
			
			if ( transformer instanceof Controller ) {
				( ( Controller )transformer ).setMessageEmitter( (Receiver)( mr = new XSLT2MessageReceiver() ) ); 
			} else
			if ( transformer instanceof com.icl.saxon.Controller ) {
				( ( com.icl.saxon.Controller )transformer ).setMessageEmitter( (com.icl.saxon.output.Emitter)( mr = new XSLT1MessageReceiver() ) );
			}
			
			transformer.transform( source, sr );
			
			if ( mr != null ) {
				if ( container instanceof XSLTConsoleMode ) {
					( ( XSLTConsoleMode )container ).setMessage( mr.getResult() );
				}
			}
			
			workingContainer = null;
			
			if ( openDocument ) {
				String type = DocumentModel.getTypeForFileName( res );
				ActionModel.activeActionById(ActionModel.OPEN, null, res, type );
			} else
			if ( displayDocument ) {
				BrowserCaller.displayURL(
						res );
			} else 
			if ( fop ) {

				if ( "pdf".equalsIgnoreCase( fopExt ) )  {
					container.setProperty( "fo.render", "PDF" );	
				} else
				if  ( "rtf".equalsIgnoreCase( fopExt ) ) {
					container.setProperty( "fo.render", "RTF" );
				} else
				if ( "svg".equalsIgnoreCase( fopExt ) ) {
					container.setProperty( "fo.render", "SVG" );
				} else
				if ( "ps".equalsIgnoreCase( fopExt ) ) {
					container.setProperty( "fo.render", "PS" );
				} else {
					EditixFactory.buildAndShowWarningDialog( "Unknown FOP target file :" + fopExt + "?" );
					return ERROR;
				}

				container.setProperty( "fo.output", fopResultFile );				
				container.setProperty( "fo.source", res );

				FOPAction.applyFO( container.getMainContainer(), false );
				
			}

			// Store the result content

			if ( !fop ) {
				if (container instanceof IXMLPanel) {
					((IXMLPanel) container).setProperty(
							XSLTEditor.LOADRES_CMD, "ok" );			
				}
			}
			
		} catch (TransformerException ex) {
			ApplicationModel.debug( ex );
			XSLTManager.processError( cont, ex );
		} catch ( Throwable th ) {
			ApplicationModel.debug( th );
			return ERROR;
		}
		return OK;
	}

}
