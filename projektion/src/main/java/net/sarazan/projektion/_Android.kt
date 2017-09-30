package net.sarazan.projektion

import android.os.Handler
import android.os.Looper

/**
 * Created by Aaron Sarazan on 1/14/17
 * Copyright(c) 2017 Level, Inc.
 */

val Any.TAG: String get() = javaClass.simpleName

fun post(fn: () -> Unit) = Handler().post(fn)
fun postMain(fn: () -> Unit) = Handler(Looper.getMainLooper()).post(fn)