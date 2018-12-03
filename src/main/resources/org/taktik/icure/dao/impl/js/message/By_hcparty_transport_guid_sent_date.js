map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length && doc.sent) {
		Object.keys(doc.delegations).forEach(function (k) {
			emit([k, doc.transportGuid ? doc.transportGuid.split(":").slice(0, -1).join(":") : undefined, doc.sent], doc._id);
		});
	}
};
