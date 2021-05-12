package org.parchmentmc.axe.build.trigger;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncPolledBuildTriggerFactory;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.BuildCustomizerFactory;
import org.jetbrains.annotations.NotNull;
import org.parchmentmc.axe.common.Constants;

public class MinecraftReleaseBuildTriggerService extends BuildTriggerService
{
    @NotNull
    private static final Logger                LOG = Logger.getInstance(Loggers.VCS_CATEGORY + MinecraftReleaseBuildTriggerService.class);
    private final        BuildTriggeringPolicy policy;

    public MinecraftReleaseBuildTriggerService(
      @NotNull final AsyncPolledBuildTriggerFactory triggerFactory,
      @NotNull final BuildCustomizerFactory buildCustomizerFactory)
    {
        this.policy = triggerFactory.createBuildTrigger(new MinecraftReleaseBuildTriggerPolicy(buildCustomizerFactory), LOG);
    }

    @NotNull
    @Override
    public String getName()
    {
        return Constants.MINECRAFT_RELEASE_TRIGGER_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return Constants.MINECRAFT_RELEASE_TRIGGER_DISPLAYNAME;
    }

    @NotNull
    @Override
    public String describeTrigger(@NotNull final BuildTriggerDescriptor trigger)
    {
        return Constants.MINECRAFT_RELEASE_TRIGGER_DESCRIPTION;
    }

    @NotNull
    @Override
    public BuildTriggeringPolicy getBuildTriggeringPolicy()
    {
        return policy;
    }
}
