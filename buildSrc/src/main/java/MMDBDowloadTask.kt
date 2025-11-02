import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

open class MMDBDowloadTask : DefaultTask() {
    companion object {
        // 备用下载源列表
        val URLS = listOf(
            "https://github.com/P3TERX/GeoLite.mmdb/raw/download/GeoLite2-Country.tar.gz",
            "http://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.tar.gz"
        )
        const val MAX_RETRIES = 3
    }

    @get:OutputFile
    var output: String = ""

    @TaskAction
    fun exec() {
        val file = File(output).apply {
            parentFile?.mkdirs()
        }

        // 如果文件已存在且不为空，跳过下载
        if (file.exists() && file.length() > 0) {
            println("MMDB file already exists, skipping download")
            return
        }

        var lastException: Throwable? = null
        
        // 尝试每个 URL
        for (urlStr in URLS) {
            println("Trying to download MMDB from: $urlStr")
            
            var attempt = 0
            while (attempt < MAX_RETRIES) {
                attempt++
                var success = false
                
                try {
                    (URL(urlStr).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 30000  // 30 秒连接超时
                        readTimeout = 60000     // 60 秒读取超时

                        connect()
                        
                        if (responseCode / 100 != 2) {
                            println("HTTP error: $responseCode, attempt $attempt/$MAX_RETRIES")
                            throw GradleException("HTTP error: $responseCode")
                        }

                        FileOutputStream(file).use {
                            inputStream.copyTo(it)
                        }

                        disconnect()
                    }
                    
                    println("Successfully downloaded MMDB from: $urlStr")
                    return  // 成功下载，退出
                    
                } catch (e: Throwable) {
                    println("Download attempt $attempt/$MAX_RETRIES failed: ${e.message}")
                    lastException = e
                    
                    if (attempt < MAX_RETRIES) {
                        Thread.sleep(2000L * attempt)  // 递增延迟
                    }
                }
            }
        }
        
        // 所有 URL 和重试都失败了
        println("WARNING: Failed to download MMDB file from all sources")
        println("The app will build without GeoIP database")
        // 创建一个空文件以满足 Gradle 的输出要求
        file.createNewFile()
    }
}