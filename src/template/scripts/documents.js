/* Interact with the editix context surrounding the selection by a new node. You must put it inside the script manager for running it from the options menu */ 

var documents = EditiXManager.getDocumentModel();

// Navigate into any document
for ( var i = 0; i < documents.getDocumentCount(); i++ ) {
	var document = documents.getDocument( i );
	// Select XML
	if ( document.getType() == "XML" ) {	
		var date = new Date();
		// Look at the XML declaration
		var endprolog = document.getTextContent().indexOf( "?>" );
		// Insert a comment after the declaration
		if ( endprolog == -1 ) {	// Not found
			document.insertTextAt( 0, "<!-- Commented at " + date + " -->" );		
		} else {	// OK
			document.insertTextAt( endprolog + 3, "<!-- Commented at " + date + " -->" );		
		}
	}
}
