package com.can.funspart.api;

/**
 *
 * Singleton Factory with retrofit
 */
public class ApiFactory {


    protected static final Object monitor = new Object();
    static FunspartApi funspartApiSingleton = null;


    //return Singleton
    public static FunspartApi getFunspartApiSingleton() {
        synchronized (monitor) {
            if (funspartApiSingleton == null) {
                funspartApiSingleton = new ApiRetrofit().getFunspartApiService();
            }
            return funspartApiSingleton;
        }
    }




}
