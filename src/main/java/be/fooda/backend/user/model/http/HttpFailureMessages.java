package be.fooda.backend.user.model.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpFailureMessages {
    USER_IS_DELETED("User existed but deleted."),
    USER_DOES_NOT_EXIST("There is no such user with the given information"),
    USER_IS_DELETED_CANNOT_BE_VALIDATED("This user cannot be validated because it is deleted."),
    USER_IS_DELETED_CANNOT_LOGIN("This user is deleted. You cannot use a deleted account. Please try to create another."),
    USER_EXISTS("User exists."),
    USER_TO_UPDATE_ALREADY_EXIST("This user already exist.. Cannot be updated.. "),
    USER_ALREADY_DELETED("....."),
    STORE_DOES_NOT_EXIST("There is no such store with the given information"),
    PRODUCT_CURRENT_PRICE_DOES_NOT_MATCH("Given price does not match with the current price of the product"),
    INGREDIENTS_DOES_NOT_EXIST("There are no such ingredients"),
    PRODUCT_OUT_OF_STOCK("Store does not have enough product in their stock"),
    PRODUCT_DOES_NOT_EXIST("There is no such product with the given information"),
    PRODUCT_ALREADY_EXISTS("The product you wanted to add already exists in the basket"),
    UNKNOWN_FAILURE("An unknown exception happened."),
    USER_HAS_NO_PRODUCTS_IN_THIS_STORE("Given user does not have any product from the given store"),
    USER_HAS_NO_PRODUCTS("Basket is empty based on the given external user id and session information"),
    SESSION_DOES_NOT_EXISTS("Given user does/did not have such a session"),
    INVALID_PRODUCT_QUANTITY("Product quantity can not be negative or new quantity is equal to the old ones"),
    USER_HAS_NO_ADDRESS("Given user does not have any address yet"),
    USER_HAS_NO_CONTACT("Given user does not have any contact yet"),
    ADDRESS_DOES_NOT_EXIST("Given address does not exist"),
    CONTACT_DOES_NOT_EXIST("Given contact does not exist"),
    ADDRESS_ALREADY_EXISTS("The address you wanted to create already exists"),
    CONTACT_ALREADY_EXISTS("The contact you wanted to create already exists"),
    PAYMENT_ALREADY_EXISTS("The payment you wanted to create already exists"),
    ORDER_ALREADY_EXISTS("The order you wanted to create already exists"),
    PAYMENT_DOES_NOT_EXIST_IN_BASKET("Given payment does not exist in basket"),
    ADDRESS_DOES_NOT_EXIST_IN_BASKET("Given address does not exist in basket"),
    CONTACT_DOES_NOT_EXIST_IN_BASKET("Given contact does not exist in basket"),
    ORDER_DOES_NOT_EXIST_IN_BASKET("Given order does not exist in basket"),
    PRODUCT_DOES_NOT_EXIST_IN_BASKET("Given product does not exist in basket"),
    USER_HAS_NO_SUCH_PRODUCT("Given user did not add this product to basket"),
    REQUIRED_FIELDS_ARE_MISSING_IN_CREATE_REQUEST("Required fields are missing in create request"),
    PRODUCT_COULD_NOT_BE_UPDATED("For an unknown reason product could not be updated"),
    PRODUCT_COULD_NOT_BE_DELETED("Product can not be deleted due to an unknown reason"),
    USER_HAS_NO_PAYMENT("Given user does not have any payment"),
    USER_HAS_NO_ORDERS("Given user has no orders in basket"),
    USER_HAS_NO_ORDER_FROM_THIS_STORE("Given user has no order from given store"),
    ORDER_COULD_NOT_BE_DELETED("Given order could not be deleted"),
    SMS_CODE_COULD_NOT_BE_SENT_TO_USER("SMS code could not be sent to the given user"),
    USER_IS_NOT_VALID("User could not be validated"),
    THE_CODE_COULD_NOT_BE_VALIDATED("The code given by the user is not correct"),
    USER_COULD_NOT_BE_CREATED("User could not be created"),
    NO_USER_FOUND("No user found"),
    USER_ALREADY_EXISTS("user already exists"),
    REQUIRED_FIELDS_ARE_MISSING_IN_UPDATE_REQUEST("Required fields are missing in update request"),
    USER_CODE_IS_NOT_VALID("User validation is not valid."),
    USER_COULD_NOT_BE_DELETED("Given user could not be deleted");

    private final String description;
}
