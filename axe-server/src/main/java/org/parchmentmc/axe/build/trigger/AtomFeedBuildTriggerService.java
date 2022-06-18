package org.parchmentmc.axe.build.trigger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncPolledBuildTriggerFactory;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.BuildCustomizerFactory;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.parchmentmc.axe.common.Constants;

import java.util.ArrayList;
import java.util.List;

public class AtomFeedBuildTriggerService extends BuildTriggerService
{
    @NotNull
    private static final Logger                LOG = Logger.getInstance(Loggers.VCS_CATEGORY + AtomFeedBuildTriggerService.class);
    private final        BuildTriggeringPolicy policy;
    @NotNull
    private final PluginDescriptor pluginDescriptor;


    public AtomFeedBuildTriggerService(
      @NotNull PluginDescriptor pluginDescriptor,
      @NotNull final AsyncPolledBuildTriggerFactory triggerFactory,
      @NotNull BuildCustomizerFactory customizerFactory
    )
    {
        this.policy = triggerFactory.createBuildTrigger(new AtomFeedBuildTriggerPolicy(customizerFactory), LOG);
        this.pluginDescriptor = pluginDescriptor;
    }

    @NotNull
    @Override
    public String getName()
    {
        return Constants.ATOM_TRIGGER_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return Constants.ATOM_TRIGGER_DISPLAYNAME;
    }

    @NotNull
    @Override
    public String describeTrigger(@NotNull final BuildTriggerDescriptor trigger)
    {
        final String value = trigger.getProperties().get(Constants.ATOM_TRIGGER_URL_PARAM);
        if (value == null) {
            return Constants.ATOM_TRIGGER_DESCRIPTION;
        }

        return String.format(Constants.ATOM_TRIGGER_DESCRIPTION, value);
    }

    @NotNull
    @Override
    public BuildTriggeringPolicy getBuildTriggeringPolicy()
    {
        return policy;
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return this.pluginDescriptor.getPluginResourcesPath("editAtomFeedBuildTrigger.jsp");
    }

    @Nullable
    @Override
    public PropertiesProcessor getTriggerPropertiesProcessor() {
        return (properties) -> {
            List<InvalidProperty> invalidProps = new ArrayList<>();
            String url = properties.get(Constants.ATOM_TRIGGER_URL_PARAM);
            if (StringUtil.isEmptyOrSpaces(url)) {
                invalidProps.add(new InvalidProperty(Constants.ATOM_TRIGGER_URL_PARAM, "URL must be specified"));
            }

            String lastUpdatedPath = properties.get(Constants.ATOM_TRIGGER_LAST_UPDATED_PATH);
            if (StringUtil.isEmptyOrSpaces(lastUpdatedPath)) {
                invalidProps.add(new InvalidProperty(Constants.ATOM_TRIGGER_LAST_UPDATED_PATH, "Last updated XPath must be specified"));
            }

            String firstEntryPath = properties.get(Constants.ATOM_TRIGGER_NEW_ENTRY_PATH);
            if (StringUtil.isEmptyOrSpaces(firstEntryPath)) {
                invalidProps.add(new InvalidProperty(Constants.ATOM_TRIGGER_NEW_ENTRY_PATH, "New updated XPath must be specified"));
            }

            return invalidProps;
        };

    }
}
