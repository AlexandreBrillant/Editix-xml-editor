// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.dialog.about;

import java.awt.Window;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;

/**
 * About dialog. It shows the current product version and the
 * memory usage. Call the <code>addAboutProperty</code> from the AboutPanel for
 * custom properties
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class AboutDialog {

	static {
		ApplicationMain.class.getName();
	}
	

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

	public static void main( String[] args ) {
		
		AboutDialog.showDialog( new JFrame(), "test editix", "2026", "010101", "abrillant", null, "product info", "no" );
		
	}
	
}

