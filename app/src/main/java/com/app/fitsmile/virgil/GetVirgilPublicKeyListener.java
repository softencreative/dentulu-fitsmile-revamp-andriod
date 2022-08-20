package com.app.fitsmile.virgil;

import com.virgilsecurity.sdk.cards.Card;

public interface GetVirgilPublicKeyListener {
    void onPublicKeyFound(Card card);

    void onError(String error);
}
