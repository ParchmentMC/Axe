package net.mojang.manifest.versions;

import java.util.List;

public class Manifest{

    private Latest        latest;
    private List<Version> versions;

    public Manifest()
    {
    }

    public Manifest(final Latest latest, final List<Version> versions)
    {
        this.latest = latest;
        this.versions = versions;
    }

    public Latest getLatest()
    {
        return latest;
    }

    public void setLatest(Latest latest)
    {
        this.latest = latest;
    }

    public List<Version> getVersions()
    {
        return versions;
    }

    public void setVersions(List<Version> versions)
    {
        this.versions = versions;
    }
}

