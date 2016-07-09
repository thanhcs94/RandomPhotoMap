
package com.thanhcs.randomphoto.entities;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class GetPhoto {

    @SerializedName("ringtone")
    @Expose
    private List<Photo> ringtone = new ArrayList<Photo>();

    /**
     * 
     * @return
     *     The ringtone
     */
    public List<Photo> getRingtone() {
        return ringtone;
    }

    /**
     * 
     * @param ringtone
     *     The ringtone
     */
    public void setRingtone(List<Photo> ringtone) {
        this.ringtone = ringtone;
    }

}
