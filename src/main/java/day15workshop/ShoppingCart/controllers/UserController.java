package day15workshop.ShoppingCart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import day15workshop.ShoppingCart.models.Item;
import day15workshop.ShoppingCart.models.Cart;
import day15workshop.ShoppingCart.models.User;
import day15workshop.ShoppingCart.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import static day15workshop.ShoppingCart.Constants.*;

import java.util.logging.Logger;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserService userSvc;

    Logger logger = Logger.getLogger(UserController.class.getName());

    @GetMapping(path = { "/", "index.html" })
    public String getLogin(Model model) {

        model.addAttribute(ATTR_USER, new User());

        return "login";
    }

    /* GET and POST /home */

    @GetMapping("/home")
    public String getHome(
            Model model,
            HttpSession session) {

        // Attempt to retrieve User from session
        User user = (User) session.getAttribute(ATTR_USER);

        // if user is null --> means client has not logged in
        if (user == null) {
            model.addAttribute(ATTR_USER, new User());
            return "login";
        }

        // Retrieve User info from Database (if there is any)
        user = userSvc.getUserByUsername(user.getUsername());

        // Get list of cart ids and bind to model
        model.addAttribute(ATTR_ALL_CARTS, user.getCartMap());

        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "home";
    }

    @PostMapping("/home")
    public String postHome(
            Model model,
            HttpSession session,
            @Valid @ModelAttribute User user,
            BindingResult binding) {

        if (binding.hasErrors())
            return "login";

        logger.info(">>>>postHome>>>> getting user from database");

        // Retrieve User info from Database (if there is any)
        user = userSvc.getUserByUsername(user.getUsername());

        // Set session attributes
        session.setAttribute(ATTR_USER, user);

        // Get list of cart ids and bind to model
        model.addAttribute(ATTR_ALL_CARTS, user.getCartMap());

        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "home";
    }

    /* GET /newCart */

    @GetMapping("/newCart")
    public String getNewCart(
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute(ATTR_USER);

        // if user is null --> means client has not logged in
        if (user == null) {
            model.addAttribute(ATTR_USER, new User());
            return "login";
        }

        // create new cart and bind cart Id to model
        model.addAttribute(ATTR_CART_ID, userSvc.createNewCart(user));

        // bind new Item() to accept input + username to display
        model.addAttribute(ATTR_ITEM, new Item());
        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "cart";
    }

    /* GET and POST /cart/{cartId} */

    // User "clicks VIEW" from home
    @GetMapping("/cart/{cartId}")
    public String getCartId(
            Model model,
            HttpSession session,
            @PathVariable String cartId) {

        // retrieve the user from session
        User user = (User) session.getAttribute(ATTR_USER);

        // if user is null --> means client has not logged in
        if (user == null) {
            model.addAttribute(ATTR_USER, new User());
            return "login";
        }

        Cart currCart = user.getCartMap().get(cartId);

        // Bind 1) new Item(), 2) List of items, 3) Cart Id, 4) Username
        model.addAttribute(ATTR_ITEM, new Item());
        model.addAttribute(ATTR_ITEM_LIST, currCart.getItemSet());
        model.addAttribute(ATTR_CART_ID, cartId);
        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "cart";
    }

    // when user "adds" items to cart
    @PostMapping("/cart/{cartId}")
    public String postCartId(
            Model model,
            HttpSession session,
            @PathVariable String cartId,
            @Valid @ModelAttribute Item item,
            BindingResult binding) {

        // retrieve the user from session
        User user = (User) session.getAttribute(ATTR_USER);

        if (binding.hasErrors()) {
            Cart currCart = user.getCartMap().get(cartId);
            model.addAttribute(ATTR_ITEM_LIST, currCart.getItemSet());
            model.addAttribute(ATTR_CART_ID, cartId);
            model.addAttribute(ATTR_USERNAME, user.getUsername());
            return "cart";
        }

        // Update User
        user = userSvc.addItem(user, user.getCartMap().get(cartId), item);

        // user should be updated from the database already before reaching this page
        Cart currCart = user.getCartMap().get(cartId);

        // Bind 1) new Item(), 2) List of items, 3) Cart Id, 4) Username
        model.addAttribute(ATTR_ITEM, new Item());
        model.addAttribute(ATTR_ITEM_LIST, currCart.getItemSet());
        model.addAttribute(ATTR_CART_ID, cartId);
        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "cart";
    }

    /* POST /deleteCart/{cartId} */

    @PostMapping("/deleteCart/{cartId}")
    public String postDeleteCartId(
            Model model,
            HttpSession session,
            @PathVariable String cartId) {

        // retrieve the user from session
        User user = (User) session.getAttribute(ATTR_USER);

        // Delete the cart from the database
        userSvc.deleteCart(user, cartId);

        // Get list of cart ids and bind to model
        model.addAttribute(ATTR_ALL_CARTS, user.getCartMap());

        model.addAttribute(ATTR_USERNAME, user.getUsername());

        return "home";
    }

}
