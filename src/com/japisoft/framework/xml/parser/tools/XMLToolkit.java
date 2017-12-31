package com.japisoft.framework.xml.parser.tools;

import java.util.Vector;

import javax.swing.tree.TreePath;
import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.document.Document;
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
public final class XMLToolkit {

	/**
	 * @param doc
	 *            the current XML document
	 * @param line
	 *            the current line
	 * @return the best node for a line. It will work only for a flatView mode
	 */
	public static FPNode getNodeForLine(Document doc, int line) {
		FastVector v;
		if ((v = doc.getFlatNodes()) == null)
			throw new RuntimeException(
					"Invalid usage for getNodeForLine : No flatView mode ");

		int i = 0;
		FPNode r = null;

		while (true) {
			FPNode n = (FPNode) v.get(i);
			if (n.getStartingLine() > line)
				break;
			if (line >= n.getStartingLine() && line <= n.getStoppingLine())
				r = n;
			else {
			}
			i++;
			if (i >= v.size()) {
				if (r == null)
					r = n;
				break;
			}
		}
		return r;
	}

	/**
	 * @param doc
	 *            the current XML document
	 * @param line
	 *            the document offset
	 * @return the best node for a document location. It will work only for a
	 *         flatView mode
	 */
	public static FPNode getNodeForOffset(Document doc, int offset) {
		FastVector v;

		if (doc == null)
			return null;

		if ((v = doc.getFlatNodes()) == null) {
			throw new RuntimeException(
					"Invalid usage for getNodeForLine : No flatView mode ");
		}

		int i = 0;
		FPNode r = null;
		FPNode root = (FPNode) doc.getRoot();
		if (root.startingOffset > offset
				|| root.stoppingOffset < offset) {
			return null;
		}

		int delta = 0;
		
		while (true) {
			FPNode n = (FPNode) v.get(i);
			if (n.startingOffset > offset)
				break;
			
			if ( n.isText() )
				delta = 1;
			else
				delta = 0;
			if (offset > ( n.startingOffset - delta )
					&& offset <= n.stoppingOffset)
				r = n;
			else {
			}
			i++;
			if (i >= v.size()) {
				if (r == null)
					r = n;
				break;
			}
		}
		return r;
	}

	public static String[] getQualifiedPath(FPNode node) {
		if (node == null)
			return null;
		Vector v = new Vector();
		while (node != null) {
			v.insertElementAt(node.getQualifiedContent(), 0);
			node = node.getFPParent();
		}
		String[] r = new String[v.size()];
		v.copyInto(r);
		return r;
	}

	/** @return a swing tree path matching this node */
	public static TreePath getTreePath(FPNode n) {
		Vector v = new Vector();
		while (n != null) {
			v.insertElementAt(n, 0);
			n = n.getFPParent();
		}
		if (v.size() == 0)
			return null;
		return new TreePath(v.toArray());
	}

	/** @return the first child matching at least one tag from the parent */
	public static FPNode getMatchingNode(FPNode parent, String[] tags) {
		for (int i = 0; i < parent.childCount(); i++) {
			FPNode child = parent.childAt(i);
			if (child.isTag()) {
				String tag = child.getNodeContent();
				for (int j = 0; j < tags.length; j++) {
					if (tag.equals(tags[j])) {
						return child;
					}
				}
			}
		}
		return null;
	}

	public static String resolveCharEntities(String value) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '<')
				sb.append("&lt;");
			else if (c == '>')
				sb.append("&gt;");
			else if (c == '\"')
				sb.append("&quot;");
			else if (c == '\'')
				sb.append("&apos;");
			else if (c == '&'
					&& !(i + 1 < value.length() && value.charAt(i + 1) == '#'))
				sb.append("&amp;");
			else if (c == '\n')
				sb.append("&#10;");
			else
				sb.append(c);
		}
		return sb.toString();
	}

}

