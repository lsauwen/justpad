package justpad

class Notepad {

	String chave, conteudo

    static constraints = {
    	conteudo nullable:true, maxSize:40000
    }

    static mapping = {
    	conteudo sqlType: 'clob'
    }
}
