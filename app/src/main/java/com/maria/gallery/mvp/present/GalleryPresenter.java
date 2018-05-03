package com.maria.gallery.mvp.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.maria.gallery.adapter.ImagesRowAdapter;
import com.maria.gallery.mvp.model.FeedRepo;
import com.maria.gallery.mvp.model.OAuth;
import com.maria.gallery.mvp.model.data.Image;
import com.maria.gallery.mvp.model.data.ImagesPair;
import com.maria.gallery.mvp.view.GalleryView;
import com.maria.gallery.tool.SaveDataHelper;
import com.maria.gallery.ui.GalleryActivity;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> {

    private static final int REQUEST_LOGIN_SDK = 2;

    private YandexAuthSdk sdk;

    private final FeedRepo feedRepo = new FeedRepo();

    public void parseFeed(List<Image> images) {
        List<ImagesPair> imagesPairs = new ArrayList<>();

        int i = 0;
        while (i < images.size() - 1) {
            ImagesPair row = new ImagesPair(images.get(i++), images.get(i++));
            imagesPairs.add(row);
        }

        /*if (images.size() % 2 == 1) {
            ImagesPair row = new ImagesPair(images.get(i), img);
            imagesPairs.add(row);
        }*/

        getViewState().onRowsSet(imagesPairs);
    }

    @SuppressLint("CheckResult")
    public void loadFeed() {
        feedRepo.getFeed()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getViewState()::fillFeed, getViewState()::errorGetFeed);
    }

    @Override
    protected void onFirstViewAttach() {
        int b = this.getAttachedViews().size();
        super.onFirstViewAttach();
        int gb = this.getAttachedViews().size();
     }

    public void login(Context context) {
        String token = SaveDataHelper.getToken(context);
        if ((token == null) || (token.length() == 0)) {
            sdk = new YandexAuthSdk(new YandexAuthOptions(context, true));
            getViewState().startYandexAuthActivity(sdk.createLoginIntent(context, null), REQUEST_LOGIN_SDK);
        } else {
            onHaveToken(token);
        }
    }

    public void activityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN_SDK) {
            onLogin(context, resultCode, data);
        }

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
        OAuth.token = token;
        getViewState().showFeed();
    }
}
