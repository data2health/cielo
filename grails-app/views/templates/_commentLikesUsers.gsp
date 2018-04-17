<g:each in="${users}" var="user">
    <div style="padding-bottom: 1em;">
    <g:getUserProfilePic user="${user}" sticker="${false}" showLink="${true}">
            <span>${user.profile.firstName} ${user.profile.lastName}</span> (<em>${user.username}</em>)<br>
    </g:getUserProfilePic>
    </div>
</g:each>