package com.japisoft.sc;

import java.io.*;
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
public class SyntaxFileMapper {
  private ScEditorKit sc;

  public SyntaxFileMapper( ScEditorKit sc ) {
    super();
    this.sc = sc;
  }

  private Hashtable htDescriptor;

  /** Add a relation between a file format and a syntax descriptor */
  public void addRelation( String fileExtension, String propertyId ) {
    if ( htDescriptor == null )
      htDescriptor = new Hashtable();
    htDescriptor.put( fileExtension.toLowerCase(), propertyId );
  }

  /** Update the editor kit for supporting this file format */
  public void resetSyntaxColor( File f ) throws FileNotFoundException {
    resetSyntaxColor( f.toString() );
  }
  /** Update the editor kit for supporting this file format */
  public void resetSyntaxColor( String f ) throws FileNotFoundException {
    if ( htDescriptor != null ) {
      int i = f.lastIndexOf( "." );
      if ( i > -1 ) {
        String ext = f.substring( i + 1 );
        String propertyId = ( String )htDescriptor.get( ext.toLowerCase() );
        if ( propertyId != null ) {
          sc.readSyntaxColorDescriptor( propertyId );
        }
      }
    }
  }

}
