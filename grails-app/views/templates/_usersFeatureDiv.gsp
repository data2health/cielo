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
                <g:each in="${users}" var="user">
                    <div style="display: inline-block;">
                    <g:getUserProfilePic style="padding-left: 5em;padding-right: 5em;padding-bottom: 2em;padding-top: 2em;" user="${user}"
                                         sticker="${true}"
                                         tooltipText="${user.profile.firstName} ${user.profile.lastName}${'<br><em>'}${user.username}${'</em>'}"
                                         showLink="${true}"
                                         imageSize="x-large"/>&nbsp;
                    </div>

                </g:each>
            </div>
        </g:if>
        <g:else>
            <div class="col-md-12">
                <em style="font-weight: 600;">
                    None Yet
                </em>
            </div>
        </g:else>
    </div>
</section>