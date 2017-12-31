package com.japisoft.editix.editor.xsd.view.element.simpletype;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.View;

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
public class SimpleTypeViewImpl extends JPanel implements View, ItemListener {

	private JComboBox cb = new JComboBox(
			new String[] {
				"None",
				"Restriction",
				"List",
				"Union"
			} );
	private ContentPanel content;
	private Factory factory = null;

	public SimpleTypeViewImpl( Factory factory ) {
		this.factory = factory;
		this.content = new ContentPanel( factory );
		setLayout( new BorderLayout() );
		add( cb, BorderLayout.NORTH );
		add( content, BorderLayout.CENTER );
	}

	public void addNotify() {
		super.addNotify();
		cb.addItemListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		cb.removeItemListener( this );
	}

	private Element initE;
	
	public void init( Element schemaNode ) {
		this.initE = schemaNode;
		content.init( schemaNode );
		if ( schemaNode != null ) {
			cb.removeItemListener( this );
			Element parent = schemaNode;
			// Switch to the right content
			if ( SchemaHelper.hasChild( schemaNode, "simpleType" ) ) {
				parent = SchemaHelper.getChildAt( parent, 0, new String[] { "simpleType"} );
				if ( parent == null ) {
					parent = schemaNode;
				}
			}
			if ( SchemaHelper.hasChild( parent, "restriction" ) ) {
				content.show( "Restriction" );
				cb.setSelectedItem( "Restriction" );
			} else
			if ( SchemaHelper.hasChild( parent, "list" ) ) {				
				content.show( "List" );
				cb.setSelectedItem( "List" );
			} else
			if ( SchemaHelper.hasChild( parent, "union" ) ) {
				content.show( "Union" );
				cb.setSelectedItem( "Union" );
			} else {
				content.show( "None" );
				cb.setSelectedItem( "None" );
			}
			cb.addItemListener( this );
		} 
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		this.initE = null;
		content.dispose();
	}
	public void stopEditing() {}

	public void itemStateChanged( ItemEvent e ) {
		if ( cb.getSelectedIndex() == 0 ) {
			// Remove all simpleType
			NodeList nl = initE.getChildNodes();
			for ( int i = 0; i < nl.getLength(); i++ ) {
				Node n = nl.item( i );
				if ( n instanceof Element ) {
					Element _ = ( Element )n;
					if ( "simpleType".equals( _.getLocalName() ) )
						initE.removeChild( _ );
				}
			}
		}
		content.show( ( String )e.getItem() );
	}

	class ContentPanel extends JPanel {
		public ContentPanel( Factory factory ) {
			setLayout( new CardLayout() );
			add( new JPanel(), "None" );
			add( new JScrollPane( new RestrictionViewImpl( factory ) ), "Restriction" );
			add( new JScrollPane( new ListViewImpl().getView() ), "List" );
			add( new JScrollPane( new UnionViewImpl( factory ) ), "Union" );
		}
		public void show( String name ) {
			( ( CardLayout )getLayout() ).show( this, name );
		}

		public void init( Element schemaNode ) {
			for ( int i = 0; i < getComponentCount(); i++ ) {
				JComponent cc = ( JComponent )getComponent( i );
				if ( cc instanceof JScrollPane ) {
					cc = ( JComponent )( ( JScrollPane )cc ).getViewport().getView();
				}
				if ( cc instanceof View ) {
					( ( View )cc ).init( schemaNode );
				}
			}
		}
		public void dispose() {
			for ( int i = 0; i < getComponentCount(); i++ ) {
				JComponent cc = ( JComponent )getComponent( i );
				if ( cc instanceof JScrollPane ) {
					cc = ( JComponent )( ( JScrollPane )cc ).getViewport().getView();
				}
				if ( cc instanceof View ) {
					( ( View )cc ).dispose();
				}				
			}			
		}
	}

	@Override
	public void cut() {
	}

	@Override
	public void copy() {
	}

	@Override
	public void paste() {
	}
	
}
