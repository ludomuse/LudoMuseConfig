package com.ihmtek.ludomuseconfig;

/**
 * Created by IHMTEK Vibox 2 on 05/04/2017.
 */

public class Model {

    private int icon;
    private String text;

    public Model(int icon, String text)
    {
        super();
        this.icon = icon;
        this.text = text;
    }


    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getIcon()
    {
        return icon;
    }

    public String getText()
    {
        return text;
    }
}
