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

        //user
        "/user/$id"(controller: "user", action: "view")

        //project
        "/bundle/$id"(controller: "project", action: "view")

        //Activity
        "/activity/getActivities"(controller: "activity", action: "getActivities")
        "/activity/saveComment"(controller: "activity", action: "saveComment")
        "/activity/getComments/$id"(controller: "activity", action: "getComments")

        //Errors
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
