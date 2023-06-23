package com.projects.finn.utils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;

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

    private static final String KEY_REMOTE_SERVER = "remote_server";
}