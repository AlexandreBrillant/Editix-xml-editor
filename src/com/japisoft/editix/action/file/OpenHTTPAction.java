package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.editix.action.file.imp.HTMLImport;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.p3.Manager;

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
			
		/*
			
		    public XMLFileData connect( String encodingMode ) throws Throwable {
		        String url = ( String )cbURL.getSelectedItem();
		        // Add parameters for GET usage
		        TableModel tm = tbParams.getModel();
		        URL urlObj = null;
		        
		        if ( rbPost.isSelected() ) {
		            urlObj = new URL( url );
		        }
		        String params = "";
		        for ( int i = 0; i < tm.getRowCount(); i++ ) {
		            String param = ( String )tm.getValueAt( i, 0 );
		            String value = ( String )tm.getValueAt( i, 1 );
		            if ( param != null &&
		                    value != null &&
		                    !"".equals( param ) &&
		                    !"".equals( value ) ) {
		                if ( rbGet.isSelected() ) {
		                    if ( url.indexOf( "?" ) == -1 )
		                        url += "?";
		                }
		                if ( !"".equals( params ) )
		                    params += "&";
		                params += URLEncoder.encode( param ) + "=" + URLEncoder.encode( value );
		            }
		        }
		        
		        if ( rbGet.isSelected() ) {
		            urlObj = new URL( url + params );
		        }

		        URLConnection connection = urlObj.openConnection();
		        connection.setDoOutput( rbPost.isSelected() );
		        connection.setRequestProperty( "user-agent", "Mozilla" );
		        if ( rbPost.isSelected() ) {
		            if ( !params.equals( "" ) )
		                connection.getOutputStream().write( params.getBytes() );
		        }
		        InputStream input = connection.getInputStream();
		        
		        if ( cbConvertHTML.isSelected() ) {
		        	byte[] data = HTMLImport.convertHTMLInputStream(
		        			input );
		        	if ( data != null ) {
		        		input = new ByteArrayInputStream( data );
		        	}
		        }
		              
		        XMLFileData xfd = XMLToolkit.getContentFromInputStream( input, encodingMode );

		        
		        return xfd;
		    }
						
			
		}
		
*/		
		

/*			
		//£££
		ImportURLUI ui = new ImportURLUI();
		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"HTTP import",
				"HTTP import",
				"Choose a GET or POST mode for getting our document",
				null,
				ui ) == 
					DialogManager.OK_ID ) {
			
			try {
				XMLFileData data = ui.connect( Toolkit.getCurrentFileEncoding() );
				OpenAction.openFile( 
						"XML", 
						false, 
						Toolkit.getCurrentFileEncoding(), 
						ui.getURL(), 
						null, 
						data );				
				
			} catch ( Throwable e1 ) {
				EditixFactory.buildAndShowErrorDialog( "Can't show " + e1.getMessage() );
			}
		}

		//££
*/

	}

}
