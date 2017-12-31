package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import com.japisoft.editix.ui.xslt.action.RunAction;
import com.japisoft.editix.ui.xslt.action.SyntaxCompletionAction;
import com.japisoft.framework.dialog.welcome.WelcomeDialog;

import com.japisoft.xmlpad.UIStateListener;
import com.japisoft.xmlpad.action.ActionGroup;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.file.LoadAction;


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
public class XSLTContainerLauncher
	extends JFrame
	implements WindowListener, UIStateListener {

	static int height = 15;

	public XSLTContainerLauncher(String type) {
		super(type == null ? "XSLT Editor : http://www.editix.com" : "XML Editor : http://www.editix.com");
		setSize(800, 640 - 2 * height);
		initUI();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(this);
	}

	static XSLTContainerLauncher cl = null;

	public static String CONTEXT = "nbxslt";
	public static String CONTEXT_TITLE = "Netbeans - XSLT Editor";

	private static void storeConfig(XSLTContainerLauncher launcher) {
		File parent = new File(System.getProperty("user.home"), "." + CONTEXT);
		if (!parent.exists()) {
			parent.mkdir();
		}
		
		try {
			FileWriter fw = new FileWriter(new File(parent, "config"));
			try {
				Rectangle r = launcher.getBounds();
				fw.write(r.x + "," + r.y + "," + r.width + "," + r.height);
			} finally {
				fw.close();
			}

			FileWriter fw2 = new FileWriter(new File(parent, "xslt.param"));
			try {
				fw2.write(
					XSLTEditor.DEF_XSLTFile == null
						? "-"
						: XSLTEditor.DEF_XSLTFile);
				fw2.write("\n");
				fw2.write(
					XSLTEditor.DEF_DATAFile == null
						? "-"
						: XSLTEditor.DEF_DATAFile);
				fw2.write("\n");
				fw2.write(
					XSLTEditor.DEF_RESULTFile == null
						? "-"
						: XSLTEditor.DEF_RESULTFile);
			} finally {
				fw2.close();
			}

		} catch (Throwable th) {
		}
	}

	private static void restoreConfig(
		XSLTContainerLauncher launcher,
		File init) {

		launcher.delayedResetContent = false;

		File parent = new File(System.getProperty("user.home"), "." + CONTEXT);
		try {

			String restoredFile = null;

			File f = new File(parent, "config");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				try {
					String r = br.readLine();
					if (r != null) {
						StringTokenizer s = new StringTokenizer(r, ",");
						Rectangle _ =
							new Rectangle(
								Integer.parseInt(s.nextToken()),
								Integer.parseInt(s.nextToken()),
								Integer.parseInt(s.nextToken()),
								Integer.parseInt(s.nextToken()));
						launcher.setBounds(_);
					}
				} finally {
					br.close();
				}
			}

			f = new File(parent, "xslt.param");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				try {

					String tmp = br.readLine();
					if (!"-".equals(tmp))
						restoredFile = tmp;
					tmp = br.readLine();
					if (!"-".equals(tmp))
						XSLTEditor.DEF_DATAFile = tmp;
					tmp = br.readLine();
					if (!"-".equals(tmp))
						XSLTEditor.DEF_RESULTFile = tmp;

				} finally {
					br.close();
				}
			}

			if (init != null) {
				if (init.length() == 0) {
					XSLTEditor.DEF_XSLTFile = null;
					launcher.resetContent();
					return;
				}

				restoredFile = init.toString();
			}

			if (restoredFile != null) {
				XSLTEditor.DEF_XSLTFile = restoredFile;
			} else
				launcher.resetContent();

		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	public static void showUI() {
		showUI(CONTEXT_TITLE, null, null);
	}

	public static void showUI(String title, File f, String type) {

		if (cl == null)
			cl = new XSLTContainerLauncher(type);

		cl.prepareDocumentType(type);

		restoreConfig(cl, f);
		cl.setVisible(true);
		cl.toFront();

		WelcomeDialog.showDialog( 
				cl.getOwner(),
				"http://www.japisoft.com. This tool uses JXMLPad : http://www.jxmlpad.com and JDock a swing docking framework : http://www.swingall.com",
				title,
				new String[] {
						"JDock is a swing docking framework working with any layouts : http://www.swingall.com",
						"Need a swing component ? : Look at http://www.swingall.com",
						"Use JXMLPad inside your swing application for editing any XML documents : http://www.jxmlpad.com",
						"Do you want an XSLT Debugger ? Look at EditiX : http://www.editix.com",
						"Do you want to generate a schema, a DTD ? Look at EditiX : http://www.editix.com",
						"Do you want to use RelaxNG ? Look at EditiX : http://www.editix.com",
						"XML Diff ? Look at EditiX : http://www.editix.com",
						"Do you want an Applet for editing your XML document : http://www.jxmlpad.com",
						"JXMLPad is a swing based component for editing your XML documents. JXMLPad supports W3C XML Schema, DTD or XML RelaxNG. http://www.jxmlpad.com",
						"Evaluate your math expressions with JFormula 3.0 : http://www.japisoft/formula",
						"Evaluate your boolean expressions with JFormula 3.0 : http://www.japisoft/formula",
						"Plug your function, symbol and operators with JFormula 3.0 : http://www.japisoft.com/formula" }
						,
					null
				);
	}

	private XSLTEditor container;

	public void ready() {
		if (delayedResetContent) {
			resetDocument();
		} else {
			if (XSLTEditor.DEF_XSLTFile != null) {
				try {
					LoadAction.loadInBuffer(
						container.getMainContainer(),
						XSLTEditor.DEF_XSLTFile);
					container
						.getMainContainer()
						.getDocumentInfo()
						.setCurrentDocumentLocation(
						XSLTEditor.DEF_XSLTFile.toString());
				} catch (Throwable th) {
				}
			}
			if (XSLTEditor.DEF_DATAFile != null) {
				container.loadDataFile(XSLTEditor.DEF_DATAFile);
			}
		}

		delayedResetContent = false;
	}

	public void dispose() {
		delayedResetContent = false;
	}

	private boolean delayedResetContent = false;

	private void resetContent() {
		delayedResetContent = true;
	}

	private void resetDTD(String root, String path) {
		URL dtd = getClass().getResource(path);
		if (dtd == null)
			dtd =
				ClassLoader.getSystemClassLoader().getResource(
					"com/japisoft/editix/ui/xslt/" + path);
		if (dtd != null) {
			container.getMainContainer().getSchemaAccessibility().setDefaultDTD(root, dtd );
		}
	}

	private void prepareDocumentType(String type) {

		if (type == null) {

			container.getMainContainer().getDocumentInfo().setDefaultFileExt(
				"xsl");

			container.getMainContainer().getDocumentColorAccessibility().setColorForPrefix( "xsl", Color.green.darker() );
			container.getMainContainer().getDocumentColorAccessibility().setColorForPrefix( "xs", Color.green.darker() );

			resetDTD("xsl:stylesheet", "xslt.dtd");

			String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			s += "<xsl:stylesheet version=\"1.0\"";
			s += " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n";
			s += "xmlns=\"http://www.w3.org/TR/xhtml1/strict\">\n";
			s += "<xsl:output method=\"html\"/>\n";
			s += "<xsl:template match=\"/\">\n";
			s += " <html>\n";
			s += "   <body>\n";
			s += "   </body>\n";
			s += "  </html>\n";
			s += " </xsl:template>\n";
			s += "</xsl:stylesheet>\n";
			template = s;

		} else {

			template = null;

			if ("ant".equals(type)) {
				resetDTD("project", "ant.dtd");
			} else if ("xsd".equals(type)) {
				resetDTD("xsd:schema", "XMLSchema.dtd");
				template =
					" <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
						+ "</xsd:schema>";
			}

			container.getMainContainer().getSchemaAccessibility().setDefaultDTD(null, (String) null);
			if (template == null)
				template = "<?xml version=\"1.0\"?>\n\n";
		}
	}

	String template = null;

	private void resetDocument() {

		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		s += "<xsl:stylesheet version=\"1.0\"";
		s += " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n";
		s += "xmlns=\"http://www.w3.org/TR/xhtml1/strict\">\n";
		s += "<xsl:output method=\"html\"/>\n";
		s += "<xsl:template match=\"/\">\n";
		s += " <html>\n";
		s += "   <body>\n";
		s += "   </body>\n";
		s += "  </html>\n";
		s += " </xsl:template>\n";
		s += "</xsl:stylesheet>\n";

		container.getMainContainer().getAccessibility().setText(template);
	}

	private void initUI() {
		getContentPane().setLayout(new BorderLayout());

		JToolBar tb = new JToolBar();
		getContentPane().add(tb, BorderLayout.NORTH);

		ActionModel.removeActionByName(ActionModel.NEW_ACTION);

		container = new XSLTEditor( null, new SingleFactoryImpl(), false );
		//container.getMainContainer().setUIStateListener( this );
		
		if (ActionModel.getGroupByName("XSLT") == null) {
			ActionGroup xsltGroup = new ActionGroup("XSLT");
			ActionModel.addGroup(xsltGroup);
			xsltGroup.addAction(
				new SyntaxCompletionAction(container.getMainContainer()));
			xsltGroup.addAction(new RunAction(this, container));
		}
		
		ActionModel.buildToolBar(tb);

		getContentPane().add(container.getView(), BorderLayout.CENTER);

		ActionModel.resetActionState(container.getMainContainer());
		ActionModel.setEnabledAutoResetActionState(false);
		ActionModel.setEnabledAction(ActionModel.LOAD_ACTION, true);
		ActionModel.setEnabledAction(ActionModel.FORMAT_ACTION, true);
	}

	private boolean ok = false;

	public void windowActivated(WindowEvent e) {
		if (!ok)
			ready();
		ok = true;
	}

	public void windowClosed(WindowEvent e) {
		storeConfig(this);
		dispose();
	}

	public void windowClosing(WindowEvent e) {
		dispose();
		ok = false;
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public static void main(String[] args) {
		showUI();
	}

}
