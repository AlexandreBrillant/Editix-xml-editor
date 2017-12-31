package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.Transformer;

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
public abstract class AbstractTransformer implements Transformer {

	protected boolean sequenceMode = false;
	
	public void setSequenceMode( boolean sequenceMode ) {
		this.sequenceMode = sequenceMode;
	}

	public String transform(MetaNode root, List<MetaNode> nodeCollection) {
		Map<String,Boolean> processed = new HashMap<String,Boolean>();
		StringBuffer sb = new StringBuffer();
		initTransform( root, sb );
		for ( int i = 0; i < nodeCollection.size(); i++ ) {
			String name = ( ( MetaNode )nodeCollection.get( i ) ).getName();
			if ( name != null ) {
				if ( processed.containsKey( name ) )
					continue;
			}			
			generateMetaNode( (MetaNode)nodeCollection.get( i ), sb );
			if ( name != null ) {
				processed.put( name, Boolean.TRUE );
			}			
		}
		closeTransform( root, sb );
		return sb.toString();
	}

	protected void initTransform( MetaNode root, StringBuffer sb ) {}
	protected void generateMetaNode( MetaNode node, StringBuffer sb ) {};
	protected void closeTransform( MetaNode root, StringBuffer sb ) {}
	
}
