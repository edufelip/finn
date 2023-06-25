package com.edufelip.finn.utils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.edufelip.finn.BuildConfig;
import com.edufelip.finn.R;

public class RemoteConfigUtils {
    private final FirebaseRemoteConfig remote = fetchAndActivate();

    private FirebaseRemoteConfig fetchAndActivate() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().build());
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        long cacheExpiration = BuildConfig.DEBUG ? 1L : 3600L;
        remoteConfig.fetch(cacheExpiration).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.activate();
            }
        });

        return remoteConfig;
    }

    public String getRemoteServerAddress() {
        return remote.getString(KEY_REMOTE_SERVER);
    }

    public boolean getIsFacebookAuthEnabled() {
        return remote.getBoolean(IS_FACEBOOK_AUTH_ENABLED);
    }

    private static final String KEY_REMOTE_SERVER = "remote_server";
    private static final String IS_FACEBOOK_AUTH_ENABLED = "is_facebook_auth_enabled";
}