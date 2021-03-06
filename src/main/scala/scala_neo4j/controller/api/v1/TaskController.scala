package scala_neo4j.controller.api.v1

import skinny.micro.WebApp
import skinny.micro.contrib.json4s.JSONSupport
import skinny.micro.response._

import scala.util.{Failure, Success}
import scala_neo4j.service.TaskService

case class TaskRequest(title: String, description: String)

object TaskController extends WebApp with JSONSupport {

  get("/api/v1/tasks/:id"){
    params.getAs[Long]("id") match {
      case Some(id) => TaskService.findById(id) match {
        case Some(t) => Ok(toJSONString(t), Map("Content-Type" -> "application/json"))
        case None => NotFound
      }
      case None => NotFound
    }
  }

  get("/api/v1/tasks"){
    Ok(
      toJSONString(TaskService.findAll()),
      Map("Content-Type" -> "application/json")
    )
  }

  post("/api/v1/tasks"){
    fromJSONString[TaskRequest](request.body) match {
      case Success(t) => Created(
        toJSONString(TaskService.create(t.title, t.description)),
        Map("Content-Type" -> "application/json")
      )
      case Failure(e) => BadRequest(e.getMessage)
    }
  }

  delete("/api/v1/tasks/:id"){
    params.getAs[Long]("id") match {
      case Some(id) => {
        TaskService.delete(id)
        NoContent
      }
      case None => BadRequest
    }
  }
}
