package day15workshop.ShoppingCart.models;

import java.util.Set;
import java.util.HashSet;

public class Cart {

    private String cartId;

    private Set<Item> itemSet = new HashSet<>();

    private int totalItemQuantity;

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public Set<Item> getItemSet() { return itemSet; }
    public void setItemSet(Set<Item> itemSet) { this.itemSet = itemSet; }

    public int getTotalItemQuantity() {
        return itemSet.stream().mapToInt(Item::getQuantity).sum();
    }
    public void setTotalItemQuantity(int totalItemQuantity) { this.totalItemQuantity = totalItemQuantity; } 

}
