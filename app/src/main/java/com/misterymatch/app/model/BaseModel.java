package com.misterymatch.app.model;


import android.support.annotation.NonNull;

import com.misterymatch.app.common.Constants;
import com.misterymatch.app.library.CustomException;
import com.misterymatch.app.library.ExceptionTracker;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.dto.response.BaseResponse;
import com.misterymatch.app.model.listener.IBaseModelListener;
import com.misterymatch.app.webservice.ApiClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by CSS03 on 10-01-2018.
 */

public abstract class BaseModel<T extends BaseResponse> implements IBaseModelListener<T> {

    protected String TAG = getClass().getSimpleName();
    private long mCurrentTaskId = -1;

    private Callback<T> baseModelCallBackListener = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            if (response.isSuccessful() && response.body() != null) {
//                Log.d(TAG, "body: " + new CodeSnippet(SpicApplication.getInstance()).getJsonStringFromObject(response.body()));
                T result = response.body();
                if (response.code() == Constants.InternalHttpCode.SUCCESS_CODE) {
                    onSuccessfulApi(mCurrentTaskId, result);
                } else {
                    onFailureApi(mCurrentTaskId, new CustomException(result));
                }
            } else {
                try {
                    Converter<ResponseBody, T> converter = ApiClient.getRetrofit().responseBodyConverter(BaseResponse.class, new Annotation[0]);

                    CustomException customException = new CustomException(converter.convert(response.errorBody()));
                    Log.d(TAG, "CustomException Error code : " + customException.getCode());
                    if (response.code() == Constants.InternalHttpCode.UNAUTH_CODE) {
                        onUnauthorizedSession(mCurrentTaskId, customException);
                        return;
                    }
                    onFailureApi(mCurrentTaskId, customException);
                } catch (IOException | NullPointerException e) {
                    ExceptionTracker.track(e);
                    onFailureApi(mCurrentTaskId, new CustomException(response.code(), "Our server is under maintenance. Kindly try after some time!"));
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onFailureApi(mCurrentTaskId, new CustomException(501, t.getLocalizedMessage()));
        }
    };

    protected void enQueueTask(long taskId, Call<T> tCall) {
        this.mCurrentTaskId = taskId;
        tCall.enqueue(baseModelCallBackListener);
    }
}
