<section class="features1 cid-qHBWkob2I0" id="user_connections" style="background-color: rgba(58, 71, 85, 0.6);
border-left: none;border-right: none;background-image: url(${assetPath(src: "connections.svg")});
background-position: left;">
    <div class="container-fluid" style="text-align: center;">
        <div class="row" style="display: inline-block; margin-top: -2em; margin-bottom: 3em;">
            <span style="z-index: 1;font-family: inherit; font-size: 2em; color: white;padding-bottom: 2em; margin-top:-2em;">
                ${title?:""}
            </span>
        </div>
        <g:if test="${users}">
            <div class="col-md-12" style="display: inline-flex; overflow: scroll;">
                <g:each in="${users}" var="userObject">
                    <g:render template="userAccessIndividual" model="[userObject: userObject, projectId: projectId]"/>
                </g:each>
            </div>
        </g:if>
        <g:else>
            <div class="col-md-12">
                <em class="lead" style="font-weight: 600;color: white;">
                    None Yet
                </em>
            </div>
        </g:else>
    </div>
</section>