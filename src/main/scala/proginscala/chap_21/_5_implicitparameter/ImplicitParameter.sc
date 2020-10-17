class PreferredPrompt(val preference: String)

object Greeter {
  def greet(name: String)(implicit prompt: PreferredPrompt) = {
    println("Welcome, " + name + ". The system is ready.")
    println(prompt.preference)
  }
}

val bobsPrompt = new PreferredPrompt("relax> ")
Greeter.greet("Bob")(bobsPrompt)

object JennyPrefs {
  implicit val prompt = new PreferredPrompt("Yes, master> ")
}

import JennyPrefs.prompt
Greeter.greet("Jenny")
