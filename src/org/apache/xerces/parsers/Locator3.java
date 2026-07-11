package org.apache.xerces.parsers;

import org.xml.sax.ext.Locator2;

public interface Locator3 extends Locator2 {

	public int getCharacterOffset();
	public int getLastStartingPart();
	
}
