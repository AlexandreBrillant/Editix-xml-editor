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

package com.japisoft.editix.action.edit.merger;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.job.JobAdapter;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class MergerPanel extends JPanel implements ActionListener, ListCellRenderer<Node>, ListSelectionListener {

	private JTextField xpathField;
	private JButton runXPath;
	private JList<Node> listNode;
	private JButton mergeButton;
	private JButton mergeButton2;
	private String currentFileLocation;

	public MergerPanel() {
		setLayout( new MigLayout( "", "[grow][]", "[][][][grow][]" ) );
		add( new JLabel( "Select nodes with XPath"), "grow,span" );
		add( xpathField = new JTextField(), "grow" );
		add( runXPath = new JButton( "Run" ), "wrap" );
		add( new JLabel( "Choose nodes to merge"), "grow,span" );
		add( new JScrollPane( listNode = new JList<Node>() ), "span,grow, wrap" );
		
		JPanel tmp = new JPanel();
		tmp.add( mergeButton = new JButton( "Merge content" ) );
		tmp.add( mergeButton2 = new JButton( "Merge content/New container" ) );
		add( tmp, "span" );

		mergeButton.setToolTipText( "Merge content keeping the first node as the main container" );
		listNode.setCellRenderer( this );
		listNode.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		runXPath.addActionListener( this );
		mergeButton.addActionListener( this );
		mergeButton2.addActionListener( this );
		listNode.getSelectionModel().addListSelectionListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		runXPath.removeActionListener( this );
		mergeButton.removeActionListener( this );
		mergeButton2.removeActionListener( this );
		listNode.getSelectionModel().removeListSelectionListener( this );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == runXPath )
			runXPath( true );
		else
		if ( e.getSource() == mergeButton ) 
			merge( null );
		else
		if ( e.getSource() == mergeButton2 ) {
			String newContainer = EditixFactory.buildAndShowInputDialog( "Choose a container name " );
			if ( newContainer != null )
				merge( newContainer );
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Object[] oo = listNode.getSelectedValues();
		if (currentFileLocation != null)
			EditixFrame.THIS.activeXMLContainer( currentFileLocation );
		for ( Object o : oo ) {
			if (o instanceof FPNode) {
				FPNode sn = (FPNode)o;
				XMLContainer container = EditixFrame.THIS.getSelectedSubContainer( "XML" );
				if (container != null) {
					container.getEditor().highlightLine( sn.getStartingLine() );
					container.getEditor().setCaretPosition( sn.getStartingOffset() + 1 );
				}
			}
		}
	}

	JLabel lst_renderer;
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends Node> list, 
			Node value, 
			int index, 
			boolean isSelected,
		boolean cellHasFocus) {
		if ( lst_renderer == null ) {
			lst_renderer = new JLabel();
			lst_renderer.setOpaque( true );
		}
		if ( value instanceof Element ) {
			Element e = ( Element )value;
			String content = e.getNodeName();
			
			if ( e.hasChildNodes() ) {
				if ( e.getFirstChild() instanceof Text ) {
					content += " " + e.getFirstChild().getNodeValue().trim();
				}
			}

			lst_renderer.setText( content );
		} else
		if ( value instanceof Text ) {
			lst_renderer.setText( ( (Text)value ).getNodeValue() );
		}
		if ( isSelected ) {
			lst_renderer.setBackground( list.getSelectionBackground() );
			lst_renderer.setForeground( list.getSelectionForeground() );
		} else {
			lst_renderer.setBackground( list.getBackground() );
			lst_renderer.setForeground( list.getForeground() );
		}
		
		return lst_renderer;
	}
	
	private void runXPath( boolean warningMode ) {
		String xpathStr = xpathField.getText();
		if ( "".equals( xpathStr ) ) {
			EditixFactory.buildAndShowErrorDialog( "No xpath expression ?" );
			return;
		}
		XPathFactory factory = XPathFactory.newDefaultInstance();
		try {
			XPath xpath = factory.newXPath();
			Node root = getNodeFromRoot();

			NodeList lst = (NodeList)xpath.evaluate(
					xpathStr,
					root,
					XPathConstants.NODESET
			);

			if ( lst.getLength() == 0 ) {
				if ( listNode.getModel() instanceof DefaultListModel ) {
					( ( DefaultListModel )listNode.getModel() ).removeAllElements();
				}
				if ( warningMode )
					EditixFactory.buildAndShowWarningDialog( "No result" );
			}
			else {
				DefaultListModel<Node> model = new DefaultListModel<Node>();				
				for ( int i = 0; i < lst.getLength(); i++ ) {
					if ( lst.item( i ) instanceof Element ) {
						model.add(i, lst.item( i ) );
					} else {
						if ( warningMode )
							EditixFactory.buildAndShowWarningDialog( "Invalid node type, required only Element" );
						return;
					}
				}
				listNode.setModel( model );
			}
			
		} catch( Throwable exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't use xpath ? [" + exc.getMessage() + "]" );
		}
		
	}

	private void merge( String newContainer ) {
		List<Node> nodes = listNode.getSelectedValuesList();		
		if ( nodes == null || nodes.size() == 0 )
			EditixFactory.buildAndShowWarningDialog( "No selected node" );
		else {
			// Resolve xpath for DOM
			List<String> xpaths = new ArrayList<String>();
			for ( Node n : nodes ) {
				if ( n instanceof FPNode ) {
					xpaths.add( ( ( FPNode )n ).getXPathLocation() );
				}
			}
			
			XMLContainer container = EditixFrame.THIS
					.getSelectedSubContainer("XML");
			if (container == null)
				container = EditixFrame.THIS.getSelectedContainer();			
			
			String rawXML = container.getText();

			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse( new InputSource( new StringReader( rawXML) ) );
				XPath xpath = XPathFactory.newInstance().newXPath();
				List<Element> nodesToMerge = new ArrayList<Element>();
				for ( String xpathStr : xpaths ) {
					Element e = ( Element )xpath.evaluate( xpathStr, doc, XPathConstants.NODE );
					nodesToMerge.add( e );
				}
				mergeNodes( newContainer, container, doc, nodesToMerge );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Error found [" + exc.getMessage() + "] ?" );
			}
		}
	}

	private void mergeNodes( String newContainer, XMLContainer container, Document doc, List<Element> nodes ) {		
		Element refNode = nodes.get( 0 );
		int start = 1;
		
		if ( newContainer != null ) {
			refNode = doc.createElement( newContainer );
			nodes.get( 0 ).getParentNode().insertBefore( refNode, nodes.get( 0 ) );
			start = 0;
		}

		for ( int i = start; i < nodes.size(); i++ ) {
			Element node = nodes.get( i );
			NodeList nl = node.getChildNodes();
			for ( int j = 0; j < nl.getLength(); j++ ) {
				refNode.appendChild( nl.item( j ).cloneNode( true ) );
			}
			node.getParentNode().removeChild( node );
		}

		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			transformer.transform(
				    new DOMSource(doc),
				    new StreamResult(sw) );
			String xml = sw.toString();
			container.setText( xml );
			JobManager.addJob( new JobAdapter() {
				@Override
				public void run() {
					runXPath( false );
				}
			});
			
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't generate output document [" + exc.getMessage() + "]?" );
		}
		
	}

	private Node getNodeFromRoot() throws Throwable {
		XMLContainer container = EditixFrame.THIS
				.getSelectedSubContainer("XML");
		if (container == null)
			container = EditixFrame.THIS.getSelectedContainer();
		if (container == null)
			throw new RuntimeException("No selected document. Open a document");
		currentFileLocation = container.getCurrentDocumentLocation();
		String document = container.getText();
		FPParser p = new FPParser();
		p.setNodeFactory(new DomNodeFactory());	
		com.japisoft.framework.xml.parser.document.Document d = p.parse(new StringReader(document));
		return ( Node )d.getRoot();
	}

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		f.getContentPane().add( new MergerPanel() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}
	
}