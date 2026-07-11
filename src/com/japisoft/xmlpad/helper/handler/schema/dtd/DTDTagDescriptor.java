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
			
			AttDescriptor[] __ = new AttDescriptor[ atts.size() ];
			for ( int i = 0; i < atts.size(); i++ )
				__[ i ] = ( AttDescriptor )atts.get( i );
			setAttDescriptor( __ );

		}
	}
	
	
	
}
