package net.mojang.manifest.versions;

import java.util.Date;

public class Version
{
    private String id;
    private String type;
    private String url;
    private Date   time;
    private Date   releaseTime;
    private String sha1;
    private int    complianceLevel;

    public Version()
    {
    }

    public Version(final String id, final String type, final String url, final Date time, final Date releaseTime, final String sha1, final int complianceLevel)
    {
        this.id = id;
        this.type = type;
        this.url = url;
        this.time = time;
        this.releaseTime = releaseTime;
        this.sha1 = sha1;
        this.complianceLevel = complianceLevel;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getReleaseTime()
    {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime)
    {
        this.releaseTime = releaseTime;
    }

    public String getSha1()
    {
        return sha1;
    }

    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }

    public int getComplianceLevel()
    {
        return complianceLevel;
    }

    public void setComplianceLevel(int complianceLevel)
    {
        this.complianceLevel = complianceLevel;
    }
}
