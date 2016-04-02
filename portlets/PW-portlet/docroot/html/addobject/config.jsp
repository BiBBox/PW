<%@ include file="/html/init.jsp" %> 
<%@ page import="com.liferay.portal.kernel.util.Constants" %> 

<style>

td {
   vertical-align:top;
   padding:0px 0px 0px 10px;
    height:10px;
}

</style>


<%
String articalTemplate_cfg = GetterUtil.getString(portletPreferences.getValue("articalTemplate", ""));
String inventarNumberPattern_cfg = GetterUtil.getString(portletPreferences.getValue("inventarNumberPattern", "#"));
String articalEnabled_cfg = GetterUtil.getString(portletPreferences.getValue("articalEnabled", ""));

long webcontentFolderId_cfg = GetterUtil.getLong(portletPreferences.getValue("webcontentFolderId", "0"));
long documentFolderId_cfg = GetterUtil.getLong(portletPreferences.getValue("documentFolderId", "0"));


HashMap<String, String> artical_template = new HashMap<String, String>();
%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />
<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:fieldset>
		<aui:layout cssClass="proposelayout">
   			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
   			
   			<table>
   			<%
   			int counter = 0;
			List<DDMStructure> ddm_structures = DDMStructureLocalServiceUtil.getStructures(themeDisplay.getScopeGroupId());
			for(DDMStructure ddm_structure : ddm_structures) {
				if(ddm_structure.getClassName().equals("com.liferay.portlet.journal.model.JournalArticle")) {
					boolean checked = false;
					if(articalEnabled_cfg.contains("id_" + ddm_structure.getStructureKey() + "_enabled")) {
						checked = true;
					}
					%>
					<tr> 
						<td width="20px"> <aui:input name='<%= "structureenabledId_" + counter %>'  type="checkbox" label="" checked='<%= checked %>' /> </td>
						<td width="170px"> <b> <%= ddm_structure.getName(themeDisplay.getLocale()) %> </b>   </td>
						<td>	  Display Template: </td>
						<td>
							<aui:select label="" name='<%= "structuretemplateId_" + counter %>'> 
								<%
								List<DDMTemplate> templates = ddm_structure.getTemplates();
								for(DDMTemplate template : templates) {
									%>
									<aui:option selected='<%= articalTemplate_cfg.contains(ddm_structure.getStructureKey() + "_" + template.getTemplateKey()) %>' value='<%= ddm_structure.getStructureKey() + "_" + template.getTemplateKey() %>'><%= template.getName(themeDisplay.getLocale()) %>
								    </aui:option>
									<%
								}
								%>
							</aui:select>
						</td>
					</tr>
					<%
					counter++;
				}
			}
			%>
			</table>
			<aui:input name="numberofstructures" type="hidden" value="<%= counter %>" />
			<aui:column columnWidth="100" first="true">
				<aui:input name="preferences--inventarNumberPattern--" value="<%= inventarNumberPattern_cfg %>" />
			</aui:column>
			
			<aui:column columnWidth="100" first="true">
				<aui:input name="preferences--webcontentFolderId--" value="<%= webcontentFolderId_cfg %>" />
			</aui:column>

			<aui:column columnWidth="100" first="true">
				<aui:input name="preferences--documentFolderId--" value="<%= documentFolderId_cfg %>" />
			</aui:column>

			
   		</aui:layout>
   	</aui:fieldset>
   	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>