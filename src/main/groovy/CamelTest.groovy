#!/usr/bin/env groovy

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.language.groovy.GroovyRouteBuilder

File dir = new File("/Users/rbe/tmp/cameltest")

@Grab(group='org.apache.camel', module='camel-groovy', version='2.2.0')
@Grab(group='org.apache.camel', module='camel-mail', version='2.2.0')
@Grab(group='org.apache.camel', module='camel-core', version='2.2.0')
class AnalyticsRoute extends GroovyRouteBuilder {
	void configure() {
		println "configure"
		from("imaps://imap.gmail.com?username=ralfbensmann@googlemail.com&password=Sowujax44&folderName=INBOX&consumer.delay=30")
		.filter {
			println "filter: $it"
			new File(dir, it.in.subject) << it.in.headers.toString()
			it.in.headers.subject.startsWith('Analytics')
		}
		.process(new ExtractAttachment())
	}
}

class ExtractAttachment implements org.apache.camel.Processor {
	void process(org.apache.camel.Exchange exchange) throws Exception {
		println "process($exchange)"
		def attachments = exchange.in.attachments
		attachments.each { attachment ->
			def datahandler = attachment.value
			def xml = exchange.context.typeConverter.convertTo(String.class, datahandler.inputStream)
			new File(dir, datahandler.name) << xml
		}
	}
}

dir.mkdirs()
def camelCtx = new DefaultCamelContext()
camelCtx.addRoutes(new AnalyticsRoute())
camelCtx.start()
System.in.read()
