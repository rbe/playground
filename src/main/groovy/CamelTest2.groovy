#!/usr/bin/env groovy

import groovy.lang.Binding
import groovy.lang.GroovyShell
//import groovy.queue.GroovyScriptEngine
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.language.groovy.GroovyRouteBuilder

@Grab(group='org.apache.camel', module='camel-groovy', version='2.2.0')
@Grab(group='org.apache.camel', module='camel-mail', version='2.2.0')
@Grab(group='org.apache.camel', module='camel-core', version='2.2.0')
class GroovyRoute extends GroovyRouteBuilder {
	
	def fromUri
	def script
	def toUri
	
	def binding = new Binding()
	def gs = new GroovyShell(binding)
	
	protected void configure() {
		from(fromUri)
		.process({ camelExchange ->
				println "process: camelExchange=${camelExchange}"
				if (script) {
					binding.setVariable("camelExchange", camelExchange)
					def closure = gs.evaluate("{->${script}}")
					closure()
				}
			} as org.apache.camel.Processor)
		.to(toUri)
	}
	
}

def fromUri = "imaps://sslmailpool.ispgateway.de?username=ralf@bensmann.com&password=Sowujax44&folderName=INBOX&delete=false&unseen=false&consumer.delay=" + 10 * 1000
def toUri = "log:mymail?level=INFO&showAll=true" //&multiline=true

def camelCtx = new DefaultCamelContext()
camelCtx.addRoutes(
	new GroovyRoute(fromUri: fromUri, toUri: toUri, script: """ println "1" """)
)
/*
, script: """
println "hello: "
camelExchange.metaClass.properties.each { it -> println "camelExchange.prop: \${it.name}: \${it.getProperty(camelExchange)}" }
camelExchange.metaClass.methods.each { it -> println "camelExchange.meth: \${it}" }
println ""
println ""
"""
*/
camelCtx.start()
try { Thread.sleep(1000 * 60 * 5) } catch (Exception e) {}
println "ended"
camelCtx.stop()
