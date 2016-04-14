<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="at.pw.HideFields"%>

<% HideFields hf = new HideFields();  %>

<% String htmlResponse = ParamUtil.getString(request, "htmlResponse"); %>
<%= hf.clean(htmlResponse) %>
