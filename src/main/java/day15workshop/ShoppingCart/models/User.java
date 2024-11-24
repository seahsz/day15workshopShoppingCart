package day15workshop.ShoppingCart.models;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public class User {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    private Map<String, Cart> cartMap = new HashMap<>();

    public String getUsername() { return username; }
    public void setUsername(String username) {
        // Automatically trim and convert to lowercase
        this.username = username != null ? username.trim().toLowerCase() : null; 
    }

    public Map<String, Cart> getCartMap() { return cartMap; }
    public void setCartMap(Map<String, Cart> cartMap) { this.cartMap = cartMap; }
    
}
