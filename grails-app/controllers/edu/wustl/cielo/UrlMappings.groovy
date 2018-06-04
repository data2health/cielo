package edu.wustl.cielo

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        //Landing Page
        "/"(controller: 'home', action: 'index')

        //login
        "/login/auth"(view: "/login/auth")

        //Registration
        "/register"(controller: "registration", action: "register")
        "/saveNewUser"(controller: "registration", action: "saveNewUser")
        "/registered"(controller: "registration", action: "registered")
        "/registration/activate"(controller: "registration", action: "activateUser")

        //Communications
        "/contactUs"(controller: "communications", action: "contactUs")

        //Home
        "/home"(controller: "home", action: "home")
        "/refreshLeftSidebar"(controller: "home", action: "sidebarLeft")

        //user
        "/user/$id"(controller: "user", action: "view")
        "/user/update/"(controller: "user", action: "updateUser")
        "/user/follow/"(controller: "user", action: "followUser")
        "/user/unFollow/"(controller: "user", action: "unFollowUser")
        "/user/following/"(controller: "user", action: "getUsersIFollow")
        "/user/updateConnections"(controller: "user", action: "updateUsersIFollow")

        //project
        "/project/$id"(controller: "project", action: "view")
        "/project/saveComment"(controller: "project", action: "saveProjectComment")
        "/project/getProjectComments/$id"(controller: "project", action: "getProjectComments")
        "/project/saveReply"(controller: "project", action: "saveCommentReply")
        "/project/saveChanges"(controller: "project", action: "saveProjectBasicChanges")
        "/project/likePost"(controller: "project", action: "likeComment")
        "/project/removeLike"(controller: "project", action: "removeCommentLike")
        "/project/getUsers"(controller: "project", action: "getCommentLikeUsers")
        "/project/myList/"(controller: "project", action: "myProjects")
        "/project/delete/"(controller: "project", action: "deleteProject")
        "/project/public/list"(controller: "project", action: "publicProjectsList")
        "/project/addTeam"(controller: "project", action: "addTeamToProject")
        "/project/getTeams"(controller: "project", action: "getTeams")
        "/project/newProject"(controller: "project", action: "newProject")
        "/project/saveProject"(controller: "project", action: "saveProject")
        "/project/newUploadScreen"(controller: "project", action: "renderNewUploadScreen")
        "/project/addBundle"(controller: "project", action: "addBundleToProject")
        "/project/removeTeam"(controller: "project", action: "removeTeam")
        "/project/removeBundle"(controller: "project", action: "removeBundleFromProject")
        "/project/table/list"(controller: "project", action: "projectsTableRows")
        "/project/bundles/list"(controller: "project", action: "getBundles")
        "/project/filtered/list"(controller: "project", action: "getFilteredProjects")

        //license
        "/license/$id"(controller: "license", action: "getLicenseBody")
        "/license/termsOfUse"(controller: "license", action: "getTermsOfUse")

        //Activity
        "/activity/getActivities"(controller: "activity", action: "getActivities")
        "/activity/activity"(controller: "activity", action: "getActivity")
        "/activity/saveComment"(controller: "activity", action: "saveComment")
        "/activity/getComments/$id"(controller: "activity", action: "getComments")
        "/activity/likePost"(controller: "activity", action: "likeActivity")
        "/activity/removelike"(controller: "activity", action: "removeActivityLike")
        "/activity/getUsers/"(controller: "activity", action: "getCommentLikeUsers")
        "/activity/post"(controller: "activity", action: "saveNewActivity")

        //Team
        "/team/members"(controller:"team", action: "getTeamMembers")
        "/team/membersStrip"(controller:"team", action: "teamMembersSnippet")
        "/team/deleteTeam"(controller: "team", action: "deleteTeam")
        "/team/newTeam"(controller: "team", action: "newTeamForm")
        "/team/$id"(controller: "team", action: "view")
        "/team/updateMembers"(controller: "team", action: "updateTeamUsers")

        //code
        "/code/$id"(controller: "code", action: "view")

        //data
        "/data/$id"(controller: "data", action: "view")

        //Errors
        "500"(view:'/error')
        "404"(view:'/notFound')

        //Google domain verification
        "/google7a73685f036100a5.html"(view: "/verification")
    }
}
