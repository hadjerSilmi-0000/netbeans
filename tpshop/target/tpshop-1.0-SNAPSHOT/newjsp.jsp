<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Résultat Produit</title></head>
<body>
    <h1>My Order</h1>

    <c:choose>
        <c:when test="${not empty requestScope.error}">
            <p style="color:red;">${requestScope.error}</p>
        </c:when>
        <c:otherwise>
            <p>Produit : ${product}</p>
            <p>Prix : ${price}</p>
        </c:otherwise>
    </c:choose>
<h1>Cart</h1>
<ul>
    <c:forEach var="item" items="${products}">
        <li>
            Product ID: ${item.productId} | 
            Price: ${item.price}$ | 
            Quantity: ${item.quantity} | 
            Subtotal: ${item.subtotal}$
        </li>
    </c:forEach>
</ul>
<p>Total : ${total}$</p> 
<h3>Suggestions:</h3> 
<c:forEach var="msg" items="${pluginMessages}"> 
    <p style="color:green;">${msg}</p> 
</c:forEach> 


    <p><a href="index.html">Retour</a></p>
</body>
</html>