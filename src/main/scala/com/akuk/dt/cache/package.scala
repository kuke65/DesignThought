package com.akuk.dt

import spray.json.JsValue

package object cache {
  def stringToSpecificType(value: JsValue) = {
    val valueString = Option.apply(value).getOrElse("").toString()
    if(34.toChar.equals(valueString.charAt(0)) &&
      34.toChar.equals(valueString.charAt(valueString.length -1)) &&
      (valueString.length -1 > 0)) {
      valueString.substring(1, valueString.length -1)
    }else {
      valueString
    }
  }

}
