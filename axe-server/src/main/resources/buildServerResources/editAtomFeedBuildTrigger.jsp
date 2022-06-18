<%@ include file="/include.jsp" %>
<%@ page import="org.parchmentmc.axe.common.Constants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>

<tr class="noBorder" >
    <td colspan="2">
        <em>The Atom Feed build trigger will add a build to the Queue when an atom feed publishes a new page.</em>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=Constants.ATOM_TRIGGER_URL_PARAM%>">URL: <l:star/></label></th>
    <td>
        <props:textProperty name="<%=Constants.ATOM_TRIGGER_URL_PARAM%>" className="longField"/>
        <span class="smallNote">
          e.g. https://blogtest.minecraftforge.net/index.xml,<br/>
          https://parchmentmc.org/feed.xml<br/>
      </span>
        <span class="error" id="error_<%=Constants.ATOM_TRIGGER_URL_PARAM%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=Constants.ATOM_TRIGGER_LAST_UPDATED_PATH%>">Last updated XPath: <l:star/></label></th>
    <td>
        <props:textProperty name="<%=Constants.ATOM_TRIGGER_LAST_UPDATED_PATH%>" className="longField"/>
        <span class="smallNote">
          e.g. /rss/channel/lastBuildDate,<br/>
          /feed/updated<br/>
      </span>
        <span class="error" id="error_<%=Constants.ATOM_TRIGGER_LAST_UPDATED_PATH%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=Constants.ATOM_TRIGGER_NEW_ENTRY_PATH%>">New entry XPath: <l:star/></label></th>
    <td>
        <props:textProperty name="<%=Constants.ATOM_TRIGGER_NEW_ENTRY_PATH%>" className="longField"/>
        <span class="smallNote">
          e.g. (/rss/channel/item)[1],<br/>
          (/feed/entry)[1]<br/>
      </span>
        <span class="error" id="error_<%=Constants.ATOM_TRIGGER_NEW_ENTRY_PATH%>"></span>
    </td>
</tr>