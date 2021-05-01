package com.electroniccode.cooking.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object Utils {

    fun loadInterAd(context: Context, adRequest: AdRequest): InterstitialAd? {

        var interAd: InterstitialAd? = null

        InterstitialAd.load(
                context,
                "ca-app-pub-9413566793399770/3450242305",
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        interAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interAd = null
                        //openReceptDetailsScreen(documentPath, false);
                    }
                })
        return interAd
    }



}