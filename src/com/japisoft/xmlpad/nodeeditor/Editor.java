package com.japisoft.xmlpad.nodeeditor;

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
public interface Editor {

	/** @return true when this editor accepts to edit this node */
	public boolean accept( FPNode node );
	/** Edit a particular text context. This context contains the current
	 * text and node to edit. You must call the <code>setResult</code> once
	 * you terminate the custom editing part. If you don't want to edit it, just
	 * write a <code>null</code> result.
	 * @param context Editing context
	 */
	public void edit( EditorContext context );

}
