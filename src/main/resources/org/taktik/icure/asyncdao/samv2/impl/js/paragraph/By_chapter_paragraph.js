map = function(doc) {
    emit([doc.chapterName, doc.paragraphName], 1)
};
