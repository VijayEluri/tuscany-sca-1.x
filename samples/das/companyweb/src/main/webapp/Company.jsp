<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%-- 
 *  Copyright (c) 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 --%>

<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"

	pageEncoding="ISO-8859-1"

        import="org.apache.tuscany.samples.das.companies.CompanyClient"
        import="commonj.sdo.*"
%>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Company Test</title>
</head>
<body>

<H2>Tuscany DAS Companies WEB Example</H2>


<form>
<input type="submit" id="doFill" name="doFill" value="All Companies">
<input type="submit" id="doFillAll" name="doFillAll" value="All Companies/Departments">
<input type="submit" id="doAddDepartment" name="doAddDepartment" value="Add department to first company">
<input type="submit" id="doChangeDepartmentNames" name="doChangeDepartmentNames" value="Change Company(1) Dept names">
<input type="submit" id="doDeleteDepartments" name="doDeleteDepartments" value="Delete Company(1) Depts">
<hr>

<!-- Do Fill -->
<%if(request.getParameter("doFill") != null){%>

<table border>
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
		</tr>
	</thead>
	<tbody>

		<%
		java.util.Iterator i = (new CompanyClient()).getCompaniesWithDepartments().iterator();
		while (i.hasNext()) {
			DataObject company = (DataObject)i.next();
		%>
			<tr>
				<td><%=company.getInt("ID")%></td>
				<td><%=company.getString("NAME")%></td>
			<tr>
		<%	
		}
		%>
		
	</tbody>
</table>
<%}%>


<!-- Do Add Department -->
<%
if(request.getParameter("doAddDepartment") != null){

	(new CompanyClient()).addDepartmentToFirstCompany();

}
%>

<!-- Do Delete Departments from first company -->
<%
if(request.getParameter("doDeleteDepartments") != null){

	(new CompanyClient()).deleteDepartmentsFromFirstCompany();
}
%>

<!-- Do Change First Company's Department Names -->
<%
if(request.getParameter("doChangeDepartmentNames") != null){

	(new CompanyClient()).changeFirstCompanysDepartmentNames();
}
%>


<!-- Do FillAll -->
<%if(request.getParameter("doFill") == null) {%>

<table border>
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Department_ID</th>
			<th>Department_Name</th>
		</tr>
	</thead>
	<tbody>

		<%
		java.util.Iterator i = (new CompanyClient()).getCompaniesWithDepartments().iterator();
		while (i.hasNext()) {
			DataObject company = (DataObject)i.next();
		%>
			<tr>
				<td><%=company.getInt("ID")%></td>
				<td><%=company.getString("NAME")%></td>
			<tr>

			

			<%
			java.util.Iterator j = company.getList("departments").iterator();
			while (j.hasNext()) {
				DataObject department = (DataObject)j.next();
			%>
				<tr>
					<td></td><td></td><td><%=department.getInt("ID")%></td>
					<td><%=department.getString("NAME")%></td>
				<tr>
			<%	
			}
			%>
		<%	
		}
		%>
		
	</tbody>
</table>
<%}%>



</form>
</body>
</html>
