package no.rmy.works.lucene

import org.apache.lucene.index.FieldInvertState
import org.apache.lucene.search.CollectionStatistics
import org.apache.lucene.search.TermStatistics
import org.apache.lucene.search.similarities.*

class WorkSimilarity : Similarity() {
    val inner = BM25Similarity()

    override fun computeNorm(state: FieldInvertState?): Long =
        inner.computeNorm(state)

    override fun scorer(
        boost: Float,
        collectionStats: CollectionStatistics,
        vararg termStats: TermStatistics
    ): SimScorer {
        return inner.scorer(boost, collectionStats, *termStats)
    }

    override fun toString(): String = inner.toString()
}
