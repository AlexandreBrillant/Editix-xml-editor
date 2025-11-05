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

package com.japisoft.editix.main.steps.lookandfeel;

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
import com.japisoft.framework.ApplicationModel;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.SharedProperties;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.common.MinimumSizedIcon;
import com.jgoodies.looks.plastic.PlasticBorders;
import com.jgoodies.looks.plastic.PlasticIconFactory;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticUtils;

/**
 * Note
 * - PlasticLookAndFeel can be found at https://www.jgoodies.com, EditiX uses an old version under BSD open source license
 *   This look and feel isn’t really useful, so it should be remove it later...
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EditiXDarkLookAndFeel extends PlasticLookAndFeel {

	public static Color DARK_BLACK = new Color( Integer.parseInt( "111111", 16 ) );
	public static Color LIGHT_BLACK = new Color( Integer.parseInt( "808080", 16 ) );
	public static Color INFORMATION_COLOR = new Color( Integer.parseInt( "3D4B85", 16 ) );
	public static Color DARK_INFORMATION_COLOR = new Color( Integer.parseInt( "488466", 16 ) ); 	
	public static Color ERROR_COLOR = new Color( Integer.parseInt( "FF4444", 16 ) );
	public static Color DISABLED_COLOR = new Color( Integer.parseInt( "555555", 16 ) );
	
	// Error line inside the editor
	public static Color ERROR_COLOR_BACKGROUND = new Color( Integer.parseInt( "FFDDDD", 16 ) );

	


	static {
		setMyCurrentTheme( new EditiXDarkTheme() );
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
		Object scrollPaneBorder = new EmptyBorder( 2, 2, 2, 2 );
		
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
		"CheckBox.foreground",							EditiXDarkTheme.DEFAULT_FOREGROUND,

		// Use a modified check
		"CheckBox.icon", 								checkBoxIcon,
			
		"CheckBoxMenuItem.border",						menuItemBorder,
		"CheckBoxMenuItem.margin",						menuItemMargin,			// 1.4.1 Bug
		"CheckBoxMenuItem.checkIcon",					checkBoxMenuItemIcon,
        "CheckBoxMenuItem.background", 					getMenuItemBackground(),
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

        "Label.foreground",								getCurrentTheme().getUserTextColor(),
		"List.font",									getControlTextFont(),
		"Menu.border",									PlasticBorders.getMenuBorder(), 
		"Menu.margin",									menuMargin,
		"Menu.arrowIcon",								PlasticIconFactory.getMenuArrowIcon(),

		"MenuItem.checkIcon",	 						menuItemCheckIcon,
		"MenuItem.margin",								menuItemMargin,
		"MenuItem.background", 							EditiXDarkTheme.DEFAULT_BACKGROUND,
		"MenuItem.selectionForeground",					EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND,
		"MenuItem.selectionBackground",					EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND,
		"MenuItem.acceleratorForeground",				menuItemForeground,
		"MenuItem.acceleratorSelectionForeground",		getMenuItemSelectedForeground(),
		"MenuItem.acceleratorSelectionBackground",		getMenuItemSelectedBackground(),
				
		"MenuItem.disabledForeground", 					EditiXDarkTheme.DEBUG_COLOR,
		
		"OptionPane.errorIcon",							makeIcon(getClass(), "icons/Error.png"),
        "OptionPane.informationIcon",                   makeIcon(getClass(), "icons/Inform.png"),
        "OptionPane.warningIcon",                       makeIcon(getClass(), "icons/Warn.png"),
        "OptionPane.questionIcon",                      makeIcon(getClass(), "icons/Question.png"),
		
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
		"Tree.background",								EditiXDarkTheme.DEFAULT_BACKGROUND,	
	  	"Tree.foreground",								EditiXDarkTheme.DEFAULT_FOREGROUND,
	  	"Tree.selectionBackground",						EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND,
	  	"Tree.selectionForeground",						EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND,
        "FormattedTextField.border",                    textFieldBorder,            
        "FormattedTextField.margin",                    textInsets,             

		"PasswordField.border",							textFieldBorder,			
        "PasswordField.margin",                         textInsets,             

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

		"TabbedPane.background",						EditiXDarkTheme.DEFAULT_BACKGROUND,
		"TabbedPane.foreground",						EditiXDarkTheme.DEFAULT_FOREGROUND,
		"TabbedPane.selectionForeground",				EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND,
		"TabbedPane.selectionBackground",				EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND,
		
		"Table.foreground",								EditiXDarkTheme.DEFAULT_FOREGROUND,
		"Table.gridColor",								controlColor, //new ColorUIResource(new Color(216, 216, 216)),
        "Table.scrollPaneBorder", 						scrollPaneBorder,
        "Table.rowHeight", 15,
		"TableHeader.cellBorder",						tableHeaderBorder,
		"TextArea.margin",								wrappedTextInsets,	
		"TextField.border",								textFieldBorder,			
		"TextField.background",							EditiXDarkTheme.DEFAULT_BACKGROUND,
		"TextField.foreground",							EditiXDarkTheme.DEFAULT_FOREGROUND,		
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

        "CheckBox.border",                      marginBorder,
        "RadioButton.border",                   marginBorder,

        "ScrollBar.width", 15
		};
		
		table.putDefaults(defaults);
		
		
		Color lightBackground = EditiXDarkTheme.DEFAULT_BACKGROUND.brighter().brighter();
		
		
		UIManager.put("MenuItem.disabledForeground",new Color( Integer.parseInt( "555555", 16 ) ) );
		UIManager.put( "editix.linklabel", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "TextField.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "TextField.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "TextArea.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "TextArea.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );			
		UIManager.put( "PasswordField.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "PasswordField.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );			
        UIManager.put( "FileChooser.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "FileChooser.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put( "List.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "List.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put( "List.selectionBackground", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        UIManager.put( "List.selectionForeground", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
        
        UIManager.put( "Menu.borderPainted", false );
        UIManager.put( "MenuItem.borderPainted", false );
        UIManager.put( "PopupMenu.border", new EmptyBorder(2,2,2,2) );
        UIManager.put( "MenuBar.border", new EmptyBorder(2,2,2,2) );
        
        UIManager.put( "Table.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "Table.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put( "Table.selectionBackground", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        UIManager.put( "Table.selectionForeground", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
        UIManager.put( "Tree.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "Tree.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put( "Tree.selectionForeground", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
        UIManager.put( "Tree.selectionBackground", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        
        UIManager.put("ScrollBar.background", EditiXDarkTheme.DEFAULT_BACKGROUND);
        UIManager.put("ScrollBar.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put("ScrollBar.track", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        UIManager.put("ScrollBar.thumb", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        UIManager.put("ScrollBar.thumbDarkShadow", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put("ScrollBar.thumbHighlight", EditiXDarkTheme.DEFAULT_FOREGROUND );
        
        
        
        UIManager.put( "preferences.pawn", com.japisoft.framework.app.toolkit.Toolkit.getImageIcon( "images/folder_closed.png" ) );
        UIManager.put( "jdock.innerwindow.gradient.selectedStartColor", lightBackground );
        UIManager.put( "jdock.innerwindow.gradient.selectedStopColor", lightBackground );
        UIManager.put( "jdock.innerwindow.titleColor", Color.WHITE );
        
        
        SharedProperties.LINE_NUMBER_COLOR_SELECTED = Color.WHITE;
        SharedProperties.LINE_NUMBER_COLOR = EditiXDarkTheme.DEFAULT_FOREGROUND.darker();
		
		Color tmp = EditiXDarkTheme.DEFAULT_BACKGROUND;
		Color tmp2 = tmp.brighter();
	
		EditixApplicationModel.setSharedProperty( "table.background.odd.color", EditiXDarkTheme.DEFAULT_BACKGROUND );
		EditixApplicationModel.setSharedProperty( "table.background.even.color", tmp2 );		
		EditixApplicationModel.setSharedProperty( "table.foreground.color", EditiXDarkTheme.DEFAULT_FOREGROUND );
		EditixApplicationModel.setSharedProperty( "table.background.even.dark.color", EditiXDarkTheme.DEFAULT_BACKGROUND );
		EditixApplicationModel.setSharedProperty( "about.memory.foreground", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
		
		UIManager.put( "xmlpad.editor.focusBorder", null );

        UIManager.put( "jxmlpad.rowcomponent.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
        
        UIManager.put( "xmlpad.tableElementView.prefixNameColor", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "xmlpad.tableElementView.lowlightColor" , EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "xmlpad.tableElementView.highlightColor" , EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "xmlpad.tableElementView.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
        
        UIManager.put( "xmlpad.helper.backgroundColor", EditiXDarkTheme.DEFAULT_BACKGROUND );
        UIManager.put( "xmlpad.helper.foregroundColor", EditiXDarkTheme.DEFAULT_FOREGROUND );
        UIManager.put( "xmlpad.helper.selectionBackgroundColor", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
        UIManager.put( "xmlpad.helper.selectionForegroundColor", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
		UIManager.put( "xmlpad.errorColor",  EditiXDarkTheme.DEFAULT_ERROR_FOREGROUND );		
		
		UIManager.put( "editix.error.foreground", EditiXDarkTheme.DEFAULT_ERROR_FOREGROUND );
		
		UIManager.put( "editix.nodelocation.dark", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND.darker() );
		UIManager.put( "editix.nodelocation.color",lightBackground );
		UIManager.put( "editix.label.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
		
		UIManager.put( "editix.panel.project.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "editix.panel.diff.removed", new Color( 150,50, 50 ) );
		UIManager.put( "editix.diff.error", new Color( 150,50, 50 ) );
		
		UIManager.put( "editix.xsd.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "editix.xsd.table.background", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "editix.xsd.table.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "editix.xsd.withattribute", Color.WHITE );
		UIManager.put( "editix.xsd.line", Color.WHITE );
		UIManager.put( "editix.xsd.cssstyle", "schema-dark.css" );

		UIManager.put( "editix.xsd.defaultcss", "a{ color:#" + EditiXDarkTheme.DEFAULT_RGB_FOREGROUND + "} body { font-family: Arial, Helvetica, sans-serif;color:#" + EditiXDarkTheme.DEFAULT_RGB_FOREGROUND + ";background-color : #" + EditiXDarkTheme.DEFAULT_RGB_BACKGROUND + "; 	}\n.doc { 		width : 80%; 		background-color:#" + EditiXDarkTheme.DEFAULT_RGB_SELECTIONBACKGROUND + ";color:#" + EditiXDarkTheme.DEFAULT_RGB_SELECTIONFOREGROUND + ";border-radius:4px; 		font-style:italic; 		padding:5px; 	}\n.name { 		font-weight : bolder; 		color : #" + EditiXDarkTheme.DEFAULT_RGB_FOREGROUND + "; 		font-size:1.0em; 	}\nh1,h2 { 		font-size:1.2em; 	}\ntable { 		border:1px solid #999; 	} );" );
		UIManager.put( "editix.xslt.breakpoint", new Color( 150,50, 50 ) );
		UIManager.put( "editix.xslt.currentline", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
		UIManager.put( "editix.xslt.templates", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "editix.xslt.templates.foreground", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "editix.xslt.mapping", EditiXDarkTheme.DEFAULT_FOREGROUND );
		
		UIManager.put( "editix.scenario.bg1", EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "editix.scenario.bg2", EditiXDarkTheme.DEFAULT_BACKGROUND.brighter() );
		UIManager.put( "editix.scenario.fg1", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "editix.scenario.fgSelection", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
		UIManager.put( "editix.scenario.bgSelection", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );

		UIManager.put( "editix.xmlform.border", EditiXDarkTheme.DEFAULT_FOREGROUND );
		UIManager.put( "editix.xmlform.selection", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
		
		UIManager.put( "editor.css.background" , EditiXDarkTheme.DEFAULT_BACKGROUND );
		UIManager.put( "editor.css.foreground" , EditiXDarkTheme.DEFAULT_FOREGROUND );
		
		UIManager.put( "Button.borderPaintsFocus", true );
		UIManager.put( "splitpane.divider.background", lightBackground );
	}
	
	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		new EditiXDarkLookAndFeel();
	}
	
}

