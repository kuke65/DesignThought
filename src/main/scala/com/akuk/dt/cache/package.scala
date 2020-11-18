package com.akuk.dt

import java.util

import spray.json.{JsArray, JsBoolean, JsField, JsNull, JsNumber, JsObject, JsString, JsValue}

package object cache {
  def stringToSpecificType(value: JsValue) = {
    val valueString = Option.apply(value).getOrElse("").toString()
    if (34.toChar.equals(valueString.charAt(0)) &&
      34.toChar.equals(valueString.charAt(valueString.length - 1)) &&
      (valueString.length - 1 > 0)) {
      valueString.substring(1, valueString.length - 1)
    } else {
      valueString
    }
  }

  /**
    *
    * java.util.Map数据类型 转换JSON(key 不能为null)
    *
    * @see  #utilMapToJsObjectForSS
    * @see  #utilMapToJsObjectForSO
    *
    * e.g.
    *
    *       val credentials = new util.HashMap[String, Any]
    *       credentials.put("_id", "1")
    *       credentials.put("secret", "2")
    *       credentials.put("name", 3)
    *       val credentials2 = new util.HashMap[String, Any]
    *       credentials2.put("name", 666)
    *       val credentials3 = new util.ArrayList[String]
    *       val credentials4 = new util.ArrayList[Double]
    *       val credentials5 = new util.ArrayList[util.Map[String, Any]]
    *       credentials3.add("asda")
    *       credentials3.add("ssbg")
    *       credentials4.add(11231231D)
    *       credentials4.add(143422323.123D)
    *       val credentials7 = new util.HashMap[String, Any]
    *       credentials7.put("name","sadas")
    *       val credentials8 = new util.HashMap[String, Any]
    *       credentials8.put("name","ddsaf")
    *       credentials5.add(credentials7)
    *       credentials5.add(credentials8)
    *       credentials.put("credentials2", credentials2)
    *       credentials.put("credentials3", credentials3)
    *       credentials.put("credentials4", credentials4)
    *       credentials.put("credentials5", credentials5)
    *       val value: JsonWriter[util.Map[String, Any]] =
    *       JsonWriter.func2Writer[util.Map[String, Any]](utilMapToJsObjectForSO)
    *       val jsValue: JsValue = value.write(credentials)
    *       val mapString: String = jsValue.prettyPrint
    *       println(mapString)
    *       val fields = JsonParser(ParserInput(mapString)).asJsObject.fields
    *       println(fields("name"))
    *
    * @return
    */
  def utilMapToJsObjectForSS: util.Map[String, String] => JsObject = map => {
    val z = new Array[JsField](map.size())
    val it = map.keySet().iterator()
    var i = 0
    while (it.hasNext) {
      val key = it.next()
      val value = map.get(key)
      if(Option(value).isEmpty) {
        z(i) = (key, JsNull)
      }else {
        z(i) = (key, JsString(value))
      }
      i += 1
    }
    JsObject(z: _*)
  }

  def utilMapToJsObjectForSO: util.Map[String, Any] => JsObject = map => {
    if (Option.apply(map).isDefined && map.size() > 0) {
      utilMapToJsObject(map)
    } else {
      JsObject.empty
    }
  }

  private def utilMapToJsObject(map: util.Map[String, Any]): JsObject = {
    val z = new Array[JsField](map.size())
    val it = map.keySet().iterator()
    var i = 0
    while (it.hasNext) {
      val key = it.next()
      val value = map.get(key)
      if(Option(value).isEmpty) {
        z(i) = (key, JsNull)
      }else {
        value match {
          case a: String => z(i) = (key, JsString(a))
          case a: Boolean => z(i) = (key, JsBoolean(a))
          case a: Int => z(i) = (key, JsNumber(a))
          case a: Short => z(i) = (key, JsNumber(a))
          case a: Char => z(i) = (key, JsNumber(a))
          case a: Float => z(i) = (key, JsNumber(a))
          case a: Double => z(i) = (key, JsNumber(a))
          case a: Long => z(i) = (key, JsNumber(a))
          case a: util.Map[String, Any] => z(i) = (key, utilMapToJsObject(a))
          case a: util.List[Any] => z(i) = (key, utilListToJsArray(a))
          case a: Any => throw new RuntimeException(s"cache.package.utilMapObjectToJsValue unknown type: $a")
        }
      }
      i += 1
    }
    JsObject(z: _*)
  }

  private def utilListToJsArray(list: util.List[Any]): JsValue = {
    val z = new Array[JsValue](list.size())
    var i = 0
    while (i < list.size()) {
      val value = list.get(i)
      if(Option(value).isEmpty) {
        z(i) = JsNull
      }else {
        value match {
          case a: String => z(i) = JsString(a)
          case a: Boolean => z(i) = JsBoolean(a)
          case a: Int => z(i) = JsNumber(a)
          case a: Short => z(i) = JsNumber(a)
          case a: Char => z(i) = JsNumber(a)
          case a: Float => z(i) = JsNumber(a)
          case a: Double => z(i) = JsNumber(a)
          case a: Long => z(i) = JsNumber(a)
          case a: util.Map[String, Any] => z(i) = utilMapToJsObject(a)
          case a: util.List[Any] => z(i) = utilListToJsArray(a)
          case a: Any => throw new RuntimeException(s"cache.package.utilListToJsArray unknown type: $a")
        }
      }
      i += 1
    }
    JsArray(z: _*)
  }
}
