map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Verse' && !doc.deleted) {
        emit([doc.chapterName, doc.paragraphName, doc.verseNum, doc.verseSeq], 1)
    }
};
