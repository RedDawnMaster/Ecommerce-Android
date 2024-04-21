package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sop.ecommerceapp.models.Cart;
import org.sop.ecommerceapp.models.CartItem;
import org.sop.ecommerceapp.repositories.CartItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CartItemService {
    @Autowired
    private CartItemDao cartItemDao;
    @Autowired
    private CartService cartService;

    private CartItem findByProductLabelAndCartUserUsername(String label, String username) {
        return cartItemDao.findByProductLabelAndCartUserUsername(label, username);
    }

    List<CartItem> findAll() {
        return cartItemDao.findAll();
    }

    @Transactional
    public int deleteByProductLabelAndCartUserUsername(String label, String username) {
        CartItem cartItem = findByProductLabelAndCartUserUsername(label, username);
        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        cartService.update(cart);
        return cartItemDao.deleteByProductLabelAndCartUserUsername(label, username);
    }

    @Transactional
    public int deleteByCartUserUsername(String username) {
        Cart cart = cartService.findByUserUsername(username);
        cart.setCartItems(null);
        cartService.update(cart);
        return cartItemDao.deleteByCartUserUsername(username);
    }

    @Transactional
    public CartItem save(CartItem cartItem, Long id) {
        Cart cart = cartService.findById(id);
        CartItem found = findByProductLabelAndCartUserUsername(cartItem.getProduct().getLabel(),
                cart.getUser().getUsername());
        if (found != null) {
            found.setQuantity(found.getQuantity() + cartItem.getQuantity());
            cartItemDao.save(found);
            cartService.update(cart);
            return found;
        } else {
            cartItem.setCart(cart);
            cartItem = cartItemDao.save(cartItem);
            if (cart.getCartItems() == null)
                cart.setCartItems(new ArrayList<>());
            cart.getCartItems().add(cartItem);
            cartService.update(cart);
        }
        return cartItem;
    }

}
