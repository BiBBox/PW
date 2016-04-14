<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portlet.dynamicdatamapping.storage.Fields" %>


<%@ page import="at.pw.HideFields"%>

<% HideFields hf = new HideFields();  %>

<% String htmlResponse = ParamUtil.getString(request, "htmlResponse"); %>
<% String structureIDs = ParamUtil.getString(request, "structureIDs"); %>
<%= hf.clean(htmlResponse, structureIDs) %>
