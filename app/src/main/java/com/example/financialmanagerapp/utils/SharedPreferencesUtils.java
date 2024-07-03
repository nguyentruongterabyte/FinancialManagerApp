package com.example.financialmanagerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    protected static final String PREF_NAME = "MyAppPrefs";

    protected static final String KEY_USER_ID = "key_user_id";
    protected static final String KEY_EMAIL = "key_email";
    protected static final String KEY_PASSWORD = "key_password";
    protected static final String KEY_PASSWORD_CONFIRMATION = "key_password_confirmation";
    protected static final String KEY_NAME = "key_name";
    protected static final String KEY_INITIAL_CURRENCY_ID = "key_initial_currency_id";
    protected static final String KEY_INITIAL_CURRENCY_NAME = "key_initial_currency_name";
    protected static final String KEY_ACCESS_TOKEN = "key_access_token";
    protected static final String KEY_REFRESH_TOKEN = "key_refresh_token";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUserId(Context context, int id) {
        getSharedPreferences(context).edit().putInt(KEY_USER_ID, id).apply();
    }

    public static int getUserId(Context context) {
        return getSharedPreferences(context).getInt(KEY_USER_ID, -1);
    }

    public static void saveRefreshToken(Context context, String refreshToken) {
        getSharedPreferences(context).edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public static String getRefreshToken(Context context) {
        return getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null);
    }

    public static void saveAccessToken(Context context, String accessToken) {
        getSharedPreferences(context).edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public static String getAccessToken(Context context) {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null);
    }
    public static void saveName(Context context, String name) {
        getSharedPreferences(context).edit().putString(KEY_NAME, name).apply();
    }

    public static String getName(Context context) {
        return getSharedPreferences(context).getString(KEY_NAME, "");
    }
    public static void saveEmail(Context context, String email) {
        getSharedPreferences(context).edit().putString(KEY_EMAIL, email).apply();
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_EMAIL, "");
    }

    public static void savePassword(Context context, String password) {
        getSharedPreferences(context).edit().putString(KEY_PASSWORD, password).apply();
    }

    public static String getPassword(Context context) {
        return getSharedPreferences(context).getString(KEY_PASSWORD, "");
    }

    public static void savePasswordConfirmation(Context context, String passwordConfirmation) {
        getSharedPreferences(context).edit().putString(KEY_PASSWORD_CONFIRMATION, passwordConfirmation).apply();
    }

    public static String getPasswordConfirmation(Context context) {
        return getSharedPreferences(context).getString(KEY_PASSWORD_CONFIRMATION, "");
    }

    public static void saveInitialCurrencyId(Context context, int id) {
        getSharedPreferences(context).edit().putInt(KEY_INITIAL_CURRENCY_ID, id).apply();
    }

    public static int getInitialCurrencyId(Context context) {
        return getSharedPreferences(context).getInt(KEY_INITIAL_CURRENCY_ID,-1);
    }

    public static void saveInitialCurrencyName(Context context, String currencyName) {
        getSharedPreferences(context).edit().putString(KEY_INITIAL_CURRENCY_NAME, currencyName).apply();
    }

    public static String getInitialCurrencyName(Context context) {
        return getSharedPreferences(context).getString(KEY_INITIAL_CURRENCY_NAME,"");
    }

    public static void clearSharedPreferences(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

}
