package com.japisoft.xmlpad.helper.model;

import javax.swing.DefaultListModel;

import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobAdapter;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class AttributeHelper extends AbstractHelper {

	private TagDescriptor helper = null;

	public AttributeHelper( TagDescriptor helper ) {
		this.helper = helper;
	}

	/** Wait for a tag descriptor */
	public void prepare( Object loc ) {
		helper = ( TagDescriptor ) loc;
	}

	public String getTitle() {
		return helper.getName();
	}

	protected boolean hasElements() {
		return helper != null && helper.getAtts() != null
				&& helper.getAtts().length > 0;
	}

	public AttDescriptor[] getAttributes( FPNode node ) {
		if ( helper == null )
			return null;

		AttDescriptor[] atts = helper.getAtts();
		if ( atts != null ) {
			// Disable known attributes

			if ( node != null ) {
				for ( int i = 0; i < atts.length; i++ ) {
					AttDescriptor _ = atts[i];
					String name = _.getName();
					if ( node.hasAttribute( name ) )
						_.setEnabled( false );
					else
						_.setEnabled( true );
				}
			}
		}
		return atts;
	}
	
	public AttDescriptor getAttribute( String name ) {
		AttDescriptor[] atts = helper.getAtts();
		if ( atts != null ) {
			for ( int i = 0; i < atts.length; i++ ) {
				if ( name.equals( atts[ i ].getName() ) )
					return atts[ i ];
			}
		}
		return null;
	}

	protected void insertResult( 
			XMLPadDocument doc, 
			int offset, 
			String result,
			Descriptor descriptor, String added ) {

		AttDescriptor att = ( AttDescriptor )descriptor;
		
		String name = att.getName();
		if ( added != null )
			name = name.substring( added.length() );
				
		doc.insertStringWithoutHelper( 
				offset, 
				name,
				null );

		offset += name.length();

		doc.insertStringWithoutHelper( offset, "=", null );
		offset++;
		int storedOffset = 0;
		if ( att.hasEnumValues() ) {
			storedOffset = offset;
		}
		if ( !att.hasEnumValues() )
			doc.insertStringWithoutHelper( offset, "\"" + att.getDefaultValue() + "\"", null );
		else
			doc.insertStringWithoutHelper( offset, "\"" + "\"", null );

		if ( storedOffset > 0 ) {
			final int ii = storedOffset;
			final XMLPadDocument doc2 = doc;
			Job newJob = new JobAdapter() {
				public void run() {
					doc2.manageCompletion( false, ii, "\"" );
				}
			};
			JobManager.addJob( newJob );
		}
	}
	
	protected void fillList( FPNode node, DefaultListModel model ) {
		AttDescriptor[] as = getAttributes(node);
		all: for (int i = 0; i < as.length; i++) {
			for (int j = 0; j < model.getSize(); j++) {
				AttDescriptor ad = ( AttDescriptor ) model.get(j);
				if (ad.getName() == null)
					continue all;
				if ( ad.getName().compareTo( as[ i ].getName() ) > 0 ) {
					model.insertElementAt( as[ i ], j );
					continue all;
				}
			}
			model.addElement( as[ i ] );
		}
	}

}
