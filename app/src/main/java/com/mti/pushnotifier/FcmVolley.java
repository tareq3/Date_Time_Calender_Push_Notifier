/*
 * Created by Tareq Islam on 6/28/18 12:59 PM
 *
 *  Last modified 6/28/18 12:59 PM
 */

package com.mti.pushnotifier;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/***
 * Created by Tareq on 28,June,2018.
 */
public class FcmVolley {

    private static FcmVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private FcmVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized FcmVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FcmVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
