package no.rmy.works.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.MMapDirectory
import no.rmy.works.oss.schema.Chapter
import no.rmy.works.oss.schema.Paragraph
import no.rmy.works.oss.schema.Role
import no.rmy.works.oss.schema.Work
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths


class WorkIndex {
    val searchManagerMap = mutableMapOf<Index, SearcherManager>()
    val similarity = WorkSimilarity()

    data class ManagedSearcher(val manager: SearcherManager) {
        val searcher: IndexSearcher = manager.acquire()

        fun release() {
            manager.release(searcher)
        }

        fun doc(docId: Int) {
            searcher.doc(docId)
        }

    }

    enum class Index {
        works, paragraphs, chapters, roles;

        val path: Path get() = Paths.get("lucene/${name}.idx")
    }

    private fun writer(index: Index): IndexWriter {
        val analyzer: Analyzer = WhitespaceAnalyzer()
        val directory: Directory = MMapDirectory(index.path)
        val config = IndexWriterConfig(analyzer)
        config.openMode = IndexWriterConfig.OpenMode.CREATE
        config.ramBufferSizeMB = 128.0
        config.maxBufferedDocs = 2500
        config.similarity = similarity

        return IndexWriter(directory, config)
    }

    fun searchManager(index: Index): SearcherManager {
        return searchManagerMap.getOrPut(index) {
            val directory: Directory = MMapDirectory(index.path)
            val sf = object: SearcherFactory() {
                override fun newSearcher(reader: IndexReader, previousReader: IndexReader?): IndexSearcher {
                    val searcher = IndexSearcher(reader)
                    searcher.similarity = similarity
                    return searcher
                }
            }
            SearcherManager(directory, sf)
        }
    }

    fun managedSearcher(index: Index): ManagedSearcher {
        return  ManagedSearcher(searchManager(index))
    }

    private fun write(index: Index, docs: Iterable<Document>) {
        val indexWriter = writer(index)
        docs.forEach {
            indexWriter.addDocument(it)
        }
        indexWriter.commit()
        indexWriter.close()
    }

    fun writeAll() {
        write(Index.works, Work.all.values.map { it.toLuceneDoc() })
        write(Index.paragraphs, Paragraph.all.values.map { it.toLuceneDoc() })
        write(Index.chapters, Chapter.all.values.map { it.toLuceneDoc() })
        write(Index.roles, Role.all.values.map { it.toLuceneDoc() })
        dv(28000)
    }

    fun search(q: String, filter: String? = null, index: Index = Index.paragraphs): TopDocs {
        val ms = managedSearcher(Index.paragraphs)
        val analyzer: Analyzer = WhitespaceAnalyzer()
        //val parser = QueryParser("plainText", analyzer)
        val field = "plainText"
        val terms= q.split("\\s+".toRegex()).map { Term(field, it) }.map {
            when(it.text()) {
                "*" -> WildcardQuery(it)
                else -> TermQuery(it)
            }
        }
        val query = BooleanQuery.Builder().let { b ->
            terms.forEach { t ->
                b.add(BooleanClause(t, BooleanClause.Occur.MUST))
            }
            filter?.let {
                //DocValues.getSorted()
                //FilteredDocIdSetIterator(ms.searcher.indexReader.)
                val filterQuery = TermQuery(Term("workId", it ))
                b.add(BooleanClause(filterQuery, BooleanClause.Occur.FILTER))
            }
            b
        }.build()
        val docs = ms.searcher.search(query, 100)
        ms.release()

        /*
        logger.info("Total hits: ${docs.totalHits}")
        logger.info("Results: ")
        docs.scoreDocs.forEach {
            val d = ms.searcher.doc(it.doc)
            logger.info(" * " + d["plainText"].trim())
        }
         */

        return docs
    }

    fun dv(docId: Int): List<String> {
        val sm = searchManager(Index.paragraphs)
        val s = sm.acquire()
        sm.release(s)

        search("Polonius", "hamlet", Index.chapters)

        return DocValuesSearcher(s).get(docId)
    }

    companion object {
        val logger = LoggerFactory.getLogger(this.javaClass)
    }
}
