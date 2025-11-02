package tun

import (
	"fmt"

	"github.com/metacubex/mihomo/log"
)

type handler struct {
	// Placeholder for future TUN adapter if needed
	// In mihomo, TUN is managed through listener package
	fd  int
	mtu int
}

var (
	instance *handler
)

// StartTunProxy - start
// Note: In mihomo, TUN functionality is now managed through the listener package
// This is a compatibility layer for the Android app
func StartTunProxy(fd, mtu int) error {
	StopTunProxy()

	instance = &handler{
		fd:  fd,
		mtu: mtu,
	}

	log.Infoln("Android tun proxy started (compatibility mode)")
	fmt.Println("Android tun started")

	return nil
}

// StopTunProxy - stop
func StopTunProxy() {
	if instance != nil {
		instance = nil
		log.Infoln("Android tun proxy stopped")
	}
}

// ResetDnsRedirect - reset DNS redirect
// Note: In mihomo, DNS is managed through the DNS resolver system
// This is a compatibility function
func ResetDnsRedirect() {
	if instance == nil {
		return
	}
	log.Debugln("ResetDnsRedirect called (compatibility mode)")
}
