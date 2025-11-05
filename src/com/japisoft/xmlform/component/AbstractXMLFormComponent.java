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

package com.japisoft.xmlform.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.xml.XPathToolkit;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.xmlform.component.container.GridComponent;
import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;
import com.japisoft.xmlform.designer.properties.descriptors.HeightPropertyDescriptor;
import com.japisoft.xmlform.designer.properties.descriptors.WidthPropertyDescriptor;
import com.japisoft.xmlform.designer.properties.descriptors.XPropertyDescriptor;
import com.japisoft.xmlform.designer.properties.descriptors.YPropertyDescriptor;

public abstract class AbstractXMLFormComponent extends JComponent implements
		MouseMotionListener, 
		MouseListener, 
		HierarchyListener,
		PropertyChangeListener, 
		ActionListener, 
		XMLFormComponent {

	protected boolean designMode = false;
	protected ComponentContext context = null;
	private boolean topComponent = false;
	protected GrammarNodeTreeNode node = null;
	private Element fieldDescription = null;

	static {
		FocusManager.setCurrentManager( 
			new CustomFocusManager() );
	}

	private Color borderColor = Color.BLACK;
	private Color selectionColor = Color.BLUE;
	
	public AbstractXMLFormComponent(
			boolean designMode, 
			ComponentContext context) {

		setContext( context );
		
		this.designMode = designMode;
		checkListening();
			
		setLayout( new BorderLayout() );
		if ( designMode ) {
			if ( UIManager.getColor( "editix.xmlform.border" ) != null ) {
				borderColor = UIManager.getColor( "editix.xmlform.border" );
			}
			if ( UIManager.getColor( "editix.xmlform.selection" ) != null ) {
				selectionColor = UIManager.getColor( "editix.xmlform.selection" );
			}
			setBorder( new LineBorder( borderColor ) );
		}
	}

	private boolean referenceComponent = false;

	/** 
	 * @param referenceComponent Set this component as a non removable one */
	public void setReferenceComponent( boolean referenceComponent ) {
		this.referenceComponent = referenceComponent;
	}

	public void setContext( ComponentContext context ) {
		this.context = context;
/*		GrammarNodeTreeNode node = context.getCurrentTreeNode();
		if ( node != null )
			setGrammarNode( node ); */
	}
	
	public void setGrammarNode( GrammarNodeTreeNode node ) {
		this.node = node;
		node.setUserObject( this );
		setXpath( node.toXPath() );
		GrammarNode gn = node.getSource();
		if (gn != null) {
			setMaxOccurs( 
					gn.getMaxOccurs() );
			setMinOccurs( 
					gn.getMinOccurs() );
		}
		setId( node.getId() );
	}

	public GrammarNodeTreeNode getGrammarNode() {
		return this.node;
	}

	/** From the form description */
	public void setFieldDescription(Element fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	public AbstractXMLFormComponent getXMLFormComponentParent() {
		if (getParent() instanceof AbstractXMLFormComponent)
			return (AbstractXMLFormComponent) getParent();
		else if (getParent() instanceof GridComponent) {
			GridComponent gc = (GridComponent) getParent();
			if (gc.getParent() instanceof AbstractXMLFormComponent) {
				return (AbstractXMLFormComponent) gc.getParent();
			}
		}
		return null;
	}

	public Container getComponentContainer() {
		return this;
	}

	private int maxOccurs = 1;

	private int minOccurs = 1;

	public void setMaxOccurs(int maxOccurs) {
		firePropertyChange( "maxOccurs", this.maxOccurs, maxOccurs );
		this.maxOccurs = maxOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		firePropertyChange( "minOccurs", this.minOccurs, minOccurs );
		this.minOccurs = minOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	private int occurence = 1;

	private int getCurrentOccurrence() {
		return occurence;
	}

	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}
	
	private String id = null;
	
	public void setId( String id ) {
		firePropertyChange( "id", this.id, id );
		this.id = id;
	}

	public String getId() { 
		return id;
	}

	private boolean required = false;

	public void setRequired( boolean required ) {
		firePropertyChange( "required", this.required, required );
		this.required = required;
	}

	public boolean getRequired() {
		return required;
	}

	private String siblingId = null;
	
	public void setNextSiblingId( String id ) {
		firePropertyChange( 
			"nextSiblingId", 
			this.siblingId, 
			id );
		this.siblingId = id;
	}

	public String getNextSiblingId() {
		return siblingId;
	}

	public void resolveNextSibling() {
				
		if ( node != null ) {			
			TreeNode sibling = node.getNextSibling();
			while ( sibling != null ) {
				GrammarNodeTreeNode gnt = ( GrammarNodeTreeNode )sibling;
				if ( gnt.getUserObject() != null ) {
					AbstractXMLFormComponent component = 
						( AbstractXMLFormComponent )gnt.getUserObject();
					setNextSiblingId( component.id );
					break;
				}
				sibling = gnt.getNextSibling();
			}
		}
	}

	private Border savedBorder = null;

	public void setError(String message) {
		setToolTipText(message);
		if (message == null) {
			setBorder(savedBorder);
			savedBorder = null;
		} else {
			if (savedBorder == null)
				savedBorder = getBorder();
			setBorder( new LineBorder( Color.RED ) );
		}
	}

	private AbstractXMLFormComponent getNextSiblingComponent() {
/*		String nextSibling = getNextSiblingId();
		if ( nextSibling != null ) {
			return 
				( ( EditingContext )context ).getComponentById( nextSibling );
		}
		return null; */
		
		return getNextSiblingComponent( false );
	}
	
	private AbstractXMLFormComponent getNextSiblingComponentWithDOMNode() {
		
		AbstractXMLFormComponent loopComponent = this;

		while ( true ) {

			String nextSibling = 
				loopComponent.getNextSiblingId();

			if ( nextSibling != null ) {

				loopComponent = 
					( ( EditingContext )loopComponent.context ).getComponentById( 
							nextSibling );

				if ( loopComponent == null )
					return null;

				if ( loopComponent.dom != null )
					return loopComponent;

			} else
				return null;

		}

	}

	private AbstractXMLFormComponent getNextSiblingComponent( boolean required ) {
		
		AbstractXMLFormComponent loopComponent = this;

		while ( true ) {

			String nextSibling = loopComponent.getNextSiblingId();
			if ( nextSibling != null ) {

				loopComponent = 
					( ( EditingContext )loopComponent.context ).getComponentById( nextSibling );

				if ( loopComponent == null )
					return null;

				if ( !required ) {
					
					if ( loopComponent.hasDOMParent() ) 
					
						return loopComponent;
				}
				else
					if ( loopComponent.getRequired() )
						return loopComponent;


			} else
				return null;

		}

	}

	private AbstractXMLFormComponent previousSiblingComponent = null;
	
	private AbstractXMLFormComponent getPreviousSiblingComponent() {
		return previousSiblingComponent;
	}

	public Node getDOMNextSibling( boolean required ) {

		AbstractXMLFormComponent component = 
			getNextSiblingComponent( required ); 
		if ( component != null ) {
			return component.getDOM();
		}
		return null;

	}

	public Node getDOMNextSibling() {
		
/*		AbstractXMLFormComponent component = getNextSiblingComponent(); 
		if ( component != null ) {
			return component.getDOM();
		}
		
		return null; */

		return getDOMNextSibling( false ); 

	}

	protected Node dom = null;

	public Node getDOM( Document doc ) {
		if ( dom == null ) {

			if ( doc == null ) {
				try {
					doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				} catch (ParserConfigurationException e) {
				}
			}

			if ( getXpath() != null ) {
				dom = ( XPathToolkit.buildNode( 
						doc, 
						getXpath() ) );
				if ( dom != null )
					dom.setUserData( "ui", this, null );
			}

		}

		return dom;
	}

	public Node getDOM() {
		return getDOM( context.getDocument() );
	}

	protected boolean hasDOMParent() {
		if ( dom == null )
			return false;
		return ( dom.getParentNode() != null );
	}

	protected void setDOM( Node newDom ) {
		this.dom = newDom;
		if ( newDom != null )
			newDom.setUserData( "ui", this, null );
	}

	protected void checkNodeIsBound(
			Node n,
			AbstractXMLFormComponent node ) {

		if ( ( node != null ) && ( n != null ) ) {

			if ( n.getParentNode() == null  ) {
	
				if ( node.getXMLFormComponentParent() != null ) {
	
					Node parent = 
						node.getXMLFormComponentParent().getDOM();
					
					if ( parent != null ) {

						// It doesn't create a next node
						n = parent.getOwnerDocument().adoptNode( n );					

						if ( n instanceof Attr ) {
	
							( ( Element ) parent ).setAttributeNode( ( Attr ) n );
	
						} else {
							
							// Node nextSibling = getNextSiblingComponentWithDOMNode();

							AbstractXMLFormComponent nextSiblingComponent = 
								node.getNextSiblingComponentWithDOMNode();

							// node.getDOMNextSibling();

							if ( nextSiblingComponent == null ) {

								parent.appendChild( n );
								
							} else {

								Node nextSiblingNode = nextSiblingComponent.dom;
								
								if ( nextSiblingNode.getParentNode() == null ) {
									
									checkNodeIsBound(
											nextSiblingNode, 
											nextSiblingComponent );									
									
								}
								
/*								if ( nextSibling.getParentNode() == null ) {

									AbstractXMLFormComponent nextSiblingComponent = 
										node.getNextSiblingComponent();
									
									if ( nextSiblingComponent != null ) {

										// The sibling node must be bound

										checkNodeIsBound(
											nextSibling, 
											nextSiblingComponent );

									}

								} */

								if ( nextSiblingComponent.dom instanceof Attr ) {

									parent.appendChild( n );
									
								} else
								
									parent.insertBefore( 
										n,
										nextSiblingComponent.dom );

							}

						}
	
					} else {
						
						// Should be impossible !
						
					}
	
				} else {
	
					if ( context.getDocument().getDocumentElement() == null ) {
	
						// Should be the root document
						context.getDocument().appendChild( n );

					}

				}

				checkNodeIsBound(
					n.getParentNode(),
					node.getXMLFormComponentParent() );

			}
			
			
			// else {
				

			// }

		}
		
	}

	/** From a new XML document */
	public boolean dispatchDOM( Node parentNode ) {

		if ( parentNode == null ) {

			Container c = getComponentContainer();
			for ( int i = 0; i < c.getComponentCount(); i++ ) {
				( ( AbstractXMLFormComponent )c.getComponent( i ) ).setDOM( null );
				( ( AbstractXMLFormComponent )c.getComponent( i ) ).dispatchDOM( null );
			}

			return false;
			
		} else {

			String finalXPath = 
				getXpath();
			
			// For empty text case otherwise it will be ignored
			if ( finalXPath.endsWith( "/text()" ) ) {
				finalXPath = finalXPath.substring(
						0, 
						finalXPath.length() - "/text()".length() );
			}
	
			try {
				// XPath xpath = 
 //					XPathFactory.newInstance( XPathFactory.DEFAULT_OBJECT_MODEL_URI ).newXPath();
				
				// XPath xpath = net.sf.saxon.xpath.XPathFactoryImpl.newInstance().newXPath();
				
				XPath xpath = new XPathEvaluator();
	
				NodeList res = ( NodeList )xpath.evaluate(
						finalXPath, 
						parentNode,
						XPathConstants.NODESET );
	
				if ( res.getLength() > 0 ) {
	
					ArrayList<Node> snap = new ArrayList<Node>();
					for (int i = 0; i < res.getLength(); i++) {
						snap.add( 
							res.item( i ) );
					}
	
					// New node
					if ( snap.size() > 1 ) {
	
						AbstractXMLFormComponent newComponent = this;
						for ( int i = 1; i < snap.size(); i++ ) {
	
							newComponent = newComponent.addSiblingComponent();
							newComponent.setDOM( 
								snap.get( i ) );
							newComponent.dispatchToChildren( 
								snap.get( i ) );
	
						}
					}
	
					// For the current one
					setDOM(
						res.item( 0 ) );
	
					// Dispatch this the children components
					dispatchToChildren(
						res.item( 0 ) );
	
					return true;
				} else
					return false;
			} catch ( XPathExpressionException e ) {
				e.printStackTrace();
				return false;
			} catch( Exception ex ) {
				ex.printStackTrace();
				return false;
			}
		
		}
	}

	public void dispatchToChildren( Node newNode ) {

		if ( this instanceof XMLFormContainer ) {
		
			Container container = 
				getComponentContainer();
			Component[] children = 
				container.getComponents();
	
			boolean ok = false;

			if ( children != null )
				for ( int i = 0; i < children.length; i++ ) {
					Component c = children[ i ];
					
					if ( c instanceof AbstractXMLFormComponent ) {
						( ( AbstractXMLFormComponent ) c )
							.dispatchDOM( newNode );
						
					}
				}
		}
		
	}

	public boolean matchElement(AbstractXMLFormComponent component) {
		Node n = component.getDOM();
		if (getDOM().getNodeType() == n.getNodeType()) {
			return n.getNodeName().equals(getDOM().getNodeName());
		} else
			return false;
	}

	private boolean listening = false;

	private boolean listeningHierarchy = false;

	private void checkListening() {
		if (!listening) {
			if (designMode) {
				addMouseMotionListener(this);
				addMouseListener(this);
				addPropertyChangeListener(this);
			}
			addHierarchyListener(this);
			this.listening = true;
		}
	}

	private JButton addRemoveButton = null;

	protected void checkOccurenceActions() {

		if (maxOccurs > 1) {

			boolean canAddRemove = false;

			if (occurence > minOccurs) {
				// Add the delete button
				canAddRemove = true;
			}
			if (occurence < maxOccurs) {
				// Add the add button
				canAddRemove = true;
			}

			if ( canAddRemove ) {

				addRemoveButton = context.getComponentFactory()
						.newAddDeleteComponent();

				JPanel panel = new JPanel();
				panel.setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ));
				panel.add( addRemoveButton );

				add(panel, BorderLayout.WEST);
				invalidate();
				validate();

				addRemoveButton.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						displayActionPopup(e);
					};
				});

			}

		}

	}

	private void displayActionPopup(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem item = null;
		menu.add( item = context.getComponentFactory().newAddMenuItem() );		
		item.setText( Traductor.traduce( "an", "Add a new" ) + " " + getDOM().getNodeName() );
		item.addActionListener( this );
		item.setEnabled( occurence < maxOccurs );
		menu.add(item = context.getComponentFactory().newDeleteMenuItem());
		item.setText( Traductor.traduce( "del", "Delete this" ) + " " + getDOM().getNodeName() );
		item.addActionListener( this );
		item.setEnabled( occurence > minOccurs );
		menu.show((Component) e.getSource(), e.getX(), e.getY());
	}

	@Override
	public void addNotify() {
		super.addNotify();
		checkListening();
		if (!designMode)
			checkOccurenceActions();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseMotionListener(this);
		removeMouseListener(this);
		removeHierarchyListener(this);
		removePropertyChangeListener(this);
		listening = false;
		listeningHierarchy = false;
	}

/*	private Node getSiblingNodeFromTheCurrentNode() {
		Node d = getDOM();
		if ( d == null ) // ??
			return null;
		Node p = d.getParentNode();
		if ( p == null )	// ??
			return null;
	} */

	public void actionPerformed( ActionEvent e ) {
		if ( "add".equals( 
				e.getActionCommand() ) ) {
			Node siblingNode = 
				getDOMNextSibling();
			AbstractXMLFormComponent component = 
				addSiblingComponent();

			if ( component != null ) {
				
				// dumpDOM();

				Node parentNode = getXMLFormComponentParent().getDOM();
				
				// dumpDOM();
				
				// Null for forcing a new document and avoiding a default add
				Node n = component.getDOM( null );

				// dumpDOM();

				// Reforce in the good document
				if ( n instanceof Element ) {
					n = n.cloneNode( true );
					n = context.getDocument().adoptNode( n );
					// component.setDOM( n );
					component.dom = n;
					component.dispatchToChildren( n );
				}

				// dumpDOM();
				
				if ( siblingNode != null
						&& ( siblingNode.getParentNode() == parentNode ) ) {
					parentNode.insertBefore( n, siblingNode );
				} else
					parentNode.appendChild( n );

				// dumpDOM();
				
				context.action( 
					ComponentContext.SELECT_ACTION, 
					component );

			}
		} else if ( "delete".equals( e.getActionCommand() ) ) {
			removeChild();
		}
	}

	public void removeXMLFormComponent(AbstractXMLFormComponent component) {
		GrammarNodeTreeNode node = component.getGrammarNode();
		if ( node != null ) {
			node.setUserObject( null );
		}
		Container container = getComponentContainer();
		container.remove(component);		
	}

	public void removeChild() {
		
		String nextSiblingId = getNextSiblingId();
		AbstractXMLFormComponent nextSiblingComponent = getNextSiblingComponent();

		if ( ( nextSiblingComponent != null ) && 
				( nextSiblingComponent.occurence == 0 ) ) {
			if ( referenceComponent ) {
				// Must erase the content without removing it
				
				setDOM( null );
				dispatchToChildren( null );
				return;
				
			}
		}

		AbstractXMLFormComponent previousSiblingComponent = getPreviousSiblingComponent();
		
		if ( previousSiblingComponent != null && 
				nextSiblingId != null ) {
			previousSiblingComponent.setNextSiblingId( nextSiblingId );
			( (EditingContext )( previousSiblingComponent.context ) ).setComponentById( 
					nextSiblingId,
					nextSiblingComponent );
		}

		DynamicLayout.remove( getParent(), this );

	}

	public AbstractXMLFormComponent addSiblingComponent() {
		String nextSibling = 
			getNextSiblingId();

		// dumpDOM();		

		AbstractXMLFormComponent componentSibling = 
			getNextSiblingComponent();

		// dumpDOM();		
		
		AbstractXMLFormComponent newComponent = 
			cloneIt();
		
		// dumpDOM();		
		
		newComponent.previousSiblingComponent = this;
		newComponent.setId( 
				"" + newComponent.hashCode() );

		// dumpDOM();		
		
		setNextSiblingId( newComponent.getId() );
		( ( EditingContext )context ).setComponentById( newComponent.getId(), newComponent );

		// ( ( EditingContext )context ).getComponentById( newComponent.getId() );

		newComponent.setReferenceComponent( false );
		newComponent.setOccurence( occurence + 1 );
		DynamicLayout.add( getParent(), this, newComponent );

		// dumpDOM();		

		if ( nextSibling != null ) {
			newComponent.setNextSiblingId( nextSibling );
			
			if ( componentSibling == null )
				componentSibling = ( ( EditingContext )context ).getComponentById( nextSibling );

			( ( EditingContext )newComponent.context ).setComponentById(
				nextSibling,
				componentSibling );
		}

		// dumpDOM();		

		return newComponent;
	}

	public void save(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element field = XMLSerializer.serialize(doc, parent, this);
		parent.appendChild(field);
	}

	private HashMap<String, PropertyChangeEvent> mapProperties = null;

	public HashMap<String, PropertyChangeEvent> getModifiedProperties() {
		if (mapProperties == null)
			mapProperties = new HashMap<String, PropertyChangeEvent>();
		requiredProperties(mapProperties);
		return mapProperties;
	}

	// For being sure for having this properties at the end
	protected void requiredProperties(HashMap<String, PropertyChangeEvent> map) {
		map.put("bounds", new PropertyChangeEvent(this, "bounds", null,
				getBounds()));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();

		if (XMLDeserizalizer.isPropertyManaged(evt.getNewValue())) {
			if (mapProperties == null)
				mapProperties = new HashMap<String, PropertyChangeEvent>();
			// May be should be cloned ?
			mapProperties.put(name, evt);
		}
	}

	public void hierarchyChanged(HierarchyEvent e) {

		if ( ( e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED ) != 0 ) {

			Container c = getXMLFormComponentParent();
			if ( c != null ) {

				AbstractXMLFormComponent parent = ( AbstractXMLFormComponent ) c;
				if ( designMode ) {

					// Design mode
					if ( node != null ) {

						String parentXPath = parent.getAbsoluteXPath();

						String relativeXPath = XPathToolkit.getRelativeXPath(
								parentXPath, 
								node.toXPath() );

						setXpath( relativeXPath );

					}

				} else {

					/*
					 * Node parentNode = parent.getDOM(); Node sibling =
					 * getNextSibling();
					 * 
					 * if ( getDOM() instanceof Attr ) { ( ( Element )parentNode
					 * ).setAttributeNode( ( Attr )getDOM() ); } else {
					 * 
					 * if ( sibling == null ) parentNode.appendChild( getDOM() );
					 * else { parentNode.insertBefore( getDOM(), sibling ); }
					 *  }
					 */

				}

			}

		}

	}

	public void setTopComponent(boolean topComponent) {
		this.topComponent = topComponent;
	}

	private boolean selected = false;

	public void setSelectedComponent(boolean selected) {
		this.selected = selected;
		if ( !selected ) {
			setBorder( new LineBorder( borderColor ) );
		}
		else {
			setBorder( new LineBorder( selectionColor, 2 ) );
			requestFocus();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	protected String xpath = null;
	protected JLabel label = null;

	public void setXpath( String xpath ) {
		firePropertyChange( 
				"xpath", 
				this.xpath, 
				xpath );
		this.xpath = xpath;
		if ( designMode )
			updateLabel( resolveTitle() );
	}

	protected String resolveTitle() {
		return " xpath : " + xpath + " ";
	}

	protected void updateLabel( String title ) {
		if ( designMode ) {
			if ( label == null ) {
				label = new JLabel( title );
				label.setFont( label.getFont().deriveFont( Font.ITALIC, 10.0f ) );
				label.setOpaque( true );
				label.setBackground( selectionColor );
				label.setForeground( borderColor );
				label.setBorder( new LineBorder( Color.GRAY ) );
				add( label, BorderLayout.NORTH );
				invalidate();
				validate();
			} else
				label.setText( title );
		} else {
			if ( label == null ) {
				// Put an empty label for having the good height
				label = new JLabel();
				label.setText( title );
				label.setOpaque( true );
				label.setForeground( borderColor );
				add( label, BorderLayout.NORTH );
				invalidate();
				validate();
			} else
				label.setText( title );
		}
	}

	/** This is relative one */
	public String getXpath() {
		return this.xpath;
	}

	public String getAbsoluteXPath() {
		
		if ( xpath.startsWith( "/" ) )
			return xpath;

		StringBuffer sb = new StringBuffer(getXpath());
		AbstractXMLFormComponent c = getXMLFormComponentParent();
		while (c != null) {
			String parentXPath = ((AbstractXMLFormComponent) c).getXpath();
			if (!"/".equals(parentXPath)) {
				sb.insert(0, parentXPath + "/");
				if ( parentXPath.startsWith( "/" ) )
					break;
			}
			else
				sb.insert(0, "/");
			c = c.getXMLFormComponentParent();
		}
		
		return sb.toString();

	}

	public void mouseDragged( MouseEvent e ) {
		if ( !topComponent && 
				firstResizeMouseEvent != null ) {
			int x = getX();
			int y = getY();
			int width = getWidth();
			int height = getHeight();

			switch ( resizeMode ) {

			case RESIZE_UP: {
				int delta = (firstResizeMouseEvent.getY() - e.getY());
				height += delta;
				y -= delta;
				break;
			}
			case RESIZE_DOWN: {
				int delta = (e.getY() - firstResizeMouseEvent.getY());
				height = initHeight + delta;
				break;
			}
			case RESIZE_LEFT: {
				int delta = (firstResizeMouseEvent.getX() - e.getX());
				x -= delta;
				width += delta;
				break;
			}
			case RESIZE_RIGHT: {
				int delta = (e.getX() - firstResizeMouseEvent.getX());
				width = initWidth + delta;
				break;
			}
			case RESIZE_UP_LEFT: {
				int deltaY = (firstResizeMouseEvent.getY() - e.getY());
				int deltaX = (firstResizeMouseEvent.getX() - e.getX());
				height += deltaY;
				y -= deltaY;
				x -= deltaX;
				width += deltaX;
				break;
			}
			case RESIZE_UP_RIGHT: {
				int deltaY = (firstResizeMouseEvent.getY() - e.getY());
				height += deltaY;
				y -= deltaY;
				int deltaX = (e.getX() - firstResizeMouseEvent.getX());
				width = initWidth + deltaX;
				break;
			}
			case RESIZE_DOWN_LEFT: {
				int deltaY = (e.getY() - firstResizeMouseEvent.getY());
				height = initHeight + deltaY;
				int deltaX = (firstResizeMouseEvent.getX() - e.getX());
				x -= deltaX;
				width += deltaX;
				break;
			}
			case RESIZE_DOWN_RIGHT: {
				int deltaY = (e.getY() - firstResizeMouseEvent.getY());
				height = initHeight + deltaY;
				int deltaX = (e.getX() - firstResizeMouseEvent.getX());
				width = initWidth + deltaX;
				break;
			}
			case RESIZE_ALL: {
				int deltaX = (e.getX() - firstResizeMouseEvent.getX());
				int deltaY = (e.getY() - firstResizeMouseEvent.getY());
				x = x + deltaX;
				y = y + deltaY;
				break;
			}

			}

			resize(new Rectangle(x, y, width, height));

		}
	}

	public void resize(Rectangle r) {

		if ( r.width <= GridComponent.getGridSize()
				|| r.height <= GridComponent.getGridSize() )
			return;

		setBounds( r );

		// For component content
		invalidate();
		validate();

		AbstractXMLFormComponent parent = 
			getXMLFormComponentParent();

		if ( parent.topComponent ) {
			parent.resetRootContainerSize();
		}
	}

	public void resetRootContainerSize() {
		Container container = getComponentContainer();
		if ( container.getComponentCount() > 0 ) {
			int maxX = 0;
			int maxY = 0;
			for ( int i = 0; i < container.getComponentCount(); i++ ) {
				Component c = container.getComponent( i );
				maxX = Math.max( 
						maxX, 
						c.getX() + c.getWidth() );
				maxY = Math.max( 
						maxY, 
						c.getY() + c.getHeight() );
			}

			Container parent = 
				getParent();

			if ( parent instanceof JViewport ) {
				JViewport vp = ( JViewport )parent;
				Rectangle r = vp.getViewRect();
				maxX = Math.max( maxX, r.width );
				maxY = Math.max( maxY, r.height );
				parent = 
					parent.getParent();
			}
			
			setPreferredSize( 
					new Dimension( maxX, maxY ) );
			parent.invalidate();
			parent.validate();			
		}
	}

	public AbstractXMLFormComponent cloneIt() {
		// Build a temporary DOM view
		try {
			Element field = fieldDescription;

			if ( field == null ) {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setNamespaceAware( true );
				Document doc = factory.newDocumentBuilder().newDocument();
				field = XMLSerializer.serialize( doc, null, this );
			}

			final HashMap<String,AbstractXMLFormComponent> components = 
				new HashMap<String, AbstractXMLFormComponent>();

			EditingContext cloneContext = new EditingContext() {

				public AbstractXMLFormComponent getComponentById(String id) {
					return components.get( id );
				}

				public Collection<AbstractXMLFormComponent> getComponents() {
					return components.values();
				}

				public void setComponentById(String id,
						AbstractXMLFormComponent component) {
					components.put( id, component );
				}
				
				public XMLFormComponentFactory getComponentFactory() {
					return context.getComponentFactory();
				}

				public GrammarNodeTreeNode getCurrentTreeNode() {
					return context.getCurrentTreeNode();
				}
				
				public Document getDocument() {
					return context.getDocument();
				}

				public void action(int actionCode, Object parameter) {
					context.action(actionCode, parameter);
				}

			};

			AbstractXMLFormComponent clone = XMLDeserizalizer.build(
					field,
					designMode,
					null,
					components,
					cloneContext, 
					topComponent );

			// Rename new component id and next id

			return clone;

		} catch ( Exception e ) {

			ApplicationModel.debug( e );
			return null;

		}

	}

	public void mouseMoved(MouseEvent e) {

		if ( topComponent || !designMode )
			return;

		int rt = getResizeType( e );
		switch ( rt ) {

			case RESIZE_UP: {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				break;
			}
			case RESIZE_DOWN: {
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				break;
			}
			case RESIZE_LEFT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				break;
			}
			case RESIZE_RIGHT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				break;
			}
			case RESIZE_UP_LEFT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				break;
			}
			case RESIZE_UP_RIGHT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				break;
			}
			case RESIZE_DOWN_LEFT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				break;
			}
			case RESIZE_DOWN_RIGHT: {
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				break;
			}
			case RESIZE_ALL: {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				break;
			}
			default: {
				setCursor(Cursor.getDefaultCursor());
			}

		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1)
			activeAction();
	}

	protected void activeAction() {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private static final int RESIZE_UP = 1;

	private static final int RESIZE_DOWN = 2;

	private static final int RESIZE_LEFT = 3;

	private static final int RESIZE_RIGHT = 4;

	private static final int RESIZE_UP_LEFT = 5;

	private static final int RESIZE_UP_RIGHT = 6;

	private static final int RESIZE_DOWN_LEFT = 7;

	private static final int RESIZE_DOWN_RIGHT = 8;

	private static final int RESIZE_ALL = 9; // Move it

	private int resizeMode = 0;

	private static final int BORDER_SIZE = 5;

	protected boolean canBeResizedVertically() {
		return true;
	}

	private int getResizeType(MouseEvent e) {
		int rt = 0;

		if ((e.getX() < BORDER_SIZE) && (e.getY() < BORDER_SIZE))
			rt = RESIZE_UP_LEFT;
		else if ((e.getX() > getWidth() - BORDER_SIZE)
				&& (e.getY() < BORDER_SIZE))
			rt = RESIZE_UP_RIGHT;
		else if ((e.getX() < BORDER_SIZE)
				&& (e.getY() > getHeight() - BORDER_SIZE))
			rt = RESIZE_DOWN_LEFT;
		else if ((e.getX() > getWidth() - BORDER_SIZE)
				&& (e.getY() > getHeight() - BORDER_SIZE))
			rt = RESIZE_DOWN_RIGHT;
		else if (e.getX() < BORDER_SIZE)
			rt = RESIZE_LEFT;
		else if (e.getX() > getWidth() - BORDER_SIZE)
			rt = RESIZE_RIGHT;
		else if (e.getY() < BORDER_SIZE)
			rt = RESIZE_UP;
		else if (e.getY() > getHeight() - BORDER_SIZE)
			rt = RESIZE_DOWN;
		else
			rt = RESIZE_ALL;

		if (e.getX() < 0 || e.getX() > getWidth() || e.getY() < 0
				|| e.getY() > getHeight())
			rt = 0;

		if (!canBeResizedVertically()) {
			if (rt == RESIZE_UP_LEFT || rt == RESIZE_DOWN
					|| rt == RESIZE_DOWN_LEFT || rt == RESIZE_DOWN_RIGHT
					|| rt == RESIZE_UP || rt == RESIZE_UP_LEFT
					|| rt == RESIZE_UP_RIGHT)
				rt = 0;
		}

		return rt;
	}

	private MouseEvent firstResizeMouseEvent = null;

	private int initHeight = 0;

	private int initWidth = 0;

	public void mousePressed(MouseEvent e) {

		if ( designMode ) {
			context.action( 
				ComponentContext.SELECT_ACTION, 
				this );
		}

		if (designMode && !topComponent) {
			resizeMode = getResizeType(e);
			firstResizeMouseEvent = e;
			this.initHeight = getHeight();
			this.initWidth = getWidth();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (resizeMode > 0) {
			resizeMode = 0;
			firstResizeMouseEvent = null;

			if (getParent() instanceof GridComponent) {
				GridComponent gc = (GridComponent) getParent();
				gc.resizeToGrid(this);
			}
		}

		context.action( ComponentContext.SELECT_ACTION, this );
	}

	protected Component getNearest( 
			Container pc, 
			Component from ) {

		int minX = 
			Integer.MAX_VALUE;
		int minY = 
			Integer.MAX_VALUE;
		
		Component nearComponent = null;

		int fromY = 0;
		int fromX = 0;		

		if ( from != null ) {
			fromY = 
				from.getY();
			fromX = 
				from.getX();
		}

		for ( int i = 0; i < pc.getComponentCount(); i++ ) {
	
			Component c =
				pc.getComponent( i );

			if ( c instanceof StaticXMLFormComponent )
				continue;

			if ( c.getY() <= fromY ) {
				
				if ( c.getY() == fromY ) {

					if ( c.getX() <= fromX )
						continue;

				} else
					continue;

			}
			
			if ( c instanceof AbstractXMLFormComponent ) {
				
				if ( c.getY() <= minY ) {					
					minY = 
						c.getY();

					nearComponent = c;					

					if ( c.getY() == minY ) {
						if ( c.getX() < minX ) {
							minX = c.getX();
							nearComponent = c;
						}
					}
				}
			}
		}

		return nearComponent;

	}

	public boolean nextXMLFormFocus() {
		if ( getXMLFormComponentParent() == null )
			return false;
		Component c = getNearest( getXMLFormComponentParent().getComponentContainer(), this );
		if ( c != null ) {
			if ( c instanceof AbstractXMLFormComponent ) {
				( ( AbstractXMLFormComponent )c ).requestFocus();
				return true;
			} else
				return false;
		} else {
			
			AbstractXMLFormComponent parent = getXMLFormComponentParent();
			while ( parent != null && !parent.nextXMLFormFocus() ) {
				parent = parent.getXMLFormComponentParent();
			}
			
			return ( parent != null );
		}
	}

	public void previousFocus() {
		
	}
	
	// ///////////////////////////////////////////////////////////////////////

	public PropertyDescriptor[] getProperties() {
		try {

			ArrayList<PropertyDescriptor> l = new ArrayList<PropertyDescriptor>();
			prepareProperties(l);
			Collections.sort(l, new Comparator<PropertyDescriptor>() {
				public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
					return ( "" + o1 ).compareTo( "" + o2 );
				}
			});
			PropertyDescriptor[] pd = new PropertyDescriptor[l.size()];

			for (int i = 0; i < l.size(); i++)
				pd[i] = l.get(i);
			return pd;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void prepareProperties(ArrayList<PropertyDescriptor> l)
			throws Exception {
		l.add(!topComponent ? new XPropertyDescriptor(this) : null);
		l.add(!topComponent ? new YPropertyDescriptor(this) : null);
		l.add(!topComponent ? new WidthPropertyDescriptor(this) : null);
		l.add(!topComponent ? new HeightPropertyDescriptor(this) : null);
		l.add(new PropertyDescriptorImpl("background", Color.class, this));
		l.add(new PropertyDescriptorImpl("foreground", Color.class, this));
		l.add(new PropertyDescriptorImpl("font", Font.class, this));	
		l.add( 
				new PropertyDescriptorImpl( 
						"required", 
						Boolean.class, 
						this ) );		
		l.add(new PropertyDescriptorImpl("xpath", String.class, this));
	}

	@Override
	public String toString() {
		return 
			getClass().getName() + " - " + getId();
	}

	public void dumpDOM() {

		try {
			Document doc = 
				dom.getOwnerDocument();
			TransformerFactory tf = 
				TransformerFactory.newInstance();
			Transformer t = 
				tf.newTransformer();
			t.setOutputProperty( OutputKeys.INDENT, "yes" );
			t.setOutputProperty( OutputKeys.METHOD, "xml" );
			t.transform( 
					new DOMSource( doc ), 
					new StreamResult( System.out ) 
			);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch( Exception e ) {
			e.printStackTrace();
		}

	}

}

