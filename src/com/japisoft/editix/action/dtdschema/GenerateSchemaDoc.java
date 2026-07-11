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

package com.japisoft.editix.action.dtdschema;

import java.awt.Color;

import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view2.DesignerViewImpl;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.XSDFactoryImpl;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.ui.toolkit.FileManager;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

public class GenerateSchemaDoc extends AbstractAction {

	private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

	public static final String DEFAULT_CSS = "body { font-family: Arial, Helvetica, sans-serif;background-color : white; 	}\n.doc { 		width : 80%; 		background-color:#DDD; 		border-radius:4px; 		font-style:italic; 		padding:5px; 	}\n.name { 		font-weight : bolder; 		color : #0AA; 		font-size:1.0em; 	}\nh1,h2 { 		font-size:1.2em; 	}\ntable { 		border:1px solid #999; 	}"; 
	
	public static String getCommonHeader( XMLContainer container ) {

		String defaultCss = DEFAULT_CSS;
		
		String style = "schema.css";
		if ( UIManager.get( "editix.xsd.cssstyle" ) != null )
			style = (String)( UIManager.get( "editix.xsd.cssstyle" ) );

		if ( UIManager.get( "editix.xsd.defaultcss" ) != null ) {
			defaultCss = ( (String)UIManager.get( "editix.xsd.defaultcss" ) );
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append( "<head>" );
		sb.append( "<title>" );
		sb.append( container.getDocumentInfo().getCurrentDocumentFileName() );
		sb.append( "</title>" );
		sb.append( "<style type='text/css'>" );	
		sb.append( FileManager.getFileContent( EditixApplicationModel.getAppFile( style ), defaultCss ) );
		sb.append( "</style>" );
		sb.append( "</style>" );
		sb.append( "</head>" );
		return sb.toString();
	}

	public static String getCommonFooter( XMLContainer container ) {
		return "<hr />\n<p style='text-align:center'><small>Generated with <a href='https://www.editix.com'>EditiX XML Editor</a> at " + new Date() + "</small></p>";
	}

	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Please save your Schema before" );
			return;
		}

		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		// Generate the new schema
		panel.prepareToSave();
		
		File f = FileManager.getSelectedDirectory( new File( container.getCurrentDocumentLocation() ), "HTML output directory" ); 

		if ( f != null ) {
			
			DefaultValidator dv = new DefaultValidator( true );
			if ( dv.validate( container, false ) != Validator.OK ) {
				EditixFactory.buildAndShowErrorDialog( "Error found inside the source\nPlease fix it" );
				return;
			}

			Document doc = dv.getDocument();
			Element root = doc.getDocumentElement();
			if ( root == null )
				return;

			try {

				f = new File( f, "index.html" );
				
				PrintWriter pw = 
					new PrintWriter(
							new FileWriter( f ) );
				try {

					pw.println( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" );
					pw.println( "<html>" );					
					pw.println( getCommonHeader( container ) );
					pw.println( "<body>" );
					pw.println( "<h1>" );
					pw.println( "Schema <span class='name'>" + container.getDocumentInfo().getCurrentDocumentFileName() );
					pw.println( "</span></h1>" );

					String docStr = getDocumentation( root );
					
					if ( docStr != null && docStr.length() > 0 ) {
						pw.println( "<p class='doc'>" );
						pw.println(  docStr );
						pw.println( "</p>");
					}
					
					NodeList nl = root.getChildNodes();
					
					pw.println( getTM( nl ) );
					
					boolean first = false;
					for ( int i = 0;i < nl.getLength(); i++ ) {

						Node n = ( nl.item( i ) );
						if ( n instanceof Element ) {
							
							Element ee = ( Element )n;
							if ( !"annotation".equals( ee.getLocalName() ) ) {
								//if ( !first )
									process( f, ee, pw );
								first = true;
							}
							
						}
					}
					
					pw.println( getCommonFooter( container ) );
					
					pw.println( "</body>" );
					pw.println( "</html>" );

				} finally {
					pw.close();
				}

				BrowserCaller.displayURL( f.toString() );

			} catch (IOException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't create documentation : " + e1.getMessage() );
			}			
		}
		//
	}
	
	private String getTM( NodeList nl ) {
		ArrayList l = new ArrayList();
		StringBuffer sb = new StringBuffer();
			
		for ( int i = 0;i < nl.getLength(); i++ ) {
			Node n = ( nl.item( i ) );
			if ( n instanceof Element ) {
				
				Element ee = ( Element )n;
				if ( !"annotation".equals( ee.getLocalName() ) ) {

					l.add( getAnchor( ee ) );
					
				}
			}
		}

		Collections.sort( l );

		String oldType = null;
		
		for ( int i = 0; i < l.size(); i++ ) {
			String anchor = ( String )l.get( i );
			int ii = anchor.indexOf( '_' );
			String type = anchor.substring( 0, ii );
			String name = anchor.substring( ii + 1 );
			
			if ( !type.equals( oldType) ) {
				sb.append( "<h3>" ).append( type ).append( "</h3>" );
			} else
				sb.append( " | " );

			sb.append( "<a href='#" ).append( anchor ).append( "'>" ).append( name ).append( "</a>" );
			
			oldType = type;
		}

		return sb.toString();
	}

	private String getAnchor( Element e ) {
		String name = e.getLocalName();
		return name + "_" + e.getAttribute( "name" );
	}

	private void process( 
			File f, 
			Element e, 
			PrintWriter pw ) {

		String name = e.getLocalName();

		pw.println( "<hr color='gray' with='50%'>" );
		pw.println( "<a name='" + getAnchor( e ) + "'>" );
		pw.println( "</a>" );
		pw.println( "<h2>" );
		pw.println( Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 ) );
		
		boolean externalType = false;
		String externalTypeLink = null;
		
		if ( "element".equals( name ) ) {
			Element ee = SchemaHelper.getRef( 
					e.getOwnerDocument().getDocumentElement(),
					"complexType",
					e.getAttribute( "type" ) );
			
			if ( ee == null ) {
				ee = SchemaHelper.getRef( 
						e.getOwnerDocument().getDocumentElement(),
						"simpleType",
						e.getAttribute( "type" ) 
				);
			}

			if ( ee != null ) {
				externalType = true;
				pw.println( "<a class='name' href='#" + ( externalTypeLink = getAnchor( ee ) ) + "'>" );
			}
		}

		pw.println( " <span class='name'>" );
		pw.println( e.getAttribute( "name" ) );
		
		pw.println( "</span>" );

		if ( externalType )
			pw.println( "</a>" );
		
		pw.println( "</h2>" );
		
		String docStr = getDocumentation( e );
		
		if ( docStr != null && docStr.length() > 0 ) {				
			pw.println( "<p class='doc'>" );
			pw.println( docStr );
			pw.println( "</p>" );
		}

		String imgName = name + e.getAttribute( "name" ) + ".png";

		try {
			// Display the image
			if ( generateImage( e, new File( f.getParentFile(), imgName ) ) ) {
				
				if ( externalType ) {
					pw.println( "<a href='#" + externalTypeLink + "'>" );	
				}
				pw.println( "<img border='0' src='" + imgName + "'" + " alt='" + name + " " + e.getAttribute( "name" ) + "'>" );
				if ( externalType ) {
					pw.println( "</a>" );
				}
			}
		} catch( Throwable th ) {
			ApplicationModel.debug( th );
		}

		// Show all elements inside
		NodeList nl = e.getElementsByTagNameNS( SCHEMA_NS, "element" );
		if ( nl.getLength() > 0 ) {

			pw.println( "<h3>Elements</h3>" );
			pw.println( "<table>" );
			pw.println( "<tr><th>Element</th><th>Type</th><th>Documentation</th></tr>" );
			
			for ( int i = 0; i < nl.getLength(); i++ ) {
				Element ee = ( Element )nl.item( i );
				Element eee = ee;
				
				pw.println( "<tr>" );

				// NAME
				pw.println( "<td>" );
				if ( ee.hasAttribute( "name" ) ) {
					pw.println( ee.getAttribute( "name" ) );
				} else
				if ( ee.hasAttribute( "ref" ) ) {
					String ref = ee.getAttribute( "ref" );
					
					eee = SchemaHelper.getRef(
							ee.getOwnerDocument().getDocumentElement(),
							"element",
							ee.getAttribute( "ref" ) );

					if ( eee != null ) {
						pw.println( "<a href='#" + getAnchor( eee) + "'>" );
					}
					
					int ii = ref.indexOf( ':' );
					if ( ii > -1 ) {
						pw.println( ref.substring( ii + 1 ) );
					} else
						pw.println( ref );
					
					if ( eee != null ) {
						pw.println( "</a>" );
					}
				}
				pw.println( "</td>" );

				// TYPE
				pw.println( "<td>" );
								
				if ( eee == null ) {
					pw.println( "?" );
				} else {
					
					Element eType = SchemaHelper.getRef(
							eee.getOwnerDocument().getDocumentElement(),
							"complexType",
							eee.getAttribute( "type" ) ); 
					
					if ( eType != null ) {
						pw.println( "<a href='#" +
								getAnchor( eType ) + "'>" + eType.getAttribute( "name" ) + "</a>" );
					} else {
						if ( eee.hasAttribute( "type" ) )
							pw.println( eee.getAttribute( "type" ) );
						else
							pw.println( "Local type" );
					}
					
				}

				pw.println( "</td>" );

				pw.println( "<td class='doc'>" );

				String comment = getDocumentation( eee == null ? ee : eee );

				// String comment = SchemaHelper.getDocumentation(  );
				if ( comment != null )
					pw.println( comment );
				else
					pw.println( "No documentation" );
				pw.print( "</td>" );
				
				pw.println( "</tr>" );
			}

			pw.println( "</table>" );

		}

		// Show all attributes inside
		nl = e.getElementsByTagNameNS( SCHEMA_NS, "attribute" );
		if ( nl.getLength() > 0 ) {

			pw.println( "<h3>Attributes</h3>" );
			pw.println( "<table>" );
			pw.println( "<tr><th>Attribute</th><th>Type</th><th>Documentation</th></tr>" );

			for ( int i = 0; i < nl.getLength(); i++ ) {
				Element ee = ( Element )nl.item( i );
				Element eee = ee;
				
				pw.println( "<tr>" );

				// NAME
				pw.println( "<td>" );
				if ( ee.hasAttribute( "name" ) ) {
					pw.println( ee.getAttribute( "name" ) );
				} else
				if ( ee.hasAttribute( "ref" ) ) {
					String ref = ee.getAttribute( "ref" );

					eee = SchemaHelper.getRef(
							ee.getOwnerDocument().getDocumentElement(),
							"attribute",
							ee.getAttribute( "ref" ) );

					if ( eee != null ) {
						pw.println( "<a href='#" + getAnchor( eee) + "'>" );
					}

					int ii = ref.indexOf( ':' );
					if ( ii > -1 ) {
						pw.println( ref.substring( ii + 1 ) );
					} else
						pw.println( ref );
					
					if ( eee != null ) {
						pw.println( "</a>" );
					}
				}
				pw.println( "</td>" );

				// TYPE
				pw.println( "<td>" );
								
				if ( eee == null ) {
					pw.println( "?" );
				} else {
					
					Element eType = SchemaHelper.getRef(
							eee.getOwnerDocument().getDocumentElement(),
							"simpleType",
							eee.getAttribute( "type" ) ); 

					if ( eType != null ) {
						pw.println( "<a href='#" +
								getAnchor( eType ) + "'>" + eType.getAttribute( "name" ) + "</a>" );
					} else {
						if ( eee.hasAttribute( "type" ) )
							pw.println( eee.getAttribute( "type" ) );
						else
							pw.println( "Local type" );
					}
					
				}

				pw.println( "</td>" );

				pw.println( "<td class='doc'>" );

				String comment = SchemaHelper.getDocumentation( eee == null ? ee : eee );
				if ( comment != null )
					pw.println( comment );
				else
					pw.println( "No documentation" );
				pw.print( "</td>" );
				
				pw.println( "</tr>" );
			}

			pw.println( "</table>" );

		}
				
	}

	public static boolean generateImage( Element e, File location ) throws IOException {
		return generateImage( e, location, "PNG" );	
	}

	public static boolean generateImage( Element e, File location, String imageType ) throws IOException {

		DesignerViewImpl d = new DesignerViewImpl( new XSDFactoryImpl() );
		if ( UIManager.getColor( "editix.xsd.background" ) != null )		
			d.setBackground( UIManager.getColor( "editix.xsd.background" ) );

		d.init( e );
		JFrame f = new JFrame();
		f.setLocation( -100, -100 );
		f.setSize( 0, 0 );
		f.getContentPane().add( new JScrollPane( d ) );
		f.setVisible( true );
		
		d.paintNode( 0, 0, d.getNode(), (Graphics2D)f.getGraphics() );
		
		BufferedImage img = new BufferedImage(
				d.getMaxX(),
				d.getMaxY() - d.getMinY(),
				BufferedImage.TYPE_INT_RGB );

		Graphics2D gc = img.createGraphics();

		if ( UIManager.getColor( "editix.xsd.background" ) != null ) {
			gc.setColor( UIManager.getColor( "editix.xsd.background" ) );
		} else
			gc.setColor( Color.WHITE );
		
		gc.fillRect( 0, 0, d.getMaxX(), d.getMaxY() * 2 );

		d.paintNode( 0, Math.abs( d.getMinY() ), d.getNode(), gc );
		
		try {				
			ImageIO.write( img, imageType, location  );
		} catch (IOException e1) {
			e1.printStackTrace();
        }

		f.setVisible( false );
		f.dispose();
		
		return true;
		
	}

	private String getDocumentation( Element e ) {
		
		if ( e == null )
			return "";
		
		StringBuffer sb = new StringBuffer();
		
		NodeList nl = e.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			
			if ( nl.item( i ) instanceof Element ) {

				Element ee = ( Element )nl.item( i );
				if ( "annotation".equals( ee.getLocalName() ) ) {
					
					NodeList nll = ee.getChildNodes();
					for ( int j = 0; j < nll.getLength(); j++ ) {

						Node n2 = nll.item( j );
						if ( n2 instanceof Element ) {

							if ( sb.length() > 0 ) {
								sb.append( "<br>" );
							}
							
							Element eee = ( Element )n2;
							
							if ( eee.hasAttribute( "source" ) ) {
								sb.append( "<strong>" + eee.getAttribute( "source" ) + ":</strong> " );
							}
							
							if ( "documentation".equals( eee.getLocalName() ) ) {
								
								NodeList nlll = eee.getChildNodes();
								for ( int k = 0; k < nlll.getLength(); k++ ) {

									if ( k > 0 ) {
										sb.append( "<br />" );
									}
									Node n3 = nlll.item( k );
									sb.append( escapeTextContent( n3.getTextContent() ) );

								}

							}
							
						}
						
					}

				}

			}

		}

		if ( "simpleType".equals( 
				e.getLocalName() ) ) {
			NodeList nlTmp = e.getElementsByTagNameNS( SCHEMA_NS, "enumeration" );
			for ( int i = 0; i < nlTmp.getLength(); i++ ) {
				Element ee = ( Element )nlTmp.item( i );
				sb.append( "<br/>- Enumeration " + ee.getAttribute( "value" ) + " : " ).append( getDocumentation( ee ) );
			}
		}

		/*
		if ( sb.length() == 0 )
			sb.append( "No documentation" );
		*/
		
		return sb.toString().trim();
	}

	private String escapeTextContent( String text ) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < text.length(); i++ ) {
			if ( text.charAt( i ) == '<' )
				sb.append( "&lt;" );
			else
			if ( text.charAt( i ) == '>' )
				sb.append( "&gt;" );
			else
			if ( text.charAt( i ) == '\n' )
				sb.append( "<br>" );
			else
				sb.append( text.charAt( i ) );
		}
		return sb.toString();
	}

}
