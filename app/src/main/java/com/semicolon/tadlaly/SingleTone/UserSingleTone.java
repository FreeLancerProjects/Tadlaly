package com.semicolon.tadlaly.SingleTone;

import com.semicolon.tadlaly.Models.UserModel;

public class UserSingleTone {
    private static UserSingleTone instance =null;
    private UserModel userModel;
    private UserSingleTone() {
    }

    public static synchronized UserSingleTone getInstance()
    {
        if (instance==null)
        {
            instance = new UserSingleTone();
        }
        return instance;
    }

    public interface OnCompleteListener
    {
        void onSuccess(UserModel userModel);

    }

    public void  setUserModel(UserModel userModel)
    {
        this.userModel = userModel;
    }
    public void getUser(OnCompleteListener onCompleteListener)
    {
        onCompleteListener.onSuccess(userModel);
    }
}
