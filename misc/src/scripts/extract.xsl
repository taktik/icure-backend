<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="text()"/>

    <xsl:template match="*">
        <xsl:for-each select="ancestor-or-self::*">
            <xsl:value-of select="concat('/',local-name())"/>
        </xsl:for-each>
        <xsl:text>&#xA;</xsl:text>
        <xsl:apply-templates select="node()"/>
    </xsl:template>

</xsl:stylesheet>
