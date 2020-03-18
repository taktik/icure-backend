map = function (doc) {
    var emit_tarifications_by_code = function(hcparty, doc) {
        doc.invoicingCodes.forEach(function (invoicingCode) {
            var d = doc.invoiceDate ? doc.invoiceDate : doc.created;
            emit([hcparty, invoicingCode.tarificationId, d<99999999?d*1000000:d], invoicingCode._id)
        });
    };

    if (doc.java_type === 'org.taktik.icure.entities.Invoice' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                emit_tarifications_by_code(k, doc);
            });
        }
    }
};
