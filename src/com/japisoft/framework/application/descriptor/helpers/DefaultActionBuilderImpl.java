package com.japisoft.framework.application.descriptor.helpers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import com.japisoft.framework.application.descriptor.InterfaceBuilderException;
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
public class DefaultActionBuilderImpl implements ActionBuilder {

	private static Map CLASS_TRANSLATOR = null;

	/** Translate an action name to another one. This is useful when you
	 * must compute the final action */
	public static void translateActionClass( String actionSource, String finalAction ) {
		if ( CLASS_TRANSLATOR == null )
			CLASS_TRANSLATOR = new HashMap();
		CLASS_TRANSLATOR.put( actionSource, finalAction );
	}

	private static Map CLASS_PROVIDER = null;
	
	/** Provide a class for an action name. Thus you can found a class
	 * anywhere */
	public static void provideClass( String actionSource, Class finalClass ) {
		if ( CLASS_PROVIDER == null )
			CLASS_PROVIDER = new HashMap();
		CLASS_PROVIDER.put( actionSource, finalClass );
	}

	public Action buildAction( FPNode source, String actionName ) throws InterfaceBuilderException {
		try {
			if ( CLASS_TRANSLATOR != null )
				if ( CLASS_TRANSLATOR.containsKey( actionName ) )
					actionName = ( String )CLASS_TRANSLATOR.get( actionName );
			
			Class cl = null;
			
			if ( CLASS_PROVIDER != null && 
					CLASS_PROVIDER.containsKey( actionName ) ) {
				cl = ( Class )CLASS_PROVIDER.get( actionName );
			}

			if ( cl == null )
				cl = Class.forName( actionName );
			
			return ( Action )cl.newInstance();
		} catch( Throwable th ) {
			throw new InterfaceBuilderException( "Cannot find the class : [" + actionName + "]", th );
		}
	}	

}
