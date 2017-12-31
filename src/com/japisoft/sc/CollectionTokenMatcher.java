package com.japisoft.sc;

import java.awt.*;
import java.util.*;

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
class CollectionTokenMatcher extends TokenMatcher {

  private TokenMatcher[] tm;
  private Hashtable collection;
  private String name;
  private TokenMatcher[] tm2;

  public CollectionTokenMatcher( Hashtable collection, String name ) {
    this.collection = collection;
    this.name = name;
  }

  public CollectionTokenMatcher( TokenMatcher[] tm ) {
    this.tm2 = tm;
  }

  public TokenMatcher[] getNext(char current) {
    // Check if one caracter is inside the collection
    if ( tm == null && tm2 == null )
      tm = new TokenMatcher[] { new CollectionTokenMatcher( tm2 = ( TokenMatcher[] )collection.get( name ) ) };
    for ( int i = 0; i < tm2.length; i++ ) {
      if ( tm2[ i ].getNext( current ) != null ) {
        Color c = tm2[ i ].getToken().getColor();
        this.t = tm2[ i ].getToken();
        System.out.println( c.getRed() + "," + c.getGreen() + "," + c.getBlue() );
        return new TokenMatcher[] { this };
      }
    }
    return null;
  }
}
