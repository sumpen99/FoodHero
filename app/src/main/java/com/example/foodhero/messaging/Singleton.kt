package com.example.foodhero.messaging
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.lang.ref.WeakReference

class Singleton(context: Context){
    private var requestQueue: RequestQueue?
    private var ctx: WeakReference<Context>
    init {
        ctx = WeakReference(context)
        requestQueue = getRequestQueue()
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.get()?.applicationContext)
        }
        return requestQueue
    }

    fun addToRequestQueue(req: Request<*>) {
        getRequestQueue()?.add(req)
    }

    companion object {
        private var instance: Singleton? = null
        @Synchronized
        fun getInstance(context: Context): Singleton? {
            if (instance == null) {
                instance = Singleton(context)
            }
            return instance
        }
    }
}