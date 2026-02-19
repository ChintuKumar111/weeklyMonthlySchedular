package com.example.freshyzoappmodule.helper

import com.example.freshyzoappmodule.data.model.CartStateModel

class CartPreviewHelper(
    private val cartPreviewView: CartPreviewView
) {

    fun onItemAdded(itemCount: Int, totalPrice: Double) {

        cartPreviewView.showCart(
            CartStateModel(itemCount, totalPrice)
        )
    }

    fun onCartEmpty() {
        cartPreviewView.hideCart()
    }
}