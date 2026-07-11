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

package com.japisoft.xmlform.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.XMLDeserizalizer;
import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.designer.data.DataPanel;
import com.japisoft.xmlform.designer.data.DataPanelListener;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;
import com.japisoft.xmlform.designer.library.ComponentsPanel;
import com.japisoft.xmlform.designer.properties.PropertiesPanel;

public class DesignerComponent extends JPanel implements DataPanelListener {
	
	private XMLFormContainer form = null;
	private DataPanel dp = null;
	private PropertiesPanel pp = null;
	private ComponentsPanel cp = null;
	private JSplitPane sp = null;
	private JScrollPane scrollPane = null;

	private int version = 1;
	
	public DesignerComponent() {
		initUI();		
	}

	private void initUI() {
		setLayout( 
			new BorderLayout() 
		);	
		
		sp = new JSplitPane( 
				JSplitPane.HORIZONTAL_SPLIT );

		dp = new DataPanel( this );		
		
		sp.setLeftComponent( 
				scrollPane = new JScrollPane(
						form = 
							dp.getComponentFactory().newRootContainer() ) );

		JTabbedPane tp = new JTabbedPane( 
				JTabbedPane.TOP );

		tp.setPreferredSize( new Dimension( 250, 0 ) );
		
		cp = 
			new ComponentsPanel();
		pp = 
			new PropertiesPanel();

		tp.addTab( "Data", 
				Toolkit.getImageIcon( "images/data.png" ), dp );
		tp.addTab( "Components", 
				Toolkit.getImageIcon( "images/component.png"), cp );
		tp.addTab( "Properties", 
				Toolkit.getImageIcon( "images/palette2.png" ), pp );

		sp.setOneTouchExpandable( true );

		sp.setRightComponent( tp );
		add( sp );
		
		sp.setDividerLocation( 700 );
		
	}

	public void setCurrentComponent( AbstractXMLFormComponent component ) {
		pp.init( component.getProperties() );
		GrammarNodeTreeNode gnt = component.getGrammarNode();
		if ( gnt != null ) {
			dp.selectNode( gnt );
		}

/*		Point p = SwingUtilities.convertPoint( 
				component, 
				new Point( 0, 0 ), 
				scrollPane );

		JViewport port = scrollPane.getViewport();
		port.setViewPosition( p ); */

	}
	
	public void cut() {
		form.cut();
	}

	public void scrollTo( AbstractXMLFormComponent component ) {

		Point p = UIToolkit.getLocation( component );

		scrollPane.getViewport().setViewPosition( p );

	}	

	public void newRoot(String selectedItem) {
		form.setXpath( "/" + selectedItem );
	}	

	public void newForm() {
		int dividerLocation = sp.getDividerLocation();
		dp.resetSchema( null );
		sp.setLeftComponent( null );
		sp.setLeftComponent(
			scrollPane = new JScrollPane(
				form = 
					dp.getComponentFactory().newRootContainer() ) );
		sp.setDividerLocation( dividerLocation );
		XmlFormModel.CURRENT_DOCUMENT = null;
	}
	
	public void save( Document doc ) {

		Element root = doc.createElementNS( null, "xf" );
		root.setAttribute( "appVersion", XmlFormModel.getAppVersion() );
		root.setAttribute( "version", "" + version );
		if ( XmlFormModel.CURRENT_SPELLCHECK != null )
			root.setAttribute( "spell", XmlFormModel.CURRENT_SPELLCHECK );

		if ( dp.getSchemaLocation() != null ) {
			root.setAttribute( "schema", "" + dp.getSchemaLocation() );
		}

		if ( dp.getRoot() != null ) {
			root.setAttribute( "root", dp.getRoot() );
		}

		Element fields = doc.createElementNS( null, "fields" );
		root.appendChild( fields );

		form.save( fields );
		doc.appendChild( root );

	}

	public void load( File f ) throws Exception {
		load( f.toString(), new FileInputStream( f ) );
	}

	public void load( String uri ) throws Exception {
		if ( uri.indexOf( "://" ) > -1 ) {
			load( uri, new URL( uri ).openStream() );
		} else
			load( new File( uri ) );
	}
	
	public void load( String uri, InputStream input ) {
		try {

			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder db = 
				factory.newDocumentBuilder();
			Document doc = 
				db.parse( input );
			load( uri, doc );
			
		} catch ( Exception e ) {
			XmlFormModel.debug( e );
			UIToolkit.dispatchError( 
					"Can't load " + uri + " : " + e.getMessage() 
			);
		}
		
	}

	public void load( String uri, Document doc ) throws Exception {
			
			Element root = doc.getDocumentElement();
			
			if ( root.hasAttribute( "spell" ) )
				XmlFormModel.CURRENT_SPELLCHECK = 
					root.getAttribute( "spell" ); 

			String schema = root.getAttribute( "schema" );
			
			if ( !"".equals( schema ) ) {

				schema = com.japisoft.xmlform.Toolkit.getAbsolutePath(
						uri,
						schema );

				dp.resetSchema( schema );
				
			} else {
				
				UIToolkit.warn( "No schema found ?" );

			}

			String schemaRoot = root.getAttribute( "root" );
			if ( !"".equals( schemaRoot ) ) {
				dp.resetRoot( schemaRoot );
			}
						
			AbstractXMLFormComponent rootComponent = 
				XMLDeserizalizer.build( 
					doc,
					true,
					dp.getGrammarNodeTreeNodeRoot(),
					null,
					new ComponentContext() {
						public com.japisoft.xmlform.component.XMLFormComponentFactory getComponentFactory() {
							return dp.getComponentFactory();
						};
						public com.japisoft.xmlform.designer.data.GrammarNodeTreeNode getCurrentTreeNode() {
							return dp.getCurrentTreeNode();
						};
						public Document getDocument() {
							return null;
						}
						public boolean inBuildingXMLDocument() {
							return false;
						}

						public AbstractXMLFormComponent getComponentById(
								String id) {
							return null;
						}
						public void setComponentById(String id,
								AbstractXMLFormComponent component) {
						}
						
						public void action(int actionCode,Object parameter) {
							dp.action( actionCode, parameter );
						}
					} );
			if ( !( rootComponent instanceof XMLFormContainer ) ) {
				throw new Exception( "Illegal root component" );
			}
			XMLFormContainer newRoot = 
				( XMLFormContainer )rootComponent;
			this.form = newRoot;

			// For removing the previous one
			int dividerLocation = sp.getDividerLocation();
			sp.setLeftComponent( null );
			sp.setLeftComponent( 
					scrollPane = new JScrollPane( newRoot ) );
			sp.setDividerLocation( 
					dividerLocation );

			newRoot.resetRootContainerSize();

			XmlFormModel.CURRENT_DOCUMENT = uri;
			
	}
	
}
