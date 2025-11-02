package profile

import (
	"github.com/metacubex/mihomo/hub/executor"
	"github.com/metacubex/mihomo/log"

	"github.com/kr328/cfa/tun"
)

// LoadDefault - load default configure
// In mihomo, default configuration is handled by executor
func LoadDefault() {
	log.Infoln("Loading default configuration")
	tun.ResetDnsRedirect()
	
	// mihomo will use its internal default configuration
	// No need to manually create proxies as they are managed by mihomo
}

// LoadFromFile - load file
func LoadFromFile(path string) error {
	log.Infoln("Loading configuration from: %s", path)
	
	cfg, err := executor.ParseWithPath(path)
	if err != nil {
		return err
	}

	executor.ApplyConfig(cfg, true)

	tun.ResetDnsRedirect()

	return nil
}
