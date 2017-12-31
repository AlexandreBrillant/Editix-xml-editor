package com.japisoft.editix.document.xslt;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

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
public class XPath1Handler extends AbstractHelperHandler {

	protected String getActivatorSequence() {
		return null;
	}

	public String getTitle() {
		return "XPath 1 Functions";
	}

	private static String[] functions = new
		String[] {

		"boolean(object)",
		"The boolean function converts its argument to a boolean",

		"ceiling(number)",
		"The ceiling function returns the smallest (closest to negative infinity) number that is not less than the argument and that is an integer",
		
		"concat(string, string, string*)",
		"The concat function returns the concatenation of its arguments",
				
		"contains(string, string)",
		"The contains function returns true if the first argument string contains the second argument string, and otherwise returns false",

		"count(node-set)",
		"The count function returns the number of nodes in the argument node-set.",

		"current()",
		"It returns a node-set that has the current node as its only member",
		
		"generate-id(node-set?)",
		"It returns a string that uniquely identifies the node in the argument node-set that is first in document order",

		"false()",
		"",

		"floor(number)",
		"The floor function returns the largest (closest to positive infinity) number that is not greater than the argument and that is an integer",
				
		"id(object)",
		"The id function selects elements by their unique ID",

		"lang(string)",
		"The lang function returns true or false depending on whether the language of the context node as specified by xml:lang attributes is the same as or is a sublanguage of the language specified by the argument string",
		
		"last()",
		"The last function returns a number equal to the context size from the expression evaluation context.",
		
		"local-name(node-set?)",
		"The local-name function returns the local part of the expanded-name of the node in the argument node-set that is first in document order",
		
		"name(node-set?)",
		"The name function returns a string containing a QName representing the expanded-name of the node in the argument node-set that is first in document order",
				
		"namespace-uri(node-set?)",
		"The namespace-uri function returns the namespace URI of the expanded-name of the node in the argument node-set that is first in document order",

		"normalize-space(string?)",
		"The normalize-space function returns the argument string with whitespace normalized by stripping leading and trailing whitespace and replacing sequences of whitespace characters by a single space",

		"not(boolean)",
		"The not function returns true if its argument is false, and false otherwise",

		"number(object?)",
		"The number function converts its argument to a number",
				
		"position()",
		"The position function returns a number equal to the context position from the expression evaluation context.",

		"round(number)",
		"The round function returns the number that is closest to the argument and that is an integer",
				
		"starts-with(string, string)",
		"The starts-with function returns true if the first argument string starts with the second argument string, and otherwise returns false",
			
		"string(object?)",
		"The string function converts an object to a string",
		
		"string-length(string?)",
		"The string-length returns the number of characters in the string",
				
		"substring(string, number, number?)",
		"The substring function returns the substring of the first argument starting at the position specified in the second argument with length specified in the third argument",
						
		"substring-after(string, string)",
		"The substring-after function returns the substring of the first argument string that follows the first occurrence of the second argument string in the first argument string, or the empty string if the first argument string does not contain the second argument string",
						
		"substring-before(string, string)",
		"The substring-before function returns the substring of the first argument string that precedes the first occurrence of the second argument string in the first argument string, or the empty string if the first argument string does not contain the second argument string",

		"sum(node-set)",
		"The sum function returns the sum, for each node in the argument node-set, of the result of converting the string-values of the node to a number",
			
		"system-property(string)",
		"It returns an object representing the value of the system property identified by the name",
		
		"translate(string, string, string)",
		"The translate function returns the first argument string with occurrences of characters in the second argument string replaced by the character at the corresponding position in the third argument string",

		"true()",
		"",
		
		"unparsed-entity-uri(string)",
		"It returns the URI of the unparsed entity with the specified name in the same document as the context node "
		
		};

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		return activatorString == null && document.isInsideAttributeValue( offset );
	}
	
	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document,
			int offset, 
			String activatorString ) {

		String[] raws = functions;
		for ( int i = 0; i < raws.length; i += 2 ) {
			BasicDescriptor rs = ( BasicDescriptor )addDescriptor( 
					new BasicDescriptor( 
							raws[ i ] ) );
			rs.setComment( raws[ i + 1 ] );
		}
		
	}

}

