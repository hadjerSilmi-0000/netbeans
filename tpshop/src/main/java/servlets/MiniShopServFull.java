package servlets;

import java.io.IOException;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import EJB.MiniShopEJB;
import EJB.MiniShopFull;
import EJB.ShopService;

@WebServlet(name = "MiniShopServFull", urlPatterns = {"/MiniShopServFull"})
public class MiniShopServFull extends HttpServlet {

    @EJB 
    private ShopService shopService; 

    @EJB
    private MiniShopFull cartEJB;

    protected void processRequest(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    String productParam = request.getParameter("product");
    String error = shopService.addProductToCart(productParam, cartEJB);

    request.setAttribute("error", error);
    request.setAttribute("price", shopService.getLastPrice());
    request.setAttribute("total", shopService.getCartTotal(cartEJB));
    request.setAttribute("products", shopService.getCartItems(cartEJB));
    request.setAttribute("product", productParam);
    request.setAttribute("pluginMessages", cartEJB.getLastPluginMessages()); // ← MOVED HERE
    request.getRequestDispatcher("newjsp.jsp").forward(request, response);   // ← ALWAYS LAST
}
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}