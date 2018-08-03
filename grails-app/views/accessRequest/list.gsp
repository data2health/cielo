<%@ page import="edu.wustl.cielo.Project" %>

<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<section class="mbr-fullscreen d-block d-lg-none" style="min-height: 0; max-height: 2vh;">&nbsp;</section>

<section class="mbr-fullscreen" style="min-height: 100vh;">
    <g:if test="${messages.size() > 0}">
    <div class="messages-background-div">
        <div class="container-fluid" style="">
            <div class="row justify-content-md-center projectFilterDiv">
                <div class="col-md-12 lead">
                    <g:message code="project.access.control.header.message"/>
                </div>
            </div>
            <div class="row justify-content-md-center">
                <div class="col-md-12">
                    <g:render template="requestsTable" model="[accessRequests: messages, pagesCount: pages, offset: pageOffset]"/>
                </div>
            </div>
        </g:if>
        <g:else>
        <div style="min-width: 100vw;">
            <div class="container-fluid" style="">
                <div class="jumbotron" style="text-align: center;background-color: #6c757d; color: white;">
                    <span class="lead">No messages to show</span>
                </div>
        </g:else>
        </div>
    </div>
</section>

<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>

<script type="application/javascript">
    $(function () {
        $('[data-toggle="popover"]').popover();

        $('#activityMenuUnderline, #teamsMenuUnderline, #projectMenuUnderline').hide().removeClass('d-lg-block').removeClass('d-none');
        $('#messagesMenuUnderline').addClass('d-lg-block').addClass('d-none').show();

        if ($('#messagesBadge').is(':visible')) {
            //change the style slightly so that the underline is not under badge
            $('#messagesMenuUnderline').css('margin-right', '1.25em');
        } else {
            $('#messagesMenuUnderline').css('margin-right', '');
        }
    });

    function onMessagesPageSelection() {
        var offsetVal  = parseInt($('#paging-options').val()) - 1;

        $.get("${createLink(controller: "accessRequest", action:"getTableRows")}", {offset: offsetVal}, function (data) {
            updateMessagesTableRow(data);
            updateToolbarButtons();
        });
    }

    function initiateApproval(requestId) {
        if ($('#approve_' + requestId).attr("class").indexOf("-disabled") === -1) {
            disableButtons(requestId);
            approveRequest(requestId);
        }
    }

    function initiateDenial(requestId) {
        if ($('#deny_' + requestId).attr("class").indexOf("-disabled") === -1) {
            disableButtons(requestId);
            denyRequest(requestId);
        }
    }

    function disableButtons(requestId) {
        $('#deny_' + requestId).removeClass("accessControlButton-red");
        $('#approve_' + requestId).removeClass("accessControlButton");

        $('#deny_' + requestId).addClass("accessControlButton-disabled");
        $('#approve_' + requestId).addClass("accessControlButton-disabled");
    }

    function enableButtons(requestId) {
        $('#deny_' + requestId).removeClass("accessControlButton-disabled");
        $('#approve_' + requestId).removeClass("accessControlButton-disabled");

        $('#deny_' + requestId).addClass("accessControlButton-red");
        $('#approve_' + requestId).addClass("accessControlButton");
    }

    function denyRequest(requestId) {
        $.post("${createLink(controller: "project", action: "denyAccessToProject")}", {id: requestId}, function(data) {
            // enableButtons(requestId);
            refreshRequestsTable();
        });
    }

    function approveRequest(requestId) {
        $.post("${createLink(controller: "project", action: "grantAccessToProject")}", {id: requestId}, function(data) {
            // enableButtons(requestId);
            refreshRequestsTable();
        });
    }

    function refreshRequestsTable () {
        var pageOffset = parseInt($('#paging-options').val()) - 1;
        //get the rows from the server again
        $.get("${createLink(controller: "accessRequest", action: "getTableRows")}", {offset: pageOffset}, function(data) {
            replaceTableRows(data);
            updateBadge();
        });
    }

    function replaceTableRows(data) {
        //remove all rows
        $('#requestsTableBody tr').remove();
        $('#requestsTableBody').html(data.html);

        //now check to see if we need to change pages
        if ($('#requestsTableBody tr').length === 0) {
            if (parseInt($('#paging-options').val()) === 1) {
                location.reload();
            } else {
                var previousPage = parseInt($('#paging-options').val()) - 1;
                $('#paging-options').val(previousPage);
                $('#paging-options').change();
            }
        }
    }

    function updateBadge() {
        var badge =  $('#messagesBadge');

        if (badge.is(':visible')) {
            var currentCount    = parseInt(badge.text());
            var newCount        = currentCount - 1;

            if (newCount === 0) {
                $('#messagesBadge').hide();
            }

            badge.text(newCount);
        }
    }

    function updateMessagesTableRow(data) {
        var currentPageCount = $('#paging-options').find('option').length;

        //fix the number of pages
        if (currentPageCount !== data.pagesCount) {
            //rebuild the paging options
            $('#paging-options option').remove();

            for (var index = 1; index <= data.pagesCount; index++) {
                $('#paging-options').append('<option value="' + index +'">' + index + '</option>');
            }

            $('#ofPages').text('of ' + data.pagesCount);
        }

        //remove old rows
        $('#requestsTableBody').find('tr').remove();

        //replace rows
        $('#requestsTableBody').html(data.html);
    }
</script>