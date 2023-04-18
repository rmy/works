package no.rmy.works.view

import io.ktor.server.html.*
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.*


class LayoutTemplate(val page: String = "Works"): Template<HTML> {
    val header = Placeholder<FlowContent>()
    val crumb = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<ContentTemplate>()
    override fun HTML.apply() {
        head {
            title { +page }
            link(rel = "stylesheet", href = "/works/css/styles.css", type = "text/css")
        }
        body {
            p {
                a(href = ResourcePath.root) { +"Home" }
                insert(crumb)
                +" > "
                insert(header)
            }

            insert(ContentTemplate(), content)
        }
    }
}