package org.parchmentmc.axe.build.trigger;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncPolledBuildTriggerFactory;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;
import org.parchmentmc.axe.common.Constants;

public class MinecraftSnapshotBuildTriggerService extends BuildTriggerService
{
    @NotNull
    private static final Logger                LOG = Logger.getInstance(Loggers.VCS_CATEGORY + MinecraftSnapshotBuildTriggerService.class);
    private final        BuildTriggeringPolicy policy;

    public MinecraftSnapshotBuildTriggerService(
      @NotNull final AsyncPolledBuildTriggerFactory triggerFactory
    )
    {
        this.policy = triggerFactory.createBuildTrigger(new MinecraftSnapshotBuildTriggerPolicy(), LOG);
    }

    @NotNull
    @Override
    public String getName()
    {
        return Constants.MINECRAFT_SNAPSHOT_TRIGGER_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return Constants.MINECRAFT_SNAPSHOT_TRIGGER_DISPLAYNAME;
    }

    @NotNull
    @Override
    public String describeTrigger(@NotNull final BuildTriggerDescriptor trigger)
    {
        return Constants.MINECRAFT_SNAPSHOT_TRIGGER_DESCRIPTION;
    }

    @NotNull
    @Override
    public BuildTriggeringPolicy getBuildTriggeringPolicy()
    {
        return policy;
    }
}
