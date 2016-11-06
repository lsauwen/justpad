<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'notepad.label', default: 'Notepad')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>

        <asset:javascript src="application" />
        <asset:javascript src="spring-websocket" />

        <script type="text/javascript">
            $(function() { 
                var socket = new SockJS("${createLink(uri: '/stomp')}");
                var client = Stomp.over(socket);

                client.connect({}, function() {
                    client.subscribe("/topic/hello", function(message) {
                        $("#helloDiv").append(message.body);
                    });
                });

                $("#helloButton").click(function() {
                    client.send("/app/hello", {}, JSON.stringify("world"));
                });
            });
        </script> 
    </head>
    <body>
        <a href="#edit-notepad" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-notepad" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
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
                <g:textField name="id" value="${this.notepad?.id}" />
                <fieldset class="form">

                    <g:textField name="conteudo" value="${this.notepad?.conteudo}" id="conteudo"/>
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
        <button id="helloButton">hello</button>
        <div id="helloDiv"></div>
    </body>
</html>