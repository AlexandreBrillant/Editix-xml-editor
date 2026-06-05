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

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is a toolkit for navigating through your XML tree easily.
 * is is able to return you a list of tag by name...
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
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
	public Iterator getTagNodeByName(String name, boolean deep) {
		return getCriteriaResult(new NodeNameCriteria(name), deep, false);
	}

	/** Search for the first node */
	public FPNode getFirstTagNodeByName( String name, boolean deep ) {
		Iterator enume = getTagNodeByName( name, deep );
		if ( enume.hasNext() )
			return ( FPNode )enume.next();
		return null;
	}

	/** @return all text node containing the subcontent */
	public Iterator getTextNode(String subcontent, boolean deep) {
		return getCriteriaResult(new TextCriteria(subcontent), deep, false);
	}

	/** 
	 * Sample of criteria :
	 * <code>new OrCriteria( new NodeNameCriteria( "aa" ), new NodeNameCriteria( "bb" ) )</code> for
	 * returning 'aa' or 'bb' node.
	 *  @param vc Criteria for searching node 
	 *  @param deep deeply found tag
	 *  @return SimpleNode enumeration */
	public Iterator getNodeByCriteria(ValidCriteria vc, boolean deep) {
		return getCriteriaResult(vc, deep, false);
	}

	public FPNode getOneNodeByCriteria(ValidCriteria vc, boolean deep) {
		Iterator enume = getNodeByCriteria( vc, deep );
		if ( enume.hasNext() )
			return ( FPNode )enume.next();
		return null;
	}

	public Iterator getNodeByCriteria(ValidCriteria vc, boolean deep, boolean stopAtFirst ) {
		return getCriteriaResult( vc, deep, stopAtFirst );
	}

	private Iterator getCriteriaResult(ValidCriteria vc, boolean deep, boolean stopAtFirst ) {
		ArrayList v = new ArrayList();
		matchTag(vc, v, node, deep,false);
		return v.iterator();
	}

	// Browser the XML tree
	private void matchTag(
		ValidCriteria vc,
		List v,
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


