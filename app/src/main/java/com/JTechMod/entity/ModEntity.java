package com.JTechMod.entity;

import org.json.JSONObject;

import com.JTechMod.entity.base.BaseEntity;

/**
 * 模型
 *
 * @author JTech
 */
public class ModEntity extends BaseEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void parseData(String data) {

    }

    @Override
    public String getDataStr() {
        return null;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}