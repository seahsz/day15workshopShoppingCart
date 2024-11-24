package day15workshop.ShoppingCart.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Item {

    @NotBlank(message = "Item cannot be empty")
    private String itemName;

    @Positive(message = "Quantity must be at least 1")
    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) {
        // Automatically trim and convert to lowercase
         this.itemName = itemName != null ? itemName.trim().toLowerCase() : null; 
        }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    
}
