<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="notepad" />
        <g:set var="entityName" value="${message(code: 'notepad.label', default: 'Notepad')}" />
        <title>Justpad</title>

        <asset:javascript src="application" />
        <asset:javascript src="spring-websocket" />

        <script type="text/javascript">

            var delay = (function(){
              var timer = 0;
              return function(callback, ms){
                clearTimeout (timer);
                timer = setTimeout(callback, ms);
              };
            })();

            $(function() { 
                var socket = new SockJS("${createLink(uri: '/stomp')}");
                var client = Stomp.over(socket);

                client.connect({}, function() {
                    client.subscribe("/topic/updateContent", function(message) {
                        $("#conteudo").val(message.body);
                    });
                });

                $("#conteudo").on('keyup paste',function() {
                    delay(function(){
                        var valor = $("#conteudo").val();
                        client.send("/app/updateContent", {}, JSON.stringify({
                            'chave': '<%=params.chave%>',
                            'conteudo': $("#conteudo").val()}));
                    }, 2000 );
                });
            });
        </script> 
        <style type="text/css">
        .txtArea {
            width: 100%;
            height: 100%;
            border: 1px solid;
            box-sizing: border-box;
        }
        </style>
    </head>
    <body>
        <a href="#edit-notepad" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div id="edit-notepad" class="content scaffold-edit" role="main">
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.notepad}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.notepad}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.notepad}" method="PUT">
                <g:hiddenField name="version" value="${this.notepad?.version}" />
                <fieldset class="form">

                    <g:textArea name="conteudo" value="${this.notepad?.conteudo}" id="conteudo" rows="18" class="txtArea"/>
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
