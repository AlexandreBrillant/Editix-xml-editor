package com.japisoft.framework.xml.refactor;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.elements.AttributeRefactor;
import com.japisoft.framework.xml.refactor.elements.CommentRefactor;
import com.japisoft.framework.xml.refactor.elements.ElementRefactor;
import com.japisoft.framework.xml.refactor.elements.EntityRefactor;
import com.japisoft.framework.xml.refactor.elements.NamespaceRefactor;
import com.japisoft.framework.xml.refactor.elements.PIRefactor;
import com.japisoft.framework.xml.refactor.elements.PrefixRefactor;
import com.japisoft.framework.xml.refactor.elements.RefactorAction;
import com.japisoft.framework.xml.refactor.elements.RefactorObj;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
public final class RefactorManager {
    private static ArrayList refactors;
    private static HashMap mapNameRefactorClass;

    static {
        refactors = new ArrayList();
        mapNameRefactorClass = new HashMap();
        addRefactor( new ElementRefactor() );
        addRefactor( new PrefixRefactor() );
        addRefactor( new NamespaceRefactor() );
        addRefactor( new AttributeRefactor() );
        addRefactor( new EntityRefactor() );
        addRefactor( new CommentRefactor() );
        addRefactor( new PIRefactor() );
    }

    public static void addRefactor( RefactorObj ro ) {
    	mapNameRefactorClass.put(
    			ro.getName(),
    			ro.getClass() );
        refactors.add( ro );
    }

    public static void addRefactorAt( RefactorObj ro, int index ) {
    	mapNameRefactorClass.put(
    			ro.getName(),
    			ro.getClass() );
    	refactors.add( index, ro );
    }

    public static void removeNonDefaultRefactors() {
    	Iterator it = refactors.iterator();
    	while ( it.hasNext() ) {
    		RefactorObj obj = ( RefactorObj )it.next();
    		if ( !obj.isDefault() )
    			it.remove();
    	}
    }

    public static void removeRefactor( RefactorObj ro ) {
        refactors.remove( ro );
    }

    public static void removeRefactorByName( String name ) {
    	for ( int i = 0; i < getRefactorCount(); i++ ) {
    		if ( getRefactor( i ).getName().equals( name ) ) {
    			refactors.remove( getRefactor( i ) );
    			break;
    		}
    	}
    }

    public static int getRefactorCount() {
        return refactors.size();
    }

    public static RefactorObj getRefactor( int i ) {
        return ( RefactorObj )refactors.get( i );
    }

    public static String getRefactorName( int i ) {
        return getRefactor( i ).getName();
    }
    
    public static RefactorObj getRefactor( String name ) {
    	for ( int i = 0; i < getRefactorCount(); i++ ) {
    		if ( name.equals( getRefactorName( i ) ) )
    			return getRefactor( i );
    	}
    	return null;
    }
    
    public static RefactorObj[] getRefactors() {
        RefactorObj[] ro = new RefactorObj[ refactors.size() ];
        for ( int i = 0; i < ro.length; i++ )
            ro[ i ] = ( RefactorObj )refactors.get( i );
        return ro;
    }
    
    public static String[] getRefactorActions( String name ) {
        for ( int i = 0; i < getRefactorCount(); i++ ) {
            RefactorObj ro = getRefactor( i );
            if ( ro.getName().equals( name ) )
                return ro.getActions();
        }
        return null;
    }

    public static RefactorObj buildRefactorObj( 
    		String name, 
    		RefactorAction action ) throws Exception {
    	Class cl = ( Class )mapNameRefactorClass.get( name );
    	if ( cl == null )
    		throw new RuntimeException( "Invalid class for name " + name );
    	RefactorObj ro = ( RefactorObj )cl.newInstance();
    	ro.setRefactorAction( action );
    	return ro;
    }

}
