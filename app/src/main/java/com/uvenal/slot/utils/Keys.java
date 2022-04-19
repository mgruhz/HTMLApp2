package com.uvenal.slot.utils;

public enum Keys {
    GLOBAL_SHARED("photoArtRoom"),
    FOV_KEY("fov"),
    FOU_KEY("fou"),
    LOU_KEY("lou"),
    WCK_KEY("wck"),
    ////////////////////////////////////
    p_installID("p_installID"),
    p_deepLink("p_deepLink"),
    //
    p_token("p_token"),
    //
    p_timeSplash("p_timeSplash");
    private final String code;
    Keys(String code){
        this.code = code;
    }
    public String getCode(){ return code;}
}
