package justpad

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.converters.JSON

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

@Transactional(readOnly = true)
class NotepadController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Notepad.list(params), model:[notepadCount: Notepad.count()]
    }

    def show(Notepad notepad) {
        respond notepad
    }

    def create() {
        respond new Notepad(params)
    }

    @Transactional
    def save(Notepad notepad) {

        notepad.save flush:true

        println "*****************************"
        println "CHAVE >>> ${notepad.chave}"
        // redirect action:"edit", params: [chave:notepad.chave], encoding: 'UTF-8'
        redirect url:"http://localhost:8080/${notepad.chave}"
    }

    def edit(Notepad notepad) {
        def notepadInstance = Notepad.findByChave(params.chave)

        if(notepadInstance){
            respond notepadInstance
        }else{
            respond new Notepad(chave:params.chave).save(flush:true)
        }

    }


    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    protected String hello(String world) {
        // return "hello from controller, ${world}!"
        def objJson = JSON.parse(world)
        Notepad.withTransaction{
            def notepad = Notepad.findByChave(objJson.chave)
            notepad.conteudo = objJson.conteudo
            notepad.save(flush:true)
        }
        println objJson.chave
        return objJson.conteudo
    }
}
