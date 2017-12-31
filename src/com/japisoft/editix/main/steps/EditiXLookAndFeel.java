package com.japisoft.editix.main.steps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;

import org.netbeans.swing.plaf.util.RelativeColor;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.StoringLocationAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.SharedProperties;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.common.MinimumSizedIcon;
import com.jgoodies.looks.plastic.PlasticBorders;
import com.jgoodies.looks.plastic.PlasticIconFactory;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticUtils;

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
public class EditiXLookAndFeel extends PlasticLookAndFeel {

	public static Color DARK_BLACK = new Color( Integer.parseInt( "303030", 16 ) );
	public static Color LIGHT_BLACK = new Color( Integer.parseInt( "808080", 16 ) );
	public static Color INFORMATION_COLOR = new Color( Integer.parseInt( "58A27D", 16 ) );
	public static Color DARK_INFORMATION_COLOR = new Color( Integer.parseInt( "488466", 16 ) ); 	
	public static Color ERROR_COLOR = new Color( Integer.parseInt( "FF4444", 16 ) );
	// Error line inside the editor
	public static Color ERROR_COLOR_BACKGROUND = new Color( Integer.parseInt( "FFDDDD", 16 ) );
	
	static {

		// XMLPad 
		
		// CurrentLine
		UIManager.put( "xmlpad.editor.currentLineColor", new Color( Integer.parseInt( "CEE3D9", 16 ) ) );
		
		// Table

		SharedProperties.LINE_NUMBER_COLOR_SELECTED = DARK_BLACK;		
		SharedProperties.LINE_NUMBER_COLOR = LIGHT_BLACK;
		
		// Dialog header
		
		UIManager.put( "editix.dialog.header", DARK_BLACK );

		// XMLContainer node location header
		
		UIManager.put( "editix.nodelocation.light", LIGHT_BLACK );
		UIManager.put( "editix.nodelocation.light2", LIGHT_BLACK );
		UIManager.put( "editix.nodelocation.dark", Color.WHITE );

		// NetBeans tabpane

		UIManager.put(
				"winclassic_tab_sel_gradient",
				new RelativeColor( 
						DARK_BLACK, 
						DARK_BLACK, 
						"InternalFrame.activeTitleBackground" 
				)
		);
		
		UIManager.put(
			"jdock.innerwindow.gradient.selectedStopColor",
			new Color( Integer.parseInt( "C8C8C8", 16 ) ) );

		Color tmp = new Color( 200, 200, 200 );
		Color tmp2 = new Color( 220, 220, 220 );
		
		Color c1 = Preferences.getPreference( "interface", "table-color-odd", tmp );
		Color c2 = Preferences.getPreference( "interface", "table-color-even", tmp2 );
		Color c3 = Preferences.getPreference( "interface", "table-color-foreground", Color.BLACK );		
		Color c4 = Preferences.getPreference( "interface", "table-color-even-dark", tmp2 );
		
		EditixApplicationModel.setSharedProperty( "table.background.odd.color", c1 );
		EditixApplicationModel.setSharedProperty( "table.background.even.color", c2 );		
		EditixApplicationModel.setSharedProperty( "table.foreground.color", c3 );
		EditixApplicationModel.setSharedProperty( "table.background.even.dark.color", c4 );

		UIManager.put( "xmlpad.tableElementView.highlightColor", c1 );
		UIManager.put( "xmlpad.tableElementView.lowlightColor", c2 );		
		UIManager.put( "xmlpad.tableElementView.prefixNameColor", c4 );
		
		setMyCurrentTheme( new EditiXPlasticTheme() );

		// Disabled ctrl down
		UIManager.put( "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(
				new Object[] {
		         "RIGHT", "navigateRight",
	              "KP_RIGHT", "navigateRight",
	                  "LEFT", "navigateLeft",
	               "KP_LEFT", "navigateLeft",
	                    "UP", "navigateUp",
	                 "KP_UP", "navigateUp",
	                  "DOWN", "navigateDown",
	               "KP_DOWN", "navigateDown" 
				} ) );
		
		DialogManager.getDefaultDialogActionModel().addDialogAction(
				new StoringLocationAction());
		
	}
	
	class ExtendedLineBorder extends AbstractBorder {		
		private Insets insets = null;
		private Color col = DARK_BLACK;
		
		
		public ExtendedLineBorder( int padding ) {
			insets = new Insets( padding, padding, padding, padding );
		}
		
		public ExtendedLineBorder( int padding, Color col ) {
			this( padding );
			this.col = col;
		}
		
		public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {

			if ( c.isEnabled() ) {
				
				g.translate(x, y);
				g.setColor( col );
				g.drawRect( 1, 1, w - 2, h - 2);				
								
			} else {
				PlasticUtils.drawDisabledBorder(g, x, y, w - 1, h - 1);
			}
		}		
				
		public void setInsets(Insets insets) {
			this.insets = insets;
		}
		
		
		 public Insets getBorderInsets(Component c, Insets newInsets)       {
			 Insets margin = null;
	           if (c instanceof AbstractButton) {
	               // AbstractButton b = (AbstractButton)c;
	               // margin = b.getMargin();
	           } else if (c instanceof JToolBar) {
	               JToolBar t = (JToolBar)c;
	               margin = t.getMargin();
	           } else if (c instanceof JTextComponent) {
	               JTextComponent t = (JTextComponent)c;
	               margin = t.getMargin();
	           }
	           newInsets.top	 = margin != null ? margin.top : insets.top;
				newInsets.left	 = margin != null ? margin.left : insets.left;
				newInsets.bottom = margin != null ? margin.bottom : insets.bottom;
				newInsets.right  = margin != null ? margin.right : insets.right;
				return newInsets;
	        }		
		
		public Insets getInsets() {
			return insets;
		}
		
	}

	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults2(table);
		
		Object marginBorder	= new BasicBorders.MarginBorder();
		
		Object textFieldBorder = new ExtendedLineBorder( 5, LIGHT_BLACK ); 
		Object buttonBorder = new ExtendedLineBorder( 5, LIGHT_BLACK );
		Object tableHeaderBorder = textFieldBorder;
		Object scrollPaneBorder = new EmptyBorder( 1, 1, 1, 1 );
		
		Object menuItemBorder = scrollPaneBorder;
		
        Object toggleButtonBorder		= PlasticBorders.getToggleButtonBorder();
		
		Object menuBarEmptyBorder		= marginBorder;
		Object menuBarSeparatorBorder	= PlasticBorders.getSeparatorBorder();  
		Object menuBarEtchedBorder		= PlasticBorders.getEtchedBorder();
		Object menuBarHeaderBorder		= PlasticBorders.getMenuBarHeaderBorder(); 
		
		Object toolBarEmptyBorder		= marginBorder;
		Object toolBarSeparatorBorder	= PlasticBorders.getSeparatorBorder();
		Object toolBarEtchedBorder		= PlasticBorders.getEtchedBorder();
		Object toolBarHeaderBorder		= PlasticBorders.getToolBarHeaderBorder();
		
		Object internalFrameBorder		= getInternalFrameBorder();
		Object paletteBorder			= getPaletteBorder();
		
		Color controlColor 				= table.getColor("control");
		
		Object checkBoxIcon				= PlasticIconFactory.getCheckBoxIcon();
		Object checkBoxMargin			= new InsetsUIResource(2, 0, 2, 1); // 1.4.1 uses 2,2,2,2
		
		Object defaultButtonMargin		= LookUtils.createButtonMargin(false);
		Object narrowButtonMargin		= LookUtils.createButtonMargin(true);
		
		// Windows uses 2,2,2,2, but we try to adjust baselines of text and label.
		Object textInsets 			    = new InsetsUIResource(1, 2, 1, 2);
        Object wrappedTextInsets		= new InsetsUIResource(2, 3, 1, 2);
                                                
		Object menuItemMargin			= LookUtils.IS_LOW_RESOLUTION
											? new InsetsUIResource(3, 0, 3, 0)
											: new InsetsUIResource(2, 0, 2, 0);
		Object menuMargin				= new InsetsUIResource(2, 4, 2, 4);

		Icon   menuItemCheckIcon		= new MinimumSizedIcon(); 
		Icon   checkBoxMenuItemIcon		= PlasticIconFactory.getCheckBoxMenuItemIcon();
		Icon   radioButtonMenuItemIcon	= PlasticIconFactory.getRadioButtonMenuItemIcon();
		
		Color  menuItemForeground		= table.getColor("MenuItem.foreground");

		// 	Should be active.
		int     treeFontSize			= table.getFont("Tree.font").getSize(); 
		Integer rowHeight				= new Integer(treeFontSize + 6);
        Object  treeExpandedIcon		= PlasticIconFactory.getExpandedTreeIcon();
        Object  treeCollapsedIcon		= PlasticIconFactory.getCollapsedTreeIcon();
        ColorUIResource gray 			= new ColorUIResource(Color.GRAY);
		
		Boolean is3D					= Boolean.valueOf(is3DEnabled());
		
		Object[] defaults = { 
	    "Button.border",								buttonBorder,
		"Button.margin",								defaultButtonMargin,
		"Button.narrowMargin",							narrowButtonMargin,

		"CheckBox.margin", 								checkBoxMargin,

		// Use a modified check
		"CheckBox.icon", 								checkBoxIcon,
			
		"CheckBoxMenuItem.border",						menuItemBorder,
		"CheckBoxMenuItem.margin",						menuItemMargin,			// 1.4.1 Bug
		"CheckBoxMenuItem.checkIcon",					checkBoxMenuItemIcon,
        "CheckBoxMenuItem.background", 					getMenuItemBackground(),// Added by JGoodies
		"CheckBoxMenuItem.selectionForeground",			getMenuItemSelectedForeground(),
		"CheckBoxMenuItem.selectionBackground",			getMenuItemSelectedBackground(),
		"CheckBoxMenuItem.acceleratorForeground",		menuItemForeground,
		"CheckBoxMenuItem.acceleratorSelectionForeground",getMenuItemSelectedForeground(),
		"CheckBoxMenuItem.acceleratorSelectionBackground",getMenuItemSelectedBackground(),

		// ComboBox uses menu item selection colors
				
		"ComboBox.selectionForeground",					getMenuSelectedForeground(),
		"ComboBox.selectionBackground",					getMenuSelectedBackground(),
		
        "ComboBox.editorColumns",                       new Integer(5),
        
        "EditorPane.margin",                            wrappedTextInsets,

        "InternalFrame.border", 						internalFrameBorder,
        "InternalFrame.paletteBorder", 					paletteBorder,

		"List.font",									getControlTextFont(),
		"Menu.border",									PlasticBorders.getMenuBorder(), 
		"Menu.margin",									menuMargin,
		"Menu.arrowIcon",								PlasticIconFactory.getMenuArrowIcon(),

		"MenuBar.emptyBorder",							menuBarEmptyBorder,		// Added by JGoodies 
		"MenuBar.separatorBorder",						menuBarSeparatorBorder,	// Added by JGoodies
		"MenuBar.etchedBorder",							menuBarEtchedBorder,	// Added by JGoodies
		"MenuBar.headerBorder",							menuBarHeaderBorder,	// Added by JGoodies

		"MenuItem.border",								menuItemBorder,
		"MenuItem.checkIcon",	 						menuItemCheckIcon,		// Aligns menu items
		"MenuItem.margin",								menuItemMargin,			// 1.4.1 Bug
        "MenuItem.background", 							getMenuItemBackground(),// Added by JGoodies
		"MenuItem.selectionForeground",					getMenuItemSelectedForeground(),// Added by JGoodies
		"MenuItem.selectionBackground",					getMenuItemSelectedBackground(),// Added by JGoodies
		"MenuItem.acceleratorForeground",				menuItemForeground,
		"MenuItem.acceleratorSelectionForeground",		getMenuItemSelectedForeground(),
		"MenuItem.acceleratorSelectionBackground",		getMenuItemSelectedBackground(),

		"OptionPane.errorIcon",							makeIcon(getClass(), "icons/Error.png"),
        "OptionPane.informationIcon",                   makeIcon(getClass(), "icons/Inform.png"),
        "OptionPane.warningIcon",                       makeIcon(getClass(), "icons/Warn.png"),
        "OptionPane.questionIcon",                      makeIcon(getClass(), "icons/Question.png"),
		
		//"DesktopIcon.icon", 							makeIcon(superclass, "icons/DesktopIcon.gif"),
		"FileView.computerIcon",						makeIcon(getClass(), "icons/Computer.gif"),
		"FileView.directoryIcon",						makeIcon(getClass(), "icons/TreeClosed.gif"),
		"FileView.fileIcon", 							makeIcon(getClass(), "icons/File.gif"),
		"FileView.floppyDriveIcon", 					makeIcon(getClass(), "icons/FloppyDrive.gif"),
		"FileView.hardDriveIcon", 						makeIcon(getClass(), "icons/HardDrive.gif"),
		"FileChooser.homeFolderIcon", 					makeIcon(getClass(), "icons/HomeFolder.gif"),
        "FileChooser.newFolderIcon", 					makeIcon(getClass(), "icons/NewFolder.gif"),
        "FileChooser.upFolderIcon",						makeIcon(getClass(), "icons/UpFolder.gif"),
		"Tree.closedIcon", 								makeIcon(getClass(), "icons/TreeClosed.gif"),
	  	"Tree.openIcon", 								makeIcon(getClass(), "icons/TreeOpen.gif"),
	  	"Tree.leafIcon", 								makeIcon(getClass(), "icons/TreeLeaf.gif"),
			
        "FormattedTextField.border",                    textFieldBorder,            
        "FormattedTextField.margin",                    textInsets,             

		"PasswordField.border",							textFieldBorder,			
        "PasswordField.margin",                         textInsets,             

		// "PopupMenu.border",								popupMenuBorder,
		"PopupMenuSeparator.margin",					new InsetsUIResource(3, 4, 3, 4),	

		"RadioButton.margin",							checkBoxMargin,					
		"RadioButtonMenuItem.border",					menuItemBorder,
		"RadioButtonMenuItem.checkIcon",				radioButtonMenuItemIcon,
		"RadioButtonMenuItem.margin",					menuItemMargin,			// 1.4.1 Bug
        "RadioButtonMenuItem.background", 				getMenuItemBackground(),// Added by JGoodies
		"RadioButtonMenuItem.selectionForeground",		getMenuItemSelectedForeground(),
		"RadioButtonMenuItem.selectionBackground",		getMenuItemSelectedBackground(),
		"RadioButtonMenuItem.acceleratorForeground",	menuItemForeground,
		"RadioButtonMenuItem.acceleratorSelectionForeground",	getMenuItemSelectedForeground(),
		"RadioButtonMenuItem.acceleratorSelectionBackground",	getMenuItemSelectedBackground(),
		"Separator.foreground",							getControlDarkShadow(),
		"ScrollPane.border",							scrollPaneBorder,
		"ScrollPane.etchedBorder",   					scrollPaneBorder,

		"SimpleInternalFrame.activeTitleForeground",	getSimpleInternalFrameForeground(),
		"SimpleInternalFrame.activeTitleBackground",	getSimpleInternalFrameBackground(),
		
	    "Spinner.border", 								PlasticBorders.getFlush3DBorder(),
	    "Spinner.defaultEditorInsets",				    textInsets,
	    
	    "SplitPane.dividerSize",						new Integer(7),
		"TabbedPane.focus",								getFocusColor(),
		"TabbedPane.tabInsets",							new InsetsUIResource(1, 9, 1, 8),
		"Table.foreground",								table.get("textText"),
		"Table.gridColor",								controlColor, //new ColorUIResource(new Color(216, 216, 216)),
        "Table.scrollPaneBorder", 						scrollPaneBorder,
        "Table.rowHeight", 15,
		"TableHeader.cellBorder",						tableHeaderBorder,
		"TextArea.margin",								wrappedTextInsets,	
		"TextField.border",								textFieldBorder,			
		"TextField.margin", 							textInsets,				
		"TitledBorder.font",							getTitleTextFont(),
		"TitledBorder.titleColor",						getTitleTextColor(),
		"ToggleButton.border",							toggleButtonBorder,
		"ToggleButton.margin",							defaultButtonMargin,
		"ToggleButton.narrowMargin",					narrowButtonMargin,

		"ToolBar.emptyBorder", 							toolBarEmptyBorder,		// Added by JGoodies
		"ToolBar.separatorBorder", 						toolBarSeparatorBorder,	// Added by JGoodies
		"ToolBar.etchedBorder", 						toolBarEtchedBorder,	// Added by JGoodies
		"ToolBar.headerBorder", 						toolBarHeaderBorder,	// Added by JGoodies

		"ToolTip.hideAccelerator",						Boolean.TRUE,
				
		"Button.is3DEnabled",							is3D,
		"ComboBox.is3DEnabled",							is3D,
		"MenuBar.is3DEnabled",							is3D,
		"ToolBar.is3DEnabled",							is3D,
		"ScrollBar.is3DEnabled",						is3D,
		"ToggleButton.is3DEnabled",						is3D,

        // 1.4.1 uses a 2 pixel non-standard border, that leads to bad
        // alignment in the typical case that the border is not painted
        "CheckBox.border",                      marginBorder,
        "RadioButton.border",                   marginBorder,

        "ScrollBar.width", 15
        
		};
		
		table.putDefaults(defaults);		
	}
	
	
}
