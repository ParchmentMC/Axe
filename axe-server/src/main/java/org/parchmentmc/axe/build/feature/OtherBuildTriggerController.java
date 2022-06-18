package org.parchmentmc.axe.build.feature;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OtherBuildTriggerController extends BaseController
{
    public static final String PATH = "admin/features/editTriggerBuildFeature.html";
    private final PluginDescriptor pluginDescriptor;
    private final WebControllerManager webControllerManager;
    private final ProjectManager projectManager;

    public OtherBuildTriggerController(
      final PluginDescriptor pluginDescriptor,
      final WebControllerManager webControllerManager,
      final ProjectManager myProjectManager) {
        this.pluginDescriptor = pluginDescriptor;
        this.webControllerManager = webControllerManager;
        this.projectManager = myProjectManager;
    }

    public void register() {
        setSupportedMethods(WebContentGenerator.METHOD_POST, WebContentGenerator.METHOD_GET);
        webControllerManager.registerController(pluginDescriptor.getPluginResourcesPath(PATH), this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(
      @NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception
    {
        BasePropertiesBean propertiesBean = (BasePropertiesBean)request.getAttribute("propertiesBean");
        String selectedId = propertiesBean != null ? propertiesBean.getProperties().get(OtherBuildTriggerFeature.TRIGGERS_PARAMETER_NAME) : null;

        boolean restSelectorAvailable = WebUtil.isWebComponentSupportAware(request);
        OtherBuildTriggerBean buildTriggerBean = new OtherBuildTriggerBean(
          this.projectManager,
          selectedId != null ? this.projectManager.findBuildTypeByExternalId(selectedId) : null,
          !restSelectorAvailable
          );
        ModelAndView mv = new ModelAndView("editTriggerBuildFeature.jsp");
        mv.getModel().put("dependencyTriggerBean", buildTriggerBean);
        return mv;
    }
}
