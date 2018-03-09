package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.*

class ProjectControllerSpec extends Specification implements ControllerUnitTest<ProjectController>, DomainUnitTest<Project> {

    def populateValidParams(params) {
        assert params != null
        assert false, "TODO: Populate valid params"
    }

    def populateInvalidParams(params) {
        assert params != null
        assert false, "TODO: Populate invalid params"
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index().get()

        then:"The model is correct"
            !model.projectList
            model.projectCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.project!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.method = "POST"
            def project= new Project()
            project.validate()
            controller.save(project).get()

        then:"The create view is rendered again with the correct model"
            model.project!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            project= new Project(params)

            controller.save(project).get()

        then:"A redirect is issued to the show action"
            response.status == 201
            Project.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is execu ted with a null domain"
            controller.show(null).get()

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def project= new Project(params).save(flush:true)

            controller.show(project.id).get()

        then:"A model is populated containing the domain instance"
            model.project.id==project.id
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null).get()

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def project = new Project(params).save(flush:true)
            controller.edit(project?.id).get()

        then:"A model is populated containing the domain instance"
            model.project.id==project.id
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.method = "PUT"
            controller.update(null).get()

        then:"A 404 error is returned"
            status == 404

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            def project= new Project(params).save(flush:true)
            params.clear()
            populateInvalidParams(params)
            controller.update(project.id).get()

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.project.id==project.id

        when:"A valid domain instance is passed to the update action"
            response.reset()
            params.clear()
            populateValidParams(params)
            controller.update(project.id).get()

        then:"A redirect is issued to the show action"
            project != null
            response.status == 200
            !book.isDirty()
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.method = "DELETE"
            controller.delete(null).get()

        then:"A 404 is returned"
            status == 404

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def project= new Project(params).save(flush: true)

        then:"It exists"
            Project.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(project.id).get()

        then:"The instance is deleted"
            Project.count() == 0
            response.status == 204
    }
}