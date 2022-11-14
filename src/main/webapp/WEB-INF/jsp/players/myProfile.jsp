<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parchisoca" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>


<petclinic:layout pageName="players">
    <h1>Mi perfil</h1>
    <a href="/players/myProfile/edit">
        Editar mi perfil
    </a>

    <table class="table table-striped">
         <tr>
            <th>Nombre</th>
            <td><b><c:out value="${player.firstName} ${player.lastName}"/></b></td>
         </tr>
         <tr>
            <th>Username</th>
            <td><b><c:out value="${player.user.username}"/></b></td>
        </tr>
        <tr>
            <th>Logros</th>
                <td>
                    <c:choose>
                        <c:when test="${player.achievements.isEmpty()}">
                            <p>None</p>
                        </c:when>
                        <c:otherwise>
                            ${player.achievements}
                            <br/>
                        </c:otherwise>
                    </c:choose>
                </td>
        </tr>
    </table>

    <h3>Mis amigos (nº)</h3>

    <table class="table table-striped">
        <tr>
            <th>Username</th>
        </tr>
    </table>

</parchisoca:layout>
