package com.devd.diary.data

enum class CanScrollDirection {
    CAN_NOT_SCROLL_LEFT,
    CAN_NOT_SCROLL_RIGHT,
    CAN_SCROLL_ANYWHERE,
    CAN_NOT_SCROLL_ANYWHERE;
}


fun CanScrollDirection.isShowLeftScroll(): Boolean {
    return this != CanScrollDirection.CAN_NOT_SCROLL_LEFT && this != CanScrollDirection.CAN_NOT_SCROLL_ANYWHERE
}

fun CanScrollDirection.isShowRightScroll(): Boolean {
    return this != CanScrollDirection.CAN_NOT_SCROLL_RIGHT && this != CanScrollDirection.CAN_NOT_SCROLL_ANYWHERE
}