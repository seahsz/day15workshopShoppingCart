package day15workshop.ShoppingCart.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import day15workshop.ShoppingCart.models.Cart;
import day15workshop.ShoppingCart.models.Item;
import day15workshop.ShoppingCart.models.User;

@Repository
public class UserRepository {

    @Autowired
    @Qualifier("redis-obj")
    private RedisTemplate<String, String> template;

    private Logger logger = Logger.getLogger(UserRepository.class.getName());

    // hset username (cartid)
    // [updating / creating new cart]
    public void insertCart(User user, Cart cart) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, Integer> items = cart.getItemSet().stream()
                .collect(Collectors.toMap(Item::getItemName, Item::getQuantity));

        // Objectmapper instance for serializing
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Serialize to JSON
            String jsonString = mapper.writeValueAsString(items);

            // Hset into Redis
            hashOps.put(user.getUsername(), cart.getCartId(), jsonString);
        } catch (JsonProcessingException e) {
            logger.warning("Error serializing map to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // hgetall username [using username, we recreate User from Redis]
    public Optional<User> getUserByUsername(String username) {
        HashOperations<String, String, String> hashOps = template.opsForHash();

        // check if username exist, if not send empty optional
        if (!template.hasKey(username))
            return Optional.empty();


        // carts -> Map<cartId, JsonListOfItems>
        Map<String, String> carts = hashOps.entries(username);

        User user = new User();
        user.setUsername(username);

        // Repopulate the user details
        // iterate through carts, for each entry deserialize the value (jsonString) -->
        // map<Item name, Quantity>
        // --> Create the Item --> Create the Cart --> Put cart into User.getCartMap
        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry<String, String> cart : carts.entrySet()) {

            Cart currCart = new Cart();
            currCart.setCartId(cart.getKey());

            String jsonValueInString = cart.getValue().toString();

            // Situations of null: Created cart but did not add anything
                // if not null, create List<Item> to set into cart
            if (jsonValueInString != null) {

                try {
                    // Map<Item name, quantity>
                    Map<String, Integer> itemMap = mapper.readValue(jsonValueInString,
                            new TypeReference<Map<String, Integer>>() {});

                    Set<Item> itemSet = itemMap.entrySet()
                                            .stream()
                                            .map(entry -> {
                                                Item item = new Item();
                                                item.setItemName(entry.getKey());
                                                item.setQuantity(entry.getValue());
                                                return item;
                                            })
                                            .collect(Collectors.toSet());
                    
                    currCart.setItemSet(itemSet);

                } catch (Exception e) {
                    logger.warning("Error deserializing map from JSON: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // If null, don't need to set List<Item> --> leave it empty

            // add each cart into User
            user.getCartMap().put(currCart.getCartId(), currCart);
        }

        return Optional.of(user);
    }

    // hdel username (cartId) [delete the cart]
    public void deleteCart(User user, String cartId) {
        HashOperations<String, String, String> hashOps = template.opsForHash();

        // Delete the Cart
        hashOps.delete(user.getUsername(), cartId);
    }


}
