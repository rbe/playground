package artofcoding

class Template(n: String) {
    
    val bla: String = n

    override fun toString(): String {
        return "${bla}"
    }

}

fun x<T: String>(f: () -> T): Any {
    return "Greetings, " + f()
}

fun main(args: Array<String>) {
    val r = x({ Template("Ralf") as String })
    println("Hello World! $r")
}
