package org.parchmentmc.axe.build.feature;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OtherBuildTriggerListener extends BuildServerAdapter
{
    @NotNull
    private final ProjectManager         projectManager;
    @NotNull
    private final BuildCustomizerFactory buildCustomizerFactory;

    public OtherBuildTriggerListener(
      @NotNull EventDispatcher<BuildServerListener> events,
      @NotNull final ProjectManager projectManager,
      @NotNull final BuildCustomizerFactory buildCustomizerFactory)
    {
        this.projectManager = projectManager;
        this.buildCustomizerFactory = buildCustomizerFactory;
        events.addListener(this);
    }

    @Override
    public void buildFinished(@NotNull final SRunningBuild build)
    {
        if (!build.getFailureReasons().isEmpty())
        {
            return;
        }

        final Collection<SBuildFeatureDescriptor> triggerFeatures = build.getBuildFeaturesOfType(OtherBuildTriggerFeature.TRIGGER_BUILD_FEATURE_TYPE);
        if (triggerFeatures.isEmpty())
        {
            return;
        }

        triggerFeatures.forEach(feature -> {
            final String configurationToTriggerName = feature.getParameters().get(OtherBuildTriggerFeature.TRIGGERS_PARAMETER_NAME);
            if (configurationToTriggerName == null)
            {
                return;
            }

            final Optional<SBuildType> buildType = getRequestedBuildType(configurationToTriggerName);
            if (!buildType.isPresent())
            {
                return;
            }

            final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(buildType.get(), null);
            if (feature.getParameters().containsKey(OtherBuildTriggerFeature.PARAMETERS_PARAMETER_NAME))
            {
                final String parameters = feature.getParameters().get(OtherBuildTriggerFeature.PARAMETERS_PARAMETER_NAME);
                if (parameters != null && parameters.trim().length() != 0)
                {
                    final Map<String, String> customParams = new HashMap<>();

                    Arrays.stream(parameters.split("\n"))
                      .filter(param -> param.contains("="))
                      .map(param -> param.split("="))
                      .filter(param -> param.length >= 2)
                      .forEach(param -> customParams.put(param[0], String.join("=", Arrays.copyOfRange(param, 1, param.length))));

                    buildCustomizer.setParameters(customParams);
                }
            }
            final BuildPromotion promotion = buildCustomizer.createPromotion();
            promotion.addToQueue(build.getTriggeredBy().getRawTriggeredBy());
        });
    }

    @NotNull
    public Optional<SBuildType> getRequestedBuildType(final String externalId)
    {
        return projectManager.getProjects()
          .stream()
          .flatMap(project -> project.getBuildTypes().stream())
          .filter(sBuildType -> sBuildType.getExternalId().equals(externalId))
          .findFirst();
    }
}
