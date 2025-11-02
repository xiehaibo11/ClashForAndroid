module github.com/kr328/cfa

go 1.20

require (
	github.com/metacubex/mihomo v0.0.0 // local
	golang.org/x/sys v0.5.0
)

require github.com/google/go-cmp v0.5.9 // indirect

replace github.com/metacubex/mihomo v0.0.0 => ./clash
