package com.semicolon.tadlaly.Services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.semicolon.tadlaly.Models.TokenModel;

import org.greenrobot.eventbus.EventBus;

public class MyfirebaseInstanceId extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        TokenModel tokenModel = new TokenModel(FirebaseInstanceId.getInstance().getToken());
        EventBus.getDefault().post(tokenModel);
    }
}
