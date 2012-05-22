/**
 * /Users/rbe/project/playground/src/Camel3.groovy
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
import groovy.lang.Binding
import groovy.lang.GroovyShell
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.language.groovy.GroovyRouteBuilder
/*
@Grab(group = "org.apache.camel", module = "camel-groovy", version = "2.2.0")
@Grab(group = "org.apache.camel", module = "camel-mail",   version = "2.2.0")
@Grab(group = "org.apache.camel", module = "camel-core",   version = "2.2.0")
*/
@Singleton
class MyBean {
	def bla(File file) { println "${this}: ${file.dump()}" }
}

class GroovyRoute extends GroovyRouteBuilder {
	
	def fromUri
	def script
	def toUri
	
	def binding = new Binding()
	def gs = new GroovyShell(binding)
	
	protected void configure() {
		from(fromUri)
		.filter {
			it.in.headers.CamelFileName.endsWith("a.pdf")
		}
		.process({ camelExchange ->
				if (script) {
					binding.setVariable("camelExchange", camelExchange)
					def closure = gs.evaluate("{ -> ${script} }")
					closure()
				}
			} as org.apache.camel.Processor)
		.to(toUri)
	}
	
}

def camelCtx = new DefaultCamelContext()
camelCtx.registry = new org.apache.camel.impl.SimpleRegistry()
camelCtx.registry.registry.put("myBean", MyBean.instance)
camelCtx.addRoutes(
	new GroovyRoute(
		fromUri: "file:///Users/rbe/tmp/pdf/in?noop=true",
		toUri: "bean:myBean?method=bla"
	)
)
camelCtx.start()
try { Thread.sleep(1000 * 60 * 5) } catch (Exception e) {}
println "ended"
camelCtx.stop()
