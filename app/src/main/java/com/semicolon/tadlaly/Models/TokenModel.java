package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class TokenModel implements Serializable {
    private String token_id;

    public TokenModel(String token_id) {
        this.token_id = token_id;
    }

    public String getToken_id() {
        return token_id;
    }
}
