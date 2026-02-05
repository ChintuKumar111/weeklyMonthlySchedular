package com.example.freshyzoappmodule.cartpreview

import com.example.freshyzoappmodule.cartpreview.model.CartData

class CartPreviewHelper(
    private val cartPreviewView: CartPreviewView
) {

    fun onItemAdded(itemCount: Int, totalPrice: Double) {

        cartPreviewView.showCart(
            CartData(itemCount, totalPrice)
        )
    }

    fun onCartEmpty() {
        cartPreviewView.hideCart()
    }
}