<%@ page isELIgnored="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="org.taktik.icure.reports" />
<fmt:requestEncoding value="utf-8"/>

<%@ page import="org.taktik.icure.entities.Patient" %>
<%@ page import="org.taktik.icure.services.external.http.ReportServlet" %>
<%@ page import="org.taktik.icure.entities.Invoice" %>
<%@ page import="org.taktik.icure.entities.embed.InvoicingCode" %>
<%@ page import="org.taktik.icure.entities.User" %>
<%@ page import="org.taktik.icure.entities.HealthcareParty" %>
<%@ page import="org.taktik.icure.entities.embed.Address" %>
<%@ page import="org.taktik.icure.entities.embed.AddressType" %>
<%@ page import="org.taktik.icure.entities.embed.Telecom" %>
<%@ page import="org.taktik.icure.entities.embed.TelecomType" %>
<%@ page import="org.taktik.icure.be.drugs.civics.VerseInfos" %>
<%@ page import="org.taktik.icure.be.drugs.civics.ParagraphInfos" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>
<%!
    private void addVerses(List<VerseInfos> verses, SortedSet<VerseInfos> versesToAdd) {
        for (VerseInfos v:versesToAdd) {
            verses.add(v);
            addVerses(verses,v.getVerses());
        }
    }
%>
<%
    String lang = request.getParameter("lang");
    String chapterName = request.getParameter("chapterName");
    String paragraphName = request.getParameter("paragraphName");

    Map<String,Object> iCureMap = (Map<String,Object>) pageContext.getAttribute("icure", PageContext.REQUEST_SCOPE);

    ReportServlet.LogicWrapper l = (ReportServlet.LogicWrapper) iCureMap.get("logic");

    lang = l.getDrugsLogic().getAvailableLanguage(lang);
    l.log("Language is "+lang);

    ParagraphInfos paragraphInfos = l.getDrugsLogic().getParagraphInfos(chapterName, paragraphName);
    List<VerseInfos> verses = new ArrayList<VerseInfos>();

    addVerses(verses, paragraphInfos.getHeaderVerse().getVerses());

    pageContext.setAttribute("lang", lang);
    pageContext.setAttribute("paragraph", paragraphInfos);
    pageContext.setAttribute("verses", verses);
%>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <script language="javascript">
			function updateVerses(numVerse,isChecked) {
                var el = document.getElementsByTagName('body')[0];
                var dv = el.getAttribute('data-verses');
				var verses = dv.length?dv.split(','):[];
				var idx = verses.indexOf(numVerse);

				if (idx>=0 && !isChecked) {
					verses.splice(idx,1);
					el.setAttribute('data-verses',verses.join(','));
				} else if (idx==-1 && isChecked) {
					verses.push(numVerse);
					el.setAttribute('data-verses',verses.join(','));
				}
			}
    </script>
    <title>${paragraph.paragraphName}</title>
</head>

<body id="chapter4verses" data-verses="">
<h2>Chapter ${paragraph.chapterName}, paragraph ${paragraph.paragraphName}</h2>
<p><c:if test="${lang=='fr'}"><c:out value="${paragraph.keyStringFr}" escapeXml="true"/></c:if><c:if test="${lang=='nl'}"><c:out value="${paragraph.keyStringNl}" escapeXml="true"/></c:if></p>
<p><c:if test="${lang=='fr'}"><c:out value="${paragraph.headerVerse.textFr}" escapeXml="true"/></c:if><c:if test="${lang=='nl'}"><c:out value="${paragraph.headerVerse.textNl}" escapeXml="true"/></c:if></p>

<form action="#">
    <ul>
        <c:set var="pv"/>
        <c:set var="delta" value="${0}"/>
        <c:forEach items="${verses}" var="v" varStatus="i">
            <c:if test="${i.index>0 && v.verseLevel > pv.verseLevel}"><ul><c:set var="delta" value="${delta+1}"/></c:if>
            <c:if test="${i.index>0 && v.verseLevel < pv.verseLevel}"></ul><c:set var="delta" value="${delta-1}"/></c:if>
            <li style="background-color: ${v.checkBoxInd == 'Y'?'#ffa':'#fff'};">
                <c:if test="${v.checkBoxInd == 'Y'}"><input type="checkbox" id="${v.verseNum}" onclick="updateVerses('${v.verseNum}',this.checked);"/></c:if>
                <c:if test="${lang=='fr'}"><c:out value="${v.textFr}" escapeXml="true"/> </c:if>
                <c:if test="${lang=='nl'}"><c:out value="${v.textNl}" escapeXml="true"/></c:if> [<c:out value="${v.verseNum}" escapeXml="true"/>]
                <c:if test="${v.minCheckNum>0}"><span style="background-color: #fb9">[Veuillez cocher au minimum ${v.minCheckNum} case(s) ci-dessous]</span></c:if>
            </li>
            <c:set var="pv" value="${v}"/>
        </c:forEach>
        <c:forEach begin="${0}" end="${delta}"></ul></c:forEach>
    </ul>
</form>
</body>
</html>