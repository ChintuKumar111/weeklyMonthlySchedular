package com.example.freshyzoappmodule.utils.helper

import com.example.freshyzoappmodule.data.model.CartDataModel

class CartPreviewHelper(
    private val cartPreviewView: CartPreviewView
) {

    fun onItemAdded(itemCount: Int, totalPrice: Double) {

        cartPreviewView.showCart(
            CartDataModel(itemCount, totalPrice)
        )
    }

    fun onCartEmpty() {
        cartPreviewView.hideCart()
    }
}