/* 
	Create a dialog with a button. Each time the button is pressed a message 'ok' is show in the console
	(go to the help menu for display the console).
	You must put it inside the script manager for running it from the options menu 
*/ 

// Import Java packages
var SwingGui = new JavaImporter(javax.swing,
                            javax.swing.event,
                            javax.swing.border,
                            java.awt.event);
                       
// Create a new actionlistener implementation
var action = new java.awt.event.ActionListener {
	actionPerformed : function( event ) {
		println( "ok" );
	}
}

// Build the frame
with (SwingGui) {
    var mybutton = new JButton("test");
    // Connect the button to our action
    mybutton.addActionListener( action );
    var myframe = new JFrame("test");
    myframe.add( mybutton );
    myframe.setSize( 100, 100 );
    // Display our frame with our button
    myframe.setVisible( true );
}
