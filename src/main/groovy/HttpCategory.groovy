import java.nio.file.Path
import java.util.zip.ZipInputStream

public class HttpCategory {

    def static leftShift(OutputStream stream, URL url, String user = null, String passwd = null) {
        InputStream input = null
        OutputStream output = null
        try {
            input = url.openStream()
            if (user && passwd) {
                def userPwdBase64 = "${user}:${passwd}".getBytes().encodeBase64().toString()
                def basicAuth = "Basic ${userPwdBase64}"
                url.setRequestProperty('Authorization', basicAuth);
            }
            output = new BufferedOutputStream(stream)
            output << input
        } finally {
            input?.close()
            output?.close()
        }
    }

    def static leftShift(Path path, URL url, String user = null, String passwd = null) {
        def fos = new FileOutputStream(path.toFile())
        leftShift(fos, url, user, passwd)
    }

}

public class HttpHelper {

    public static void downloadTo(Path target, URL url, String user = null, String passwd = null) {
        use(HttpCategory) {
            target << url
        }
    }

    public static void downloadTo(Path target, String url, String user = null, String passwd = null) {
        downloadTo(target, new URL(url), user, passwd)
    }

    public static String download(String url) {
        OutputStream baos = new ByteArrayOutputStream()
        BufferedOutputStream out = new BufferedOutputStream(baos)
        out << new URL(url).openStream()
        out.close()
        new String(baos.toByteArray())
    }

    public static void download(String url, File file) {
        String fname = file ?: url.tokenize("/")[-1]
        OutputStream fos = new FileOutputStream(fname)
        BufferedOutputStream out = new BufferedOutputStream(fos)
        out << new URL(url).openStream()
        out.close()
    }

    def static unzip = { String dest ->
        // In metaclass added methods, 'delegate' is the object on which the method is called.
        // Here it's the file to unzip.
        def result = new ZipInputStream(new FileInputStream(delegate))
        def destFile = new File(dest)
        if (!destFile.exists()) {
            destFile.mkdir()
        }
        result.withStream {
            def entry
            while (entry = result.nextEntry) {
                if (!entry.isDirectory()) {
                    new File("${dest}/${entry.name}").parentFile?.mkdirs()
                    def output = new FileOutputStream("${dest}/${entry.name}")
                    output.withStream {
                        int len = 0
                        byte[] buffer = new byte[32 * 1024]
                        while ((len = result.read(buffer)) > 0) {
                            output.write(buffer, 0, len)
                        }
                    }
                } else {
                    new File("${dest}/${entry.name}").mkdir()
                }
            }
        }
    }

    public static void downloadAndUnzip(String url) {
        /*
        File.metaClass.unzip = HttpHelper.unzip
        // Download ZIP from webserver
        //println "update: trying to download ${u}"
        def buf = new byte[512 * 1024]
        // Destination for download
        def dest = File.createTempFile('ventplan', '.tmp')
        dest.deleteOnExit()
        // Download data and write into temporary file
        dest.withOutputStream { ostream ->
            def r
            new URL(u).withInputStream { istream ->
                while ((r = istream.read(buf, 0, buf.length)) > -1) {
                    ostream.write(buf, 0, r)
                }
            }
        }
        //println "update: downloaded into ${dest}"
        // And copy it to update/ folder
        def dest2 = new File('ventplan', 'update.zip')
        dest2.parentFile.mkdirs()
        dest.renameTo(dest2)
        // Unzip it
        dest2.unzip(dest2.parent)
        //println "update: unzipped into ${dest2.parent}"
        // Delete
        dest2.deleteOnExit()
        dest2.delete()
        //println "update: done"
        */
    }

}
