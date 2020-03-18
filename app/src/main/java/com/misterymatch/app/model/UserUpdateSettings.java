package com.misterymatch.app.model;

/**
 * Created by CSS03 on 10-01-2018.
 */
/*
public class UserUpdateSettings extends BaseModel<NewSettingsResponse> {

    private long mCurrentTaskId = -1;
    private IBaseModelListener<NewSettingsResponse> iBaseModelListener;

    public UserUpdateSettings(IBaseModelListener<NewSettingsResponse> iBaseModelListener) {
        this.iBaseModelListener = iBaseModelListener;
    }

    public void getUpdatedSettings(long taskId, BaseRequest request, HashMap<String, String> map) {
        this.mCurrentTaskId = taskId;
        enQueueTask(mCurrentTaskId, ApiClient.getAbsRetrofit().create(ApiInterface.class).newSettings(request.getAccessToken(),map));
    }

    @Override
    public void onSuccessfulApi(long taskId, NewSettingsResponse response) {

    }

    @Override
    public void onSuccessfulApi(long taskId, HashMap<String, String> response) {
        iBaseModelListener.onSuccessfulApi(taskId, response);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        iBaseModelListener.onFailureApi(taskId, e);
    }

    @Override
    public void onUnauthorizedSession(long taskId, CustomException e) {
        iBaseModelListener.onUnauthorizedSession(taskId, e);
    }
}*/
