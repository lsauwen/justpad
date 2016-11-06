package justpad

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

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
/*        if (notepad == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (notepad.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond notepad.errors, view:'create'
            return
        }*/

        notepad.save flush:true

        println "*****************************"
        println "CHAVE >>> ${notepad.chave}"
        // redirect action:"edit", params: [chave:notepad.chave], encoding: 'UTF-8'
        redirect url:"http://localhost:8080/${notepad.chave}"
    }

    def edit(Notepad notepad) {
        println "************************************"
        println params
        def notepadInstance = Notepad.findByChave(params.chave)

        if(notepadInstance){
            respond notepadInstance
        }else{
            respond new Notepad(chave:params.chave).save(flush:true)
        }

    }

    @Transactional
    def update(Notepad notepad) {
        if (notepad == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (notepad.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond notepad.errors, view:'edit'
            return
        }

        notepad.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'notepad.label', default: 'Notepad'), notepad.id])
                redirect notepad
            }
            '*'{ respond notepad, [status: OK] }
        }
    }

    @Transactional
    def delete(Notepad notepad) {

        if (notepad == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        notepad.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'notepad.label', default: 'Notepad'), notepad.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'notepad.label', default: 'Notepad'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    protected String hello(String world) {
        return "hello from controller, ${world}!"
    }
}
