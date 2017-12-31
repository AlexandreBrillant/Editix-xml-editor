package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

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
public class ProcessingInstructionImpl
	extends NodeImpl
	implements ProcessingInstruction {
	public ProcessingInstructionImpl() {
		super();
	}

	/**
	  * The target of this processing instruction. XML defines this as being the 
	  * first token following the markup that begins the processing instruction.
	  */
	public String getTarget() {
		throw new RuntimeException("Not supported");
	}

	/**
	 * The content of this processing instruction. This is from the first non 
	 * white space character after the target to the character immediately 
	 * preceding the <code>?&gt;</code>.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
	 */
	public String getData() {
		throw new RuntimeException("Not supported");
	}

	public void setData(String data) throws DOMException {
		throw new RuntimeException("Not supported");
	}

}

// ProcessingInstructionImpl ends here
