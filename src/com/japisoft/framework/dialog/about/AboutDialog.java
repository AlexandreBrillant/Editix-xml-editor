package com.japisoft.framework.dialog.about;

import java.awt.Window;
import java.util.HashMap;
import javax.swing.ImageIcon;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;

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
public class AboutDialog {

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@
	

	/**
	 * @param owner Window parent
	 * @param product Product name
	 * @param version Product version
	 * @param build Product build
	 * @param company Company owner
	 * @param productLogo Product Logo
	 * @param registered User being registered can be <code>null</code>
	 * @return
	 */
	public static int showDialog(	
			Window owner,
			String product, 
			String version,
			String build,
			String company,
			String productLogo,
			String productInfo,
			String registered ) {

		HashMap map = new HashMap();
		if ( build != null )
			map.put( AboutPanel.BUILD_KEY, build );
		if ( company != null )
			map.put( AboutPanel.COMPANY_KEY, company );
		if ( product != null )
			map.put( AboutPanel.PRODUCT_KEY, product );
		if ( version != null )
			map.put( AboutPanel.VERSION_KEY, version );
		if ( productLogo != null ) {
			map.put( AboutPanel.IMAGE_KEY, new ImageIcon( ClassLoader.getSystemResource( productLogo ) ) );
		}
		if ( registered != null )
			map.put( AboutPanel.REGISTERED_KEY, registered );

		return DialogManager.showDialog(
				owner,
				"About",
				"About " + product,
				productInfo,
				null,
				new AboutPanel( map ),
				DialogActionModel.getDefaultDialogOkActionModel(),
				null
		);

	}

}
