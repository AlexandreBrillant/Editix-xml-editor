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

package com.japisoft.xmlform.editor;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.xml.validator.ErrorValidationNode;
import com.japisoft.framework.xml.validator.XSDValidator;
import com.japisoft.xmlform.Toolkit;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.EditingContext;
import com.japisoft.xmlform.component.XMLDeserizalizer;
import com.japisoft.xmlform.component.XMLFormComponentFactory;
import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.component.editable.XMLEditableComponent;

public class EditorComponent extends JPanel implements ErrorHighlighter {

	public EditorComponent() {
		initUI();
	}
	
	private void initUI() {
		setLayout( 
			new BorderLayout() 
		);
	}

	private XMLFormComponentFactory factory = null;

    public XMLFormComponentFactory getComponentFactory() {
    	if ( factory == null )
    		factory = new XMLFormComponentFactory( false, null );
    	return factory;
    }    
	
	private XMLFormContainer form = null;
	private JScrollPane sp = null;

	/** Load the form template in the main panel */
	public void initForm( XMLFormContainer container ) {
		if ( sp != null ) {
			remove( sp );
		}
		add( sp = new JScrollPane(
				form = 
					container ) );
		invalidate();
		validate();
		container.resetRootContainerSize();
	}

	private Document doc = null;
	
	public Document getDocument() {
		if ( doc == null ) {
			try {
				DocumentBuilderFactory 
					dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware( true );
				doc = 
					dbf.newDocumentBuilder().newDocument();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}

			if ( form != null ) {
				Element root = ( Element )form.getDOM( doc );
				doc = root.getOwnerDocument();
				if ( doc.getDocumentElement() == null )
					doc.appendChild( root );
			}

		return doc;
	}
	
	public String getFormTemplateURI() {
		if ( form == null )
			return null;
		return form.getFormTemplateURI();
	}

	public String getSchemaURI() {
		if ( form == null )
			return null;
		return form.getSchemaURI();
	}

	//////////////////////////////////////////////////////////////////////////////////

	public void loadDocument( File f ) throws Exception {
		loadDocument( f.toString(), new FileInputStream( f ) );
	}

	public void loadDocument( String uri ) throws Exception {
		if ( uri.indexOf( "://" ) > -1 ) {
			loadDocument( uri, new URL( uri ).openStream() );
		} else
			loadDocument( new File( uri ) );
		
	}

	private void updateTitle( String uri ) {

/*		int i = uri.lastIndexOf( "/" );
		if ( i == -1 )
			i = uri.lastIndexOf( "\\" );
		String title = getTitle();
		int j = 
			title.lastIndexOf( "[" );
		if ( j > -1 )
			title = title.substring( 0, j );
		if ( i == -1 )
			title += " [" + uri + "]";
		else
			title += " [" + uri.substring( i + 1 ) + "]";
		
		setTitle( title ); */
		
	}

	public void loadDocument( String uri, InputStream input ) throws Exception {
		updateTitle( uri );
		DocumentBuilderFactory dbf = 
			DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware( true );
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse( input, uri );
		loadDocument( uri, doc );
	}

	public void loadDocument( String uri, Document doc ) throws Exception {
		NodeList childNodes = doc.getChildNodes();
		// Get the form template location
		String formPath = null;
		for ( int i = 0; i < childNodes.getLength(); i++ ) {
			Node n = childNodes.item( i );
			if ( n instanceof ProcessingInstruction ) {
				ProcessingInstruction pi = 
					( ProcessingInstruction )n;
				if ( "xmlform".equalsIgnoreCase( pi.getTarget() ) )
					formPath = Toolkit.trimQuote( pi.getData() );
			}
		}
		if ( formPath == null ) {
			throw new Exception( 
				"Can't find the XML form document" );
		}

		formPath = 
			Toolkit.getAbsolutePath( uri, formPath );
		XMLFormContainer container = 
			loadXMLForm( formPath );
		initForm( container );

		XMLEditableComponent firstEditable = 
			container.getFirstEditableComponent();

		container.dispatchDOM( doc );

		if ( firstEditable != null ) {
			firstEditable.requestFocus();
		}

		EditorModel.CURRENT_DOCUMENT = uri;

		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						// Go to the top
						sp.getViewport().setViewPosition( 
							new Point( 0, 0 ) );						
					}
				} );
	}
	
	public void setFocusTo( AbstractXMLFormComponent component ) {
		
/*		Point p = SwingUtilities.convertPoint( 
				component, 
				new Point( 0, 0 ), 
				form ); */
		
		Point p = UIToolkit.getLocation( component );

		JViewport port = sp.getViewport();
		port.setViewPosition( p );
		component.requestFocus();

	}

	private AbstractXMLFormComponent getBoundComponent( Node n ) {
		if ( n == null )
			return null;
		AbstractXMLFormComponent component = 
			( AbstractXMLFormComponent )n.getUserData( "ui" );
		if ( component != null )
			return component;
		else
			return getBoundComponent( 
					n.getParentNode() );
	}

	private List<ErrorValidationNode> lastErrors = null;

	private EditorValidationErrorsPanel errorsPanel = null;
	
	public void highlight(ErrorValidationNode node) {
		Node n = node.getNode();
		if ( n != null ) {
			AbstractXMLFormComponent c = getBoundComponent( n );
			if ( c != null ) {
				c.setError( 
						node.getMessage() );
				setFocusTo( c );
			}
		}
	}	

	public List<Node> checkEmptyFields() {
		Document d = getDocument();
		NodeList nl = d.getElementsByTagName( "*" );
		ArrayList<Node> empties = new ArrayList<Node>();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				Element e = ( Element )n;
				
				if ( e.getUserData( "ui" ) == null )
					continue;

				NamedNodeMap nnm = 
					e.getAttributes();
				for ( int j = 0; j < nnm.getLength(); j++ ) {
					Attr attr = 
						( Attr )nnm.item( j );
					if ( isEmptyString( 
							attr.getNodeValue() ) ) {
						empties.add( attr );
					}
				}

				// Check for empty text
				if ( e.hasChildNodes() ) {
					
					NodeList l = e.getChildNodes();
					boolean empty = true;
					
					for ( int j = 0; j < l.getLength(); j++ ) {
						
						Node nn = l.item( j );
						if ( nn instanceof Text ) {

							if ( !isEmptyString( ( ( Text )nn  ).getNodeValue() ) ) {
								empty = false;
								break;
							}

						} else {

							empty = false;
							break;

						}

					}

					if ( empty ) {
						
						empties.add( e );
						
					}
				}
			}
		}
		return empties;
	}

	private boolean isEmptyString( String s ) {
		for ( int i = 0; i < s.length(); i++ ) {
			if ( !Character.isWhitespace( s.charAt( i ) ) ) {
				return false;
			}
		}
		return true;
	}

	private void removeOldErrors() {
		
		// Remove old errors
		if ( lastErrors != null ) {
			for ( ErrorValidationNode node : lastErrors ) {
				Node n = node.getNode();
				if ( n != null ) {
					AbstractXMLFormComponent c = 
						getBoundComponent( n );
					if ( c != null ) {
						c.setError( null );
					}
				}			
			}
			lastErrors = null;
			// Remove the error panel
			remove( errorsPanel );
			invalidate();
			validate();
			errorsPanel = null;
		}
				
	}

	public boolean validateDocument() {

		removeOldErrors();

		String schemaUri = form.getSchemaURI();
		if ( schemaUri != null ) {
			String baseUri = form.getFormTemplateURI();
			String absoluteSchemaUri = Toolkit.getAbsolutePath( 
					baseUri, 
					schemaUri );

			try {
				XSDValidator validator = new XSDValidator( 
						absoluteSchemaUri );

				if ( !validator.validate( getDocument() ) ) {
					
					String errorMsg = validator.getLastErrorMessage();
					
					// Dispatch the errors
					lastErrors = validator.getErrors();
					
					if ( ( lastErrors != null ) && 
							( lastErrors.size() > 0 ) ) {
						
						// Show the error(s) panel
						
						add( errorsPanel = new EditorValidationErrorsPanel( this ), BorderLayout.SOUTH ); 

						errorsPanel.init( lastErrors );
						
						invalidate();
						validate();
						
						for ( ErrorValidationNode node : lastErrors ) {
							Node n = node.getNode();
							if ( n != null ) {
								AbstractXMLFormComponent c = getBoundComponent( n );
								if ( c != null ) {
									errorMsg = node.getMessage();
									c.setError( 
											node.getMessage() );
								}
							}
						}
						
						AbstractXMLFormComponent c = getBoundComponent( 
								lastErrors.get( 0 ).getNode() );
						if ( c != null )
							setFocusTo( c );
					}

					UIToolkit.dispatchError( 
							Traductor.traduce( "errors", "Error(s) found" ) + " : [ " + 
								errorMsg + "]" );

					return false;
				}
			} catch ( Exception e ) {
				ApplicationModel.debug( e );
			}

		}

		return true;
	}

	public void saveDocument( String uri ) throws Exception {
		
		if ( !validateDocument() ) {

			if ( !UIToolkit.confirm( 
					Traductor.traduce(
						"saveerror",
						"Your document contains some errors, could you confirm the saving operation ?"  ) ) )
				return;

		}

		if ( uri.indexOf( "://" ) > -1 ) {
			
			URL u = new URL( uri );
			URLConnection c = 
				u.openConnection();
			c.setDoOutput( true );
			c.setUseCaches( false );

			ByteArrayOutputStream output = 
				new ByteArrayOutputStream();

			saveDocument(
				uri,
				output 
			);

			String s = new String( output.toByteArray(), "UTF-8" );
			OutputStream out = c.getOutputStream();
			DataOutputStream dout = new DataOutputStream( out );
			try {
				dout.writeBytes( "file=" + URLEncoder.encode( s ) );
			} finally {
				dout.close();
			}

			InputStream input = 
				c.getInputStream();

			int cc;
			while ( ( cc = input.read() ) != -1 ) {}

		} else
			saveDocument( 
				new File( uri ) 
			);
	}

	public void saveDocument( File f ) throws Exception {
		saveDocument( f.toString(), new FileOutputStream( f ) );
	}

	public void saveDocument( String uri, OutputStream output ) throws Exception {

		Transformer t = 
			TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty( OutputKeys.METHOD, "xml" );
		t.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
		t.setOutputProperty( OutputKeys.INDENT, "yes" );
		
		// Insert processing instruction for the template location
		String formTemplateURI = getFormTemplateURI();

		Document docXml = getDocument();
		
		if ( formTemplateURI != null ) {
			
			String finalTemplateURI = null;
			if ( Toolkit.areRelativeURI(
					uri,
					formTemplateURI ) ) {
				finalTemplateURI = Toolkit.getRelativeURI( formTemplateURI );
			} else
				finalTemplateURI = formTemplateURI;
			
			// Delete the previous instruction
			NodeList children = docXml.getChildNodes();
			for ( int i = 0; i < children.getLength(); i++ ) {
				Node n = children.item( i );
				if ( n instanceof ProcessingInstruction ) {
					ProcessingInstruction pi = ( ProcessingInstruction )n;
					if ( "xmlform".equals( pi.getTarget() ) ) {
						docXml.removeChild( pi );
						break;
					}
				}
			}

			ProcessingInstruction pi = 
				docXml.createProcessingInstruction( "xmlform", finalTemplateURI );
			docXml.insertBefore( pi, docXml.getDocumentElement() );
			
		}

		String schemaURI = getSchemaURI();
		if ( schemaURI != null ) {
			String finalSchemaURI = null;
			if ( Toolkit.areRelativeURI(
					uri,
					schemaURI ) ) {
				finalSchemaURI = Toolkit.getRelativeURI( schemaURI );
			} else
				finalSchemaURI = schemaURI;		

			Element e = docXml.getDocumentElement();			
			e.setAttribute( "xsi:noNamespaceSchemaLocation", finalSchemaURI );
			e.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );	
		}
		
		t.transform(
				new DOMSource( docXml ),
				new StreamResult( output ) );
		
		if ( schemaURI != null ) {
			// Remove it for the validator
			Element e = docXml.getDocumentElement();			
			e.removeAttribute( "xsi:noNamespaceSchemaLocation" );
			e.removeAttribute( "xmlns:xsi" );	
		}

		EditorModel.CURRENT_DOCUMENT = uri;
	}

	public void newDocument( File formPath ) throws Exception {
		newDocument(
			formPath.toString(), 
			new FileInputStream( formPath ) );
	}

	public void newDocument( String formURI ) throws Exception {
		if ( formURI.indexOf( "://" ) > -1 ) {
			newDocument(
					formURI,
					new URL( formURI ).openStream() );
		} else
			newDocument( 
					new File( formURI ) );
	}

	public void newDocument( String formURI, InputStream formInput ) throws Exception {
		XMLFormContainer root = loadXMLForm( formURI, formInput );
		initForm( root );
		EditorModel.CURRENT_DOCUMENT = null;
		doc = null;
	}

	private XMLFormContainer loadXMLForm( String formURI ) throws Exception {
		if ( formURI.indexOf( "://" ) > -1 ) {
			return loadXMLForm( 
					formURI, 
					new URL( formURI ).openStream() );
		} else
			return loadXMLForm( 
					formURI, 
					new FileInputStream( formURI ) );
	}

	private void action( int actionType, Object param ) {
		switch( actionType ) {
			case ComponentContext.SELECT_ACTION :
				setFocusTo( ( AbstractXMLFormComponent )param );
		}
	}

	private HashMap<String,AbstractXMLFormComponent> components = null;
	
	private XMLFormContainer loadXMLForm( String formURI, InputStream formInput ) throws Exception {

		removeOldErrors();		

		components = new HashMap<String, AbstractXMLFormComponent>();
		DocumentBuilder db = 
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = 
			db.parse( formInput, formURI );
		AbstractXMLFormComponent component = 
			XMLDeserizalizer.build( 
					doc, 
					false,
					null,
					components,
				new EditingContext() {
					public XMLFormComponentFactory getComponentFactory() {
						return EditorComponent.this.getComponentFactory();
					};
					public com.japisoft.xmlform.designer.data.GrammarNodeTreeNode getCurrentTreeNode() {
						return null;
					};
					public AbstractXMLFormComponent getComponentById(String id) {
						return components.get( id );
					}
					public void setComponentById(String id,
							AbstractXMLFormComponent component) {
						components.put( id, component );
					}
					public Collection<AbstractXMLFormComponent> getComponents() {
						return components.values();
					}
					public Document getDocument() {
						return EditorComponent.this.getDocument();
					}
					public void action(int actionCode,Object parameter) {
						EditorComponent.this.action( actionCode, parameter );
					}
				}
		);
		if ( !( component instanceof XMLFormContainer ) ) {
			throw new Exception(
					Traductor.traduce(
						"invalidform",
						"Invalid form document, no root component" )
			);
		}
		XMLFormContainer root = ( XMLFormContainer )component;
		root.setFormTemplateURI( formURI.toString() );
		return root;
	}
	
	
	
}
