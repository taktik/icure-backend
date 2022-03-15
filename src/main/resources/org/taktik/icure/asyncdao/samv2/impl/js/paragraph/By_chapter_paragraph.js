map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Paragraph' && !doc.deleted) {
        emit([doc.chapterName, doc.paragraphName], 1)
    }
};
