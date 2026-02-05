📦 Cart Preview Module — Bottom Floating Cart Bar
📌 Overview

Cart Preview Module is a reusable bottom floating cart component used to display:

Total items added to cart

Total cart price

View Cart navigation

Smooth slide-up animation

This component is designed to be plug & play, reusable across multiple screens like:

Product Listing

Product Details

Home Screen

Category Screen

🎯 Features

✅ Bottom floating cart bar
✅ Slide-up animation (Blinkit / Zepto style)
✅ Dynamic cart updates
✅ Auto hide when cart empty
✅ View Cart click support
✅ Reusable custom view


📁 Module Structure
cartpreview/
 ├── CartPreviewView.kt
 ├── CartPreviewHelper.kt
 ├── CartData.kt
 ├── bottom_sheet_cart_preview.xml
 └── README.md

🧱 Components
1️⃣ CartPreviewView

Custom UI component responsible for:

Showing cart preview

Updating UI

Running animations

Handling visibility

2️⃣ CartPreviewHelper

Helper layer used by Activity / Fragment to:

Update cart UI

Pass cart data

Keep UI logic separate

3️⃣ CartData

Model class containing:

data class CartData(
    val itemCount: Int,
    val totalPrice: Double
)

⚙️ Integration Steps
✅ Step 1 — Add View To Layout
<com.example.app.cartpreview.CartPreviewView
    android:id="@+id/cartPreviewView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"/>

✅ Step 2 — Initialize Helper
lateinit var cartPreviewHelper: CartPreviewHelper

cartPreviewHelper =
    CartPreviewHelper(binding.cartPreviewView)

✅ Step 3 — Update Cart UI
cartPreviewHelper.onItemAdded(
    itemCount = 2,
    totalPrice = 300.0
)

✅ Step 4 — Hide Cart When Empty
binding.cartPreviewView.hideCart()

✅ Step 5 — View Cart Navigation

Inside Activity:

binding.cartPreviewView.onViewCartClick = {

    startActivity(
        Intent(this, CartDashboardActivity::class.java)
    )
}

🔄 Recommended Production Flow
Add To Cart Button

        ↓
ViewModel

        ↓
Repository

        ↓
Backend API

        ↓
Cart LiveData / Flow

        ↓
CartPreviewHelper

        ↓
CartPreviewView