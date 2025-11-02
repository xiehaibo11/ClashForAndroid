package com.github.kr328.clash.utils

import android.util.Base64
import java.net.URLDecoder

/**
 * 订阅格式转换工具
 * 支持将各种订阅格式转换为 Clash 配置
 */
object SubscriptionConverter {
    
    /**
     * 检测并转换订阅内容
     * @param content 原始订阅内容
     * @return Clash 格式的 YAML 配置
     */
    fun convertToClash(content: String): String {
        return when {
            // 已经是 Clash YAML 格式
            isClashFormat(content) -> content
            
            // Base64 编码的订阅（SS/SSR/V2Ray/Hysteria等）
            isBase64Subscription(content) -> convertBase64ToClash(content)
            
            // 单个代理链接
            isSingleProxy(content) -> convertSingleProxyToClash(content)
            
            // 默认返回原内容
            else -> content
        }
    }
    
    /**
     * 判断是否为 Clash YAML 格式
     */
    private fun isClashFormat(content: String): Boolean {
        val trimmed = content.trim()
        return trimmed.contains("port:") && 
               (trimmed.contains("proxies:") || trimmed.contains("Proxy:"))
    }
    
    /**
     * 判断是否为 Base64 编码的订阅
     */
    private fun isBase64Subscription(content: String): Boolean {
        val trimmed = content.trim()
        // 检查是否只包含 Base64 字符
        return trimmed.matches(Regex("^[A-Za-z0-9+/=\\s]+$")) && 
               trimmed.length > 50
    }
    
    /**
     * 判断是否为单个代理链接
     */
    private fun isSingleProxy(content: String): Boolean {
        val trimmed = content.trim().lowercase()
        return trimmed.startsWith("ss://") ||
               trimmed.startsWith("ssr://") ||
               trimmed.startsWith("vmess://") ||
               trimmed.startsWith("vless://") ||
               trimmed.startsWith("trojan://") ||
               trimmed.startsWith("hysteria://") ||
               trimmed.startsWith("hysteria2://") ||
               trimmed.startsWith("hy2://")
    }
    
    /**
     * 转换 Base64 编码的订阅
     */
    private fun convertBase64ToClash(content: String): String {
        try {
            // 解码 Base64
            val decoded = String(Base64.decode(content.trim(), Base64.DEFAULT))
            
            // 分割成多个代理链接
            val proxyLines = decoded.lines().filter { it.isNotBlank() }
            
            // 转换每个代理
            val proxies = mutableListOf<String>()
            val proxyNames = mutableListOf<String>()
            
            proxyLines.forEachIndexed { index, line ->
                val proxyYaml = convertProxyLine(line.trim(), index)
                if (proxyYaml != null) {
                    proxies.add(proxyYaml.yaml)
                    proxyNames.add(proxyYaml.name)
                }
            }
            
            // 生成 Clash 配置
            return buildClashConfig(proxies, proxyNames)
        } catch (e: Exception) {
            throw IllegalArgumentException("Base64 订阅解析失败: ${e.message}")
        }
    }
    
    /**
     * 转换单个代理链接
     */
    private fun convertSingleProxyToClash(content: String): String {
        val proxy = convertProxyLine(content.trim(), 0)
            ?: throw IllegalArgumentException("不支持的代理格式")
        
        return buildClashConfig(listOf(proxy.yaml), listOf(proxy.name))
    }
    
    /**
     * 转换单行代理配置
     */
    private fun convertProxyLine(line: String, index: Int): ProxyNode? {
        return when {
            line.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(line, index)
            line.startsWith("hysteria2://", ignoreCase = true) -> parseHysteria2(line, index)
            line.startsWith("hy2://", ignoreCase = true) -> parseHysteria2(line, index)
            line.startsWith("vmess://", ignoreCase = true) -> parseVmess(line, index)
            line.startsWith("trojan://", ignoreCase = true) -> parseTrojan(line, index)
            else -> null
        }
    }
    
    /**
     * 解析 Shadowsocks 链接
     */
    private fun parseShadowsocks(url: String, index: Int): ProxyNode? {
        try {
            // ss://base64编码的内容#备注 或 ss://method:password@server:port#备注
            val parts = url.substring(5).split("#")
            val remark = if (parts.size > 1) URLDecoder.decode(parts[1], "UTF-8") else "SS-${index + 1}"
            
            val decoded = try {
                String(Base64.decode(parts[0], Base64.URL_SAFE or Base64.NO_WRAP))
            } catch (e: Exception) {
                parts[0]
            }
            
            // method:password@server:port
            val regex = Regex("(.+):(.+)@(.+):(\\d+)")
            val match = regex.find(decoded) ?: return null
            
            val (method, password, server, port) = match.destructured
            
            val yaml = """  - name: "$remark"
    type: ss
    server: $server
    port: $port
    cipher: $method
    password: "$password"
    udp: true"""
            
            return ProxyNode(remark, yaml)
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * 解析 Hysteria2 链接
     */
    private fun parseHysteria2(url: String, index: Int): ProxyNode? {
        try {
            // hysteria2://password@server:port?key=value#remark
            val urlPrefix = if (url.startsWith("hy2://")) "hy2://" else "hysteria2://"
            val content = url.substring(urlPrefix.length)
            
            val parts = content.split("#")
            val remark = if (parts.size > 1) URLDecoder.decode(parts[1], "UTF-8") else "Hysteria2-${index + 1}"
            
            val mainPart = parts[0]
            val (authAndServer, params) = if (mainPart.contains("?")) {
                val split = mainPart.split("?", limit = 2)
                split[0] to split.getOrNull(1)
            } else {
                mainPart to null
            }
            
            // password@server:port 或 server:port/password
            val serverRegex = Regex("(.+)@(.+):(\\d+)")
            val match = serverRegex.find(authAndServer)
            
            val (password, server, port) = if (match != null) {
                match.destructured
            } else {
                return null
            }
            
            // 解析参数
            val paramMap = mutableMapOf<String, String>()
            params?.split("&")?.forEach {
                val kv = it.split("=", limit = 2)
                if (kv.size == 2) {
                    paramMap[kv[0]] = URLDecoder.decode(kv[1], "UTF-8")
                }
            }
            
            val sni = paramMap["sni"] ?: paramMap["peer"] ?: server
            val insecure = paramMap["insecure"] == "1"
            
            val yaml = """  - name: "$remark"
    type: hysteria2
    server: $server
    port: $port
    password: $password
    skip-cert-verify: $insecure
    sni: $sni"""
            
            return ProxyNode(remark, yaml)
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * 解析 VMess 链接
     */
    private fun parseVmess(url: String, index: Int): ProxyNode? {
        try {
            val content = url.substring(8)
            val decoded = String(Base64.decode(content, Base64.URL_SAFE or Base64.NO_WRAP))
            // VMess 通常是 JSON 格式，这里简化处理
            val remark = "VMess-${index + 1}"
            
            // 这里需要更复杂的 JSON 解析，暂时返回 null
            return null
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * 解析 Trojan 链接
     */
    private fun parseTrojan(url: String, index: Int): ProxyNode? {
        try {
            // trojan://password@server:port?params#remark
            val content = url.substring(9)
            val parts = content.split("#")
            val remark = if (parts.size > 1) URLDecoder.decode(parts[1], "UTF-8") else "Trojan-${index + 1}"
            
            val regex = Regex("(.+)@(.+):(\\d+)")
            val match = regex.find(parts[0]) ?: return null
            
            val (password, server, port) = match.destructured
            
            val yaml = """  - name: "$remark"
    type: trojan
    server: $server
    port: $port
    password: "$password"
    skip-cert-verify: false"""
            
            return ProxyNode(remark, yaml)
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * 构建完整的 Clash 配置
     */
    private fun buildClashConfig(proxies: List<String>, proxyNames: List<String>): String {
        val proxyList = proxyNames.joinToString(", ") { "\"$it\"" }
        
        return """# Clash 配置文件 (自动转换)
port: 7890
socks-port: 7891
allow-lan: false
mode: Rule
log-level: info
external-controller: 127.0.0.1:9090

proxies:
${proxies.joinToString("\n")}

proxy-groups:
  - name: "PROXY"
    type: select
    proxies:
      - "自动选择"
${proxyNames.joinToString("\n") { "      - \"$it\"" }}

  - name: "自动选择"
    type: url-test
    proxies:
${proxyNames.joinToString("\n") { "      - \"$it\"" }}
    url: 'http://www.gstatic.com/generate_204'
    interval: 300

rules:
  - DOMAIN-SUFFIX,google.com,PROXY
  - DOMAIN-KEYWORD,google,PROXY
  - DOMAIN-SUFFIX,youtube.com,PROXY
  - DOMAIN-SUFFIX,facebook.com,PROXY
  - DOMAIN-SUFFIX,twitter.com,PROXY
  - GEOIP,CN,DIRECT
  - MATCH,PROXY
"""
    }
    
    data class ProxyNode(val name: String, val yaml: String)
}

