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

package com.japisoft.xmlform.designer.actions.file;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.designer.XmlFormModel;
import com.japisoft.xmlform.designer.actions.CommonAction;

public class SaveAction extends CommonAction {

	public void actionPerformed2(ActionEvent e) {

		String uri = XmlFormModel.CURRENT_DOCUMENT;

		if ( uri == null ) {
			File f = FileManager.getSelectedFile( 
					false, 
					OpenAction.EXT, 
					"XML form description" );
			if ( f != null )
				uri = f.toString();
		}
		if ( uri!= null ) {
			saveTo( uri );
		}

	}

	public void saveTo( String uri ) {
		try {
			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			Document d = 
				factory.newDocumentBuilder().newDocument();
			frame.save( d );

			Transformer t = 
				TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
			t.setOutputProperty( OutputKeys.INDENT, "yes" );
			t.setOutputProperty( OutputKeys.METHOD, "xml" );

			ByteArrayOutputStream out = 
				new ByteArrayOutputStream();

			StreamResult res = new StreamResult( out );
			t.transform( 
				new DOMSource( d ), 
				res );

			if ( uri.indexOf( "://" ) > -1 ) {

				URL u = new URL( uri );
				URLConnection c = 
					u.openConnection();
				c.setDoOutput( true );
				c.setUseCaches( false );

				String s = new String( out.toByteArray(), "UTF-8" );
				DataOutputStream dout = new DataOutputStream( c.getOutputStream() );
				try {
					dout.writeBytes( "file=" + URLEncoder.encode( s ) );
				} finally {
					dout.close();
				}
				
				InputStream input = 
					c.getInputStream();

				int cc;
				while ( ( cc = input.read() ) != -1 ) {}

			} else {
				FileOutputStream output = new FileOutputStream( uri );
				try {
					output.write( out.toByteArray() );
				} finally {
					output.close();
				}
			}

			XmlFormModel.CURRENT_DOCUMENT = uri;

		} catch ( Exception e1 ) {
			UIToolkit.dispatchError( "Can't save : " + e1.getMessage() );
		}		
	}
	
}

