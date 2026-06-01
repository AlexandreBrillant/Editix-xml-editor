<?xml version="1.0" encoding="UTF-8"?>

<!-- New XSLT document created with EditiX XML Editor (https://www.editix.com) at Fri Apr 24 11:11:17 CEST 2026 -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html"/>
	
	<xsl:template match="/">ls
	
	<html>
		<body>
			<xsl:for-each select="//book">
				<xsl:variable name="title" select="title"/>
				<div>
					<xsl:value-of select="$title"/>
				</div>
			</xsl:for-each>
		</body>
	</html>
	</xsl:template>

</xsl:stylesheet>


