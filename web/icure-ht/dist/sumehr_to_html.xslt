<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Property : eHealth
Author   : eHealth 
Date     : 01/06/2010
Version 1.0 Written for ehValidator library and tool

This is an XSLT file.
The purpose of this XSLT is to generate an HTML view from a khmer file respecting Sumehr rules.
Input Sumehr file must be a valid sumehr.

XSLT file: http://www.w3.org/standards/xml/transformation#xslt
Kmehr  definition: https://www.ehealth.fgov.be/standards/kmehr/en/home/home/index.xml
Sumehr definition: https://www.ehealth.fgov.be/standards/kmehr/en/transaction_detail/home/transactions/transaction_detail/Sumehr-1-1.xml
eHealth: https://www.ehealth.fgov.be
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:kmehr="http://www.ehealth.fgov.be/standards/kmehr/schema/v1" xmlns:index="index">
	<xsl:output method="html" version="4.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<html>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<style>
		
		h1 {
			margin-top:0.4em;
			margin-bottom:0.4em;
		}
		
		h4 {
			margin-top:0.6em;
			margin-bottom:0.4em;
		}
			
		table {
			margin-left:2em;
			text-align: left;
			font-size: 12px;
			font-family: verdana;
			background: #96B6A4;
		}
 
		table thead  {
			cursor: pointer;
		}
 
		table thead tr,
		table tfoot tr {
			background: #96B6A4;
		}
 
		table tbody tr {
			background: #E9F0D9;
		}
		
		table tbody th {
			background: #96B6A4;
		}
 
		td, th {
			border: 1px solid Ivory  ;
		}
	</style>
	
	<script type="text/javascript">
	function toggleRow(part) {
		var rowTags=document.getElementsByTagName('tr');
		var row;
		var i = 0;	
			
		while(row=rowTags[i++]) {
				
			if (row.parentNode.id == part ) {
				 
				if ( row.id != 'short'){
					if (row.style.display == '') {
						row.style.display = 'none';
						changeCross(part, 'close')
					} else {
						row.style.display = '';
						changeCross(part, 'open')
					}
				}
			}
		}
	}
	
	function changeCross(subject, state) {
		
		if(state=='close') {
			document.getElementById(subject + "Cross").innerHTML =" [+] ";
		} else if(state=='open') {
			document.getElementById(subject + "Cross").innerHTML =" [-] ";
		}
	} 
	
	function expandView(size) {
		var rowTags=document.getElementsByTagName('tr');
		var row;
		var i = 0;	
		
		if (size == 'short') {
			while(row=rowTags[i++]) {
				if ( row.id != 'short'){
					row.style.display = 'none';
				}
				changeCross("author", "close");
				changeCross("patient", "close");
				if (document.getElementById("risksCross").innerHTML != "") { changeCross("risks", "close"); }
				if (document.getElementById("currentproblemsCross").innerHTML != "") { changeCross("currentproblems", "close"); }
				if (document.getElementById("relevantpassivecareelementsCross").innerHTML != "") { changeCross("relevantpassivecareelements", "close"); }
				if (document.getElementById("medicationCross").innerHTML != "") { changeCross("medication", "close"); }
				if (document.getElementById("vaccineCross").innerHTML != "") { changeCross("vaccine", "close"); }
				if (document.getElementById("gmdmanagerCross").innerHTML != "") { changeCross("gmdmanager", "close"); }
				if (document.getElementById("contactpersonCross").innerHTML != "") { changeCross("contactperson", "close"); }
				if (document.getElementById("contacthcpartyCross").innerHTML != "") { changeCross("contacthcparty", "close"); }
			}
		} else {
			while(row=rowTags[i++]) {
				row.style.display = '';
				
				changeCross("author", "open");
				changeCross("patient", "open");
				if (document.getElementById("risksCross").innerHTML != "") { changeCross("risks", "open"); }
				if (document.getElementById("currentproblemsCross").innerHTML != "") { changeCross("currentproblems", "open"); }
				if (document.getElementById("relevantpassivecareelementsCross").innerHTML != "") { changeCross("relevantpassivecareelements", "open"); }
				if (document.getElementById("medicationCross").innerHTML != "") { changeCross("medication", "open"); }
				if (document.getElementById("vaccineCross").innerHTML != "") { changeCross("vaccine", "open"); }
				if (document.getElementById("gmdmanagerCross").innerHTML != "") { changeCross("gmdmanager", "open"); }
				if (document.getElementById("contactpersonCross").innerHTML != "") { changeCross("contactperson", "open"); }
				if (document.getElementById("contacthcpartyCross").innerHTML != "") { changeCross("contacthcparty", "open"); }
			}
		}
	}
	
	</script>

			<body>
				<img src="data:image/gif;base64,R0lGODlh0AFuAPf6ANXaycvSu8zTvNPYxoCdjs3Uvs/VwNPZxoajlNDWwtTZyYmkltbby9vf0WqN
e8rQvLS+nT1qUmKGc4ymmGyOffz8/M/Vwtrez9LXxdfczMrRuW6QftHXw3SUg3GSgXKTgsDIrnWV
hJCpnNjdzXmYiHqZiXyaijlnT3iXh32bi8nQudzg036cjNPYyDZlTXCRgI6ommSIdYKfkODj2F6D
cIOgkc7UwczSvoWhkmaKd/v7+97i1t3h1NLYxMHJrsPKsluBbcjOucfOtsXMtEJtVsbMtmiLecLK
sJWuoZqxpcjPt1h/ajJhSc3TwNndzrrCptHWw3WWhcTMstDWw9LXx6e7sJevozpoUNXZyjRjStHW
xdTayJ61qXeWhsTLtLW+n7fAovT09MnPu7a/oKO4rdfcy9DVxL7GrFJ6ZVV8aPHy8ZOsny9fRqC2
q8LJsZKrntHXwpyzpzBgR97h1cfNuL/Hq7/IrPDw7rzEqcLKr+Tn4ZWwo/b49012YMHIsLzFqvr6
+sbOtNvl4Pj4+HqdjMXMtujq5PX29uzt6uTr6LjErdzl4O7u7Ors6LjLwoykjbjKwdnd0JOqla+8
oIOjk4yqm6S2oNve052vk+3y8LPFvLvLw8HRyaa+ssrX0XaUfYOdhc/a1a/EuubGydTd2J+ym/n5
+bTBqcFxeKm+tXuXf9Le2O7y8JyxoKEqNae1mMTSy8nQt+zU1qg4Qt7m4p63q/nx8bRVXezx7sfV
ztri382Nk82Ok+nu7Nbg297g19Pe2ZSojMvX0Z23qnGPdOXr6Lpja22McW+PeNmqrsrY0dzf1ae4
ovLj5MeAhq5HUNfbzvP29drd0qa9svL088DKssTKssd/huC4u9OboGGDaeLo5ezU1/3+/dDWwNqq
rvv8/IZZVqm6p/7///z9/d/n48DPyPf5+MjWz/j6+fv8+/X39s6rrMPMtObr6Pnx8snX0Nfg3OC4
vINhW2+Qf5scJ/39/XGWhP7+/v////v+/wAAAAAAAAAAAAAAAAAAACH5BAEAAPoALAAAAADQAW4A
AAj/APMJHEiwoMGDCBMqXMiwocOHCvFJnIgP4kOKGDNWtMixo8ePIEOKHJmQD6uTtHTpIpWr5SZN
MKvInEmzps2bOHPq3Emyp8+fAjVSJCjRntGjRyWSFMp0I9CnUKNKDckHFy6VoVrC1ESmjdc2ZMKK
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
dWAPdmEfdmJHvoAAADs=" align="right" />
				<button  style="width:10em"  onclick="expandView('long')">Expanded View</button> <text>    </text>  <button style="width:10em" onclick="expandView('short')">Short View</button>
				<xsl:apply-templates select="//kmehr:kmehrmessage"/>
			</body>
		</html>
	</xsl:template>
	
	<!-- Kmehr HEADER processing -->
	<xsl:template match="kmehr:header">
		<span style="color:#9B1C27"><h1 onclick="toggleRow('sumehr')" >Sumehr</h1></span>
		<div id="sumehr">
			<b>Creation date : </b>
			<xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text> <xsl:value-of select="kmehr:time"/><br/>
		</div>
	</xsl:template>

	
	<!-- Kmehr FOLDER processing -->
	
	<!-- AUTHOR -->
	<xsl:template match="kmehr:folder">
		<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('author')"><small id="authorCross"> [+] </small> Author</h4></span></div>
			<table>
					<tbody id="author"> 
						<xsl:apply-templates select="//kmehr:transaction/kmehr:author/kmehr:hcparty"/>
				</tbody>
			</table> 
			
		<!-- PATIENT -->	
		<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('patient')"><small id="patientCross"> [+] </small> Patient</h4></span></div>
		<table>
			<tbody id="patient">
				<xsl:apply-templates select="kmehr:patient"/>
			</tbody>
		</table>
		<xsl:apply-templates select="kmehr:transaction"/>
	</xsl:template>
	
	
	<!-- TRANSACTION PROCESSING -->
	<xsl:template match="kmehr:transaction">
		<xsl:for-each select="kmehr:item">
			<xsl:sort select="kmehr:cd[@S='CD-ITEM']"/>
		</xsl:for-each>
		<xsl:choose>
			<xsl:when test="//kmehr:item/kmehr:cd[@S='CD-ITEM' and .='allergy'] or 
									//kmehr:item/kmehr:cd[@S='CD-ITEM' and .='adr'] or 
									//kmehr:item/kmehr:cd[@S='CD-ITEM' and .='socialrisk'] or 
									//kmehr:item/kmehr:cd[@S='CD-ITEM' and .='risk']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('risks')"><small id="risksCross"> [+] </small> Risks</h4></span></div>
				<span style="color:#9B1C27; cursor:pointer; margin-left:0.3em"><b onclick="toggleRow('risks')">Allergies</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">			
							<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='allergy']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; cursor:pointer; margin-left:0.3em"><b onclick="toggleRow('risks')">Adverse drug reactions</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">			
						<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='adr']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; cursor:pointer; margin-left:0.3em" ><b align="left" onclick="toggleRow('risks')">Social risks</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">
						<xsl:apply-templates select="//kmehr:item[kmehr:cd='socialrisk']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; cursor:pointer; margin-left:0.3em"><b onclick="toggleRow('risks')">Other risks</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">
						<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='risk']"/>
					</tbody>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="risksCross"/> Risks</h4></span></div>
				<span style="color:#9B1C27; margin-left:0.3em"><b>Allergies</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">			
							<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='allergy']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; margin-left:0.3em"><b>Adverse drug reactions</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">			
						<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='adr']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; margin-left:0.3em" ><b>Social risks</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">
						<xsl:apply-templates select="//kmehr:item[kmehr:cd='socialrisk']"/>
					</tbody>
				</table>
				<span style="color:#9B1C27; margin-left:0.3em"><b>Other risks</b></span>
				<table style="margin-bottom:3pt">
					<tbody id="risks">
						<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='risk']"/>
					</tbody>
				</table>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='healthcareelement' and kmehr:lifecycle[kmehr:cd[@S='CD-LIFECYCLE']='active']]">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('currentproblems')"><small id="currentproblemsCross"> [+] </small> Current Problems</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="currentproblemsCross"/> Current Problems</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="currentproblems">	
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='healthcareelement' and kmehr:lifecycle/kmehr:cd[@S='CD-LIFECYCLE']='active']"/>
			</tbody>
		</table>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='healthcareelement' and kmehr:lifecycle[kmehr:cd[@S='CD-LIFECYCLE']='inactive']]">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('relevantpassivecareelements')"><small id="relevantpassivecareelementsCross"> [+] </small> Relevant passive care elements</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="relevantpassivecareelementsCross"/> Relevant passive care elements</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="relevantpassivecareelements">
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='healthcareelement' and kmehr:lifecycle[kmehr:cd[@S='CD-LIFECYCLE']='inactive']]"/>
			</tbody>
		</table> 
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('medication')"><small id="medicationCross"> [+] </small> Active medication</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="medicationCross"/> Active medication</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="medication">
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']"/>
			</tbody>
		</table>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='vaccine']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('vaccine')"><small id="vaccineCross"> [+] </small> Administered vaccines</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="vaccineCross"/> Administered vaccines</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="vaccine">
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='vaccine']"/>
			</tbody>
		</table>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='contactperson']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('contactperson')"><small id="contactpersonCross"> [+] </small> Contact Persons</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="contactpersonCross"/> Contact Persons</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="contactperson">
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='contactperson']"/>
			</tbody>
		</table>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='gmdmanager']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('gmdmanager')"><small id="gmdmanagerCross"> [+] </small> GMD Manager</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="gmdmanagerCross"/> GMD Manager</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table>
			<tbody id="gmdmanager">
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='gmdmanager']"/>
			</tbody>
		</table>
		<xsl:choose>
			<xsl:when test="//kmehr:item[kmehr:cd[@S='CD-ITEM']='contacthcparty']">
				<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4 onclick="toggleRow('contacthcparty')"><small id="contacthcpartyCross"> [+] </small> Healthcare professional contact</h4></span></div>
			</xsl:when>
			<xsl:otherwise>
				<div style="background:#D9E0C9"><span style="color:#9B1C27"><h4><small id="contacthcpartyCross"/> Healthcare professional contact</h4></span></div>
			</xsl:otherwise>
		</xsl:choose>
		<table> 
			<tbody id="contacthcparty">	
				<xsl:apply-templates select="//kmehr:item[kmehr:cd[@S='CD-ITEM']='contacthcparty']"/>
			</tbody>
		</table>
		<div style="background:#D9E0C9; cursor:pointer"><span style="color:#9B1C27"><h4>Sumehr end</h4></span></div>
		<table>
			<tbody>
				<xsl:apply-templates select="//kmehr:item[not(kmehr:cd='contactperson' 
				or kmehr:cd='gmdmanager' 
				or kmehr:cd='hcparty'
				or kmehr:cd='risk'
				or kmehr:cd='contacthcparty' 
				or kmehr:cd='socialrisk' 
				or kmehr:cd='healthcareelement' 
				or kmehr:cd='allergy'
				or kmehr:cd='medication'
				or kmehr:cd='vaccine' 
				or kmehr:cd='adr')]"/>
			</tbody>
		</table>
		
	</xsl:template>
	<!-- END Transaction Processing -->
	
	<!-- ITEM PROCESSING -->
	<xsl:template match="kmehr:item">
				<xsl:choose>
					<xsl:when test="kmehr:cd[@S='CD-ITEM']='contactperson'">
						<xsl:apply-templates select="./kmehr:content/kmehr:person"/>
					</xsl:when>
					<xsl:when test="kmehr:cd[@S='CD-ITEM']='gmdmanager'">
						<xsl:apply-templates select="./kmehr:content/kmehr:hcparty"/>
					</xsl:when>
					<xsl:when test="kmehr:cd[@S='CD-ITEM']='contacthcparty'">
						<xsl:apply-templates select="./kmehr:content/kmehr:hcparty"/>
					</xsl:when>
					
					<!-- RISKS AND HEALTHCARE ELEMENTS PROCESSING -->
					<xsl:when test="kmehr:cd[@S='CD-ITEM']='risk' 
					or kmehr:cd[@S='CD-ITEM']='socialrisk' 
					or kmehr:cd[@S='CD-ITEM']='healthcareelement' 
					or kmehr:cd[@S='CD-ITEM']='allergy' 
					or kmehr:cd[@S='CD-ITEM']='adr'">
					
					<xsl:for-each select="kmehr:content/kmehr:text">
						<xsl:if test="position()=1">
							<tr id="short">
								<th colspan="3" align="center" >
									<!-- no more used <xsl:text>Label : </xsl:text> -->
									<xsl:value-of select="."/>
								</th>
							</tr>
						</xsl:if>
						<xsl:if test="not(position()=1)">
							<tr id="short">
								<th colspan="3">
									<xsl:text> &amp; </xsl:text><xsl:value-of select="."/>
								</th>
							</tr>
						</xsl:if>
					</xsl:for-each>		
						
						<tr style="display:none">
							<th>IBUI<xsl:text> </xsl:text><xsl:value-of select="kmehr:content/kmehr:cd[@S='CD-CLINICAL']/@SV"/></th>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='CD-CLINICAL']"/>
							</td>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='CD-CLINICAL']/@DN"/>
							</td>
						</tr>
						<tr style="display:none">
							<th>ICPC<xsl:text> </xsl:text>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICPC']/@SV"/>
							</th>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICPC']"/>
							</td>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICPC']/@DN"/>
							</td>
						</tr>
						<tr style="display:none">
							<th>ICD<xsl:text> </xsl:text>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICD']/@SV"/>
							</th>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICD']"/>
							</td>
							<td>
								<xsl:value-of select="kmehr:content/kmehr:cd[@S='ICD']/@DN"/>
							</td>
						</tr>
						<xsl:if test="kmehr:cd[@S='CD-ITEM']='healthcareelement'">
							<tr  id="short">
								<th>Period</th>
								<td colspan="2">
									<b>start: </b>
									<xsl:text> </xsl:text><xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:beginmoment/kmehr:time"/><xsl:value-of select="kmehr:beginmoment/kmehr:text"/>
									<xsl:choose>
										<xsl:when test="kmehr:lifecycle/kmehr:cd[@S='CD-LIFECYCLE']='inactive'">
											<xsl:text>     </xsl:text>
											<b>end: </b>
											<xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:time"/><xsl:value-of select="kmehr:endmoment/kmehr:text"/>
										</xsl:when>
										<xsl:when test="kmehr:lifecycle/kmehr:cd[@S='CD-LIFECYCLE']='active'">
											<xsl:if test="kmehr:endmoment">
												<xsl:text>     </xsl:text>
												<b>end: </b>
												<xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:time"/><xsl:value-of select="kmehr:endmoment/kmehr:text"/>
											</xsl:if>
										</xsl:when>
									</xsl:choose>
								</td>
							</tr>
						</xsl:if>
					</xsl:when>
					
					<!-- MEDICATION AND VACCINE -->
					<xsl:when test="kmehr:cd[@S='CD-ITEM']='medication' or kmehr:cd[@S='CD-ITEM']='vaccine'">
							<tr id="short">	
								<th colspan="3" align="center"> <!-- no more used <xsl:text>Name : </xsl:text> -->
									<xsl:choose>
										<xsl:when test="kmehr:content/kmehr:medicinalproduct/kmehr:intendedname">
											<xsl:value-of select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedname"/>
										</xsl:when>
										<xsl:when test="kmehr:content/kmehr:compoundprescription">
											<xsl:value-of select="kmehr:content/kmehr:compoundprescription"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="kmehr:content/kmehr:substanceproduct/kmehr:intendedname"/>
										</xsl:otherwise>
									</xsl:choose>
								</th>
							</tr>
							
							<xsl:if test="kmehr:cd[@S='CD-ITEM']='vaccine'">
								<xsl:choose>
									<xsl:when test="kmehr:content/kmehr:cd[@S='CD-VACCINEINDICATION']">
										<xsl:for-each select="kmehr:content/kmehr:cd[@S='CD-VACCINEINDICATION']">
											<tr  id="short">
												<th>Indication  <xsl:value-of select="@SV"/></th>
												<td colspan="2">
													<xsl:choose>
														<xsl:when test="@DN">
															<xsl:value-of select="."/><xsl:text>   (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="."/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
										</xsl:for-each>
									</xsl:when>
									<xsl:otherwise>
										<tr  id="short">
											<th>Indication</th>
											<td colspan="2"></td>
										</tr>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
							
							<xsl:choose>
								<xsl:when test="kmehr:content/kmehr:cd[@S='CD-ATC']">
									<xsl:for-each select="kmehr:content/kmehr:cd[@S='CD-ATC']">
										<tr style="display:none">
											<th>Code ATC  <xsl:value-of select="@SV"/></th>
											<td colspan="2">
												<xsl:choose>
													<xsl:when test="@DN">
														<xsl:value-of select="."/><xsl:text>   (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="."/>
													</xsl:otherwise>
												</xsl:choose>
											</td>
										</tr>
									</xsl:for-each>
								</xsl:when>
								<xsl:otherwise>
									<tr style="display:none">
										<th>Code ATC</th>
										<td colspan="2"></td>
									</tr>
								</xsl:otherwise>
							</xsl:choose>
						
							<xsl:choose>
								<xsl:when test="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']">
									<tr style="display:none">
										<th>Code CNK <xsl:value-of select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']/@SV"/></th>
										<td colspan="2">
											<xsl:value-of select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']"/>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="kmehr:content/kmehr:substanceproduct/kmehr:intendedcd[@S='CD-INNCLUSTER']">
									<tr style="display:none">
										<th>Code INN <xsl:value-of select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-INNCLUSTER']/@SV"/></th>
										<td colspan="2">
											<xsl:value-of select="kmehr:content/kmehr:medication/kmehr:intendedcd[@S='CD-INNCLUSTER']"/>
										</td>
									</tr>
								</xsl:when>
							</xsl:choose>
							
							<xsl:choose>
								<xsl:when test="kmehr:cd[@S='CD-ITEM']='vaccine'">
									<tr id="short">
										<th>Administration date</th>
										<td colspan="2">
											<xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:beginmoment/kmehr:time"/><xsl:value-of select="kmehr:beginmoment/kmehr:text"/>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="kmehr:cd[@S='CD-ITEM']='medication'">
									<tr  id="short">
										<th>Period</th>
										<td colspan="2">
											<b>start: </b>
											<xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:beginmoment/kmehr:time"/><xsl:value-of select="kmehr:beginmoment/kmehr:text"/>
											<xsl:text>   </xsl:text>
											<b>end: </b>
											<xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:time"/><xsl:value-of select="kmehr:endmoment/kmehr:text"/>
										</td>
									</tr>
								</xsl:when>
							</xsl:choose>
							
							<xsl:if test="kmehr:content/*/kmehr:deliveredname">
								<tr style="display:none">
									<th>Delivered name</th>
									<td colspan="2">
												<xsl:value-of select="kmehr:content/*/kmehr:deliveredname"/>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="kmehr:content/*/kmehr:deliveredcd">
								<tr style="display:none">
									<th>Delivered code <xsl:value-of select="kmehr:content/*/kmehr:deliveredcd/@S"/> <xsl:value-of select="kmehr:content/*/kmehr:deliveredcd/@SV"/></th>
									<td colspan="2">
												<xsl:value-of select="kmehr:content/*/kmehr:deliveredcd"/>
									</td>
								</tr>
							</xsl:if>
					</xsl:when>
					
					<!-- OTHER ITEMS ? -->					
					<xsl:otherwise>
                        <h3>Unexpected item element...</h3>
                        <xsl:for-each select="node()[name()]">
                            <tr id="short">
                                <th><xsl:value-of select="name()"/></th>
                                <td colspan="2"><xsl:value-of select="."/></td>
                            </tr>
                        </xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
				
				<!-- GENERIC ITEM ELEMENTS PROCESS -->
				<xsl:for-each select="node()[name()]">
					<xsl:choose>
						<xsl:when test="name() = 'cd'">
							<xsl:apply-templates select="."/>
						</xsl:when>
						<xsl:when test="name() = 'id'">
						</xsl:when>
						<xsl:when test="name() = 'content'">
						</xsl:when>
						<xsl:when test="name() = 'beginmoment'">
							<xsl:if test="not(../kmehr:cd[@S='CD-ITEM']='vaccine' or ../kmehr:cd[@S='CD-ITEM']='medication' or ../kmehr:cd[@S='CD-ITEM']='healthcareelement' )">
								<tr style="display:none">
									<th>Start date</th>
									<td>
										<xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:time"/><xsl:value-of select="kmehr:text"/>
									</td>
								</tr>
							</xsl:if>
						</xsl:when>
						<xsl:when test="name() = 'endmoment'">
							<xsl:if test="not(../kmehr:cd[@S='CD-ITEM']='medication' or ../kmehr:cd[@S='CD-ITEM']='healthcareelement' )">
								<tr style="display:none">
									<th>End date</th>
									<td>
										<xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:time"/><xsl:value-of select="kmehr:text"/>
									</td>
								</tr>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<tr style="display:none">
								<th><xsl:value-of select="name()"/></th>
								<td colspan="2"><xsl:value-of select="."/></td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
	</xsl:template>
	
	
	<!-- PROCESSING PATIENT-->
	<xsl:template match="kmehr:patient">
	
		<tr id="short">
			<th colspan="3" align="center">
				<!-- no more used <xsl:text>Patient: </xsl:text> -->
				<xsl:value-of select="kmehr:firstname[1]"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:familyname"/>
			</th>
		</tr>
		
		<xsl:if test="not(kmehr:id[@S='ID-PATIENT' or @S='INSS'])">
			<tr style="display:none"><th>INSS No</th><td></td></tr>
		</xsl:if>
		
		<xsl:for-each select="kmehr:id">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
		<tr style="display:none">
			<th>First names</th>
			<td>
				<xsl:value-of select="kmehr:firstname"/>
			</td>
		</tr>
		<tr style="display:none">
			<th>Family name</th>
			<td>
				<xsl:value-of select="kmehr:familyname"/>
			</td>
		</tr>
		<tr id="short">
			<th>Sex</th>
			<td>
				<xsl:value-of select="kmehr:sex/kmehr:cd[@S='CD-SEX']"/>
			</td>
		</tr>
		
		<tr id="short">
			<th>Birthdate</th>
			<td>
				<xsl:value-of select="kmehr:birthdate/kmehr:date"/><xsl:value-of select="kmehr:birthdate/kmehr:year"/><xsl:value-of select="kmehr:birthdate/kmehr:yearmonth"/>
			</td>
		</tr>
		<xsl:if test="kmehr:birthlocation">
			<tr style="display:none">
				<th>Birth location</th>
				<td>
					<xsl:value-of select="kmehr:birthlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:birthlocation/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:deathdate">
			<tr style="display:none">
				<th>Death date</th>
				<td>
					<xsl:value-of select="kmehr:deathdate/kmehr:date"/><xsl:value-of select="kmehr:deathdate/kmehr:year"/><xsl:value-of select="kmehr:deathdate/kmehr:yearmonth"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:deathlocation">
			<tr style="display:none">
				<th>Death location</th>
				<td>
					<xsl:value-of select="kmehr:deathlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:deathlocation/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:nationality">
			<tr style="display:none">
				<th>Nationality</th>
				<td>
					<xsl:value-of select="kmehr:nationality/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<tr id="short">
			<th>Usual language</th>
			<td>
				<xsl:value-of select="kmehr:usuallanguage"/>
			</td>
		</tr>
		
		<xsl:if test="not(kmehr:address)">
			<tr id="short"><th>address</th><td></td></tr>
		</xsl:if>
		<xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
			<tr style="display:none"><th>phone</th><td></td></tr>
		</xsl:if>
		<xsl:apply-templates select="kmehr:address"/>
		<xsl:apply-templates select="kmehr:telecom"/>
		
		<xsl:if test="kmehr:recorddatetime">
			<tr style="display:none">
				<th>Record Date</th>
				<td><xsl:value-of select="kmehr:recorddatetime"/></td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:recorddatetime">
			<tr style="display:none">
				<th>Comments </th>
				<td><xsl:value-of select="kmehr:text"/></td>
			</tr>
		</xsl:if>
	<!-- END Patient specific section -->
	</xsl:template>
	
	
	<!-- PROCESSING PERSON -->
	<xsl:template match="kmehr:person">
	
		<tr id="short">
			<th colspan="3"  align="center">
				<!-- no more used <xsl:value-of select="../../kmehr:cd[@S='CD-ITEM']"/><xsl:text>: </xsl:text> -->
				<xsl:value-of select="kmehr:firstname"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:familyname"/>
			</th>
		</tr>

		<xsl:for-each select="kmehr:id">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
		
		<tr style="display:none">
			<th>First names</th>
			<td>
				<xsl:value-of select="kmehr:firstname"/>
			</td>
		</tr>
		<tr style="display:none">
			<th>Family name</th>
			<td>
				<xsl:value-of select="kmehr:familyname"/>
			</td>
		</tr>
		<tr style="display:none">
			<th>Sex</th>
			<td>
				<xsl:value-of select="kmehr:sex/kmehr:cd[@S='CD-SEX']"/>
			</td>
		</tr>
		
		<xsl:if test="kmehr:birthdate">
			<tr style="display:none">
				<th>Birthdate</th>
				<td>
					<xsl:value-of select="kmehr:birthdate/kmehr:date"/><xsl:value-of select="kmehr:birthdate/kmehr:year"/><xsl:value-of select="kmehr:birthdate/kmehr:yearmonth"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:birthlocation">
			<tr style="display:none">
				<th>Birth location</th>
				<td>
					<xsl:value-of select="kmehr:birthlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:birthlocation/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:deathdate">
			<tr style="display:none">
				<th>Death date</th>
				<td>
					<xsl:value-of select="kmehr:deathdate/kmehr:date"/><xsl:value-of select="kmehr:deathdate/kmehr:year"/><xsl:value-of select="kmehr:deathdate/kmehr:yearmonth"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:deathlocation">
			<tr style="display:none">
				<th>Death location</th>
				<td>
					<xsl:value-of select="kmehr:deathlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:deathlocation/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:nationality">
			<tr style="display:none">
				<th>Nationality</th>
				<td>
					<xsl:value-of select="kmehr:nationality/kmehr:cd[@S='CD-COUNTRY']"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:usuallanguage">
			<tr style="display:none">
				<th>Usual language</th>
				<td>
					<xsl:value-of select="kmehr:usuallanguage"/>
				</td>
			</tr>
		</xsl:if>
		
		<xsl:if test="not(kmehr:address)">
			<tr style="display:none"><th>address</th><td></td></tr>
		</xsl:if>
		<xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
			<tr style="display:none"><th>phone</th><td></td></tr>
		</xsl:if>
		<xsl:apply-templates select="kmehr:address"/>
		<xsl:apply-templates select="kmehr:telecom"/>
		
		<xsl:if test="kmehr:recorddatetime">
			<tr style="display:none">
				<th>Record Date</th>
				<td><xsl:value-of select="kmehr:recorddatetime"/></td>
			</tr>
		</xsl:if>
		<xsl:if test="kmehr:recorddatetime">
			<tr style="display:none">
				<th>Comments </th>
				<td><xsl:value-of select="kmehr:text"/></td>
			</tr>
		</xsl:if>
	<!-- Person specific section -->
	</xsl:template>
	
	<!-- HCPARTY PROCESSING -->
	<xsl:template match="kmehr:hcparty">
		<xsl:choose>
			<xsl:when test="name(..)='author'">
				<tr id="short">
					<th colspan="3"  align="center">
						<!-- no more used <xsl:text>Author hcparty : </xsl:text> -->
						<xsl:value-of select="kmehr:name"/><xsl:value-of select="kmehr:firstname[1]"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:familyname"/>
					</th>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr id="short">
					<th colspan="3"  align="center">
						<!-- no more used <xsl:value-of select="../../kmehr:cd[@S='CD-ITEM']"/><xsl:text> : </xsl:text> -->
						<xsl:value-of select="kmehr:name"/><xsl:value-of select="kmehr:firstname"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:familyname"/>
					</th>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	
		<xsl:if test="not(kmehr:id[@S='ID-HCPARTY'])">
			<tr style="display:none"><th>NIHII / INAMI / RIZIV No</th><td></td></tr>
		</xsl:if>
		<xsl:for-each select="kmehr:id">
					<xsl:apply-templates select="."/>
		</xsl:for-each>
		
		<xsl:if test="not(kmehr:id[@S='INSS']) and name(..)='author'">
			<tr style="display:none"><th>INSS No</th><td></td></tr>
		</xsl:if>
		<xsl:if test="not(kmehr:cd[@S='CD-HCPARTY']) and name(..)='author'">
			<tr style="display:none"><th>Role</th><td></td></tr>
		</xsl:if>	
		<xsl:for-each select="kmehr:cd">
					<xsl:apply-templates select="."/>
		</xsl:for-each>
		
		<xsl:choose>
			<xsl:when test="kmehr:firstname">
				<tr style="display:none">
					<th>First names</th>
					<td>
						<xsl:value-of select="kmehr:firstname"/>
					</td>
				</tr>
				<tr style="display:none">
					<th>Family name</th>
					<td>
						<xsl:value-of select="kmehr:familyname"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:when test="kmehr:name">
				<tr style="display:none">
					<th>Name</th>
					<td>
						<xsl:value-of select="kmehr:name"/>
					</td>
				</tr>
			</xsl:when>
		</xsl:choose>
		
		<xsl:if test="not(kmehr:address)">
			<tr style="display:none"><th>address</th><td></td></tr>
		</xsl:if>
		<xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
			<tr style="display:none"><th>phone</th><td></td></tr>
		</xsl:if>
		<xsl:apply-templates select="kmehr:address"/>
		<xsl:apply-templates select="kmehr:telecom"/>
		
	</xsl:template>
	
	
	
		<!-- TELECOM PROCESSING -->
	<xsl:template match="kmehr:telecom">	
		<tr style="display:none">
			<th><xsl:value-of select="kmehr:cd[@S='CD-TELECOM']"/></th>
			<td>
				<xsl:value-of select="kmehr:telecomnumber"/> (<xsl:value-of select="kmehr:cd[@S='CD-ADDRESS']"/>)
			</td>
		</tr>
	</xsl:template>
	
	<!-- ADDRESS PROCESSING -->
	<xsl:template match="kmehr:address">
		<xsl:choose>
			<xsl:when test="name(..)='patient'">
				<tr id="short">
					<th>
				<xsl:value-of select="kmehr:cd"/><xsl:text> address</xsl:text>
					</th>
					<td>
					<xsl:choose>
						<xsl:when test="kmehr:text">
							<xsl:value-of select="kmehr:text"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="kmehr:street"/>
							<xsl:text> </xsl:text><xsl:value-of select="kmehr:housenumber"/>
							<xsl:if test="kmehr:postboxnumber">
								<xsl:text> PB : </xsl:text><xsl:value-of select="kmehr:postboxnumber"/>
							</xsl:if>
							<xsl:text> ; </xsl:text>
							<xsl:text> </xsl:text><xsl:value-of select="kmehr:zip"/>
							<xsl:if test="kmehr:nis">
								<xsl:text> nis : </xsl:text><xsl:value-of select="kmehr:nis"/>
							</xsl:if>
							<xsl:text> </xsl:text><xsl:value-of select="kmehr:city"/>
							<xsl:if test="kmehr:district">
								<xsl:text> district : </xsl:text><xsl:value-of select="kmehr:district"/>
							</xsl:if>
							<xsl:if test="kmehr:country">
								<xsl:text> / </xsl:text><xsl:value-of select="kmehr:country/kmehr:cd"/>
							</xsl:if>
							<xsl:if test="kmehr:text">
								<xsl:text> / comment : </xsl:text><xsl:value-of select="kmehr:text"/>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr style="display:none">
			<th>
				<xsl:value-of select="kmehr:cd"/><xsl:text> address</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="kmehr:text">
						<xsl:value-of select="kmehr:text"/>
					</xsl:when>
					<xsl:otherwise>
								<xsl:value-of select="kmehr:street"/>
								<xsl:text> </xsl:text><xsl:value-of select="kmehr:housenumber"/>
								<xsl:if test="kmehr:postboxnumber">
									<xsl:text> PB : </xsl:text><xsl:value-of select="kmehr:postboxnumber"/>
								</xsl:if>
								<xsl:text> ; </xsl:text>
								<xsl:text> </xsl:text><xsl:value-of select="kmehr:zip"/>
								<xsl:if test="kmehr:nis">
									<xsl:text> nis : </xsl:text><xsl:value-of select="kmehr:nis"/>
								</xsl:if>
								<xsl:text> </xsl:text><xsl:value-of select="kmehr:city"/>
								<xsl:if test="kmehr:district">
									<xsl:text> district : </xsl:text><xsl:value-of select="kmehr:district"/>
								</xsl:if>
								<xsl:if test="kmehr:country">
									<xsl:text> / </xsl:text><xsl:value-of select="kmehr:country/kmehr:cd"/>
								</xsl:if>
								<xsl:if test="kmehr:text">
									<xsl:text> / comment : </xsl:text><xsl:value-of select="kmehr:text"/>
								</xsl:if>
							</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- ID PROCESSING -->
	<xsl:template match="kmehr:id">
		<xsl:choose>
			<xsl:when test="@S='ID-KMEHR'">
			</xsl:when>
			<xsl:otherwise>
				<tr style="display:none">
					<th>
						<xsl:choose>
							<!-- no more used: replaced bay id INSS
							<xsl:when test="@S='LOCAL' and @SL='ID-PATIENT' and name(..)='hcparty'">
								<xsl:text>INSS No</xsl:text>
							</xsl:when> -->
							<xsl:when test="@S='LOCAL'">
								<xsl:text>(local id) </xsl:text><xsl:value-of select="@SL"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
							</xsl:when>
							<xsl:when test="@S='INSS'">
								<xsl:text>INSS No</xsl:text>
							</xsl:when>
							<xsl:when test="@S='ID-PATIENT'">
								<xsl:text>INSS No</xsl:text>
							</xsl:when>
							<xsl:when test="@S='ID-HCPARTY'">
								<xsl:text>NIHII / INAMI / RIZIV</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@S"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
							</xsl:otherwise>
						</xsl:choose>
					</th>
					<td colspan="2">
						<xsl:value-of select="."/>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- CD PROCESSING -->
	<xsl:template match="kmehr:cd">
		<xsl:choose>
			<xsl:when test="@S='CD-ITEM'">
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="@S='CD-HCPARTY' and ../../../kmehr:cd[@S='CD-ITEM']='contacthcparty'">
						<tr id="short">
							<th>
								<xsl:text>Role</xsl:text>
							</th>
							<td colspan="2">
								<xsl:value-of select="."/>
								<xsl:if test="@DN">
									<xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
								</xsl:if>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="@S='CD-CONTACT-PERSON' and ../kmehr:cd[@S='CD-ITEM']='contactperson'">
						<tr id="short">
							<th>
								<xsl:text>Family Tie</xsl:text>
							</th>
							<td colspan="2">
								<xsl:value-of select="."/>
								<xsl:if test="@DN">
									<xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
								</xsl:if>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr style="display:none">
							<th>
								<xsl:choose>
									<xsl:when test="@S='CD-ITEM'">
									</xsl:when>
									<xsl:when test="@S='LOCAL'">
										<xsl:text>(local cd) </xsl:text><xsl:value-of select="@SL"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
									</xsl:when>
									<xsl:when test="@S='CD-HCPARTY'">
										<xsl:text>Role</xsl:text>
									</xsl:when>
									<xsl:when test="@S='CD-CONTACT-PERSON'">
										<xsl:text>Family Tie</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@S"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
									</xsl:otherwise>
								</xsl:choose>
							</th>
							<td colspan="2">
                                <!-- If we want to use secondary files
								<xsl:variable name="code-group"><xsl:value-of select="@S"/></xsl:variable>
                                <xsl:variable name="code"><xsl:value-of select="current()"/></xsl:variable>
                                <xsl:choose>
                                  <xsl:when test="document('index.xml')//index:code-group[@S=$code-group]/index:translation/@code = $code">
                                    <xsl:value-of select="document('index.xml')//index:code-group[@S='CD-HCPARTY']/index:translation[@code='persphysician']"/>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                  </xsl:otherwise>
                                </xsl:choose> 
                                -->
                                <xsl:value-of select="."/>
								<xsl:if test="@DN">
									<xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
								</xsl:if>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
