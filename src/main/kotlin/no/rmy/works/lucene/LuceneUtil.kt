package no.rmy.works.lucene

import org.apache.lucene.document.*
import org.apache.lucene.index.IndexableField
import org.apache.lucene.util.BytesRef
import no.rmy.works.oss.schema.Chapter
import no.rmy.works.oss.schema.Role
import no.rmy.works.oss.schema.Paragraph
import no.rmy.works.oss.schema.Work
import java.util.*

fun Work.toLuceneDoc(): Document {
    val doc = Document()
    listOf(
        StringField("title", title, Field.Store.YES),
        SortedDocValuesField("genre", BytesRef(genre)),
        StringField("genre", genre, Field.Store.YES),
        TextField("longTitle", longTitle, Field.Store.YES),
        StoredField("year", year),
        StoredField("paragraphCount", paragraphCount),
        StoredField("wordCount", wordCount),
    ).forEach {
        doc.add(it)
    }
    return doc
}

fun Chapter.toLuceneDoc(): Document {
    val doc = Document()
    listOf(
        SortedDocValuesField("workId", BytesRef(workId)),
        StringField("workId", workId, Field.Store.YES),
        TextField("description", description, Field.Store.YES),
        StoredField("section", section),
        StoredField("chapter", chapter),
        StoredField("id", id),
        StoredField("name", "$section.$chapter"),
    ).forEach {
        doc.add(it)
    }
    return doc
}

fun Role.toLuceneDoc(): Document {
    val doc = Document()
    listOf<IndexableField>(
        StringField("id", this.id, Field.Store.YES),
        StringField("name", this.name, Field.Store.YES),
        StringField("abbrev", this.abbrev, Field.Store.YES),
        TextField("description", this.description, Field.Store.YES),
        StoredField("speechCount", this.speechCount),
    ).plus(
        workIds.map {
            StringField("workIds", it, Field.Store.YES)
        }
    ).forEach {
        doc.add(it)
    }
    return doc
}

fun Paragraph.toLuceneDoc(): Document {
    val doc = Document()
    listOf(
        SortedDocValuesField("workId", BytesRef(workId)),
        StringField("workIdx", workId, Field.Store.YES),
        StringField("charId", this.charId, Field.Store.YES),
        TextField("plainText", plainText, Field.Store.YES),
        TextField("lowerCaseText", plainText.lowercase(Locale.ENGLISH), Field.Store.YES),
        TextField("phoneticText", phoneticText, Field.Store.YES),
        TextField("stemText", stemText, Field.Store.YES),
        StoredField("chapter", chapter),
        StoredField("section", section),
        StoredField("line", line),
        StoredField("number", number)
    ).forEach {
        doc.add(it)
    }
    return doc
}

