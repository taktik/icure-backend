map = function(doc) {
    emit([doc.chapterName, doc.paragraphName, doc.verseNum, doc.verseSeq], 1)
};
