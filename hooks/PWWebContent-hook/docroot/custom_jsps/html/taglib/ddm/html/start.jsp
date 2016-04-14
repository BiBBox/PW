<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/taglib/ddm/html/init.jsp" %>


<c:choose>
	<c:when test="<%= permissionChecker.isOmniadmin() %>">
	</c:when>
	<c:otherwise>
		<liferay-util:html-bottom>
			<script src="/html/js/hide_fields.js" />
		</liferay-util:html-bottom>
	</c:otherwise>
</c:choose>


<div class="lfr-ddm-container" id="<%= randomNamespace %>">
	<c:if test="<%= Validator.isNotNull(xsd) %>">

		<%
		pageContext.setAttribute("checkRequired", checkRequired);
		%>


		<% String htmlResponse = DDMXSDUtil.getHTML(pageContext, xsd, fields, portletResponse.getNamespace(), fieldsNamespace, mode, readOnly, requestedLocale);  %>
	
		
		<%
  			  ServletContext hookContext = ServletContextPool.get("PWWebContent-hook");
   			 if (hookContext != null) {
		%>
        	  <liferay-util:include page="/custom_jsps/html/taglib/ddm/html/html_filter_loader.jsp" servletContext="<%=hookContext%>" >
        		  	 <liferay-util:param name="htmlResponse" value="<%= htmlResponse %>" />
        	  </liferay-util:include>
		<%
   		 							   }
		%>

		<aui:input id="<%= fieldsDisplayInputId %>" name="<%= fieldsDisplayInputName %>" type="hidden" />


		<aui:script use="liferay-ddm-repeatable-fields">
			new Liferay.DDM.RepeatableFields(
				{
					classNameId: <%= classNameId %>,
					classPK: <%= classPK %>,
					container: '#<%= randomNamespace %>',
					doAsGroupId: <%= scopeGroupId %>,
					fieldsDisplayInput: '#<portlet:namespace /><%= fieldsDisplayInputId %>',
					namespace: '<%= fieldsNamespace %>',
					p_l_id: <%= themeDisplay.getPlid() %>,
					portletNamespace: '<portlet:namespace />',
					repeatable: <%= repeatable %>
				}
			);
		</aui:script>

	</c:if>