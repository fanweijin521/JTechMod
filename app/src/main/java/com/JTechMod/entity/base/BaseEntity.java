package com.JTechMod.entity.base;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 类对象基类
 *
 * @author JTech
 */
public abstract class BaseEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private long mId = 0;

    public long getmId() {
        if (0 == mId) {
            mId = System.currentTimeMillis();
        }
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public abstract void parseData(String data);

    public abstract String getDataStr();
}