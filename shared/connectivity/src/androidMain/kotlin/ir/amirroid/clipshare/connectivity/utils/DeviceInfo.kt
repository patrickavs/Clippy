package ir.amirroid.clipshare.connectivity.utils

import android.os.Build

fun getDeviceName() = "${Build.MANUFACTURER} ${Build.MODEL}"