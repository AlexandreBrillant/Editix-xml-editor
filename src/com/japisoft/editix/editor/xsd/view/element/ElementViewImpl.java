package com.japisoft.editix.editor.xsd.view.element;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view.element.simpletype.SimpleTypeViewImpl;

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
public class ElementViewImpl extends JPanel implements View {	
	private JTabbedPane tp = new JTabbedPane( JTabbedPane.BOTTOM );
	private PropertiesViewImpl av = new PropertiesViewImpl(); 
	private SimpleTypeViewImpl stv = null;

	private Factory factory = null;
	
	public ElementViewImpl( 
			Factory factory, 
			PropertiesViewListener listener ) {
		stv = new SimpleTypeViewImpl( factory );
		setLayout( new BorderLayout() );
		add( tp );
		tp.addTab( "Properties", 
				new JScrollPane( av.getView(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) );
		tp.addTab( "Simple type", 
				stv.getView() );
		av.setPropertiesViewListener( listener );
	}

	public void setDesignerMode( boolean designerMode ) {
		av.setDesignerMode( designerMode );
	}	

	public boolean isDesignerMode() {
		return av.isDesignerMode();
	}
	
	public void init( Element schemaNode ) {
		av.init( schemaNode );
		stv.init( schemaNode );
		
		if ( schemaNode != null ) {
			tp.setEnabledAt( 0, true );
			tp.setEnabledAt( 1, "element".equals( schemaNode.getLocalName() )
					|| "simpleType".equals( schemaNode.getLocalName() )
						|| "attribute".equals( schemaNode.getLocalName() ) );
			if ( !tp.isEnabledAt( 1 ) ) {
				if ( tp.getSelectedIndex() == 1 )
					tp.setSelectedIndex( 0 );
			}
			
			// Check for complexType case
			if ( "element".equals( schemaNode.getLocalName() ) ) {
				if ( SchemaHelper.hasChild( schemaNode, "complexType" ) ) {
					tp.setEnabledAt( 1, false );
				}
			}

		} else {
			tp.setSelectedIndex( 0 );
			tp.setEnabledAt( 0, false );
			tp.setEnabledAt( 1, false );
		}
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		av.dispose();
		stv.dispose();
	}

	public void stopEditing() {		
		av.stopEditing();
	}
	
	@Override
	public void copy() {
	}
	@Override
	public void cut() {
	}
	@Override
	public void paste() {
	}
	
}
