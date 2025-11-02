module github.com/kr328/cfa

go 1.19

require (
	github.com/Dreamacro/clash v0.0.0 // local
	golang.org/x/sys v0.5.0
)

require github.com/google/go-cmp v0.5.9 // indirect

replace github.com/Dreamacro/clash v0.0.0 => ./clash
