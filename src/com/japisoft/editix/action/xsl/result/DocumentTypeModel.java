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

package com.japisoft.editix.action.xsl.result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.toolkit.FileToolkit;

public class DocumentTypeModel {

	private static final String XSLTMODELS_XML = "xsltmodels.xml";

	private DocumentTypeModel() {
		read();
	}
	
	private static DocumentTypeModel instance = null;
	
	public static DocumentTypeModel instance() {
		if ( instance == null )
			instance = new DocumentTypeModel();
		return instance;
	}
	
	public void update() {
		models = null;
		read();
	}
	
	public InputStream getModel() {
		File f = getUserPath();
		InputStream input = null;
		
		if ( f.exists() ) {
			try {
				input = new FileInputStream( f );
			} catch( IOException exc ) {
				System.out.println( "Can't open " + f + " : " + exc.getMessage() );
				System.out.println( "Use the default one" );
			}
		}
		
		if ( input == null )
			input = DocumentTypeModel.class.getResourceAsStream( XSLTMODELS_XML );

		return input;
	}
	
	public File getUserPath() {
		return EditixApplicationModel.getAppFile( XSLTMODELS_XML );
	}
	
	private void read() {
				
		InputStream input = getModel();
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( input );
			NodeList nl = doc.getElementsByTagName( "model" );
			// <model type="docx" title="Word document" source="/com/japisoft/editix/action/xsl/result/model.docx" target="/word/document.xml"/>
			for ( int i = 0; i < nl.getLength(); i++ ) {
				Element model = ( Element )nl.item( i );
				addModel( model );
			}

		} catch( SAXException exc ) {
			System.out.println( "Can't parse " + XSLTMODELS_XML + " : " + exc.getMessage() );
		} catch( IOException exc ) {
			System.out.println( "Can't read " + XSLTMODELS_XML + " : " + exc.getMessage() );
		} catch( ParserConfigurationException exc ) {
			exc.printStackTrace();
		}
	}
	
	private List<Model> models = null;
	
	private void addModel( Element model ) {
		if ( models == null )
			models = new ArrayList<Model>();
		models.add( new Model( model ) );
	}
	
	public int size() { 
		if ( models == null )
			return 0;
		return models.size();
	}
	
	public String fileExt( int index ) {
		return models.get( index ).getType();
	}
	
	public OutputStream open( String fileRes ) throws Exception {
		String ext = FileToolkit.fileExt( fileRes );
		if ( models != null ) {
			for ( Model m : models ) {
				if ( m.match( ext ) ) {
					return m.open();
				}
			}
		}
		return null;
	}
	
	public void close( String fileRes ) throws Exception {
		if ( models != null ) {
			for ( Model m : models ) {
				if ( m.isOpen() ) {
					m.close( fileRes );
				}
			}
		}
	}
	
	public boolean processed( String fileExt ) {
		if ( models != null )
			for ( Model m : models ) {
				if ( m.match( fileExt ) )
					return true;
			}
		return false;
	}
}


class Model {
	private String type;
	private String title;
	private String source;
	private String target;
	
	Model( Element model ) {
		this.type = model.getAttribute( "type" ).toLowerCase();
		this.title = model.getAttribute( "title" );
		this.source = model.getAttribute( "source" );
		this.target = model.getAttribute( "target" );
	}
	
	public String getType() {
		return type;
	}
	
	private String getTitle() {
		return title;
	}
	
	public boolean match( String ext ) {
		return type.equalsIgnoreCase( ext );
	}
	
	private List<ZipEntryData> current = null;
	private boolean open = false;
	
	public OutputStream open() throws Exception {
		if ( current != null )	// Bug ?
			current = null;
		
		File f = new File( this.source );
		ZipInputStream input = null;
		if ( f.exists() ) {
			input = new ZipInputStream( new FileInputStream( f ) );
		} else {
			input = new ZipInputStream( getClass().getResourceAsStream( this.source ) );
		}
		
		ZipEntry ze = input.getNextEntry();
		OutputStream output = null;
		
		while ( ze != null ) {

			if ( current == null )
				current = new ArrayList<ZipEntryData>();
			
			ZipEntryData zed = null;
			current.add( zed = new ZipEntryData( ze, input ) );
			
			if ( this.target.equalsIgnoreCase( ze.getName() ) || 
					this.target.equalsIgnoreCase( "/" + ze.getName() ) ) {
				output = zed.getOutputStream();
			}
			
			ze = input.getNextEntry();
	
		}

		open = true;
		return output;
	}
	
	public boolean isOpen() { return open; }
	
	public void close( String res ) throws Exception {
		ZipOutputStream output = new ZipOutputStream( new FileOutputStream( res ) );
		try {
			if ( current != null ) {
				for ( ZipEntryData zed : current ) {
					zed.write( output );
				}
			}
		} finally {
			output.close();
			current = null;
		}
	}

}
		
class ZipEntryData {
	
	private ZipEntry ze = null;
	private ByteArrayOutputStream data = null;
	
	public ZipEntryData( ZipEntry ze, InputStream input ) throws IOException {
		this.ze = ze;
		byte[] buffer = new byte[1024];
		data = new ByteArrayOutputStream();
		int c;
		while ( ( c = input.read( buffer ) ) > 0 ) {
			data.write( buffer, 0, c );
		}
	}
	
	private boolean updated = false;

	// For writting, reset the current one
	public OutputStream getOutputStream() {
		data.reset();
		updated = true;
		return data;
	}
	
	public void debug() {
		System.out.println( "DEBUG XSLT OUTPUT : " + new String( data.toByteArray() ));
	}
	
	public boolean isUpdated() { return updated; }
	
	public void write( ZipOutputStream output ) throws Exception {
		output.putNextEntry( new ZipEntry( ze.getName() ) );
		data.writeTo( output );
		output.flush();
		output.closeEntry();
	}
	
}
	

