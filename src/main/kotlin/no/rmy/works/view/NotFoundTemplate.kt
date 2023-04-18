package no.rmy.works.view

import io.ktor.server.html.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Work

class NotFoundTemplate : Template<HTML> {
    val work = Placeholder<FlowContent>()

    override fun HTML.apply() {
        head { title { +"Work Not Found" } }
        body {
            h1 { +"Work Not Found" }
            p { +"There is no work with id "
                insert(work)
            }
            p { +"Try one of:" }
            ul {
                Work.all.values.map {
                    li { a(it.id) { +it.title } }
                }
            }
        }
    }
}
