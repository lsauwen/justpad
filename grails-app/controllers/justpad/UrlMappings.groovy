package justpad

class UrlMappings {

    static excludes = ["/stomp/**"]

    static mappings = {
        "/$chave**"(controller:"notepad", action:"edit")
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/notepad")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
