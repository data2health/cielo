<div id="screens">
    <g:render template="initialStep"/>
    <g:render template="newProjectStep1"/>
    <g:render template="newProjectStep2" model="[annotations: annotations]"/>
    <g:render template="newProjectStep3" model="[licences: licences]"/>
    <g:render template="newProjectStep4" model="[users: users, teams: teams]"/>
    <g:render template="newProjectStep5"/>
    <g:render template="newProjectStep6"/>
    <g:render template="finalStep"/>
</div>

<script type="application/javascript">

    $(function () {
        $('#annotations').select2({
            minimumInputLength: 3,
            ajax: {
                delay: 250,
                url: '/annotations/list',
                data: function (params) {
                    var query = {
                        search: params.term,
                        page: (params.page - 1) || 0
                    };
                    if (params.term === "") {
                        return false;
                    } else {
                        return query;
                    }
                },
                dataType: 'json',
                processResults: function (data) {
                    // Tranforms the top-level key of the response object from 'items' to 'results'
                    return {
                        results: data.items,
                        pagination: {
                            more: data.pagination.more
                        }
                    };
                },
                cache: true
            }
        });
    });

    function setCharacterCount() {
        var charCount = $('#description').prop('value').length;
        var totalAllowed = 255;

        $('#charCount').text(charCount + "/" + totalAllowed);
    }

    function handleNext() {

        if (isFormValid()) {
            var idOfCurrentStep = $($('#screens').find('.screen.current-step')).attr('id');
            var indexOfCurrentStep = window.stepIds.indexOf(idOfCurrentStep);
            var indexOfNextStep = indexOfCurrentStep + 1;
            var idOfNextStep = window.stepIds[indexOfNextStep];

            transitionStep(idOfCurrentStep, idOfNextStep, indexOfNextStep);
        }
    }

    function isFormValid() {
        var proceedToNextScreen = true;

        $('.screen.current-step .form-group input, textarea').each(function () {
            if ($(this).prop('id') == 'dataUploadDescription' || $(this).prop('id') == 'codeUploadDescription') {
                proceedToNextScreen = isBundleFormValid($(this).closest('form'));

            } else {
                if ($(this).attr('required') === "required") {
                    if ($(this).prop('value').length === 0)
                        proceedToNextScreen = false;

                    toggleRequiredLabel($(this).attr('id'), proceedToNextScreen);
                }
            }
        });

        return proceedToNextScreen;
    }

    function handlePrevious() {
        var idOfCurrentStep = $($('#screens').find('.screen.current-step')).attr('id');
        var indexOfCurrentStep = window.stepIds.indexOf(idOfCurrentStep);
        var indexOfNextStep = indexOfCurrentStep - 1;
        var idOfNextStep = window.stepIds[indexOfNextStep];

        transitionStep(idOfCurrentStep, idOfNextStep, indexOfNextStep);
    }

    function hideForwardAndBackControlsIfNeeded() {
        //hide forward/back controls
        $('.wizard-button.previous').css('display', 'none');
        $('.wizard-button.next').css('display', 'none');

    }

    function showCancelAndContinue() {
        $('.cancel-btn').hide();
        $('.cancel-btn').css('position', '');
        $('.cancel-btn').css('right', '');
        $('.continue-btn').show();
        $('.cancel-btn').show();
    }

    function transitionStep(fromStep, toStep, toStepIndex) {
        $('.modal-header').html($('#' + window.stepIds[toStepIndex] + '_title').clone());
        $("#" + fromStep).hide();
        $("#" + fromStep).removeClass('current-step');
        $("#" + toStep).show();
        $("#" + toStep).addClass('current-step');

        if (toStepIndex === 0) {
            hideForwardAndBackControlsIfNeeded();
            showCancelAndContinue();
        } else if (toStepIndex === (window.stepIds.length - 1)) {
            $('.cancel-btn').css('position', '');
            $('.cancel-btn').css('display', '');
            $('.wizard-button.next').hide();
            $('.finish-btn').show();
        } else {
            $('.wizard-button.next').show();
            $('.finish-btn').hide();
        }
    }

    function handleSave() {
        var formData = new FormData();

        //get all the data from all the forms from the wizard for the types in the find clause
        $('.new-project-wizard .screen .form-group').each(function () {
            $(this).find('textarea, input, select').each(function () {
                if ($(this).attr('id') !== undefined && $(this).attr('disabled') === undefined) {
                    if ($(this).prop('value') !== null && $(this).prop('value').length > 0) {

                        var controlId = $(this).attr('id');

                        if (formData.has(controlId)) {
                            formData.set(controlId, $(this).prop('value'));
                        } else {
                            formData.append(controlId, $(this).prop('value'));
                        }
                    }
                }
            });
        });

        //for multiselect
        $('.multiple-select').each(function () {
            var controlId = $(this).attr('id');
            var selections = $(this).select2('data');
            var selectedIds = [];

            for (index in selections) {
                if (selectedIds.indexOf(selections[index].id) === -1) {
                    selectedIds[index] = selections[index].id;
                }
            }
            if (formData.has(controlId)) {
                formData.set(controlId, selectedIds);
            } else {
                formData.append(controlId, selectedIds);
            }
        });


        //For file inputs: data and code are optional
        $('input:file').each(function () {
            var elementId = $(this).attr('id');
            var file = document.getElementById(elementId).files[0];
            if (typeof file !== 'undefined' && elementId !== undefined &&
                $(this).attr('disabled') === undefined && typeof file !== "string") {
                if (formData.has(elementId)) {
                    formData.set(elementId, file);
                } else {
                    formData.append(elementId, file);
                }
            }
        });

        $.ajax({
            type: "POST",
            url: "${createLink(controller: "project", action: "saveProject")}",
            data: formData,
            contentType: false,
            processData: false,
            success: function () {
                if (window.location.href.indexOf('/project/viewProjects') !== -1) {
                    var offsetVal = parseInt($('#paging-options').val()) - 1;
                    var filterText = $('#projectSearch').val();
                    var filterOnProjects = $('input[name=projectType]:checked').val();
                    var myProjects;

                    if (filterOnProjects === 'all') {
                        myProjects = false;
                    } else {
                        myProjects = true;
                    }

                    $.get("${createLink(controller: "project", action: "getFilteredProjects")}",
                        {offset: offsetVal, myProjects: myProjects, filterTerm: filterText}, function (data) {
                            replaceProjectTableContent(data);
                        });
                }
            }
        });
    }
</script>