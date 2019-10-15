<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="CODE">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:variable name="code"><xsl:value-of select="."/></xsl:variable>
            <xsl:variable name="dedup" select="count(../preceding-sibling::VALUE[CODE/.=$code])"/>
            <xsl:value-of select="."/>.<xsl:value-of select="$dedup"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
