package com.japisoft.xmlpad.helper.model;

import java.util.ArrayList;

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
public class TagDescriptor extends AbstractDescriptor {
	public String name;
	private String nameHelper;
	private AttDescriptor[] atts;
	public boolean empty;
	private boolean raw;
	private boolean enabled = true;
	private int maxOcc = -1;
	private int minOcc = -1;
	private boolean choice = false;
	public String namespace = null;
	
	public TagDescriptor( 
			String name,
			boolean empty ) {
		this.name = name;
		this.empty = empty;
	}

	public TagDescriptor( 
			String name,
			String raw ) {
		this( name, false );
		this.raw = true;
		this.rawContent = raw;
	}
		
	public TagDescriptor( 
			String name, 
			AttDescriptor[] atts, 
			boolean empty ) {
		this.name = name;
		this.atts = atts;
		this.empty = empty;
	}
	
	public TagDescriptor( 
			String name, 
			AttDescriptor[] atts, 
			boolean empty, 
			boolean raw ) {
		this.name = name;
		this.atts = atts;
		this.empty = empty;
		this.raw = raw;
	}

	public TagDescriptor( 
			String name, 
			String nameHelper, 
			AttDescriptor[] atts, 
			boolean empty, 
			boolean raw ) {
		this.name = name;
		this.nameHelper = nameHelper;
		this.atts = atts;
		this.empty = empty;
		this.raw = raw;
	}

	public void setAttDescriptor( AttDescriptor[] atts ) {
		this.atts = atts;
	}
	
	public void setEmpty( boolean empty ) {
		this.empty = empty;
	}
	
	public void setMaxOcc( int occ ) {
		this.maxOcc = occ;
	}
	
	public int getMacOcc() {
		return maxOcc;
	}
	
	public void setMinOcc( int occ ) {
		this.minOcc = occ;
	}
	
	public int getMinOcc() {
		return minOcc;
	}

	public void setChoice( boolean choice ) {
		this.choice = choice;
	}
	
	public boolean isChoice() {
		return choice;
	}
	
	public String getName() { return name; }
	
	public void setName( String name ) { this.name = name; }
	public void setNameHelper( String nameHelper ) {
		this.nameHelper = nameHelper;
	}
	
	public String getNameForHelper() {
		if ( nameHelper == null ) {
			nameHelper = getName().replace( '¤', ' ' );
			if  ( nameHelper.length() > 40 ) {
				nameHelper = nameHelper.substring( 0, 40 ) + "...";
			}
		}
		return nameHelper;
	}

	public AttDescriptor[] getRequiredAtt() { return atts; }
	public AttDescriptor[] getAtts() { return atts; }

	public void addAttDescriptor( AttDescriptor ad ) {
		if ( atts == null )
			atts = new AttDescriptor[] { ad };
		else {
			AttDescriptor[] oldAtts = atts;
			atts = new AttDescriptor[ oldAtts.length + 1 ];
			System.arraycopy( oldAtts, 0, atts, 0, oldAtts.length );
			atts[ oldAtts.length ] = ad;
		}
	}

	public boolean isEmpty() { return empty; }
	public boolean isRaw() { return raw; }
	public boolean isEnabled() { return enabled; }

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
		for  ( int i = 0; i < getSynonymousTagDescriptorCount(); i++ ) {
			getSynonymousTagDescriptor( i ).setEnabled( enabled );
		}
	}

	public String getStartingTag() {
		StringBuffer sb = new StringBuffer( "<" );
		sb.append( name );
		if ( atts != null ) {
			for ( int i = 0; i < atts.length; i++ ) {
				if ( atts[ i ].isRequired() ) {
					sb.append( " ");
					sb.append( atts[ i ].getName() );
					sb.append( "=\"" );
					sb.append( atts[ i ].getDefaultValue() );
					sb.append( "\"" );
				}
			}
		}
		sb.append( ">" );		
		return sb.toString();
	}
	
	public String getEndingTag() {
		StringBuffer sb = new StringBuffer( "</" ).append( name ).append( ">" );
		return sb.toString();
	}
	
	private String endAddedPart;
	
	public void setEndAddedPart( String endAddedPart ) {
		this.endAddedPart = endAddedPart;
	}

	private String rawContent = null;
	
	public void setRawContent( String rawContent ) {
		this.rawContent = rawContent;
	}
	
	public String toExternalForm() {

		if ( rawContent != null ) {
			return rawContent;
		}
		
		StringBuffer sb = new StringBuffer();
		if ( addedPart != null )
			sb.append( addedPart );
		sb.append( name );

		boolean cursorAdded = false;
		
		if ( !raw ) {
		
			if ( atts != null ) {
				for ( int i = 0; i < atts.length; i++ ) {
					if ( atts[ i ].isRequired() ) {
						sb.append( " ");
						sb.append( atts[ i ].getName() );
						sb.append( "=\"" );
						if ( !cursorAdded ) {
							cursorAdded = true;
							sb.append( "¤" );
						}
						sb.append( atts[ i ].getDefaultValue() );
						sb.append( "\"" );
					}
				}
			}
			if ( empty )
				sb.append( "/>");
			else {
				sb.append( ">" );
				if ( !cursorAdded ) {
					sb.append( "¤" );
				}
				sb.append( "</").append( endAddedPart == null ? "" : endAddedPart ).append( name ).append( ">" );
			}
		
		}
		
		return sb.toString();	
	}
	
	public String getBuiltTag() {				
		return "<" + toExternalForm();
	}

	public String toString() {
		return getNameForHelper();
	}

	public boolean equals( Object o ) {
		if ( o instanceof TagDescriptor ) {
			TagDescriptor td = ( TagDescriptor )o;
			return td.toString() != null
				&& td.toString().equals( toString() );
		} else
			return super.equals( o );
	}
	
	private ArrayList secondaryTagDescriptor = null;

	// For the substitutionGroup feature
	public void addSynonymousTagDescriptor( TagDescriptor td ) {
		if ( secondaryTagDescriptor == null )
			secondaryTagDescriptor = new ArrayList();
		secondaryTagDescriptor.add( td );
	}

	public int getSynonymousTagDescriptorCount() {
		if ( secondaryTagDescriptor == null )
			return 0;
		return secondaryTagDescriptor.size();
	}

	public TagDescriptor getSynonymousTagDescriptor( int i ) {
		if ( secondaryTagDescriptor != null )
			return ( TagDescriptor )secondaryTagDescriptor.get( i );
		return null;
	}

}
