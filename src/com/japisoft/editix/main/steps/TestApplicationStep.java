package com.japisoft.editix.main.steps;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;

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

https://www.editix.com/buy.html

@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
*/
public class TestApplicationStep extends ApplicationStepAdapter {

	@Override
	public void start(String[] args) {
		ApplicationModel.debug( System.getProperty("java.class.path") );
		ApplicationModel.debug( "Checking dtd/XMLSchema.dtd : " + ClassLoader.getSystemClassLoader().getResource( "dtd/XMLSchema.dtd" ) );
	}
	
}
