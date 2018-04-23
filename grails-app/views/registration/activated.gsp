<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<section class="engine"></section><section class="cid-qIklYeJO9W mbr-fullscreen jarallax-scroll" id="header15-w">

    <div class="mbr-overlay" style="opacity: 0.2; background-color: rgb(15, 118, 153);"></div>

    <div class="container align-center">
        <div class="row">
            <div class="col-lg-12">
                <div class="form-container">
                    <div class="media-container-column">
                        <p style="padding: 100px; text-align: left;">
                        <g:if test="${successful}">
                        You have successfully activated your account. Please go to the login page <a href="${link}">here</a>
                        or click CIELO in navbar then click on the login link.
                        </g:if>
                        <g:else>
                        Houston, we have a problem! We were unable to activate your account with the given token. If you feel
                        like you have received this message in error, please
                        <a href="mailto:cd2h.cielo@wustl.edu?subject=Unable to activate user&body=There was an error when attempting to activate the user with token: ${token}">
                        contact us</a> so we can investigate.
                        </g:else>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/footerlncludes"/>


