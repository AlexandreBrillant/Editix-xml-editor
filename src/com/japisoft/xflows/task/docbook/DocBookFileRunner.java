// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xflows.task.docbook;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xflows.task.BasicTaskContext;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskElementFactory;
import com.japisoft.xflows.task.TaskRunner;
import com.japisoft.xflows.task.fop.FOPFileRunner;
import com.japisoft.xflows.task.fop.FOPUI;
import com.japisoft.xflows.task.xslt.XSLTFileRunner;
import com.japisoft.xflows.task.xslt.XSLTUI;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DocBookFileRunner implements TaskRunner {

	private static URL checkForResource( String resource ) {
		URL u = ClassLoader.getSystemResource( resource );
		if ( u != null )
			return u;
		File f = new File( resource );
		if ( f.exists() ) {
			try {
				return f.toURL();
			} catch( MalformedURLException exc ) {
			}
		}
		throw new RuntimeException( "Can't find " + resource );
	}

	public boolean run( TaskContext context ) {
		String renderer = context.getParam( DocBookUI.OUTPUT_TYPE );
		File output = context.getCurrentTargetFile();
		
		String xsltFile = "";
		URL url = null;
		boolean foMode = false;

		if ( "HTML".equals( renderer ) ) {
			url = checkForResource( Preferences.getPreference( "docbook", "htmlstylesheet", "docbook/html/docbook.xsl" ) );
			if ( url != null )
				xsltFile = 				
					url.toString();
			else
				return false;			
		} else
		if ( "HTML Help".equals( renderer ) ) {
			url = checkForResource( Preferences.getPreference( "docbook", "htmlHelperstylesheet", "docbook/htmlhelp/htmlhelp.xsl" ) );
			if ( url != null )
				xsltFile = url.toString();
			else
				return false;
		} else
		if ( "XHTML".equals( renderer ) ) {	
			url = checkForResource( Preferences.getPreference( "docbook", "xhtmlstylesheet", "docbook/xhtml/docbook.xsl" ) );
			if ( url != null )
				xsltFile = url.toString();
			else
				return false;
		} else
		if ( "Java Help".equals( renderer ) ) {
			url = checkForResource( Preferences.getPreference( "docbook", "javaHelpstylesheet", "docbook/javahelp/javahelp.xsl" ) );
			if ( url != null )
				xsltFile = url.toString();
			else
				return false;
		} else
		if ( "FO".equals( renderer ) ) {
			
			url = checkForResource( Preferences.getPreference( "docbook", "fostylesheet", "docbook/fo/docbook.xsl" ) );
			if ( url != null )
				xsltFile = url.toString();
			else
				return false;
			
		} else {
			
			url = checkForResource( Preferences.getPreference( "docbook", "fostylesheet", "docbook/fo/docbook.xsl" ) );
			if ( url != null )
				xsltFile = url.toString();
			else
				return false;

			output = new File( "xflows.tmp" );

			// Faire d'abord la conversion en FO
			foMode = true;
		}

		TaskRunner xsltRunner = new XSLTFileRunner();
		TaskContext newContext = new BasicTaskContext( context );
		newContext.setCurrentSourceFile( context.getCurrentSourceFile() );
		newContext.setCurrentTargetFile( output );

		if ( xsltFile.startsWith( "file:" ) )
			xsltFile = xsltFile.substring( 5 );

		newContext.getParams().setParam( XSLTUI.STYLESHEET, xsltFile );
		newContext.setQuietInfoMode( true );

		context.addInfo( "Transforming " + context.getCurrentSourceFile() );

		boolean ok = ( xsltRunner.run( newContext ) == OK );

		if ( foMode && ok ) {

			TaskRunner fopRunner = new FOPFileRunner();
			newContext = new BasicTaskContext( context );
			newContext.setCurrentSourceFile( output );
			newContext.setCurrentTargetFile( context.getCurrentTargetFile() );
			newContext.getParams().setParam( FOPUI.OUTPUT_TYPE, renderer );
			newContext.setQuietInfoMode( true );

			ok = ( fopRunner.run( newContext ) == OK );
			output.delete();
			return ok;

		} else {
			context.addError( "Can't use the following stylesheet " + xsltFile );
			return ERROR;
		}

	}

}

