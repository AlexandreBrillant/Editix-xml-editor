package com.japisoft.framework.xml.refactor.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.japisoft.framework.xml.refactor.RefactorManager;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

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
public class RefactorModel extends HashMap {
	private boolean relativeRefactoring = false;

	public boolean isRelativeRefactoring() {
		return relativeRefactoring;
	}

	public void setRelativeRefactoring( boolean relativeRefactoring ) {
		this.relativeRefactoring = relativeRefactoring;
	}

	public String getConfig() {
		StringBuffer sb = new StringBuffer();
		Iterator it = keySet().iterator();
		while ( it.hasNext() ) {
			String key = ( String )it.next();
			RefactorTable rt = ( RefactorTable )get( key );
			RefactorAction[] rat = rt.getActions();
			if ( rat != null ) {
				if ( sb.length() > 0 )
					sb.append( "$$$" );
				sb.append( key );
				for ( int i = 0; i < rat.length; i++ ) {
					sb.append( "!!!" );
					sb.append( rat[ i ].toString() );
				}
			}
		}		
		return sb.toString();
	}

	public void setConfig( String config ) {
		if ( config == null )
			return;
		StringTokenizer st = new StringTokenizer( config, "$$$", false );
		while ( st.hasMoreTokens() ) {
			String token = st.nextToken();
			StringTokenizer st2 = new StringTokenizer( token, "!!!", false );
			String key = st2.nextToken();
			ArrayList al = new ArrayList();
			while ( st2.hasMoreTokens() ) {
				String token2 = st2.nextToken();
				RefactorAction ra = new RefactorAction( token2 );
				al.add( ra );
			}
			RefactorAction[] ra = new RefactorAction[ al.size() ];
			for ( int i = 0; i < al.size(); i++ )
				ra[ i ] = ( RefactorAction )al.get( i );
			RefactorTable rt = new RefactorTable( key );
			rt.setActions( ra );
			put( key, rt );
		}
	}

	public RefactorObj[] getRefactorObjs() {
		ArrayList lr = new ArrayList();
		Iterator it = keySet().iterator();

		while ( it.hasNext() ) {
			String key = ( String )it.next();
			RefactorTable rt = ( RefactorTable )get( key );
			RefactorAction[] rat = rt.getActions();
			try {	
				if ( rat != null ) 
					for ( int i = 0; i < rat.length; i++ ) {
						RefactorAction ra = rat[ i ];
						lr.add( 
							RefactorManager.buildRefactorObj( key, ra ) );
					}
			} catch (Throwable e) {
			}
		}

		if ( lr.size() == 0 ) {
			return null;
		} else {
			RefactorObj[] res = new RefactorObj[ lr.size() ];
			for ( int i = 0; i < lr.size(); i++ )
				res[ i ] = ( RefactorObj )lr.get( i );
			return res;
		}		
	}

}
