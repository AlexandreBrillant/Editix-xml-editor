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

package com.japisoft.xmlpad.helper.handler.schema.dtd;

import java.util.ArrayList;

import com.japisoft.dtdparser.node.AttributeDTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DTDTagDescriptor extends TagDescriptor {

	public DTDTagDescriptor( ElementDTDNode ref ) {

		super( ref.getName(), null, ref.isEmptyElement() );
		
		setComment( ref.getNodeComment() );
		
		ArrayList atts = null;
		for ( int i = 0; i < ref.getDTDNodeCount(); i++ ) {
			
			if ( ref.getDTDNodeAt( i ).isAttribute() ) {
				AttributeDTDNode attNode = ( AttributeDTDNode )ref.getDTDNodeAt( i );
				if ( atts == null )
					atts = new ArrayList();
				atts.add( new DTDAttDescriptor( attNode ) );
			}

		}

		if ( atts != null ) {
			
			AttDescriptor[] _ = new AttDescriptor[ atts.size() ];
			for ( int i = 0; i < atts.size(); i++ )
				_[ i ] = ( AttDescriptor )atts.get( i );
			setAttDescriptor( _ );

		}
	}
	
	
	
}

