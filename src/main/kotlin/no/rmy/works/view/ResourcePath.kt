package no.rmy.works.view

object ResourcePath {
    val root = "/works"
    fun work(id: String): String = "$root/$id"

    fun gram(id1: String, id2: String?): String {
        return if(id2 == null) {
            "$root/gram/$id1"
        }
        else {
            "$root/gram/$id1/$id2"
        }
    }

    fun person(workId: String, personId: String): String {
        return "$root/person/$workId/$personId"
    }

    fun chapter(workId: String, chapterId: Int): String {
        return "$root/chapter/$workId/$chapterId"
   }

    fun query(): String ="$root/q"
}