<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MiniShop — My Order</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap');

        :root {
            --bg: #080c14;
            --surface: #0f1624;
            --card: #141d2e;
            --border: #1e2d45;
            --accent: #38bdf8;
            --accent2: #818cf8;
            --green: #34d399;
            --red: #f87171;
            --yellow: #fbbf24;
            --text: #e2e8f0;
            --muted: #64748b;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            background: var(--bg);
            color: var(--text);
            font-family: 'Syne', sans-serif;
            min-height: 100vh;
            padding: 30px 20px;
            position: relative;
            overflow-x: hidden;
        }

        body::before {
            content: '';
            position: fixed;
            inset: 0;
            background-image:
                linear-gradient(rgba(56,189,248,0.03) 1px, transparent 1px),
                linear-gradient(90deg, rgba(56,189,248,0.03) 1px, transparent 1px);
            background-size: 50px 50px;
            pointer-events: none;
            z-index: 0;
        }

        .orb {
            position: fixed;
            border-radius: 50%;
            filter: blur(80px);
            pointer-events: none;
        }
        .orb-1 { width: 300px; height: 300px; background: rgba(56,189,248,0.05); top: -50px; right: -50px; }
        .orb-2 { width: 250px; height: 250px; background: rgba(129,140,248,0.05); bottom: -50px; left: -50px; }

        .wrapper {
            max-width: 700px;
            margin: 0 auto;
            position: relative;
            z-index: 1;
        }

        /* Header */
        .page-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 32px;
            animation: fadeIn 0.5s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .page-title {
            font-size: 2rem;
            font-weight: 800;
            background: linear-gradient(135deg, #e2e8f0, #38bdf8);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            color: var(--muted);
            text-decoration: none;
            font-size: 0.8rem;
            font-family: 'JetBrains Mono', monospace;
            padding: 8px 14px;
            border: 1px solid var(--border);
            border-radius: 8px;
            transition: all 0.2s;
        }

        .back-link:hover {
            color: var(--accent);
            border-color: var(--accent);
            background: rgba(56,189,248,0.05);
        }

        /* Error */
        .error-card {
            background: rgba(248,113,113,0.08);
            border: 1px solid rgba(248,113,113,0.25);
            border-radius: 12px;
            padding: 16px 20px;
            margin-bottom: 24px;
            display: flex;
            align-items: center;
            gap: 12px;
            animation: shake 0.4s ease;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-6px); }
            75% { transform: translateX(6px); }
        }

        .error-icon { font-size: 1.2rem; }
        .error-text { color: var(--red); font-size: 0.9rem; font-weight: 600; }

        /* Product result card */
        .result-card {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 20px;
            position: relative;
            overflow: hidden;
            animation: slideIn 0.4s ease;
        }

        @keyframes slideIn {
            from { opacity: 0; transform: translateX(-20px); }
            to { opacity: 1; transform: translateX(0); }
        }

        .result-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0;
            height: 2px;
            background: linear-gradient(90deg, var(--accent), var(--accent2));
        }

        .result-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 16px;
        }

        .section-label {
            font-size: 0.7rem;
            font-weight: 600;
            color: var(--muted);
            text-transform: uppercase;
            letter-spacing: 1.5px;
            font-family: 'JetBrains Mono', monospace;
        }

        .product-info {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .product-emoji {
            width: 52px; height: 52px;
            background: rgba(56,189,248,0.1);
            border: 1px solid rgba(56,189,248,0.2);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            flex-shrink: 0;
        }

        .product-details .product-name {
            font-size: 1rem;
            font-weight: 700;
            margin-bottom: 2px;
        }

        .product-details .product-id {
            font-size: 0.75rem;
            color: var(--muted);
            font-family: 'JetBrains Mono', monospace;
        }

        .price-badge {
            background: rgba(52,211,153,0.1);
            border: 1px solid rgba(52,211,153,0.25);
            border-radius: 10px;
            padding: 8px 16px;
            text-align: right;
        }

        .price-label {
            font-size: 0.65rem;
            color: var(--muted);
            font-family: 'JetBrains Mono', monospace;
            text-transform: uppercase;
        }

        .price-value {
            font-size: 1.2rem;
            font-weight: 800;
            color: var(--green);
        }

        /* Cart section */
        .cart-card {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            overflow: hidden;
            margin-bottom: 20px;
            animation: slideIn 0.5s ease 0.1s both;
        }

        .cart-header {
            padding: 16px 24px;
            background: var(--surface);
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .cart-title {
            font-size: 0.85rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .cart-count {
            background: rgba(56,189,248,0.15);
            color: var(--accent);
            font-size: 0.65rem;
            font-family: 'JetBrains Mono', monospace;
            padding: 2px 8px;
            border-radius: 20px;
            font-weight: 700;
        }

        .cart-items {
            padding: 8px;
        }

        .cart-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            border-radius: 10px;
            transition: background 0.15s;
            border-bottom: 1px solid var(--border);
        }

        .cart-item:last-child { border-bottom: none; }
        .cart-item:hover { background: rgba(255,255,255,0.02); }

        .item-num {
            width: 24px; height: 24px;
            background: rgba(56,189,248,0.1);
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.65rem;
            font-family: 'JetBrains Mono', monospace;
            color: var(--accent);
            font-weight: 700;
            flex-shrink: 0;
        }

        .item-info {
            flex: 1;
        }

        .item-id {
            font-size: 0.8rem;
            font-weight: 700;
            margin-bottom: 2px;
        }

        .item-meta {
            display: flex;
            gap: 10px;
        }

        .item-tag {
            font-size: 0.65rem;
            font-family: 'JetBrains Mono', monospace;
            color: var(--muted);
            background: rgba(255,255,255,0.04);
            padding: 2px 6px;
            border-radius: 4px;
        }

        .item-qty {
            background: rgba(129,140,248,0.12);
            color: var(--accent2);
        }

        .item-subtotal {
            font-size: 0.85rem;
            font-weight: 700;
            color: var(--text);
            font-family: 'JetBrains Mono', monospace;
            text-align: right;
        }

        /* Empty cart */
        .empty-cart {
            padding: 40px;
            text-align: center;
            color: var(--muted);
            font-size: 0.85rem;
            font-family: 'JetBrains Mono', monospace;
        }

        /* Total */
        .total-card {
            background: linear-gradient(135deg, rgba(56,189,248,0.08), rgba(129,140,248,0.08));
            border: 1px solid rgba(56,189,248,0.2);
            border-radius: 16px;
            padding: 20px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            animation: slideIn 0.5s ease 0.2s both;
        }

        .total-label {
            font-size: 0.75rem;
            color: var(--muted);
            font-family: 'JetBrains Mono', monospace;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 4px;
        }

        .total-value {
            font-size: 1.8rem;
            font-weight: 800;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .total-icon { font-size: 2rem; }

        /* Plugin messages */
        .plugins-card {
            background: var(--card);
            border: 1px solid rgba(251,191,36,0.2);
            border-radius: 16px;
            overflow: hidden;
            margin-bottom: 20px;
            animation: slideIn 0.5s ease 0.3s both;
        }

        .plugins-header {
            padding: 14px 20px;
            background: rgba(251,191,36,0.05);
            border-bottom: 1px solid rgba(251,191,36,0.15);
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.8rem;
            font-weight: 700;
            color: var(--yellow);
        }

        .plugin-msg {
            padding: 12px 20px;
            font-size: 0.85rem;
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--text);
        }

        .plugin-msg:last-child { border-bottom: none; }
        .plugin-dot { width: 6px; height: 6px; background: var(--yellow); border-radius: 50%; flex-shrink: 0; }
    </style>
</head>
<body>

<div class="orb orb-1"></div>
<div class="orb orb-2"></div>

<div class="wrapper">

    <!-- Header -->
    <div class="page-header">
        <h1 class="page-title">My Order</h1>
        <a href="index.html" class="back-link">← Back to Shop</a>
    </div>

    <!-- Error -->
    <c:if test="${not empty requestScope.error}">
        <div class="error-card">
            <span class="error-icon">⚠</span>
            <span class="error-text">${requestScope.error}</span>
        </div>
    </c:if>

    <!-- Product Result -->
    <c:if test="${empty requestScope.error}">
    <div class="result-card">
        <div class="result-header">
            <span class="section-label">Last Added</span>
        </div>
        <div style="display:flex; align-items:center; justify-content:space-between;">
            <div class="product-info">
                <div class="product-emoji">🛍️</div>
                <div class="product-details">
                    <div class="product-name">Product #${product}</div>
                    <div class="product-id">// unit price</div>
                </div>
            </div>
            <div class="price-badge">
                <div class="price-label">Price</div>
                <div class="price-value">${price}$</div>
            </div>
        </div>
    </div>
    </c:if>

    <!-- Cart -->
    <div class="cart-card">
        <div class="cart-header">
            <div class="cart-title">
                🛒 Cart
                <span class="cart-count">${fn:length(products)} items</span>
            </div>
        </div>
        <div class="cart-items">
            <c:choose>
                <c:when test="${empty products}">
                    <div class="empty-cart">// cart is empty</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="item" items="${products}" varStatus="status">
                        <div class="cart-item">
                            <div class="item-num">${status.index + 1}</div>
                            <div class="item-info">
                                <div class="item-id">Product #${item.productId}</div>
                                <div class="item-meta">
                                    <span class="item-tag">${item.price}$ each</span>
                                    <span class="item-tag item-qty">qty: ${item.quantity}</span>
                                </div>
                            </div>
                            <div class="item-subtotal">${item.subtotal}$</div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Total -->
    <div class="total-card">
        <div>
            <div class="total-label">Total Ordered</div>
            <div class="total-value">${total}$</div>
        </div>
        <div class="total-icon">💰</div>
    </div>

    <!-- Plugin Messages -->
    <c:if test="${not empty pluginMessages}">
    <div class="plugins-card">
        <div class="plugins-header">✦ Smart Suggestions</div>
        <c:forEach var="msg" items="${pluginMessages}">
            <div class="plugin-msg">
                <div class="plugin-dot"></div>
                ${msg}
            </div>
        </c:forEach>
    </div>
    </c:if>

</div>
</body>
</html>
