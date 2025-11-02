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
        // 备用下载源列表 - 使用公开可用的镜像
        val URLS = listOf(
            // Cloudflare CDN 镜像 (可靠)
            "https://raw.githubusercontent.com/Loyalsoldier/geoip/release/Country.mmdb",
            // Dreamacro 的 MaxMind 镜像
            "https://github.com/Dreamacro/maxmind-geoip/releases/latest/download/Country.mmdb",
            // PaperMC 托管的 GeoIP 数据库
            "https://download.geoip.0xabc.dev/GeoLite2-Country.mmdb"
        )
        const val MAX_RETRIES = 2
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
        println("Last error: ${lastException?.message}")
        
        // 创建一个空文件以满足 Gradle @OutputFile 要求
        // extractMMDB 任务会通过 onlyIf 检查跳过空文件
        file.createNewFile()
    }
}