package jp.co.gnavi.meshclient.data;

/**
 * Created by kaifuku on 2016/11/01.
 */

public class UserData {
    int     miId;
    String  mstrCode;
    String  mstrName;
    String  mstrOrganization;

    public UserData( int id, String code, String name, String org )
    {
        miId = id;
        mstrCode = code;
        mstrName = name;
        mstrOrganization = org;
    }

    public int getId()
    {
        return miId;
    }

    public String getCode()
    {
        return mstrCode;
    }

    public String getName()
    {
        return mstrName;
    }

    public String getOrganization()
    {
        return mstrOrganization;
    }
}
