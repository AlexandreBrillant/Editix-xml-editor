package com.japisoft.xflows.task.xquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;

import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

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
		}

		return OK;
	}

}
