package com.japisoft.xmlpad.helper.handler;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.model.AbstractDescriptor;
import com.japisoft.xmlpad.helper.model.Descriptor;
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
/** Common class for building an helper */
public abstract class AbstractHelperHandler {
	protected ArrayList descriptors = null;

	public void setOverrideDescriptors( ArrayList descriptors ) {
		this.descriptors = descriptors;
	}
	/**
	 * This is activated each time the user insert a special key (like ctrl-space)
	 * or for some special inserted caracters like '<'...
	 * @return the part of the content assistant */
	public Descriptor[] resolveContentAssistant(
			FPNode currentNode,
			XMLPadDocument document,
			boolean insertBefore,
			int offset, 
			String activatorString ) {

		if ( descriptors == null )
			descriptors = new ArrayList();
		else
			descriptors.removeAll( descriptors );

		String tmp = ( activatorString == null ? 
				getActivatorSequence() : activatorString );
		installDescriptors( currentNode, document, offset, tmp );

		if ( descriptors != null && 
				descriptors.size() > 0 ) {
			Descriptor[] d = new Descriptor[ descriptors.size() ];
			for ( int i = 0; i < descriptors.size(); i++ ) {
				d[ i ] = ( Descriptor )descriptors.get( i );
				if ( d[ i ] instanceof AbstractDescriptor ) {
					if ( d[ i ].getIcon() == null )
						( ( AbstractDescriptor )d[ i ] ).setIcon( getDefaultIcon() );
				}
			}
			return d;
		}

		return null;
	}

	protected String getActivatorSequence() { return null; } 

	protected Icon getDefaultIcon() { return null; }
	
	protected boolean match( 
			XMLPadDocument document, 
			int offset,
			String addedPart, 
			String mustMatch ) {

		// For ctrl space case
		if ( addedPart == null )
			return true;

		// Add previous charts until the tmp size if the mustMatch size
		int extractSize = mustMatch.length() - 
			( addedPart != null ? addedPart.length() : 0 );
		try {
			String content = document.getText( 
					offset - extractSize, 
					extractSize );

			if ( mustMatch.equals( content + 
					( addedPart == null ? "" : addedPart ) ) )
				return true;
		} catch (BadLocationException e) {
		}
		return false;
	}

	public abstract boolean haveDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset, 
			String activatorString );
	
	public String getTitle() {
		return null;
	}

	protected abstract void installDescriptors( FPNode currentNode, XMLPadDocument document, 
			int offset,
			String activatorString );

	protected Descriptor addDescriptor( Descriptor d ) {
		if ( descriptors == null )
			descriptors = new ArrayList();
		if ( !descriptors.contains( d ) ) {
			descriptors.add( d );
		}
		return d;
	}

	protected Descriptor addOrderedDescriptor( Descriptor d ) {
		if ( descriptors == null )
			descriptors = new ArrayList();
		if ( !descriptors.contains( d ) ) {
			boolean added = false;
			for ( int i = 0; i < descriptors.size(); i++ ) {
				Descriptor d2 = ( Descriptor )descriptors.get( i );
				String name2 = d2.getName();
				if ( name2.compareTo( d.getName() ) > 0 ) {
					descriptors.add( i, d );
					added = true;
					break;
				}
			}
			if ( !added )
				descriptors.add( d );
		}
		return d;
	}

	protected void removeDescriptor( Descriptor d ) {
		if ( descriptors != null )
			descriptors.remove( d );
	}
	
	protected void flushDescriptors() {
		descriptors = null;
	}

	public void dispose() {
		descriptors = null;
	}

	public String getName() {
		return getClass().getName();
	}
	
	public String getType() {
		return null;
	}
	
	/** Give information if this helper can managed the result inserting. Only for particular case like attribute value */
	public boolean hasDelegateForInsertingResult() {
		return false;
	}

	/** Only if the <code>hasDelegateForInsertingResult</code> is <code>true</code> */
	public void insertResult( XMLPadDocument document, int offset, String result ) {
	}

	/** If the assistant must be called a a job for being synchronized with parsing... */
	public boolean mustBeJobSynchronized() { return false; }

	private boolean enabled = true;
	public boolean isEnabled() { return enabled; }
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	/** Here a way to change the order of the handler */
	public int getPriority() { return -1; }
}

