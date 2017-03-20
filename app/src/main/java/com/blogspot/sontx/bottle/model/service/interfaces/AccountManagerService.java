package com.blogspot.sontx.bottle.model.service.interfaces;

import com.blogspot.sontx.bottle.model.bean.AccountBasicInfo;

public interface AccountManagerService extends ServiceBase {
    void resolveAsync(String id, Callback<AccountBasicInfo> callback);

    void updateUserPublicInfo(AccountBasicInfo basicInfo);
}