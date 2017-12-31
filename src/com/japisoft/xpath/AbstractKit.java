package com.japisoft.xpath;

import java.util.Enumeration;
import java.util.Hashtable;

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
public abstract class AbstractKit implements XPathKit {

	/** Compute the node ID value. Only for compatibility with the 1.1
	 * This method will throw a RuntimeException with version > 1.1
	 *  @deprecated */
	public String getId(Object node) {
		throw new RuntimeException("Supported only with 1.1 version");
	}

	private Hashtable htFeatures = null;

	/** Used by the descendant for specifying a supported feature */
	protected void addFeature(String feature, boolean enable) {
		if (htFeatures == null)
			htFeatures = new Hashtable();
		htFeatures.put(feature.toLowerCase(), new Boolean(enable));
	}

	/** Set a feature support for the current kit. A RuntimeException
	 * should be thrown by the kit that doesn't support such feature */
	public void setFeature(String feature, boolean enable) {
		if (htFeatures == null)
			throw new RuntimeException("Unknown feature " + feature);
		if (!htFeatures.containsKey(feature.toLowerCase()))
			throw new RuntimeException("Unknown feature " + feature);
		htFeatures.put(feature.toLowerCase(), new Boolean(enable));
	}

	/** @return true if the feature is supported by the kit */
	public boolean isFeatureSupported(String feature) {
		if (htFeatures == null)
			return false;
		if (!htFeatures.containsKey(feature.toLowerCase()))
			return false;
		return ((Boolean) htFeatures.get(feature.toLowerCase())).booleanValue();
	}

	/** Return a list of supported features */
	public String[] getSupportedFeatures() {
		if (htFeatures == null)
			return new String[] {
		};
		String[] s = new String[htFeatures.size()];
		int i = 0;
		for (Enumeration enume = htFeatures.keys(); enume.hasMoreElements();) {
			s[i++] = (String) enume.nextElement();
		}
		return s;
	}

	/** Particular case where the provided node is not valid for Xpath. This is often then case for
	 * Document objet rather than Node object */
	public Object getBetterReferenceNode( Object refNode ) {
		return refNode;
	}
}
