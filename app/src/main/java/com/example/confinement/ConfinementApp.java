package com.example.confinement;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ConfinementApp  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("confinement.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}