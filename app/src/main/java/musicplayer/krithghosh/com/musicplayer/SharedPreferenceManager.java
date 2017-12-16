package musicplayer.krithghosh.com.musicplayer;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class SharedPreferenceManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private Gson gson = null;
    private Type typeOfObject = null;

    public SharedPreferenceManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
        gson = new Gson();
        typeOfObject = new TypeToken<Object>() {
        }.getType();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public float getFloat(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value).commit();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, null);
    }

    public void removeValue(String key) {
        editor.remove(key).commit();
    }

    public void logoutUser() {
        removeAll();
    }

    public void removeAll() {
        editor.clear();
        editor.commit();
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, gson.toJson(object));
        editor.commit();
    }

    public <T> T getObject(String key, Class<T> a) {
        String value = sharedPreferences.getString(key, null);
        if (value == null) {
            return null;
        }
        return gson.fromJson(value, a);
    }
}