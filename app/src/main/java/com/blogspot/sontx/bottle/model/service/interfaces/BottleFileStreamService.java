package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.UploadResult;
import com.blogspot.sontx.bottle.model.service.Callback;

public interface BottleFileStreamService extends ServiceBase {
    void uploadAsync(String mediaPath, Callback<UploadResult> callback);
}
