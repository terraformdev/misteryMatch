package com.misterymatch.app.model.listener;


import com.misterymatch.app.library.CustomException;

import java.util.HashMap;

public interface IBaseModelListener<T> {

    void onSuccessfulApi(long taskId, T response);

    void onSuccessfulApi(long taskId, HashMap<String, String> response);

    void onFailureApi(long taskId, CustomException e);

    void onUnauthorizedSession(long taskId, CustomException e);
}
