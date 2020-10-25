package wee.digital.fpa.repository.base

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class BaseSharedPref {

    private var sharedPref: EncryptedSharedPreferences? = null

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun init(context: Context) {
        sharedPref = EncryptedSharedPreferences.create(
                "secret_wee_encrypted",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM) as EncryptedSharedPreferences
    }

    fun saveStringValue(alias: String, str: String) {
        sharedPref!!.edit().putString(alias, str).apply()
    }

    fun getStringValue(alias: String, default: String): String {
        return sharedPref!!.getString(alias, default) ?: ""
    }

    fun saveStringSetValue(alias: String, str: Set<String>) {
        sharedPref!!.edit().putStringSet(alias, str).apply()
    }

    fun getStringSetValue(alias: String, hashSet: HashSet<String>): Set<String> {
        return sharedPref!!.getStringSet(alias, hashSet) ?: HashSet()
    }

    fun saveBooleanValue(alias: String, bool: Boolean) {
        sharedPref!!.edit().putBoolean(alias, bool).apply()
    }

    fun getBooleanValue(alias: String, default: Boolean): Boolean {
        return sharedPref!!.getBoolean(alias, default)
    }

    fun saveIntValue(alias: String, intNumber: Int) {
        sharedPref!!.edit().putInt(alias, intNumber).apply()
    }

    fun getIntValue(alias: String, default: Int): Int {
        return sharedPref!!.getInt(alias, default)
    }

    fun saveFloatValue(alias: String, floatNumber: Float) {
        sharedPref!!.edit().putFloat(alias, floatNumber).apply()
    }

    fun getFloatValue(alias: String, default: Float): Float {
        return sharedPref!!.getFloat(alias, default)
    }

    fun removeValue(alias: String) {
        sharedPref!!.edit().remove(alias).apply()
    }
}
