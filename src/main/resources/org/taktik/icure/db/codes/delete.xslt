<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="VALUE[CODE[contains(.,'.0')]]" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="VALUE[CODE]"  priority="1"/>
    <xsl:template match="CODE">
        <xsl:copy>
            <xsl:value-of select="substring(.,0,4)"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
