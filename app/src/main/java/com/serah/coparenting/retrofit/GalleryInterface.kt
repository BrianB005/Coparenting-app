package com.serah.coparenting.retrofit

interface GalleryInterface {
    fun success(galleryItems:List<Gallery>)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}