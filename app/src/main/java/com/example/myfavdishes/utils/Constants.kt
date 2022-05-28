package com.example.myfavdishes.utils

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    fun dishTypes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")
        return list

    }

    fun dishCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("pizza")
        list.add("bbq")
        list.add("bakery")
        list.add("burger")
        list.add("cafe")
        list.add("chicken")
        list.add("dessert")
        list.add("drinks")
        list.add("hot_dogs")
        list.add("juices")
        list.add("sandwich")
        list.add("tea & coffee")
        list.add("wraps")
        list.add("other")
        return list
    }

    fun dishCookingTimes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("160")
        list.add("180")
        return list
    }


}