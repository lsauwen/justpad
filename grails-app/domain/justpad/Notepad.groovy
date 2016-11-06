package justpad

class Notepad {

	String chave, conteudo

    static constraints = {
    	conteudo nullable:true
    }

    static mapping = {
    	conteudo sqlType: 'clob'
    }
}
