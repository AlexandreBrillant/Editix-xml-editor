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

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.AbstractAction;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;

import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

public class OpenHTTPAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		OpenHTTPPanel pane = new OpenHTTPPanel();

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"HTTP import",
				"HTTP import",
				"Choose a GET or POST mode for getting our document",
				null,
				pane ) == 
					DialogManager.OK_ID ) {
			try {
				Element config = pane.getConfiguration();
				String method = config.getAttribute( "method" );
				String url = config.getAttribute( "url" );
				
				StringBuffer sbParams = new StringBuffer();
				
				NodeList nl = config.getElementsByTagName( "parameters" );
				if ( nl.getLength() > 0 ) {
					Element parameters = ( Element )nl.item( 0 );
					nl = parameters.getChildNodes();
					for ( int i = 0; i < nl.getLength(); i++ ) {
						if ( nl.item( i ) instanceof Element ) {
							Element param = ( Element )nl.item( i );
							if ( sbParams.length() > 0 ) {
								sbParams.append( "&" );
							}
							sbParams.append( URLEncoder.encode( param.getAttribute( "name" ) ) );
							sbParams.append( "=" );
							sbParams.append( URLEncoder.encode( param.getAttribute( "value" ) ) );
						}
					}
				}
				
				if ( "GET".equalsIgnoreCase( method ) ) {
					if ( url.indexOf( "?" ) == -1 ) {
						url = url + "?";
					}
					url = url + sbParams.toString();
				}
				
				URL urlObj = new URL( url );
				URLConnection connection = urlObj.openConnection();
		        connection.setDoOutput( "POST".equalsIgnoreCase( method ) );
		        // HTTP Header
				nl = config.getElementsByTagName( "headers" );
				if ( nl.getLength() > 0 ) {
					Element parameters = ( Element )nl.item( 0 );
					nl = parameters.getChildNodes();
					for ( int i = 0; i < nl.getLength(); i++ ) {
						if ( nl.item( i ) instanceof Element ) {
							Element param = ( Element )nl.item( i );
							connection.setRequestProperty( param.getAttribute( "name" ), param.getAttribute( "value" ) );
						}
					}
				}
				if ( "POST".equalsIgnoreCase( method ) && sbParams.length() > 0 ) {
					  connection.getOutputStream().write( sbParams.toString().getBytes() );
				}
				
				 InputStream input = connection.getInputStream();			                 
			     XMLFileData xfd = XMLToolkit.getContentFromInputStream( input, null );				
			
			     OpenAction.openFile( config.getAttribute( "openAs" ), false, null, null, null, xfd );
				
			} catch( Throwable exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't connect :" + exc.getMessage() );
			}
			
		}

	}

}
