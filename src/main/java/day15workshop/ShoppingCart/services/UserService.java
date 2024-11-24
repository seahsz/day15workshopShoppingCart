package day15workshop.ShoppingCart.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import day15workshop.ShoppingCart.models.Cart;
import day15workshop.ShoppingCart.models.Item;
import day15workshop.ShoppingCart.models.User;
import day15workshop.ShoppingCart.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    // Get User info (Cart etc.) from Redis database using username
    public User getUserByUsername(String username) {
        Optional<User> opt = userRepo.getUserByUsername(username);

        if (opt.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            return user;
        }

        return opt.get();
    }

    // Create new cart & returns the new cart Id
    public String createNewCart(User user) {
        
        String newCartId = UUID.randomUUID().toString().substring(0,7);

        Cart cart = new Cart();
        cart.setCartId(newCartId);

        // update User object
        user.getCartMap().put(newCartId, cart);

        // update Redis database
        userRepo.insertCart(user, cart);

        return newCartId;
    }

    // Add item to cart
    public User addItem(User user, Cart cart, Item item) {

        // update the cart and user
        cart.getItemSet().add(item);
        user.getCartMap().put(cart.getCartId(), cart);

        // update the redis database
        userRepo.insertCart(user, cart);

        return user;
    }

    // Delete cart from repository
    public void deleteCart(User user, String cartId) {

        // delete cart from user
        user.getCartMap().remove(cartId);

        // delete cart from repo
        userRepo.deleteCart(user, cartId);
    }
    
}
