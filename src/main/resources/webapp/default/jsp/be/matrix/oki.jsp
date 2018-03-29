<%@page contentType="text/plain" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %><fmt:setLocale value="${lang}"/><fmt:setBundle basename="org.taktik.icure.reports" /><fmt:requestEncoding value="utf-8"/>
<%@ page import="org.taktik.icure.services.external.http.ReportServlet" %><%@ page import="org.taktik.icure.entities.*"%><%@ page import="org.taktik.icure.entities.embed.*"%><%@ page import="java.util.stream.Collectors"%><%@ page import="java.util.*"%><%@ page import="org.taktik.icure.entities.base.Code"%><%@ page import="org.taktik.icure.be.matrix.Codeline"%><%@ page import="java.time.format.DateTimeFormatter"%>
<%
    String lang = request.getParameter("lang");
    String patientId = request.getParameter( "patientId" );
    String invoiceId = request.getParameter("invoiceId");
    String codeIds = request.getParameter("codeIds");
    String yesOrNo = request.getParameter("yesOrNo");


    Map<String,Object> iCureMap = (Map<String,Object>) pageContext.getAttribute("icure", PageContext.REQUEST_SCOPE);

    ReportServlet.LogicWrapper l = (ReportServlet.LogicWrapper) iCureMap.get("logic");

    Patient patient = l.getPatientLogic().getPatient(patientId);
    Invoice invoice = l.getInvoiceLogic().getInvoice(invoiceId);
    User user = l.getUserLogic().getUser(invoice.getAuthor());
    HealthcareParty hcp = l.getHealthcarePartyLogic().getHealthcareParty(user.getHealthcarePartyId());

    if (codeIds!=null) {
    	List<String> idsList = Arrays.asList(codeIds.split(","));
        invoice.setInvoicingCodes(invoice.getInvoicingCodes().stream().filter(ic->idsList.contains(ic.getId())).collect(Collectors.toList()));
     }

    List<Tarification> tarifs = l.getTarificationLogic().get(invoice.getInvoicingCodes().stream().map(InvoicingCode::getTarificationId).collect(Collectors.toList()));
    Set<String> consultCodes = new HashSet<>(tarifs.stream().filter(t->t.getConsultationCode()!=null&&t.getConsultationCode()).map(Code::getCode).collect(Collectors.toList()));

    invoice.getInvoicingCodes().removeIf(ic->consultCodes.contains(ic.getCode())||(ic.getTarificationId().contains("|")&&consultCodes.contains(ic.getTarificationId().split("\\|")[1])));

    List<Codeline> cls = new ArrayList<>(10);

    for (int i=0;i<10;i++) {
    	cls.add(new Codeline(i<invoice.getInvoicingCodes().size()?invoice.getInvoicingCodes().get(i):null, i+10<invoice.getInvoicingCodes().size()?invoice.getInvoicingCodes().get(i+10):null, null));
    }

    Insurability insurability = patient.getInsurabilities().size()>0 ? patient.getInsurabilities().get(0) : null;
    Insurance is = patient.getInsurabilities().size()>0 && patient.getInsurabilities().get(0) != null && patient.getInsurabilities().get(0).getInsuranceId() != null ? l.getInsuranceLogic().getInsurance(patient.getInsurabilities().get(0).getInsuranceId()) :
        invoice.getRecipientId() != null ? l.getInsuranceLogic().getInsurance(invoice.getRecipientId()) : null;
    Address address = patient.getAddresses().size()>0?patient.getAddresses().stream().filter(a->a.getAddressType()==null||a.getAddressType().equals(AddressType.home)).findFirst().orElse(null):null;

    pageContext.setAttribute("invoice_date", new Date(invoice.getInvoiceDate()) );

    Address workAddress =  new Address();
    String workPhone = "";
    String workEmail = "";

    for (Address a:hcp.getAddresses()) {
        if (a.getAddressType() != null && a.getAddressType().equals(AddressType.work)) {
            for (Telecom t:a.getTelecoms()) {
                if (t.getTelecomType() != null && t.getTelecomNumber() != null && t.getTelecomType().equals(TelecomType.phone)) {
                    workPhone = t.getTelecomNumber();
                }
                if (t.getTelecomType() != null && t.getTelecomNumber() != null && t.getTelecomType().equals(TelecomType.email)) {
                    workEmail = t.getTelecomNumber();
                }
            }
            workAddress = a;
            break;
        }
    }

    pageContext.setAttribute("hcp_address", workAddress );
    pageContext.setAttribute("hcp_phone", workPhone);
    pageContext.setAttribute("hcp_email", workEmail);

    pageContext.setAttribute("consult_code", consultCodes.size()>0?consultCodes.iterator().next():"***********");

    pageContext.setAttribute("address", address);
    pageContext.setAttribute("yesOrNo", yesOrNo);
    pageContext.setAttribute("patient", patient);
    pageContext.setAttribute("invoice", invoice);
    pageContext.setAttribute("insurance", is);
    if (is != null) {
		pageContext.setAttribute("insuranceName", is.getName() != null ? (is.getName().get(lang) != null ? is.getName().get(lang) : is.getName().get("fr")) : "");
	}
    pageContext.setAttribute("insurability", insurability);
    pageContext.setAttribute("hcp", hcp);
    pageContext.setAttribute("codeLines",cls);
    pageContext.setAttribute("today",new Date());

    l.log("Language is "+lang);

    response.setContentType("text/plain");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("pragma","no-cache");
%>
 ${patient.firstName!=null?patient.firstName:"N/A"} ${patient.lastName!=null?patient.lastName:"N/A"}

               ${(insurance.code)!=null?(insurance.code):"N/A"} - ${(insuranceName)!=null?(insuranceName):""}
               ${patient.ssin!=null?patient.ssin:"N/A"}

               ${address!=null && address.street!=null?address.street:"N/A"} ${address!=null && address.houseNumber!=null?address.houseNumber:"N/A"}
               ${address!=null && address.postalCode!=null?address.postalCode:"N/A"} ${address!=null && address.city!=null?address.city:"N/A"}




                   ${patient.firstName!=null?patient.firstName:"N/A"} ${patient.lastName!=null?patient.lastName:"N/A"}
                                  ${insurability != null && insurability.parameters['status']!=null?patient.parameters['status']:"N/A"}

     <fmt:formatDate value="${invoice_date}" pattern="dd-MM-yyyy"/>                ${consult_code}
                      ***********



<c:forEach items="${codeLines}" var="c">${c.c1.date!=null?c.c1.date:"********"}      ${c.c1.code!=null?c.c1.code:"******"}  ${c.c2.date!=null?c.c2.date:"********"}      ${c.c2.code!=null?c.c2.code:"******"}
</c:forEach>
            Dr. ${hcp.firstName!=null?hcp.firstName:"N/A"} ${hcp.lastName!=null?hcp.lastName:"N/A"}
          <fmt:formatDate value="${invoice_date}" pattern="dd-MM-yy"/>

                                ${hcp.nihii!=null?hcp.nihii:"N/A"}






                                 ${yesOrNo}

                  ${hcp.nihii!=null?hcp.nihii:"N/A"}
                  Dr. ${hcp.firstName!=null?hcp.firstName:"N/A"} ${hcp.lastName!=null?hcp.lastName:"N/A"}
                  ${hcp_address!=null && hcp_address.street != null?hcp_address.street:"N/A"} ${hcp_address!=null && hcp_address.houseNumber != null?hcp_address.houseNumber:"N/A"}
                  ${hcp_address!=null && hcp_address.postalCode != null?hcp_address.postalCode:"N/A"} ${hcp_address!=null && hcp_address.city != null?hcp_address.city:"N/A"}


                             <fmt:formatDate value="${invoice_date}" pattern="dd-MM-yyyy"/>






                                          ${hcp.cbe!=null?hcp.cbe:""}

                                               <fmt:formatDate value="${invoice_date}" pattern="dd-MM-yyyy"/>

