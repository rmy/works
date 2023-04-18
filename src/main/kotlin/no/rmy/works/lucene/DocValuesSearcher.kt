package no.rmy.works.lucene

import org.apache.lucene.index.DocValues
import org.apache.lucene.index.SortedDocValues
import org.apache.lucene.search.IndexSearcher

class DocValuesSearcher(val searcher: IndexSearcher) {

    fun get(docId: Int): List<String> {
        var ret = mutableListOf<String>()
        val doc = searcher.indexReader.document(docId)
        WorkIndex.logger.info("Doc $docId: $doc")
        searcher.leafContexts.lastOrNull { it.docBase < docId }?.let { leaf ->
            val sdv: SortedDocValues = DocValues.getSorted(leaf.reader(), "workId")
            sdv.advanceExact(docId - leaf.docBase)
            val term = sdv.lookupOrd(sdv.ordValue()).utf8ToString()
            ret.add(term)
            WorkIndex.logger.debug("Value doc id: ${sdv.docID()}+${leaf.docBase}=${sdv.docID() + leaf.docBase}  ${term}")
        }
        WorkIndex.logger.info("Ret: $ret")
        return ret
    }

}
