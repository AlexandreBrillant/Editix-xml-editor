package com.japisoft.xmlpad.helper.model;

import java.util.Vector;

import javax.swing.DefaultListModel;

import com.japisoft.framework.xml.parser.node.FPNode;
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
public abstract class AbstractEntityHelper extends AbstractHelper implements EntityHelper {

	private Vector entities = null;
		
	public AbstractEntityHelper() {
		initDefaultEntities();
	}
	
	public String getTitle() {
		return "Entities";
	}
	
	public boolean hasTitle() {
		return true;
	}
	
	protected boolean hasElements() {
		return ( entities != null ) && ( entities.size() > 0 );
	}

	protected void fillList( FPNode node, DefaultListModel model ) {
		if ( entities == null )
			return;
		for ( int i = 0; i < entities.size(); i++ )
			model.addElement( entities.get( i ) );
	}

	/** Reset gt,lt,quot,apos entities */
	protected void initDefaultEntities() {
		addEntity( "gt", ">" );
		addEntity( "lt", "<" );
		addEntity( "quot", "\"" );
		addEntity( "amp", "&" );
		addEntity( "apos", "\'" );		
		addEntity( "#10", "Line break" );
	}

	/** Add a new entry */
	public void addEntityDescriptor( EntityDescriptor entity ) {
		if ( entities == null )
			entities = new Vector();
		entities.add( entity );
	}

	public void addEntity( String name, String value ) {
		addEntityDescriptor( new EntityDescriptor( name, value ) );
		cache = null;
	}

	private EntityDescriptor[] cache = null;

	/* (non-Javadoc)
	 * @see com.japisoft.xmlpad.helper.EntityHelper#getEntities()
	 */
	public EntityDescriptor[] getEntities() {
		if ( entities == null )
			return null;
		if ( cache != null )
			return cache;
		EntityDescriptor[] d = new EntityDescriptor[ entities.size() ];
		entities.copyInto( d );
		cache = d;
		return d;
	}
	

	protected String getLostCharacter() {
		return "&";
	}	
}
