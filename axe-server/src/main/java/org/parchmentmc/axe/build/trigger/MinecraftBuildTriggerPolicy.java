package org.parchmentmc.axe.build.trigger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.BuildType;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.BaseAsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.*;
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
import java.util.HashMap;
import java.util.Map;

public class MinecraftBuildTriggerPolicy extends BaseAsyncPolledBuildTrigger
{
    private static final Gson GSON = new GsonBuilder()
                                       .setDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
                                       .create();

    private final BuildCustomizerFactory buildCustomizerFactory;

    public MinecraftBuildTriggerPolicy(final BuildCustomizerFactory customizerFactory)
    {
        this.buildCustomizerFactory = customizerFactory;
    }

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
                final Date currentDate = previousValue != null ? getDateForRelease(manifest, previousValue) : Date.from(Instant.EPOCH);
                final Date latestSnapshotDate = getDateForRelease(manifest, manifest.getLatest().getSnapshot());
                final Date latestReleaseDate = getDateForRelease(manifest, manifest.getLatest().getRelease());

                if (currentDate.before(latestSnapshotDate) && latestSnapshotDate.before(latestReleaseDate)) {
                    final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(context.getBuildType(), null);
                    final Map<String, String> customParams = new HashMap<>();
                    customParams.put(jetbrains.buildServer.agent.Constants.ENV_PREFIX + "MC_VERSION", manifest.getLatest().getRelease());
                    buildCustomizer.setParameters(customParams);

                    final BuildPromotion buildPromotion = buildCustomizer.createPromotion();
                    buildPromotion.addToQueue(Constants.MINECRAFT_TRIGGER_NAME);
                    return manifest.getLatest().getRelease();
                }
                else if (currentDate.before(latestReleaseDate) && latestReleaseDate.before(latestSnapshotDate)) {
                    final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(context.getBuildType(), null);
                    final Map<String, String> customParams = new HashMap<>();
                    customParams.put(jetbrains.buildServer.agent.Constants.ENV_PREFIX + "MC_VERSION", manifest.getLatest().getSnapshot());
                    buildCustomizer.setParameters(customParams);

                    final BuildPromotion buildPromotion = buildCustomizer.createPromotion();
                    buildPromotion.addToQueue(Constants.MINECRAFT_TRIGGER_NAME);
                    return manifest.getLatest().getSnapshot();
                }
                else if (latestSnapshotDate.before(currentDate) && currentDate.before(latestReleaseDate)) {
                    final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(context.getBuildType(), null);
                    final Map<String, String> customParams = new HashMap<>();
                    customParams.put(jetbrains.buildServer.agent.Constants.ENV_PREFIX + "MC_VERSION", manifest.getLatest().getRelease());
                    buildCustomizer.setParameters(customParams);

                    final BuildPromotion buildPromotion = buildCustomizer.createPromotion();
                    buildPromotion.addToQueue(Constants.MINECRAFT_TRIGGER_NAME);
                    return manifest.getLatest().getRelease();
                }
                else if (latestReleaseDate.before(currentDate) && currentDate.before(latestSnapshotDate)) {
                    final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(context.getBuildType(), null);
                    final Map<String, String> customParams = new HashMap<>();
                    customParams.put(jetbrains.buildServer.agent.Constants.ENV_PREFIX + "MC_VERSION", manifest.getLatest().getSnapshot());
                    buildCustomizer.setParameters(customParams);

                    final BuildPromotion buildPromotion = buildCustomizer.createPromotion();
                    buildPromotion.addToQueue(Constants.MINECRAFT_TRIGGER_NAME);
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
