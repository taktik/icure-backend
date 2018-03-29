<%@ page isELIgnored="false" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%--@elvariable id="locale" type="String"--%>
<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="org.taktik.icure.reports" />
<fmt:requestEncoding value="utf-8" />

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
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<html>
<%

    String patientId = request.getParameter( "patientId" );
    String invoiceId = request.getParameter( "invoiceId" );

    Map<String,Object> iCureMap = (Map<String,Object>) pageContext.getAttribute("icure", PageContext.REQUEST_SCOPE);

    ReportServlet.LogicWrapper l = (ReportServlet.LogicWrapper) iCureMap.get("logic");

    Patient patient = l.getPatientLogic().getPatient(patientId);
    Invoice invoice = l.getInvoiceLogic().getInvoice(invoiceId);

    User user = l.getUserLogic().getUser(invoice.getAuthor());
    HealthcareParty hcp = l.getHealthcarePartyLogic().getHealthcareParty(user.getHealthcarePartyId());

    while(invoice.getInvoicingCodes().size()<10) {
        invoice.getInvoicingCodes().add(new InvoicingCode());
    }

    pageContext.setAttribute("patient", patient);
    pageContext.setAttribute("invoice", invoice);
    pageContext.setAttribute("user", user);
    pageContext.setAttribute("hcp", hcp);

    pageContext.setAttribute("invoice_date", new Date(invoice.getInvoiceDate()) );

    Address workAddress =  new Address();
    String workPhone = "";
    String workEmail = "";
    String workFax = "";

    for (Address a:hcp.getAddresses()) {
        if (a.getAddressType() != null && a.getAddressType().equals(AddressType.work)) {
            for (Telecom t:a.getTelecoms()) {
                if (t.getTelecomType() != null && t.getTelecomNumber() != null && t.getTelecomType().equals(TelecomType.phone)) {
                    workPhone = t.getTelecomNumber();
                }
                if (t.getTelecomType() != null && t.getTelecomNumber() != null && t.getTelecomType().equals(TelecomType.email)) {
                    workEmail = t.getTelecomNumber();
                }
                if (t.getTelecomType() != null && t.getTelecomNumber() != null && t.getTelecomType().equals(TelecomType.fax)) {
                    workFax = t.getTelecomNumber();
                }
            }
            workAddress = a;
            break;
        }
    }

    pageContext.setAttribute("hcp_address", workAddress );
    pageContext.setAttribute("hcp_phone", workPhone);
    pageContext.setAttribute("hcp_fax", workFax);
    pageContext.setAttribute("hcp_email", workEmail);

    Address homeAddress = null;

    for (Address a:patient.getAddresses()) {
        if (a.getAddressType() != null && a.getAddressType().equals(AddressType.home)) {
            homeAddress = a;
            break;
        }
    }

    if (homeAddress == null) {
        for (Address a:patient.getAddresses()) {
            homeAddress = a;
            break;
        }
    }

    pageContext.setAttribute("pat_address", homeAddress );


%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title></title>
    <meta name="Generator" content="Cocoa HTML Writer">
    <meta name="CocoaVersion" content="1038.36">
    <style type="text/css">
        p.p1 {margin: 0 0 0 0; font: 10px Helvetica; min-height: 12px}
        p.p2 {margin: 0 0 0 0; font: 10px 'Helvetica Neue'}
        p.p3 {margin: 0 0 0 0; text-align: center; font: 10px 'Helvetica Neue'}
        p.p4 {margin: 0 0 0 0; text-align: right; font: 10px 'Helvetica Neue'}
        span.s1 {letter-spacing: 0
        }
        table.t1 {background-color: #ffffff; border-collapse: collapse}
        td.td1 {width: 240px; height: 12px; padding: 1px 2px 1px 2px}
        td.td2 {width: 360px; height: 12px; padding: 1px 2px 1px 2px}
        td.td3 {width: 176px; height: 12px; padding: 1px 2px 1px 2px}
        td.td4 {width: 29px; height: 12px; padding: 1px 2px 1px 2px}
        td.td5 {width: 81px; height: 12px; padding: 1px 2px 1px 2px}
        td.td6 {width: 62px; height: 12px; padding: 1px 2px 1px 2px}
        td.td7 {width: 22px; height: 12px; padding: 1px 2px 1px 2px}
        td.td8 {width: 34px; height: 12px; padding: 1px 2px 1px 2px}
        td.td9 {width: 356px; height: 12px; padding: 1px 2px 1px 2px}
        td.td10 {width: 420px; height: 12px; padding: 1px 2px 1px 2px}
        td.td16 {width: 22px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
        td.td17 {width: 22px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
        td.td18 {width: 181px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
        td.td19 {width: 29px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
        td.td20 {width: 40px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
        td.td36 {width: 604px; height: 140px; padding: 1px 2px 1px 2px}
        td.td37 {width: 302px; height: 12px; padding: 1px 2px 1px 2px}
        td.td38 {width: 302px; height: 12px; padding: 1px 2px 1px 2px}
        td.td39 {width: 604px; height: 12px; padding: 1px 2px 1px 2px}
        td.td40 {width: 604px; height: 12px;
            border: 0 solid transparent;
            border-bottom: 1px #111111;
            padding: 1px 2px 1px 2px}
        td.td41 {width: 604px; height: 12px;
            border: 1px solid #111111;
            padding: 1px 2px 1px 2px}
    </style>
</head>
<body>
<p class="p1"><br></p>
<table cellspacing="0" cellpadding="0" class="t1">
    <tbody>
    <tr>
        <td colspan="5" valign="top" class="td1">
            <p class="p2"><span class="s1">${hcp.companyName != null?hcp.companyName:(hcp.civility!=null?hcp.civility.concat(" "):"").concat(hcp.firstName).concat(" ").concat(hcp.lastName)}</span></p>
        </td>
        <td colspan="3" valign="top" class="td2">
            <p class="p2"><span class="s1">Code médecin: ${hcp.nihii}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="5" valign="top" class="td1">
            <p class="p2"><span class="s1">${hcp.speciality}</span></p>
        </td>
        <td valign="top" colspan="3" class="td3">
            <p class="p1"><span class="s1">${hcp_address.city}, le <fmt:formatDate value="${invoice_date}" pattern="dd/MM/yyyy"/></span></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">${hcp_address.street} ${hcp_address.houseNumber} ${hcp_address.postboxNumber != null ? "b. ".concat(hcp_address.postboxNumber) : ""} </span></p>
        </td>
        <td valign="top"  colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">${hcp_address.postalCode} ${hcp_address.city}</span></p>
        </td>
        <td valign="top"  colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">Tel: ${hcp_phone}</span></p>
        </td>
        <td valign="top"  colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">Fax: ${hcp_fax}</span></p>
        </td>
        <td valign="top"  colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p2"><span class="s1">${hcp_email}</span></p>
        </td>
        <td valign="top" class="td8">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top"  colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td8">
            <p class="p1"><br></p>
        </td>
        <td colspan="2" valign="top" class="td3">
            <p class="p1"><br></p>
        </td>
        <td colspan="4" valign="top" class="td2">
            <p class="p2"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td8">
            <p class="p1"><br></p>
        </td>
        <td colspan="2" valign="top" class="td3">
            <p class="p1"><br></p>
        </td>
        <td colspan="4" valign="top" class="td2">
            <p class="p2"><br></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">Matricule: ${fn:substring(patient.ssin,0,8)}-${fn:substring(patient.ssin,8,13)}</span></p>
        </td>
        <td colspan="5" valign="top" class="td10">
            <p class="p2"><span class="s1">${patient.gender==null || patient.gender.name == "female" ? "Madame" : "Monsieur"} ${patient.firstName} ${fn:toUpperCase(patient.spouseName)} ${fn:toUpperCase(patient.lastName)}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"><span class="s1">Signalétique: ${fn:toUpperCase(patient.lastName)} ${patient.firstName}</span></p>
        </td>
        <td colspan="5" valign="top" class="td10">
            <p class="p1"><span class="s1">${pat_address.street} ${pat_address.houseNumber} ${pat_address.postboxNumber != null ? "b. "+pat_address.postboxNumber : ""}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"></p>
        </td>
        <td colspan="5" valign="top" class="td10">
            <p class="p1"><span class="s1">${pat_address.postalCode} ${pat_address.city}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"></p>
        </td>
        <td colspan="5" valign="top" class="td10">
            <p class="p1"><span class="s1">${pat_address.country}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="3" valign="top" class="td1">
            <p class="p2"></p>
        </td>
        <td colspan="5" valign="top" class="td10">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td8">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td8">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" colspan="2" class="td3">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td4">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    <tr>
        <td valign="top" class="td7">
            <p class="p1"><br></p>
        </td>
        <td colspan="5" valign="top" class="td9">
            <p class="p3"><span class="s1"><b>MEMOIRE D’HONORAIRES ${invoice.invoiceReference}-${patient.ssin}</b></span></p>
        </td>
        <td valign="top" class="td5">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td6">
            <p class="p1"><br></p>
        </td>
    </tr>
    </tbody>
</table>
<table cellspacing="0" cellpadding="0" class="t1">
    <tbody>
    <tr>
        <td valign="top" class="td16">
            <p class="p1">H</p>
        </td>
        <td valign="top" class="td17">
            <p class="p1">Date</p>
        </td>
        <td valign="top" class="td18">
            <p class="p1">CODE TARIF/LIBELLE</p>
        </td>
        <td valign="top" class="td19">
            <p class="p1">NBR</p>
        </td>
        <td valign="top" class="td20">
            <p class="p1">TOTAL</p>
        </td>
    </tr>
    <c:set var="totalRemb" value="${0.0}" />
    <c:set var="totalACharge" value="${0.0}" />
    <c:set var="totalSup" value="${0.0}" />
    <c:set var="total" value="${0.0}" />
    <c:forEach items="${invoice.invoicingCodes}" var="c">
        <c:set var="totalRemb" value="${totalRemb + (c.reimbursement!=null?c.reimbursement:0)}" />
        <c:set var="totalACharge" value="${totalACharge + (c.patientIntervention!=null?c.patientIntervention:0)}" />
        <c:set var="totalSup" value="${totalSup + (c.doctorSupplement!=null?c.doctorSupplement:0)}" />
        <c:set var="total" value="${total + (c.totalAmount!=null?c.totalAmount:0)}" />
        <c:set var="c_code" value="${fn:replace(fn:substring(c.tarificationId,12,-1),'|1.0','')}" />
        <jsp:useBean id="dateValue" class="java.util.Date"/>
        <jsp:setProperty name="dateValue" property="time" value="${c.dateCode}"/>

        <tr>
            <td valign="top" class="td16">
                <p class="p1">${c.id!=null?'A':''}</p>
            </td>
            <td valign="top" class="td17">
                <p class="p1"><fmt:formatDate value="${c.dateCode!=null?dateValue:''}" pattern="dd/MM/yyyy"/></p>
            </td>
            <td valign="top" class="td18">
                <p class="p1">${c_code} ${c.label}</p>
            </td>
            <td valign="top" class="td19">
                <p class="p1"><fmt:formatNumber value="${c.units!=null?c.units:1}" pattern="0"/></p>
            </td>
            <td valign="top" class="td20">
                <p class="p1"><fmt:formatNumber value="${c.totalAmount}" pattern="0.00"/></p>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td valign="top" class="td16">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td17">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td18">
            <p class="p4"><span class="s1">TOTAL TVAC :</span></p>
        </td>
        <td valign="top" class="td19">
            <p class="p1"><br></p>
        </td>
        <td valign="top" class="td20">
            <p class="p1"><fmt:formatNumber value="${total}" pattern="0.00"/></p>
        </td>
    </tr>
    <tr><td colspan="8" class="td36"></td>&nbsp;</tr>
    <tr>
        <td valign="top" colspan="3" class="td37">
            <p class="p1">Pour acquit, le: </p>
        </td>
        <td valign="top" colspan="5" class="td38">
            <p class="p1">Signature du médecin</p>
        </td>
    </tr>
    <tr>
        <td colspan="8" valign="top" class="td36">&nbsp;</td>
    </tr>
    <tr>
        <td colspan="8" valign="top" class="td39">
            <p class="p3"><span class="s1">${hcp.bankAccount}</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="8" valign="top" class="td40">
            <p class="p3"><span class="s1">* Prière de rappeler la (les) référence(s) ci-dessus sur l’ordre de virement.</span></p>
        </td>
    </tr>
    <tr>
        <td colspan="8" valign="top" class="td41">
            <p class="p3"><span class="s1">La loi du 31.3.79, modifiée par celle du 1.10.92, art 28-1(5), est appliquée</span></p>
        </td>
    </tr>
    </tbody>
</table>
<p class="p1"><br></p>
</body>
</html>
