package jp.co.gnavi.meshclient.data;

import java.io.Serializable;

import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectListData implements Serializable
{
    String mstrListNo = "";
    String mstrTeam = "";
    String mstrTargetName = "";
    String mstrStartTime = "";

    String mstrIconImageUrl = null;
    int miIconResourceId = Utility.INVALID_ID;


    public void setListNo( String strListNo )
    {
        mstrListNo = strListNo;
    }

    public String getListNo()
    {
        return mstrListNo;
    }

    public void setTeam( String strTeam )
    {
        mstrTeam = strTeam;
    }

    public String getTeam()
    {
        return mstrTeam;
    }

    public void setTargetName( String strName )
    {
        mstrTargetName = strName;
    }

    public String getTargetName()
    {
        return mstrTargetName;
    }


    public void setIconImageUrl( String strImageUrl )
    {
        mstrIconImageUrl = strImageUrl;
    }

    public String getIconImageUrl()
    {
        return mstrIconImageUrl;
    }

    public void setIconResourceId( int iResourceId )
    {
        miIconResourceId = iResourceId;
    }

    public int getIconResourceId()
    {
        return miIconResourceId;
    }


    public void setStartTime( String strDate )
    {
        mstrStartTime = strDate;
    }

    public String getStartTime()
    {
        return mstrStartTime;
    }
}
