package net.mojang.manifest.versions;

public class Latest
{
    private String release;
    private String snapshot;

    public Latest()
    {
    }

    public Latest(final String release, final String snapshot)
    {
        this.release = release;
        this.snapshot = snapshot;
    }

    public String getRelease()
    {
        return release;
    }

    public void setRelease(String release)
    {
        this.release = release;
    }

    public String getSnapshot()
    {
        return snapshot;
    }

    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
    }
}
