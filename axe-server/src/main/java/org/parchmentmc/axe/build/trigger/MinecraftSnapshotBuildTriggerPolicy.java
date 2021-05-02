package org.parchmentmc.axe.build.trigger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledBuildTrigger;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.BaseAsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.SimpleParameter;
import net.mojang.manifest.versions.Manifest;
import net.mojang.manifest.versions.Version;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.parchmentmc.axe.common.Constants;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class MinecraftSnapshotBuildTriggerPolicy extends BaseAsyncPolledBuildTrigger
{
    private static final Gson GSON = new GsonBuilder()
                                       .setDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
                                       .create();

    private static Date getDateForRelease(final Manifest manifest, final String releaseName) {
        return manifest.getVersions().stream().filter(v -> v.getId().equals(releaseName)).map(Version::getReleaseTime).findFirst().orElse(Date.from(Instant.EPOCH));
    }

    @Nullable
    @Override
    public String triggerBuild(@Nullable final String previousValue, @NotNull final PolledTriggerContext context) throws BuildTriggerException
    {
        try(final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            final HttpGet versionsGetRequest = new HttpGet(Constants.MINECRAFT_RELEASE_VERSIONS_MANIFEST);
            try(final CloseableHttpResponse response = client.execute(versionsGetRequest)) {
                if (response.getStatusLine().getStatusCode() < 200 && response.getStatusLine().getStatusCode() > 299)
                {
                    throw new IllegalStateException("Could not successfully download the versions json.");
                }

                final HttpEntity entity = response.getEntity();
                final String bodyJson = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                final Manifest manifest = GSON.fromJson(bodyJson, Manifest.class);
                final Date currentSnapshotDate = previousValue != null ? getDateForRelease(manifest, previousValue) : Date.from(Instant.EPOCH);
                final Date latestSnapshotDate = getDateForRelease(manifest, manifest.getLatest().getSnapshot());

                if (currentSnapshotDate.before(latestSnapshotDate)) {
                    context.getBuildType().addBuildParameter(new SimpleParameter(jetbrains.buildServer.agent.Constants.ENV_PREFIX + "MC_VERSION", manifest.getLatest().getSnapshot()));
                    context.getBuildType().addToQueue(Constants.MINECRAFT_SNAPSHOT_TRIGGER_NAME);
                    return manifest.getLatest().getSnapshot();
                }

                return previousValue;
            }
        }
        catch(Exception ex) {
            throw new BuildTriggerException("Failed to trigger build", ex);
        }
    }
}
