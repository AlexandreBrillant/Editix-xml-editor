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

package com.japisoft.xflows.task.xquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XQueryFileRunner implements TaskRunner {

	private TaskContext context = null;

	public boolean run( TaskContext context ) {
		this.context = context;
		try {
			File fxquery = new File( context.getParam( XQueryUI.XQUERY ) );		
			context.addInfo( "Transforming " + context.getCurrentSourceFile() );
			return applyTransformation( context, context.getCurrentSourceFile(), fxquery, context.getCurrentTargetFile() );
		} finally {
			this.context = null;
		}
	}

	public static boolean applyTransformation( TaskContext context, File data, File xquery, File res ) {

		try {
			Processor processor = new Processor(false);

			XQueryCompiler xqueryCompiler = processor.newXQueryCompiler();
			XQueryExecutable xqueryExec = xqueryCompiler.compile( new FileReader( xquery ) );
			XQueryEvaluator evaluator = xqueryExec.load();
			
			DocumentBuilder docBuilder = processor.newDocumentBuilder();
			XdmNode contextNode = docBuilder.build( new StreamSource( data ) );
	
			evaluator.setContextItem( contextNode );				
				
			OutputStreamWriter writer = new OutputStreamWriter( 
					new FileOutputStream( res ), 
					Preferences.getPreference( "xquery", "output.encoding", "UTF-8" ) 
			); 
			
			try {
			
				Serializer serializer = processor.newSerializer( writer );
	
				
				if ( "xml".equals( context.getParam( XQueryUI.OUTPUT ) ) ) {
				    serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
				    serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
				} else {
				    serializer.setOutputProperty(Serializer.Property.METHOD, "text");
				}
	
				evaluator.run( serializer );
	
			} finally {
				
				writer.close();
				
			}		
		
		} catch( Exception exc ) {
			context.addError( exc.getMessage() );
			return ERROR;
		}

/*		
		Configuration config = new Configuration();
		StaticQueryContext staticContext = 
		        new StaticQueryContext( config );
		
		try {
			XQueryExpression exp = 
			        staticContext.compileQuery( new FileReader( xquery ) );
			
			DynamicQueryContext dynamicContext = 
			    new DynamicQueryContext( config );
			dynamicContext.setContextNode(
					staticContext.buildDocument(
							new SAXSource(
									new org.xml.sax.InputSource( new FileReader( data ) ) )				
					) 
			);

			FileWriter buffer = new FileWriter( res );
			StreamResult result = new StreamResult( buffer );			
			Properties props = new Properties();

			if ( "xml".equals( context.getParam( XQueryUI.OUTPUT ) ) ) {
				props.setProperty(OutputKeys.METHOD, "xml");
				props.setProperty(OutputKeys.INDENT, "yes");
			} else {
				props.setProperty(OutputKeys.METHOD, "text");
			}

			exp.run( 
					dynamicContext, 
					result, 
					props );
			
		} catch ( XPathException e ) {
			context.addError( e.getMessageAndLocation() );
			return ERROR;
		} catch ( FileNotFoundException e ) {
			context.addError( e.getMessage() );
			return ERROR;
		} catch ( IOException e ) {
			context.addError( e.getMessage() );
			return ERROR;
		} */

		return OK;
	}

}
