package com.japisoft.xmlpad.helper.handler.schema;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.SchemaHelperManager;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.AttributeHelper;
import com.japisoft.xmlpad.helper.model.Descriptor;
import com.japisoft.xmlpad.helper.model.EnumerationDescriptor;
import com.japisoft.xmlpad.helper.model.SchemaNodable;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

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
public abstract class AbstractTagHandler extends AbstractHelperHandler {

	final static boolean DEBUG_HELPER = Debug.DEBUG; 
	static TagDescriptor COMMENT_DESCRIPTOR = new TagDescriptor( 
			"!-- -->",
			null, 
			true, 
			true );
	static TagDescriptor CDATA_DESCRIPTOR = new TagDescriptor( "![CDATA[ ]]>",
			null, true, true );
	private Vector tags = null;
	protected String namespace;

	protected String getActivatorSequence() {
		return "<";
	}

	public void dispose() {
		super.dispose();
		this.tags = null;
	}

	private String currentElementName = null;
	
	public int getPriority() {
		return 1;
	}	
	
	protected boolean isCtrlSpaceActivator() {
		return lastActivatorString == null;
	}
	
	private String lastActivatorString = null;
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document,
			boolean insertBefore, 
			int offset, 
			String activatorString ) {
		this.lastActivatorString = activatorString;
		if ( "<".equals( activatorString ) || 
				( activatorString == null && 
						!document.isInsideTag( offset, false, false ) ) ) {		
			flush();
			setLocation( currentNode, offset );
			return ( tags != null && tags.size() > 0 );
		} else
			return false;
	}

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset, 
			String activatorString ) {

		if ( tags != null ) {
			
			for ( int i = 0; i < tags.size(); i++ ) {
				addDescriptor(
						( Descriptor )tags.get( i ) );
				if ( tags.get( i )
						instanceof TagDescriptor ) {
					TagDescriptor td = ( TagDescriptor )tags.get( i );
					td.setAddedPart( activatorString );
					
					// Check for required attributs
					AttDescriptor[] ats = td.getAtts();
					if ( ats != null ) {
						for ( int j = 0; j < ats.length; j++ ) {
							
							AttDescriptor at = ats[ j ];
							if ( at.isRequired() ) {
																
								XMLContainer container = document.getContainer();
								String[] toForce = container.getDocumentInfo().getListOfAttributesWithAutoAssistant();
								if ( toForce != null ) {
									for ( int k = 0; k < toForce.length; k++ ) {
										if ( toForce[ k ].equals(
												at.getName() ) ) {
											at.setAutomaticNextHelper( true );
											td.setAutomaticNextHelper( true );
											break;
										}
									}
								}

							}
							
						}
					}
				}
			}
		}
	}	

	public String getName() {
		return SchemaHelperManager.SCHEMA_ELEMENTS;
	}	

	public boolean mustBeJobSynchronized() {
		return true;
	}	
	
	/////////////////////////////////////////////////////////////
	// OLD HELPER CODE //////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	
	public void setNamespace( String namespace ) {
		this.namespace = namespace;
	}

	public String getNamespace() { return namespace; };

	public TagDescriptor addTag(String name, AttDescriptor[] att, boolean empty) {
		TagDescriptor td = addTagDescriptor(new TagDescriptor(name, att, empty));
		td.namespace = namespace;
		return td;
	}

	protected String[] locationPath;
	protected String location;
	protected String lastLocation;

	public String getTitle() {
		return null;
	}

	protected FPNode currentDocumentNode;
	protected int currentDocumentLocation;

	public abstract TagDescriptor getTag( FPNode node );
	
	protected FPNode translateNode(FPNode located) {
		return located;
	}

	/** Set the current location */
	public void setLocation( FPNode locatedNode, int offset ) {
		flush();

		if ( locatedNode != null ) {
			locatedNode = translateNode( locatedNode );
			locationPath = new String[] { locatedNode.getContent() };
		} else
			locationPath = null;

		currentDocumentNode = locatedNode;
		currentDocumentLocation = offset;

		try {

			location = null;

			if ( locationPath != null && 
					locationPath.length > 0 )
				location = locationPath[ locationPath.length - 1 ];

			if ( ( namespace != null ) && 
					( locatedNode != null ) ) {
				// Search for the first parent with the right namespace
				while ( !namespace.equals( 
						locatedNode.getNameSpaceURI() ) ) {
					locatedNode = locatedNode.getFPParent();
					if ( locatedNode == null )
						break;
				}
				if ( locatedNode != null )
					currentDocumentNode = locatedNode;
			}

			notifyLocation();

			if ( this instanceof SchemaNodable ) {
				SchemaNodable nodable = (SchemaNodable) this;
				SchemaNode schemaRoot = nodable.getSchemaNode();

				if (schemaRoot != null) {

					if ( DEBUG_HELPER )
						schemaRoot.dump();

					if (namespace != null && schemaRoot.namespace == null)
						schemaRoot.namespace = namespace;
					if (currentDocumentNode != null) {
						buildContentFromSchemaNode(schemaRoot);
					} else {
						// add only the first child of the root
						if (schemaRoot.getSchemaNodeCount() > 0) {
							SchemaNode first = schemaRoot.getSchemaNode(0);
							addFirstSchemaNode(first);
						}
					}
					completeContentForElementWithoutPrefix();
				}
			}

			lastLocation = location;

		} finally {
			currentDocumentNode = null;
		}
	}

	/** Particular case with DTD and namespace */
	protected void completeContentForElementWithoutPrefix() {}

	private void addFirstSchemaNode( SchemaNode first ) {

		if (first.isElement()) {
			addTagDescriptor(first.element);
		} else if (first.isOpOR()) {
			// Add all
			for (int i = 0; i < first.getSchemaNodeCount(); i++) {
				if ( first.getSchemaNode( i ) != first )
					addFirstSchemaNode(first.getSchemaNode(i));
			}
		} else if (first.isOpAND()) {
			// Add the first one
			if (first.getSchemaNodeCount() > 0 && 
					first.getSchemaNode( 0 ) != first )
				addFirstSchemaNode(first.getSchemaNode(0));
		}
	}

	/** Prepare for tag list for the current location */
	protected void notifyLocation() {
	}

	/** @return the attribute helper for this node */
	public AttributeHelper getAttributeHelperForNode(FPNode node) {
		TagDescriptor td = getTag(node);
		if (td != null) {
			AttributeHelper helper = new AttributeHelper(td);
			helper.prepare(td);
			return helper;
		}
		return null;
	}

	public void addEnumerationDescriptor( EnumerationDescriptor ed ) {
		if (tags == null)
			tags = new Vector();
		tags.add( ed );
	}
	
	public TagDescriptor addTagDescriptor( TagDescriptor tag ) {
		if (tag == null)
			return null;

		if (tags == null)
			tags = new Vector();

		int insertTo = -1;

		// Avoid redondancy
		for (int i = 0; i < tags.size(); i++) {
			String name = ( ( TagDescriptor )tags.get( i ) ).getName();
			if (name != null) {
				if (name.equals(tag.getName()))
					return null;
				if (insertTo == -1) {
					if (name.compareTo(tag.name) >= 0) {
						insertTo = i;
					}
				}
			}
		}

		// Lexico graphique order

		if (insertTo == -1)
			tags.add( tag );
		else
			tags.add( insertTo, tag );

		tag.namespace = namespace;

		return tag;
	}

	/** Reset all tags */
	protected void flush() {
		tags = new Vector();
	}

	public boolean hasElements() {
		return (tags != null) && (tags.size() > 0);
	}

	public TagDescriptor[] getTags() {
		if (tags == null)
			return null;
		TagDescriptor[] d = new TagDescriptor[tags.size()];
		tags.copyInto(d);
		return d;
	}

	public boolean addedSystemTag = true;

	protected boolean addSystemTag() {
		return addedSystemTag;
	}

	public void fillList( FPNode node, DefaultListModel model ) {
		if ( tags == null ) {
			if ( addSystemTag() ) {
				model.addElement( COMMENT_DESCRIPTOR );
				model.addElement( CDATA_DESCRIPTOR );
			}
			return;
		}
		for (int i = 0; i < tags.size(); i++) {
			Object _;
			model.addElement( _ = tags.get( i ) );
		}
		if ( addSystemTag() ) {
			model.addElement( COMMENT_DESCRIPTOR );
			model.addElement( CDATA_DESCRIPTOR );
		}
	}

	protected String getLostCharacter() {
		return "<";
	}

	private String source;

	/** Reset the schema initial source */
	public void setSource(String source) {
		this.source = source;
	}

	/** @return the schema initial source */
	public String getSource() {
		return source;
	}

	/////////////////////////////////////////////////////////////////////////
	//////////////////////////// GESTION SCHEMA /////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	/** Build the available tag list */
	private void buildContentFromSchemaNode(SchemaNode parent) {

		ArrayList xmlnodes = null;

		String prefix = currentDocumentNode.getDocument().getRoot()
				.getNameSpacePrefix();

		for (int i = 0; i < currentDocumentNode.childCount(); i++) {

			if (xmlnodes == null)
				xmlnodes = new ArrayList();

			if (currentDocumentNode.childAt(i).isTag()) {

				if (prefix != null) {
					if (!prefix.equals(currentDocumentNode.childAt(i)
							.getNameSpacePrefix()))
						continue;
				}

				xmlnodes.add(currentDocumentNode.childAt(i));
			}
		}

		if (parent.getSchemaNodeCount() > 0) {
			namespace = parent.namespace;
			ArrayList previousUsage = new ArrayList();
			manageSchemaNode(xmlnodes, parent.getSchemaNode(0), previousUsage);

			String addedPrefix = null;

			// Search for a prefix
			if (tags != null && namespace != null) {
				FPNode nodeTmp = currentDocumentNode;
				all: while (nodeTmp != null) {
					Iterator<String> enume = nodeTmp.getNameSpaceDeclaration();
					if (enume != null) {
						while (enume.hasNext()) {
							String pref = (String) enume.next();
							String namespacePref = nodeTmp
									.getNameSpaceDeclarationURI(pref);
							if (namespace.equals(namespacePref)) {
								addedPrefix = pref;
								break all;
							}
						}
					}
					nodeTmp = nodeTmp.getFPParent();
				}
			}
			if (addedPrefix != null) {
				for (int i = 0; i < tags.size(); i++) {
					TagDescriptor td = (TagDescriptor) tags.get(i);
					td.name = addedPrefix + ":" + td.name;
					for (int j = 0; j < td.getSynonymousTagDescriptorCount(); j++) {
						td.getSynonymousTagDescriptor(j).name = addedPrefix
								+ ":" + td.getSynonymousTagDescriptor(j).name;

					}
				}
			}
		}
	}

	private int manageSchemaNode(
			ArrayList nodes, 
			SchemaNode node,
			ArrayList previousUsage) {

		if (previousUsage.contains(node))
			return FALSE;

		previousUsage.add(node);

		if (node.isRoot()) {
			for (int i = 0; i < node.getSchemaNodeCount(); i++) {
				manageSchemaNode(nodes, node.getSchemaNode(i), previousUsage);
			}
			return TRUE;
		} else if (node.isOpAND()) {
			return manageAnd(nodes, node, previousUsage);
		} else if (node.isOpOR()) {
			return manageOr(nodes, node, previousUsage);
		} else if (node.isElement()) {
			int ok = manageElement(nodes, node, previousUsage);
			if (ok == TRUE)
				previousUsage.removeAll(previousUsage);
			return ok;
		} else
			return UNKNOWN;
	}

	static final int TRUE = 1;

	static final int FALSE = 0;

	static final int UNKNOWN = 2;

	private int manageAnd(ArrayList nodes, SchemaNode node,
			ArrayList previousUsage) {

		node.marked = true;

		int toReturn = TRUE;

		for (int i = 0; i < node.getSchemaNodeCount(); i++) {

			SchemaNode childNode = node.getSchemaNode(i);

			int matched = manageSchemaNode(nodes, childNode, previousUsage);

			if (matched == FALSE) {

				if ((i == node.getSchemaNodeCount() - 1)
						|| ((i == node.getSchemaNodeCount() - 2) && node
								.getSchemaNode(node.getSchemaNodeCount() - 1) == node)) {
					// OK
					toReturn = TRUE;

				} else

					toReturn = FALSE;

				break;
			}
		}

		return toReturn;
	}

	private int manageOr(ArrayList nodes, SchemaNode node,
			ArrayList previousUsage) {

		int toReturn = FALSE;

		node.marked = true;

		for (int i = 0; i < node.getSchemaNodeCount(); i++) {

			SchemaNode childNode = node.getSchemaNode(i);

			int matched = manageSchemaNode(nodes, childNode, previousUsage);

			if (matched == TRUE) {
				toReturn = TRUE;
				break;
			} else
			if ( matched == UNKNOWN ) {
				toReturn = UNKNOWN;
			}
				
		}

		if ( toReturn == UNKNOWN )
			return UNKNOWN;
		
		if (toReturn == FALSE) {

			for (int i = 0; i < node.getSchemaNodeCount(); i++) {

				SchemaNode childNode = node.getSchemaNode(i);
				if (childNode.isSigma())
					return UNKNOWN;

			}

		} else {

			// Check for loop case

			for (int i = 0; i < node.getSchemaNodeCount(); i++) {

				SchemaNode childNode = node.getSchemaNode(i);
				if (childNode == node) {

					return manageSchemaNode(nodes, node, previousUsage);

				}

			}

		}

		// Test non terminal case

		/*
		 * if ( node.getSchemaNode( node.getSchemaNodeCount() - 1 ).isOpAND() |
		 * node.getSchemaNode( node.getSchemaNodeCount() - 1 ).isOpOR() )
		 * manageSchemaNode( nodes, node.getSchemaNode(
		 * node.getSchemaNodeCount() - 1 ), previousUsage );
		 */

		return toReturn;
	}

	private int manageElement(ArrayList nodes, SchemaNode node,
			ArrayList previousUsage) {

		FPNode first = nextXMLnode(nodes);

		if (first == null) {

			// Add it !

			addTagDescriptor(node.element);

		} else {

			if (match(first, node)) {

				// Next one

				nodes.remove(0);
				node.marked = true;

				// Loop on itself ?

				for (int i = 0; i < node.getSchemaNodeCount(); i++) {

					// Unmarked it

					node.getSchemaNode(i).marked = false;
					previousUsage.remove(node.getSchemaNode(i));

					manageSchemaNode(nodes, node.getSchemaNode(i),
							previousUsage);

				}

				return TRUE;
			}
		}

		return FALSE;
	}

	// ------------------------------------------------------------------------------

	private boolean matchWithNS(FPNode node1, TagDescriptor td) {
		String content = node1.getContent();
		boolean match = content.equals(td.getName())
				&& (td.namespace == null || td.namespace.equals(node1
						.getNameSpaceURI()));

		return match;
	}

	private boolean match(FPNode node1, SchemaNode node2) {
		TagDescriptor td = node2.element;
		if (!matchWithNS(node1, td)) {
			for (int i = 0; i < td.getSynonymousTagDescriptorCount(); i++) {
				TagDescriptor td2 = td.getSynonymousTagDescriptor(i);
				if (matchWithNS(node1, td2))
					return true;
			}
			return false;
		} else
			return true;
	}

	private FPNode nextXMLnode(ArrayList nodes) {
		if (nodes == null || nodes.size() == 0)
			return null;
		FPNode first = (FPNode) nodes.get(0);
		if (first.getStartingOffset() >= currentDocumentLocation)
			return null;
		return first;
	}
	
	
}
