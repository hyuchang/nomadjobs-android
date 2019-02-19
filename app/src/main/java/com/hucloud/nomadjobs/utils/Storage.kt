package com.hucloud.nomadjobs.utils

import android.content.Context

/**
 * 프로젝트명    : toitdoit
 * 패키지명      : com.valuebiz.toitdoit
 * 작성 및 소유자 : hucloud(huttchang@gmail.com)
 * 최초 생성일   : 2018. 7. 26.
 */
class Storage(context: Context) {
    val prefrence = context.getSharedPreferences("T@itD@it-St@r@g#!$!##@T$^^#$@@$",Context.MODE_PRIVATE)

    fun addPrefStr(key: String, value: String) {
        prefrence.edit().putString(key, value).commit()
    }

    fun addPrefBoolean(key: String, value: Boolean) {
        prefrence.edit().putBoolean(key, value).commit()
    }

    fun getPrefStr(key: String): String? {
        return prefrence.getString(key, null)
    }

    @JvmOverloads
    fun getPrefBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefrence.getBoolean(key, defaultValue)
    }

    fun deletePref(key: String) {
        try {
            prefrence.edit().remove(key).commit()
        } catch (e: Exception) {
        }

    }
}