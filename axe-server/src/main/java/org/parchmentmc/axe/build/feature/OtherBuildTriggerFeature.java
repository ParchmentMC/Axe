package org.parchmentmc.axe.build.feature;

import com.google.common.collect.ImmutableMap;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.parchmentmc.axe.build.feature.OtherBuildTriggerController.PATH;

public class OtherBuildTriggerFeature extends BuildFeature
{
    public static final String TRIGGER_BUILD_FEATURE_TYPE = "triggerBuildFeature";
    public static final String TRIGGER_BUILD_FEATURE_NAME = "Trigger Build";
    public static final String TRIGGERS_PARAMETER_NAME = "triggers";
    public static final String PARAMETERS_PARAMETER_NAME = "parameters";

    private final PluginDescriptor pluginDescriptor;

    public OtherBuildTriggerFeature(final PluginDescriptor pluginDescriptor) {this.pluginDescriptor = pluginDescriptor;}

    @NotNull
    @Override
    public String getType()
    {
        return TRIGGER_BUILD_FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return TRIGGER_BUILD_FEATURE_NAME;
    }

    @Nullable
    @Override
    public String getEditParametersUrl()
    {
        return pluginDescriptor.getPluginResourcesPath(PATH);
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultParameters()
    {
        return ImmutableMap.of(
          TRIGGERS_PARAMETER_NAME, "",
          PARAMETERS_PARAMETER_NAME, ""
        );
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull final Map<String, String> params)
    {
        return "Triggers build: " + params.get(TRIGGERS_PARAMETER_NAME);
    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor()
    {
        return properties -> {
            List<InvalidProperty> result = new ArrayList<>();
            if (!properties.containsKey(TRIGGERS_PARAMETER_NAME) || properties.get(TRIGGERS_PARAMETER_NAME) == null || properties.get(TRIGGERS_PARAMETER_NAME).isEmpty()) {
                result.add(new InvalidProperty(TRIGGERS_PARAMETER_NAME, "Build configuration must be specified"));
            }

            return result;
        };
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed()
    {
        return true;
    }

    @Override
    public boolean isRequiresAgent()
    {
        return false;
    }

}
