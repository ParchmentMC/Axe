<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="admfn" uri="/WEB-INF/functions/admin" %>
<jsp:useBean id="dependencyTriggerBean" type="org.parchmentmc.axe.build.feature.OtherBuildTriggerBean" scope="request"/>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>
<bs:webComponentsSettings/>
<tr>
    <td colspan="2"><em>Triggers the selected build configuration.</em></td>
</tr>
<tr>
    <td style="vertical-align: baseline;">
        <label for="triggers">Build configuration:</label>
    </td>
    <td style="vertical-align: baseline;">
        <input type="hidden" id="triggers" name="prop:triggers" value="${propertiesBean.properties['triggers']}"/>
        <input type="hidden" id="triggersDefaultBranchExcluded"  value=""/>
        <input type="hidden" id="triggersHasBranches"  value=""/>
        <div id="triggersSelectorWrapper" style="width: 330px;"></div>
        <script type="text/javascript">
            {
                const excludedBuildTypes = [];
                ReactUI.renderConnected(document.getElementById('triggersSelectorWrapper'), ReactUI.ProjectBuildTypeSelect, {
                    excludedBuildTypes,
                    <c:if test="${not empty dependencyTriggerBean.selectedBuildType}">
                    selected: {
                        nodeType: 'bt',
                        id: "${dependencyTriggerBean.selectedBuildType.externalId}",
                    },
                    </c:if>
                    onSelect(item) {
                        $j('#triggers').val(item.id);
                    }
                })
            }
        </script>
        <span class="error" id="error_triggers"></span>
        <script type="text/javascript">
            window._triggers = "${propertiesBean.properties['triggers']}";
        </script>
    </td>
</tr>
<tbody id="finishTriggerBranchFilter" style="${empty propertiesBean.properties['triggers'] ? 'display: none;' : ''}">
    <tr class="groupingTitle ">
        <td colspan="2">Parameter configuration</td>
    </tr>
    <tr>
        <td style="vertical-align: top;">
            <label for="parameters" class="rightLabel">Parameter overrides:</label>
        </td>
        <td style="vertical-align: top;">
            <c:set var="note">Configure custom parameter overrides. Each line is split on the first "=" the section before the sign is used as a parameter key, the section after as its value.</c:set>
            <props:multilineProperty name="parameters" linkTitle="Edit parameter overrides" cols="35" rows="3" note="${note}"/>
            <script type="text/javascript">
                BS.MultilineProperties.updateVisible();
            </script>
        </td>
    </tr>
</tbody>
