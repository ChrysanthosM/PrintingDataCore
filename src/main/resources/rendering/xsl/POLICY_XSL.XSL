<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">
    <xsl:output method="xml" indent="yes"/>
    <!-- Root template -->
    <xsl:template match="/">
        <fo:root>
            <!-- Page layout -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4"
                    page-height="29.7cm" page-width="21cm" margin="2cm">
                    <fo:region-body margin-top="1cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Title -->
                    <fo:block font-size="20pt" font-weight="bold" text-align="center" space-after="15pt">
                        Insurance Policy Summary
                    </fo:block>
                    <!-- Policy Header -->
                    <fo:block font-size="14pt" font-weight="bold" space-after="8pt">Policy Details</fo:block>
                    <fo:block>
                        <fo:list-block provisional-distance-between-starts="2cm">
                            <fo:list-item>
                                <fo:list-item-label><fo:block>Policy Number:</fo:block></fo:list-item-label>
                                <fo:list-item-body><fo:block><xsl:value-of select="InsurancePolicy/PolicyHeader/PolicyNumber"/></fo:block></fo:list-item-body>
                            </fo:list-item>
                            <fo:list-item>
                                <fo:list-item-label><fo:block>Type:</fo:block></fo:list-item-label>
                                <fo:list-item-body><fo:block><xsl:value-of select="InsurancePolicy/PolicyHeader/PolicyType"/></fo:block></fo:list-item-body>
                            </fo:list-item>
                            <fo:list-item>
                                <fo:list-item-label><fo:block>Effective:</fo:block></fo:list-item-label>
                                <fo:list-item-body><fo:block><xsl:value-of select="InsurancePolicy/PolicyHeader/EffectiveDate"/></fo:block></fo:list-item-body>
                            </fo:list-item>
                            <fo:list-item>
                                <fo:list-item-label><fo:block>Expires:</fo:block></fo:list-item-label>
                                <fo:list-item-body><fo:block><xsl:value-of select="InsurancePolicy/PolicyHeader/ExpirationDate"/></fo:block></fo:list-item-body>
                            </fo:list-item>
                        </fo:list-block>
                    </fo:block>
                    <!-- Policy Holder -->
                    <fo:block font-size="14pt" font-weight="bold" space-before="15pt" space-after="8pt">Policy Holder</fo:block>
                    <fo:block>
                        <xsl:value-of select="InsurancePolicy/PolicyHolder/FirstName"/> 
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="InsurancePolicy/PolicyHolder/LastName"/>
                    </fo:block>
                    <fo:block>
                        <xsl:value-of select="InsurancePolicy/PolicyHolder/Address/Street"/>,
                        <xsl:value-of select="InsurancePolicy/PolicyHolder/Address/City"/>
                    </fo:block>
                    <!-- Coverages -->
                    <fo:block font-size="14pt" font-weight="bold" space-before="15pt" space-after="8pt">Coverages</fo:block>
                    <fo:table table-layout="fixed" width="100%">
                        <fo:table-column column-width="4cm"/>
                        <fo:table-column column-width="4cm"/>
                        <fo:table-column column-width="4cm"/>
                        <fo:table-header>
                            <fo:table-row font-weight="bold" background-color="#e0e0e0">
                                <fo:table-cell><fo:block>Type</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block>Limit</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block>Deductible</fo:block></fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="InsurancePolicy/Coverages/Coverage">
                                <fo:table-row>
                                    <fo:table-cell><fo:block><xsl:value-of select="CoverageType"/></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block><xsl:value-of select="Limit"/></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block><xsl:value-of select="Deductible"/></fo:block></fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <!-- Premium -->
                    <fo:block font-size="14pt" font-weight="bold" space-before="15pt" space-after="8pt">Premium</fo:block>
                    <fo:block>
                        Annual Premium: <xsl:value-of select="InsurancePolicy/Premium/AnnualPremium"/>
                    </fo:block>
                    <fo:block>
                        Payment Plan: <xsl:value-of select="InsurancePolicy/Premium/PaymentPlan"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>