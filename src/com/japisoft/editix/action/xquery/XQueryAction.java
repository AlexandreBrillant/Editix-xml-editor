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

package com.japisoft.editix.action.xquery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NamespaceUri;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.editix.action.xsl.XSLTDialog;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.editor.xquery.XQueryContainer;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public class XQueryAction extends XSLTAction {

/*	protected void initAction() {
		XSLT = "xquery";
		
		DATAFILE_PROPERTY = XSLT + ".data.file";
		XSLTFILE_PROPERTY = XSLT + ".xslt.file";
		XSLTRESULT_PROPERTY = XSLT + ".result.file";		
	} */
	
	protected XSLTDialog getDialog() {
		return EditixFactory.getConfigDialog( true );
	}

	public void run() {
		errorMessage = null;
		containerXSLT.getMainContainer().getErrorManager().initErrorProcessing();
		boolean ok = xquery(
				containerXSLT.getMainContainer().getDocumentInfo().getType(),
				containerXSLT, DEBUG_MODE, false, this);
		if (!ok) {
			errorMessage = "Error found";	
		}
		containerXSLT.getMainContainer().getErrorManager().stopErrorProcessing();
	}

	public boolean transform( 
			IXMLPanel container,
			boolean background, 
			boolean debugMode,
			boolean profilerMode,
			ErrorListener errorListener ) {		

		if ( background ) {
			containerXSLT = container;
			JobManager.addJob( ( Job )this );		
			return true;			
		} else {
			
			return xquery(
				container.getMainContainer().getDocumentInfo().getType(),
				container, 
				debugMode, 
				profilerMode, 
				errorListener 
			);
		}

	}

	public static boolean xquery(
			String type,
			IXMLPanel container,
			boolean debugMode,
			boolean profilerMode,
			ErrorListener errorListener) {

		if ( container.getMainContainer() != null )
			container.getMainContainer().getErrorManager().notifyNoError( false );
		
		String data = ( String ) container.getProperty( "xquery.data.file" );
		String xslt = ( String ) container.getProperty( "xquery.xslt.file" );
		String res = ( String ) container.getProperty( "xquery.result.file" );

		if ( EditixApplicationModel.DEBUG_MODE ) {
			System.out.println( "DATA=" + data );
			System.out.println( "XSLT=" + xslt );
			System.out.println( "RES=" + res );
		}

		if ( data == null || 
				"".equals( data ) ) {
			EditixFactory.buildAndShowErrorDialog( "No data file" );
			return ERROR;
		}

		if ( xslt == null || 
				"".equals( xslt ) ) {
			EditixFactory.buildAndShowErrorDialog( "No xquery file ?" );
			return ERROR;
		}

		if ( res == null ) {
			if ( container.getMainContainer() != null ) {
				res = container.getMainContainer().getCurrentDocumentLocation();
				File f = new File( res ).getParentFile();
				res = new File( f, "XQRResult.xml" ).toString();
			}
		}

		XMLContainer cont = container.getMainContainer();
		if ( cont != null ) {
			if ( cont.hasErrorMessage() ) {
				cont.getErrorManager().notifyNoError( false );
			}
		}

		boolean openDocument = "true".equals( ( String ) container
				.getProperty( "xquery.openFile" ) );
		boolean displayDocument = "true".equals( ( String ) container
				.getProperty( "xquery.displayFile" ) );
		
		Configuration config = new Configuration();
		StaticQueryContext staticContext = 
		        new StaticQueryContext( config );

		// Apply parameters
		for (int i = 0; i < 100; i++) {
			String param = (String) container
					.getProperty( "xquery.param.name." + i);
			String value = (String) container
					.getProperty( "xquery.param.value." + i);
			if (param != null && value != null && !"".equals(param)
					&& !"".equals(value)) {

				/*
				staticContext.declareNamespace(
						param, 
						value 
				);
				*/
				
				// For Saxon 12
				staticContext.declareNamespace(
						param,
						NamespaceUri.of( value ) );
			}
		}		
		
		try {
			XMLFileData xfd = XMLToolkit.getContentFromURI( xslt, null );
			
			try {
				

				/*
				XQueryExpression exp = 
				        staticContext.compileQuery( xfd.getContent() );
				
				DynamicQueryContext dynamicContext = 
				    new DynamicQueryContext( config );
				*/
				
				/*
				dynamicContext.setContextNode(
						staticContext.buildDocument(
								new StreamSource( data ) )
				);
				*/

				/*
				StringWriter buffer = new StringWriter();
				StreamResult result = new StreamResult( buffer );

				Properties props = new Properties();

				if ( res.toLowerCase().endsWith( "xml" ) ) {
					props.setProperty( OutputKeys.METHOD, "xml");
					props.setProperty( OutputKeys.INDENT, "yes");
				} else {
					props.setProperty( OutputKeys.METHOD, "text");
				}


				dynamicContext.setErrorListener(errorListener);
							
				exp.run( 
						dynamicContext, 
						result, 
						props );
				*/
								
				
				Processor processor = new Processor(false);
				XQueryCompiler xqueryCompiler = processor.newXQueryCompiler();
				XQueryExecutable xqueryExec = xqueryCompiler.compile( xfd.getContent() );
				XQueryEvaluator evaluator = xqueryExec.load();
				
				DocumentBuilder docBuilder = processor.newDocumentBuilder();
				XdmNode contextNode = docBuilder.build( new StreamSource( data ) );

				evaluator.setContextItem( contextNode );				
				evaluator.setErrorListener( errorListener );

//				StringWriter writer = new StringWriter();
				
				OutputStreamWriter writer = new OutputStreamWriter( 
						new FileOutputStream( res ), 
						Preferences.getPreference( "xquery", "output.encoding", "UTF-8" ) ); 
				
				try {
				
					Serializer serializer = processor.newSerializer( writer );
	
					
					if (res.toLowerCase().endsWith("xml")) {
					    serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
					    serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
					} else {
					    serializer.setOutputProperty(Serializer.Property.METHOD, "text");
					}
	
					evaluator.run( serializer );

				} finally {
					
					writer.close();
					
				}
				
				/*
				
				OutputStreamWriter writer = new OutputStreamWriter( 
						new FileOutputStream( res ), 
						Preferences.getPreference( "xquery", "output.encoding", "UTF-8" ) 
				);
				try {
					writer.write( result.toString() );
				} finally {
					writer.close();
				}
				*/
				
				if ( openDocument ) {
					String typeRes = DocumentModel.getTypeForFileName( res );
					ActionModel.activeActionById(ActionModel.OPEN, null, res, typeRes );
				} else
				if ( displayDocument ) {
					BrowserCaller.displayURL(
							res );
				}	
				
				if (container instanceof IXMLPanel) {
					((IXMLPanel) container).setProperty(
							XQueryContainer.LOADRES_CMD, "ok" );			
				}

			} catch (SaxonApiException exc) {

				Throwable cause = exc.getCause();
				if ( cause instanceof XPathException ) {
					XPathException xe = ( XPathException )cause;
					errorListener.fatalError( 
							new TransformerException( 
									exc.getMessage(), 
									xe.getLocator() ) );	
				}

				return false;

			}
		} catch (Throwable e) {
			try {
				errorListener.fatalError( 
						new TransformerException( 
							"Can't load the XQuery document " ) 
				);
			} catch (TransformerException e1) {
			}

			return false;
		}

		return true;
	}

}
