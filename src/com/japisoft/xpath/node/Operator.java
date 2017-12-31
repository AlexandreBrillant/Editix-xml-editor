// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.node;

import com.japisoft.xpath.XPathContext;
import com.japisoft.xpath.NodeSet;
import com.japisoft.xpath.XPathResolver;

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
public class Operator extends Expr {
	public Operator() {
		super();
	}

	private String operator;

	/** Set the operator */
	public void setOperator(String operator) {
		this.operator = operator;
		if ("div".equals(operator)) {
			predefinedOperator = XPathResolver.DIV;
		} else if ("mod".equals(operator)) {
			predefinedOperator = XPathResolver.MOD;
		}
	}

	public String getOperator() {
		return operator;
	}

	public String toString() {
		if (operator != null) {
			return operator;
		}
		return "" + predefinedOperator;
	}

	private int predefinedOperator;

	public void setPredefinedOperator(int op) {
		this.predefinedOperator = op;
	}

	public int getPredefinedOperator() {
		return predefinedOperator;
	}

	private boolean getBOperand1(XPathContext context) {
		Object obj = getNodeAt(0).eval(context);
		Boolean r = null;
		if (obj instanceof Boolean)
			r = (Boolean) obj;
		else if (obj instanceof NodeSet) {
			return (((NodeSet) obj).size() > 0);
		} else
			return false;
		return r.booleanValue();
	}

	private boolean getBOperand2(XPathContext context) {
		Object obj = getNodeAt(1).eval(context);
		Boolean r = null;
		if (obj instanceof Boolean)
			r = (Boolean) obj;
		else if (obj instanceof NodeSet) {
			return (((NodeSet) obj).size() > 0);
		} else
			return false;
		return r.booleanValue();

	}

	private double getDOperand1(XPathContext context) {
		Object o = getNodeAt(0).eval(context);
		if ( o instanceof Double ) {
			return ((Double)o).doubleValue();
		} else
		if ( o instanceof NodeSet ) {
			return context.convertNodeSetToDouble( ( NodeSet)o ).doubleValue();
		} else
		if ( o instanceof String ) {
			return new Double( o.toString() ).doubleValue();
		}
		return Double.MIN_VALUE;
	}

	private double getDOperand2(XPathContext context) {
		Object o = getNodeAt( 1 ).eval(context);
		if ( o instanceof Double ) {
			return ((Double)o).doubleValue();
		} else
		if ( o instanceof NodeSet ) {
			return context.convertNodeSetToDouble( ( NodeSet)o ).doubleValue();
		} else
		if ( o instanceof String ) {
			return new Double( o.toString() ).doubleValue();
		}
		return Double.MIN_VALUE;		
	}

	public Object getOperand1(XPathContext context) {
		Object obj = getNodeAt(0).eval(context);
		return obj;
	}

	public Object getOperand2(XPathContext context) {
		Object obj = getNodeAt(1).eval(context);
		return obj;
	}

	public NodeSet getNSOperand1(XPathContext context) {
		return (NodeSet) (getNodeAt(0).eval(context));
	}

	public NodeSet getNSOperand2(XPathContext context) {
		return (NodeSet) (getNodeAt(1).eval(context));
	}

	private Boolean equals(XPathContext context) {
		Object obj1 = getOperand1(context);
		Object obj2 = getOperand2(context);
		
		boolean isns1 = (obj1 instanceof NodeSet);
		boolean isns2 = (obj2 instanceof NodeSet);

		if (isns1 && isns2) {
			NodeSet ns1 = (NodeSet) obj1;
			NodeSet ns2 = (NodeSet) obj2;
			for (int i = 0; i < ns1.size(); i++) {
				Object o1 = ns1.elementAt(i);
				String s1 = context.getStringValue(o1);
				for (int j = 0; j < ns2.size(); j++) {
					Object o2 = ns2.elementAt(j);
					String s2 = context.getStringValue(o2);

					if (s1.equals(s2))
						return new Boolean(true);
				}
			}
			return new Boolean(false);
		} else if (isns1 || isns2) {
			NodeSet ns = (isns1) ? ((NodeSet) obj1) : ((NodeSet) obj2);
			Object obj = (isns1) ? obj2 : obj1;

			if ( obj instanceof Double ) {
				return new Boolean( obj.equals( context.convertNodeSetToDouble( ns ) ) );
			}

			for (int i = 0; i < ns.size(); i++) {
				String s1 = context.getStringValue(ns.elementAt(i));
				if (s1.equals(obj.toString()))
					return new Boolean(true);
			}
			return new Boolean(false);
		}

		return new Boolean(obj1.equals(obj2));
	}


	
	public Object eval(XPathContext context) {

		switch (predefinedOperator) {
		case XPathResolver.OR: {
			int location = context.getContextPosition();
			NodeSet ns = context.getContextNodeSet();
			boolean b1 = getBOperand1(context);
			context.setContextNodeSet(ns);
			context.setContextPosition(location);
			boolean b2 = getBOperand2(context);
			return new Boolean(b1 || b2);
		}
		case XPathResolver.AND: {
			int location = context.getContextPosition();
			NodeSet ns = context.getContextNodeSet();
			boolean b1 = getBOperand1(context);
			context.setContextNodeSet(ns);
			context.setContextPosition(location);
			boolean b2 = getBOperand2(context);
			return new Boolean(b1 && b2);
		}
		case XPathResolver.EQUAL:
			return equals(context);
		case XPathResolver.NOT_EQUAL:
			return new Boolean(!equals(context).booleanValue());
		case XPathResolver.INF:
			return new Boolean(getDOperand1(context) < getDOperand2(context));
		case XPathResolver.SUP:
			return new Boolean(getDOperand1(context) > getDOperand2(context));
		case XPathResolver.INFE:
			return new Boolean(getDOperand1(context) <= getDOperand2(context));
		case XPathResolver.SUPE:
			return new Boolean(getDOperand1(context) >= getDOperand2(context));
		// /////////////////
		case XPathResolver.ADD:
			return new Double(getDOperand1(context) + getDOperand2(context));
		case XPathResolver.MINUS:
			return new Double(getDOperand1(context) - getDOperand2(context));
		case XPathResolver.STAR:
			return new Double(getDOperand1(context) * getDOperand2(context));
		case XPathResolver.DIV:
			return new Double(getDOperand1(context) / getDOperand2(context));
		case XPathResolver.MOD:
			return new Double(getDOperand1(context) % getDOperand2(context));
		// /////////////////
		case XPathResolver.UNION: {

			int location = context.getContextPosition();
			NodeSet ns = context.getContextNodeSet();
			NodeSet set1 = getNSOperand1(context);
			context.setContextNodeSet(ns);
			context.setContextPosition(location);
			NodeSet set2 = getNSOperand2(context);

			return set1.union(set2);
		}
		}
		throw new RuntimeException("Invalid operator " + operator);
	}

}
// Operator ends here
