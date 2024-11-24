package day15workshop.ShoppingCart;

import day15workshop.ShoppingCart.models.Item;
import day15workshop.ShoppingCart.models.User;

public class Constants {

    public final static String ATTR_USER = "user";
    public final static String ATTR_USERNAME = "username";

    public final static String ATTR_CART_ID = "cartId";
    public final static String ATTR_ALL_CARTS = "carts";

    public final static String ATTR_ITEM = "item";
    public final static String ATTR_ITEM_LIST = "itemList";

    public static boolean isItemEmpty(Item item) {
        return (item.getItemName() == null && item.getQuantity() == 0);
    }

    public static boolean isUserNull(User user) {
        return (user.getUsername() == null && user.getCartMap().isEmpty());
    }
    
}
