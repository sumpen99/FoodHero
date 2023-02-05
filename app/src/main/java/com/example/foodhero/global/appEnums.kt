package com.example.foodhero.global

enum class FragmentInstance {
    FRAGMENT_MAIN_HOME,
    FRAGMENT_LOGIN_HOME,
    FRAGMENT_LOGIN_OPTIONS,
}

// Hopefully this will not be needed
enum class SortOperation{
    SORT_ON_PRICE,
    SORT_ON_NAME,
    SORT_ON_COMMENTS,
    SORT_ON_RATING,
    SORT_ON_CATHEGORY,
    SORT_ON_CITY,
}

enum class ServerResult {
    UPLOAD_ERROR,
    UPLOAD_OK
}