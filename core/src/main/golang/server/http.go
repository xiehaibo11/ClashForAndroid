package server

import (
	"github.com/metacubex/mihomo/listener/http"
	"github.com/metacubex/mihomo/log"
	"github.com/metacubex/mihomo/tunnel"
)

var httpListener *http.Listener

func startRandomHttpPort() {
	listener, err := http.New("127.0.0.1:0", tunnel.Tunnel)
	if err != nil {
		log.Warnln("Failed to start random HTTP proxy: %v", err)
		return
	}

	httpListener = listener
}
