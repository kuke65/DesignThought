# [Scastie](https://scastie.scala-lang.org)

<a href="https://scastie.scala-lang.org/OlegYch/fpDs19tOQYiqILyVmkNC5g">
  <img alt="Demo" src="https://raw.githubusercontent.com/scalacenter/scastie/master/demo/demo.png" style="width: 400px;">
</a>

## What is Scastie?

Scastie is sbt in your browser. You can:

* Use any version of Scala
* Use alternate backends such as Dotty, Scala.js, Scala Native, and Typelevel Scala.
* Use any publicly available library
* Share Scala programs with anybody
* Embed Scastie into your own website or library user guide.

## How does it work?

When a user evaluates their code, the browser sends all its input to our server.
We run your code in an isolated Java Virtual Machine on our servers.
We integrated a Scaladex interface to allow you to search the Scala ecosystem
and include any published Scala library in your project. You don’t need to
remember what the latest version number of a particular library is anymore!

The output is streamed back to the user. A protocol will allow
the client to interpret different events such as compiler errors,
runtime exceptions, instrumentation, console output, etc.

We also support a worksheet mode, which feels much like a worksheet in an IDE.
It lets a user write code as top-level expressions, without having to put
your code inside of a class or object with a main method. Worksheet mode gives you two ways to interleave your results; on the one hand, when an expression
is evaluated, you can see the value and type of the evaluated expression
to the right of the code that you wrote. On the other hand, worksheet mode
also makes it possible to do a kind of literate programming; that is, you
may interleave code and HTML blocks much like in notebook environments
such as iPython notebooks.
