package no.rmy.works.oss.schema

import kotlinx.serialization.Serializable
import no.rmy.works.oss.io.OssFile

@Serializable
class Role(
    val id: String,
    val workIds: Set<String>,
    val name: String,
    val abbrev: String,
    val description: String,
    val speechCount: Int
) {
    constructor(m: Map<String, Any>) : this(
        //~CharID~,~CharName~,~Abbrev~,~Works~,~Description~,~SpeechCount~
        //~1apparition-mac~,~First Apparition~,~First Apparition~,~macbeth~,,1
        id = m["CharID"] as String,
        workIds = (m["Works"] as String).split(",").toSet(),
        name = m["CharName"] as String,
        abbrev = m["Abbrev"] as String,
        description = m["Description"] as String? ?: "",
        speechCount = m["SpeechCount"] as Int
    )

    val works: List<String> get() = workIds.map { Work.all[it].toString() }

    override fun toString(): String = name

    companion object {
        private val roles = OssFile("oss/Characters.txt").all.map { Role(it) }

        val all: Map<String, Role> = roles.associateBy { it.id }
        val byWork: Map<String, List<Role>> = Work.all.keys.associateWith { w ->
            roles.filter { ch -> ch.workIds.contains(w) }
        }
    }
}