<section class="features1 cid-qHBWkob2I0" id="user_projects" style="background-color: #788492;
color: white;">
    <div class="container-fluid" style="text-align: center;padding-bottom: 4em;">
        <div class="row" style="display: inline-block;">
            <span style="z-index: 1;font-family: inherit; font-size: 2em; color: white;padding-bottom: 2em; margin-top:-2em;">
                Projects
            </span>
        </div>
    </div>
    <g:set var="size" value="${projects.size()}"/>
    <g:set var="index" value="${0}"/>
    <g:set var="contribtuteSize" value="${contribtuteTo.size()}"/>
    <g:set var="contributeIndex" value="${0}"/>

    <g:if test="${(size == index) && (contribtuteSize == contributeIndex)}">
    <div class="container-fluid" style="text-align: center;">
        <div class="row" style="margin-bottom: 3em; display: inline-block;">
        <em>None yet</em>
    </g:if>
    <g:else>
    <div class="container-fluid" style="padding-left: 5em;">
        <div class="row" style="margin-bottom: 3em;">
    </g:else>
            <g:while test="${(index + 5) < size}">
                <div class="col-sm-auto">
                <g:each in="${projects.getAt([index..(index+5)])}" var="project">
                    <span>
                        <g:if test="${project?.shared}">
                            <i class="fas fa-lock-open"></i>
                        </g:if>
                        <g:else>
                            <i class="fas fa-lock"></i>
                        </g:else>
                    </span>
                    <span>
                         <g:if test="${isUsersOwnPage || project?.shared}">
                            <a class="btn btn-link project-link" href="${createLink(controller: "project", action: "view", id: project.id)}">${project?.name}</a> (Owner)
                        </g:if>
                        <g:else>
                            ************
                        </g:else>
                    </span>
                    <br>
                </g:each>
                </div>
                <div class="col-sm-auto">
                    &nbsp;
                </div>
                <g:set var="index" value="${index+5}"/>
            </g:while>

            <g:if test="${index < size}">
                <div class="col-sm-auto">
                <g:each in="${projects.getAt([index..(size-1)])}" var="project">
                    <span>
                        <g:if test="${project?.shared}">
                            <i class="fas fa-lock-open"></i>
                        </g:if>
                        <g:else>
                            <i class="fas fa-lock"></i>
                        </g:else>
                        <span>
                        <g:if test="${isUsersOwnPage || project?.shared}">
                            <a class="btn btn-link project-link" href="${createLink(controller: "project", action: "view", id: project.id)}">${project.name}</a> (Owner)
                        </g:if>
                        <g:else>
                            ************
                        </g:else>
                        </span><br>
                    </span>
                </g:each>
                </div>
            </g:if>
            <g:while test="${(contributeIndex + 5) < contribtuteSize}">
                <div class="col-sm-auto">
                  <g:each in="${contribtuteTo.getAt([contributeIndex..(contributeIndex+5)])}" var="project">
                      <span>
                          <g:if test="${project?.shared}">
                              <i class="fas fa-lock-open"></i>
                          </g:if>
                          <g:else>
                              <i class="fas fa-lock"></i>
                          </g:else>
                      </span>
                      <span>
                          <g:if test="${isUsersOwnPage || project?.shared}">
                              <a class="btn btn-link project-link" href="${createLink(controller: "project", action: "view", id: project.id)}">${project.name}</a> (Contributor)
                          </g:if>
                          <g:else>
                              ************
                          </g:else>
                      </span>
                      <br>
                  </g:each>
              </div>
                <div class="col-sm-auto">
                    &nbsp;
                </div>
                <g:set var="contributeIndex" value="${contributeIndex+5}"/>
            </g:while>
            <g:if test="${contributeIndex < contribtuteSize}">
              <div class="col-sm-auto">
                  <g:each in="${contribtuteTo.getAt([contributeIndex..(contribtuteSize-1)])}" var="project">
                      <span>
                          <g:if test="${project?.shared}">
                              <i class="fas fa-lock-open"></i>
                          </g:if>
                          <g:else>
                              <i class="fas fa-lock"></i>
                          </g:else>
                          <span>
                          <g:if test="${isUsersOwnPage || project?.shared}">
                              <a class="btn btn-link project-link" href="${createLink(controller: "project", action: "view", id: project.id)}">${project.name}</a> (Contributor)
                          </g:if>
                          <g:else>
                              ************
                          </g:else>
                          </span>
                          <br>
                      </span>
                  </g:each>
              </div>
          </g:if>
        </div>
    </div>
</section>