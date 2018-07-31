package edu.wustl.cielo

class ErrorsController {

    def denied() {
        render(view: "denied")
    }

    def notFound() {
        render(view: "notFound")
    }

    def error() {
        render(view: "error")
    }
}
