map = function(doc) {
    var emit_word = function(doc, word) {
        var summary = {'_id':doc._id,'_rev':doc._rev,'java_type':doc.java_type,'descr':doc.descr,'entityType':doc.entityType,'defaultTemplate':doc.defaultTemplate};
        if (word) {
            emit([ doc.entityType, word], summary);
        }
    };

    if (doc.java_type === 'org.taktik.icure.entities.EntityTemplate' && !doc.deleted && doc.keywords && doc.keywords.length) {
        doc.keywords.forEach(function(x) { emit_word(doc, x) });
    }
}
