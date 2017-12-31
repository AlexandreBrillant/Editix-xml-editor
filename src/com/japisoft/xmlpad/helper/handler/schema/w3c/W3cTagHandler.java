package com.japisoft.xmlpad.helper.handler.schema.w3c;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;

import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;

import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.model.EnumerationDescriptor;
import com.japisoft.xmlpad.helper.model.SchemaNodable;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.TagDescriptor;
import com.japisoft.xmlpad.toolkit.XMLFileData;
import com.japisoft.xmlpad.toolkit.XMLToolkit;

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
public class W3cTagHandler extends AbstractTagHandler implements SchemaNodable {
	private String root;
	private FPNode node;
	private TreeWalker walker = null;

	public W3cTagHandler(String rootName, FPNode node,
			String currentDocument) {
		this.root = rootName;
		this.node = node;
		ArrayList processedList = new ArrayList();		
		resolveIncludeRedefine(node, currentDocument, processedList );
		walker = new TreeWalker(node);
	}

	public void dispose() {
		super.dispose();
		this.node = null;
		this.walker = null;
	}

	private void resolveIncludeRedefine(
			FPNode root, 
			String currentDocument,
			ArrayList processedList ) {

		ArrayList removeList = new ArrayList();

		for ( int i = 0; i < root.childCount(); i++ ) {
			FPNode child = root.childAt( i );
			if ( child.isTag() ) {
				if ( child.matchContent( "include" ) ) {
					String location = child.getAttribute( "schemaLocation" );
					if ( location != null ) {
						if ( processedList.contains( location ) )
							continue;
						resolveNodeInclude( 
								root, 
								child, 
								currentDocument,
								location );
						processedList.add( location );
					}
					removeList.add( child );
				} else
				if ( child.matchContent( "redefine" ) ) {
					String location = child.getAttribute( "schemaLocation" );
					if ( location != null ) {
						if ( processedList.contains( location ) )
							continue;						
						resolveNodeRedefine( 
								root, 
								child, 
								currentDocument,
								location );
						processedList.add( location );
					}
					removeList.add( child );
				}
			}
		}

		for ( int i = 0; i < removeList.size(); i++ ) {
			FPNode wrongNode = ( FPNode )removeList.get( i );
			root.removeChildNode( wrongNode );
		}
	}

	private void resolveNodeRedefine(
			FPNode root, 
			FPNode child,
			String currentDocument,
			String location ) {
		
		InputStream reader = null;
		SchemaLocator schemaLocator = new SchemaLocator( currentDocument, location );

		try {
			reader = schemaLocator.getInputStream();
			
			XMLFileData content =
				XMLToolkit.getContentFromInputStream(
						reader, null );

			if ( reader != null ) {
				FPParser p = new FPParser();
				try {
					Document doc = p.parse(new StringReader(content.getContent()));
					if ( doc.getRoot() != null ) {
						FPNode n = ( FPNode )doc.getRoot();
						
						// Add all the content to the end
						// Only if there's no conflict with the redefine part
						
						for ( int i = 0; i < n.childCount(); i++ ) {
							FPNode externalNode = n.childAt( i );
							// Look for a name ?
							String name = externalNode.getAttribute( "name" );
							if ( name != null ) {
								
								boolean found = false;
								
								// Search if this part is redefined ?
								for ( int j = 0; j < child.childCount(); j++ ) {
									String nameRedefine = child.childAt( j ).getAttribute( "name" );
									if ( name.equals( nameRedefine ) ) {
										found = true;
										break;
									}
								}
								
								if ( !found ) {
									root.appendChild( externalNode );
								}
								
							} else {
								root.appendChild( externalNode );
							}
							
						}
						
						// Add the redefine part
						for ( int i = 0; i < child.childCount(); i++ ) {
							root.appendChild( child.childAt( i ) );
						}

					}
				} catch( ParseException exc ) {

				}
			}
		} catch( Throwable exc ) {

		}
		
	}	
	
	private void resolveNodeInclude(
			FPNode root, 
			FPNode child,
			String currentDocument,
			String location ) {

		InputStream reader = null;
		SchemaLocator schemaLocator = new SchemaLocator( currentDocument, location );

		try {
			// For encoding !
			reader = schemaLocator.getInputStream();

			if ( reader != null ) {
				FPParser p = new FPParser();
				
				XMLFileData content =
					XMLToolkit.getContentFromInputStream(
							reader, null );

				try {
					Document doc = p.parse(new StringReader(content.getContent()));
					if ( doc.getRoot() != null ) {
						FPNode n = ( FPNode ) doc.getRoot();

						// Add all the content to the end

						for ( int i = 0; i < n.childCount(); i++ ) {
							root.appendChild( n.childAt( i ) );
						}

					}
				} catch (ParseException exc) {

				}
			}
		} catch ( Throwable e ) {
		}
	}
	
	public TagDescriptor getTag( FPNode node ) {
		FPNode def = getElementDef( node );
		if ( def == null )
			return null;
		SchemaToSchemaNode sts = new SchemaToSchemaNode();
		sts.allattributeMode = true;
		SchemaNode element = new SchemaNode(SchemaNode.ELEMENT);
		sts.processElement(def, element, true);
		return element.element;
	}

	private SchemaNode schemaNode = null;

	public SchemaNode getSchemaNode() {
		return schemaNode;
	}

	private FPNode getElementDef( FPNode documentNode ) {
		FPNode elementDef = walker.getOneNodeByCriteria( new AndCriteria(
				new NodeNameCriteria( "element" ), 
				new AttributeCriteria("name",
						documentNode.getContent() ) ), true );
		return elementDef;
	}

	// Rejecting default simple type
	private FPNode getElementTypeDef( FPNode elementNode ) {
		if ( elementNode.hasAttribute( "type" ) ) {

			String type = elementNode.getAttribute( "type" );
			
			// Reject default simple type
			String p = elementNode.getNameSpacePrefix();
			if ( p != null ) {

				if ( type.startsWith( p + ":" ) )
					return null;
				
			}
			
			// Search for this type
			for  ( int i = 0; i < node.getViewChildCount();i++ ) {

				FPNode n = ( FPNode )node.getViewChildAt( i );
				if  (n.matchContent( "simpleType" ) ||
						n.matchContent( "complexType" ) ) {
				
					// Check for the name
					if ( type.equals( 
							n.getAttribute( "name" ) ) )
						return n;

				}

			}

		} else {
			
			for ( int i = 0; i < elementNode.childCount(); i++ ) {
				
				FPNode n = ( FPNode )elementNode.getViewChildAt( i );
				if  (n.matchContent( "simpleType" ) ||
						n.matchContent( "complexType" ) )
					return n;

			}
			
		}
		return null;
	}

	protected void notifyLocation() {
		schemaNode = null;
		if (currentDocumentNode == null)
			return;
		SchemaToSchemaNode sts = new SchemaToSchemaNode();
		FPNode elementDef = getElementDef( currentDocumentNode );
		
		if ( elementDef != null ) {
			
			boolean foundEnumeration = false;
			
			if ( isCtrlSpaceActivator() ) {
				// Check for enumeration values
				
				FPNode typeNode = getElementTypeDef( elementDef );
				if ( typeNode != null && 
						typeNode.matchContent( "simpleType" ) ) {

					// We get a simple type may be with enumeration ?
					TreeWalker tw = new TreeWalker(
							typeNode );
					Enumeration e = tw.getNodeByCriteria(
							new NodeNameCriteria( "enumeration" ), true );
					if ( e != null ) {
						
						while ( e.hasMoreElements() ) {
							
							foundEnumeration = true;
							FPNode enume = ( FPNode )e.nextElement();
							String val = enume.getAttribute( "value" );
							if ( val != null ) {
								EnumerationDescriptor 
									ed = new EnumerationDescriptor( val );
								addEnumerationDescriptor( ed );
							}

						}
						
					}
				}

			}

			if ( !foundEnumeration )
				schemaNode = sts.getSchemaNode( elementDef );
		}
	}


}
