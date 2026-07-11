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

package com.japisoft.editix.action.file.export;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.SelectableEncoding;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xmlpad.IXMLPanel;

public class JSONExportAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		IXMLPanel container = 
			EditixFrame.THIS.getSelectedContainer();

		if ( container == null || container.getMainContainer() == null ) {
			EditixFactory.buildAndShowWarningDialog( "No selected XML document ?" );
			return;
		}

		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "JSON file (*.json, *.jso)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".json" ) || tmp.endsWith( ".jso" );
				}
				return true;
			}
		});

		try {
			if ( fc.showSaveDialog( EditixFrame.THIS ) == JOptionPane.OK_OPTION ) {
				String xmlContent = container.getMainContainer().getText();
				
				// JSONObject jo = XML.toJSONObject( xmlContent );
				// String content = jo.toString(1);
				String content = convertToJSON( xmlContent );
				if ( content == null ) {
					throw new JSONException( "Can't convert this XML to JSON" );
				}
				
				String encoding = null;
				if ( fc instanceof SelectableEncoding ) {
					encoding = ( ( SelectableEncoding )fc ).getSelectedEncoding();
					if ( "AUTOMATIC".equalsIgnoreCase( encoding ) ) {
						encoding = null;
					}
				}

				FileToolkit.writeFile(
					fc.getSelectedFile(),
					content,
					encoding
				);

				if ( Preferences.getPreference( "file", "openFileExportJSON", true ) )
					OpenAction.openFile( "JSON", false, fc.getSelectedFile(), encoding );
				else
					EditixFactory.buildAndShowInformationDialog( "Exported" );
			}
		} catch( JSONException ex ) {
			EditixFactory.buildAndShowErrorDialog( ex.getMessage() );
		} catch( IOException ex ) {
			EditixFactory.buildAndShowErrorDialog( ex.getMessage() );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( exc.getMessage() );
		}

	}
	
	public static String convertToJSON( String xml ) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( 
				new InputSource( 
						new StringReader( xml ) ) );
		Element root = doc.getDocumentElement();
		JSONObject jo = new JSONObject();

		List<Node> processed = new ArrayList<Node>();		
		convertTOJSON2( root, jo, processed );
		return jo.toString( 1 );
	}
	
	private static List<Element> getBrothers( NodeList nl, Element n ) {
		ArrayList<Element> l = new ArrayList<Element>();
		String name = n.getNodeName();
		for ( int i = 0;i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( name.equals( e.getNodeName() ) ) {
					l.add( e );
				}
			}
		}
		return l;
	}

	private static void convertTOJSON2( Element node, JSONObject parent, List<Node> processed ) throws JSONException {
	
		if ( processed.contains( node ) )
			return;
	
		processed.add( node );
		
		String key = node.getNodeName();
		
		if ( node.hasAttributes() ) {
			
			NamedNodeMap nnm = node.getAttributes();
			
			JSONObject attributes = new JSONObject();
			parent.put( "ed:attributes", attributes );
			
			for ( int j = 0;j < nnm.getLength(); j++ ) {
				
				Node nn = nnm.item(j);
				attributes.put( nn.getNodeName(), nn.getNodeValue() );
				
			}
			
		}
			
		String text = "";
		NodeList nl = node.getChildNodes();
		JSONArray array = null;
		
		for ( int i = 0; i < nl.getLength(); i++ ) {

			Node n = nl.item( i );
			
			if ( processed.contains( n ) )
				continue;
			
			if ( n instanceof Element ) {
				
				if  ( array == null ) {
					array = new JSONArray();	
					parent.put( "ed:children", array );						
				}
				
				Element e = ( Element )n;
				JSONObject obj = new JSONObject();										
				array.put( obj );
				obj.put( e.getNodeName(), obj = new JSONObject() );

				convertTOJSON2( e, obj, processed );					
				
			} else
			if ( n instanceof Text ) {
				
				String tmp = n.getNodeValue().trim();
				if ( !"".equals( tmp ) )
					text+= n.getTextContent();
				
			}
			
		}
		
		if ( !"".equals( text ) ) {
			parent.put( "ed:text", text );
		}

	}

	private static void convertToJSON( Element node, JSONObject obj ) throws JSONException {
		NodeList nl = node.getChildNodes();
		
		HashMap<String,Boolean> processed = new HashMap<String,Boolean>();
		
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
						
			if ( n instanceof Element ) {
				if ( processed.containsKey( n.getNodeName() ) )
					continue;			
				
				processed.put( n.getNodeName(), Boolean.TRUE );
				
				List<Element> brothers = getBrothers( nl, ( Element )n );
				JSONArray array = null;
				if ( brothers.size() > 1 ) {
					array = new JSONArray();
					obj.put( n.getNodeName(), array );
				}
				
				JSONObject child = null;
				
				for( Element e : brothers ) {
					
					if ( array != null ) {
						child = new JSONObject();
						array.put( child );
					} else {

						child = new JSONObject();
						obj.put( n.getNodeName(), child );						
					}
					
					if ( e.hasAttributes() ) {
					
						NamedNodeMap nnm = e.getAttributes();
						for ( int j = 0;j < nnm.getLength(); j++ ) {
							Node nn = nnm.item(j);
							child.put( nn.getNodeName(), nn.getNodeValue() );
						}
						
					} else 
						if ( !isEmptyOrText( e ) ) {

							if ( array == null ) {
								
								child.put( e.getNodeName(), e.getTextContent() );
								
							} else {
								
								child.put( "_text", e.getTextContent() );
								
							}
							
						} else
					
							convertToJSON( e, child );
					
				}
				
			}
		}
		
	}

	private static boolean isEmptyOrText( Element e ) {
		if ( !e.hasChildNodes() )
			return true;
		NodeList nl = e.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( !( nl.item( i ) instanceof Text ) )
				return false;
		}
		return true;
	}
	
	private static boolean isEmpty( Element e ) {
		if ( !e.hasChildNodes() )
			return true;
		NodeList nl = e.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element )
				return false;
			if ( nl.item( i ) instanceof Text ) {
				Text t = ( Text )nl.item( i );
				if ( !"".equals( t.getNodeValue().trim() ) )
					return false;
			}
		}
		return true;
	}
	
}
