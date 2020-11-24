package com.olegych.scastie.api

import play.api.libs.json.{OFormat, _}

object TaskId {
  implicit val formatSbtRunTaskId: OFormat[TaskId] =
    Json.format[TaskId]
}

case class TaskId(snippetId: SnippetId)
