package com.app.studentessentials.Gsons;

import android.view.View;



import java.util.ArrayList;

/**
 * Created by castle on 11/6/2017.
 */

public class TipItem {

    private String no;
    private String name;
    private Integer image_link;
    private String txt_tips_hint;
    private String url_link;
    public Integer subimg_link;
    public String suburl_link;


    private View.OnClickListener requestBtnClickListener;

    public TipItem() {
    }

    public TipItem(String no, String name, Integer image_link, String url_link,String txt_tips_hint, Integer subimage, String suburl) {
        this.no = no;
        this.name = name;
        this.image_link = image_link;
        this.url_link = url_link;
        this.txt_tips_hint=txt_tips_hint;
        this.subimg_link = subimage;
        this.suburl_link = suburl;
    }

    public String getTxt_tips_hint() {
        return txt_tips_hint;
    }

    public void setTxt_tips_hint(String txt_tips_hint) {
        this.txt_tips_hint = txt_tips_hint;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Integer getImage_link() {
        return image_link;
    }

    public void setImage_link(Integer image_link) {
        this.image_link = image_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getUrl_link() {
        return url_link;
    }

    public void setUrl_link(String url_link) {
        this.url_link = url_link;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }
}
