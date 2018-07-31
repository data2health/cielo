<%@ page import='org.springframework.security.acls.domain.BasePermission' %>
<div class='container'>
    <div class='row'>
        <g:message code="project.access.request"/> <br>
    </div>
    <div class='row'>
        &nbsp;
    </div>
    <div class='row'>
        <label id="accessRequestError" style="display: none;"><em style="color: red;">*Required</em></label>
        <select id='projectAccess' name='projectAccess' required='required' class='form-control' aria-required='true'>
            <option value=''>Select Access</option>
            <option value='${BasePermission.READ.getMask()}'>Read</option>
            <option value='${BasePermission.WRITE.getMask()}'>Write</option>
            <option value='${BasePermission.ADMINISTRATION.getMask()}'>Administer</option>
        </select>
    </div>
</div>