package com.misterymatch.app.common;

/**
 * Created by CSS03 on 10-01-2018.
 */

public interface Constants {


    interface BundleKey {

    }

    interface BroadCastType {

    }

    interface RequestCodes {

    }

    interface ApiRequestKey {

    }

    interface Permission {

    }

    interface InternalHttpCode {
        int SUCCESS_CODE = 200;
        int FAILURE_CODE = 204;
        int UNAUTH_CODE = 401;
        int SERVER_ERROR = 500;
    }

    interface HttpErrorMessage {
        String INTERNAL_SERVER_ERROR = "server maintance error";
    }
}
