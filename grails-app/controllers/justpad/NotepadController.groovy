package justpad

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.converters.JSON

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

@Transactional(readOnly = true)
class NotepadController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def create() {
        respond new Notepad(params)
    }

    @Transactional
    def save(Notepad notepad) {

        notepad.save flush:true

        redirect action:"edit", params: [chave:notepad.chave], encoding: 'UTF-8'
    }

    def edit(Notepad notepad) {
        def notepadInstance = Notepad.findByChave(params.chave)

        if(notepadInstance){
            respond notepadInstance
        }else{
            respond new Notepad(chave:params.chave).save(flush:true)
        }

    }


    @MessageMapping("/updateContent")
    @SendTo("/topic/updateContent")
    protected synchronized String updateContent(String world) {
        def objJson = JSON.parse(world)
        def notepad = null
        Notepad.withTransaction{
            notepad = Notepad.findByChave(objJson.chave)
            notepad.conteudo = objJson.conteudo
            notepad.merge(flush:true)
        }
        return notepad.conteudo
    }
}
