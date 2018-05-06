package com.maria.gallery.mvp.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.maria.gallery.mvp.model.repository.FeedRepo;
import com.maria.gallery.mvp.model.network.OAuth;
import com.maria.gallery.mvp.view.GalleryView;
import com.maria.gallery.tool.SaveDataHelper;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> {

    private YandexAuthSdk sdk;

    @SuppressLint("CheckResult")
    public void loadFeed() {
        FeedRepo feedRepo = new FeedRepo();
        feedRepo.getFeed()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getViewState()::fillFeed, getViewState()::errorGetFeed);
    }

    public void login(Context context) {
        String token = SaveDataHelper.getToken(context);
        if ((token == null) || (token.length() == 0)) {
            sdk = new YandexAuthSdk(new YandexAuthOptions(context, true));
            getViewState().startYandexAuth(sdk.createLoginIntent(context, null));
        } else {
            onHaveToken(token);
        }
    }

    public void activityResult(Context context, int resultCode, Intent data) {
        onLogin(context, resultCode, data);
    }

    private void onLogin(Context context, int resultCode, Intent data) {
        try {
            final YandexAuthToken yandexAuthToken = sdk.extractToken(resultCode, data);
            if (yandexAuthToken != null) {
                String token = yandexAuthToken.getValue();
                SaveDataHelper.saveToken(token, context);

                onHaveToken(token);
            }
        } catch (YandexAuthException e) {
            getViewState().showMessage(e.getLocalizedMessage());
        }

    }

    private void onHaveToken(String token) {
        OAuth.setToken(token);
        getViewState().showFeed();
    }
}
