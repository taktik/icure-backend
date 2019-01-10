function hasInvoicingCodes(doc) {
  return doc.invoicingCodes.length > 0;
}

function onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
}

function mapCodeStatus(code){
  if(!code.pending && !code.canceled && !code.accepted && !code.resent && !code.archived) return "TOBESENT";
  if(code.pending && !code.canceled && !code.accepted && !code.resent && !code.archived) return "PENDING";
  if(code.pending && !code.canceled && !code.accepted && code.resent && !code.archived) return "TOBECORRECTED";
  if(!code.pending && (code.canceled || code.accepted) && !code.resent && !code.archived) return "TREATED";
  if(code.archived) return "ARCHIVED";
  return "UNKNOWN";
}

function mapStatuses(doc){
  var codesStatus = doc.invoicingCodes.map(mapCodeStatus);
  var uniqueCodes = codesStatus.filter(onlyUnique);
  return uniqueCodes;
}

function mapSendingMode(doc){
  if(doc.invoiceType === "payingagency") {
    return "OP";
  } else if(doc.invoiceType === "mutualfund") {
    return "EFACT"
  } else if(doc.sentMediumType === "eattest") {
    return "EATTEST";
  } else if(doc.sentMediumType === "paper" && doc.invoiceType === "patient") {
    return "PATIENT";
  } else {
    return "UNKNOWN";
  }
}

map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && hasInvoicingCodes(doc) && doc.delegations && Object.keys(doc.delegations).length) {
	  var statuses = mapStatuses(doc)
	  var sendingMode = mapSendingMode(doc)
		Object.keys(doc.delegations).forEach(function (k) {
      statuses.forEach(function (s) {
        emit([k, sendingMode, s, doc.invoiceDate], doc._id)
      });
		});
	}
};