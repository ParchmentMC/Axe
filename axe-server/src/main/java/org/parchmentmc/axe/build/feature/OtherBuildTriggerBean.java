package org.parchmentmc.axe.build.feature;

import jetbrains.buildServer.serverSide.BuildTypeSettings;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.dependency.Dependent;
import jetbrains.buildServer.web.util.BuildTypesHierarchyBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class OtherBuildTriggerBean
{
    private final ProjectManager projectManager;
    private final boolean    fillBuildTypes;
    private final SBuildType selectedBuildType;

    public OtherBuildTriggerBean(
      @NotNull ProjectManager projectManager,
      @Nullable SBuildType selectedBuildType,
      boolean fillBuildTypes)
    {
        super();
        this.projectManager = projectManager;
        this.fillBuildTypes = fillBuildTypes;
        this.selectedBuildType = selectedBuildType;
    }

    @NotNull
    public List<BuildTypesHierarchyBean> getBuildTypes()
    {
        List<BuildTypesHierarchyBean> resultList;
        if (!this.fillBuildTypes)
        {
            resultList = Collections.emptyList();
            return resultList;
        }
        else
        {

            Map<SProject, List<SBuildType>> map = new LinkedHashMap<>();
            Iterator<SProject> var2 = this.projectManager.getProjects().iterator();

            for (SProject project : this.projectManager.getProjects())
            {
                List<SProject> projectPath = project.getProjectPath();
                for (final SProject parent : projectPath)
                {
                    if (!parent.isRootProject() && !map.containsKey(parent))
                    {
                        map.put(parent, new ArrayList<>());
                    }
                }

                for (SBuildType buildType : project.getBuildTypes())
                {
                    map.get(project).add(buildType);
                }
            }

            return BuildTypesHierarchyBean.getBuildTypesFor(
              map, true
            );
        }
    }

    public SBuildType getSelectedBuildType()
    {
        return this.selectedBuildType;
    }
}
