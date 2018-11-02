package com.codesgood.restaurants.data.model

/**
 * Restaurant's data class/model.
 *
 * @author Amilcar Serrano
 */
data class Restaurant(
    val deliveryTimeMinMinutes: String = "",
    val validReviewsCount: Int = 0,
    val cityName: String = "",
    val sortingLogistics: Int = 0,
    val allCategories: String = "",
    val sortingDistance: Double = 0.toDouble(),
    val doorNumber: String = "",
    val minDeliveryAmount: Double = 0.toDouble(),
    val deliveryTimeOrder: Int = 0,
    val description: String = "",
    val sortingTalent: Int = 0,
    val mandatoryPaymentAmount: Boolean = false,
    val shippingAmountIsPercentage: Boolean = false,
    val sortingVip: Int = 0,
    val opened: Int = 0,
    val index: Int = 0,
    val deliveryAreas: String = "",
    val favoritesCount: Int = 0,
    val isNew: Boolean = false,
    val delivers: Boolean = false,
    val businessType: String = "",
    val nextHourClose: String = "",
    val hasOnlinePaymentMethods: Boolean = false,
    val homeVip: Boolean = false,
    val discount: Double = 0.toDouble(),
    val deliveryTime: String = "",
    val food: Double = 0.toDouble(),
    val deliveryTimeId: Int = 0,
    val sortingNew: Int = 0,
    val nextHour: String = "",
    val acceptsPreOrder: Boolean = false,
    val weighing: Double = 0.toDouble(),
    val noIndex: Boolean = false,
    val restaurantRegisteredDate: String = "",
    val link: String = "",
    val sortingCategory: Int = 0,
    val restaurantTypeId: Int = 0,
    val generalScore: Double = 0.toDouble(),
    val isGoldVip: Boolean = false,
    val topCategories: String = "",
    val id: Int = 0,
    val distance: Double = 0.toDouble(),
    val area: String = "",
    val name: String = "",
    val sortingReviews: Double = 0.toDouble(),
    val coordinates: String = "",
    val maxShippingAmount: Double = 0.toDouble(),
    val ratingScore: String = "",
    val logo: String = "",
    val deliveryTimeMaxMinutes: String = "",
    val deliveryType: String = "",
    val speed: Double = 0.toDouble(),
    val favoriteByUser: Boolean = false,
    val deliveryZoneId: Int = 0,
    val sortingOrderCount: Int = 0,
    val sortingOnlinePayment: Int = 0,
    val shippingAmount: Int = 0,
    val sortingGroupOrderCount: Int = 0,
    val address: String = "",
    val hasZone: Boolean = false,
    val stateId: Int = 0,
    val favoriteByOrders: Boolean = false,
    val service: Double = 0.toDouble(),
    val paymentMethods: String = "",
    val rating: String = "",
    val withLogistics: Boolean = false
)