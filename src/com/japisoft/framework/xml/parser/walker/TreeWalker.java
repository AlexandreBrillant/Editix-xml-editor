package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.node.*;

import java.util.Enumeration;
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
public class TreeWalker {
	private FPNode node;

	/** Reference node. It is illegal to use a <code>null</code> node */
	public TreeWalker(FPNode node) {
		super();
		this.node = node;
		if ( node == null )
			throw new RuntimeException("Illegal null node");
	}

	/** @param tag to find
	    @param deep deeply found tag
	@return a list of tag mathing the name */
	public Enumeration getTagNodeByName(String name, boolean deep) {
		return getCriteriaResult(new NodeNameCriteria(name), deep, false);
	}

	/** Search for the first node */
	public FPNode getFirstTagNodeByName( String name, boolean deep ) {
		Enumeration enume = getTagNodeByName( name, deep );
		if ( enume.hasMoreElements() )
			return ( FPNode )enume.nextElement();
		return null;
	}

	/** @return all text node containing the subcontent */
	public Enumeration getTextNode(String subcontent, boolean deep) {
		return getCriteriaResult(new TextCriteria(subcontent), deep, false);
	}

	/** 
	 * Sample of criteria :
	 * <code>new OrCriteria( new NodeNameCriteria( "aa" ), new NodeNameCriteria( "bb" ) )</code> for
	 * returning 'aa' or 'bb' node.
	 *  @param vc Criteria for searching node 
	 *  @param deep deeply found tag
	 *  @return SimpleNode enumeration */
	public Enumeration getNodeByCriteria(ValidCriteria vc, boolean deep) {
		return getCriteriaResult(vc, deep, false);
	}

	public FPNode getOneNodeByCriteria(ValidCriteria vc, boolean deep) {
		Enumeration enume = getNodeByCriteria( vc, deep );
		if ( enume.hasMoreElements() )
			return ( FPNode )enume.nextElement();
		return null;
	}

	public Enumeration getNodeByCriteria(ValidCriteria vc, boolean deep, boolean stopAtFirst ) {
		return getCriteriaResult( vc, deep, stopAtFirst );
	}

	private Enumeration getCriteriaResult(ValidCriteria vc, boolean deep, boolean stopAtFirst ) {
		FastVector v = new FastVector();
//		if ( vc.isValid(  node ) )
//			v.add( node );	
		matchTag(vc, v, node, deep,false);
		return v.elements();
	}

	// Browser the XML tree
	private void matchTag(
		ValidCriteria vc,
		FastVector v,
		FPNode node,
		boolean deep,
		boolean stopAtFirst ) {
		for (int i = 0; i < node.childCount(); i++) {
			FPNode n = node.childAt(i);
			if (vc.isValid(n)) {
				v.add(n);
				if ( stopAtFirst )
					return;
			}
			if (deep && !n.isLeaf() ) {
				matchTag(vc, v, n, deep,stopAtFirst);
			}
		}
		
	}

}

// TreeWalker ends here
